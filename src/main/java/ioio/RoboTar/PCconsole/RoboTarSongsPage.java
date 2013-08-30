package ioio.RoboTar.PCconsole;

import java.awt.BorderLayout;
import java.awt.EventQueue;

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
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.ListSelectionModel;

public class RoboTarSongsPage extends JFrame {

	private JPanel frmBlueAhuizoteSongs;
	private JTextPane textPane; 
	private Song actualSong;
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
				}
			}
		});
		
		JButton btnSimPedal = new JButton("Sim Pedal");
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
		panel.add(textPane);
	}

	protected void playSong() {
		// first check the chords
	}

	/**
	 * Prepare song for display
	 * @param song
	 * @param pane
	 */
	protected void showSong(Song song, JTextPane pane) {
		SimpleAttributeSet chordStyle = new SimpleAttributeSet();
		StyleConstants.setForeground(chordStyle, Color.BLACK);
		StyleConstants.setBackground(chordStyle, Color.WHITE);
		StyleConstants.setBold(chordStyle, true);
		StyleConstants.setFontFamily(chordStyle, "courier");
		StyleConstants.setFontSize(chordStyle, 12);
		
		SimpleAttributeSet textStyle = new SimpleAttributeSet();
		StyleConstants.setForeground(textStyle, Color.BLACK);
		StyleConstants.setBackground(textStyle, Color.WHITE);
		StyleConstants.setBold(textStyle, true);
		StyleConstants.setFontFamily(textStyle, "courier");
		StyleConstants.setFontSize(textStyle, 12);
		
		SimpleAttributeSet markedStyle = new SimpleAttributeSet();
		StyleConstants.setForeground(markedStyle, Color.RED);
		StyleConstants.setBackground(markedStyle, Color.YELLOW);
		StyleConstants.setBold(markedStyle, true);
		StyleConstants.setFontFamily(markedStyle, "monospaced");
		StyleConstants.setFontSize(markedStyle, 20);
		
		SimpleAttributeSet titleStyle = new SimpleAttributeSet();
		StyleConstants.setForeground(titleStyle, Color.BLACK);
		StyleConstants.setBackground(titleStyle, Color.WHITE);
		StyleConstants.setBold(titleStyle, true);
		StyleConstants.setFontSize(titleStyle, 18);
		
		StyledDocument doc = pane.getStyledDocument();
		try {
			// title and interpret line
			doc.insertString(0, song.getTitle() + "     " + song.getInterpret() + "\n", titleStyle);
			
			// process parts
			for (Part part : song.getParts()) {
				doc.insertString(doc.getLength(), "\n", null);
				
				for (Line line : part.getLines()) {
					doc.insertString(doc.getLength(), formatChords(line), chordStyle);
					doc.insertString(doc.getLength(), line.getText() + "\n", textStyle);
				}
				doc.setParagraphAttributes(0, 1, markedStyle, true);
			}
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Will position chords to the proper places.
	 * first position is 1, not 0!
	 * 
	 * @param line
	 * @return
	 */
	private String formatChords(Line line) {
		StringBuilder str = new StringBuilder();
		
		for (ChordRef ref : line.getChords()) {
			int end = str.length() + 1;
			int position = ref.getPosition();
			if (end < position) {
				for (int i = 0; i< position-end; i++) {
					str.append(" ");
				}
			}
			str.append(ref.getChordId().split("-")[1]);	// TODO very specific to current format, future change?
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
