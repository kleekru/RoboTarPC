package com.robotar.ui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.Color;

import cz.versarius.xchords.Chord;
import cz.versarius.xchords.ChordBag;
import cz.versarius.xchords.ChordLibrary;
import cz.versarius.xchords.ChordManager;
import cz.versarius.xsong.ChordRef;
import cz.versarius.xsong.Line;
import cz.versarius.xsong.Part;
import cz.versarius.xsong.PartType;
import cz.versarius.xsong.Song;
import cz.versarius.xsong.Verse;
import cz.versarius.xsong.XMLSongLoader;
import cz.versarius.xsong.XMLSongSaver;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Enumeration;
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

import com.robotar.ioio.LEDSettings;
import com.robotar.util.RoboTarPreferences;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Component;
import java.awt.RenderingHints;

import javax.swing.SwingConstants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

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
	private JList/*<Song>*/ songList;
	private DefaultListModel/*<Song>*/ songListModel;
	private JList/*<Chord>*/ chordList;
	private DefaultListModel/*<Chord>*/ chordListModel;
	
	private JButton btnPlay;
	private JButton btnSimPedal;
	private JButton btnEditSong;
	private JButton btnNewSong;
	private JButton btnLoadDefaultSongs;

	/** reference to mainframe and chordmanager. */
	private RoboTarPC mainFrame;
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
	private JPanel songPanel;
	private JEditorPane helpPane;
	private Marker marker;
	private String SPACES = "     ";
	
	/**
	 * Create the frame.
	 */
	public RoboTarSongsPage(final RoboTarPC mainFrame) {
		this.mainFrame = mainFrame;
		messages = mainFrame.getMessages();

		setBounds(100, 100, 1200, 510);
		frmBlueAhuizoteSongs = new JPanel() {

			@Override
			protected void paintComponent(Graphics g) {
				// TODO Auto-generated method stub
				Graphics2D g2d = (Graphics2D) g;
		                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		                        RenderingHints.VALUE_ANTIALIAS_ON);
		 
		                GradientPaint gp = new GradientPaint(0, 0,
		                       Const.BACKGROUND_COLOR.brighter(), 0, getHeight(),
		                       Const.BACKGROUND_COLOR);
		 
		                g2d.setPaint(gp);
		                g2d.fillRect(0, 0, getWidth(), getHeight());

				super.paintComponent(g);
			}
		};
		frmBlueAhuizoteSongs.setOpaque(false);
		//frmBlueAhuizoteSongs.setBackground(Const.BACKGROUND_COLOR);
		frmBlueAhuizoteSongs.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(frmBlueAhuizoteSongs);
		GridBagLayout gbl_frmBlueAhuizoteSongs = new GridBagLayout();
		gbl_frmBlueAhuizoteSongs.columnWidths = new int[] {30, 200, 113, 200, 200, 0};
		gbl_frmBlueAhuizoteSongs.rowHeights = new int[] {30, 30, 376, 0};
		gbl_frmBlueAhuizoteSongs.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_frmBlueAhuizoteSongs.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		frmBlueAhuizoteSongs.setLayout(gbl_frmBlueAhuizoteSongs);
		
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
		textPane.addMouseListener(new SongPaneClick());
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		addWindowListener(this);
		
		btnLoadDefaultSongs = new JButton(messages.getString("robotar.songs.load_default_songs"));
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
		
		songList = new JList(); //<Song>();
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
		
		helpPane = new JEditorPane();
		URL editSongHelp = RoboTarSongsPage.class.getResource("/help/songs.html");
		try {
			helpPane.setPage(editSongHelp);
			helpPane.setVisible(false);
			helpPane.setAutoscrolls(true);
		} catch (IOException e1) {
			e1.printStackTrace();
			helpPane.setText("Problem with loading help from classpath...");
		}
		frmBlueAhuizoteSongs.add(helpPane, gbc_songList);
		
		editPanel = new JPanel();
		//editPanel.setBackground(Color.BLUE);
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
		btnNewVerse = new JButton();
		GridBagConstraints gbc_btnNewVerse = new GridBagConstraints();
		gbc_btnNewVerse.gridy = 0;
		gbc_btnNewVerse.gridx = 0;
		gbc_btnNewVerse.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewVerse.fill = GridBagConstraints.BOTH;
		editPanel.add(btnNewVerse, gbc_btnNewVerse);
		
		btnNewLine = new JButton();
		GridBagConstraints gbc_btnNewLine = new GridBagConstraints();
		gbc_btnNewLine.fill = GridBagConstraints.BOTH;
		gbc_btnNewLine.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewLine.gridx = 0;
		gbc_btnNewLine.gridy = 1;
		editPanel.add(btnNewLine, gbc_btnNewLine);
		
		btnCreateChord = new JButton(messages.getString("robotar.songs.create_chord"));
		GridBagConstraints gbc_btnCreateChord = new GridBagConstraints();
		gbc_btnCreateChord.fill = GridBagConstraints.BOTH;
		gbc_btnCreateChord.insets = new Insets(0, 0, 5, 0);
		gbc_btnCreateChord.gridx = 0;
		gbc_btnCreateChord.gridy = 2;
		btnCreateChord.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textPane.requestFocusInWindow();
				mainFrame.startChordsPage();
			}
		});
		editPanel.add(btnCreateChord, gbc_btnCreateChord);
		
		btnAddChord = new JButton(); 
		btnAddChord.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_btnAddChord = new GridBagConstraints();
		gbc_btnAddChord.fill = GridBagConstraints.BOTH;
		gbc_btnAddChord.insets = new Insets(0, 0, 5, 0);
		gbc_btnAddChord.gridx = 0;
		gbc_btnAddChord.gridy = 3;
		/*btnAddChord.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addChordToSong();
			}
		});*/
		editPanel.add(btnAddChord, gbc_btnAddChord);
		
		chordList = new JList(); //<Chord>();
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
		
		loadRecent(mainFrame.getPreferences());
		
		// delete chords
		songList.registerKeyboardAction(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//DefaultListModel<Song> model = (DefaultListModel<Song>) songList.getModel();
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

	private void initEditing() {
		initKeys();
		//initCaret();
		marker = new Marker();
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
		textPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), "deletechord");
		textPane.getActionMap().put("deletechord", new DeleteChordAction());
		// text keystroke - T
		textPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_T, 0), "textedit");
		textPane.getActionMap().put("textedit", new TextEditAction());
		
		// add/insert chord - I
		Action acha = new AddChordAction(messages.getString("robotar.songs.add_chord"));
		textPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_I, 0), "addchord");
		textPane.getActionMap().put("addchord", acha);
		chordList.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_I, 0), "addchord");
		chordList.getActionMap().put("addchord", acha);
		btnAddChord.setAction(acha);
		
		// add new line (Enter)
		Action anlcha = new AddNewLineAction(messages.getString("robotar.songs.new_line"));
		textPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "addnewline");
		textPane.getActionMap().put("addnewline", anlcha);
		chordList.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "addnewline");
		chordList.getActionMap().put("addnewline", anlcha);
		btnNewLine.setAction(anlcha);
		
		// add new verse (V/Ctrl+Enter)
		Action anva = new AddNewVerseAction(messages.getString("robotar.songs.new_verse"));
		textPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.CTRL_DOWN_MASK), "addnewverse");
		textPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_V, 0), "addnewverse");
		textPane.getActionMap().put("addnewverse", anva);
		chordList.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.CTRL_DOWN_MASK), "addnewverse");
		chordList.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_V, 0), "addnewverse");
		chordList.getActionMap().put("addnewverse", anva);
		btnNewVerse.setAction(anva);
	}
	
	/*
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
	}*/
	
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
		pref.setSongs(getSongsList((Enumeration<Song>)songListModel.elements()));
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
			songListModel = new DefaultListModel(); //<Song>();
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
		@SuppressWarnings("unchecked")
		//JList<Song> list = (JList<Song>)e.getSource();
		//DefaultListModel<Song> model = (DefaultListModel<Song>) list.getModel();
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
			//Song song = (Song) model.get(selIdx);
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
			if (!marker.isDisplayed() && lastHint != null) {
				// get the selection
				@SuppressWarnings("unchecked")
				JList list = (JList)e.getSource();
				//DefaultListModel<Chord> model = (DefaultListModel<Chord>) list.getModel();
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
							hints.adjustChords(diff, lastHint);
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
		//addNewLineData();
	}
	
	protected void showActualSong() {
		showSong(actualSong, textPane);
	}
	
	protected void setupEditMode() {
		if (actualSong == null) {
			return;
		}
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
		int posBehindLastChord = getTitleLineLength();
		PositionHint last = hints.getLastChordAtSong();
		if (last != null) {
			int chordNameLength = Chord.getChordName(last.getChordRef().getChordId()).length();
			posBehindLastChord = last.getOffset() + chordNameLength; // + 1;
		}
		textPane.setCaretPosition(posBehindLastChord);
		
		if (last == null && actualSong.getLine(0).hasAnyText()) {
			placeMarkerNewline(posBehindLastChord + 1);
		} else {
			placeMarker(posBehindLastChord + 1, (last != null));
		}
		textPane.requestFocusInWindow();
		if (!actualSong.isChanged()) {
			actualSong.setChanged(true);
			modifiedCount++;
		}
		songList.setVisible(false);
		helpPane.setVisible(true);
	}

	/**
	 * Place marker (*) at specified position. 
	 * The optional space will be at position-1.
	 * Scrolls to position.
	 * @param position
	 * @param withSpace
	 */
	protected void placeMarker(int position, boolean withSpace) {
		marker.place(textPane.getStyledDocument(), position, withSpace);
		scrollTo(position);
		btnAddChord.setText(messages.getString("robotar.songs.add_chord"));
		btnNewLine.setEnabled(marker.isDisplayed());
		btnNewVerse.setEnabled(marker.isDisplayed());
	}
	
	protected Marker getMarker() {
		return marker;
	}
	
	/**
	 * Place marker (*) at specified position.
	 * Newline will be at position+1.
	 * Scrolls to position.
	 * @param position
	 */
	protected void placeMarkerNewline(int position) {
		marker.placeNewline(textPane.getStyledDocument(), position);
		scrollTo(position);
		btnAddChord.setText(messages.getString("robotar.songs.add_chord"));
		btnNewLine.setEnabled(marker.isDisplayed());
		btnNewVerse.setEnabled(marker.isDisplayed());
	}
	
	/**
	 * Hide marker (*).
	 */
	protected void removeMarker() {
		marker.remove(textPane.getStyledDocument());
		btnAddChord.setText(messages.getString("robotar.songs.insert_chord"));
		btnNewLine.setEnabled(marker.isDisplayed());
		btnNewVerse.setEnabled(marker.isDisplayed());
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
						if (!line.hasAnyChords() && !line.hasAnyText()) {
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
		helpPane.setVisible(false);
		songList.setVisible(true);
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
			//int linesSize = part.getLines().size();
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
			chordListModel = new DefaultListModel(); //<Chord>();
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
		// select first in used chords list (so we can immediately use keyboard for editing)
		chordList.setSelectedIndex(0);
	}

	/*protected void addNewLineData(Line afterThisLine) {
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
	}*/

	protected void addNewLineToSong() {
		PositionHint lineHint = hints.findLineAt(marker.getPosition());
		actualSong.insertLineAfter(lineHint.getLine());
		
		// have to recount everything with new line
		reshowSong(actualSong, textPane, hints);
		// create *
		PositionHint nextLineHint = hints.getNextLineHint(lineHint);
		marker.placeNewline(textPane.getStyledDocument(), nextLineHint.getOffset());
		int ensureTextLineVisible = marker.getPosition() + 2;
		chordList.setSelectedIndex(0);
		scrollTo(ensureTextLineVisible);
	}

	protected void addNewVerseToSong() {
		PositionHint lineHint = hints.findLineAt(marker.getPosition());
		actualSong.insertPartAfter(lineHint.getLine(), PartType.VERSE);
		
		// have to recount everything with new line
		reshowSong(actualSong, textPane, hints);
		// create *
		PositionHint nextLineHint = hints.getNextLineHint(lineHint);
		marker.placeNewline(textPane.getStyledDocument(), nextLineHint.getOffset());
		int ensureTextLineVisible = marker.getPosition() + 2;
		chordList.setSelectedIndex(0);
		scrollTo(ensureTextLineVisible);
		
	}

	/**
	 * Adds or inserts chord based on marker.isDisplayed()
	 */
	protected void addChordToSong() {
		Chord chord = (Chord)chordList.getSelectedValue();
		if (chord != null) {
			Line line;
			int lineLength = 0;
			StyledDocument doc = textPane.getStyledDocument();
			int position = doc.getLength() - 1;
			boolean inserting = false;
			int insertIdx = -1;
			if (!marker.isDisplayed()) {
				// insert before current chord
				PositionHint hint = hints.getLastSelectedChordHint();
				PositionHint lineHint = hints.getLineHint(hint);
				line = actualSong.getLine(lineHint.getLine());
				inserting = true;
				insertIdx = line.getChords().indexOf(hint.getChordRef());
				
				// relative position on the line
				lineLength = hint.getChordRef().getPosition() - 1;
				// absolute position in doc, where the chord will come
				position = hint.getOffset(); 
				hints.moveChords(chord.getName().length() + 1, hint);
			} else {
				// add chord to the current line
				PositionHint lineHint = hints.findLineAt(marker.getPosition());
				line = actualSong.getLine(lineHint.getLine());
				if (line.getChords() == null) {
					line.setChords(new ArrayList<ChordRef>());
				}
				lineLength = line.getChordLineLength();
				position = marker.getPosition();
			}
			
			ChordRef ref = new ChordRef();
			ref.setChordId(chord.getId());
			// TODO - clone?
			ref.setChord(chord);
			// first position is 1, not 0
			ref.setPosition(lineLength + 1);
			if (inserting) {
				line.getChords().add(insertIdx, ref);
			} else {
				line.getChords().add(ref);
			}
			// add to used chords section
			actualSong.getUsedChords().add(chord);
			
			// reshow song
			reshowSong(actualSong, textPane, hints);
			if (inserting) {
				// set new hint as last (new structure)
				PositionHint newHint = hints.findChordBefore(position+1);
				hints.setLastSelectedChord(newHint);
				markCurrentEditedChord(newHint);
			} else {
				// show marker?
				int newPosition = (marker.getPosition() + chord.getName().length() + 1);
				placeMarker(newPosition, true);
			}
			scrollTo(marker.getPosition()); // text line offset of the marker TODO probably
			
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
			createNewSong(dialog.getTitleText(), dialog.getInterepretText(), dialog.getInfoText());
			songList.setSelectedIndex(0);
			// put into edit mode
			setupEditMode();
		}
	}

	protected void createNewSong(String title, String interpret, String info) {
		Song song = new Song();
		song.setTitle(title);
		song.setInterpret(interpret);
		song.setInfo(info);
		
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
						@SuppressWarnings("unchecked")
						DefaultListModel model = (DefaultListModel)chPage.getChordListModel();
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
	
	public int getTitleLineLength() {
		return actualSong.getTitle().length() + SPACES.length() + actualSong.getInterpret().length() + 1;
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
			doc.insertString(0, song.getTitle() + SPACES + song.getInterpret() + "\n", titleStyle);
			int lengthSoFar = doc.getLength();
			// process parts
			int lineNum = 0;
			for (Part part : song.getParts()) {
				doc.insertString(doc.getLength(), "\n", null);
				
				for (Line line : part.getLines()) {
					if (!line.hasAnyChords()) {
						if (line.getText() == null) {
							// totally empty line
							//continue;
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
			int actLength = doc.getLength();
			String str = doc.getText(actLength - 5, 5);
			LOG.info(""+str.length());
			LOG.info("'" + doc.getText(actLength - 5, 5) + "'");
			/*if (actLength != lengthSoFar) {
				// remove last trailing \n
				doc.remove(actLength - 1, 1);
			}*/
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
			doc.insertString(0, song.getTitle() + SPACES + song.getInterpret() + "\n", titleStyle);
			
			// process parts
			int lineNum = 0;
			for (Part part : song.getParts()) {
				doc.insertString(doc.getLength(), "\n", null);
				
				for (Line line : part.getLines()) {
					if (!line.hasAnyChords()) {
						if (line.getText() == null) {
							// totally empty line - should be created anyway, we need line positionhint!
							//continue;
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
		
		// replace empty text lines with one space
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
	 * !! hints.setLastSelectedChord() must be set before this call!
	 * (because chordsList.set.. generates listselectionevent and we get the last selected chord in there)
	 * 
	 * @param chordHint
	 * @param lineHint
	 * @return position of the chord (offset)
	 */
	protected int markCurrentEditedChord(PositionHint chordHint) {
		StyledDocument doc = textPane.getStyledDocument();
		Style editMarkedChordStyle = doc.getStyle(Styles.EDIT_MARKED_CHORD_STYLE);
		// active chord
		doc.setCharacterAttributes(chordHint.getOffset(), chordHint.getLength(), editMarkedChordStyle, false);
		// select in used chords list
		chordList.setSelectedValue(chordHint.getChordRef().getChord(), true);
		return chordHint.getOffset();
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

		private static final long serialVersionUID = 3860330078622760515L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!editing) {
				return;
			}
			
			if (marker.isDisplayed()) {
				// B section 
				// check if there are any chords in song
				//if (!actualSong.hasAnyChords()) {
					//return;
				//}
				// the same condition as above (almost. hints are not created on last line,
				// but are added to song
				if (hints.getChords().size() == 0) {
					StyledDocument doc = textPane.getStyledDocument();
					int position = doc.getLength();
					if (marker.getPosition() != position) {
						//return;
					}
				}
				PositionHint lineHint = hints.findLineAt(marker.getPosition());
				PositionHint prevChordHint = hints.findChordBefore(marker.getPosition());
				if ((prevChordHint != null) 
						&& (prevChordHint.getLine() + 1 >= lineHint.getLine())) {
					// on the same line or previous
					removeMarker();
					hints.setLastSelectedChord(prevChordHint);
					markCurrentEditedChord(prevChordHint);
				} else {
					if (lineHint.getLine() > 0) {
						removeMarker();
						// we have to display marker again, at the line with no chords
						PositionHint prevLineHint = hints.getPrevLineHint(lineHint);
						placeMarkerNewline(prevLineHint.getOffset());
						scrollTo(prevLineHint.getOffset());
						chordList.setSelectedIndex(0);
					} else {
						// we are at the beginning, nothing to do. (or beep sound)
					}
				}
			} else {
				// A section
				PositionHint lastChordHint = hints.getLastSelectedChordHint();
				PositionHint prevChordHint = hints.getPrevChordHint(lastChordHint);
				if (prevChordHint != null) {
					// unmark
					unmarkCurrentEditedChord(lastChordHint);
					
					// decide if * should be put on line between, without chords
					if (lastChordHint.getLine() > prevChordHint.getLine() + 1) {
						// there are rows of text without chords (between those 2 chords)
						int lineWithoutChords = hints.getPrevLineOffset(lastChordHint, 0);
						placeMarkerNewline(lineWithoutChords);
						scrollTo(lineWithoutChords);
						chordList.setSelectedIndex(0);
					} else {
						// the same or prev line - mark chord (skip *)
						hints.setLastSelectedChord(prevChordHint);
						markCurrentEditedChord(prevChordHint);
						// scrollto
						int ensurePrevTextLineVisible = hints.getPrevLineOffset(prevChordHint, 0);
						scrollTo(ensurePrevTextLineVisible);
					}
				} else {
					// we are at the place of first chord, there may be lines
					// without chords above
					if (lastChordHint.getLine() != 0) {
						// there are rows of text without chords
						int lineWithoutChords = hints.getPrevLineOffset(lastChordHint, 0);
						placeMarkerNewline(lineWithoutChords);
						scrollTo(lineWithoutChords);
						chordList.setSelectedIndex(0);
					}
				}
			}
		}
	}
	
	class RightAction extends AbstractAction {

		private static final long serialVersionUID = -7650665587003989836L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!editing) {
				return;
			}
			
			if (marker.isDisplayed()) {
				// B section 
				// check if there are any chords in song
				//if (!actualSong.hasAnyChords()) {
					//return;
				//}
				// the same condition as above
				/*if (hints.getChords().size() == 0) {
					return;
				}*/
				// we are either at the end of line with chords or at empty line
				// check if next line exists, if so, remove marker. if not, stay where you are
				PositionHint lineHint = hints.findLineAt(marker.getPosition());
				int lines = actualSong.getLinesCount();
				if (lines > lineHint.getLine() + 1) {
					// next line exists
					removeMarker();
					// find next chord after the current position
					PositionHint nextChordHint = hints.findChordAfter(marker.getPosition());
					int ensureTextLineVisible = 0;
					if ((nextChordHint != null)
							&& (lineHint.getLine() + 1 == nextChordHint.getLine())) {
						// it's next line with chord - highlight the next chord
						hints.setLastSelectedChord(nextChordHint);
						markCurrentEditedChord(nextChordHint);
						ensureTextLineVisible = hints.getNextLineOffset(nextChordHint, textPane.getStyledDocument().getLength());
						
					} else {
						// there are lines of text without chords - put marker before the text (with newline)
						PositionHint nextLineHint = hints.getNextLineHint(lineHint);
						placeMarkerNewline(nextLineHint.getOffset());
						ensureTextLineVisible = marker.getPosition() + 2;
						chordList.setSelectedIndex(0);
					}
					scrollTo(ensureTextLineVisible);
				} else {
					// we are at the end of document - nothing (or beep sound)
				}
			} else {
				// A section
				PositionHint lastChordHint = hints.getLastSelectedChordHint();
				PositionHint nextChordHint = hints.getNextChordHint(lastChordHint);
				if ((nextChordHint != null)
						&& (lastChordHint.getLine() == nextChordHint.getLine())) {
					// unmark
					hints.setLastSelectedChord(nextChordHint);
					unmarkCurrentEditedChord(lastChordHint);
					markCurrentEditedChord(nextChordHint);
					// scrollto - the same line, no need
				} else {
					unmarkCurrentEditedChord(lastChordHint);
					// we are at the last chord on the row - place marker at the end of the row
					placeMarker(lastChordHint.getOffsetAfter(), true);
					chordList.setSelectedIndex(0);
				}
			}
		}
	}
	
	class UpAction extends AbstractAction {

		private static final long serialVersionUID = -8289591429082252915L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!editing) {
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

		private static final long serialVersionUID = 8295947628879158412L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!editing) {
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

		private static final long serialVersionUID = 6419398425102219652L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!editing) {
				return;
			}
			
			if (marker.isDisplayed()) {
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

		private static final long serialVersionUID = -7140568654064941697L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!editing) {
				return;
			}
			
			if (marker.isDisplayed()) {
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

	class AddChordAction extends AbstractAction {

		private static final long serialVersionUID = 1488203601848039131L;

		public AddChordAction(String name) {
			super(name);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			textPane.requestFocusInWindow();
			LOG.info("got add chord action");
			if (!editing) {
				return;
			}
			
			addChordToSong();
		}
	}
	
	class AddNewLineAction extends AbstractAction {
		private static final long serialVersionUID = -1973600146133921652L;

		public AddNewLineAction(String name) {
			super(name);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			textPane.requestFocusInWindow();
			if (!marker.isDisplayed()) {
				return;
			}
			addNewLineToSong();
			
		}
	}
	
	class AddNewVerseAction extends AbstractAction {
		private static final long serialVersionUID = 2016802979420682194L;

		public AddNewVerseAction(String name) {
			super(name);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			textPane.requestFocusInWindow();
			if (!marker.isDisplayed()) {
				return;
			}
			addNewVerseToSong();
		}
	}
	
	class DeleteChordAction extends AbstractAction {

		private static final long serialVersionUID = 5415456999853959871L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!editing) {
				return;
			}
			
			if (marker.isDisplayed()) {
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
					placeMarker(point, false); 
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
			} else {
				// show * at empty line
				PositionHint lineHint = hints.getLineHint(hint);
				placeMarkerNewline(lineHint.getOffset());
			}
		}
	}

	class TextEditAction extends AbstractAction {

		private static final long serialVersionUID = 4394233642709046669L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!editing) {
				return;
			}
			
			if (marker.isDisplayed()) {
				// * is displayed not only at the end, but also on empty lines without chords
				PositionHint lineHint = hints.findLineAt(marker.getPosition());
				editLineText(lineHint.getLine());
			} else {
				// last is not null, because of previous conditions
				PositionHint hint = hints.getLastSelectedChordHint();
				editLineText(hint.getLine());
			}
		}
	}
	
	public void editLineText(int lineIdx) {
		Line line = actualSong.getLine(lineIdx);
		String inputText = JOptionPane.showInputDialog(messages.getString("robotar.songs.enter_line_text"), line.getText());
		// if pressed Cancel, inputText is set to null
		if (inputText != null) {
			line.setText(inputText);
			PositionHint hint = hints.getLastSelectedChordHint();
			// recreate and redisplay song
			reshowSong(actualSong, textPane, hints);
			if (!marker.isDisplayed()) {
				// mark previously edited chord
				PositionHint newHint = hints.findChordBefore(hint.getOffset()+1);
				hints.setLastSelectedChord(newHint);
				int pos = markCurrentEditedChord(newHint);
				scrollTo(pos);
			} else {
				// create editing marker again
				if (marker.isNewline()) {
					// empty line (no chords)
					PositionHint lineHint = hints.getLines().get(lineIdx);
					placeMarkerNewline(lineHint.getOffset());
				} else {
					// marker at the end of chords
					placeMarker(marker.getPosition(), true);
				}
			}
		}
	}

	class SongPaneClick extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			super.mouseClicked(e);
			if (!editing) {
				return;
			}
			int start = textPane.getSelectionStart();
			int caret = textPane.getCaretPosition(); // current scroll
			PositionHint hint = hints.findLineWith(start);
			if (hint != null) {
				// we have line, where it was clicked
				editLineText(hint.getLine());
				scrollTo(caret);
			}
			
		}
	}
	
	/** 
	 * Edit marker.
	 */
	class Marker {
		// is marker currently displayed or not
		private boolean displayed;
		// extra newline put after the marker, must be removed then
		private boolean newline;
		// extra space put before the marker, must be removed then
		private boolean withSpace;
		// offset in document, where '*' is
		private int position;
		
		public boolean isDisplayed() {
			return displayed;
		}
		public void setDisplayed(boolean displayed) {
			this.displayed = displayed;
		}
		public boolean isNewline() {
			return newline;
		}
		public void setNewline(boolean newline) {
			this.newline = newline;
		}
		public int getPosition() {
			return position;
		}
		public void setPosition(int position) {
			this.position = position;
		}
		public boolean isWithSpace() {
			return withSpace;
		}
		public void setWithSpace(boolean withSpace) {
			this.withSpace = withSpace;
		}
		
		public void place(StyledDocument doc, int position,	boolean withSpace) {
			Style markerStyle = doc.getStyle(Styles.MISSING_CHORD_STYLE);
			try {
				// put extra space before the marker (we are behind other chord)
				if (withSpace) {
					Style mainStyle = doc.getStyle(Styles.MAIN_STYLE);
					doc.insertString(position - 1, " ", mainStyle);
				}
				doc.insertString(position, "*", markerStyle);
				displayed = true;
				newline = false;
				this.withSpace = withSpace;
				this.position = position;
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}	
		
		public void placeNewline(StyledDocument doc, int position) {
			Style markerStyle = doc.getStyle(Styles.MISSING_CHORD_STYLE);
			try {
				// put extra new line after the marker
				doc.insertString(position, "*\n", markerStyle);
				displayed = true;
				newline = true;
				this.position = position;
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
		
		/** remove marker from doc */
		public void remove(StyledDocument doc) {
			if (doc == null) return;
			try {
				if (marker.isDisplayed()) { // to be sure
					if (marker.isNewline()) {
						doc.remove(position, 2);
					} else {
						if (marker.isWithSpace()) {
							doc.remove(position - 1, 2);
						} else {
							doc.remove(position, 1);
						}
					}
				}
				displayed = false;
				newline = false;
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}
}
