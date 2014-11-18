package com.robotar.ui;


import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import cz.versarius.xchords.Chord;
import cz.versarius.xchords.ChordLibrary;
import cz.versarius.xchords.ChordManager;
import cz.versarius.xchords.XML2SVG;
import cz.versarius.xchords.XMLChordSaver;

import javax.swing.JLabel;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.Font;
import java.awt.Dimension;

import javax.swing.JButton;

import java.awt.Insets;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.ListSelectionModel;
import javax.swing.JToggleButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kitfox.svg.SVGCache;
import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.app.beans.SVGPanel;
import com.robotar.ioio.LEDSettings;
import com.robotar.ioio.ServoSettings;

import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.ComponentOrientation;
import java.awt.RenderingHints;
import java.awt.Toolkit;

public class RoboTarChordsPage extends JFrame implements WindowListener {
	private static final long serialVersionUID = -4977090038183485379L;

	static final Logger LOG = LoggerFactory.getLogger(RoboTarChordsPage.class);

	private JPanel frmBlueAhuizoteChords;
	
	/** reference to mainframe and chordmanager */
	private RoboTarPC mainFrame;
	private ResourceBundle messages;

	private DefaultListModel libraryListModel;
	private JList listLibraries;
	private DefaultListModel chordListModel;
	private JList listChords;

	private ChordRadioPanel radioPanel;
	private JTextField chordName;
	private String chordNameSend;
	private SVGPanel chordSVG;
	private JLabel activeSong;
		
	private JToggleButton tglbtnTestChord;
	private JButton btnAddToSong;
	private JButton btnAddToChordList;

	private JButton btnLoadChords;
	private JButton btnSaveChords;
	private JButton btnLoadDefault;
	private JButton btnClearChords;
	private JButton btnNewLibrary;
	private JButton btnNewChord;
	
	private boolean unsavedChords;

	private XML2SVG dyn = new XML2SVG();

