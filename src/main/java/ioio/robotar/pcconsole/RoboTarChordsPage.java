package ioio.robotar.pcconsole;


import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import java.awt.Color;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

import cz.versarius.xchords.Chord;

import javax.swing.JLabel;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.Font;
import java.awt.Dimension;

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
import javax.swing.JToggleButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoboTarChordsPage extends JFrame implements ActionListener,
		ListSelectionListener, WindowListener {
	private static final long serialVersionUID = -4977090038183485379L;

	static final Logger LOG = LoggerFactory.getLogger(RoboTarChordsPage.class);

	private JPanel frmBlueAhuizoteChords;
	
	private JButton btnNewChord;
	
	private String chordNameSend;
	
	private DefaultListModel chordList;
	private JList listChords;
	private JLabel lblChordPicture;
	
	/** reference to mainframe and chordmanager */
	private RoboTarStartPage mainFrame;
	private ResourceBundle messages;
	
	private ChordRadioPanel radioPanel;

	private JButton btnAddToSong;

	private JButton btnAddToChordList;

	private JLabel activeSong;

	/**
	 * Create the frame.
	 * 
	 * @param chordReceived
	 */
	public RoboTarChordsPage(RoboTarStartPage mainFrame) {
		this.setMainFrame(mainFrame);
		messages = mainFrame.getMessages();
		
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
				FormFactory.LINE_GAP_ROWSPEC, RowSpec.decode("92px")}));

		JLabel activeSongLbl = new JLabel(messages.getString("robotar.chords.active_song"));
		activeSongLbl.setForeground(Color.WHITE);
		activeSongLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
		frmBlueAhuizoteChords.add(activeSongLbl, "3, 2");

		activeSong = new JLabel(messages.getString("robotar.chords.no_song_selected"));
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
				radioPanel.clear();
				radioPanel.setChordName(messages.getString("robotar.chords.enter_chord_name"));
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
				prepareChord();
			}

		});

		// add to song button
		btnAddToSong = new JButton("");
		frmBlueAhuizoteChords.add(btnAddToSong, "5, 4");
		btnAddToSong.setMinimumSize(new Dimension(20, 9));
		btnAddToSong.setMaximumSize(new Dimension(20, 9));
		btnAddToSong.setIcon(new ImageIcon(RoboTarChordsPage.class
				.getResource("/data/ArrowUp.png")));
		btnAddToSong.setFont(new Font("Segoe UI", Font.BOLD, 13));
		btnAddToSong.setToolTipText(messages.getString("robotar.chords.add_chord_to_song.tooltip"));
		btnAddToSong.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				addToSongActionPerformed(evt);
			}
		});
		
		radioPanel = new ChordRadioPanel(messages);
		frmBlueAhuizoteChords.add(radioPanel, "5, 6");
		
		btnAddToChordList = new JButton("");
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
				chordNameSend = radioPanel.getChordName();
				if (isValidChordName(chordNameSend)) {
					LOG.info("add chord to chord list... TODO");
					Chord chord = radioPanel.createChordFromRadios(getLibraryName());
					chordList.addElement(chord);
					radioPanel.setChordName(null);
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
		lblChordPicture.setBounds(new Rectangle(0, 0, 0, 0));
		lblChordPicture.setPreferredSize(new Dimension(132, 138));
		lblChordPicture.setMaximumSize(new Dimension(132, 138));
		//lblChordPicture.setBorder(new LineBorder(Color.RED, 3));
		frmBlueAhuizoteChords.add(lblChordPicture, "3, 6, 1, 1, center, center");

		// populate the list based on XML file load.
		XMLChordLoader3 loader = new XMLChordLoader3();
		List<Chord> chords = loader.load(RoboTarChordsPage.class.getResourceAsStream("/default-chords/robotar-default.xml"));
		chordList = new DefaultListModel();
		for (Chord chord : chords) {
			chordList.addElement(chord);
		}
		JScrollPane scrollPane = new JScrollPane();
		frmBlueAhuizoteChords.add(scrollPane, "3, 8, fill, fill");
		listChords = new JList(chordList);
		scrollPane.setViewportView(listChords);
		listChords.setFont(new Font("Tahoma", Font.BOLD, 11));

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
		listChords.setCellRenderer(new ChordListCellRenderer());
		listChords.setVisibleRowCount(4);
		listChords.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listChords.setBorder(new LineBorder(Color.BLUE, 3, true));
		listChords.addListSelectionListener(this);

		/* set size of the frame */
		setSize(800, 409);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		addWindowListener(this);
		LOG.debug("constructed");
	}

	private void clearSelection() {
		listChords.clearSelection();
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

	public void addToSongActionPerformed(ActionEvent evt) {
		chordNameSend = radioPanel.getChordName();
		if (isValidChordName(chordNameSend)) {
			LOG.info("adding chord to song chord list: {}", chordNameSend);
			Chord chord = radioPanel.createChordFromRadios(getLibraryName());
			if (mainFrame.getSongsPage() != null) {
				boolean result = mainFrame.getSongsPage().addChordToUsedChords(chord);
				if (result) {
					radioPanel.setChordName(null);
					radioPanel.clear();
					clearSelection();
				} else {
					JOptionPane.showMessageDialog(RoboTarChordsPage.this, messages.getString("robotar.chords.chord_already_there"));
				}
			} else {
				LOG.error("no songs page!");
			}
		} else {
			JOptionPane.showMessageDialog(RoboTarChordsPage.this, messages.getString("robotar.chords.fill_chord_name"));
		}
	}
	
	private boolean isValidChordName(String chordName) {
		return (!((chordName == null) || chordName.trim().isEmpty() || chordName.equalsIgnoreCase(messages.getString("robotar.chords.enter_chord_name"))));
	}

	/**
	 * If chord list selection is changed, this method is called.
	 */
	@Override
	public void valueChanged(ListSelectionEvent event) {
		if (!event.getValueIsAdjusting()) {
			JList theList = (JList) event.getSource();
			DefaultListModel model = (DefaultListModel) theList.getModel();
			int selIdx = (int) theList.getSelectedIndex();
			if (selIdx >= 0) {
				Chord chord = (Chord) model.get(selIdx);
				setChord(chord);
			}
		}
	}

	/**
	 * Updates all UI components with current chord.
	 * @param chord
	 */
	private void setChord(Chord chord) {
		radioPanel.setChordName(chord.getName());
		radioPanel.setupRadios(chord);
		showChordImage(chord);
		prepareChord();
	}
	
	/**
	 * TODO currently reads fixed images. after adding own new chords -> generate image on the fly
	 * @param chord
	 */
	private void showChordImage(Chord chord) {
		String chordName = chord.getName();
		String imageName = "/default-chords/" + chordName + ".png";
		try {
			lblChordPicture.setIcon(new ImageIcon(ImageIO
					.read(RoboTarChordsPage.class.getResource(imageName)))); 
			lblChordPicture.setText(chordName);
		} catch (IOException e) {
			LOG.error("file not found: {}", imageName);
			lblChordPicture.setText(messages.getString("robotar.chords.image_not_found"));
			lblChordPicture.setIcon(null);
			LOG.debug("stacktrace", e);
		}
	}

	/**
	 * Fills servoSettings field to be used in loop() - RoboTarConsole
	 */
	public void prepareChord() {
		// use radio panel as source for chord, unfilled radios will be marked OPEN
		Chord chord = radioPanel.createChordFromRadios(getLibraryName());
		ServoSettings servos = new ServoSettings(chord);
		mainFrame.setServoSettings(servos);
		LEDSettings leds = new LEDSettings(chord);
		mainFrame.setLeds(leds);
		LOG.debug("preparing servos Values on chords page: {}", servos.debugOutput());
		LOG.debug("preparing leds on chords page: {}", leds.debugOutput());
	}
	
	private String getLibraryName() {
		if (listChords.isSelectionEmpty()) {
			return "user";
		} else {
			return ((Chord)listChords.getSelectedValue()).getLibrary();
		}
	}

	public JButton getBtnNewChord() {
		return btnNewChord;
	}

	public void setBtnNewChord(JButton btnNewChord) {
		this.btnNewChord = btnNewChord;
	}

	public DefaultListModel getChordList() {
		return chordList;
	}

	public void setChordList(DefaultListModel chordList) {
		this.chordList = chordList;
	}

	public RoboTarStartPage getMainFrame() {
		return mainFrame;
	}

	public void setMainFrame(RoboTarStartPage mainFrame) {
		this.mainFrame = mainFrame;
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
		btnAddToSong.setEnabled(mainFrame.isActiveSongEditable());
		if (mainFrame.isActiveSongEditable()) {
			activeSong.setText(mainFrame.getSongsPage().getActualSong().getFullTitle());
		} else {
			activeSong.setText(messages.getString("robotar.chords.no_song_selected"));
		}
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

}
