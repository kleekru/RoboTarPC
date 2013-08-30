package ioio.robotar.pcconsole;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.Color;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

import cz.versarius.xchords.Chord;
import cz.versarius.xchords.ChordLibrary;
import cz.versarius.xsong.ChordRef;
import cz.versarius.xsong.Line;
import cz.versarius.xsong.Part;
import cz.versarius.xsong.Song;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JTextPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.ListSelectionModel;

public class RoboTarSongsPage extends JFrame {
	/** style constants */
	private static final String TITLE_STYLE = "TitleStyle";
	private static final String MAIN_STYLE = "MainStyle";
	private static final String MARKED_STYLE = "MarkedStyle";
	private static final String CHORD_STYLE = "ChordStyle";
	private static final String MARKED_CHORD_STYLE = "MarkedChordStyle";
	private static final String MISSING_CHORD_STYLE = "MissingChordStyle";
	
	private JPanel frmBlueAhuizoteSongs;
	private JTextPane textPane; 
	private Song actualSong;
	// where are chords and lines in the text pane - to be able to select them during play
	private PositionHints hints;
	private JList songList;
	private DefaultListModel songListModel;
	/** reference to mainframe and chordmanager. */
	private RoboTarStartPage mainFrame;
	private boolean missingChords;
	private JButton btnPlay;
	private JButton btnSimPedal;
	private JButton btnEdit;
	private JButton btnNewSong;
	private JButton btnLoadDefaultSongs;
	