	/**
	 * Create the frame.
	 * 
	 * @param chordReceived
	 */
	public RoboTarChordsPage(final RoboTarPC mainFrame) {
		super();
		this.setMainFrame(mainFrame);
		messages = mainFrame.getMessages();
		
		setTitle(messages.getString("robotar.chords.title"));
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				RoboTarChordsPage.class
						.getResource("/data/BlueAhuizoteIcon.png")));
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		
		frmBlueAhuizoteChords = new JPanel() {

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
		frmBlueAhuizoteChords.setOpaque(false);
		frmBlueAhuizoteChords.setPreferredSize(new Dimension(850, 410));
		frmBlueAhuizoteChords.setAlignmentY(Component.TOP_ALIGNMENT);
		frmBlueAhuizoteChords.setAlignmentX(Component.LEFT_ALIGNMENT);
		//frmBlueAhuizoteChords.setBackground(Const.BACKGROUND_COLOR);
		frmBlueAhuizoteChords.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(frmBlueAhuizoteChords);
		GridBagLayout gbl_frmBlueAhuizoteChords = new GridBagLayout();
		gbl_frmBlueAhuizoteChords.columnWidths = new int[] { 100, 20, 100, 20, 250, 20, 250, 20 };
		gbl_frmBlueAhuizoteChords.rowHeights = new int[] { 24, 24, 144, 20, 36, 36, 32, 36, 50, 20, 24 };
		gbl_frmBlueAhuizoteChords.columnWeights = new double[]{ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0 };
		gbl_frmBlueAhuizoteChords.rowWeights = new double[]{ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0 };
		frmBlueAhuizoteChords.setLayout(gbl_frmBlueAhuizoteChords);
		
		JLabel activeSongLbl = new JLabel(messages.getString("robotar.chords.active_song"));
		activeSongLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
		GridBagConstraints gbc_activeSongLbl = new GridBagConstraints();
		gbc_activeSongLbl.fill = GridBagConstraints.HORIZONTAL;
		gbc_activeSongLbl.gridx = 4;
		gbc_activeSongLbl.gridy = 7;
		frmBlueAhuizoteChords.add(activeSongLbl, gbc_activeSongLbl);
						
		activeSong = new JLabel(messages.getString("robotar.chords.no_song_selected"));
		GridBagConstraints gbc_activeSong = new GridBagConstraints();
		gbc_activeSong.fill = GridBagConstraints.HORIZONTAL;
		gbc_activeSong.gridx = 4;
		gbc_activeSong.gridy = 8;
		frmBlueAhuizoteChords.add(activeSong, gbc_activeSong);
		/*activeSong.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null,
				null, null));*/
		activeSong.setFont(new Font("Segoe UI", Font.BOLD, 14));
				
		// new library button
		btnNewLibrary = new JButton(messages.getString("robotar.chords.new_library"));
		GridBagConstraints gbc_btnNewLibrary = new GridBagConstraints();
		gbc_btnNewLibrary.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnNewLibrary.gridx = 0;
		gbc_btnNewLibrary.gridy = 1;
		frmBlueAhuizoteChords.add(btnNewLibrary, gbc_btnNewLibrary);
		//btnNewLibrary.setBackground(Color.BLUE); //new Color(0, 0, 128));
		//btnNewLibrary.setOpaque(true);
		//btnNewLibrary.setBorderPainted(false);
		//btnNewLibrary.setForeground(Color.WHITE);
		btnNewLibrary.setMargin(new Insets(2, 1, 2, 1));
		btnNewLibrary.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				newLibraryButtonActionPerformed();
			}
		});
		
		// new chord button
		btnNewChord = new JButton(messages.getString("robotar.chords.new_chord"));
		GridBagConstraints gbc_btnNewChord = new GridBagConstraints();
		gbc_btnNewChord.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnNewChord.gridx = 2;
		gbc_btnNewChord.gridy = 1;
		frmBlueAhuizoteChords.add(btnNewChord, gbc_btnNewChord);
		//btnNewChord.setBackground(new Color(0, 0, 128));
		btnNewChord.setMargin(new Insets(2, 1, 2, 1));
		btnNewChord.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				newChordButtonActionPerformed();
			}
		});
		
		tglbtnTestChord = new JToggleButton(messages.getString("robotar.chords.test_chord_off"));
		/*tglbtnTestChord.setPressedIcon(new ImageIcon(RoboTarChordsPage.class
				.getResource("/data/TestChordOn.png")));*/
		GridBagConstraints gbc_tglbtnTestChord = new GridBagConstraints();
		gbc_tglbtnTestChord.gridx = 6;
		gbc_tglbtnTestChord.gridy = 0;
		//gbc_tglbtnTestChord.gridheight = 2;
		gbc_tglbtnTestChord.fill = GridBagConstraints.BOTH;
		frmBlueAhuizoteChords.add(tglbtnTestChord, gbc_tglbtnTestChord);
		/*tglbtnTestChord.setSelectedIcon(new ImageIcon(RoboTarChordsPage.class
				.getResource("/data/TestChordOn.png")));
		tglbtnTestChord.setIcon(new ImageIcon(RoboTarChordsPage.class
				.getResource("/data/TestChordOff.png")));*/
		tglbtnTestChord.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				prepareChord();
			}

		});

		// add to song button
		btnAddToSong = new JButton(messages.getString("robotar.chords.add_to_song"));
		GridBagConstraints gbc_btnAddToSong = new GridBagConstraints();
		gbc_btnAddToSong.anchor = GridBagConstraints.SOUTH;
		gbc_btnAddToSong.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnAddToSong.gridx = 4;
		gbc_btnAddToSong.gridy = 10;
		btnAddToSong.setFont(new Font("Segoe UI", Font.BOLD, 13));
		btnAddToSong.setToolTipText(messages.getString("robotar.chords.add_chord_to_song.tooltip"));
		frmBlueAhuizoteChords.add(btnAddToSong, gbc_btnAddToSong);
		btnAddToSong.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				addToSongActionPerformed();
			}
		});

		
		// TODO Need to update so this field shows a chord picture - use XChords
		chordSVG = new SVGPanel();
		chordSVG.setPreferredSize(new Dimension(130, 190));
		chordSVG.setMinimumSize(new Dimension(130, 190));
		//chordSVG.setAutosize(SVGPanel.AUTOSIZE_STRETCH);
		chordSVG.setAntiAlias(true);
		chordSVG.setOpaque(true);
		chordSVG.setVisible(false);
		//chordSVG.setBackground(Color.GREEN);
		//chordSVG.setScaleToFit(true); //SVGPanel.AUTOSIZE_BESTFIT);
		GridBagConstraints gbc_lblChordPicture = new GridBagConstraints();
		gbc_lblChordPicture.insets = new Insets(0, 0, 0, 0);
		gbc_lblChordPicture.gridx = 6;
		gbc_lblChordPicture.gridy = 4;
		gbc_lblChordPicture.gridheight = 5;
		gbc_lblChordPicture.anchor = GridBagConstraints.CENTER;
		frmBlueAhuizoteChords.add(chordSVG, gbc_lblChordPicture);

		// labels for radio panel
		JPanel radioLabelsPanel = new JPanel();
		radioLabelsPanel.setOpaque(false);
		//radioLabelsPanel.setBackground(Const.BACKGROUND_COLOR);
		GridBagConstraints gbc_radioLabelsPanel = new GridBagConstraints();
		gbc_radioLabelsPanel.insets = new Insets(0, 0, 5, 0);
		gbc_radioLabelsPanel.gridx = 4;
		gbc_radioLabelsPanel.gridy = 2;
		gbc_radioLabelsPanel.fill = GridBagConstraints.VERTICAL;
		gbc_radioLabelsPanel.anchor = GridBagConstraints.EAST;
		frmBlueAhuizoteChords.add(radioLabelsPanel, gbc_radioLabelsPanel);
		GridBagLayout radioLabelsLayout = new GridBagLayout();
		int rh = 24;
		radioLabelsLayout.rowHeights = new int[] { 18, rh, rh, rh, rh, rh, rh };
		radioLabelsLayout.rowWeights = new double[]{ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		radioLabelsPanel.setLayout(radioLabelsLayout);
		
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.fill = GridBagConstraints.HORIZONTAL;
		gbc_label.insets = new Insets(0, 0, 0, 0);
		gbc_label.gridx = 0;
		gbc_label.gridy = 1;
		// muted radio buttons
		JLabel lblClickToMute = new JLabel(messages.getString("robotar.chords.mute_string"), SwingConstants.RIGHT);
		radioLabelsPanel.add(lblClickToMute, gbc_label);
		lblClickToMute.setFont(new Font("Segoe UI", Font.BOLD, 16));
		gbc_label.gridy++;
		// open radio buttons
		JLabel lblOpenString = new JLabel(messages.getString("robotar.chords.open_string"), SwingConstants.RIGHT);
		lblOpenString.setFont(new Font("Segoe UI", Font.BOLD, 16));
		radioLabelsPanel.add(lblOpenString, gbc_label);
		// 1st fret
		gbc_label.gridy++;
		JLabel lblstFret = new JLabel(messages.getString("robotar.chords.first_fret"), SwingConstants.RIGHT);
		lblstFret.setFont(new Font("Segoe UI", Font.BOLD, 16));
		radioLabelsPanel.add(lblstFret, gbc_label);
		// 2nd fret
		gbc_label.gridy++;
		JLabel lblndFret = new JLabel(messages.getString("robotar.chords.second_fret"), SwingConstants.RIGHT);
		lblndFret.setFont(new Font("Segoe UI", Font.BOLD, 16));
		radioLabelsPanel.add(lblndFret, gbc_label);
		// 3rd fret
		gbc_label.gridy++;
		JLabel lblrdFret = new JLabel(messages.getString("robotar.chords.third_fret"), SwingConstants.RIGHT);
		lblrdFret.setFont(new Font("Segoe UI", Font.BOLD, 16));
		radioLabelsPanel.add(lblrdFret, gbc_label);
		// 4th fret
		gbc_label.gridy++;
		JLabel lblthFret = new JLabel(messages.getString("robotar.chords.fourth_fret"), SwingConstants.RIGHT);
		lblthFret.setFont(new Font("Segoe UI", Font.BOLD, 16));
		radioLabelsPanel.add(lblthFret, gbc_label);
		
		// radios
		radioPanel = new ChordRadioPanel();
		GridBagConstraints gbc_radioPanel = new GridBagConstraints();
		gbc_radioPanel.insets = new Insets(0, 0, 5, 0);
		gbc_radioPanel.gridx = 6;
		gbc_radioPanel.gridy = 2;
		gbc_radioPanel.fill = GridBagConstraints.NONE;
		gbc_radioPanel.anchor = GridBagConstraints.CENTER;
		frmBlueAhuizoteChords.add(radioPanel, gbc_radioPanel);
		
		// chord name
		JLabel lblChordName = new JLabel(messages.getString("robotar.chords.chord_name"));
		lblChordName.setForeground(Color.BLACK);
		lblChordName.setFont(new Font("Segoe UI", Font.BOLD, 16));
		GridBagConstraints gbc_lblChordName = new GridBagConstraints();
		gbc_lblChordName.gridx = 4;
		gbc_lblChordName.gridy = 4;
		gbc_lblChordName.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblChordName.anchor = GridBagConstraints.WEST;
		frmBlueAhuizoteChords.add(lblChordName, gbc_lblChordName);
		
		chordName = new JTextField();
		chordName.setColumns(10);
		chordName.setText(messages.getString("robotar.chords.enter_chord_name"));
		GridBagConstraints gbc_chordName = new GridBagConstraints();
		gbc_chordName.fill = GridBagConstraints.HORIZONTAL;
		gbc_chordName.gridx = 4;
		gbc_chordName.gridy = 5;
		frmBlueAhuizoteChords.add(chordName, gbc_chordName);
		
		// load chords
		btnLoadChords = new JButton(messages.getString("robotar.chords.load_chords"));
		btnLoadChords.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				try {
					loadChords();
				} catch (FileNotFoundException e1) {
					JOptionPane.showMessageDialog(RoboTarChordsPage.this, messages.getString("robotar.chords.file_not_found"));
				}
			}
		});
		GridBagConstraints gbc_btnLoadChords = new GridBagConstraints();
		gbc_btnLoadChords.gridx = 0;
		gbc_btnLoadChords.gridy = 0;
		gbc_btnLoadChords.fill = GridBagConstraints.HORIZONTAL;
		frmBlueAhuizoteChords.add(btnLoadChords, gbc_btnLoadChords);
		
		// save chords
		btnSaveChords = new JButton(messages.getString("robotar.chords.save_chords"));
		btnSaveChords.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				saveChords();
			}
		});
		GridBagConstraints gbc_btnSaveChords = new GridBagConstraints();
		gbc_btnSaveChords.gridx = 2;
		gbc_btnSaveChords.gridy = 0;
		gbc_btnSaveChords.fill = GridBagConstraints.HORIZONTAL;
		frmBlueAhuizoteChords.add(btnSaveChords, gbc_btnSaveChords);
		
		/*
		btnLoadDefault = new JButton(messages.getString("robotar.chords.load_default"));
		btnLoadDefault.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				loadDefaultChords(evt);
			}
		});
		btnPanel.add(btnLoadDefault);
		*/
		/*
		btnClearChords = new JButton(messages.getString("robotar.chords.clear_chords"));
		btnClearChords.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				clearChords(evt);
			}
		});
		btnPanel.add(btnClearChords);
		*/
		
		// library list
		JScrollPane libScrollPane = new JScrollPane();
		libScrollPane.setPreferredSize(new Dimension(90, 100));
		libScrollPane.setMaximumSize(new Dimension(100, 200));
		libScrollPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		GridBagConstraints gbc_libScrollPane = new GridBagConstraints();
		gbc_libScrollPane.fill = GridBagConstraints.BOTH;
		gbc_libScrollPane.gridx = 0;
		gbc_libScrollPane.gridheight = 9;
		gbc_libScrollPane.gridy = 2;
		frmBlueAhuizoteChords.add(libScrollPane, gbc_libScrollPane);
		listLibraries = new JList();
		listLibraries.setFont(new Font("Tahoma", Font.BOLD, 11));
		listLibraries.setCellRenderer(new ChordLibraryListCellRenderer());
		listLibraries.setVisibleRowCount(6);
		listLibraries.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//listLibraries.setBorder(new LineBorder(Color.GRAY, 3, true));
		listLibraries.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent event) {
				if (!event.getValueIsAdjusting()) {
					JList theList = (JList) event.getSource();
					DefaultListModel model = (DefaultListModel) theList.getModel();
					int selIdx = (int) theList.getSelectedIndex();
					if (selIdx >= 0) {
						if (unsavedChords) {
							int confirm = JOptionPane.showOptionDialog(RoboTarChordsPage.this,
									messages.getString("robotar.chords.unsaved_chords"),
									messages.getString("robotar.chords.unsaved_chords.title"), JOptionPane.YES_NO_OPTION,
					                JOptionPane.QUESTION_MESSAGE, null, null, null);
					        if (confirm == JOptionPane.YES_OPTION) {
					        	saveChords();
					        } else {
					        	unsavedChords = false;
					        	// probably. safe way is find library on chords-id (it may not exist..-deleted)
								ChordLibrary lib = mainFrame.getChordManager().findByName(mainFrame.getChordManager().getChosenLibrary());
								lib.setChanged(false);
					        }
						}
						ChordLibrary lib = (ChordLibrary) model.get(selIdx);
						mainFrame.getChordManager().setChosenLibrary(lib.getName());
						reloadChordList(lib.getName());
					}
				}
			}
		});
		libScrollPane.setViewportView(listLibraries);
		
		// chord list
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(90, 100));
		scrollPane.setMaximumSize(new Dimension(100, 200));
		scrollPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 2;
		gbc_scrollPane.gridheight = 9;
		gbc_scrollPane.gridy = 2;
		frmBlueAhuizoteChords.add(scrollPane, gbc_scrollPane);
		listChords = new JList();
		listChords.setFont(new Font("Tahoma", Font.BOLD, 11));
		listChords.setCellRenderer(new ChordListCellRenderer());
		listChords.setVisibleRowCount(6);
		listChords.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//listChords.setBorder(new LineBorder(Color.GRAY, 3, true));
		listChords.addListSelectionListener(new ListSelectionListener() {
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
		});
		scrollPane.setViewportView(listChords);
		
		// add to chord list
		btnAddToChordList = new JButton(messages.getString("robotar.chords.add_to_chord_list"));
		GridBagConstraints gbc_btnAddToChordList = new GridBagConstraints();
		gbc_btnAddToChordList.anchor = GridBagConstraints.SOUTH;
		gbc_btnAddToChordList.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnAddToChordList.gridx = 6;
		gbc_btnAddToChordList.gridy = 10;
		frmBlueAhuizoteChords.add(btnAddToChordList, gbc_btnAddToChordList);
		btnAddToChordList
				.setToolTipText(messages.getString("robotar.chords.add_chord_to_list.tooltip"));
		btnAddToChordList.setFont(new Font("Segoe UI", Font.BOLD, 13));
		btnAddToChordList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				addToChordListActionPerformed();
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
		setSize(820, 520);
		addWindowListener(this);
		
		// initialize chord libraries list
		reloadLibrariesList(mainFrame.getChordManager());
		// initialize with recent chord library, robotar by default
		//reloadChordList(mainFrame.getChordManager().getChosenLibrary());
		
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
					    ChordLibrary lib = getMainFrame().getChordManager().findByName(chosenLib);
					    lib.setChanged(true);
					    unsavedChords = true;
					    listLibraries.repaint();
					}
				}
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), JComponent.WHEN_FOCUSED);
		
		// this is causing problems with layout
		//pack();
		setLocationByPlatform(true);
		setVisible(true);
	}

	protected void newChordButtonActionPerformed() {
		if (listLibraries.isSelectionEmpty()) {
			JOptionPane.showMessageDialog(this,
        		    messages.getString("robotar.chords.no_library_selected"),
        		    messages.getString("robotar.chords.new_chord.title"),
        		    JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		radioPanel.clear();
		//radioPanel.setChordName(messages.getString("robotar.chords.enter_chord_name"));
		String generatedName = generateUnusedChordName();
		setChordName(generatedName);
		clearSelection();
		Chord chord = radioPanel.createChordFromRadios(((ChordLibrary)listLibraries.getSelectedValue()).getName(), generatedName);
		showChordImage(chord);
	}

	protected void newLibraryButtonActionPerformed() {
		if (unsavedChords) {
			int confirm = JOptionPane.showOptionDialog(RoboTarChordsPage.this,
					messages.getString("robotar.chords.unsaved_chords"),
					messages.getString("robotar.chords.new_chord.title"), JOptionPane.YES_NO_OPTION,
	                JOptionPane.QUESTION_MESSAGE, null, null, null);
	        if (confirm == JOptionPane.YES_OPTION) {
				if (!saveChords()) {
					LOG.error("can not save chords.... check log");
					return;
				}
			} else {
				unsavedChords = false;
				// probably. safe way is find library on chords-id (it may not exist..-deleted)
				ChordLibrary lib = mainFrame.getChordManager().findByName(mainFrame.getChordManager().getChosenLibrary());
				lib.setChanged(false);
			}
		}
		String newName = findLibraryName(true);
		if (newName != null) {
			ChordLibrary lib = new ChordLibrary();
			lib.setName(newName);
			mainFrame.getChordManager().getChordLibraries().put(newName, lib);
			mainFrame.getChordManager().setChosenLibrary(newName);
			reloadLibrariesList(mainFrame.getChordManager());
			listLibraries.setSelectedValue(lib, true);
		}
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

	protected void clearChords() {
		if (chordListModel != null) {
			chordListModel.removeAllElements();
		}
	}

	protected boolean saveChords() {
		// better condition?
		if ((chordListModel == null) || 
				(chordListModel != null && chordListModel.isEmpty())) {
			JOptionPane.showMessageDialog(this,
        		    messages.getString("robotar.chords.empty_chord_list"),
        		    messages.getString("robotar.chords.saving_chords.title"),
        		    JOptionPane.INFORMATION_MESSAGE);
			return false;
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
						// prepare file where to save
						XMLChordSaver saver = new XMLChordSaver();
						File file = null;
						if (lib.getPath() == null) {
							// create new one
							JFileChooser fc = new JFileChooser();
							int returnValue = fc.showSaveDialog(this);
							if (returnValue == JFileChooser.APPROVE_OPTION) {
					            file = fc.getSelectedFile();
					            lib.setPath(file.getAbsolutePath());
							} else {
								return false;
							}
						} else {
							file = new File(lib.getPath());
						}
						// adjust all chords in library to the current state of left list of chords!
						// add new, remove old, modify existing - the easiest is just copy everything
						// to the library again
						List<Chord> list = new ArrayList<Chord>();
						for (int i=0; i<chordListModel.size(); i++) {
			            	Chord c = (Chord)chordListModel.get(i);
			            	list.add(c);
			            }
						lib.setChords(list);
						
						saver.save(lib, file);
						
						// mark and save recent
						mng.setChosenLibrary(libraryName);
			            unsavedChords = false;
			            lib.setChanged(unsavedChords);
			            listLibraries.repaint();
			            return true;
					}
				} else if (ChordManager.DEFAULT_ROBOTAR.equals(libraryName)) {
					// user tries to save modified 'robotar' library
					JOptionPane.showMessageDialog(this,
							messages.getString("robotar.chords.dont_change_robotar_chords"),
							messages.getString("robotar.chords.dont_change_robotar_chords.title"),
		        		    JOptionPane.ERROR_MESSAGE);
					return false;
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
            	return true; // ? it is saved, but cannot be loaded
            }
            mng.setChosenLibrary(libName);
            unsavedChords = false;
            return true;
		}
		return false;
	}

	protected void loadChords() throws FileNotFoundException {
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
	 * Reload current list of chord libraries and set chosen library from chordmanager.
	 * @param mng
	 */
	@SuppressWarnings("unchecked")
	protected void reloadLibrariesList(ChordManager mng) {
		Map<String, ChordLibrary> libs = mng.getChordLibraries();
		if (libs.isEmpty()) {
			LOG.error("Cannot find any chord library, empty chord manager", libs);
			return;
		}
		libraryListModel = new DefaultListModel();
		for (Map.Entry<String, ChordLibrary> entry : libs.entrySet()) {
			libraryListModel.addElement(entry.getValue());
		}
		if (!libraryListModel.isEmpty()) {
			Collections.sort((List<ChordLibrary>)Collections.list(libraryListModel.elements()));
		}
		listLibraries.setModel(libraryListModel);
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

	protected void loadDefaultChords() {
		// populate the list with default chords set
		reloadChordList(ChordManager.DEFAULT_ROBOTAR);
	}

	private void clearSelection() {
		listChords.clearSelection();
		//lblChordPicture.setText(messages.getString("robotar.chords.no_chord_selected"));
		//lblChordPicture.setIcon(null);
		radioPanel.clear();
		chordSVG.setVisible(false);
	}
	
	public void setChordName(String name) {
		chordName.setText(name);
		chordName.selectAll();
		chordName.requestFocusInWindow();
	}
	
	public String getChordName() {
		return chordName.getText();
	}
	
	public JPanel getFrmBlueAhuizoteChords() {
		return frmBlueAhuizoteChords;
	}

	public void setFrmBlueAhuizoteChords(JPanel frmBlueAhuizoteChords) {
		this.frmBlueAhuizoteChords = frmBlueAhuizoteChords;
	}

	public void addToChordListActionPerformed() {
		chordNameSend = getChordName();
		if (isValidChordName(chordNameSend)) {
			// prepare chord and chordlistmodel
			String libName = getLibraryName(true);
			Chord chord = radioPanel.createChordFromRadios(libName, chordNameSend);
			if (chordListModel == null) {
				chordListModel = new DefaultListModel();
				listChords.setModel(chordListModel);
			}
			
			// check if the name(id) is unique
			int idx = chordListModel.indexOf(chord);
			if (idx != -1) {
				int confirm = JOptionPane.showOptionDialog(RoboTarChordsPage.this,
						messages.getString("robotar.chords.add_chord_to_list.exist"),
						messages.getString("robotar.chords.add_chord_to_list.exist.title"), JOptionPane.YES_NO_OPTION,
		                JOptionPane.QUESTION_MESSAGE, null, null, null);
		        if (confirm == JOptionPane.NO_OPTION) {
		        	return;
		        }
			}
			
			// add chord to the list
			LOG.info("adding chord to chord list");
			if (idx != -1) {
				chordListModel.set(idx, chord);
			} else {
				chordListModel.addElement(chord);
			}
			setChordName(null);
			clearSelection();
			unsavedChords = true;
			mainFrame.getChordManager().findByName(libName).setChanged(true);
			listLibraries.repaint();
			// update SVGimage in SVGCache
			createSVG(chord);
		} else {
			JOptionPane.showMessageDialog(RoboTarChordsPage.this, messages.getString("robotar.chords.fill_chord_name"));
		}
	}

	public void addToSongActionPerformed() {
		chordNameSend = getChordName();
		if (isValidChordName(chordNameSend)) {
			LOG.info("adding chord to song chord list: {}", chordNameSend);
			Chord chord = radioPanel.createChordFromRadios(getLibraryName(false), chordNameSend);
			if (mainFrame.getSongsPage() != null) {
				boolean result = mainFrame.getSongsPage().addChordToUsedChords(chord);
				if (result) {
					setChordName(null);
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
	 * Updates all UI components with current chord.
	 * @param chord
	 */
	private void setChord(Chord chord) {
		setChordName(chord.getName());
		radioPanel.setupRadios(chord);
		showChordImage(chord);
		prepareChord();
		LOG.info("chordid: {}", chord.getId());
	}
	
	/**
	 * Creates SVG image, in SVGCache and returns URI to it.
	 * @param chord
	 * @return
	 */
	private URI createSVG(Chord chord) {
		Writer w = dyn.transform(chord);
		if (w != null) {
			URI uri = SVGCache.getSVGUniverse().loadSVG(new StringReader(w.toString()), chord.getId(), true);
			return uri;
		}
		return null;
	}
	
	/**
	 * Generates SVG image of the chord on the fly
	 * @param chord
	 */
	public void showChordImage(Chord chord) {
		String chordName = chord.getName();
		URI uri = createSVG(chord);	
		if (uri != null) {
			//lblChordPicture.setIcon(new ImageIcon(ImageIO.read(res)));
			//SVGIcon icon = new SVGIcon();
			//icon.setSvgURI(uri);
			chordSVG.setVisible(true);
			chordSVG.setSvgURI(uri);
			System.out.println("h:" + chordSVG.getSVGHeight());
			System.out.println("w:" + chordSVG.getSVGWidth());
			System.out.println("d:" + chordSVG.getSize());
			SVGDiagram diag = chordSVG.getSvgUniverse().getDiagram(uri);
			chordSVG.repaint();
			//lblChordPicture.setIcon(icon);
			//lblChordPicture.setText("");
		} else {
			LOG.error("svg not generated for: {}", chordName);
			//lblChordPicture.setText(messages.getString("robotar.chords.image_not_found"));
		}
	}

	/**
	 * Fills servoSettings field to be used in loop() - RoboTarConsole
	 */
	public void prepareChord() {
		if (tglbtnTestChord.isSelected()) {
			tglbtnTestChord.setText(messages.getString("robotar.chords.test_chord_on"));
			// use radio panel as source for chord, unfilled radios will be marked OPEN
			Chord chord = radioPanel.createChordFromRadios(getLibraryName(false), getChordName());
			mainFrame.getServoSettings().setChord(chord);
			LEDSettings leds = new LEDSettings(chord);
			mainFrame.setLeds(leds);
			LOG.debug("preparing servos Values on chords page: {}", mainFrame.getServoSettings().debugOutput());
			LOG.debug("preparing leds on chords page: {}", leds.debugOutput());
		} else {
			tglbtnTestChord.setText(messages.getString("robotar.chords.test_chord_off"));
			// release
			mainFrame.getServoSettings().setInitialPosition();
			LEDSettings leds = new LEDSettings();
			mainFrame.setLeds(leds);
			LOG.debug("releasing all");
			LOG.debug("releasing servos: {}", mainFrame.getServoSettings().debugOutput());
			LOG.debug("turning off LEDs: {}", leds.debugOutput());
		}
	}
	
	/**
	 * Create new default name of the library and (optionally) let the user change it.
	 * Validates non-existence in loaded libraries list. (manager)
	 * @param askUserIfNotDefault
	 * @return
	 */
	public String findLibraryName(boolean askUserIfNotDefault) {
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
				name = JOptionPane.showInputDialog(this, messages.getString("robotar.chords.enter_library_name"), defName);
				nameOK = mng.isNameAvailable(name);
				if (!nameOK) {
					// msg box
					JOptionPane.showMessageDialog(this, messages.getString("robotar.chords.library_name_used"));
				}
			} while (!nameOK);
		}
		
		// finally we have valid name
		return name;
	}
	
	/**
	 * Get selected library name or create new one, if needed, ask user for the name.
	 * @param askUserIfNotDefault
	 * @return
	 */
	public String getLibraryName(boolean askUserIfNotDefault) {
		DefaultListModel model = (DefaultListModel)listLibraries.getModel();
		if (model == null || model.isEmpty() || listLibraries.isSelectionEmpty()) {
			// chord libraries list is empty - choose new name of the library
			return findLibraryName(askUserIfNotDefault);
		} else {
			// adding to existing library = take the same name as the chords there
			// it is already set as chosen library (listselectedevent)
			return (mainFrame.getChordManager().getChosenLibrary());
			//return ((Chord)listChords.getModel().getElementAt(0)).getLibrary();
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

	public RoboTarPC getMainFrame() {
		return mainFrame;
	}

	public void setMainFrame(RoboTarPC mainFrame) {
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
			activeSong.setText("<html>" + messages.getString("robotar.chords.no_song_selected") + "</html>");
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
