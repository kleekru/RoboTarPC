

// console.log calls wont fatal-error when it doesn't exist
// TODO: put this in the weebly editor js

if (!window.console) {
	window.console = {};
}

if (!window.console.log) {
	window.console.log = function(){};
}



// -----------------------------------------------------------------------------------------------------
// Copied and pasted from libraries/jquery_utils.js!!!!!! (TODO: better JS build system)
// -----------------------------------------------------------------------------------------------------


Weebly = _W = window.Weebly || {};

Weebly.jQuery = jQuery.noConflict(true); // relinquish control of `$` and `jQuery` and save reference

// FOR EDITOR: make plain-old `jQuery` available again
// FOR PUBLISHED SITES: if a different version of jQuery wasn't previously defined,
//    make it available for end-developer convenience
jQuery = window.jQuery || Weebly.jQuery;




(function($) {


	$.fn.up = function(selector) { // note: doesn't support index argument
		return this.eq(0).parent().closest(selector || '*');
	};


	$.fn.down = function(selector) { // note: doesn't support index argument
		if (!selector) {
			return this.eq(0).children(':first');
		}
		return this.eq(0).find(selector || '*').eq(0);
	};


	var idCounter = 1;

	$.fn.identify = function() {
		var id = this.attr('id');
		if (!id && this.length) {
			do {
				id = 'anonymous_element_' + idCounter++;
			}
			while ($('#' + id).length);
			this.attr('id', id);
		}
		return id;
	};

	/**
	 * HTML5 Placeholder polyfill
	 */
	$.fn.placeholder = function() {
        if (!('placeholder' in document.createElement('input'))) {
			var $el,
			    placeholder;

			this.each(function(i, el) {
				placeholder = el.getAttribute('placeholder');
				if (placeholder && el.nodeName.toLowerCase() === 'input') {
					$el = $(el).on({
						'focus': function(ev) {
							if (el.value === placeholder) {
								$(el).removeClass('wsite-placeholder');
								el.value = '';
							}
						},
						'blur': function(ev) {
							if (!el.value.length) {
								el.value = placeholder;
								el.className += ' wsite-placeholder';
							}
						}
					});

					// Initialize Input
					el.className += ' wsite-placeholder';
					el.value = placeholder;
				}
			});
        }
	};
	
	// we need the Prototype document.observe('dom:loaded') to work because slideshow JS relies on it
	// and often slideshow HTML/JS is cached in blog post HTML
	if (!document.observe) {
		document.observe = function(eventName, callback) {
			if (eventName == 'dom:loaded') {
				$(document).ready(callback);
			}
		};
	}


})(Weebly.jQuery);


