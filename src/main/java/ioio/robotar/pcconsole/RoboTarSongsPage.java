package ioio.robotar.pcconsole;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.Color;

import cz.versarius.xchords.Chord;
import cz.versarius.xchords.ChordBag;
import cz.versarius.xchords.ChordLibrary;
import cz.versarius.xsong.ChordRef;
import cz.versarius.xsong.Line;
import cz.versarius.xsong.Part;
import cz.versarius.xsong.Song;
import cz.versarius.xsong.Verse;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.ListSelectionModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Adjustable;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Component;
import java.awt.Rectangle;

import javax.swing.SwingConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class RoboTarSongsPage extends JFrame implements WindowListener {
	private static final long serialVersionUID = -7862830927381806488L;
	static final Logger LOG = LoggerFactory.getLogger(RoboTarSongsPage.class);
	
	/** style constants */
	private static final String TITLE_STYLE = "TitleStyle";
	private static final String MAIN_STYLE = "MainStyle";
	private static final String MARKED_STYLE = "MarkedStyle";
	private static final String CHORD_STYLE = "ChordStyle";
	private static final String MARKED_CHORD_STYLE = "MarkedChordStyle";
	private static final String MISSING_CHORD_STYLE = "MissingChordStyle";
	
	private JPanel frmBlueAhuizoteSongs;
	
	private static int counter = 1;
	
	private Song actualSong;
	private boolean playing;
	private boolean editing;
	private boolean missingChords;
	private boolean defaultSongsLoaded;
	
	// where are chords and lines in the text pane - to be able to select them during play
	private PositionHints hints;
	private JTextPane textPane;
	private JScrollPane scrollPane;
	private JList songList;
	private DefaultListModel songListModel;
	private JList chordList;
	private DefaultListModel chordListModel;
	
	private JButton btnPlay;
	private JButton btnSimPedal;
	private JButton btnEditSong;
	private JButton btnNewSong;
	private JButton btnLoadDefaultSongs;

	/** reference to mainframe and chordmanager. */
	private RoboTarStartPage mainFrame;
	private ResourceBundle messages;
	private JPanel editPanel;
	private JButton btnNewLine;
	private JButton btnNewVerse;
	private JButton btnCreateChord;
	private JButton btnAddChord;
	private Set<Chord> usedChords;
	private JButton btnLoadSong;
	private JButton btnSaveSong;
	
	/**
	 * Create the frame.
	 */
	public RoboTarSongsPage(final RoboTarStartPage mainFrame) {
		this.mainFrame = mainFrame;
		messages = mainFrame.getMessages();

		setBounds(100, 100, 800, 510);
		frmBlueAhuizoteSongs = new JPanel();
		frmBlueAhuizoteSongs.setBackground(Color.BLUE);
		frmBlueAhuizoteSongs.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(frmBlueAhuizoteSongs);
		GridBagLayout gbl_frmBlueAhuizoteSongs = new GridBagLayout();
		gbl_frmBlueAhuizoteSongs.columnWidths = new int[] {30, 200, 113, 200, 200, 0};
		gbl_frmBlueAhuizoteSongs.rowHeights = new int[] {30, 30, 376, 0};
		gbl_frmBlueAhuizoteSongs.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_frmBlueAhuizoteSongs.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		frmBlueAhuizoteSongs.setLayout(gbl_frmBlueAhuizoteSongs);
		
		btnLoadDefaultSongs = new JButton("Load default songs");
		btnLoadDefaultSongs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadDefaultSongs();
			}
		});
		GridBagConstraints gbc_btnLoadDefaultSongs = new GridBagConstraints();
		gbc_btnLoadDefaultSongs.anchor = GridBagConstraints.NORTH;
		gbc_btnLoadDefaultSongs.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnLoadDefaultSongs.insets = new Insets(0, 0, 5, 5);
		gbc_btnLoadDefaultSongs.gridx = 1;
		gbc_btnLoadDefaultSongs.gridy = 0;
		frmBlueAhuizoteSongs.add(btnLoadDefaultSongs, gbc_btnLoadDefaultSongs);
		
		// buttons default status
		btnLoadDefaultSongs.setEnabled(true);
		
		btnEditSong = new JButton(messages.getString("robotar.songs.edit_song"));
		btnEditSong.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (messages.getString("robotar.songs.done_edit_song").equals(((JButton)e.getSource()).getText())) {
					doneEditSongPressed();
				} else {
					editSongPressed();
				}
			}
		});
		
		btnNewSong = new JButton(messages.getString("robotar.songs.new_song"));
		btnNewSong.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newSongPressed();
			}
		});
		
		btnLoadSong = new JButton("Load song");
		GridBagConstraints gbc_btnLoadSong = new GridBagConstraints();
		gbc_btnLoadSong.fill = GridBagConstraints.BOTH;
		gbc_btnLoadSong.insets = new Insets(0, 0, 5, 5);
		gbc_btnLoadSong.gridx = 2;
		gbc_btnLoadSong.gridy = 0;
		btnLoadSong.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					loadSong();
				} catch (FileNotFoundException e1) {
					JOptionPane.showMessageDialog(RoboTarSongsPage.this, messages.getString("robotar.songs.file_not_found"));
				}
			}
		});
		frmBlueAhuizoteSongs.add(btnLoadSong, gbc_btnLoadSong);
		
		GridBagConstraints gbc_btnNewSong = new GridBagConstraints();
		gbc_btnNewSong.anchor = GridBagConstraints.NORTH;
		gbc_btnNewSong.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnNewSong.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewSong.gridx = 3;
		gbc_btnNewSong.gridy = 0;
		frmBlueAhuizoteSongs.add(btnNewSong, gbc_btnNewSong);
		btnNewSong.setEnabled(true);
		
		GridBagConstraints gbc_btnEditSong = new GridBagConstraints();
		gbc_btnEditSong.anchor = GridBagConstraints.NORTH;
		gbc_btnEditSong.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnEditSong.insets = new Insets(0, 0, 5, 0);
		gbc_btnEditSong.gridx = 4;
		gbc_btnEditSong.gridy = 0;
		frmBlueAhuizoteSongs.add(btnEditSong, gbc_btnEditSong);
		btnEditSong.setEnabled(false);
		
		songList = new JList();
		songList.setCellRenderer(new SongListCellRenderer());
		songList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		songList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					songSelected(e);
				}
			}
		});
		
		btnSimPedal = new JButton(messages.getString("robotar.songs.sim_pedal"));
		btnSimPedal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				simPedalPressed();
			}
		});
		
		btnPlay = new JButton(messages.getString("robotar.songs.play_song"));
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (messages.getString("robotar.songs.stop_song").equals(((JButton)e.getSource()).getText())) {
					stopSong();
				} else {
					playSong();
				}
			}
		});
		
		btnSaveSong = new JButton("Save song");
		GridBagConstraints gbc_btnSaveSong = new GridBagConstraints();
		gbc_btnSaveSong.fill = GridBagConstraints.BOTH;
		gbc_btnSaveSong.insets = new Insets(0, 0, 5, 5);
		gbc_btnSaveSong.gridx = 2;
		gbc_btnSaveSong.gridy = 1;
		btnSaveSong.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveSong();
			}
		});
		frmBlueAhuizoteSongs.add(btnSaveSong, gbc_btnSaveSong);
		
		GridBagConstraints gbc_btnPlay = new GridBagConstraints();
		gbc_btnPlay.anchor = GridBagConstraints.NORTH;
		gbc_btnPlay.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnPlay.insets = new Insets(0, 0, 5, 5);
		gbc_btnPlay.gridx = 3;
		gbc_btnPlay.gridy = 1;
		frmBlueAhuizoteSongs.add(btnPlay, gbc_btnPlay);
		btnPlay.setEnabled(false);
		
		GridBagConstraints gbc_btnSimPedal = new GridBagConstraints();
		gbc_btnSimPedal.anchor = GridBagConstraints.NORTH;
		gbc_btnSimPedal.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnSimPedal.insets = new Insets(0, 0, 5, 0);
		gbc_btnSimPedal.gridx = 4;
		gbc_btnSimPedal.gridy = 1;
		frmBlueAhuizoteSongs.add(btnSimPedal, gbc_btnSimPedal);
		btnSimPedal.setEnabled(false);
		
		GridBagConstraints gbc_songList = new GridBagConstraints();
		gbc_songList.fill = GridBagConstraints.BOTH;
		gbc_songList.insets = new Insets(0, 0, 0, 5);
		gbc_songList.gridx = 1;
		gbc_songList.gridy = 2;
		frmBlueAhuizoteSongs.add(songList, gbc_songList);
		
		editPanel = new JPanel();
		editPanel.setBackground(Color.BLUE);
		GridBagConstraints gbc_editPanel = new GridBagConstraints();
		gbc_editPanel.fill = GridBagConstraints.BOTH;
		gbc_editPanel.insets = new Insets(0, 0, 0, 5);
		gbc_editPanel.gridx = 2;
		gbc_editPanel.gridy = 2;
		frmBlueAhuizoteSongs.add(editPanel, gbc_editPanel);
		
		GridBagLayout gbl_editPanel = new GridBagLayout();
		gbl_editPanel.columnWidths = new int[] {100, 0};
		gbl_editPanel.rowHeights = new int[] {30, 30, 30, 30, 142, 0};
		gbl_editPanel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_editPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		editPanel.setLayout(gbl_editPanel);
		
		GridBagConstraints gbc_btnTest = new GridBagConstraints();
		gbc_btnTest.insets = new Insets(0, 0, 5, 0);
		gbc_btnTest.gridx = 0;
		gbc_btnTest.gridy = 0;
		gbc_btnTest.anchor = GridBagConstraints.NORTH;
		gbc_btnTest.fill = GridBagConstraints.BOTH;
		btnNewVerse = new JButton(messages.getString("robotar.songs.new_verse"));
		
		GridBagConstraints gbc_btnNewVerse = new GridBagConstraints();
		gbc_btnNewVerse.gridy = 0;
		gbc_btnNewVerse.gridx = 0;
		gbc_btnNewVerse.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewVerse.fill = GridBagConstraints.BOTH;
		btnNewVerse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addNewVerseToSong();
			}
		});
		editPanel.add(btnNewVerse, gbc_btnNewVerse);
		
		btnNewLine = new JButton(messages.getString("robotar.songs.new_line"));
		GridBagConstraints gbc_btnNewLine = new GridBagConstraints();
		gbc_btnNewLine.fill = GridBagConstraints.BOTH;
		gbc_btnNewLine.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewLine.gridx = 0;
		gbc_btnNewLine.gridy = 1;
		btnNewLine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addNewLineToSong();
			}
		});
		editPanel.add(btnNewLine, gbc_btnNewLine);
		
		btnCreateChord = new JButton(messages.getString("robotar.songs.create_chord"));
		GridBagConstraints gbc_btnCreateChord = new GridBagConstraints();
		gbc_btnCreateChord.fill = GridBagConstraints.BOTH;
		gbc_btnCreateChord.insets = new Insets(0, 0, 5, 0);
		gbc_btnCreateChord.gridx = 0;
		gbc_btnCreateChord.gridy = 2;
		btnCreateChord.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainFrame.startChordsPage();
			}
		});
		editPanel.add(btnCreateChord, gbc_btnCreateChord);
		
		btnAddChord = new JButton(messages.getString("robotar.songs.add_chord"));
		btnAddChord.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_btnAddChord = new GridBagConstraints();
		gbc_btnAddChord.fill = GridBagConstraints.BOTH;
		gbc_btnAddChord.insets = new Insets(0, 0, 5, 0);
		gbc_btnAddChord.gridx = 0;
		gbc_btnAddChord.gridy = 3;
		btnAddChord.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addChordToSong();
			}
		});
		editPanel.add(btnAddChord, gbc_btnAddChord);
		
		chordList = new JList();
		chordList.setLayoutOrientation(JList.VERTICAL_WRAP);
		chordList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		chordList.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		chordList.setCellRenderer(new ChordListPlainCellRenderer());
		chordList.setVisibleRowCount(14);
		GridBagConstraints gbc_chordList = new GridBagConstraints();
		gbc_chordList.anchor = GridBagConstraints.EAST;
		gbc_chordList.fill = GridBagConstraints.BOTH;
		gbc_chordList.gridx = 0;
		gbc_chordList.gridy = 4;
		editPanel.add(chordList, gbc_chordList);
		
		JPanel songPanel = new JPanel();
		songPanel.setBackground(Color.WHITE);
		GridBagConstraints gbc_songPanel = new GridBagConstraints();
		gbc_songPanel.gridwidth = 2;
		gbc_songPanel.fill = GridBagConstraints.BOTH;
		gbc_songPanel.gridx = 3;
		gbc_songPanel.gridy = 2;
		frmBlueAhuizoteSongs.add(songPanel, gbc_songPanel);
		
		
		textPane = new JTextPane();
		textPane.setEditable(false);
		scrollPane = new JScrollPane(textPane);
		scrollPane.setPreferredSize(new Dimension(450, 370));
		songPanel.add(scrollPane);
		//getContentPane().add(scrollPane);
		setupStyles(textPane);
		
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		addWindowListener(this);
		
		pack();
		setLocationByPlatform(true);
		setVisible(true);
	}

	protected void loadSong() throws FileNotFoundException {
		JFileChooser fc = new JFileChooser();
		int returnValue = fc.showOpenDialog(this);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            XMLSongLoader loader = new XMLSongLoader();
            Song song = loader.loadSong(new FileInputStream(file));
            if (songListModel == null) {
    			songListModel = new DefaultListModel();
    			songList.setModel(songListModel);
    		}
            songListModel.add(0, song);
		}
	}

	protected void saveSong() {
		if (actualSong == null) {
			JOptionPane.showMessageDialog(RoboTarSongsPage.this, messages.getString("robotar.songs.nothing_to_save"));
			return;
		}
		JFileChooser fc = new JFileChooser();
		int returnValue = fc.showSaveDialog(this);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            XMLSongSaver saver = new XMLSongSaver();
            saver.save(actualSong, file);
		}
		
	}

	protected void loadDefaultSongs() {
		XMLSongLoader loader = new XMLSongLoader();
		Song song = loader.loadSong(RoboTarChordsPage.class.getResourceAsStream("/default-songs/greensleeves.xml"));
		// load other songs..
		
		if (songListModel == null) {
			songListModel = new DefaultListModel();
			songList.setModel(songListModel);
		}
		int previousSize = songListModel.size();
		songListModel.addElement(song);
		// selects first of the default songs
		actualSong = (Song) songListModel.get(previousSize);
		defaultSongsLoaded = true;
		btnLoadDefaultSongs.setEnabled(false);
	}

	protected void songSelected(ListSelectionEvent e) {
		// show song in textpane
		JList list = (JList)e.getSource();
		actualSong = (Song) list.getSelectedValue();
		prepareForPlaying();
		btnEditSong.setEnabled(true);
		btnNewSong.setEnabled(true);
	}
	
	/**
	 * Traverse through song, creates position hints to be able to mark chords when playing,
	 * also checks if all referenced chords exists in usedchords section.
	 */
	protected void prepareForPlaying() {
		showSong(actualSong, textPane);
		checkChords();
	}
	
	protected void editSongPressed() {
		setupEditMode();
		addNewLineData();
	}
	
	protected void setupEditMode() {
		btnEditSong.setText(messages.getString("robotar.songs.done_edit_song"));
		btnEditSong.setEnabled(true);
		btnNewSong.setEnabled(false);
		btnPlay.setEnabled(false);
		btnSimPedal.setEnabled(false);
		songList.setEnabled(false);
		btnLoadDefaultSongs.setEnabled(false);
		btnLoadSong.setEnabled(false);
		btnSaveSong.setEnabled(false);
		editPanel.setVisible(true);
		//textPane.setEditable(true);
		editing = true;
		loadChordsFromSong();
		createMarker();
	}

	protected void createMarker() {
		StyledDocument doc = textPane.getStyledDocument();
		int position = doc.getLength();
		
		Style markerStyle = doc.getStyle(MISSING_CHORD_STYLE);
		try {
			doc.insertString(position, "*", markerStyle);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	protected void removeMarker() {
		StyledDocument doc = textPane.getStyledDocument();
		int position = doc.getLength();
		try {
			doc.remove(position - 1, 1);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	protected void pruneEmptyObjects() {
		// check empty parts
		int lastIdx = actualSong.getParts().size() - 1;
		if (lastIdx == -1) return;
		for (int i = lastIdx; i >= 0; i--) {
			Part part = actualSong.getParts().get(i);
			if (part.getLines() == null) {
				actualSong.getParts().remove(i);
			} else {
				// check empty lines
				int lastLineIdx = part.getLines().size() - 1;
				if (lastLineIdx == -1) {
					actualSong.getParts().remove(i);
				} else {
					for (int j = lastLineIdx; j >= 0; j--) {
						Line line = part.getLines().get(j);
						if (line.getChords() == null) {
							part.getLines().remove(j);
						}
					}
					if (part.getLines().size() == 0) {
						actualSong.getParts().remove(i);
					}
				}
			}
		}
	}

	protected void doneEditSongPressed() {
		btnEditSong.setText(messages.getString("robotar.songs.edit_song"));
		btnEditSong.setEnabled(true);
		btnNewSong.setEnabled(true);
		btnPlay.setEnabled(true);
		btnSimPedal.setEnabled(false);
		songList.setEnabled(true);
		btnLoadDefaultSongs.setEnabled(!defaultSongsLoaded);
		editPanel.setVisible(false);
		btnLoadSong.setEnabled(true);
		btnSaveSong.setEnabled(true);
		//textPane.setEditable(false);
		editing = false;
		removeMarker();
		pruneEmptyObjects();
		prepareForPlaying();
	}
	
	protected void loadChordsFromSong() {
		if (chordListModel == null) {
			chordListModel = new DefaultListModel();
			chordList.setModel(chordListModel);
		} else {
			chordListModel.removeAllElements();
		}
		// todo - maybe can be done simpler, this is now double checking
		usedChords = new HashSet<Chord>();
		for (PositionHint hint : hints.getChords()) {
			ChordRef ref = hint.getChordRef();
			Chord chord = ref.getChord();
			if (chord != null && !usedChords.contains(chord)) {
				chordListModel.addElement(chord);
				usedChords.add(chord);
			}
		}
	}

	protected void addNewLineData() {
		Part part;
		Line line;
		if (actualSong.getParts().isEmpty()) {
			part = new Verse();
			actualSong.getParts().add(part);
		} else {
			part = actualSong.getParts().get(actualSong.getParts().size() - 1);
		}
		if (part.getLines() == null) {
			part.setLines(new ArrayList<Line>());
		}
		// always create
		line = new Line();
		part.getLines().add(line);
	}

	protected void addNewLineToSong() {
		addNewLineData();
		
		StyledDocument doc = textPane.getStyledDocument();
		int position = doc.getLength() - 1;
		
		Style mainStyle = doc.getStyle(MAIN_STYLE);
		try {
			doc.insertString(position, "\n", mainStyle);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	protected void addNewVerseToSong() {
		StyledDocument doc = textPane.getStyledDocument();
		int position = doc.getLength() - 1;
		
		Style mainStyle = doc.getStyle(MAIN_STYLE);
		try {
			doc.insertString(position, "\n\n", mainStyle);

			Part part;
			part = new Verse();
			actualSong.getParts().add(part);

		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
	}

	protected void addChordToSong() {
		Chord chord = (Chord)chordList.getSelectedValue();
		if (chord != null) {
			Part part;
			Line line;
			if (actualSong.getParts().isEmpty()) {
				part = new Verse();
				actualSong.getParts().add(part);
			} else {
				part = actualSong.getParts().get(actualSong.getParts().size() - 1);
			}
			if (part.getLines() == null) {
				part.setLines(new ArrayList<Line>());
			}
			if (part.getLines().isEmpty()) {
				line = new Line();
				part.getLines().add(line);
			} else {
				line = part.getLines().get(part.getLines().size() - 1);
			}
			if (line.getChords() == null) {
				line.setChords(new ArrayList<ChordRef>());
			}
			int lineLength = 0;
			for (ChordRef chr : line.getChords()) {
				lineLength += chr.getChord().getName().length() + 1;
			}
			StyledDocument doc = textPane.getStyledDocument();
			int position = doc.getLength() - 1;
			
			ChordRef ref = new ChordRef();
			ref.setChordId(chord.getId());
			// TODO - clone?
			ref.setChord(chord);
			// first position is 1, not 0
			ref.setPosition(lineLength+1);
			line.getChords().add(ref);
			// add to used chords section
			actualSong.getUsedChords().add(chord);
			
			Style chordStyle = doc.getStyle(CHORD_STYLE);
			Style mainStyle = doc.getStyle(MAIN_STYLE);
			try {
				doc.insertString(position, chord.getName(), chordStyle);
				doc.insertString(position + chord.getName().length(), " ", mainStyle);
				
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}
	
	protected void newSongPressed() {
		// dialog - title, artist, info
		NewSongDialog dialog = new NewSongDialog(this);
		dialog.pack();
		dialog.setTitleText(generateSongName());
		dialog.setInterpretText(messages.getString("robotar.songs.default_artist_title"));
		dialog.setInfoText("");
		dialog.setVisible(true);
		
		if (dialog.isPressedOK()) {
			Song song = new Song();
			song.setTitle(dialog.getTitleText());
			song.setInterpret(dialog.getInterepretText());
			song.setInfo(dialog.getInfoText());
			
			// first part and line - empty, must be there to behave correctly, if empty, they will be removed at the end
			Part firstPart = new Verse();
			song.getParts().add(firstPart);
			List<Line> lines = new ArrayList<Line>();
			firstPart.setLines(lines);
			List<ChordRef> chordrefs = new ArrayList<ChordRef>();
			Line line = new Line();
			line.setChords(chordrefs);
			lines.add(line);
			
			if (songListModel == null) {
				songListModel = new DefaultListModel();
				songList.setModel(songListModel);
			}
			// will fire refreshing of textpane twice... :/
			songListModel.add(0, song);
			actualSong = (Song)songListModel.firstElement();
			songList.setSelectedIndex(0);
			
			// put into edit mode
			setupEditMode();
		}
	}

	private String generateSongName() {
		String name = messages.getString("robotar.songs.default_song_title") + " " + counter;
		counter++;
		return name;
	}

	/**
	 * Simulation of pressing pedal.
	 * The processing will be almost the same, but written in some event, to react for the pedal.
	 */
	protected void simPedalPressed() {
		PositionHint oldChordHint = hints.getChordHint();
		PositionHint oldLineHint = hints.getLineHint(oldChordHint);
		
		PositionHint chordHint = hints.getNextChordHint();
		if (chordHint != null) {
			// visual handling
			PositionHint lineHint = hints.getLineHint(chordHint);
			if (oldChordHint != null) {
				// the first one chord doesn't unmark anything
				unmarkCurrent(oldChordHint, oldLineHint, lineHint);
			}
			markCurrent(chordHint, lineHint);
			
			// TODO call IOIO to change servos
			// hm. we can't call IOIO. there is loop() in console, that check's pedal
			prepareChord(chordHint.getChordRef().getChord());
		} else {
			// end of song
			stopIt(oldChordHint, oldLineHint);
		}
		
	}

	public void prepareChord(Chord chord) {
		mainFrame.getServoSettings().setChord(chord);
		prepareLEDs(new LEDSettings(chord));
	}

	public void prepareNoChord() {
		mainFrame.getServoSettings().setInitialPosition();
		prepareLEDs(new LEDSettings());
	}
	
	protected void prepareLEDs(LEDSettings leds) {
		mainFrame.setLeds(leds);
		LOG.debug("preparing LED Values on songs page: {}", leds.debugOutput());
	}
	
	/**
	 * Checks, if all the chords in the song exists in usedchords section.
	 * for each chord:
	 * If chord exists in usedchords, set it to chordref and try to put it to chordmanager, 
	 * 		into the appropriate library (if already exists there = nothing - even if not the same TODO)
	 * If chord doesn't exist in used chords, try to load it from chordmanager,
	 *      from the appropriate library (based on id of chord ('library-chord')) 
	 *      and set to chordref and put into the usedchords section.
	 *      If it doesn't exist, mark as missing.
	 */
	protected void checkChords() {
		ChordManager manager = mainFrame.getChordManager();
		ChordBag used = actualSong.getUsedChords();
		for (PositionHint hint : hints.getChords()) {
			ChordRef ref = hint.getChordRef();
			String libraryName = Chord.getLibraryName(ref.getChordId());
			String chordName = Chord.getChordName(ref.getChordId());
			Chord chord = used.findByName(chordName);
			if (chord != null) {
				ref.setChord(chord);
				ChordLibrary lib = manager.findByName(libraryName);
				if (lib == null) {
					lib = new ChordLibrary(libraryName);
				}
				Chord existing = lib.findByName(chordName);
				if (existing == null) {
					lib.add(chord);
				} else {
					// TODO - compare the two.. now left the older one as is...
				}
				continue;
			} else {
				// chord was not in usedchords section
				ChordLibrary library = manager.getChordLibraries().get(libraryName);
				if (library != null) {
					Chord existing = library.findByName(chordName);
					if (existing != null) {
						ref.setChord(existing);
						used.add(existing);
						continue;
					}
				}
				
				if ("user".equals(libraryName)) {
					// look into current chord buffer on chords page
					RoboTarChordsPage chPage = mainFrame.getChordsPage();
					if (chPage != null) {
						DefaultListModel model = chPage.getChordListModel();
						if (model != null) {
							Chord existing = findByName(model, ref.getChordId()); 
							if (existing != null) {
								ref.setChord(existing);
								used.add(existing);
								continue;
							}
						}
					}
				}
			}
			markMissing(hint);
			missingChords = true;
			
		}
		btnPlay.setEnabled(!missingChords);
		// first press Play
		btnSimPedal.setEnabled(false); 
		btnEditSong.setEnabled(true);
		btnNewSong.setEnabled(true);
	}
	
	private Chord findByName(DefaultListModel model, String chordId) {
		// user-T-1
		String name = chordId.substring(5);
		for (int i = 0; i<model.getSize(); i++) {
			Chord chord = (Chord)model.get(i);
			if (chord != null) {
				if (name.equalsIgnoreCase(chord.getName())) {
					return chord;
				}
			}
		}
		return null;
	}

	/**
	 * Select this chord as missing one.
	 */
	protected void markMissing(PositionHint chordHint) {
		StyledDocument doc = textPane.getStyledDocument();
		Style missingChordStyle = doc.getStyle(MISSING_CHORD_STYLE);
		doc.setCharacterAttributes(chordHint.getOffset(), chordHint.getLength(), missingChordStyle, false);
	}
	
	/**
	 * Select current chord and line.
	 * @param chordHint
	 * @param lineHint
	 */
	protected void markCurrent(PositionHint chordHint, PositionHint lineHint) {
		StyledDocument doc = textPane.getStyledDocument();
		Style markedStyle = doc.getStyle(MARKED_STYLE);
		Style markedChordStyle = doc.getStyle(MARKED_CHORD_STYLE);
		
		doc.setCharacterAttributes(lineHint.getOffset(), lineHint.getLength(), markedStyle, false);
		doc.setCharacterAttributes(chordHint.getOffset(), chordHint.getLength(), markedChordStyle, false);
		
		int ahead = hints.lookAhead(lineHint);
		scrollTo(ahead);
	}

	/**
	 * Unselect current chord and line.
	 * @param chordHint
	 * @param lineHint
	 * @param newLineHint
	 */
	protected void unmarkCurrent(PositionHint chordHint, PositionHint lineHint, PositionHint newLineHint) {
		if (chordHint == null) {
			// not even first chord displayed
			return;
		}
		StyledDocument doc = textPane.getStyledDocument();
		Style chordStyle = doc.getStyle(CHORD_STYLE);
		Style mainStyle = doc.getStyle(MAIN_STYLE);

		doc.setCharacterAttributes(chordHint.getOffset(), chordHint.getLength(), chordStyle, true);
		if (newLineHint == null || lineHint != newLineHint) {
			doc.setCharacterAttributes(lineHint.getOffset(), lineHint.getLength(), mainStyle, true);
		}

	}

	/**
	 * Start to play a song.
	 */
	protected void playSong() {
		if (hints.getChords().isEmpty()) {
			JOptionPane.showMessageDialog(RoboTarSongsPage.this, messages.getString("robotar.songs.no_chords_in_song"));
		} else {
			btnPlay.setText(messages.getString("robotar.songs.stop_song"));
			btnSimPedal.setEnabled(true);
			btnNewSong.setEnabled(false);
			btnEditSong.setEnabled(false);
			btnLoadDefaultSongs.setEnabled(false);
			songList.setEnabled(false);
			btnLoadSong.setEnabled(false);
			btnSaveSong.setEnabled(false);
			// set as start
			hints.setCurrentChord(-1);
			hints.setCurrentLine(0);
			scrollTo(0);
			playing = true;
		}
	}

	/** 
	 * Scroll to some position in song.
	 * 
	 * @param position
	 */
	protected void scrollTo(int position) {
		if (position == -1) {
			position = textPane.getDocument().getLength();
		}
		LOG.debug("position: {}", position);
		textPane.setCaretPosition(position);
		if (position == 0) {
			textPane.setCaretPosition(1);
			/*Rectangle vis = textPane.getVisibleRect();
			vis.x = 0;
			textPane.scrollRectToVisible(vis);*/
		}
	}
	
	/**
	 * Stop to play a song.
	 */
	protected void stopSong() {
		PositionHint oldChordHint = hints.getChordHint();
		PositionHint oldLineHint = hints.getLineHint(oldChordHint);
		
		stopIt(oldChordHint, oldLineHint);
	}
	
	
	protected void stopIt(PositionHint oldChordHint, PositionHint oldLineHint) {
		playing = false;
		unmarkCurrent(oldChordHint, oldLineHint, null);
		btnPlay.setText(messages.getString("robotar.songs.play_song"));
		btnSimPedal.setEnabled(false);
		btnNewSong.setEnabled(true);
		btnEditSong.setEnabled(true);
		btnLoadDefaultSongs.setEnabled(!defaultSongsLoaded);
		songList.setEnabled(true);
		btnLoadSong.setEnabled(true);
		btnSaveSong.setEnabled(true);
		scrollTo(0);
		// call to release servos - neutral positions
		prepareNoChord();
	}

	/**
	 * Prepare styles for lyrics and chords.
	 * @param pane
	 */
	protected void setupStyles(JTextPane pane) {
		StyleContext sc = new StyleContext();
		Style defaultStyle = sc.getStyle(StyleContext.DEFAULT_STYLE);

		Style mainStyle = sc.addStyle(MAIN_STYLE, null);
	    StyleConstants.setForeground(mainStyle, Color.BLACK);
	    StyleConstants.setBackground(mainStyle, Color.WHITE);
	    StyleConstants.setBold(mainStyle, true);
	    StyleConstants.setFontFamily(mainStyle, "monospaced");
	    StyleConstants.setFontSize(mainStyle, mainFrame.getPreferences().getMainSize());
	    
	    
	    Style chordStyle = sc.addStyle(CHORD_STYLE, mainStyle);
		
	    Style markedStyle = sc.addStyle(MARKED_STYLE, mainStyle);
	    StyleConstants.setForeground(markedStyle, mainFrame.getPreferences().getMarkedColor());
		StyleConstants.setBackground(markedStyle, Color.YELLOW);
		StyleConstants.setFontSize(markedStyle, mainFrame.getPreferences().getMarkedSize());
	    
		Style markedChordStyle = sc.addStyle(MARKED_CHORD_STYLE, mainStyle);
		StyleConstants.setForeground(markedChordStyle, mainFrame.getPreferences().getMarkedChordColor());
		StyleConstants.setBackground(markedChordStyle, Color.YELLOW);
		StyleConstants.setFontSize(markedChordStyle, mainFrame.getPreferences().getMarkedChordSize());

		Style missingChordStyle = sc.addStyle(MISSING_CHORD_STYLE, mainStyle);
		StyleConstants.setForeground(missingChordStyle, Color.RED);
		StyleConstants.setBackground(missingChordStyle, Color.WHITE);

	    Style titleStyle = sc.addStyle(TITLE_STYLE, defaultStyle);
		StyleConstants.setForeground(titleStyle, Color.BLACK);
		StyleConstants.setBackground(titleStyle, Color.WHITE);
		StyleConstants.setBold(titleStyle, true);
		StyleConstants.setFontSize(titleStyle, 18);
		
		StyledDocument doc = pane.getStyledDocument();
		doc.addStyle(CHORD_STYLE, chordStyle);
		doc.addStyle(MAIN_STYLE, mainStyle);
		doc.addStyle(TITLE_STYLE, titleStyle);
		doc.addStyle(MARKED_STYLE, markedStyle);
		doc.addStyle(MARKED_CHORD_STYLE, markedChordStyle);
		doc.addStyle(MISSING_CHORD_STYLE, missingChordStyle);
	}

	/**
	 * Prepare song for display
	 * @param song
	 * @param pane
	 */
	protected PositionHints showSong(Song song, JTextPane pane) {
		// prepare styles
		StyledDocument doc = pane.getStyledDocument();
		Style titleStyle = doc.getStyle(TITLE_STYLE);
		Style chordStyle = doc.getStyle(CHORD_STYLE);
		Style mainStyle = doc.getStyle(MAIN_STYLE);
		
		hints = new PositionHints();
		try {
			// clear pane
			doc.remove(0, doc.getLength());
			
			// title and interpret line
			doc.insertString(0, song.getTitle() + "     " + song.getInterpret() + "\n", titleStyle);
			
			// process parts
			for (Part part : song.getParts()) {
				doc.insertString(doc.getLength(), "\n", null);
				
				int lineNum = 0;
				for (Line line : part.getLines()) {
					if ((line.getChords() == null) || (line.getChords().isEmpty())) {
						if (line.getText() == null) {
							// totally empty line
							continue;
						}
					} else {
						// put chords above the text
						doc.insertString(doc.getLength(), formatChords(line, hints, lineNum, doc.getLength()), chordStyle);
					}

					int lineOffset = doc.getLength();
					String lineText = line.getText();
					if (lineText == null) {
						lineText = "";
					}
					doc.insertString(doc.getLength(), lineText + "\n", mainStyle);
					PositionHint lineHint = new PositionHint();
					lineHint.setLine(lineNum);
					lineHint.setOffset(lineOffset);
					if (line.getText() != null) {
						lineHint.setLength(line.getText().length());
					} else {
						lineHint.setLength(0);
					}
					hints.getLines().add(lineHint);
					
					lineNum++;
				}
	            
			}
			// scroll to top
			scrollTo(0); 
		} catch (BadLocationException e1) {
			e1.printStackTrace(); //TODO
		}
		return hints;
	}

	/**
	 * Will position chords to the proper places.
	 * first position is 1, not 0!
	 * 
	 * @param line
	 * @return
	 */
	private String formatChords(Line line, PositionHints hints, int lineNum, int lineOffset) {
		StringBuilder str = new StringBuilder();
		for (ChordRef ref : line.getChords()) {
			int end = str.length() + 1;
			int position = ref.getPosition();
			if (end < position) {
				for (int i = 0; i< position-end; i++) {
					str.append(" ");
				}
			}
			String chordName = Chord.getChordName(ref.getChordId());
			
			PositionHint chordHint = new PositionHint();
			chordHint.setOffset(lineOffset + position - 1);
			chordHint.setLength(chordName.length());
			chordHint.setLine(lineNum);
			chordHint.setChordRef(ref);
			hints.getChords().add(chordHint);
			
			str.append(chordName);	
		}
		
		str.append("\n");
		return str.toString();
	}

	public JPanel getFrmBlueAhuizoteSongs() {
		return frmBlueAhuizoteSongs;
	}

	public void setFrmBlueAhuizoteSongs(JPanel frmBlueAhuizoteSongs) {
		this.frmBlueAhuizoteSongs = frmBlueAhuizoteSongs;
	}

	public Song getActualSong() {
		return actualSong;
	}

	public void setActualSong(Song actualSong) {
		this.actualSong = actualSong;
	}

	public DefaultListModel getSongList() {
		return songListModel;
	}

	public void setSongList(DefaultListModel songList) {
		this.songListModel = songList;
	}

	public boolean isPlaying() {
		return playing;
	}

	public void setPlaying(boolean playing) {
		this.playing = playing;
	}

	public boolean isEditing() {
		return editing;
	}

	public void setEditing(boolean editing) {
		this.editing = editing;
	}

	public void windowClosing(WindowEvent e) {
	}
	
	@Override
	public void windowOpened(WindowEvent e) {
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
		btnLoadDefaultSongs.setEnabled(!defaultSongsLoaded && !editing);
		editPanel.setVisible(editing);
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	public boolean isDefaultSongsLoaded() {
		return defaultSongsLoaded;
	}

	public void setDefaultSongsLoaded(boolean defaultSongsLoaded) {
		this.defaultSongsLoaded = defaultSongsLoaded;
	}

	public boolean addChordToUsedChords(Chord chord) {
		if ((usedChords != null) && (!usedChords.contains(chord))) {
			usedChords.add(chord);
			chordListModel.addElement(chord);
			return true;
		}
		return false;
	}
}
