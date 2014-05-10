package com.robotar.ui;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import cz.versarius.xchords.ChordLibrary;

public class ChordLibraryListCellRenderer extends DefaultListCellRenderer {
	
	private static final long serialVersionUID = 8560255881254056762L;

	/*
	 * This is the only method defined by ListCellRenderer. We just reconfigure
	 * the Jlabel each time we're called.
	 */
	public Component getListCellRendererComponent(JList list, Object value, // value
																			// to
																			// display
			int index, // cell index
			boolean iss, // is the cell selected
			boolean chf) // the list and the cell have the focus
	{
		/*
		 * The DefaultListCellRenderer class will take care of the JLabels text
		 * property, it's foreground and background colors, and so on.
		 */
		super.getListCellRendererComponent(list, value, index, iss, chf);

		ChordLibrary lib = (ChordLibrary) list.getModel().getElementAt(index);
		setText(lib.getNameWithMark());

		return this;
	}


}