Weebly.evalJSON = function(json) { // not related to jQuery, but Prototype had it, so keep it here for now
	// adapted from https://github.com/sstephenson/prototype/blob/master/src/prototype/lang/string.js
	var cx = /[\u0000\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g;
	if (cx.test(json)) {
		json = json.replace(cx, function(a) {
			return '\\u' + ('0000' + a.charCodeAt(0).toString(16)).slice(-4);
		});
	}
	try {
		return eval('(' + json + ')');
	}
	catch (e) { }
	throw new SyntaxError('Badly formed JSON string: ' + json);
};



// -----------------------------------------------------------------------------------------------------
// General Initializations
// -----------------------------------------------------------------------------------------------------


(function($) {

	/**
	 * Initialize the full/mobile version chooser when the url changes
	 */
	var initializeVersion = function() {
		var updateLinks = function() {
			var fullLink = $('.wsite-view-link-full');
			var mobileLink = $('.wsite-view-link-mobile');

			var windowhref = window.location.href || '';

			if (windowhref.indexOf('?') > -1) {
				windowhref += '&';
			} else {
				windowhref += '?';
			}

			fullLink.attr('href', windowhref + 'view=full');
			mobileLink.attr('href', windowhref + 'view=mobile');
		};

		updateLinks();

		var history = window.History;
		if (!history || !history.enabled) {
			return;
		}
		History.Adapter.bind(window, 'statechange', updateLinks);
	};

	/**
	 * Initialize HTML5 placeholder polyfill for browsers that do not
	 * support the the 'placeholder' attribute (eg: IE < 10)
	 */
	function initializePlaceholders() {
		if (!('placeholder' in document.createElement('input'))) {
			$(this).find('input[placeholder]').placeholder();
		}
	}

	$(document).ready(initializeVersion);
	$(document).ready(initializePlaceholders);

})(Weebly.jQuery);


// -----------------------------------------------------------------------------------------------------
// Comments
// -----------------------------------------------------------------------------------------------------


(function($) {


	window.blogCommentDisplayForm = function(url, wrapperId, commentId) {
		var wrapper = $('#' + wrapperId);
		var isOpen = wrapper.data('isReplyFormOpen') || false;
		var replyButton = wrapper.prev('.reply-comment').find('span').first();
		var cancelText = /*tl(*/'Cancel Reply'/*)tl*/;

		if (wrapper.data('locked')) return;
		wrapper.data('locked', true);

		var replyText = wrapper.data('replyText');
		if (!replyText) {
			replyText = replyButton.html();
			wrapper.data('replyText', replyText);
		}
		
		var iframe = $('#' + wrapperId + ' iframe');
		if (!iframe.length) {
			iframe = $('<iframe src="' + url + '" frameborder="0" allowtransparency="true" scrolling="no"></iframe>');
			$('#' + wrapperId + ' > div > div').first().append(iframe);
		}

		if (isOpen) {
			replyButton.html(replyText);
			wrapper.data('isReplyFormOpen', false);
			wrapper.down().hide('slide', {
				wrapper: wrapper,
				direction: 'up',
				duration: 1000,
				complete: function() {
					wrapper.data('locked', false);
				}
			});
		}
		else {
			replyButton.html(cancelText);
			wrapper.data('isReplyFormOpen', true);
			wrapper.down().show('slide', {
				wrapper: wrapper,
				direction: 'up',
				duration: 1000,
				complete: function() {
					wrapper.data('locked', false);
				}
			});
		}

		return false;
	};


})(Weebly.jQuery);




// -----------------------------------------------------------------------------------------------------
// Form Submission
// -----------------------------------------------------------------------------------------------------


(function(Weebly, $) {


	var stylePrefix = window.STYLE_PREFIX || 'weebly';
	var currentlyFocusedFormElement = null;


	function updateForm() {
		
		if (window.location.href.match(/posted=(.*)$/)) {
			var posted = Weebly.evalJSON(
				decodeURIComponent(window.location.href.match(/posted=(.*)$/)[1].replace(/\+/g, ' '))
			);
			$('form').each(function(i, formNode) {
				var form = $(formNode);
				$.each(posted, function(key, value) {
					if (typeof value === 'object') {
						$.each(value, function(subKey, subValue) {
							form.find('input').each(function(i, inputNode) {
								if (
									inputNode.name.replace(/_u\d*/, '') == key + '[' + subKey + ']'
									|| inputNode.name == key + '[' + subKey + ']'
								) {
									if (inputNode.type === 'checkbox') {
										inputNode.checked = 1;
									}
									else {
										inputNode.value = subValue;
									}
								}
							});
						});
					}
					else {
						form.find('input,textarea,select,button').each(function(i, inputNode) {
							if (
								inputNode.name.replace(/_u\d*/, '') == key
								|| inputNode.name == key
							) {
								var realName = inputNode.name;
								if (formNode[realName][0] && formNode[realName][0].type === 'radio') {
									form.find('radio[name=' + realName + ']').each(function(i, radioNode) {
										if (radioNode.value == value) {
											radioNode.checked = true;
										}
									});
								}
								else {
									inputNode.value = value;
								}
							}
						});
					}
				});
			});
		}

		if (window.location.href.match(/form-errors=(.*?)&/) && window.location.href.match(/ucfid%22%3A%22(.*?)%/) ) {
			var errors = window.location.href.match(/form\-errors=(.*?)&/)[1].split(',');
			var ucfid = window.location.href.match(/ucfid%22%3A%22(.*?)%/)[1];
			var form = $('#form-' + ucfid);
			$.each(errors, function(i, field) {
				field = decodeURIComponent(field);
				form.find('input,textarea,select,button').each(function(i, inputNode) {
					if (
						inputNode.name.replace(/_u\d*/, '') == field 
						|| inputNode.name.replace(/.*_u/, '_u') == field
						|| inputNode.name.replace(/\[.*\]$/, '') == field
					) {
						$(inputNode)
							.addClass('form-input-error')
							.up('.' + stylePrefix + '-form-field')
								.addClass('form-field-error');
					}
				});
			});
			$('#' + ucfid + '-form-parent').after('<div>Please correct the highlighted fields</div>');
		}

		if (window.location.href.match(/success\=1/) && window.location.href.match(/ucfid\=(.*)/)) {
			var ucfid = window.location.href.match(/ucfid\=(.*?)&/)[1];
			var form = $('#form-'+ucfid);
			var confText = 'Your data was successfully submitted.';
			var textMatch = window.location.href.match(/text=(.*?)&/);
			if (textMatch) {
				confText = decodeURIComponent(textMatch[1].replace(/\+/g, ' '));
			}
			form.html('<div>' + confText + '</div>');
		}
		
	}


	function listenToResponse() {

		function receiveMessage(message) {
			var response = Weebly.evalJSON(message.data);
			switch (response.action) {
				case "finished" :
					var ucfid = response.data.ucfid;
					var form = $("#form-" + ucfid);
					form.hide();
					var msgElm = $('#' + ucfid + '-msg');
					if (!msgElm.length) {
						msgElm = $('<div id="'+ucfid+'-msg"/>')
							.insertAfter(form);
					}
					msgElm.html(response.data.message);
					msgElm.effect('highlight', {}, 2000);
					$('body').animate({
						scrollTop: msgElm.offset().top
					}, {
						easing: 'easeOutQuart',
						duration: 2000
					});
					return;
				case "redirect" :
					window.location = response.data.location;
					return;
				case "error" :
					var errors = response.data['error-fields'];
					var ucfid = response.data.ucfid;
					var form = $("#form-" + ucfid);
					form.find('input,textarea,select,button')
						.filter('.form-input-error')
						.each(function(i, inputNode) {
							$(inputNode)
								.removeClass('form-input-error')
								.up('.' + stylePrefix + '-form-field')
								.removeClass('form-field-error');
						});
					$.each(errors, function(i, field) {
						form.find('input,textarea,select,button')
							.each(function(i, inputNode) {
								if (
									inputNode.name.replace(/_u\d*/, '') == field 
									|| inputNode.name.replace(/.*_u/, '_u') == field
									|| inputNode.name.replace(/\[.*\]$/, '') == field
								) {
									$(inputNode)
										.addClass('form-input-error')
										.up('.' + stylePrefix + '-form-field')
											.addClass('form-field-error');
								} 
							});
					});
					var msgElm = $('#' + ucfid + '-msg');
					if (!msgElm.length) {
						msgElm = $('<div id="' + ucfid + '-msg"/>').insertAfter(form);
					}
					msgElm.html(response.data.message);
					return;
			}
		}

		$('form').each(function(i, formNode) {
			if (formNode.action.match(/formSubmit\.php$/)) {
				
				formNode.action = formNode.action.replace(
					/(.*)\/formSubmit\.php$/,
					window.location.protocol + "//" + window.location.host +"/ajax/apps/formSubmitAjax.php"
				);
				formNode.acceptCharset = "UTF-8";
				var name = formNode.id + "-target";
				var iframe = $('<iframe name="' + name + '"/>')
					.hide()
					.attr('id', name)
					.insertAfter(formNode);
				var iframeNode = iframe[0];
				formNode.target = iframeNode.id;
			
				if (!window.postMessage) {
					iframe.on('load', function() {
						try {
							var location = (iframeNode.contentDocument || iframeNode.contentWindow.document).location.href;
							var data = (iframeNode.contentDocument || iframeNode.contentWindow.document).body.firstChild.nodeValue;
							if (location != "about:blank") {
								receiveMessage({
									data: data,
									source: iframeNode.contentWindow
								});
							}
						}
						catch(e) {}
					});
				}
			}   
		});

		if (window.postMessage) {
			$(window).on('message', function(event) {
				try{
					receiveMessage(event.originalEvent);
				} catch(e) {
					//console.log(e);
				}
			});
		}

	}


	function showFieldInstructions(msg, pointTo) {
		
		removeFieldInstructions();
		var target = $(pointTo);
		target.identify();
		var image = false;
		var el =
			$("<div/>", {
				'class': 'instructions-container',
				'id': target.attr('id') + '-instructions'
			})
			.html(msg)
			.appendTo('body')
			.on('click', function() {
				el.hide().remove();
			});
		
		currentVisibleError = el.identify();
		
		var elWidth = el.outerWidth();
		var elHeight = el.outerHeight();
		var targetWidth = target.outerWidth();
		var targetHeight = target.outerHeight();
		var targetOffset = target.offset();
		el.css({
			top: targetOffset.top + targetHeight/2 - elHeight/2,
			left: targetOffset.left + targetWidth + 20
		});
		
		//set arrow position
		var imageTop  = Math.floor(elHeight/2) - 10;
		var imageLeft = '-13';
		el.append(
			'<img' +
			' src="http://www.weebly.com/images/error_arrow_left.gif"' +
			' style="position: absolute; left:'+ imageLeft + 'px; top:' + imageTop + 'px;"' +
			'/>'
		);
	}


	function handlerRemoveFieldInstructions(event) {
		var el = $(event.target);
		if (
			!el.hasClass(stylePrefix + '-form-field') &&
			!el.up('.' + stylePrefix + '-form-field').length
		) {
			$(document).off('mousemove', handlerRemoveFieldInstructions);
			removeFieldInstructions();
		}
	}


	function removeFieldInstructions() {
		$('.instructions-container').each(function(i, node) {
			var inputID = node.id.replace('-instructions', '');
			if (
				!currentlyFocusedFormElement ||
				$('#' + inputID).up('.' + stylePrefix + '-form-field').identify() != currentlyFocusedFormElement
			) {
				$(node).remove();
			}
		});
	}


	function fieldInstructionsHandler() {
		$('.' + stylePrefix + '-form-instructions').each(function(i, elNode) {
			var el = $(elNode);
			if (el.html()) {

				// Escape special characters for jQuery
				var instructions = elNode.id.replace('instructions', 'input').replace(/(#|;|&|,|\.|\+|\*|~|\'|\:|\"|\!|\^|\$|[|]|\(|\)|\=|\>|\||\/|\?|\s)/g, '\\$1'),
				    pointTo = $('#' + instructions);

				// select inputs
				if (!pointTo.length) {
					pointTo = el.up('.' + stylePrefix + '-form-field').down('.form-select');
				}
				// radio/checkbox inputs
				if (!pointTo.length) {
					pointTo = el.up('.' + stylePrefix + '-form-field').down('.' + stylePrefix + '-form-label');
				}
				var container = pointTo.up('.' + stylePrefix + '-form-field');
				if (
					pointTo.up('.' + stylePrefix + '-form-input-container').length &&
					pointTo.up('.' + stylePrefix + '-form-input-container').hasClass(stylePrefix + '-form-left')
				) {
					pointTo = pointTo.up('.' + stylePrefix + '-form-input-container').next('.' + stylePrefix + '-form-right');
				}
				container.on('mouseover', function(event) {
					if ($(this).hasClass(stylePrefix + '-form-field')) {
						if (!container.down('.instructions-container').length) {
							showFieldInstructions(el.html(), pointTo);
						}
						$(document).on('mousemove', handlerRemoveFieldInstructions);
					}
				});
			}
		});
	}


	function setWeeblyApproved() {
		$("input[name=" + stylePrefix + "_approved]").val('weebly');
		$(document)
			.off('mousedown', setWeeblyApproved)
			.off('keydown', setWeeblyApproved);
	}


	$(document)
		.ready(updateForm)
		.ready(fieldInstructionsHandler)
		.ready(listenToResponse)
		.on('mousedown', setWeeblyApproved)
		.on('keydown', setWeeblyApproved)
		.on('click', function(event) {
			var el = $(event.target);
			var up = el.up('.' + stylePrefix + '-form-field');
			if (el.hasClass(stylePrefix + '-form-field')) {
				up = el;
			}
			if (up.length) {
				currentlyFocusedFormElement = up.identify();
			}
			else{
				currentlyFocusedFormElement = null;
			}
			removeFieldInstructions();
		});

})(Weebly, Weebly.jQuery);



// -----------------------------------------------------------------------------------------------------
// Search Header and Element
// -----------------------------------------------------------------------------------------------------


(function($) {

	/**
	 * Set up handlers for our search field. We have to do this because
	 * the 'submit' / 'search' button isn't a button but a span. Which
	 * makes it easier to stylize, but harder to convert into a button.
	 */
	function listenToSearchSubmit() {
		// Header search form
		// ------------------
		$('#wsite-header-search-form').on('click', 'span.wsite-search-button', function(ev) {
			$(ev.delegateTarget).submit();
		}).on('submit', function(ev) {
			// Disable search from Theme Previews
			return !window.document.body.className.match(/wsite-page-theme_browser\/preview/);
		});

		// Search element
		// ------------------
		$('form.wsite-search-element-form')
			.on('click', 'span.wsite-search-element-submit', function(ev) {
				$(ev.delegateTarget).submit();
			})
			.on('submit', function() {
				// Disable search from Theme Previews
				return !window.document.body.className.match(/wsite-page-theme_browser\/preview/);
			});
	}

	$(document).ready(listenToSearchSubmit);

})(Weebly.jQuery);



// -----------------------------------------------------------------------------------------------------
// Fancybox
// -----------------------------------------------------------------------------------------------------


(function($) {

	$(document).ready(function() {

		$('a[rel=lightbox]') // ...standalone lightbox <a>'s
			.removeAttr('rel') // don't group them by rel (fancybox will want to)
			.add('a[rel^="lightbox["]') // ...select gallery <a>'s too
				.addClass('w-fancybox');
				
		if ($.fn.fancybox) { // mobile doesn't have fancybox. uses photoswipe instead

			// it's best to use a global selector to initialize fancybox so that it can use event delegation internally.
			// PS- fancybox's same event delegation is buggy when used like this:
			// $('.whatever').removeClass('whatever').fancybox()
			$('.w-fancybox')
				.fancybox({
					prevEffect: 'none',
					nextEffect: 'none',
					padding: 10,
					helpers: {
						title: {
							type: 'inside'
						},
						thumbs: {
							width: 50,
							height: 50
						}
					}
				});
				
			window.lightboxLoaded = true;
			
		}

	});

})(Weebly.jQuery);




// -----------------------------------------------------------------------------------------------------
// PhotoSwipe
// -----------------------------------------------------------------------------------------------------


(function($) {

	
	var callbacks = [];
	var insertedTags = false;
	var isTouch = 'ontouchstart' in document.documentElement;

	
	window.whenPhotoSwipeLoaded = function(callback) {
		// TODO: could jqueryify this
		if (window.Code && window.Code.PhotoSwipe) {
			callback();
		}else{
			callbacks.push(callback);
			if (!insertedTags) {
				insertedTags = true;
				var head = document.getElementsByTagName('head')[0];
				var script = document.createElement('script');
				var cssLink = document.createElement('link');
				script.type = 'text/javascript';
				script.async = true;
				script.src = STATIC_BASE + "weebly/libraries/photoswipe/code.photoswipe-3.0.4-custom.min.js";
				cssLink.setAttribute('rel', 'stylesheet');
				cssLink.setAttribute('type', 'text/css');
				cssLink.setAttribute('href', STATIC_BASE + "weebly/libraries/photoswipe/photoswipe.css");
				head.insertBefore(cssLink, head.firstChild);
				head.insertBefore(script, head.firstChild);
			}
		}
	};

	
	window._photoSwipeLoaded = function() { // called by the photoswipe JS file
		for (var i=0; i<callbacks.length; i++) {
			callbacks[i]();
		}
	};


	function initPhotoSwipeAnchors(anchorNodes) {
		// kill lightbox onclick
		$(anchorNodes)
			.removeClass('w-fancybox')
			.attr('onclick', '')
			.off('click');
		whenPhotoSwipeLoaded(function() {
			Code.PhotoSwipe.attach(
				anchorNodes,
				{
					captionAndToolbarFlipPosition: true,
					captionAndToolbarAutoHideDelay: 0, // always show
					loop: false
				}
			);
		});
	}


	if (isTouch) {
		$(document).ready(function() {
			var anchorGroups = {};
			$('a.w-fancybox').each(function(i, anchorNode) {
				var match = ($(anchorNode).attr('rel') || '').match(/^lightbox\[(.*)\]/);
				if (match) {
					var groupName = match[1];
					anchorGroups[groupName] = anchorGroups[groupName] || [];
					anchorGroups[groupName].push(anchorNode);
				}else{
					initPhotoSwipeAnchors([ anchorNode ]);
				}
			});
			$.each(anchorGroups, function(name, anchorNodes) {
				initPhotoSwipeAnchors(anchorNodes);
			});
		});
	}

	
})(Weebly.jQuery);

