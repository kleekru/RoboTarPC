// RoboTar is a product of Life Upgrades Group, a company dedicated to helping everyone live with 
// ever changing technology options.
// RoboTar was built and developed by Kevin Krumwiede with assistance from Ytai Ben-Tsvi,
// who is the inventor of the IOIO board which makes up much of the brains for RoboTar.

// TODO Save Attributes for a chord including an image, name, a logical structure that can be sent to IOIO
// TODO Send full Chord to RoboTar based on button push
// TODO Send Next chord to RoboTar (saved in a song) based on a button push
// TODO Create songs page
// TODO Associate an image with each chord name
// TODO Add a custom chord
// TODO Create import of full songs based on XML file
// TODO Create a push button to download songs from an internet location (RESTful service?)
// TODO Create an About page


package ioio.robotar.pcconsole;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Insets;

import javax.swing.JFrame;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingConstants;

import java.awt.Point;

/**
 * RoboTar GUI main window.
 */
public class RoboTarStartPage implements ActionListener {

	private JFrame frmBlueAhuizote;
	public JButton btnChords;
	public JButton btnSongs;
	/**
	 * This is the field, which RoboTarIOIOforPCConsole reads in loop() 
	 * and uses its values to send them to the device.
	 */
	private ServoSettings servoSettings;
	
	/**
	 * This will hold all chord libraries loaded in one instance.
	 */
	private ChordManager chordManager;
	private RoboTarChordsPage chordsPage;
	private RoboTarSongsPage songsPage;
	
