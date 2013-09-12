package ioio.robotar.pcconsole;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import cz.versarius.xchords.Chord;

public class ChordListPlainCellRenderer extends DefaultListCellRenderer {
	
	private static final long serialVersionUID = 6702005405805998236L;

	public Component getListCellRendererComponent(JList list, Object value, // value
																			// to
																			// display
			int index, // cell index
			boolean iss, // is the cell selected
			boolean chf) // the list and the cell have the focus
	{
		super.getListCellRendererComponent(list, value, index, iss, chf);

		Chord chord = (Chord) list.getModel().getElementAt(index);
		setText(chord.getPlainText());

		return this;
	}
	
	public int getHorizontalAlignment() {
        return RIGHT;
	}

}
