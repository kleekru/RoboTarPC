package ioio.robotar.pcconsole;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import cz.versarius.xchords.Chord;
import cz.versarius.xchords.StringInfo;
import cz.versarius.xchords.StringState;

public class ChordRadioPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 18765864433L;
	
	// button groups for radio buttons
	private ButtonGroup lowEstring;
	private ButtonGroup Astring;
	private ButtonGroup Dstring;
	private ButtonGroup Gstring;
	private ButtonGroup Bstring;
	private ButtonGroup highEstring;

	// radio buttons
	private JRadioButton radioButton_6M;
	private JRadioButton radioButton_5M;
	private JRadioButton radioButton_4M;
	private JRadioButton radioButton_3M;
	private JRadioButton radioButton_2M;
	private JRadioButton radioButton_1M;
	private JRadioButton radioButton_6O;
	private JRadioButton radioButton_5O;
	private JRadioButton radioButton_4O;
	private JRadioButton radioButton_3O;
	private JRadioButton radioButton_2O;
	private JRadioButton radioButton_1O;
	private JRadioButton radioButton_61;
	private JRadioButton radioButton_51;
	private JRadioButton radioButton_41;
	private JRadioButton radioButton_31;
	private JRadioButton radioButton_21;
	private JRadioButton radioButton_11;
	private JRadioButton radioButton_62;
	private JRadioButton radioButton_52;
	private JRadioButton radioButton_42;
	private JRadioButton radioButton_32;
	private JRadioButton radioButton_22;
	private JRadioButton radioButton_12;
	private JRadioButton radioButton_63;
	private JRadioButton radioButton_53;
	private JRadioButton radioButton_43;
	private JRadioButton radioButton_33;
	private JRadioButton radioButton_23;
	private JRadioButton radioButton_13;
	private JRadioButton radioButton_64;
	private JRadioButton radioButton_54;
	private JRadioButton radioButton_44;
	private JRadioButton radioButton_34;
	private JRadioButton radioButton_24;
	private JRadioButton radioButton_14;

	// chord name
	private JTextField chordName;
	
	public ChordRadioPanel(ResourceBundle messages) {
		super();

		setAlignmentY(Component.BOTTOM_ALIGNMENT);
		setAlignmentX(Component.RIGHT_ALIGNMENT);
		setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec.decode("5px"),
				ColumnSpec.decode("5px"), ColumnSpec.decode("101px"),
				ColumnSpec.decode("75px"), FormFactory.DEFAULT_COLSPEC,
				ColumnSpec.decode("3px"), FormFactory.DEFAULT_COLSPEC,
				ColumnSpec.decode("4px"), FormFactory.DEFAULT_COLSPEC,
				ColumnSpec.decode("3px"), FormFactory.DEFAULT_COLSPEC,
				ColumnSpec.decode("4px"), FormFactory.DEFAULT_COLSPEC,
				ColumnSpec.decode("5px"), FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(86dlu;default)"), }, new RowSpec[] {
				FormFactory.DEFAULT_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, }));

		// Group the radio buttons.
		lowEstring = new ButtonGroup();
		Astring = new ButtonGroup();
		Dstring = new ButtonGroup();
		Gstring = new ButtonGroup();
		Bstring = new ButtonGroup();
		highEstring = new ButtonGroup();

		// muted radio buttons
		JLabel lblClickToMute = new JLabel(
				messages.getString("robotar.chords.mute_string"));
		add(lblClickToMute, "3, 1");
		lblClickToMute.setFont(new Font("Segoe UI", Font.BOLD, 16));

		radioButton_6M = new JRadioButton("");
		radioButton_6M.setBackground(Color.BLACK);
		add(radioButton_6M, "5, 1, left, center");
		lowEstring.add(radioButton_6M);
		radioButton_6M.addActionListener(this);

		radioButton_5M = new JRadioButton("");
		radioButton_5M.setBackground(Color.BLACK);
		add(radioButton_5M, "7, 1, left, center");
		Astring.add(radioButton_5M);
		radioButton_5M.addActionListener(this);

		radioButton_4M = new JRadioButton("");
		radioButton_4M.setBackground(Color.BLACK);
		add(radioButton_4M, "9, 1, left, center");
		Dstring.add(radioButton_4M);
		radioButton_4M.addActionListener(this);

		radioButton_3M = new JRadioButton("");
		radioButton_3M.setBackground(Color.BLACK);
		add(radioButton_3M, "11, 1, left, center");
		Gstring.add(radioButton_3M);
		radioButton_3M.addActionListener(this);

		radioButton_2M = new JRadioButton("");
		radioButton_2M.setBackground(Color.BLACK);
		add(radioButton_2M, "13, 1, left, center");
		Bstring.add(radioButton_2M);
		radioButton_2M.addActionListener(this);

		radioButton_1M = new JRadioButton("");
		radioButton_1M.setBackground(Color.BLACK);
		add(radioButton_1M, "15, 1, left, center");
		highEstring.add(radioButton_1M);
		radioButton_1M.addActionListener(this);

		// open radio buttons
		JLabel lblOpenString = new JLabel(
				messages.getString("robotar.chords.open_string"));
		lblOpenString.setFont(new Font("Segoe UI", Font.BOLD, 16));
		add(lblOpenString, "3, 2, left, center");

		radioButton_6O = new JRadioButton("");
		radioButton_6O.setBackground(Color.WHITE);
		add(radioButton_6O, "5, 2, left, center");
		lowEstring.add(radioButton_6O);
		radioButton_6O.addActionListener(this);

		radioButton_5O = new JRadioButton("");
		radioButton_5O.setBackground(Color.WHITE);
		add(radioButton_5O, "7, 2, left, center");
		Astring.add(radioButton_5O);
		radioButton_5O.addActionListener(this);

		radioButton_4O = new JRadioButton("");
		radioButton_4O.setBackground(Color.WHITE);
		radioButton_4O.setActionCommand("new Radio Button");
		add(radioButton_4O, "9, 2, left, center");
		Dstring.add(radioButton_4O);
		radioButton_4O.addActionListener(this);

		radioButton_3O = new JRadioButton("");
		radioButton_3O.setBackground(Color.WHITE);
		add(radioButton_3O, "11, 2, left, center");
		Gstring.add(radioButton_3O);
		radioButton_3O.addActionListener(this);

		radioButton_2O = new JRadioButton("");
		radioButton_2O.setBackground(Color.WHITE);
		add(radioButton_2O, "13, 2, left, center");
		Bstring.add(radioButton_2O);
		radioButton_2O.addActionListener(this);

		radioButton_1O = new JRadioButton("");
		radioButton_1O.setBackground(Color.WHITE);
		add(radioButton_1O, "15, 2, left, center");
		highEstring.add(radioButton_1O);
		radioButton_1O.addActionListener(this);

		// chord name
		JLabel lblChordName = new JLabel(
				messages.getString("robotar.chords.chord_name"));
		add(lblChordName, "17, 2, center, default");
		lblChordName.setForeground(Color.BLACK);
		lblChordName.setFont(new Font("Segoe UI", Font.BOLD, 16));

		// 1st fret
		JLabel lblstFret = new JLabel(
				messages.getString("robotar.chords.first_fret"));
		lblstFret.setFont(new Font("Segoe UI", Font.BOLD, 16));
		add(lblstFret, "3, 3, left, center");

		radioButton_61 = new JRadioButton("");
		radioButton_61.setBackground(new Color(205, 133, 63));
		add(radioButton_61, "5, 3, left, center");
		lowEstring.add(radioButton_61);
		radioButton_61.addActionListener(this);

		radioButton_51 = new JRadioButton("");
		radioButton_51.setBackground(new Color(205, 133, 63));
		add(radioButton_51, "7, 3, left, center");
		Astring.add(radioButton_51);
		radioButton_51.addActionListener(this);

		radioButton_41 = new JRadioButton("");
		radioButton_41.setBackground(new Color(205, 133, 63));
		add(radioButton_41, "9, 3, left, center");
		Dstring.add(radioButton_41);
		radioButton_41.addActionListener(this);

		radioButton_31 = new JRadioButton("");
		radioButton_31.setBackground(new Color(205, 133, 63));
		add(radioButton_31, "11, 3, left, center");
		Gstring.add(radioButton_31);
		radioButton_31.addActionListener(this);

		radioButton_21 = new JRadioButton("");
		radioButton_21.setBackground(new Color(205, 133, 63));
		add(radioButton_21, "13, 3, left, center");
		Bstring.add(radioButton_21);
		radioButton_21.addActionListener(this);

		radioButton_11 = new JRadioButton("");
		radioButton_11.setBackground(new Color(205, 133, 63));
		add(radioButton_11, "15, 3, left, center");
		highEstring.add(radioButton_11);
		radioButton_11.addActionListener(this);

		// frmBlueAhuizoteChords.setFocusTraversalPolicy(new
		// FocusTraversalOnArray(new Component[]{btnNewChord_1, tglbtnTestChord,
		// btnAddToSong, chordName_1, radioButton_6M, radioButton_5M,
		// radioButton_4M, radioButton_3M, radioButton_2M, radioButton_1M,
		// radioButton_6O, radioButton_5O, radioButton_4O, radioButton_3O,
		// radioButton_2O, radioButton_1O, radioButton_61, radioButton_51,
		// radioButton_41, radioButton_31, radioButton_21, radioButton_11,
		// radioButton_62, radioButton_52, radioButton_42, radioButton_32,
		// radioButton_22, radioButton_12, radioButton_63, radioButton_53,
		// radioButton_43, radioButton_33, radioButton_23, radioButton_13,
		// radioButton_64, radioButton_54, radioButton_44, radioButton_34,
		// radioButton_24, radioButton_14, listChords}));

		// 2nd fret
		JLabel lblndFret = new JLabel(
				messages.getString("robotar.chords.second_fret"));
		lblndFret.setFont(new Font("Segoe UI", Font.BOLD, 16));
		add(lblndFret, "3, 4, left, center");

		radioButton_62 = new JRadioButton("");
		radioButton_62.setBackground(new Color(205, 133, 63));
		add(radioButton_62, "5, 4, left, center");
		lowEstring.add(radioButton_62);
		radioButton_62.addActionListener(this);

		radioButton_52 = new JRadioButton("");
		radioButton_52.setBackground(new Color(205, 133, 63));
		add(radioButton_52, "7, 4, left, center");
		Astring.add(radioButton_52);
		radioButton_52.addActionListener(this);

		radioButton_42 = new JRadioButton("");
		radioButton_42.setBackground(new Color(205, 133, 63));
		add(radioButton_42, "9, 4, left, center");
		Dstring.add(radioButton_42);
		radioButton_42.addActionListener(this);

		radioButton_32 = new JRadioButton("");
		radioButton_32.setBackground(new Color(205, 133, 63));
		add(radioButton_32, "11, 4, left, center");
		Gstring.add(radioButton_32);
		radioButton_32.addActionListener(this);

		radioButton_22 = new JRadioButton("");
		radioButton_22.setBackground(new Color(205, 133, 63));
		add(radioButton_22, "13, 4, left, center");
		Bstring.add(radioButton_22);
		radioButton_22.addActionListener(this);

		radioButton_12 = new JRadioButton("");
		radioButton_12.setBackground(new Color(205, 133, 63));
		add(radioButton_12, "15, 4, left, center");
		highEstring.add(radioButton_12);
		radioButton_12.addActionListener(this);

		// 3rd fret
		JLabel lblrdFret = new JLabel(
				messages.getString("robotar.chords.third_fret"));
		lblrdFret.setFont(new Font("Segoe UI", Font.BOLD, 16));
		add(lblrdFret, "3, 5, left, center");

		radioButton_63 = new JRadioButton("");
		radioButton_63.setBackground(new Color(205, 133, 63));
		add(radioButton_63, "5, 5, left, center");
		lowEstring.add(radioButton_63);
		radioButton_63.addActionListener(this);

		radioButton_53 = new JRadioButton("");
		radioButton_53.setBackground(new Color(205, 133, 63));
		add(radioButton_53, "7, 5, left, center");
		Astring.add(radioButton_53);
		radioButton_53.addActionListener(this);

		radioButton_43 = new JRadioButton("");
		Dstring.add(radioButton_43);
		radioButton_43.setBackground(new Color(205, 133, 63));
		add(radioButton_43, "9, 5");

		radioButton_33 = new JRadioButton("");
		Gstring.add(radioButton_33);
		radioButton_33.setBackground(new Color(205, 133, 63));
		add(radioButton_33, "11, 5");

		radioButton_23 = new JRadioButton("");
		Bstring.add(radioButton_23);
		radioButton_23.setBackground(new Color(205, 133, 63));
		add(radioButton_23, "13, 5");

		radioButton_13 = new JRadioButton("");
		highEstring.add(radioButton_13);
		radioButton_13.setBackground(new Color(205, 133, 63));
		add(radioButton_13, "15, 5");

		// 4th fret
		JLabel lblthFret = new JLabel(
				messages.getString("robotar.chords.fourth_fret"));
		lblthFret.setFont(new Font("Segoe UI", Font.BOLD, 16));
		add(lblthFret, "3, 6, left, center");

		radioButton_64 = new JRadioButton("");
		radioButton_64.setBackground(new Color(205, 133, 63));
		add(radioButton_64, "5, 6, left, center");
		lowEstring.add(radioButton_64);
		radioButton_64.addActionListener(this);

		radioButton_54 = new JRadioButton("");
		radioButton_54.setBackground(new Color(205, 133, 63));
		add(radioButton_54, "7, 6, left, center");
		Astring.add(radioButton_54);
		radioButton_54.addActionListener(this);

		radioButton_44 = new JRadioButton("");
		radioButton_44.setBackground(new Color(205, 133, 63));
		add(radioButton_44, "9, 6, left, center");
		Dstring.add(radioButton_44);
		radioButton_44.addActionListener(this);

		radioButton_34 = new JRadioButton("");
		radioButton_34.setBackground(new Color(205, 133, 63));
		add(radioButton_34, "11, 6, left, center");
		Gstring.add(radioButton_34);
		radioButton_34.addActionListener(this);

		radioButton_24 = new JRadioButton("");
		radioButton_24.setBackground(new Color(205, 133, 63));
		add(radioButton_24, "13, 6, left, center");
		Bstring.add(radioButton_24);
		radioButton_24.addActionListener(this);

		radioButton_14 = new JRadioButton("");
		radioButton_14.setBackground(new Color(205, 133, 63));
		add(radioButton_14, "15, 6, left, center");
		highEstring.add(radioButton_14);
		radioButton_14.addActionListener(this);

		// chord name
		chordName = new JTextField();
		add(chordName, "17, 3, center, default");
		chordName.setColumns(10);
		chordName.setText(messages.getString("robotar.chords.enter_chord_name"));

	}

	public void clear() {
		lowEstring.clearSelection();
		Astring.clearSelection();
		Dstring.clearSelection();
		Gstring.clearSelection();
		Bstring.clearSelection();
		highEstring.clearSelection();
	}

	public void setupRadios(Chord chord) {
		StringInfo[] strings = chord.getStrings();
		for (int i = 0; i < strings.length; i++) {
			StringInfo si = chord.getString(i);
			if (si == null) {
				//clearString(i);
				openString(i);
			} else {
				StringState state = si.getState();
				if (state == StringState.DISABLED) {
					muteString(i);
				} else if (state == StringState.OPEN) {
					openString(i);
				} else {
					setString(i, si.getFret());
				}
			}
		}
	}

	/**
	 * @param i
	 *            from 0 to 5
	 */
	private void clearString(int i) {
		switch (i) {
		case 0:
			lowEstring.clearSelection();
			break;
		case 1:
			Astring.clearSelection();
			break;
		case 2:
			Dstring.clearSelection();
			break;
		case 3:
			Gstring.clearSelection();
			break;
		case 4:
			Bstring.clearSelection();
			break;
		case 5:
			highEstring.clearSelection();
			break;
		default:
			// nothing
		}
	}

	private void muteString(int i) {
		switch (i) {
		case 0:
			radioButton_6M.setSelected(true);
			break;
		case 1:
			radioButton_5M.setSelected(true);
			break;
		case 2:
			radioButton_4M.setSelected(true);
			break;
		case 3:
			radioButton_3M.setSelected(true);
			break;
		case 4:
			radioButton_2M.setSelected(true);
			break;
		case 5:
			radioButton_1M.setSelected(true);
			break;
		default:
			// nothing
		}
	}

	private void openString(int i) {
		switch (i) {
		case 0:
			radioButton_6O.setSelected(true);
			break;
		case 1:
			radioButton_5O.setSelected(true);
			break;
		case 2:
			radioButton_4O.setSelected(true);
			break;
		case 3:
			radioButton_3O.setSelected(true);
			break;
		case 4:
			radioButton_2O.setSelected(true);
			break;
		case 5:
			radioButton_1O.setSelected(true);
			break;
		default:
			// nothing
		}
	}

	private void setString(int i, int fret) {
		switch (i) {
		case 0:
			switch (fret) {
			case 1:
				radioButton_61.setSelected(true);
				break;
			case 2:
				radioButton_62.setSelected(true);
				break;
			case 3:
				radioButton_63.setSelected(true);
				break;
			case 4:
				radioButton_64.setSelected(true);
				break;
			default:
				// nothing
			}
			break;
		case 1:
			switch (fret) {
			case 1:
				radioButton_51.setSelected(true);
				break;
			case 2:
				radioButton_52.setSelected(true);
				break;
			case 3:
				radioButton_53.setSelected(true);
				break;
			case 4:
				radioButton_54.setSelected(true);
				break;
			default:
				// nothing
			}
			break;
		case 2:
			switch (fret) {
			case 1:
				radioButton_41.setSelected(true);
				break;
			case 2:
				radioButton_42.setSelected(true);
				break;
			case 3:
				radioButton_43.setSelected(true);
				break;
			case 4:
				radioButton_44.setSelected(true);
				break;
			default:
				// nothing
			}
			break;
		case 3:
			switch (fret) {
			case 1:
				radioButton_31.setSelected(true);
				break;
			case 2:
				radioButton_32.setSelected(true);
				break;
			case 3:
				radioButton_33.setSelected(true);
				break;
			case 4:
				radioButton_34.setSelected(true);
				break;
			default:
				// nothing
			}
			break;
		case 4:
			switch (fret) {
			case 1:
				radioButton_21.setSelected(true);
				break;
			case 2:
				radioButton_22.setSelected(true);
				break;
			case 3:
				radioButton_23.setSelected(true);
				break;
			case 4:
				radioButton_24.setSelected(true);
				break;
			default:
				// nothing
			}
			break;
		case 5:
			switch (fret) {
			case 1:
				radioButton_11.setSelected(true);
				break;
			case 2:
				radioButton_12.setSelected(true);
				break;
			case 3:
				radioButton_13.setSelected(true);
				break;
			case 4:
				radioButton_14.setSelected(true);
				break;
			default:
				// nothing
			}
			break;
		default:
			// nothing
		}
	}

	public Chord createChordFromRadios() {
		Chord chord = new Chord();
		chord.setName(getChordName());
		StringInfo si = getFrom("e6", radioButton_6M, radioButton_6O, radioButton_61, radioButton_62, radioButton_63, radioButton_64);
		chord.setString(0, si);
		si = getFrom("a", radioButton_5M, radioButton_5O, radioButton_51, radioButton_52, radioButton_53, radioButton_54);
		chord.setString(1, si);
		si = getFrom("d", radioButton_4M, radioButton_4O, radioButton_41, radioButton_42, radioButton_43, radioButton_44);
		chord.setString(2, si);
		si = getFrom("g", radioButton_3M, radioButton_3O, radioButton_31, radioButton_32, radioButton_33, radioButton_34);
		chord.setString(3, si);
		si = getFrom("b", radioButton_2M, radioButton_2O, radioButton_21, radioButton_22, radioButton_23, radioButton_24);
		chord.setString(4, si);
		si = getFrom("e1", radioButton_1M, radioButton_1O, radioButton_11, radioButton_12, radioButton_13, radioButton_14);
		chord.setString(5, si);
		return chord;
	}
	
	public StringInfo getFrom(String name, JRadioButton muted, JRadioButton open, JRadioButton firstFret, JRadioButton secondFret, JRadioButton thirdFret, JRadioButton fourthFret) {
		StringInfo si = new StringInfo();
		si.setName(name);
		if (muted.isSelected()) {
			si.setState(StringState.DISABLED);
		} else if (open.isSelected()) {
			si.setState(StringState.OPEN);
		} else if (firstFret.isSelected()) {
			si.setFret(1);
			si.setState(StringState.OK);
		} else if (secondFret.isSelected()) {
			si.setFret(2);
			si.setState(StringState.OK);
		} else if (thirdFret.isSelected()) {
			si.setFret(3);
			si.setState(StringState.OK);
		} else if (fourthFret.isSelected()) {
			si.setFret(4);
			si.setState(StringState.OK);
		} else {
			return null;
		}
		
		return si;
	}