	/**
	 * Launch the application.
	 */
	public void mainstart(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//RoboTarStartPage window = new RoboTarStartPage();
					frmBlueAhuizote.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public RoboTarStartPage() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	public void initialize() {
		chordManager = new ChordManager();
		servoSettings = new ServoSettings();
		frmBlueAhuizote = new JFrame();
		frmBlueAhuizote.setBackground(new Color(0, 0, 255));
		frmBlueAhuizote.setBounds(100, 100, 800, 600);
		frmBlueAhuizote.getContentPane().setBackground(new Color(30, 144, 255));
		frmBlueAhuizote.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmBlueAhuizote.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setSize(new Dimension(50, 50));
		lblNewLabel.setLocation(new Point(0, 43));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		java.net.URL res = RoboTarStartPage.class.getResource("/data/BlueAhuizoteIcon.png");
		System.out.println(res.getPath());
		lblNewLabel.setIcon(new ImageIcon(res));
		//lblNewLabel.setIcon(new ImageIcon(RoboTarStartPage.class.getResource("/data/BlueAhuizoteIcon.png")));
		lblNewLabel.setBorder(null);
		frmBlueAhuizote.getContentPane().setBackground(Color.BLUE);
		frmBlueAhuizote.getContentPane().add(lblNewLabel, BorderLayout.WEST);
		
		btnChords = new JButton("");
		btnChords.addActionListener(this);
		btnChords.setForeground(Color.BLUE);
		btnChords.setMinimumSize(new Dimension(100, 100));
		btnChords.setMaximumSize(new Dimension(100, 100));
		btnChords.setBackground(Color.BLUE);
		btnChords.setName("ChordsButton");
		btnChords.setMargin(new Insets(0, 0, 0, 0));
		btnChords.setIcon(new ImageIcon(RoboTarStartPage.class.getResource("/data/chords.png")));
		btnChords.setSelectedIcon(null);
		btnChords.setRolloverIcon(null);
		btnChords.setToolTipText("Create or Browse Chords");
		btnChords.setRolloverSelectedIcon(null);
		btnChords.setMnemonic(KeyEvent.VK_C);
		frmBlueAhuizote.getContentPane().setBackground(Color.BLUE);
		frmBlueAhuizote.getContentPane().add(btnChords, BorderLayout.CENTER);
		
		btnSongs = new JButton("");
		btnSongs.addActionListener(this);
		btnSongs.setBorderPainted(false);
		btnSongs.setBackground(Color.BLUE);
		btnSongs.setForeground(Color.BLUE);
		btnSongs.setMargin(new Insets(0, 0, 0, 0));
		btnSongs.setToolTipText("Select or Create Songs");
		btnSongs.setMnemonic(KeyEvent.VK_S);
		btnSongs.setIcon(new ImageIcon(RoboTarStartPage.class.getResource("/data/SheetMusic.png")));
		btnSongs.setName("SongsButton");
		frmBlueAhuizote.getContentPane().setBackground(Color.BLUE);
		frmBlueAhuizote.getContentPane().add(btnSongs, BorderLayout.EAST);
		
		JLabel lblNewLabel_1 = new JLabel("");
		lblNewLabel_1.setForeground(new Color(30, 144, 255));
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setIcon(new ImageIcon(RoboTarStartPage.class.getResource("/data/RoboTarLogoFont3.png")));
		lblNewLabel_1.setBackground(Color.GRAY);
		frmBlueAhuizote.getContentPane().add(lblNewLabel_1, BorderLayout.NORTH);
		
		JLabel lblNewLabel_2 = new JLabel("");
		lblNewLabel_2.setIcon(new ImageIcon(RoboTarStartPage.class.getResource("/data/junglespeakermountainsmall.png")));
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		frmBlueAhuizote.getContentPane().add(lblNewLabel_2, BorderLayout.SOUTH);
		
		JMenuBar menuBar = new JMenuBar();
		frmBlueAhuizote.setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("File");
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmAbout = new JMenuItem("About RoboTar");
		mnNewMenu.add(mntmAbout);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("Exit");
		mnNewMenu.add(mntmNewMenuItem);
		mntmNewMenuItem.setName("Exit");
		
		JMenu mnChordLauncher = new JMenu("Chord Launcher");
		menuBar.add(mnChordLauncher);
		
		JMenuItem mntmChords = new JMenuItem("Chords");
		mnChordLauncher.add(mntmChords);
		
		JMenuItem mntmSongs = new JMenuItem("Songs");
		mnChordLauncher.add(mntmSongs);
		
		JMenuItem mntmSongPlayer = new JMenuItem("Song Player");
		mnChordLauncher.add(mntmSongPlayer);
		
		JMenu mnUtilities = new JMenu("Utilities");
		menuBar.add(mnUtilities);
		
		JMenuItem mntmTunercomingSoon = new JMenuItem("Tuner (Coming Soon)");
		mnUtilities.add(mntmTunercomingSoon);
		
		JMenuItem mntmMetronomecomingSoon = new JMenuItem("Metronome (Coming Soon)");
		mnUtilities.add(mntmMetronomecomingSoon);
		
		JMenuItem mntmSongDownloadscomingSoon = new JMenuItem("Song Downloads(Coming Soon)");
		mnUtilities.add(mntmSongDownloadscomingSoon);
	}
	
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == btnChords) {
			setChordsPage(new RoboTarChordsPage(this));
			getChordsPage().setVisible(true);
		}
		if (event.getSource() == btnSongs) {
			setSongsPage(new RoboTarSongsPage(this));
			getSongsPage().setVisible(true);
	}
	}

	public ChordManager getChordManager() {
		// TODO rewrite to better use of singleton pattern - synchronize!
		if (!chordManager.isInitialized()) {
			chordManager.initialize();
		}
		return chordManager;
	}

	public void setChordManager(ChordManager chordManager) {
		this.chordManager = chordManager;
	}

	public RoboTarChordsPage getChordsPage() {
		return chordsPage;
	}

	public void setChordsPage(RoboTarChordsPage chordsPage) {
		this.chordsPage = chordsPage;
	}

	public RoboTarSongsPage getSongsPage() {
		return songsPage;
	}

	public void setSongsPage(RoboTarSongsPage songsPage) {
		this.songsPage = songsPage;
	}

	public ServoSettings getServoSettings() {
		return servoSettings;
	}

	public void setServoSettings(ServoSettings chordServo) {
		this.servoSettings = chordServo;
	}
}
