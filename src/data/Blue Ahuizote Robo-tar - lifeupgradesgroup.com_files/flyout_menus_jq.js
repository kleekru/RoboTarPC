
/*
 * Responsible for flyout menus within the editor + on a published page.
 * Also responsible for condensing overflowing nav and making a "more..." link.
 *

 * Author: Adam Shaw
 */

(function($) {
	

	/****************************** publicly available functions ****************************/

	var moreItemHTML;
	var activeLiId;
	var currentPageId;
	var stylePrefix = window.STYLE_PREFIX || 'weebly';
	var SLIDE_DURATION = 300;

	// called from a published page
	window.initPublishedFlyoutMenus = function(topLevelSummary, cpid, moreItemHTML, aLiId, isPreview) {
		currentPageId = cpid;
		if (topLevelSummary.length > 0) {
			var go = function() {
				activeLiId = aLiId;
				var container = $("<div id='" + stylePrefix + "-menus' />").appendTo('body');
				var firstItem = navElm(topLevelSummary[0].id);
				if (firstItem) {
					window.navFlyoutMenu = new FlyoutMenu(firstItem.parent(), {
						relocate: container,
						aLiId: aLiId
					});
					condenseNav(topLevelSummary, moreItemHTML);
				}
			}
			if (isPreview) {
				go(); // css has been written in html <style> tag, no need to check if loaded
			}else{
				whenThemeCSSLoaded(go);
			}
		}
	};

	// called from the editor
	window.initEditorFlyoutMenus = function() {
		whenThemeCSSLoaded(function() {
			function go() {
				var topLevelSummary = Weebly.PageManager.getTopLevelSummary();
				if (topLevelSummary.length > 0) {
					var listItem0 = navElm(topLevelSummary[0].id);
					if (listItem0) {
						var listElement = listItem0.parent();
						if (!listElement.is('table,tbody,thead,tr')) {
							window.navFlyoutMenu = new FlyoutMenu(listElement, {
								relocate: '#' + stylePrefix + '-menus'
							});

							moreItemHTML = renderItem({ title: /*tl(*/'more...'/*)tl*/ }, false, currentNormalItem);
							moreItemHTML =
								"<span class='" + stylePrefix + "-nav-handle " + stylePrefix + "-nav-more'>" +
								moreItemHTML +
								"</span>";
							condenseNav(topLevelSummary, moreItemHTML);
						}else{
							window.navFlyoutMenu = null;
						}
					}else{
						window.navFlyoutMenu = null;
					}
				}else{
					window.navFlyoutMenu = null;
				}
			}
			if ($.browser.webkit) {
				// this solves a webkit bug where the <span>s within the <ul> are displayed as block
				// this problem has nothing to do with the flyout code, but this was the most convenient place to put it ~ashaw
				var handles = $('#icontent span.' + stylePrefix + '-nav-handle');
				handles.hide();
				setTimeout(function() {
					handles.show();
					go();
				},0);
			}else{
				go();
			}
		});
	};

	// called from the editor when nav positioning might have changed
	window.refreshNavCondense = function(callback) {
		if (window.navFlyoutMenu) {
			//console.log('refresh');
			condenseNav(Weebly.PageManager.getTopLevelSummary(), moreItemHTML);
		}
	};

	window.disableFlyouts = false;



	/*********************************** flyout menu class ************************************/

	window.FlyoutMenu = function(mainList, options) {

		mainList = $(mainList); // the element that contains all the nav elements
		options = options || {};

		// settings (an attempt at making FlyoutMenu portable)
		var listTag = options.listTag ? options.listTag.toLowerCase() : 'ul';
		var itemTag = options.itemTag ? options.itemTag.toLowerCase() : 'li';
		var delay = (options.delay || 0.5) * 1000;

		// if specified, all submenus will be detached from original place in DOM and put in here
		var relocate = options.relocate ? $(options.relocate) : false;

		// FYI
		// a 'handle' is an element that contains the templatable HTML for each page's nav link
		// a 'handle' may be a wrapping SPAN element (with className 'PREFIX-nav-handle')
		//   OR it may be the item itself (such as an LI)

		var allItems; // list of all nav items
						// (the first child within a handle OR the handle itself)


		//
		// attach all event handlers and do state-keeping for flyout menus
		//

		function initItem(item) {

			item.css('position', 'relative'); // this gives more accurate offsets
			item.find('a').css('position', 'relative'); // more accurate offset (prevents IE bug)

			// states
			var isSliding = false;
			var isExpanded = false;
			var isMouseoverItem = false;
			var mouseoverCnt = 0;

			var slidVertically = false;
			var slidRight = false;

			var sublistWrapper; // a DIV.PREFIX-menu-wrap OR null
			var sublist;        // a UL.PREFIX-menu OR null


			//
			// expand a sublist on mouseover
			//

			function itemMouseover() {
				if (disableFlyouts) return false;
				mouseoverCnt++;
				isMouseoverItem = true;
				if (!isExpanded && !isSliding) {
					if (sublist) {
						// when a sublist is expanded, immediately contract all siblings' sublists
						getSiblings(item).each(function(i, siblingNode) {
							if (siblingNode._flyoutmenu_contract) {
								siblingNode._flyoutmenu_contract();
							}
						});
						expandSublist();
					}
				}
			}


			//
			// contract sublist on mouseout (after delay)
			//

			function itemMouseout() {
				if (disableFlyouts) return false;
				isMouseoverItem = false;
				if (isExpanded) {
					var mouseoverCnt0 = mouseoverCnt;
					setTimeout(function() {
						if (mouseoverCnt == mouseoverCnt0 && isExpanded && !isSliding) {
							contractSublist();
						}
					}, delay);
				}
			}


			//
			// prevent contracting when sublist is moused over
			//

			function sublistWrapperMouseover() {
				if (disableFlyouts) return false;
				mouseoverCnt++;
			}


			//
			// do item's sublist's expand animation
			//

			function expandSublist() {
				isSliding = true;
				var opts = {
					wrapper: sublistWrapper,
					duration: SLIDE_DURATION,
					complete: function() { // when animation has finished
						isSliding = false;
						isExpanded = true;
						if (!isMouseoverItem) {
							// if mouse was not over when animation finished, immediately contract
							contractSublist();
						}else{
							// attach methods for later hiding/contracting
							item[0]._flyoutmenu_contract = contractSublist; // assign to DOM node
							item[0]._flyoutmenu_hide = function() {         //
								isSliding = false;
								isExpanded = false;
								isMouseoverItem = false;
								item[0]._flyoutmenu_contract = null;
								item[0]._flyoutmenu_hide = null;
								sublistWrapper.hide();
							};
						}
					}
				};
				var massCoords = getItemMassCoords(item);

				// need to show it for IE8 to get the correct offsetParent
				sublistWrapper.css('left', -10000);
				sublistWrapper.show();

				var localOriginElement = sublistWrapper.offsetParent();
				var localOrigin = localOriginElement.is('body') ? {top:0,left:0} : localOriginElement.offset();
					// ^ special case body. jQuery provides inaccurate offset for body. always 0,0

				sublistWrapper.hide();
				sublist.show(); // so calls to sublistWrapper.outerWidth() are correct

				if (inVerticalList(item, true, options.aLiId)) {
					// slide right/left on vertical nav
					slidVertically = false;
					sublistWrapper.css('top', -localOrigin.top + massCoords[0].top);
					var w = sublistWrapper.outerWidth();
					if (massCoords[1].left + w > $('body').outerWidth()) {
						slidRight = false;
						sublistWrapper.css('left', -localOrigin.left + massCoords[0].left - w);
						opts.direction = 'right';
						sublist.show('slide', opts);
					}else{
						slidRight = true;
						sublistWrapper.css('left', -localOrigin.left + massCoords[1].left);
						opts.direction = 'left';
						sublist.show('slide', opts);
					}
				}else{
					// slide down on horizontal nav
					slidVertically = true;
					sublistWrapper.css('top', -localOrigin.top + massCoords[1].top);
					var w = sublistWrapper.outerWidth();
					if (massCoords[0].left + w > $('body').outerWidth()) {
						sublistWrapper.css('left', -localOrigin.left + massCoords[1].left - w);
					}else{
						sublistWrapper.css('left', -localOrigin.left + massCoords[0].left);
					}
					opts.direction = 'up';
					sublist.show('slide', opts);
				}
			}


			//
			// do item's sublist's contract animation
			//

			function contractSublist(mouseoverHack) {
				if (disableFlyouts || !item.parent().length) { // no parentNode?? removed from dom already? wtf!?
					// contractSublist is often called from a delay, might have been disabled in that time
					return;
				}
				if (mouseoverHack) {
					// IE6 wasn't registering the mouseout
					isMouseoverItem = false;
				}
				isSliding = true;
				item[0]._flyoutmenu_contract = null;
				item[0]._flyoutmenu_hide = null;
				var opts = {
					wrapper: sublistWrapper,
					duration: SLIDE_DURATION,
					complete: function() {
						isSliding = false;
						isExpanded = false;
						if (isMouseoverItem) {
							// if mouseleft, but re-entered before animation finished
							// immediately expand sublist again
							expandSublist();
						}
					}
				}
				if (slidVertically) {
					opts.direction = 'up';
					sublist.hide('slide', opts);
				}else{
					if (slidRight) {
						opts.direction = 'left';
						sublist.hide('slide', opts);
					}else{
						opts.direction = 'right';
						sublist.hide('slide', opts);
					}
				}
			}


			//
			// initialize submenu and attach events
			//

			sublist = getSublist(item, listTag);
			if (sublist) {
				sublistWrapper = sublist.parent();
				sublistWrapper.css('position', 'absolute');
				sublistWrapper.hide(); // should already be display:none, but just in case
				if (relocate) {
					// since sublist is no longer a descendant of the item, mouse events
					// wont cascade. simulate this
					sublistWrapper.on('mouseover', itemMouseover);
					sublistWrapper.on('mouseout', itemMouseout);
				}else{
					// keep the submenu alive...
					sublistWrapper.on('mouseover', sublistWrapperMouseover);
				}
			}
			item.on('mouseover', itemMouseover);
			item.on('mouseout', itemMouseout);


			//
			// attach a method for removing registered events
			// (returns the sublist wrapper)
			//

			item[0]._flyoutmenu_destroy = function(removeSublist) { // attach to raw DOM node
				item.off('mouseover', itemMouseover);
				item.off('mouseout', itemMouseout);
				if (removeSublist) {
					if (sublistWrapper) {
						return sublistWrapper.remove(); // detach before returning
					}
				}
				else if (sublistWrapper) {
					if (relocate) {
						sublistWrapper.off('mouseover', itemMouseover);
						sublistWrapper.off('mouseout', itemMouseout);
					}else{
						sublistWrapper.off('mouseover', sublistWrapperMouseover);
					}
					return sublistWrapper;
				}
			};

		}


		//
		// methods for the FlyoutMenu object
		//

		// close all submenus with an animation
		this.contract = function() {
			allItems.each(function(i, itemNode) {
				if (itemNode._flyoutmenu_contract) {
					itemNode._flyoutmenu_contract(true);
				}
			});
		};

		// hide all submenus immediately
		this.hideSubmenus = function() {
			allItems.each(function(i, itemNode) {
				if (itemNode._flyoutmenu_hide) {
					itemNode._flyoutmenu_hide();
				}
			});
		};

		// detach all event handlers
		this.destroy = function() {
			allItems.each(function(i, itemNode) {
				if (itemNode._flyoutmenu_destroy) {
					itemNode._flyoutmenu_destroy();
				}
			});
		};

		// initialize a top level item that has already been placed into mainList
		this.addItem = function(handle) { // todo: rename initTopLevelItem()
			handle = $(handle);
			var item = getRealTopLevelItem(handle);
			if (item.length) {
				initItem(item);
				var sublist = getSublist(item, listTag);
				if (sublist) {
					sublist.find(itemTag).each(function(i, itemNode) { // init all subitems
						initItem($(itemNode));
					});
				}
				if (relocate && sublist) {
					relocate.append(sublist.parent()); // relocate sublist's wrap
				}
				allItems = allItems.add(item);
				writeOrderingClassNames();
			}
		};

		// detach an item's event handlers and remove from DOM
		this.removeItem = function(handle) { // todo: rename
			handle = $(handle);
			var item = getRealTopLevelItem(handle);
			if (item.length) {
				if (item[0]._flyoutmenu_destroy) {
					item[0]._flyoutmenu_destroy(true);
				}
				item.remove();
				allItems = allItems.not(item);
				writeOrderingClassNames();
			}
		};

		// accessor
		this.getMainList = function() {
			return mainList;
		};


		//
		// initialize allItems and relocate
		//
		
		function writeOrderingClassNames() {
			var i = 1;
			getTopLevelItems(mainList).each(function(i, itemNode) {
				itemNode.className = itemNode.className.replace(/wsite-nav-\w+/, '');
				var item = $(itemNode);
				if (item.css('display') != 'none') {
					item.addClass('wsite-nav-' + i);
					i++;
				}
			});
		}

		allItems = getAllItems(mainList, itemTag);
		allItems.each(function(i, itemNode) {
			initItem($(itemNode));
		});
		writeOrderingClassNames();

		if (relocate) {
			getTopLevelItems(mainList).each(function(i, itemNode) {
				var sublist = getSublist($(itemNode), listTag);
				if (sublist) {
					relocate.append(sublist.parent());
				}
			});
		}

	};





	/****************************** more... link and menu *****************************/

	function condenseNav(topLevelSummary, moreItemHTML) { // can be called repeatedly for updating
		if (window.DISABLE_NAV_MORE) return;
		//console.log('condenseNav');
		var cpid = window.currentPage || currentPageId;
		var mainList = navFlyoutMenu.getMainList();
		var mainListChildren = mainList.children();
		var moreHandle;
		if (mainListChildren.length > 0) {
			moreHandle = mainListChildren.eq(-1).filter('.' + stylePrefix + '-nav-more');
			if (!moreHandle.length) {
				moreHandle = undefined;
			}
		}
		var alreadyMore = false;
		if (moreHandle) {
			moreHandle.hide();
			alreadyMore = true;
		}
		var isVertical;
		var handles = []; // holds all the handles up til the breaking element
		var itemCoords = [];
		var breakingHandle;
		var breakingIndex;
		var verticalContainer;
		var verticalMaxY;
		for (var i=0; i<topLevelSummary.length; i++) {
			var handle = navElm(topLevelSummary[i].id);
			handle.show();
		}
		for (var i=0; i<topLevelSummary.length; i++) {
			var handle = navElm(topLevelSummary[i].id);
			var item = getRealTopLevelItem(handle);
			if (!item) continue;
			var coords = getItemMassCoords(item);
			if (i == 1) {
				isVertical = Math.abs(coords[0].top - itemCoords[0][0].top) > Math.abs(coords[0].left - itemCoords[0][0].left);
				if (isVertical) {
					verticalContainer = item.closest('.wsite-nav-vertical');
					if (verticalContainer.length) {
						verticalMaxY = verticalContainer.offset().top + (parseInt(verticalContainer.css('padding-top')) || 0) + verticalContainer.height();
					}
				}
			}
			else if (!isVertical) {
				if (i > 1 && Math.abs(coords[0].top - itemCoords[i-1][0].top) > 5) {
					breakingHandle = handle;
					breakingIndex = i;
					break;
				}
			}
			else { // is vertical
				if (coords[1].top > verticalMaxY) {
					breakingHandle = handle;
					breakingIndex = i;
					break;
				}
			}
			handles.push(handle);
			itemCoords.push(coords);
		}
		if (breakingHandle) {
			if (moreHandle) {
				moreHandle.show();
			}else{
				var temp = $("<div/>");
				temp.html(moreItemHTML);
				moreHandle = temp.children().first();
				moreHandle.find('a').each(function(i, moreAnchorNode) {
					$(moreAnchorNode)
						.on('click', function() { return false })
						.css('position', 'relative') // match what initItem does
						.attr('id', stylePrefix + '-nav-more-a');
				});
				mainList.append(moreHandle);
			}
			var moreItem = getRealTopLevelItem(moreHandle);
			moreItem.css('position', 'relative'); // match what initItem does
			var hiddenItemIndices = [];
			for (var i=breakingIndex; i<topLevelSummary.length; i++) {
				navElm(topLevelSummary[i].id).hide();
				hiddenItemIndices.push(i);
			}
			if (!isVertical) {
				var firstItem = getRealTopLevelItem(navElm(topLevelSummary[0].id));
				for (var i=breakingIndex-1; i>=0; i--) {
					var firstItemCoords = getItemMassCoords(firstItem);
					var moreCoords = getItemMassCoords(moreItem);
					if (Math.abs(moreCoords[0].top - firstItemCoords[0].top) > 5) {
						handles[i].hide();
						hiddenItemIndices.unshift(i);
					}else{
						break;
					}
				}
			}
			if (hiddenItemIndices.length == 0) {
				// no items were hidden, no need for more...
				moreHandle.remove();
			}
			else if (hiddenItemIndices.length == topLevelSummary.length) {
				// all items were hidden, revert back
				for (var i=0; i<hiddenItemIndices.length; i++) {
					navElm(topLevelSummary[hiddenItemIndices[i]].id).show();
				}
				moreHandle.remove();
			}
			else {
				if (!alreadyMore) {
					var wrap = $("<div/>");
					wrap.addClass(stylePrefix+'-menu-wrap');
					var ul = $("<ul/>");
					ul.addClass(stylePrefix+'-menu');
					wrap.append(ul);
					for (var j=0; j<hiddenItemIndices.length; j++) {
						var pageSummary = topLevelSummary[hiddenItemIndices[j]];
						var liID = stylePrefix + '-nav-' + pageSummary.id;
						var li = $("<li id='" + liID + "' />");
						if (liID == cpid) {
							li.addClass(stylePrefix + '-nav-current');
						}
						var a = $("<a/>");
						if (pageSummary.onclick) {
							a.attr('href', '#');
							a.on('click', pageSummary.onclick);
						}else{
							var url = pageSummary.url;
							if (window.IS_ARCHIVE || url.match(/^http:\/\//)) {
								a.attr('href', url);
							}else{
								a.attr('href', '/' + url);
							}
						}
						if (pageSummary.onmouseover) {
							a.on('mouseover', pageSummary.onmouseover);
						}
						if (pageSummary.onmouseout) {
							a.on('mouseout', pageSummary.onmouseout);
						}
						li.append(a);
						var submenu = getRealTopLevelItem(navElm(topLevelSummary[hiddenItemIndices[j]].id))[0]._flyoutmenu_destroy();
						a.html(
							"<span class='" + stylePrefix + "-menu-title'>" +
								pageSummary.title +
							"</span>" +
							(submenu ? "<span class='" + stylePrefix + "-menu-arrow'>&gt;</span>" : '')
						);
						if (submenu) {
							li.append(submenu);
						}
						ul.append(li);
					}
					moreItem.append(wrap);
					navFlyoutMenu.addItem(moreItem);
					if (window.showEvent) {
						showEvent('navMore');
					}
				}
			}
		}
	}





	/************************ helpers for navigating and querying items/sublists/etc ********************/

	function inVerticalList(item, strict, aLiId) {
		var list = item.parent();
		if (list.hasClass(stylePrefix + '-nav-handle')) {
			list = list.parent();
		}
		var allItems = getTopLevelItems(list, strict, aLiId);
		if (allItems.length >= 2) {
			var o1 = allItems.eq(0).offset();
			var o2 = allItems.eq(1).offset();
			var diff = Math.abs(o1.left - o2.left) - Math.abs(o1.top - o2.top);
			if (diff != 0) {
				return diff < 0;
			}
		}
		return !isItemTopLevel(item);
			// default to returning false for top level user-defined css
			// and true for weebly-created submenus
	}

	function getTopLevelItems(list, strict, aLiId) {
		var res = [];
		list.children().each(function(i, handleNode) {
			var handle = $(handleNode);
			if (!strict ||
				handle.hasClass(stylePrefix + '-nav-handle') ||
				handle.hasClass(stylePrefix + '-nav-more') ||
				handleNode.id.match(/^pg/) ||
				(aLiId && handleNode.id==aLiId)) {
					var item = getRealTopLevelItem(handle);
					if (item.length) {
						res.push(item[0]);
					}
				}
		});
		return $(res);
	}

	function getRealTopLevelItem(item) { // todo: rename to getItemFromHandle()
		if (item.hasClass(stylePrefix + '-nav-handle')) {
			item = item.children().first();
		}
		if (!item.hasClass(stylePrefix + '-menu-wrap')) {
			// sometimes with SPAN handles, markup was invalid and DOM messed up
			// so make sure item is not a menu
			return item;
		}
	}

	function getAllItems(list, itemTag) {
		// get top level and all descendant items
		return list.find(itemTag).add(getTopLevelItems(list));
		// we want this list to be unique. jQuery already does this for us (for elements in DOM)
	}

	function getSiblings(item) {
		if (item.parent().hasClass(stylePrefix + '-nav-handle')) {
			return item.parent().siblings().children(':first-child');
		}else{
			// items aren't wrapped by separate handles
			return item.siblings();
		}
	}

	function getSublist(item, listTag) {
		var sublist = item.find(listTag).first();
		if (!sublist.length) {
			var next = item.next();
			if (next.hasClass(stylePrefix + '-menu-wrap')) {
				// sometimes with SPAN handles, markup is invalid, and it
				// makes the sublist a sibling AFTER the item
				sublist = next.children().first();
			}
		}
		if (!sublist.length) {
			sublist = undefined;
		}
		return sublist;
	}

	function isItemTopLevel(item) {
		var list = item.parent();
		if (list.hasClass(stylePrefix + '-nav-handle')) {
			list = list.parent();
		}
		return !list.hasClass(stylePrefix + '-menu');
	}

	function getItemMassCoords(item) {
		// look at the item and its A tag and return the largest rectangle of space it takes up
		// NOTE: we are still using offsetHeight/offsetWidth because jQuery 1.7.2 had a bug where
		//   getting curCSS on zero-height inline-displayed elements returned wrong value
		//   (because it converted to block first)
		var anchor = item.is('a') ? item : item.find('a');
		var p1 = item.offset();
		var p2 = { top:p1.top+item[0].offsetHeight, left:p1.left+item[0].offsetWidth };
		if (!anchor) {
			// messed up DOM (SPAN's around TD's and such) sometimes pushes A tag outside of item
			return [p1, p2];
		}
		var p3 = anchor.offset();
		var p4 = { top:p3.top+anchor[0].offsetHeight, left:p3.left+anchor[0].offsetWidth };
		var p5, p6;
		if (Math.abs(p1.left - p2.left) < 10) { // a tag is really small, doen't have any mass..
			// the inner A tag is probably floated and the LI isn't. lame. just use A tag's coords
			p5 = p3;
			p6 = p4;
		}else{
			p5 = { top:Math.min(p1.top, p3.top), left:Math.min(p1.left, p3.left) };
			p6 = { top:Math.max(p2.top, p4.top), left:Math.max(p2.left, p4.left) };
		}
		return [p5, p6];
	}

	function navElm(id) { // todo: rename to getHandle()
		var elm = $('#pg' + id);
		if (!elm.length && activeLiId) {
			elm = $('#' + activeLiId);
		}
		if (elm.length) {
			return elm;	
		}
	}




	/************************** helpers for theme-css-loaded detection ***********************/

	function isThemeCSSLoaded() {
		for (var i=0; i<document.styleSheets.length; i++) {
			try {
				if (document.styleSheets[i].title == stylePrefix+'-theme-css') {
					var sheet = document.styleSheets[i];
					var rules = sheet.cssRules || sheet.rules;
					return rules && rules.length > 0;
				}
			}
			catch (err) {}
		}
		return false;
	}

	function whenThemeCSSLoaded(callback) {
		if (isThemeCSSLoaded()) {
			callback();
		}else{
			var intervalID = setInterval(function() {
				if (isThemeCSSLoaded()) {
					clearInterval(intervalID);
					callback();
				}
			}, 200);
		}
	}
	
	if (!window.whenThemeCSSLoaded) {
		window.whenThemeCSSLoaded = whenThemeCSSLoaded;
	}


})(Weebly.jQuery);