/*
			
			Chords.setChannelLowE(1);
		}
		if (radioButton_6O.isSelected()) {
			setLowEstringSend(0);
		}
		if (radioButton_61.isSelected()) {
			setLowEstringSend(1);
			Chords.setChannelLowE(0);
		}
		if (radioButton_62.isSelected()) {
			setLowEstringSend(2);
			Chords.setChannelLowE(0);
		}
		if (radioButton_63.isSelected()) {
			setLowEstringSend(3);
			Chords.setChannelLowE(1);
		}
		if (radioButton_64.isSelected()) {
			setLowEstringSend(4);
			Chords.setChannelLowE(1);
		}
		if (radioButton_5M.isSelected()) {
			setAstringSend(9);
			Chords.setChannelA(3);
		}
		if (radioButton_5O.isSelected()) {
			setAstringSend(0);
		}
		if (radioButton_51.isSelected()) {
			setAstringSend(1);
			Chords.setChannelA(2);
		}
		if (radioButton_52.isSelected()) {
			setAstringSend(2);
			Chords.setChannelA(2);
		}
		if (radioButton_53.isSelected()) {
			setAstringSend(3);
			Chords.setChannelA(3);
		}
		if (radioButton_54.isSelected()) {
			setAstringSend(4);
			Chords.setChannelA(3);
		}
		if (radioButton_4M.isSelected()) {
			setDstringSend(9);
			Chords.setChannelA(5);
		}
		if (radioButton_4O.isSelected()) {
			setDstringSend(0);
		}
		if (radioButton_41.isSelected()) {
			setDstringSend(1);
			Chords.setChannelD(4);
		}
		if (radioButton_42.isSelected()) {
			setDstringSend(2);
			Chords.setChannelD(4);
		}
		if (radioButton_43.isSelected()) {
			setDstringSend(3);
			Chords.setChannelD(5);
		}
		if (radioButton_44.isSelected()) {
			setDstringSend(4);
			Chords.setChannelD(5);
		}
		if (radioButton_3M.isSelected()) {
			setGstringSend(9);
			Chords.setChannelG(7);
		}
		if (radioButton_3O.isSelected()) {
			setGstringSend(0);
		}
		if (radioButton_31.isSelected()) {
			setGstringSend(1);
			Chords.setChannelG(6);
		}
		if (radioButton_32.isSelected()) {
			setGstringSend(2);
			Chords.setChannelG(6);
		}
		if (radioButton_33.isSelected()) {
			setGstringSend(3);
			Chords.setChannelG(7);
		}
		if (radioButton_34.isSelected()) {
			setGstringSend(4);
			Chords.setChannelG(7);
		}
		if (radioButton_2M.isSelected()) {
			setBstringSend(9);
			Chords.setChannelB(9);
		}
		if (radioButton_2O.isSelected()) {
			setBstringSend(0);
		}
		if (radioButton_21.isSelected()) {
			setBstringSend(1);
			Chords.setChannelB(8);
		}
		if (radioButton_22.isSelected()) {
			setBstringSend(2);
			Chords.setChannelB(8);
		}
		if (radioButton_23.isSelected()) {
			setBstringSend(3);
			Chords.setChannelB(9);
		}
		if (radioButton_24.isSelected()) {
			setBstringSend(4);
			Chords.setChannelB(9);
		}
		if (radioButton_1M.isSelected()) {
			setHighEstringSend(9);
			Chords.setChannelHighE(11);
		}
		if (radioButton_1O.isSelected()) {
			setHighEstringSend(0);
		}
		if (radioButton_11.isSelected()) {
			setHighEstringSend(1);
			Chords.setChannelHighE(10);
		}
		if (radioButton_12.isSelected()) {
			setHighEstringSend(2);
			Chords.setChannelHighE(10);
		}
		if (radioButton_13.isSelected()) {
			setHighEstringSend(3);
			Chords.setChannelHighE(11);
		}
		if (radioButton_14.isSelected()) {
			setHighEstringSend(4);
			Chords.setChannelHighE(11);
		}
		return chordSend;
	}
		/*
		 * int[] chordSend2 = {channelSend, lowEstringSend, AstringSend,
		 * DstringSend, GstringSend, BstringSend, highEstringSend}; for (int
		 * i=0;i<chordSend.length;i++) { System.out.println(
		 * "This is ChordSend array from the Chords Page: " + chordSend[i]); }
		 */

	// called when any radio button is pressed
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

	public ButtonGroup getAstring() {
		return Astring;
	}

	public ButtonGroup getDstring() {
		return Dstring;
	}

	public ButtonGroup getGstring() {
		return Gstring;
	}

	public ButtonGroup getBstring() {
		return Bstring;
	}

	public ButtonGroup getHighEstring() {
		return highEstring;
	}

	public void setAstring(ButtonGroup astring) {
		Astring = astring;
	}

	public void setDstring(ButtonGroup dstring) {
		Dstring = dstring;
	}

	public void setGstring(ButtonGroup gstring) {
		Gstring = gstring;
	}

	public void setBstring(ButtonGroup bstring) {
		Bstring = bstring;
	}

	public void setHighEstring(ButtonGroup highEstring) {
		this.highEstring = highEstring;
	}
	
	public void setChordName(String name) {
		chordName.setText(name);
		chordName.selectAll();
		chordName.requestFocusInWindow();
	}
	
	public String getChordName() {
		return chordName.getText();
	}
}
