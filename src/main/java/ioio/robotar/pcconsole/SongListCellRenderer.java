package ioio.robotar.pcconsole;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import cz.versarius.xsong.Song;

public class SongListCellRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = 12453562423L;
	
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

		Song song = (Song) list.getModel().getElementAt(index);
		setText(song.getTitle());

		return this;
	}


}
