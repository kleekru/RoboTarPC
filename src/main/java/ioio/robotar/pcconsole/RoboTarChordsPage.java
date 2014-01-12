package ioio.robotar.pcconsole;


import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import cz.versarius.xchords.Chord;
import cz.versarius.xchords.ChordBag;
import cz.versarius.xchords.ChordLibrary;

import javax.swing.JLabel;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
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

import javax.swing.BoxLayout;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.ComponentOrientation;
import java.awt.Toolkit;

public class RoboTarChordsPage extends JFrame implements ActionListener,
		ListSelectionListener, WindowListener {
	private static final long serialVersionUID = -4977090038183485379L;

	static final Logger LOG = LoggerFactory.getLogger(RoboTarChordsPage.class);

	private JPanel frmBlueAhuizoteChords;
	
	private JButton btnNewChord;
	
	private String chordNameSend;
	
	private DefaultListModel chordListModel;
	private JList listChords;
	private JLabel lblChordPicture;
	
	/** reference to mainframe and chordmanager */
	private RoboTarStartPage mainFrame;
	private ResourceBundle messages;
	
	private ChordRadioPanel radioPanel;

	private JButton btnAddToSong;

	private JButton btnAddToChordList;

	private JLabel activeSong;
	private JPanel btnPanel;
	private JButton btnLoadChords;
	private JButton btnSaveChords;
	private JButton btnLoadDefault;
	private JButton btnClearChords;

	private JToggleButton tglbtnTestChord;

	private boolean unsavedChords;
	
	/**
	 * Create the frame.
	 * 
	 * @param chordReceived
	 */
	public RoboTarChordsPage(RoboTarStartPage mainFrame) {
		super();
		setPreferredSize(new Dimension(800, 410));
		this.setMainFrame(mainFrame);
		messages = mainFrame.getMessages();
		
		setTitle(messages.getString("robotar.chords.title"));
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				RoboTarChordsPage.class
						.getResource("/data/BlueAhuizoteIcon.png")));
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		//setBounds(100, 100, 800, 400);
		
		frmBlueAhuizoteChords = new JPanel();
		frmBlueAhuizoteChords.setPreferredSize(new Dimension(800, 410));
		frmBlueAhuizoteChords.setAlignmentY(Component.TOP_ALIGNMENT);
		frmBlueAhuizoteChords.setAlignmentX(Component.LEFT_ALIGNMENT);
		frmBlueAhuizoteChords.setBackground(Color.BLUE);
		frmBlueAhuizoteChords.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(frmBlueAhuizoteChords);
		GridBagLayout gbl_frmBlueAhuizoteChords = new GridBagLayout();
		gbl_frmBlueAhuizoteChords.columnWidths = new int[] {100, 90, 30, 400, 30};
		gbl_frmBlueAhuizoteChords.rowHeights = new int[] {24, 80, 138, 102, 0};
		gbl_frmBlueAhuizoteChords.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0};
		gbl_frmBlueAhuizoteChords.rowWeights = new double[]{0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		frmBlueAhuizoteChords.setLayout(gbl_frmBlueAhuizoteChords);
		
				JLabel activeSongLbl = new JLabel(messages.getString("robotar.chords.active_song"));
				activeSongLbl.setForeground(Color.WHITE);
				activeSongLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
				GridBagConstraints gbc_activeSongLbl = new GridBagConstraints();
				gbc_activeSongLbl.fill = GridBagConstraints.HORIZONTAL;
				gbc_activeSongLbl.insets = new Insets(0, 0, 5, 5);
				gbc_activeSongLbl.gridx = 1;
				gbc_activeSongLbl.gridy = 0;
				frmBlueAhuizoteChords.add(activeSongLbl, gbc_activeSongLbl);
						
								activeSong = new JLabel(messages.getString("robotar.chords.no_song_selected"));
								activeSong.setForeground(Color.WHITE);
								GridBagConstraints gbc_activeSong = new GridBagConstraints();
								gbc_activeSong.anchor = GridBagConstraints.NORTH;
								gbc_activeSong.fill = GridBagConstraints.HORIZONTAL;
								gbc_activeSong.insets = new Insets(0, 0, 5, 0);
								gbc_activeSong.gridx = 3;
								gbc_activeSong.gridy = 0;
								frmBlueAhuizoteChords.add(activeSong, gbc_activeSong);
								activeSong.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null,
										null, null));
								activeSong.setBackground(Color.BLACK);
								activeSong.setVerticalAlignment(SwingConstants.TOP);
								activeSong.setFont(new Font("Segoe UI", Font.BOLD, 14));
				
						// new chord button
						btnNewChord = new JButton("");
						btnNewChord.setPressedIcon(new ImageIcon(RoboTarChordsPage.class
								.getResource("/data/NewChordPressed.png")));
						GridBagConstraints gbc_btnNewChord = new GridBagConstraints();
						gbc_btnNewChord.anchor = GridBagConstraints.NORTH;
						gbc_btnNewChord.fill = GridBagConstraints.HORIZONTAL;
						gbc_btnNewChord.insets = new Insets(0, 0, 5, 5);
						gbc_btnNewChord.gridx = 0;
						gbc_btnNewChord.gridy = 1;
						frmBlueAhuizoteChords.add(btnNewChord, gbc_btnNewChord);
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
						});
		
				tglbtnTestChord = new JToggleButton("");
				tglbtnTestChord.setPressedIcon(new ImageIcon(RoboTarChordsPage.class
						.getResource("/data/TestChordOn.png")));
				GridBagConstraints gbc_tglbtnTestChord = new GridBagConstraints();
				gbc_tglbtnTestChord.fill = GridBagConstraints.HORIZONTAL;
				gbc_tglbtnTestChord.anchor = GridBagConstraints.NORTH;
				gbc_tglbtnTestChord.insets = new Insets(0, 0, 5, 5);
				gbc_tglbtnTestChord.gridx = 1;
				gbc_tglbtnTestChord.gridy = 1;
				frmBlueAhuizoteChords.add(tglbtnTestChord, gbc_tglbtnTestChord);
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
				GridBagConstraints gbc_btnAddToSong = new GridBagConstraints();
				gbc_btnAddToSong.anchor = GridBagConstraints.NORTH;
				gbc_btnAddToSong.fill = GridBagConstraints.HORIZONTAL;
				gbc_btnAddToSong.insets = new Insets(0, 0, 5, 0);
				gbc_btnAddToSong.gridx = 3;
				gbc_btnAddToSong.gridy = 1;
				frmBlueAhuizoteChords.add(btnAddToSong, gbc_btnAddToSong);
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
		
				
				// TODO Need to update so this field shows a chord picture - use XChords
				lblChordPicture = new JLabel(messages.getString("robotar.chords.no_chord_selected"));
				lblChordPicture.setForeground(Color.WHITE);
				lblChordPicture
						.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 10));
				lblChordPicture.setBounds(new Rectangle(0, 0, 0, 0));
				lblChordPicture.setPreferredSize(new Dimension(132, 138));
				lblChordPicture.setMaximumSize(new Dimension(132, 138));
				//lblChordPicture.setBorder(new LineBorder(Color.RED, 3));
				GridBagConstraints gbc_lblChordPicture = new GridBagConstraints();
				gbc_lblChordPicture.insets = new Insets(0, 0, 5, 5);
				gbc_lblChordPicture.gridx = 1;
				gbc_lblChordPicture.gridy = 2;
				frmBlueAhuizoteChords.add(lblChordPicture, gbc_lblChordPicture);
		
		radioPanel = new ChordRadioPanel();
		GridBagConstraints gbc_radioPanel = new GridBagConstraints();
		gbc_radioPanel.insets = new Insets(0, 0, 5, 0);
		gbc_radioPanel.gridx = 3;
		gbc_radioPanel.gridy = 2;
		frmBlueAhuizoteChords.add(radioPanel, gbc_radioPanel);
		
		btnPanel = new JPanel();
		btnPanel.setBackground(Color.BLUE);
		GridBagConstraints gbc_btnPanel = new GridBagConstraints();
		gbc_btnPanel.fill = GridBagConstraints.BOTH;
		gbc_btnPanel.insets = new Insets(0, 0, 0, 5);
		gbc_btnPanel.gridx = 1;
		gbc_btnPanel.gridy = 3;
		frmBlueAhuizoteChords.add(btnPanel, gbc_btnPanel);
		btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
		
		btnLoadChords = new JButton(messages.getString("robotar.chords.load_chords"));
		btnLoadChords.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				try {
					loadChords(evt);
				} catch (FileNotFoundException e1) {
					JOptionPane.showMessageDialog(RoboTarChordsPage.this, messages.getString("robotar.chords.file_not_found"));
				}
			}
		});
		btnPanel.add(btnLoadChords);
		
		btnSaveChords = new JButton(messages.getString("robotar.chords.save_chords"));
		btnSaveChords.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				saveChords(evt);
			}
		});
		btnPanel.add(btnSaveChords);
		
		btnLoadDefault = new JButton(messages.getString("robotar.chords.load_default"));
		btnLoadDefault.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				loadDefaultChords(evt);
			}
		});
		btnPanel.add(btnLoadDefault);
		
		btnClearChords = new JButton(messages.getString("robotar.chords.clear_chords"));
		btnClearChords.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				clearChords(evt);
			}
		});
		btnPanel.add(btnClearChords);
		
				JScrollPane scrollPane = new JScrollPane();
				scrollPane.setPreferredSize(new Dimension(90, 100));
				scrollPane.setMaximumSize(new Dimension(100, 200));
				scrollPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
				GridBagConstraints gbc_scrollPane = new GridBagConstraints();
				gbc_scrollPane.fill = GridBagConstraints.BOTH;
				gbc_scrollPane.gridx = 0;
				gbc_scrollPane.gridheight = 2;
				gbc_scrollPane.insets = new Insets(0, 0, 0, 5);
				gbc_scrollPane.gridy = 2;
				frmBlueAhuizoteChords.add(scrollPane, gbc_scrollPane);
				listChords = new JList();
				listChords.setFont(new Font("Tahoma", Font.BOLD, 11));
				listChords.setCellRenderer(new ChordListCellRenderer());
				listChords.setVisibleRowCount(6);
				listChords.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				listChords.setBorder(new LineBorder(Color.BLUE, 3, true));
				listChords.addListSelectionListener(this);
				scrollPane.setViewportView(listChords);
		
		btnAddToChordList = new JButton("");
		GridBagConstraints gbc_btnAddToChordList = new GridBagConstraints();
		gbc_btnAddToChordList.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnAddToChordList.gridx = 3;
		gbc_btnAddToChordList.gridy = 3;
		frmBlueAhuizoteChords.add(btnAddToChordList, gbc_btnAddToChordList);
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
					LOG.info("adding chord to chord list");
					Chord chord = radioPanel.createChordFromRadios(getLibraryName(true));
					if (chordListModel == null) {
						chordListModel = new DefaultListModel();
						listChords.setModel(chordListModel);
					}
					chordListModel.addElement(chord);
					radioPanel.setChordName(null);
					clearSelection();
					unsavedChords = true;
				} else {
					JOptionPane.showMessageDialog(RoboTarChordsPage.this, messages.getString("robotar.chords.fill_chord_name"));
				}

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

		/* set size of the frame */
		setSize(840, 410);
		addWindowListener(this);
		
		// initialize with recent chord library, robotar by default
		reloadChordList(mainFrame.getChordManager().getChosenLibrary());
		
		// delete chords
		listChords.registerKeyboardAction(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String chosenLib = getMainFrame().getChordManager().getChosenLibrary();
				if (!ChordManager.DEFAULT_ROBOTAR.equals(chosenLib)) {
					DefaultListModel model = (DefaultListModel) listChords.getModel();
					int selectedIndex = listChords.getSelectedIndex();
					if (selectedIndex != -1) {
					    model.remove(selectedIndex);
					}
				}
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), JComponent.WHEN_FOCUSED);
		
		// this is causing problems with layout
		//pack();
		setLocationByPlatform(true);
		setVisible(true);
	}

	protected void newChordButtonActionPerformed(ActionEvent evt) {
		radioPanel.clear();
		//radioPanel.setChordName(messages.getString("robotar.chords.enter_chord_name"));
		String generatedName = generateUnusedChordName();
		radioPanel.setChordName(generatedName);
		clearSelection();
	}
	
	private String generateUnusedChordName() {
		String name = "T-";
		int counter = 1;
		while (exists(counter)) {
			counter++;
		}
		return name + Integer.toString(counter, 10);
	}

	private boolean exists(int counter) {
		if (chordListModel == null) {
			return false;
		}
		for (int i = 0; i < chordListModel.size(); i++) {
			Chord chord = (Chord)chordListModel.get(i);
			String name = chord.getName();
			if (name.contains("-")) {
				// use this prepared parser
				String number = Chord.getChordNameSimple(chord.getName());
				if (number != null) {
					int num = Integer.parseInt(number, 10);
					if (num == counter) {
						return true;
					}
				}
			}
		}
		return false;
	}

	protected void clearChords(ActionEvent evt) {
		if (chordListModel != null) {
			chordListModel.removeAllElements();
		}
	}

	protected void saveChords(ActionEvent evt) {
		// better condition?
		if ((chordListModel == null) || 
				(chordListModel != null && chordListModel.isEmpty())) {
			JOptionPane.showMessageDialog(this,
        		    "Empty list of chords, nothing to save.",
        		    "Saving chords",
        		    JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		// first check, if it is already in chord manager.
		// if so, use existing file, without asking.
		if (chordListModel != null && !chordListModel.isEmpty()) {
			Chord chord = (Chord)chordListModel.get(0);
			if (chord != null) {
				String libraryName = Chord.getLibraryName(chord.getId());
				if (libraryName != null && !ChordManager.DEFAULT_ROBOTAR.equals(libraryName)) {
					ChordManager mng = mainFrame.getChordManager();
					ChordLibrary lib = mng.findByName(libraryName);
					if (lib != null) {
						// adjust all chords in library to the current state of left list of chords!
						// add new, remove old, modify existing - the easiest is just copy everything
						// to the library again
						List<Chord> list = new ArrayList<Chord>();
						for (int i=0; i<chordListModel.size(); i++) {
			            	Chord c = (Chord)chordListModel.get(i);
			            	list.add(c);
			            }
						lib.setChords(list);
						
						// save to existing file
						XMLChordSaver saver = new XMLChordSaver();
						saver.save(lib, new File(lib.getPath()));
						
						// mark and save recent
						mng.setChosenLibrary(libraryName);
			            unsavedChords = false;
			            return;
					}
				} else if (ChordManager.DEFAULT_ROBOTAR.equals(libraryName)) {
					// user tries to save modified 'robotar' library
					JOptionPane.showMessageDialog(this,
		        		    "It is currently not possible to modify default chords for RoboTar, sorry.",
		        		    "Saving default chords",
		        		    JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
		}
		// if it is new set of chords, ask for file
		JFileChooser fc = new JFileChooser();
		int returnValue = fc.showSaveDialog(this);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            XMLChordSaver saver = new XMLChordSaver();
            ChordLibrary lib = new ChordLibrary();
            for (int i=0; i<chordListModel.size(); i++) {
            	Chord chord = (Chord)chordListModel.get(i);
            	lib.add(chord);
            }
            saver.save(lib, file);
            // add to recent files - trick, and also validation of the xml
            ChordManager mng = mainFrame.getChordManager();
            String libName = mng.loadLibrary(file);
            if (libName == null) {
            	LOG.error("Cannot load currently saved chord library file! path: {}, abspath: {} ", file.getPath(), file.getAbsolutePath());
            	// msg
            	JOptionPane.showMessageDialog(this,
            		    "Could not load currently saved file. See log console.",
            		    "Chord library validation loading",
            		    JOptionPane.ERROR_MESSAGE);
            	return;
            }
            mng.setChosenLibrary(libName);
            unsavedChords = false;
		}
		
	}

	protected void loadChords(ActionEvent evt) throws FileNotFoundException {
		JFileChooser fc = new JFileChooser();
		int returnValue = fc.showOpenDialog(this);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            ChordManager mng = mainFrame.getChordManager();
            String libName = mng.loadLibrary(file);
            if (libName == null) {
            	JOptionPane.showMessageDialog(this,
            		    "Could not load chosen file. Is it chord library? See log console.",
            		    "Chord library loading",
            		    JOptionPane.ERROR_MESSAGE);
            	return;
            } 
            reloadChordList(libName);
            
		}
	}

	/** 
	 * Reload current list of chords with chosen library from chordmanager.
	 * @param libName
	 */
	protected void reloadChordList(String libName) {
		ChordManager mng = mainFrame.getChordManager();
		ChordLibrary lib = mng.findByName(libName);
        // throwing away any previously set chords // TODO more libraries should be possible?
		//////////////////////////////////////////////////////////////////
		if (lib == null) {
			LOG.error("Cannot find chord library: '{}' in chord manager", libName);
			if (mng.getLibrariesCount() > 0) {
				lib = mng.getAny();
				libName = lib.getName();
			} else {
				LOG.error("Chord manager is empty! Cannot load anything.");
				return;
			}
		}
		chordListModel = new DefaultListModel();
		for (Chord chord : lib.getChords()) {
			chordListModel.addElement(chord);
		}
		listChords.setModel(chordListModel);
		mng.setChosenLibrary(libName);
	}

	protected void loadDefaultChords(ActionEvent evt) {
		// populate the list with default chords set
		reloadChordList(ChordManager.DEFAULT_ROBOTAR);
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
			Chord chord = radioPanel.createChordFromRadios(getLibraryName(false));
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
		LOG.info("chordid: {}", chord.getId());
	}
	
	/**
	 * TODO currently reads fixed images. after adding own new chords -> generate image on the fly
	 * @param chord
	 */
	private void showChordImage(Chord chord) {
		String chordName = chord.getName();
		String imageName = "/default-chords/" + chordName + ".png";
		try {
			URL res = RoboTarChordsPage.class.getResource(imageName);
			if (res != null) {
				lblChordPicture.setIcon(new ImageIcon(ImageIO.read(res)));
			} else {
				LOG.error("file not found: {}", imageName);
			}
			lblChordPicture.setText(chordName);
		} catch (IOException e) {
			LOG.error("file not found: {}", imageName);
			lblChordPicture.setText(messages.getString("robotar.chords.image_not_found"));
			lblChordPicture.setIcon(null);
			//LOG.debug("stacktrace", e);
		}
	}

	/**
	 * Fills servoSettings field to be used in loop() - RoboTarConsole
	 */
	public void prepareChord() {
		if (tglbtnTestChord.isSelected()) {
			// use radio panel as source for chord, unfilled radios will be marked OPEN
			Chord chord = radioPanel.createChordFromRadios(getLibraryName(false));
			mainFrame.getServoSettings().setChord(chord);
			LEDSettings leds = new LEDSettings(chord);
			mainFrame.setLeds(leds);
			LOG.debug("preparing servos Values on chords page: {}", mainFrame.getServoSettings().debugOutput());
			LOG.debug("preparing leds on chords page: {}", leds.debugOutput());
		} else {
			// release
			mainFrame.getServoSettings().setInitialPosition();
			LEDSettings leds = new LEDSettings();
			mainFrame.setLeds(leds);
			LOG.debug("releasing all");
			LOG.debug("releasing servos: {}", mainFrame.getServoSettings().debugOutput());
			LOG.debug("turning off LEDs: {}", leds.debugOutput());
		}
	}
	
	
	
	private String getLibraryName(boolean askUserIfNotDefault) {
		DefaultListModel model = (DefaultListModel)listChords.getModel();
		if (model == null || model.isEmpty()) {
		//if (listChords.isSelectionEmpty()) {
			// chord list is empty - choose new name of the library
			// prepare default name
			ChordManager mng = mainFrame.getChordManager();
			int i = 0;
			String defName;
			do {
				i++;
				defName = ChordManager.USER_PREFIX + Integer.toString(i);
			} while (!mng.isNameAvailable(defName));
			
			// let user change the name of library
			String name = defName;
			
			if (askUserIfNotDefault) {
				boolean nameOK = true;
				do {
					name = JOptionPane.showInputDialog(this, "Enter the name of the new chord library:", defName);
					nameOK = mng.isNameAvailable(name);
					if (!nameOK) {
						// msg box
						JOptionPane.showMessageDialog(this, "The name is already used in currently loaded chord libraries files!");
					}
				} while (!nameOK);
			}
			
			// finally we have valid name
			return name;
		} else {
			// adding to existing library = take the same name as the chords there
			return ((Chord)listChords.getModel().getElementAt(0)).getLibrary();
			//return ((Chord)listChords.getSelectedValue()).getLibrary();
		}
	}

	public JButton getBtnNewChord() {
		return btnNewChord;
	}

	public void setBtnNewChord(JButton btnNewChord) {
		this.btnNewChord = btnNewChord;
	}

	public DefaultListModel getChordListModel() {
		return chordListModel;
	}

	public void setChordListModel(DefaultListModel chordList) {
		this.chordListModel = chordList;
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

	public boolean isUnsavedChords() {
		return unsavedChords;
	}

	public void setUnsavedChords(boolean unsavedChords) {
		this.unsavedChords = unsavedChords;
	}

}
