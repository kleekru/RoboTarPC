package com.robotar.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import cz.versarius.xchords.Chord;
import cz.versarius.xchords.StringInfo;
import cz.versarius.xchords.StringState;

public class ChordRadioPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 18765864433L;
	
	// button groups for radio buttons
	private ButtonGroup[] btnGrps = new ButtonGroup[6];
	
	// radio buttons
	private JRadioButton [][] radioButtons = new JRadioButton[6][6];
	
	public ChordRadioPanel() {
		super();
		setBackground(Const.BACKGROUND_COLOR);
		setAlignmentY(Component.BOTTOM_ALIGNMENT);
		setAlignmentX(Component.LEFT_ALIGNMENT);
		setOpaque(true);
		//activeSong.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagLayout gbl_radioPanel = new GridBagLayout();
		int rh = 24;
		int cw = 30;
		gbl_radioPanel.columnWidths = new int[] { cw, cw, cw, cw, cw, cw };
		gbl_radioPanel.rowHeights = new int[] { rh, rh, rh, rh, rh, rh };
		gbl_radioPanel.columnWeights = new double[]{ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		gbl_radioPanel.rowWeights = new double[]{ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		setLayout(gbl_radioPanel);
		setMinimumSize(new Dimension(6*cw, 6*rh));
		setPreferredSize(new Dimension(6*cw, 6*rh));
		// Group the radio buttons.
		for (int i = 0; i < 6; i++) {
			btnGrps[i] = new ButtonGroup();
		}
		
		// radio buttons
		Color bg = new Color(205, 133, 63);
		GridBagConstraints gbc_radio = new GridBagConstraints();
		gbc_radio.insets = new Insets(0, 0, 0, 2);
		gbc_radio.gridy = 0;
		//gbc_radio.fill = GridBagConstraints.BOTH;
		gbc_radio.anchor = GridBagConstraints.CENTER;
		for (int row = 0; row < 6; row++) {
			//radioButtons[row] = new JRadioButton[6];
			gbc_radio.gridx = 0;
			for (int s = 0; s < 6; s++) {
				JRadioButton radio = new JRadioButton("");
				if (row == 0) {
					radio.setBackground(Color.BLACK);
				} else if (row == 1) {
					radio.setBackground(Color.WHITE);
				} else {
					radio.setBackground(bg);
				}
				radio.setOpaque(true);
				radio.addActionListener(this);
				//radio.setSize(20, 20);
				add(radio, gbc_radio);
				System.out.println(radio.getSize());
				System.out.println(radio.getBackground());
				radioButtons[row][s] = radio;
				btnGrps[s].add(radio);
				gbc_radio.gridx++;
			}
			gbc_radio.gridy++;
		}
		

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

	}

	/*
	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
 
                GradientPaint gp = new GradientPaint(0, 0,
                        getBackground().brighter().brighter(), 0, getHeight(),
                        getBackground().darker().darker());
 
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());

		super.paintComponent(g);
	}*/
	public void clear() {
		for (int i = 0; i < 6; i++) {
			btnGrps[i].clearSelection();
		}
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
		btnGrps[i].clearSelection();
	}

	private void muteString(int i) {
		radioButtons[0][i].setSelected(true);
	}

	private void openString(int i) {
		radioButtons[1][i].setSelected(true);
	}

	private void setString(int i, int fret) {
		radioButtons[fret+1][i].setSelected(true);
	}

	public Chord createChordFromRadios(String library, String chordName) {
		Chord chord = new Chord();
		chord.setName(chordName);
		chord.setId(library, chord.getName());
		StringInfo si = getFrom("e6", 0); //radioButton_6M, radioButton_6O, radioButton_61, radioButton_62, radioButton_63, radioButton_64);
		chord.setString(0, si);
		si = getFrom("a", 1); //radioButton_5M, radioButton_5O, radioButton_51, radioButton_52, radioButton_53, radioButton_54);
		chord.setString(1, si);
		si = getFrom("d", 2); //radioButton_4M, radioButton_4O, radioButton_41, radioButton_42, radioButton_43, radioButton_44);
		chord.setString(2, si);
		si = getFrom("g", 3); //radioButton_3M, radioButton_3O, radioButton_31, radioButton_32, radioButton_33, radioButton_34);
		chord.setString(3, si);
		si = getFrom("b", 4); //radioButton_2M, radioButton_2O, radioButton_21, radioButton_22, radioButton_23, radioButton_24);
		chord.setString(4, si);
		si = getFrom("e1", 5);//radioButton_1M, radioButton_1O, radioButton_11, radioButton_12, radioButton_13, radioButton_14);
		chord.setString(5, si);
		return chord;
	}
	
	public StringInfo getFrom(String name, int idx) {
		StringInfo si = new StringInfo();
		si.setName(name);
		if (radioButtons[0][idx].isSelected()) {
			si.setState(StringState.DISABLED);
		} else if (radioButtons[1][idx].isSelected()) {
			si.setState(StringState.OPEN);
		} else {
			for (int i = 1; i < 5; i++) {
				if (radioButtons[i+1][idx].isSelected()) {
					si.setFret(i);
					si.setState(StringState.OK);
					break;
				}
			}
		}
		return si;
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
		// TODO this should be done in RoboTarChordsPage!! refactor
		RoboTarChordsPage chordsPage = (RoboTarChordsPage)getTopLevelAncestor();
		Chord chord = createChordFromRadios(chordsPage.getLibraryName(false), chordsPage.getChordName());
		chordsPage.showChordImage(chord);
		/*RoboTarChordsPage chordsPage = (RoboTarChordsPage)getTopLevelAncestor();
		chordsPage.prepareServoValues();*/
	}

		
}
