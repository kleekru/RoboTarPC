package ioio.RoboTar.PCconsole;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.Color;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

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
import javax.swing.text.Position;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.ListSelectionModel;

public class RoboTarSongsPage extends JFrame {

	private JPanel frmBlueAhuizoteSongs;
	private JTextPane textPane; 
	private Song actualSong;
	// where are chords and lines in the text pane - to be able to select them during play
	private PositionHints hints;
	private JList songList;
	private DefaultListModel songListModel;
	
	/**
	 * Launch the application.
	 */
	public static void startSongsPage(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RoboTarSongsPage frame = new RoboTarSongsPage();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public RoboTarSongsPage() {
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
		
		JButton btnLoadDefaultSongs = new JButton("Load default songs");
		btnLoadDefaultSongs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				XMLSongLoader loader = new XMLSongLoader();
				Song song = loader.load(RoboTarChordsPage.class.getResourceAsStream("/default-songs/greensleeves.xml"));
				// other songs..
				songListModel = new DefaultListModel();
				songListModel.addElement(song);
				actualSong = (Song) songListModel.get(0);
				songList.setModel(songListModel);
			}
		});
		frmBlueAhuizoteSongs.add(btnLoadDefaultSongs, "4, 2");
		
		JButton btnNewSong = new JButton("New song");
		frmBlueAhuizoteSongs.add(btnNewSong, "6, 2");
		
		JButton btnEdit = new JButton("Edit");
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
		
		JButton btnSimPedal = new JButton("Sim Pedal");
		btnSimPedal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				simPedalPressed();
			}
		});
		frmBlueAhuizoteSongs.add(btnSimPedal, "6, 4");
		
		JButton btnPlay = new JButton("Play");
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				playSong();
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

	protected void simPedalPressed() {
		PositionHint oldChordHint = hints.getChordHint();
		PositionHint oldLineHint = hints.getLineHint(oldChordHint);
		
		PositionHint chordHint = hints.getNextChordHint();
		if (chordHint != null) {
			PositionHint lineHint = hints.getLineHint(chordHint);
			unmarkCurrent(oldChordHint, oldLineHint, lineHint);
			markCurrent(chordHint, lineHint);
		} else {
			// end of song
			unmarkCurrent(oldChordHint, oldLineHint, null);
		}
		
	}

	protected void checkChords() {
		// TODO
	}
	
	protected void markCurrent(PositionHint chordHint, PositionHint lineHint) {
		StyleContext sc = new StyleContext();
		final Style markedStyle = sc.addStyle("MarkedStyle", null);
		StyleConstants.setForeground(markedStyle, Color.BLUE);
		StyleConstants.setBackground(markedStyle, Color.YELLOW);

		final Style markedChordStyle = sc.addStyle("MarkedChordStyle", null);
		StyleConstants.setForeground(markedChordStyle, Color.RED);
		StyleConstants.setBackground(markedChordStyle, Color.YELLOW);

		// select the first chord and line
		StyledDocument doc = textPane.getStyledDocument();
		doc.getStyle("ChordStyle");
		
		doc.setCharacterAttributes(lineHint.getOffset(), lineHint.getLength(), markedStyle, false);
		doc.setCharacterAttributes(chordHint.getOffset(), chordHint.getLength(), markedChordStyle, false);

	}

	protected void unmarkCurrent(PositionHint chordHint, PositionHint lineHint, PositionHint newLineHint) {
		StyledDocument doc = textPane.getStyledDocument();
		Style chordStyle = doc.getStyle("ChordStyle");
		Style mainStyle = doc.getStyle("MainStyle");

		doc.setCharacterAttributes(chordHint.getOffset(), chordHint.getLength(), chordStyle, true);
		if (newLineHint == null || lineHint != newLineHint) {
			doc.setCharacterAttributes(lineHint.getOffset(), lineHint.getLength(), mainStyle, true);
		}

	}

	protected void playSong() {
		// select the first chord and line
		hints.setCurrentChord(0);
		hints.setCurrentLine(0);
		PositionHint chordHint = hints.getChordHint();
		PositionHint lineHint = hints.getLineHint(chordHint);
		markCurrent(chordHint, lineHint);
	}

	/**
	 * Prepare song for display
	 * @param song
	 * @param pane
	 */
	protected PositionHints showSong(Song song, JTextPane pane) {
		// prepare styles
		StyleContext sc = new StyleContext();
		Style defaultStyle = sc.getStyle(StyleContext.DEFAULT_STYLE);

		final Style mainStyle = sc.addStyle("MainStyle", defaultStyle);
	    StyleConstants.setForeground(mainStyle, Color.BLACK);
	    StyleConstants.setBackground(mainStyle, Color.WHITE);
	    StyleConstants.setBold(mainStyle, true);
	    StyleConstants.setFontFamily(mainStyle, "monospaced");
	    StyleConstants.setFontSize(mainStyle, 12);
	    
	    final Style chordStyle = sc.addStyle("ChordStyle", mainStyle);
		
	    final Style markedStyle = sc.addStyle("MarkedStyle", null);
		StyleConstants.setForeground(markedStyle, Color.BLUE);
		StyleConstants.setBackground(markedStyle, Color.YELLOW);

	    final Style titleStyle = sc.addStyle("TitleStyle", defaultStyle);
		StyleConstants.setForeground(titleStyle, Color.BLACK);
		StyleConstants.setBackground(titleStyle, Color.WHITE);
		StyleConstants.setBold(titleStyle, true);
		StyleConstants.setFontSize(titleStyle, 18);
		
		StyledDocument doc = pane.getStyledDocument();
		doc.addStyle("ChordStyle", chordStyle);
		doc.addStyle("MainStyle", mainStyle);
		
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
				//doc.setCharacterAttributes(10, 20, markedStyle, false);
	            
			}
		} catch (BadLocationException e1) {
			e1.printStackTrace();
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
			// TODO very specific to current format, future change?
			String chordName = ref.getChordId().split("-")[1];
			
			PositionHint chordHint = new PositionHint();
			chordHint.setOffset(lineOffset + position - 1);
			chordHint.setLength(chordName.length());
			chordHint.setLine(lineNum);
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