	/**
	 * Create the frame.
	 */
	public RoboTarSongsPage(RoboTarStartPage mainFrame) {
		this.mainFrame = mainFrame;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 630, 441);
		frmBlueAhuizoteSongs = new JPanel();
		frmBlueAhuizoteSongs.setBackground(Color.LIGHT_GRAY);
		frmBlueAhuizoteSongs.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(frmBlueAhuizoteSongs);
		frmBlueAhuizoteSongs.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),}));
		
		btnLoadDefaultSongs = new JButton("Load default songs");
		btnLoadDefaultSongs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				XMLSongLoader loader = new XMLSongLoader();
				Song song = loader.load(RoboTarChordsPage.class.getResourceAsStream("/default-songs/greensleeves.xml"));
				// other songs..
				songListModel = new DefaultListModel();
				songListModel.addElement(song);
				actualSong = (Song) songListModel.get(0);
				songList.setModel(songListModel);
				btnLoadDefaultSongs.setEnabled(false);
			}
		});
		frmBlueAhuizoteSongs.add(btnLoadDefaultSongs, "4, 2");
		
		btnNewSong = new JButton("New song");
		frmBlueAhuizoteSongs.add(btnNewSong, "6, 2");
		
		btnEdit = new JButton("Edit");
		frmBlueAhuizoteSongs.add(btnEdit, "10, 2");
		
		songList = new JList();
		songList.setCellRenderer(new SongListCellRenderer());
		songList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		songList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					// show song in textpane
					JList list = (JList)e.getSource();
					actualSong = (Song) list.getSelectedValue();
					showSong(actualSong, textPane);
					checkChords();
				}
			}
		});
		
		btnSimPedal = new JButton("Sim Pedal");
		btnSimPedal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				simPedalPressed();
			}
		});
		frmBlueAhuizoteSongs.add(btnSimPedal, "6, 4");
		
		btnPlay = new JButton("Play");
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if ("Stop".equals(((JButton)e.getSource()).getText())) {
					stopSong();
				} else {
					playSong();
				}
			}
		});
		frmBlueAhuizoteSongs.add(btnPlay, "10, 4");
		frmBlueAhuizoteSongs.add(songList, "4, 6, fill, fill");
		
		JPanel panel = new JPanel();
		frmBlueAhuizoteSongs.add(panel, "10, 6, fill, fill");
		
		textPane = new JTextPane();
		textPane.setEditable(false);
		panel.add(textPane);
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
			unmarkCurrent(oldChordHint, oldLineHint, lineHint);
			markCurrent(chordHint, lineHint);
			
			// TODO call IOIO to change servos
			
		} else {
			// end of song
			stopIt(oldChordHint, oldLineHint);
		}
		
	}

	/**
	 * Checks, if all the chords in the song exists in chordManager (set of chord libraries)
	 */
	protected void checkChords() {
		ChordManager manager = mainFrame.getChordManager();
		for (PositionHint hint : hints.getChords()) {
			ChordRef ref = hint.getChordRef();
			String libraryName = Chord.getLibraryName(ref.getChordId());
			String chordName = Chord.getChordName(ref.getChordId());
			ChordLibrary library = manager.getChordLibraries().get(libraryName);
			if (library != null) {
				Chord chord = library.findByName(chordName);
				if (chord != null) {
					ref.setChord(chord);
					continue;
				}
			}
			markMissing(hint);
			missingChords = true;
			
		}
		btnPlay.setEnabled(!missingChords);
		// first press Play
		btnSimPedal.setEnabled(false); 
		// TODO not implemented yet
		btnEdit.setEnabled(false);
		// TODO not implemented yet
		btnNewSong.setEnabled(false);
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
	}

	/**
	 * Unselect current chord and line.
	 * @param chordHint
	 * @param lineHint
	 * @param newLineHint
	 */
	protected void unmarkCurrent(PositionHint chordHint, PositionHint lineHint, PositionHint newLineHint) {
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
		btnPlay.setText("Stop");
		btnSimPedal.setEnabled(true);
		// select the first chord and line
		hints.setCurrentChord(0);
		hints.setCurrentLine(0);
		PositionHint chordHint = hints.getChordHint();
		PositionHint lineHint = hints.getLineHint(chordHint);
		markCurrent(chordHint, lineHint);
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
		unmarkCurrent(oldChordHint, oldLineHint, null);
		btnPlay.setText("Play Song");
		btnSimPedal.setEnabled(false);
		
		// TODO call to release servos
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
	    StyleConstants.setFontSize(mainStyle, 12);
	    
	    Style chordStyle = sc.addStyle(CHORD_STYLE, mainStyle);
		
	    Style markedStyle = sc.addStyle(MARKED_STYLE, mainStyle);
		StyleConstants.setForeground(markedStyle, Color.BLUE);
		StyleConstants.setBackground(markedStyle, Color.YELLOW);

		Style markedChordStyle = sc.addStyle(MARKED_CHORD_STYLE, mainStyle);
		StyleConstants.setForeground(markedChordStyle, Color.BLUE);
		StyleConstants.setBackground(markedChordStyle, Color.YELLOW);

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
		setupStyles(pane);
		StyledDocument doc = pane.getStyledDocument();
		Style titleStyle = doc.getStyle(TITLE_STYLE);
		Style chordStyle = doc.getStyle(CHORD_STYLE);
		Style mainStyle = doc.getStyle(MAIN_STYLE);
		
		hints = new PositionHints();
		try {
			// title and interpret line
			doc.insertString(0, song.getTitle() + "     " + song.getInterpret() + "\n", titleStyle);
			
			// process parts
			for (Part part : song.getParts()) {
				doc.insertString(doc.getLength(), "\n", null);
				
				int lineNum = 0;
				for (Line line : part.getLines()) {
					doc.insertString(doc.getLength(), formatChords(line, hints, lineNum, doc.getLength()), chordStyle);

					int lineOffset = doc.getLength();
					doc.insertString(doc.getLength(), line.getText() + "\n", mainStyle);
					PositionHint lineHint = new PositionHint();
					lineHint.setLine(lineNum);
					lineHint.setOffset(lineOffset);
					lineHint.setLength(line.getText().length());
					hints.getLines().add(lineHint);
					
					lineNum++;
				}
	            
			}
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

}
