package ioio.RoboTar.PCconsole;


import java.awt.EventQueue;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import javax.swing.JRadioButton;
import javax.swing.JLabel;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Font;
import java.awt.Dimension;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import java.awt.Insets;
import javax.swing.JList;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.Rectangle;
import javax.swing.ListSelectionModel;
import javax.swing.AbstractListModel;
import javax.swing.JToggleButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoboTarChordsPage extends JFrame implements ActionListener,
		ListSelectionListener {
	static final Logger LOG = LoggerFactory.getLogger(RoboTarChordsPage.class);

	private static final long serialVersionUID = 1L;
	
	private JPanel frmBlueAhuizoteChords;
	
	private JButton btnNewChord;
	
	// button groups for radio buttons
	private ButtonGroup lowEstring;
	private ButtonGroup Astring;
	private ButtonGroup Dstring;
	private ButtonGroup Gstring;
	private ButtonGroup Bstring;
	private ButtonGroup highEstring;
	
	private String chordNameSend;
	private int channelSend;
	
	protected int lowEstringSend;
	protected int AstringSend;
	protected int DstringSend;
	protected int GstringSend;
	protected int BstringSend;
	protected int highEstringSend;
	protected static int[] chordSend;
	
	private JTextField chordName;
	private ListModel chordList;
	private JList listChords;
	private JLabel lblChordPicture;
	private ResourceBundle messages;
	
	// public static String ROOT_DIR = "C:/AndroidWorkspace";
	public static String ROOT_DIR = "d:/projects/kleekru/";

	/**
	 * Launch the application.
	 */
	public static void StartChordsPage() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RoboTarChordsPage frame = new RoboTarChordsPage(chordSend);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * 
	 * @param chordReceived
	 */
	public RoboTarChordsPage(int[] chordReceived) {
		messages = ResourceBundle.getBundle("ioio.RoboTar.PCconsole.RoboTarBundle", Locale.ENGLISH);
		
		setVisible(true);
		setTitle(messages.getString("robotar.chords.title"));
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				RoboTarChordsPage.class
						.getResource("/data/BlueAhuizoteIcon.png")));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 800, 400);

		frmBlueAhuizoteChords = new JPanel();
		frmBlueAhuizoteChords.setAlignmentY(Component.TOP_ALIGNMENT);
		frmBlueAhuizoteChords.setAlignmentX(Component.LEFT_ALIGNMENT);
		frmBlueAhuizoteChords.setBackground(Color.BLUE);
		frmBlueAhuizoteChords.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(frmBlueAhuizoteChords);
		frmBlueAhuizoteChords.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("max(62dlu;pref)"),
				ColumnSpec.decode("center:pref:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(50dlu;min):grow"), }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(49dlu;default)"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(76dlu;default)"),
				FormFactory.LINE_GAP_ROWSPEC, RowSpec.decode("92px"), }));

		JLabel activeSongLbl = new JLabel(messages.getString("robotar.chords.active_song"));
		activeSongLbl.setForeground(Color.WHITE);
		activeSongLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
		frmBlueAhuizoteChords.add(activeSongLbl, "3, 2");

		JLabel activeSong = new JLabel(messages.getString("robotar.chords.no_song_selected"));
		activeSong.setForeground(Color.WHITE);
		frmBlueAhuizoteChords.add(activeSong, "5, 2");
		activeSong.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null,
				null, null));
		activeSong.setBackground(Color.BLACK);
		activeSong.setVerticalAlignment(SwingConstants.TOP);
		activeSong.setFont(new Font("Segoe UI", Font.BOLD, 14));

		// new chord button
		btnNewChord = new JButton("");
		btnNewChord.setPressedIcon(new ImageIcon(RoboTarChordsPage.class
				.getResource("/data/NewChordPressed.png")));
		frmBlueAhuizoteChords.add(btnNewChord, "2, 4");
		btnNewChord.setMinimumSize(new Dimension(20, 9));
		btnNewChord.setBackground(new Color(0, 0, 128));
		btnNewChord.setMaximumSize(new Dimension(20, 9));
		btnNewChord.setMargin(new Insets(2, 1, 2, 1));
		btnNewChord.setIcon(new ImageIcon(RoboTarChordsPage.class
				.getResource("/data/NewChord.png")));
		btnNewChord.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				newChordButtonActionPerformed(evt);
			}

			private void newChordButtonActionPerformed(ActionEvent evt) {
				lowEstring.clearSelection();
				Astring.clearSelection();
				Dstring.clearSelection();
				Gstring.clearSelection();
				Bstring.clearSelection();
				highEstring.clearSelection();
				chordName.setText(messages.getString("robotar.chords.enter_chord_name"));
				chordName.selectAll();
				chordName.requestFocusInWindow();
				clearSelection();
			}
		});

		// test chord button
		JToggleButton tglbtnTestChord = new JToggleButton("");
		tglbtnTestChord.setPressedIcon(new ImageIcon(RoboTarChordsPage.class
				.getResource("/data/TestChordOn.png")));
		frmBlueAhuizoteChords.add(tglbtnTestChord, "3, 4");
		tglbtnTestChord.setSelectedIcon(new ImageIcon(RoboTarChordsPage.class
				.getResource("/data/TestChordOn.png")));
		tglbtnTestChord.setIcon(new ImageIcon(RoboTarChordsPage.class
				.getResource("/data/TestChordOff.png")));
		tglbtnTestChord.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				tglbtnTestChordActionPerformed(evt);
			}

			private void tglbtnTestChordActionPerformed(ActionEvent evt) {
				try {
					int[] chord = Chords.Chords(lowEstringSend, AstringSend,
							DstringSend, GstringSend, BstringSend,
							highEstringSend);
					if (isGridFilled(chord)) {
						LOG.debug("Chord values from Chords Constructor: ");
						for (int i = 0; i < chord.length; i++) {
							LOG.debug("{}. - {}", i, chord[i]);
						}
						logValues("Low E", Chords.getChannelLowE(), Chords.getLowEstringPosition());
						logValues("A", Chords.getChannelA(), Chords.getAStringPosition());
						logValues("D", Chords.getChannelD(), Chords.getDStringPosition());
						logValues("G", Chords.getChannelG(), Chords.getGStringPosition());
						logValues("B", Chords.getChannelB(), Chords.getBStringPosition());
						logValues("High E", Chords.getChannelHighE(), Chords.getHighEStringPosition());
					} else {
						JOptionPane.showMessageDialog(RoboTarChordsPage.this, messages.getString("robotar.chords.fill_chord_grid"));
					}
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}

			}
			private void logValues(String stringName, int channel, float position) {
				LOG.debug("{} channel: {}, value: {}", stringName, channel, position);
			}
			// TODO this is not working now - Chords.Chords returns array of 0's
			private boolean isGridFilled(int[] chord) {
				boolean result = true;
				for (int i = 0; i<chord.length; i++) {
					result &= (chord[i]>0);
				}
				return result;
			}
		});

		// add chord to song button
		JButton btnAddToSong = new JButton("");
		frmBlueAhuizoteChords.add(btnAddToSong, "5, 4");
		btnAddToSong.setMinimumSize(new Dimension(20, 9));
		btnAddToSong.setMaximumSize(new Dimension(20, 9));
		btnAddToSong.setIcon(new ImageIcon(RoboTarChordsPage.class
				.getResource("/data/ArrowUp.png")));
		btnAddToSong.setFont(new Font("Segoe UI", Font.BOLD, 13));
		btnAddToSong.setToolTipText(messages.getString("robotar.chords.add_chord_to_song.tooltip"));

		/*
		 * JTextField textField = new JTextField("something");
		 * frmBlueAhuizoteChords.add(textField, "3, 6, fill, default");
		 * textField.setColumns(10);
		 */

		JPanel panel = new JPanel();
		frmBlueAhuizoteChords.add(panel, "5, 6");
		panel.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		panel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		panel.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("5px"), ColumnSpec.decode("5px"),
				ColumnSpec.decode("101px"), ColumnSpec.decode("75px"),
				FormFactory.DEFAULT_COLSPEC, ColumnSpec.decode("3px"),
				FormFactory.DEFAULT_COLSPEC, ColumnSpec.decode("4px"),
				FormFactory.DEFAULT_COLSPEC, ColumnSpec.decode("3px"),
				FormFactory.DEFAULT_COLSPEC, ColumnSpec.decode("4px"),
				FormFactory.DEFAULT_COLSPEC, ColumnSpec.decode("5px"),
				FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC,
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
		JLabel lblClickToMute = new JLabel(messages.getString("robotar.chords.mute_string"));
		panel.add(lblClickToMute, "3, 1");
		lblClickToMute.setFont(new Font("Segoe UI", Font.BOLD, 16));

		final JRadioButton radioButton_6M = new JRadioButton("");
		radioButton_6M.setBackground(Color.BLACK);
		panel.add(radioButton_6M, "5, 1, left, center");
		lowEstring.add(radioButton_6M);
		radioButton_6M.addActionListener(this);

		final JRadioButton radioButton_5M = new JRadioButton("");
		radioButton_5M.setBackground(Color.BLACK);
		panel.add(radioButton_5M, "7, 1, left, center");
		Astring.add(radioButton_5M);
		radioButton_5M.addActionListener(this);

		final JRadioButton radioButton_4M = new JRadioButton("");
		radioButton_4M.setBackground(Color.BLACK);
		panel.add(radioButton_4M, "9, 1, left, center");
		Dstring.add(radioButton_4M);
		radioButton_4M.addActionListener(this);

		final JRadioButton radioButton_3M = new JRadioButton("");
		radioButton_3M.setBackground(Color.BLACK);
		panel.add(radioButton_3M, "11, 1, left, center");
		Gstring.add(radioButton_3M);
		radioButton_3M.addActionListener(this);

		final JRadioButton radioButton_2M = new JRadioButton("");
		radioButton_2M.setBackground(Color.BLACK);
		panel.add(radioButton_2M, "13, 1, left, center");
		Bstring.add(radioButton_2M);
		radioButton_2M.addActionListener(this);

		final JRadioButton radioButton_1M = new JRadioButton("");
		radioButton_1M.setBackground(Color.BLACK);
		panel.add(radioButton_1M, "15, 1, left, center");
		highEstring.add(radioButton_1M);
		radioButton_1M.addActionListener(this);

		// open radio buttons
		JLabel lblOpenString = new JLabel(messages.getString("robotar.chords.open_string"));
		lblOpenString.setFont(new Font("Segoe UI", Font.BOLD, 16));
		panel.add(lblOpenString, "3, 2, left, center");

		final JRadioButton radioButton_6O = new JRadioButton("");
		radioButton_6O.setBackground(Color.WHITE);
		panel.add(radioButton_6O, "5, 2, left, center");
		lowEstring.add(radioButton_6O);
		radioButton_6O.addActionListener(this);

		final JRadioButton radioButton_5O = new JRadioButton("");
		radioButton_5O.setBackground(Color.WHITE);
		panel.add(radioButton_5O, "7, 2, left, center");
		Astring.add(radioButton_5O);
		radioButton_5O.addActionListener(this);

		final JRadioButton radioButton_4O = new JRadioButton("");
		radioButton_4O.setBackground(Color.WHITE);
		radioButton_4O.setActionCommand("new Radio Button");
		panel.add(radioButton_4O, "9, 2, left, center");
		Dstring.add(radioButton_4O);
		radioButton_4O.addActionListener(this);

		final JRadioButton radioButton_3O = new JRadioButton("");
		radioButton_3O.setBackground(Color.WHITE);
		panel.add(radioButton_3O, "11, 2, left, center");
		Gstring.add(radioButton_3O);
		radioButton_3O.addActionListener(this);

		final JRadioButton radioButton_2O = new JRadioButton("");
		radioButton_2O.setBackground(Color.WHITE);
		panel.add(radioButton_2O, "13, 2, left, center");
		Bstring.add(radioButton_2O);
		radioButton_2O.addActionListener(this);

		final JRadioButton radioButton_1O = new JRadioButton("");
		radioButton_1O.setBackground(Color.WHITE);
		panel.add(radioButton_1O, "15, 2, left, center");
		highEstring.add(radioButton_1O);
		radioButton_1O.addActionListener(this);

		JLabel lblChordName = new JLabel(messages.getString("robotar.chords.chord_name"));
		panel.add(lblChordName, "17, 2, center, default");
		lblChordName.setForeground(Color.BLACK);
		lblChordName.setFont(new Font("Segoe UI", Font.BOLD, 16));

		JLabel lblstFret = new JLabel(messages.getString("robotar.chords.first_fret"));
		lblstFret.setFont(new Font("Segoe UI", Font.BOLD, 16));
		panel.add(lblstFret, "3, 3, left, center");

		final JRadioButton radioButton_61 = new JRadioButton("");
		radioButton_61.setBackground(new Color(205, 133, 63));
		panel.add(radioButton_61, "5, 3, left, center");
		lowEstring.add(radioButton_61);
		radioButton_61.addActionListener(this);

		final JRadioButton radioButton_51 = new JRadioButton("");
		radioButton_51.setBackground(new Color(205, 133, 63));
		panel.add(radioButton_51, "7, 3, left, center");
		Astring.add(radioButton_51);
		radioButton_51.addActionListener(this);

		final JRadioButton radioButton_41 = new JRadioButton("");
		radioButton_41.setBackground(new Color(205, 133, 63));
		panel.add(radioButton_41, "9, 3, left, center");
		Dstring.add(radioButton_41);
		radioButton_41.addActionListener(this);

		final JRadioButton radioButton_31 = new JRadioButton("");
		radioButton_31.setBackground(new Color(205, 133, 63));
		panel.add(radioButton_31, "11, 3, left, center");
		Gstring.add(radioButton_31);
		radioButton_31.addActionListener(this);

		final JRadioButton radioButton_21 = new JRadioButton("");
		radioButton_21.setBackground(new Color(205, 133, 63));
		panel.add(radioButton_21, "13, 3, left, center");
		Bstring.add(radioButton_21);
		radioButton_21.addActionListener(this);

		final JRadioButton radioButton_11 = new JRadioButton("");
		radioButton_11.setBackground(new Color(205, 133, 63));
		panel.add(radioButton_11, "15, 3, left, center");
		highEstring.add(radioButton_11);
		radioButton_11.addActionListener(this);

		chordName = new JTextField();
		panel.add(chordName, "17, 3, center, default");
		chordName.setColumns(10);
		chordName.setText(messages.getString("robotar.chords.enter_chord_name"));

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

		JLabel lblndFret = new JLabel(messages.getString("robotar.chords.second_fret"));
		lblndFret.setFont(new Font("Segoe UI", Font.BOLD, 16));
		panel.add(lblndFret, "3, 4, left, center");

		final JRadioButton radioButton_62 = new JRadioButton("");
		radioButton_62.setBackground(new Color(205, 133, 63));
		panel.add(radioButton_62, "5, 4, left, center");
		lowEstring.add(radioButton_62);
		radioButton_62.addActionListener(this);

		final JRadioButton radioButton_52 = new JRadioButton("");
		radioButton_52.setBackground(new Color(205, 133, 63));
		panel.add(radioButton_52, "7, 4, left, center");
		Astring.add(radioButton_52);
		radioButton_52.addActionListener(this);

		final JRadioButton radioButton_42 = new JRadioButton("");
		radioButton_42.setBackground(new Color(205, 133, 63));
		panel.add(radioButton_42, "9, 4, left, center");
		Dstring.add(radioButton_42);
		radioButton_42.addActionListener(this);

		final JRadioButton radioButton_32 = new JRadioButton("");
		radioButton_32.setBackground(new Color(205, 133, 63));
		panel.add(radioButton_32, "11, 4, left, center");
		Gstring.add(radioButton_32);
		radioButton_32.addActionListener(this);

		final JRadioButton radioButton_22 = new JRadioButton("");
		radioButton_22.setBackground(new Color(205, 133, 63));
		panel.add(radioButton_22, "13, 4, left, center");
		Bstring.add(radioButton_22);
		radioButton_22.addActionListener(this);

		final JRadioButton radioButton_12 = new JRadioButton("");
		radioButton_12.setBackground(new Color(205, 133, 63));
		panel.add(radioButton_12, "15, 4, left, center");
		highEstring.add(radioButton_12);
		radioButton_12.addActionListener(this);

		JLabel lblrdFret = new JLabel(messages.getString("robotar.chords.third_fret"));
		lblrdFret.setFont(new Font("Segoe UI", Font.BOLD, 16));
		panel.add(lblrdFret, "3, 5, left, center");

		final JRadioButton radioButton_63 = new JRadioButton("");
		radioButton_63.setBackground(new Color(205, 133, 63));
		panel.add(radioButton_63, "5, 5, left, center");
		lowEstring.add(radioButton_63);
		radioButton_63.addActionListener(this);

		final JRadioButton radioButton_53 = new JRadioButton("");
		radioButton_53.setBackground(new Color(205, 133, 63));
		panel.add(radioButton_53, "7, 5, left, center");
		Astring.add(radioButton_53);
		radioButton_53.addActionListener(this);

		final JRadioButton radioButton_43 = new JRadioButton("");
		Dstring.add(radioButton_43);
		radioButton_43.setBackground(new Color(205, 133, 63));
		panel.add(radioButton_43, "9, 5");

		final JRadioButton radioButton_33 = new JRadioButton("");
		Gstring.add(radioButton_33);
		radioButton_33.setBackground(new Color(205, 133, 63));
		panel.add(radioButton_33, "11, 5");

		final JRadioButton radioButton_23 = new JRadioButton("");
		Bstring.add(radioButton_23);
		radioButton_23.setBackground(new Color(205, 133, 63));
		panel.add(radioButton_23, "13, 5");

		final JRadioButton radioButton_13 = new JRadioButton("");
		highEstring.add(radioButton_13);
		radioButton_13.setBackground(new Color(205, 133, 63));
		panel.add(radioButton_13, "15, 5");

		JLabel lblthFret = new JLabel(messages.getString("robotar.chords.fourth_fret"));
		lblthFret.setFont(new Font("Segoe UI", Font.BOLD, 16));
		panel.add(lblthFret, "3, 6, left, center");

		final JRadioButton radioButton_64 = new JRadioButton("");
		radioButton_64.setBackground(new Color(205, 133, 63));
		panel.add(radioButton_64, "5, 6, left, center");
		lowEstring.add(radioButton_64);
		radioButton_64.addActionListener(this);

		final JRadioButton radioButton_54 = new JRadioButton("");
		radioButton_54.setBackground(new Color(205, 133, 63));
		panel.add(radioButton_54, "7, 6, left, center");
		Astring.add(radioButton_54);
		radioButton_54.addActionListener(this);

		final JRadioButton radioButton_44 = new JRadioButton("");
		radioButton_44.setBackground(new Color(205, 133, 63));
		panel.add(radioButton_44, "9, 6, left, center");
		Dstring.add(radioButton_44);
		radioButton_44.addActionListener(this);

		final JRadioButton radioButton_34 = new JRadioButton("");
		radioButton_34.setBackground(new Color(205, 133, 63));
		panel.add(radioButton_34, "11, 6, left, center");
		Gstring.add(radioButton_34);
		radioButton_34.addActionListener(this);

		final JRadioButton radioButton_24 = new JRadioButton("");
		radioButton_24.setBackground(new Color(205, 133, 63));
		panel.add(radioButton_24, "13, 6, left, center");
		Bstring.add(radioButton_24);
		radioButton_24.addActionListener(this);

		final JRadioButton radioButton_14 = new JRadioButton("");
		radioButton_14.setBackground(new Color(205, 133, 63));
		panel.add(radioButton_14, "15, 6, left, center");
		highEstring.add(radioButton_14);
		radioButton_14.addActionListener(this);

		// add chord to list button
		JButton btnAddToChordList = new JButton("");
		frmBlueAhuizoteChords.add(btnAddToChordList, "5, 8, default, center");
		btnAddToChordList.setIcon(new ImageIcon(RoboTarChordsPage.class
				.getResource("/data/ArrowDown.png")));
		btnAddToChordList.setActionCommand("");
		btnAddToChordList
				.setToolTipText(messages.getString("robotar.chords.add_chord_to_list.tooltip"));
		btnAddToChordList.setFont(new Font("Segoe UI", Font.BOLD, 13));
		btnAddToChordList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				addToChordListActionPerformed(evt);
			}

			private void addToChordListActionPerformed(ActionEvent evt) {
				chordNameSend = chordName.getText();
				if (!chordNameSend.isEmpty()) {
					XMLChordLoader.chordloader(chordNameSend, lowEstringSend,
						AstringSend, DstringSend, GstringSend, BstringSend,
						highEstringSend);
					chordName.setText(null);
					clearSelection();
				} else {
					JOptionPane.showMessageDialog(RoboTarChordsPage.this, messages.getString("robotar.chords.fill_chord_name"));
				}

			}
		});

		// TODO Need to update so this field shows a chord picture - use XChords
		lblChordPicture = new JLabel(messages.getString("robotar.chords.no_chord_selected"));
		lblChordPicture.setForeground(Color.WHITE);
		lblChordPicture
				.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 10));
		lblChordPicture.setBounds(new Rectangle(0, 0, 5, 5));
		lblChordPicture.setPreferredSize(new Dimension(100, 100));
		lblChordPicture.setMaximumSize(new Dimension(200, 200));
		lblChordPicture.setBorder(new LineBorder(Color.BLUE, 3));
		frmBlueAhuizoteChords.add(lblChordPicture, "2, 8, left, center");

		// TODO Change this section to populate the list based on XML file load.
		JScrollPane scrollPane = new JScrollPane();
		frmBlueAhuizoteChords.add(scrollPane, "3, 8, fill, fill");
		listChords = new JList();
		scrollPane.setViewportView(listChords);
		listChords.setFont(new Font("Tahoma", Font.BOLD, 11));
		listChords.setModel(new AbstractListModel() {
			String[] values = new String[] { "Select Chord", "C", "Cm", "C7",
					"D", "Dm", "D7", "E", "Em", "E7", "F", "Fm", "F7", "G",
					"Gm", "G7", "A", "Am", "A7", "B", "Bm", "B7" };

			public int getSize() {
				return values.length;
			}

			public Object getElementAt(int index) {
				return values[index];
			}
		});

		// TODO Change this section so that radio buttons are selected based on
		// the chord that is loaded
		/*
		 * XML must load chord not addresses and translate them to the radio
		 * buttons Need to also optimize the IOIO for PC console class so that
		 * it reads directly from the XML chord note addresses rather than the
		 * radio buttons (assume this will be better for performance). If not,
		 * ok to leave as is and drive all chords from radio button select
		 * values?? That would mean each chord would need to be read, translated
		 * to radio buttons and then translated to channels and positions.
		 */
		listChords.setVisibleRowCount(4);
		listChords.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listChords.setBorder(new LineBorder(Color.BLUE, 3, true));
		listChords.addListSelectionListener(new ListSelectionListener() {
			private void showChordImage(String chord) {
				String imageName = ROOT_DIR
						+ "RoboTarIOIOforPCConsole/src/data/" + chord
						+ "Chord.png";
				try {
					lblChordPicture.setIcon(new ImageIcon(ImageIO
							.read(new File(imageName))));
					lblChordPicture.setText(chord);
				} catch (IOException e) {
					LOG.error("file not found: {}", imageName);
					lblChordPicture.setText(messages.getString("robotar.chords.image_not_found"));
					lblChordPicture.setIcon(null);
					LOG.debug("stacktrace", e);
				}
			}

			@Override
			public void valueChanged(ListSelectionEvent listselect) {
				chordsListSelectionPerformed(listselect);
			}

			private void chordsListSelectionPerformed(
					ListSelectionEvent listselect) {
				int newChord = (int) listChords.getSelectedIndex();
				switch (newChord) {
				case 1:
					radioButton_6M.setSelected(true);
					radioButton_53.setSelected(true);
					radioButton_42.setSelected(true);
					radioButton_3O.setSelected(true);
					radioButton_21.setSelected(true);
					radioButton_1O.setSelected(true);
					String name = (String) listChords.getSelectedValue();
					chordName.setText(name);
					getChordNotes(chordSend);
					showChordImage(name);
					break;
				case 2:
					radioButton_6M.setSelected(true);
					radioButton_53.setSelected(true);
					radioButton_41.setSelected(true);
					radioButton_3O.setSelected(true);
					radioButton_21.setSelected(true);
					radioButton_13.setSelected(true);
					name = (String) listChords.getSelectedValue();
					chordName.setText(name);
					getChordNotes(chordSend);
					showChordImage(name);
					break;
				case 3:
					radioButton_6M.setSelected(true);
					radioButton_53.setSelected(true);
					radioButton_42.setSelected(true);
					radioButton_33.setSelected(true);
					radioButton_21.setSelected(true);
					radioButton_1O.setSelected(true);
					name = (String) listChords.getSelectedValue();
					chordName.setText(name);
					getChordNotes(chordSend);
					showChordImage(name);
					break;
				case 4:
					radioButton_6M.setSelected(true);
					radioButton_5O.setSelected(true);
					radioButton_4O.setSelected(true);
					radioButton_32.setSelected(true);
					radioButton_23.setSelected(true);
					radioButton_12.setSelected(true);
					name = (String) listChords.getSelectedValue();
					chordName.setText(name);
					getChordNotes(chordSend);
					showChordImage(name); // yes, it doesn't exist
					break;
				default:
					lowEstring.clearSelection();
					Astring.clearSelection();
					Dstring.clearSelection();
					Gstring.clearSelection();
					Bstring.clearSelection();
					highEstring.clearSelection();
					// chordName.setText(null);
					getChordNotes(chordSend);
					clearSelection();
				break;
				}
			}

			public int[] getChordNotes(int[] chordSend) {
				if (radioButton_6M.isSelected()) {
					setLowEstringSend(9);
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
				/*
				 * int[] chordSend2 = {channelSend, lowEstringSend, AstringSend,
				 * DstringSend, GstringSend, BstringSend, highEstringSend}; for
				 * (int i=0;i<chordSend.length;i++) { System.out.println(
				 * "This is ChordSend array from the Chords Page: " +
				 * chordSend[i]); }
				 */
				return chordSend;
			}

		});

		// String[] chordList = null;

		/* set size of the frame */
		setSize(800, 409);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	}

	private void clearSelection() {
		listChords.setSelectedIndex(0);
		lblChordPicture.setText(messages.getString("robotar.chords.no_chord_selected"));
		lblChordPicture.setIcon(null);
	}
	
	public JPanel getFrmBlueAhuizoteChords() {
		return frmBlueAhuizoteChords;
	}

	public void setFrmBlueAhuizoteChords(JPanel frmBlueAhuizoteChords) {
		this.frmBlueAhuizoteChords = frmBlueAhuizoteChords;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnNewChord) {
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		// TODO Auto-generated method stub

	}

	public String getChordNameSend() {
		return chordNameSend;
	}

	public int getLowEstringSend() {
		return lowEstringSend;
	}

	public int getAstringSend() {
		return AstringSend;
	}

	public int getDstringSend() {
		return DstringSend;
	}

	public int getGstringSend() {
		return GstringSend;
	}

	public int getBstringSend() {
		return BstringSend;
	}

	public int getHighEstringSend() {
		return highEstringSend;
	}

	public static int[] getChordSend() {
		return chordSend;
	}

	public void setChordNameSend(String chordNameSend) {
		this.chordNameSend = chordNameSend;
	}

	public void setLowEstringSend(int lowEstringSend) {
		this.lowEstringSend = lowEstringSend;
	}

	public void setAstringSend(int astringSend) {
		this.AstringSend = astringSend;
	}

	public void setDstringSend(int dstringSend) {
		this.DstringSend = dstringSend;
	}

	public void setGstringSend(int gstringSend) {
		this.GstringSend = gstringSend;
	}

	public void setBstringSend(int bstringSend) {
		this.BstringSend = bstringSend;
	}

	public void setHighEstringSend(int highEstringSend) {
		this.highEstringSend = highEstringSend;
	}

	public static void setChordSend(int[] chordSend) {
		RoboTarChordsPage.chordSend = chordSend;
	}

	public JTextField getChordName() {
		return chordName;
	}

	public JButton getBtnNewChord() {
		return btnNewChord;
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

	public int getChannelSend() {
		return channelSend;
	}

	public void setChordName(JTextField chordName) {
		this.chordName = chordName;
	}

	public void setBtnNewChord(JButton btnNewChord) {
		this.btnNewChord = btnNewChord;
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

	public void setChannelSend(int channelSend) {
		this.channelSend = channelSend;
	}

	public ListModel getChordList() {
		return chordList;
	}

	public void setChordList(ListModel chordList) {
		this.chordList = chordList;
	}

}
