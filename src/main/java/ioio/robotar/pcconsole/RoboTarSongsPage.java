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

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Component;
import java.awt.Rectangle;

import javax.swing.SwingConstants;

import java.io.File;
import java.io.FileNotFoundException;

public class RoboTarSongsPage extends JFrame implements WindowListener {
	private static final long serialVersionUID = -7862830927381806488L;
	static final Logger LOG = LoggerFactory.getLogger(RoboTarSongsPage.class);
	
	private JPanel frmBlueAhuizoteSongs;
	
	private static int counter = 1;
	
	private Song actualSong;
	private boolean playing;
	private boolean editing;
	private boolean missingChords;
	private boolean defaultSongsLoaded;
	/** probably something modified. we cannot compare songs for diffs. */
	private int modifiedCount;
	
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
	private RoboTarPreferences pref;
	
	/** editing properties */
	private boolean editMarkerDisplayed;
	private int editMarkerPosition;
	private JPanel songPanel;
	
	/**
	 * Create the frame.
	 */
	public RoboTarSongsPage(final RoboTarStartPage mainFrame) {
		this.mainFrame = mainFrame;
		messages = mainFrame.getMessages();

		setBounds(100, 100, 1200, 510);
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
		chordList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					usedChordSelected(e);
				}
				
			}
		});
		songPanel = new JPanel(new BorderLayout());
		songPanel.setBackground(Color.WHITE);
		GridBagConstraints gbc_songPanel = new GridBagConstraints();
		gbc_songPanel.gridwidth = 2;
		gbc_songPanel.fill = GridBagConstraints.BOTH;
		gbc_songPanel.gridx = 3;
		gbc_songPanel.gridy = 2;
		gbc_songPanel.weightx = 1.0;
		gbc_songPanel.weighty = 1.0;
		frmBlueAhuizoteSongs.add(songPanel, gbc_songPanel);
		
		textPane = new JTextPane(new RefreshDocument());
		textPane.setEditable(false);
		scrollPane = new JScrollPane(textPane);
		scrollPane.setPreferredSize(new Dimension(750, 370));
		songPanel.add(scrollPane);
		setupStyles(textPane);
		
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		addWindowListener(this);
		
		loadRecent(mainFrame.getPreferences());
		
		// delete chords
		songList.registerKeyboardAction(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultListModel model = (DefaultListModel) songList.getModel();
				int selectedIndex = songList.getSelectedIndex();
				if (selectedIndex != -1) {
				    model.remove(selectedIndex);
				    repaint();
				}
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), JComponent.WHEN_FOCUSED);
				
		initEditing();
		pack();
		//setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        
		setLocationByPlatform(true);
		setVisible(true);
	}

	/** big attempt */
	private void initEditing() {
		initKeys();
		initCaret();
	}
	
	private void initKeys() {
		//textPane.registerEditorKitForContentType  later.... proper way of handling whole song editing.. 
		//LOG.info("kit {}", textPane.getEditorKit());
		//textPane.get
		// left keystroke
		textPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "left");
		textPane.getActionMap().put("left", new LeftAction());
		// right keystroke
		textPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "right");
		textPane.getActionMap().put("right", new RightAction());
		// up keystroke
		textPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "up");
		textPane.getActionMap().put("up", new UpAction());
		// down keystroke
		textPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "down");
		textPane.getActionMap().put("down", new DownAction());
		// space keystroke
		textPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "space");
		textPane.getActionMap().put("space", new SpaceAction());
		// backspace keystroke
		textPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "backspace");
		textPane.getActionMap().put("backspace", new BackspaceAction());
		// delete keystroke
		textPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "deletechord");
		textPane.getActionMap().put("deletechord", new DeleteChordAction());
		// text keystroke - f2?
		textPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "textedit");
		textPane.getActionMap().put("textedit", new TextEditAction());
		
		
	}
	
	private void initCaret() {
		textPane.addCaretListener(new CaretListener() {
			String newline = "\n";
			@Override
			public void caretUpdate(CaretEvent e) {
				int dot = e.getDot();
			    int mark = e.getMark();
			    if (dot == mark) {  // no selection
			        try {
			            Rectangle caretCoords = textPane.modelToView(dot);
			            //Convert it to view coordinates
			            LOG.info("caret: text position: " + dot +
			                    ", view location = [" +
			                    caretCoords.x + ", " + caretCoords.y + "]" +
			                    newline);
			        } catch (BadLocationException ble) {
			            LOG.info("caret: text position: " + dot + newline);
			        }
			     } else if (dot < mark) {
			        LOG.info("selection from: " + dot + " to " + mark + newline);
			     } else {
			        LOG.info("selection from: " + mark + " to " + dot + newline);
			     }
				
			}
			
		});
	}
	
	private void loadRecent(RoboTarPreferences pref) {
		this.pref = pref;
		XMLSongLoader loader = new XMLSongLoader();
		initSongModel();
		for (String fileName : pref.getSongs()) {
			File file = new File(fileName);
			Song song = loader.loadSong(file);
			if (song != null) {
				songListModel.add(songListModel.getSize(), song);
			}
		}
		// TODO sort?
	}

	private void saveRecent() {
		pref.setSongs(getSongsList(songListModel.elements()));
	}
	
	/** generate list of used songs without robotar-default-songs. */
	public List<String> getSongsList(Enumeration<Song> songs) {
		List<String> list = new ArrayList<String>();
		while (songs.hasMoreElements()) {
			Song song = songs.nextElement();
			if (!song.isRobotarDefault()) {
				list.add(song.getPath());
			}
		}
		return list;
	}
	
	private void initSongModel() {
		if (songListModel == null) {
			songListModel = new DefaultListModel();
			songList.setModel(songListModel);
		}
	}
	
	protected void loadSong() throws FileNotFoundException {
		JFileChooser fc = new JFileChooser();
		int returnValue = fc.showOpenDialog(this);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            XMLSongLoader loader = new XMLSongLoader();
            Song song = loader.loadSong(file);
            if (song == null) {
            	return ;
            }
            song.setPath(file.getPath());
            initSongModel();
            songListModel.add(0, song);
            saveRecent();
		}
	}

	protected void saveSong() {
		if (actualSong == null) {
			JOptionPane.showMessageDialog(RoboTarSongsPage.this, messages.getString("robotar.songs.nothing_to_save"));
			return;
		}
		if (!actualSong.isRobotarDefault() && actualSong.getPath() != null) {
			// default files may be saved into other file
			File file = new File(actualSong.getPath());
			if (actualSong.isChanged()) {
            	actualSong.setChanged(false);
            	modifiedCount--;
            }
            XMLSongSaver saver = new XMLSongSaver();
            saver.save(actualSong, file);
		} else { 
			// choose where
			JFileChooser fc = new JFileChooser();
			int returnValue = fc.showSaveDialog(this);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
	            File file = fc.getSelectedFile();
	            if (actualSong.isChanged()) {
	            	actualSong.setChanged(false);
	            	modifiedCount--;
	            }
	            XMLSongSaver saver = new XMLSongSaver();
	            saver.save(actualSong, file);
	            // for new songs, just saved for the first time:
	            actualSong.setPath(file.getPath());
	            saveRecent();
	            repaint();
			}
		}
	}

	protected void loadDefaultSongs() {
		XMLSongLoader loader = new XMLSongLoader();
		Song song = loader.loadSong(RoboTarChordsPage.class.getResourceAsStream("/default-songs/greensleeves.xml"));
		song.setRobotarDefault(true);
		// load other songs..
		
		initSongModel();
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
		DefaultListModel model = (DefaultListModel) list.getModel();
		int selIdx = (int) list.getSelectedIndex();
		if (selIdx < 0) {
			// if nothing selected, select the first or disable song panel
			if (model.getSize() > 0) {
				list.setSelectedIndex(0);
			} else {
				clearPane(textPane);
				actualSong = null;
				btnEditSong.setEnabled(false);
				btnPlay.setEnabled(false);
			}
		} else {
			// select song name and display it in song panel
			Song song = (Song) model.get(selIdx);
			actualSong = (Song) list.getSelectedValue();
			prepareForPlaying();
			btnEditSong.setEnabled(true);
			btnNewSong.setEnabled(true);
		}
	}
	
	protected void usedChordSelected(ListSelectionEvent e) {
		// if in editing mode, do something, otherwise nothing
		if (editing) {
			// some chord must be chosen
			PositionHint lastHint = hints.getLastSelectedChordHint();
			if (!editMarkerDisplayed && lastHint != null) {
				// get the selection
				JList list = (JList)e.getSource();
				DefaultListModel model = (DefaultListModel) list.getModel();
				int selIdx = (int) list.getSelectedIndex();
				if (selIdx >= 0) {
					// compare them (this event comes also from normal traversal)
					Chord selChord = (Chord)chordList.getSelectedValue();
					Chord last = lastHint.getChordRef().getChord();
					if (!selChord.equals(last)) {
						// if the values differ, it means, user clicked in the used chord list and changed the chord
						LOG.info("changing {}, {}", last.getId(), selChord.getId());
						//int pos = lastHint.getChordRef().getPosition();
						int oldLength = lastHint.getChordRef().getChord().getName().length();
						int newLength = selChord.getName().length();
						textPane.requestFocusInWindow();
						
						// through references, set it in underlying Song object
						lastHint.getChordRef().setChordId(selChord.getId());
						lastHint.getChordRef().setChord(selChord);
						
						// process rest of chord names on the same line!
						int diff = newLength - oldLength;
						if (diff != 0) {
							hints.moveChords(diff, lastHint);
						}
						// add to used (map)
						actualSong.getUsedChords().add(selChord);
						//RefreshDocument rdoc = (RefreshDocument)doc;
						//rdoc.refresh();
						//textPane.repaint();
						// recreate content of textPane
						reshowSong(actualSong, textPane, hints);
						// scroll to previous place
						scrollTo(lastHint.getOffset());
						// markup the chosen chord, newly created, from old info with new length
						lastHint.setLength(newLength);
						markCurrentEditedChord(lastHint);
						// set new hint as last (new structure)
						PositionHint newHint = hints.findChordBefore(lastHint.getOffset()+1);
						hints.setLastSelectedChord(newHint);
					}
				}
			}
		}
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
		int last = textPane.getStyledDocument().getLength();
		textPane.setCaretPosition(last);
		createMarker(last);
		textPane.requestFocusInWindow();
		if (!actualSong.isChanged()) {
			actualSong.setChanged(true);
			modifiedCount++;
		}
	}

	protected void createMarker(int position) {
		StyledDocument doc = textPane.getStyledDocument();
		
		Style markerStyle = doc.getStyle(Styles.MISSING_CHORD_STYLE);
		try {
			scrollTo(position);
			doc.insertString(position, "*", markerStyle);
			editMarkerDisplayed = true;
			editMarkerPosition = position;
			btnAddChord.setEnabled(editMarkerDisplayed);
			btnNewLine.setEnabled(editMarkerDisplayed);
			btnNewVerse.setEnabled(editMarkerDisplayed);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	protected void removeMarker() {
		StyledDocument doc = textPane.getStyledDocument();
		try {
			if (editMarkerDisplayed) { // to be sure
				doc.remove(editMarkerPosition, 1);
			}
			editMarkerDisplayed = false;
			btnAddChord.setEnabled(editMarkerDisplayed);
			btnNewLine.setEnabled(editMarkerDisplayed);
			btnNewVerse.setEnabled(editMarkerDisplayed);
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
		pruneNotUsedChords();
		prepareForPlaying();
	}
	
	private void pruneNotUsedChords() {
		ChordBag usedChordBag = new ChordBag();
		int partsSize = actualSong.getParts().size();
		if (partsSize <= 0) return;
		for (Part part : actualSong.getParts()) {
			// check empty lines
			int linesSize = part.getLines().size();
			for (Line line : part.getLines()) {
				if (line.getChords() != null) {
					for (ChordRef ref : line.getChords()) {
						usedChordBag.add(ref.getChord());
					}
				}
			}
		}
		// set used chords - because after editing, there may be chords, which are not used 
		actualSong.setUsedChords(usedChordBag);
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
		
		Style mainStyle = doc.getStyle(Styles.MAIN_STYLE);
		try {
			doc.insertString(position, "\n", mainStyle);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	protected void addNewVerseToSong() {
		StyledDocument doc = textPane.getStyledDocument();
		int position = doc.getLength() - 1;
		
		Style mainStyle = doc.getStyle(Styles.MAIN_STYLE);
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
			
			Style chordStyle = doc.getStyle(Styles.CHORD_STYLE);
			Style mainStyle = doc.getStyle(Styles.MAIN_STYLE);
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
			
			initSongModel();
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
		PositionHint oldLineHint = hints.getLineHintCurr(oldChordHint);
		
		PositionHint chordHint = hints.getNextChordHint();
		if (chordHint != null) {
			// visual handling
			PositionHint lineHint = hints.getLineHintCurr(chordHint);
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
					// TODO - compare the two.. now left the one in library as is...
					LOG.debug("should check content equality of chords - ? {}, {}", libraryName, chordName);
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
				
				if (libraryName.startsWith(ChordManager.USER_PREFIX)) {
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
		Style missingChordStyle = doc.getStyle(Styles.MISSING_CHORD_STYLE);
		doc.setCharacterAttributes(chordHint.getOffset(), chordHint.getLength(), missingChordStyle, false);
	}
	
	/**
	 * Select current chord and line.
	 * @param chordHint
	 * @param lineHint
	 */
	protected void markCurrent(PositionHint chordHint, PositionHint lineHint) {
		StyledDocument doc = textPane.getStyledDocument();
		Style markedStyle = doc.getStyle(Styles.MARKED_STYLE);
		Style markedChordStyle = doc.getStyle(Styles.MARKED_CHORD_STYLE);
		Style markedChordLineStyle = doc.getStyle(Styles.MARKED_CHORD_LINE_STYLE);

		// active text line
		doc.setCharacterAttributes(lineHint.getOffset(), lineHint.getLength(), markedStyle, false);
		// active chord line
		PositionHint first = hints.firstChordOnLine(chordHint);
		int startingOffset = first.getOffset() - first.getChordRef().getPosition() + 1;
		//doc.setCharacterAttributes(lineHint.getOffset() - lineHint.getLength(), lineHint.getLength(), markedChordLineStyle, false);
		doc.setCharacterAttributes(startingOffset, lineHint.getOffset() - startingOffset - 1, markedChordLineStyle, false);
		// active chord
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
		Style chordStyle = doc.getStyle(Styles.CHORD_STYLE);
		Style mainStyle = doc.getStyle(Styles.MAIN_STYLE);

		doc.setCharacterAttributes(chordHint.getOffset(), chordHint.getLength(), chordStyle, true);
		if (newLineHint == null || lineHint != newLineHint) {
			// unmark line
			doc.setCharacterAttributes(lineHint.getOffset(), lineHint.getLength(), mainStyle, true);
			// unmark chord line
			PositionHint first = hints.firstChordOnLine(chordHint);
			int startingOffset = first.getOffset() - first.getChordRef().getPosition() + 1;
			//doc.setCharacterAttributes(lineHint.getOffset() - lineHint.getLength(), lineHint.getLength(), mainStyle, true);
			doc.setCharacterAttributes(startingOffset, lineHint.getOffset() - startingOffset - 1, mainStyle, true);
		}

	}

	/**
	 * Start to play a song.
	 */
	protected void playSong() {
		if (actualSong == null) {
			JOptionPane.showMessageDialog(RoboTarSongsPage.this, messages.getString("robotar.songs.nothing_to_play"));
			return;
		}
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
		PositionHint oldLineHint = hints.getLineHintCurr(oldChordHint);
		
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

		Style mainStyle = sc.addStyle(Styles.MAIN_STYLE, null);
	    StyleConstants.setForeground(mainStyle, Color.BLACK);
	    StyleConstants.setBackground(mainStyle, Color.WHITE);
	    StyleConstants.setBold(mainStyle, true);
	    StyleConstants.setFontFamily(mainStyle, "monospaced");
	    StyleConstants.setFontSize(mainStyle, mainFrame.getPreferences().getMainSize());
	    
	    
	    Style chordStyle = sc.addStyle(Styles.CHORD_STYLE, mainStyle);
		
	    Style markedStyle = sc.addStyle(Styles.MARKED_STYLE, mainStyle);
	    StyleConstants.setForeground(markedStyle, mainFrame.getPreferences().getMarkedColor());
		StyleConstants.setBackground(markedStyle, Color.YELLOW);
		StyleConstants.setFontSize(markedStyle, mainFrame.getPreferences().getMarkedSize());
	    
		Style markedChordStyle = sc.addStyle(Styles.MARKED_CHORD_STYLE, mainStyle);
		StyleConstants.setForeground(markedChordStyle, mainFrame.getPreferences().getMarkedChordColor());
		StyleConstants.setBackground(markedChordStyle, Color.YELLOW);
		StyleConstants.setFontSize(markedChordStyle, mainFrame.getPreferences().getMarkedChordSize());

		Style markedChordLineStyle = sc.addStyle(Styles.MARKED_CHORD_LINE_STYLE, mainStyle);
		StyleConstants.setFontSize(markedChordLineStyle, mainFrame.getPreferences().getMarkedChordSize());

		Style missingChordStyle = sc.addStyle(Styles.MISSING_CHORD_STYLE, mainStyle);
		StyleConstants.setForeground(missingChordStyle, Color.RED);
		StyleConstants.setBackground(missingChordStyle, Color.WHITE);

	    Style titleStyle = sc.addStyle(Styles.TITLE_STYLE, defaultStyle);
		StyleConstants.setForeground(titleStyle, Color.BLACK);
		StyleConstants.setBackground(titleStyle, Color.WHITE);
		StyleConstants.setBold(titleStyle, true);
		StyleConstants.setFontSize(titleStyle, 18);
		
		Style editMarkedChordStyle = sc.addStyle(Styles.EDIT_MARKED_CHORD_STYLE, mainStyle);
		StyleConstants.setForeground(editMarkedChordStyle, mainFrame.getPreferences().getEditMarkedChordColor());
		
		Style editMarkedLineStyle = sc.addStyle(Styles.EDIT_MARKED_LINE_STYLE, mainStyle);
		StyleConstants.setForeground(editMarkedLineStyle, Color.GREEN);
		
		StyledDocument doc = pane.getStyledDocument();
		doc.addStyle(Styles.CHORD_STYLE, chordStyle);
		doc.addStyle(Styles.MAIN_STYLE, mainStyle);
		doc.addStyle(Styles.TITLE_STYLE, titleStyle);
		doc.addStyle(Styles.MARKED_STYLE, markedStyle);
		doc.addStyle(Styles.MARKED_CHORD_STYLE, markedChordStyle);
		doc.addStyle(Styles.MARKED_CHORD_LINE_STYLE, markedChordLineStyle);
		doc.addStyle(Styles.MISSING_CHORD_STYLE, missingChordStyle);
		doc.addStyle(Styles.EDIT_MARKED_CHORD_STYLE, editMarkedChordStyle);
		doc.addStyle(Styles.EDIT_MARKED_LINE_STYLE, editMarkedLineStyle);
	}

	protected void clearPane(JTextPane pane) {
		// clear pane
		StyledDocument doc = pane.getStyledDocument();
		try {
			doc.remove(0, doc.getLength());
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Prepare song for display
	 * @param song
	 * @param pane
	 */
	protected PositionHints showSong(Song song, JTextPane pane) {
		// prepare styles
		StyledDocument doc = pane.getStyledDocument();
		Style titleStyle = doc.getStyle(Styles.TITLE_STYLE);
		Style chordStyle = doc.getStyle(Styles.CHORD_STYLE);
		Style mainStyle = doc.getStyle(Styles.MAIN_STYLE);
		
		hints = new PositionHints();
		try {
			// clear pane
			doc.remove(0, doc.getLength());
			
			// title and interpret line
			doc.insertString(0, song.getTitle() + "     " + song.getInterpret() + "\n", titleStyle);
			
			// process parts
			int lineNum = 0;
			for (Part part : song.getParts()) {
				doc.insertString(doc.getLength(), "\n", null);
				
				for (Line line : part.getLines()) {
					if (!line.hasAnyChords()) {
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
			//scrollTo(0); 
		} catch (BadLocationException e1) {
			e1.printStackTrace(); //TODO
		}
		return hints;
	}

	/** 
	 * this is used, because no update is working. 
	 * we have to throw out textpane content and build it back.  :/
	 * TODO merge with showsong in future
	 * or find out other style (editorkit?)
	 * 
	 * @param song
	 * @param pane
	 * @param hints
	 * @return
	 */
	protected PositionHints reshowSong(Song song, JTextPane pane, PositionHints hints) {
		// prepare styles
		StyledDocument doc = pane.getStyledDocument();
		Style titleStyle = doc.getStyle(Styles.TITLE_STYLE);
		Style chordStyle = doc.getStyle(Styles.CHORD_STYLE);
		Style mainStyle = doc.getStyle(Styles.MAIN_STYLE);
		
		hints.getChords().clear();
		hints.getLines().clear();
		try {
			// clear pane
			doc.remove(0, doc.getLength());
			
			// title and interpret line
			doc.insertString(0, song.getTitle() + "     " + song.getInterpret() + "\n", titleStyle);
			
			// process parts
			int lineNum = 0;
			for (Part part : song.getParts()) {
				doc.insertString(doc.getLength(), "\n", null);
				
				for (Line line : part.getLines()) {
					if (!line.hasAnyChords()) {
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
		StringBuilder sb = new StringBuilder();
		for (ChordRef ref : line.getChords()) {
			int end = sb.length() + 1;
			int position = ref.getPosition();
			if (end < position) {
				for (int i = 0; i< position-end; i++) {
					sb.append(" ");
				}
			}
			String chordName = Chord.getChordName(ref.getChordId());
			
			PositionHint chordHint = new PositionHint();
			chordHint.setOffset(lineOffset + position - 1);
			chordHint.setLength(chordName.length());
			chordHint.setLine(lineNum);
			chordHint.setChordRef(ref);
			hints.getChords().add(chordHint);
			
			sb.append(chordName);	
		}
		
		if (line.getText() == null) {
			line.setText(" ");
		}
		
		sb.append("\n");
		return sb.toString();
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

	public int getModifiedCount() {
		return modifiedCount;
	}

	public void setModifiedCount(int modified) {
		this.modifiedCount = modified;
	}
	
	/**
	 * Select current chord 
	 * @param chordHint
	 * @param lineHint
	 */
	protected void markCurrentEditedChord(PositionHint chordHint) {
		StyledDocument doc = textPane.getStyledDocument();
		Style editMarkedChordStyle = doc.getStyle(Styles.EDIT_MARKED_CHORD_STYLE);
		// active chord
		doc.setCharacterAttributes(chordHint.getOffset(), chordHint.getLength(), editMarkedChordStyle, false);
		// select in used chords list
		chordList.setSelectedValue(chordHint.getChordRef().getChord(), true);
	}

	/**
	 * Unselect current chord and line.
	 * @param chordHint
	 * @param lineHint
	 * @param newLineHint
	 */
	protected void unmarkCurrentEditedChord(PositionHint chordHint) {
		// unselect in used chords list
		chordList.clearSelection();
		/*if (chordHint == null) {
			// not even first chord displayed
			return;
		}*/
		StyledDocument doc = textPane.getStyledDocument();
		Style chordStyle = doc.getStyle(Styles.CHORD_STYLE);
		doc.setCharacterAttributes(chordHint.getOffset(), chordHint.getLength(), chordStyle, true);
	}

	/////////////////////////////
	// editing actions
	class LeftAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!editing) {
				return;
			}
			
			if (editMarkerDisplayed) {
				// B section 
				// check if there are any chords in song
				//if (!actualSong.hasAnyChords()) {
					//return;
				//}
				// the same condition as above
				if (hints.getChords().size() == 0) {
					return;
				}
				if (hints.getLastSelectedChordHint() != null) {
					// hide *
					removeMarker();
					PositionHint lastChordHint = hints.getLastSelectedChordHint();
					// mark the chord
					markCurrentEditedChord(lastChordHint);
					// scroll to
					int ensurePrevTextLineVisible = hints.getPrevLineOffset(lastChordHint, 0);
					scrollTo(ensurePrevTextLineVisible);
				} else {
					// find last chord before current position
					PositionHint prevChordHint = hints.findChordBefore(editMarkerPosition);
					if (prevChordHint == null) {
						// at the beginning, with * -> nothing
						return;
					} else {
						hints.setLastSelectedChord(prevChordHint);
						removeMarker();
						markCurrentEditedChord(prevChordHint);
					}
				}
			} else {
				// A section
				PositionHint lastChordHint = hints.getLastSelectedChordHint();
				PositionHint prevChordHint = hints.getPrevChordHint(lastChordHint);
				if (prevChordHint != null) {
					// unmark
					hints.setLastSelectedChord(prevChordHint);
					unmarkCurrentEditedChord(lastChordHint);
					markCurrentEditedChord(prevChordHint);
					
					// scrollto
					int ensurePrevTextLineVisible = hints.getPrevLineOffset(prevChordHint, 0);
					scrollTo(ensurePrevTextLineVisible);
				}
			}
		}
	}
	
	class RightAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!editing) {
				return;
			}
			
			if (editMarkerDisplayed) {
				// B section 
				// check if there are any chords in song
				//if (!actualSong.hasAnyChords()) {
					//return;
				//}
				// the same condition as above
				if (hints.getChords().size() == 0) {
					return;
				}
				if (hints.getLastSelectedChordHint() != null) {
					// hide *
					removeMarker();
					PositionHint lastChordHint = hints.getLastSelectedChordHint();
					// mark the chord
					markCurrentEditedChord(lastChordHint);
					// scroll to
					int ensureTextLineVisible = hints.getNextLineOffset(lastChordHint, textPane.getStyledDocument().getLength());
					scrollTo(ensureTextLineVisible);
				} else {
					// find last chord after current position
					PositionHint nextChordHint = hints.findChordAfter(editMarkerPosition);
					if (nextChordHint == null) {
						// at the end, with * -> nothing
						return;
					} else {
						hints.setLastSelectedChord(nextChordHint);
						removeMarker();
						markCurrentEditedChord(nextChordHint);
					}
				}
			} else {
				// A section
				PositionHint lastChordHint = hints.getLastSelectedChordHint();
				PositionHint nextChordHint = hints.getNextChordHint(lastChordHint);
				if (nextChordHint != null) {
					// unmark
					hints.setLastSelectedChord(nextChordHint);
					unmarkCurrentEditedChord(lastChordHint);
					markCurrentEditedChord(nextChordHint);
					
					// scrollto
					int ensureTextLineVisible = hints.getNextLineOffset(nextChordHint, textPane.getStyledDocument().getLength());
					scrollTo(ensureTextLineVisible);
				} else {
					// go back to * insert style at the end only
					hints.setLastSelectedChord(null);
					unmarkCurrentEditedChord(lastChordHint);
					int last = textPane.getStyledDocument().getLength();
					createMarker(last);
					editMarkerDisplayed = true;
					editMarkerPosition = last;
				}
			}
		}
	}
	
	class UpAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!editing) {
				return;
			}
			
			if (editMarkerDisplayed) {
				return;
			}
			
			// select prev chord in used chords
			int selIdx = (int) chordList.getSelectedIndex();
			if (selIdx >= 0) {
				chordList.setSelectedIndex(--selIdx);
			}
	
		}
	}
	class DownAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!editing) {
				return;
			}
			
			if (editMarkerDisplayed) {
				return;
			}
			
			// select next chord in used chords
			int selIdx = (int) chordList.getSelectedIndex();
			if (selIdx < chordList.getModel().getSize() - 1) {
				chordList.setSelectedIndex(++selIdx);
			}
	
		}
	}
	class SpaceAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!editing) {
				return;
			}
			
			if (editMarkerDisplayed) {
				return;
			}
			
			PositionHint hint = hints.getLastSelectedChordHint();
			PositionHint nextHint = hints.nextChordOnLine(hint);
			int max = 0; 
			if (nextHint == null) {
				PositionHint lineHint = hints.getLineHint(hint);
				max = lineHint.getLength() - hint.getChordRef().getPosition() - hint.getChordRef().getChord().getName().length() + 1;
			} else {
				max = nextHint.getChordRef().getPosition() - hint.getChordRef().getPosition() - hint.getChordRef().getChord().getName().length() - 1;
			}
			if (max > 0) {
				// move by 1
				hint.getChordRef().setPosition(hint.getChordRef().getPosition() + 1);
				reshowSong(actualSong, textPane, hints);
				// set new hint as last (new structure)
				PositionHint newHint = hints.findChordBefore(hint.getOffset()+2);
				hints.setLastSelectedChord(newHint);
				markCurrentEditedChord(newHint);
			}
		}
	}
	
	class BackspaceAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!editing) {
				return;
			}
			
			if (editMarkerDisplayed) {
				return;
			}
			
			PositionHint hint = hints.getLastSelectedChordHint();
			PositionHint prevHint = hints.prevChordOnLine(hint);
			int max = 0; 
			if (prevHint == null) {
				max = hint.getChordRef().getPosition() - 1;
			} else {
				max = hint.getChordRef().getPosition() - prevHint.getChordRef().getPosition() - prevHint.getChordRef().getChord().getName().length() - 1;
			}
			if (max > 0) {
				// move by 1
				hint.getChordRef().setPosition(hint.getChordRef().getPosition() - 1);
				reshowSong(actualSong, textPane, hints);
				// set new hint as last (new structure)
				PositionHint newHint = hints.findChordBefore(hint.getOffset());
				hints.setLastSelectedChord(newHint);
				markCurrentEditedChord(newHint);
			}
		}
	}

	class DeleteChordAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!editing) {
				return;
			}
			
			if (editMarkerDisplayed) {
				return;
			}
			
			PositionHint hint = hints.getLastSelectedChordHint();
			unmarkCurrentEditedChord(hint);
			PositionHint other = hints.getNextChordHint(hint);
			int point = hint.getOffset();
			if (other == null) {
				other = hints.getPrevChordHint(hint);
				if (other == null) {
					// create marker at the end
					point = textPane.getStyledDocument().getLength();
					createMarker(point);
				}
			}
			if (other != null) {
				point = other.getOffset();
			}
			actualSong.deleteChord(hint.getLine(), hint.getChordRef());
			reshowSong(actualSong, textPane, hints);
			if (other != null) {
				// set new hint as last (new structure)
				PositionHint newHint = hints.findChordBefore(other.getOffset()+1);
				hints.setLastSelectedChord(newHint);
				markCurrentEditedChord(newHint);
			}
			scrollTo(point);
		}
	}

	class TextEditAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!editing) {
				return;
			}
			
			if (editMarkerDisplayed) {
				return;
			}
			
			PositionHint hint = hints.getLastSelectedChordHint();
			int lineIdx = hint.getLine();
			Line line = actualSong.getLine(lineIdx);
			String inputText = JOptionPane.showInputDialog("Enter line text: ", line.getText());
			if (!"".equals(inputText.trim())) {
				line.setText(inputText);
				unmarkCurrentEditedChord(hint);
				reshowSong(actualSong, textPane, hints);
				PositionHint newHint = hints.findChordBefore(hint.getOffset()+1);
				hints.setLastSelectedChord(newHint);
				markCurrentEditedChord(newHint);
			}
		}
	}

}
