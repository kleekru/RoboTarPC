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


package com.robotar.ui;

import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.TwiMaster;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.pc.IOIOSwingApp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Window;

import javax.swing.JFrame;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import net.infotrek.util.prefs.FilePreferencesFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.robotar.ioio.LEDSettings;
import com.robotar.ioio.Pins;
import com.robotar.ioio.ServoSettings;
import com.robotar.ioio.showcase.ShowcasePatterns;
import com.robotar.ui.Const;
import com.robotar.util.RoboTarPreferences;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.BackingStoreException;

/**
 * RoboTar GUI main window.
 */
public class RoboTarPC extends IOIOSwingApp {

	private static final Logger LOG = LoggerFactory.getLogger(RoboTarPC.class);
	
	private JFrame frmBlueAhuizote;
	public JButton btnChords;
	public JButton btnSongs;

	/**
	 * This is the field, which RoboTarIOIOforPCConsole reads in loop() 
	 * and uses its values to send them to the device.
	 */
	private ServoSettings servoSettings;
	
	/**
	 * LED settings for the current chord.
	 */
	private LEDSettings leds;
	private boolean stateLedOff = true;

	/**
	 * Burn prevention field. (+
	 */
	private boolean recentActivity = false;
	
	/**
	 * This will hold all chord libraries loaded in one instance.
	 */
	private ChordManagerPC chordManager = new ChordManagerPC();
	
	private RoboTarChordsPage chordsPage;
	private RoboTarSongsPage songsPage;
	private RoboTarHelp helpPage;
	
	private ResourceBundle messages;

	/** per user preferences */
	private RoboTarPreferences preferences = RoboTarPreferences.load();

	protected boolean showChecked;

	protected ShowcasePatterns patterns;

	private IOIOReconnectAction ioioReconnectAction;

	private String remoteVersion;
	private String localVersion = "0.3.2";
	
	public static final String ROBOTAR_FOLDER = ".robotar";
	public static final String ROBOTAR_PROPS_FILE = ".robotar.properties";
	
	public static void main(String[] args) throws Exception {
		// set preferences factory implementation and filename
		System.setProperty("java.util.prefs.PreferencesFactory", FilePreferencesFactory.class.getName());
	    System.setProperty(FilePreferencesFactory.SYSTEM_PROPERTY_FILE, 
	    		ROBOTAR_FOLDER + File.separator + ROBOTAR_PROPS_FILE);
	 
	    // check that the folder .robotar exists, if not, create it. 
	    String userHome = System.getProperty("user.home");
	    String folder = userHome + File.separator + ROBOTAR_FOLDER;
	    /*Path robotarFolder = Paths.get(folder);
	    if (!Files.exists(robotarFolder)) {
	    	try {
	    		Files.createDirectory(robotarFolder);
	    		LOG.debug(".robotar folder created");
	    	} catch (Exception e) {
	    		LOG.error("cannot create .robotar folder in user home!", e);
	    		// continue without it - problems will arise, but the software will be at least partially working
	    	}
	    }*/
	    File robotarFolder = new File(folder);
	    if (!robotarFolder.exists()) {
	    	try {
	    		if (robotarFolder.mkdir()) {
	    			LOG.debug(".robotar folder created");
	    		};
	    	} catch (Exception e) {
	    		LOG.error("cannot create .robotar folder in user home!", e);
	    		// continue.... problems will arise....
	    	}
	    }
	    
	    // now start the app
	    new RoboTarPC().go(args);
	}
	
	@Override
	protected Window createMainWindow(String[] args) {
		// ... create main window ...
		try {
			initialize();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		LOG.info("End of the Run method");
		if (frmBlueAhuizote == null) {
			LOG.error("frame still not available"); ///!!
		}
		return frmBlueAhuizote;
	}
	
	public RoboTarPreferences getPreferences() {
		return preferences;
	}

	/**
	 * Launch the application.
	 */
	public void mainstart(RoboTarPCConsole console, String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//this.console = console;
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public RoboTarPC() {
	}

	private void closingMethod() {
		StringBuilder sb = new StringBuilder(100);
		if (chordsPage != null && chordsPage.isUnsavedChords()) {
			sb.append(messages.getString("robotar.end.unsavedchords"));
		}
		if (songsPage != null && songsPage.getModifiedCount() > 0) {
			sb.append(messages.getString("robotar.end.unsavedsongs"));
			sb.append(songsPage.getModifiedCount());
			sb.append(messages.getString("robotar.end.unsavedsongs2"));
		}
		sb.append(messages.getString("robotar.end.question"));
		int confirm = JOptionPane.showOptionDialog(frmBlueAhuizote,
                sb.toString(),
                messages.getString("robotar.end.confirmation"), JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, null, null);
        if (confirm == JOptionPane.YES_OPTION) {
        	// save all needed information for next start
        	preferences.save();
        	LOG.info("RoboTar finished with dialog message: {}", sb.toString());
        	// and exit
            System.exit(0);
        }
    }
	
	private class ExitListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            closingMethod();
        }
    }
	
	private class ExitAdapter extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            closingMethod();
        }
    }
    
	/**
	 * Get content of the file at address URL.
	 * 
	 * @param address
	 * @return content or null
	 */
	private String getRemoteVersion(String address) {
		try {
	        URL url = new URL(address);
	         
	        BufferedReader br = new BufferedReader(
                    new InputStreamReader(url.openStream()));
	        String inputLine;
	        StringBuilder sb = new StringBuilder();
	        while ((inputLine = br.readLine()) != null) {
				sb.append(inputLine);
			}
	        br.close();
	        return sb.toString();
		} catch (IOException ex) {
			LOG.error("Problem with accessing address {}", address);
			LOG.error("exc", ex);
			return null;
		}
    }

	private boolean isNewerVersion(String version) {
		if (version == null || version.isEmpty()) {
			LOG.error("empty version information");
			return false;
		}
		LOG.debug("version: >{}<", version);
		String[] arr = version.split("\\.");
		LOG.debug("arr.length {}", arr.length);
		if (arr == null || arr.length != 3) {
			LOG.error("wrong format of version information");
			return false;
		}
		String[] arrLocal = localVersion.split("\\.");
		try {
			int remoteMajor = Integer.parseInt(arr[0], 10);
			int remoteMinor = Integer.parseInt(arr[1], 10);
			int remotePatch = Integer.parseInt(arr[2], 10);
			LOG.info("remote version: {}.{}.{}", remoteMajor, remoteMinor, remotePatch);
			
			int localMajor = Integer.parseInt(arrLocal[0], 10);
			int localMinor = Integer.parseInt(arrLocal[1], 10);
			int localPatch = Integer.parseInt(arrLocal[2], 10);
			LOG.info("local version: {}.{}.{}", localMajor, localMinor, localPatch);
			
			if (remoteMajor < localMajor) {
				return false;
			}
			if (remoteMinor < localMinor) {
				return false;
			}
			if (remotePatch <= localPatch) {
				return false;
			}
			LOG.info("!new version is available!");
			return true;
		} catch (NumberFormatException ex) {
			LOG.error("Problem with parsing version info", ex);
			return false;
		}
	}
	
	public boolean isNewerVersionAvailable() {
		//String url = "http://kleekru.mydomain.com/currentversion";
		String url = "https://raw.githubusercontent.com/kleekru/RoboTarPC/master/currentversion";
		LOG.info("checking for new version at address: {}", url);
		this.remoteVersion = getRemoteVersion(url);
		return isNewerVersion(remoteVersion);
	}
	
	public boolean displayVersionNotification() {
		return preferences.isCheckNewVersion();
	}
	
	/**
	 * Initialize the contents of the frame.
	 * @throws BackingStoreException 
	 */
	public void initialize() throws BackingStoreException {
		 /*try {
		    UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName() );
		 } catch (Exception e) {
		            e.printStackTrace();
		 }*/
	 
		getChordManager();
		servoSettings = ServoSettings.loadCorrectionsFrom(new File(preferences.getCorrectionsFile()), ROBOTAR_FOLDER);
		messages = ResourceBundle.getBundle("com.robotar.util.RoboTarBundle", preferences.getLocale());
		
		frmBlueAhuizote = new JFrame();
		
		JPanel frmBlueAhuizotePC = new JPanel() {

			@Override
			protected void paintComponent(Graphics g) {
				// TODO Auto-generated method stub
				Graphics2D g2d = (Graphics2D) g;
		                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		                        RenderingHints.VALUE_ANTIALIAS_ON);
		 
		                GradientPaint gp = new GradientPaint(0, 0,
		                       Const.BACKGROUND_COLOR.brighter(), 0, getHeight(),
		                       Color.BLUE);
		 
		                g2d.setPaint(gp);
		                g2d.fillRect(0, 0, getWidth(), getHeight());

				super.paintComponent(g);
			}
		};
		frmBlueAhuizotePC.setOpaque(false);
		frmBlueAhuizote.setContentPane(frmBlueAhuizotePC);
		//frmBlueAhuizote.setBackground(new Color(0, 0, 255));
		//frmBlueAhuizote.setBounds(100, 100, 800, 600);
		//frmBlueAhuizote.getContentPane().setBackground(Color.BLUE); //Const.BACKGROUND_COLOR);
		frmBlueAhuizote.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frmBlueAhuizote.getContentPane().setLayout(new BorderLayout(0, 0));
		frmBlueAhuizotePC.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		java.net.URL res = RoboTarPC.class.getResource("/data/BlueAhuizoteIcon.png");
		lblNewLabel.setIcon(new ImageIcon(res));
		//lblNewLabel.setIcon(new ImageIcon(RoboTarStartPage.class.getResource("/data/BlueAhuizoteIcon.png")));
		lblNewLabel.setBorder(null);
		frmBlueAhuizote.getContentPane().add(lblNewLabel, BorderLayout.WEST);
		
		Action startChordsAction = new StartChordsPageAction(messages.getString("robotar.menu.chords"), KeyEvent.VK_C);
		btnChords = new JButton("");
		btnChords.addActionListener(startChordsAction);
		btnChords.setBorderPainted(false);
		btnChords.setMargin(new Insets(0, 0, 0, 0));
		btnChords.setToolTipText("Create or Browse Chords");
		btnChords.setIcon(new ImageIcon(RoboTarPC.class.getResource("/data/chords.png")));
		frmBlueAhuizote.getContentPane().add(btnChords, BorderLayout.CENTER);
		
		Action startSongsAction = new StartSongsPageAction(messages.getString("robotar.menu.songs"), KeyEvent.VK_S);
		btnSongs = new JButton("");
		btnSongs.addActionListener(startSongsAction);
		btnSongs.setBorderPainted(false);
		btnSongs.setMargin(new Insets(0, 0, 0, 0));
		btnSongs.setToolTipText("Select or Create Songs");
		btnSongs.setIcon(new ImageIcon(RoboTarPC.class.getResource("/data/SheetMusic.png")));
		frmBlueAhuizote.getContentPane().add(btnSongs, BorderLayout.EAST);
		
		ioioReconnectAction = new IOIOReconnectAction(messages.getString("robotar.menu.reconnect"), KeyEvent.VK_R);
		
		JLabel lblNewLabel_1 = new JLabel("");
		lblNewLabel_1.setForeground(new Color(30, 144, 255));
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setIcon(new ImageIcon(RoboTarPC.class.getResource("/data/RoboTarLogoFont3.png")));
		lblNewLabel_1.setBackground(Color.GRAY);
		frmBlueAhuizote.getContentPane().add(lblNewLabel_1, BorderLayout.NORTH);
		
		JLabel lblNewLabel_2 = new JLabel("");
		lblNewLabel_2.setIcon(new ImageIcon(RoboTarPC.class.getResource("/data/junglespeakermountainsmall.png")));
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		frmBlueAhuizote.getContentPane().add(lblNewLabel_2, BorderLayout.SOUTH);
		
        frmBlueAhuizote.addWindowListener(new ExitAdapter());
        
		JMenuBar menuBar = new JMenuBar();
		frmBlueAhuizote.setJMenuBar(menuBar);
		
		JMenu mnFileMenu = new JMenu(messages.getString("robotar.menu.file"));
		menuBar.add(mnFileMenu);
		
		JMenuItem mntmAbout = new JMenuItem(new AboutAction(messages.getString("robotar.menu.about")));
		mnFileMenu.add(mntmAbout);
		
		JMenuItem mntmExit = new JMenuItem(messages.getString("robotar.menu.exit"));
		mntmExit.addActionListener(new ExitListener());
		mntmExit.setMnemonic(KeyEvent.VK_X);
		mnFileMenu.add(mntmExit);
		
		JMenu mnLauncher = new JMenu(messages.getString("robotar.menu.launcher"));
		menuBar.add(mnLauncher);
		
		JMenuItem mntmChords = new JMenuItem(startChordsAction);
		mnLauncher.add(mntmChords);
		
		JMenuItem mntmSongs = new JMenuItem(startSongsAction);
		mnLauncher.add(mntmSongs);
		
		JMenu mnIOIO = new JMenu(messages.getString("robotar.menu.ioio"));
		menuBar.add(mnIOIO);
		
		JMenuItem mntmReconnect = new JMenuItem(ioioReconnectAction);
		ioioReconnectAction.setEnabled(false); // until it will be implemented...
		mnIOIO.add(mntmReconnect);
		
		JMenu mnUtilities = new JMenu(messages.getString("robotar.menu.utilities"));
		menuBar.add(mnUtilities);
		
		JMenuItem corr = new JMenuItem(new CorrectionsAction(messages.getString("robotar.menu.servo_corrections"), KeyEvent.VK_E));
		mnUtilities.add(corr);
		
		JMenuItem mntmSettings = new JMenuItem(new StartSettingsPageAction(messages.getString("robotar.menu.settings"), KeyEvent.VK_T));
		mnUtilities.add(mntmSettings);
		
		JMenuItem mntmTuner = new JMenuItem(messages.getString("robotar.menu.tuner"));
		mntmTuner.setEnabled(false);
		mnUtilities.add(mntmTuner);
		
		JMenuItem mntmMetronome = new JMenuItem(messages.getString("robotar.menu.metronome"));
		mntmMetronome.setEnabled(false);
		mnUtilities.add(mntmMetronome);
		
		JMenuItem mntmSongDownloads = new JMenuItem(messages.getString("robotar.menu.song_downloads"));
		mntmSongDownloads.setEnabled(false);
		mnUtilities.add(mntmSongDownloads);
		
		final JCheckBoxMenuItem mntmShow = new JCheckBoxMenuItem(messages.getString("robotar.menu.show"));
		mnUtilities.add(mntmShow);
		mntmShow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showChecked = !showChecked;
				mntmShow.setSelected(showChecked);
				if (patterns != null && !showChecked) {
					patterns.quit();
				}
			}
		});
		
		JMenu mnLang = new JMenu(messages.getString("robotar.menu.lang_sel"));
		menuBar.add(mnLang);
		
		JMenuItem mntmEnglish = new JMenuItem(messages.getString("robotar.menu.english"));
		mnLang.add(mntmEnglish);
		mntmEnglish.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateLocale(Locale.ENGLISH);
			}
		});
		JMenuItem mntmSpanish = new JMenuItem(messages.getString("robotar.menu.spanish"));
		//mntmSpanish.setEnabled(false);
		mnLang.add(mntmSpanish);
		mntmSpanish.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateLocale(new Locale("es", "ES"));
			}
		});
		JMenuItem mntmCzech = new JMenuItem(messages.getString("robotar.menu.czech"));
		mnLang.add(mntmCzech);
		mntmCzech.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateLocale(new Locale("cs", "CZ"));
			}
		});
		JMenuItem mntmGerman = new JMenuItem(messages.getString("robotar.menu.german"));
		mnLang.add(mntmGerman);
		mntmGerman.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateLocale(new Locale("de", "DE"));
			}
		});
		
		JMenu mnHelp = new JMenu(messages.getString("robotar.menu.help"));
		menuBar.add(mnHelp);
		
		Action startHelpAction = new StartHelpPageAction(messages.getString("robotar.menu.help"), KeyEvent.VK_H);
		JMenuItem mntmHelp = new JMenuItem(messages.getString("robotar.menu.robotar_help"));
		mnHelp.add(mntmHelp);
		mntmHelp.addActionListener(startHelpAction);
		
		
		frmBlueAhuizote.setSize(818, 560);
		frmBlueAhuizote.setPreferredSize(new Dimension(818, 560));
		frmBlueAhuizote.pack();
		frmBlueAhuizote.setLocationByPlatform(true);
		frmBlueAhuizote.setVisible(true);
		frmBlueAhuizote.setTitle(messages.getString("robotar.name"));
		
		// display warning if device not yet configured!
		if (!servoSettings.isAnyCorrectionSet()) {
			// this is crucial, therefore it is modal dialog box (connection to ioio is stopped)
			JOptionPane.showMessageDialog(frmBlueAhuizote, 
					messages.getString("robotar.corrections.notset"), 
					"RoboTar WARNING", JOptionPane.WARNING_MESSAGE);
		}
		
		// check for newer versions
		if (displayVersionNotification() && isNewerVersionAvailable()) {
			// this is informational only, therefore it is modeless, connection to ioio continues
			JDialog dialog3 = new JDialog(frmBlueAhuizote, messages.getString("robotar.version.title"));
		    dialog3.setBounds(200, 300, 500, 120);
		    dialog3.setBackground(Const.BACKGROUND_COLOR);
			
		    JLabel label = new JLabel(MessageFormat.format(messages.getString("robotar.version.available"), remoteVersion));
		    JLabel labelYours = new JLabel(MessageFormat.format(messages.getString("robotar.version.yours"), localVersion));
		    JTextField textUrl = new JTextField("https://github.com/kleekru/RoboTarPC/releases/latest");
		    textUrl.setEditable(false);
		    JLabel labelDesc = new JLabel(messages.getString("robotar.version.desc"));
		    dialog3.getContentPane().setLayout(new BoxLayout(dialog3.getContentPane(), BoxLayout.Y_AXIS));
		    dialog3.getContentPane().add(label);
		    dialog3.getContentPane().add(labelYours);
		    dialog3.getContentPane().add(labelDesc);
		    dialog3.getContentPane().add(textUrl);
		    //dialog3.pack();
		    dialog3.setVisible(true);
		    dialog3.repaint();
		}
	}
	
	private void updateLocale(Locale locale) {
		preferences.setLocale(locale);
	}
	protected void showCorrectionsDialog(ActionEvent evt) {
		CorrectionsDialog dlg = new CorrectionsDialog(this);
		dlg.setLocationRelativeTo(frmBlueAhuizote);
		dlg.setVisible(true);
	}

	protected void showAboutDialog(ActionEvent evt) {
		AboutDialog dlg = new AboutDialog(this);
		dlg.setLocationRelativeTo(frmBlueAhuizote);
		dlg.setVisible(true);
	}
	
	protected void showSettingsDialog(ActionEvent evt) {
		RoboTarSettingsDialog dlg = new RoboTarSettingsDialog(this);
		dlg.setLocationRelativeTo(frmBlueAhuizote);
		dlg.setVisible(true);
	}
	
	private abstract class MyAction extends AbstractAction {
		public MyAction(String text, int mnemonic) {
			super(text);
		    putValue(MNEMONIC_KEY, mnemonic);
		}
	}
	
	private class StartChordsPageAction extends MyAction {
		public StartChordsPageAction(String text, int mnemonic) {
	       super(text, mnemonic);
	    }

		@Override
		public void actionPerformed(ActionEvent e) {
			startChordsPage();
		}
	}
	
	private class StartSongsPageAction extends MyAction {
		public StartSongsPageAction(String text, int mnemonic) {
	       super(text, mnemonic);
	    }

		@Override
		public void actionPerformed(ActionEvent e) {
			startSongsPage();
		}
	}
	
	private class StartSettingsPageAction extends MyAction {
		public StartSettingsPageAction(String text, int mnemonic) {
	       super(text, mnemonic);
	    }

		@Override
		public void actionPerformed(ActionEvent e) {
			showSettingsDialog(e);
		}
	}
	
	private class IOIOReconnectAction extends MyAction {
		public IOIOReconnectAction(String text, int mnemonic) {
		       super(text, mnemonic);
		    }

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO ??
				// call stop first?
				// helper_.stop() 
				// does this start new connection or not?
				helper_.start();
			}
	}
	
	private class StartHelpPageAction extends MyAction {
		public StartHelpPageAction(String text, int mnemonic) {
	       super(text, mnemonic);
	    }

		@Override
		public void actionPerformed(ActionEvent e) {
			startHelpPage();
		}
	}
	
	private class CorrectionsAction extends MyAction {
		public CorrectionsAction(String text, int mnemonic) {
			super(text, mnemonic);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			showCorrectionsDialog(e);
		}
	};
		
	private class AboutAction extends AbstractAction {

		public AboutAction(String string) {
			super(string);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			showAboutDialog(e);
		}
		
	}
	
	public void startChordsPage() {
		if (chordsPage == null) {
			chordsPage = new RoboTarChordsPage(this);
		}
		getChordsPage().setVisible(true);
	}
	
	public RoboTarChordsPage getChordsPage() {
		return chordsPage;
	}

	public void startHelpPage() {
		/*if (helpPage == null) {
			helpPage = new RoboTarHelp(this);
		}
		getHelpPage().setVisible(true);
		*/
		// we don't have help bundled. instead use www pages 
		JOptionPane.showMessageDialog(frmBlueAhuizote, 
				messages.getString("robotar.help.home"), 
				"RoboTar Help", JOptionPane.INFORMATION_MESSAGE);
	}
	
	public RoboTarHelp getHelpPage() {
		return helpPage;
	}

	
	public void startSongsPage() {
		if (songsPage == null) {
			songsPage = new RoboTarSongsPage(this);
		}
		getSongsPage().setVisible(true);
	}

	public ChordManagerPC getChordManager() {
		// TODO rewrite to better use of singleton pattern - synchronize!
		if (!chordManager.isInitialized()) {
			chordManager.initialize(preferences);
		}
		return chordManager;
	}

	public void setChordManager(ChordManagerPC chordManager) {
		this.chordManager = chordManager;
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

	public void setHelpPage(RoboTarHelp helpPage) {
		this.helpPage = helpPage;
	}
	
	public ServoSettings getServoSettings() {
		return servoSettings;
	}

	public void setServoSettings(ServoSettings chordServo) {
		this.servoSettings = chordServo;
	}

	public LEDSettings getLeds() {
		return leds;
	}

	public void setLeds(LEDSettings leds) {
		this.leds = leds;
	}

	// mediator pattern...? 
	public boolean isActiveSongEditable() {
		return (songsPage != null && songsPage.isEditing()); 
	}

	public ResourceBundle getMessages() {
		return messages;
	}

	public void setMessages(ResourceBundle messages) {
		this.messages = messages;
	}
	
	@Override
	public IOIOLooper createIOIOLooper(String connectionType, Object extra) {
		return new BaseIOIOLooper() {
			private final int I2C_PAIR = 0; //IOIO Pair for I2C
			private static final float FREQ = 50.0f;
			private static final int PCA_ADDRESS = 0x40;
			private static final byte PCA9685_MODE1 = 0x00;
			private static final byte PCA9685_PRESCALE = (byte) 0xFE;
			private TwiMaster twi_;
			
			private DigitalOutput stateLED;
			private DigitalOutput activityPIN;
			private DigitalInput pedalButton;
			// all the leds
			private DigitalOutput[][] fretLEDs = new DigitalOutput[6][4];
			// reference to actually turned on leds, to be able to turn them off
			private DigitalOutput[] fretLEDsTurnedOn = new DigitalOutput[6];
			
			private boolean lastKnownPedalPosition = true;
			
			private ReschedulableTimer safetyTimer;
			
			@Override
			protected void setup() throws ConnectionLostException,
					InterruptedException {
				LOG.info("IOIO is connected");
				frmBlueAhuizote.setTitle(messages.getString("robotar.ioio.connected"));
				ioioReconnectAction.setEnabled(false);
				
				// on-board pin
				stateLED = ioio_.openDigitalOutput(IOIO.LED_PIN, true);

				// pedal input setup
				pedalButton = ioio_.openDigitalInput(Pins.PEDAL_PIN, DigitalInput.Spec.Mode.PULL_UP);
				
				// fret leds output setup
				fretLEDs = prepareLEDs(false);
				
				// Setup IOIO TWI Pins
				twi_ = ioio_.openTwiMaster(I2C_PAIR, TwiMaster.Rate.RATE_1MHz, false);
				
				reset();
				
				// set servos in neutral position after connection is established
				resetAll();
				
				// activity pin
				activityPIN = ioio_.openDigitalOutput(Pins.ACTIVITY_PIN, true);
				safetyTimer = new ReschedulableTimer();
				safetyTimer.schedule(new Runnable() {
					public void run() {
						// set the activityPIN to false after max inactivity limit is reached
						try {
							LOG.debug("Safety timer reached, turn off state led");
							
							// turn off led
							stateLedOff = true;
							stateLED.write(stateLedOff);
							
							//LOG.debug("release all chords");
							// release chord
							//resetAll();
							
							//LOG.debug("all servos should be in neutral positions");
							Thread.sleep(100);
							//LOG.debug("and now power off to servos");
							
							// power off servos
							activityPIN.write(false);

						} catch (ConnectionLostException e) {
							LOG.error("timer. can not reset servos/write false to activity PIN!", e);
						} catch (InterruptedException e) {
							LOG.error("timer. can not reset servos!", e);
						}
					}
				}, preferences.getMaxInactivity() * 1000); // in milliseconds
			}

			private DigitalOutput[][] prepareLEDs(boolean startValue) throws ConnectionLostException {
				for (int i = 0; i < 6; i++) {
					for (int j = 0; j < 4; j++) {
						// pin matching Pins.java
						fretLEDs[i][j] = ioio_.openDigitalOutput(Pins.getLEDPin(i, j+1), startValue);
					}
				}
				return fretLEDs;
			}
			
			private void reset() throws ConnectionLostException,
			InterruptedException {
				// Set prescaler - see PCA9685 data sheet
				LOG.info("Start of the BaseIOIOLooper.reset method");
				float prescaleval = 25000000;
				prescaleval /= 4096;
				prescaleval /= FREQ;
				prescaleval -= 1;
				byte prescale = (byte) Math.floor(prescaleval + 0.5);
				
				write8(PCA9685_MODE1, (byte) 0x10); // go to sleep... prerequisite to set prescaler
				write8(PCA9685_PRESCALE, prescale); // set the prescaler
				write8(PCA9685_MODE1, (byte) 0x20); // Wake up and set Auto Increment
			}
			
			private void write8(byte reg, byte val) throws ConnectionLostException,
				InterruptedException {
				LOG.info("Start of the write8 method");
				byte[] request = {reg, val};
				twi_.writeRead(PCA_ADDRESS, false, request, request.length, null, 0);
			}
		
			@Override
			public void loop() throws ConnectionLostException,
					InterruptedException {
				//LOG.info("Start of the loop method");
				
				if (showChecked) {
					runDemo();
					// thread.sleep ?
					return;
				}
				// initial position
				// high = true, low = false
				// read pedal position
				boolean pedalInHighPosition = pedalButton.read();
				if (lastKnownPedalPosition == pedalInHighPosition) {
					// no change from last time, wait a little, than continue
					Thread.sleep(20);
					return;
				}
				// save current status of the pedal
				lastKnownPedalPosition = pedalInHighPosition;
				
				if (!pedalInHighPosition) {
					LOG.debug("Pedal is pressed");
					if (preferences.getPedalMode() == RoboTarPreferences.PRESS_AND_RELEASE) {
						// no action
						return;
					}
					
					// do action, PRESS_AND_HOLD
					sendChord2RoboTar();
					
				} else {
					LOG.debug("Pedal is released");
					
					// turn off led
					stateLedOff = true;
					stateLED.write(stateLedOff);
					
					// reset servos - in both cases
					resetAll();
					
					if (preferences.getPedalMode() == RoboTarPreferences.PRESS_AND_RELEASE) {
						// do action
						sendChord2RoboTar();
					}

				} 
				
			}
			
			private void sendChord2RoboTar() throws ConnectionLostException, InterruptedException {
				// turn led on
				stateLedOff = false;
				stateLED.write(stateLedOff);
				
				// we are checking and logging the status first
				if (frmBlueAhuizote == null) {
					LOG.error("There is no RoboTar GUI!");
				} else {
					if (RoboTarPC.this.getChordsPage() == null) {
						LOG.debug("informative - there is no chords page");
					}
					if (RoboTarPC.this.getSongsPage() == null) {
						LOG.debug("informative - there is no songs page");
					}
					
					if (RoboTarPC.this.getServoSettings() == null) {
						// this should not happen, servo settings are initialized to neutral positions in the constructor
						LOG.warn("There is no chord chosen!");
					} else {
						// user made some activity! reschedule max inactivity action
						if (RoboTarPC.this.servoSettings.isAnyCorrectionSet()) {
							LOG.debug("rescheduling the timer");
							// send POWER ON to servos
							activityPIN.write(true);
							safetyTimer.reschedule(preferences.getMaxInactivity() * 1000); // in milliseconds
						}
						// if songs page exists and we already play the song, play next chord
						if (RoboTarPC.this.getSongsPage() != null && RoboTarPC.this.getSongsPage().isPlaying()) {
							if (preferences.getAfterTimeout() == RoboTarPreferences.MOVE_TO_NEXT) {
								RoboTarPC.this.getSongsPage().simPedalPressed();
							} else {
								// otherwise the chord is already set, unless it is before 1st chord? 
								// TODO check
							}
						} else if (RoboTarPC.this.getChordsPage() != null) {
							// if not, and chords page exists, play chord that is set in radio buttons
							RoboTarPC.this.getChordsPage().prepareChord();
						}
						
						// everything is set correctly and we have servo settings available 
						// (either from songs or chords page, or default - neutral) or last one? - check
						ServoSettings chordServoValues = RoboTarPC.this.getServoSettings();
						LEDSettings leds = RoboTarPC.this.getLeds();
						LOG.debug("got chord: {}", chordServoValues.debugOutput());
						LOG.debug("leds: {}", leds);
						long timeStart = System.currentTimeMillis();
						for (int i = 0; i < 6; i++) {
							int servoNumber = chordServoValues.getServos()[i];
							float servoValue = chordServoValues.getValues()[i];
							setServo(servoNumber, servoValue);
							if (leds != null) {
								LOG.debug("leds 2: {}", leds.getLeds());
								if (leds.getLeds() != null) {
									setLED(i, leds.getLeds()[i]);
								}
							}
						}
						long timeEnd = System.currentTimeMillis();
						LOG.debug("It took {} ms to execute 6 servos and LEDs", timeEnd - timeStart);
					}
				}
			}

			/**
			 * Showcase demo
			 * @throws ConnectionLostException
			 * @throws InterruptedException
			 */
			private void runDemo() throws ConnectionLostException, InterruptedException {
				patterns = new ShowcasePatterns(fretLEDs);
				patterns.initAll();
				// endless cycle, until unchecked in menu
				while (showChecked) {
					// pick one pattern
					int n = patterns.getRandomPatternIdx();
					// play it
					patterns.play(n);
				}
			}

			/**
			 * Reset all servos to neutral position.
			 * 
			 * @throws ConnectionLostException
			 * @throws InterruptedException
			 */
			public void resetAll() throws ConnectionLostException, InterruptedException {
				stateLedOff = false;
				ServoSettings sett = RoboTarPC.this.getServoSettings();
				for (int servo = 0; servo < 12; servo++) {
					setServo(servo, sett.getInitial(servo));
				}
				turnOffFretLEDs();
				LOG.info("Servos in neutral position default");
			}

			private void turnOffFretLEDs() throws ConnectionLostException {
				for (int i = 0; i < 6; i++) {
					for (int j = 0; j < 4; j++) {
						fretLEDs[i][j].write(false);
					}
					fretLEDsTurnedOn[i] = null;
				}
			}
			
			private void turnOnFretLEDs() throws ConnectionLostException {
				for (int i = 0; i < 6; i++) {
					for (int j = 0; j < 4; j++) {
						fretLEDs[i][j].write(true);
					}
				}
			}
			
			/**
			 * Set Servo channel and milliseconds input to PulseWidth calculation
			 * 
			 * @param servoNum
			 * @param pos
			 * @throws ConnectionLostException
			 * @throws InterruptedException
			 */
			public void setServo(int servoNum, float pos) throws ConnectionLostException, InterruptedException {
				LOG.debug("setServo call: servo: {}, value: {}", servoNum, pos);
				setPulseWidth(servoNum, pos + 1.0f);  //
			}
			
			protected void setPulseWidth(int channel, float ms) throws ConnectionLostException, InterruptedException {
				// Set pulsewidth according to PCA9685 data sheet based on milliseconds value sent from setServo method
				// 4096 steps per cycle, frequency is 50MHz (50 steps per millisecond)
				int pw = Math.round(ms / 1000 * FREQ * 4096);
				// Skip to every 4th address value to turn off the pulse (see datasheet addresses for LED#_OFF_L)
				byte[] request = { (byte) (0x08 + channel * 4), (byte) pw, (byte) (pw >> 8) };
				twi_.writeRead(PCA_ADDRESS, false, request, request.length, null, 0);
			}

			/**
			 * 
			 * @param stringNum 0..5
			 * @param fretNum 1..4
			 * @throws ConnectionLostException
			 */
			public void setLED(int stringNum, int fretNum) throws ConnectionLostException {
				LOG.debug("setLED call: string: {}, fretNum: {}", stringNum, fretNum);
				if (fretNum <= 0) {
					if (fretLEDsTurnedOn[stringNum] != null) {
						// if we know what was last turned on
						fretLEDsTurnedOn[stringNum].write(false);
					} else {
						// turn off all LEDs on this string
						for (int j = 0; j < 4; j++) {
							fretLEDs[stringNum][j].write(false);
						}
					}
					fretLEDsTurnedOn[stringNum] = null;
				} else {
					// turn off last turned on LED on this string
					if (fretLEDsTurnedOn[stringNum] != null) {
						fretLEDsTurnedOn[stringNum].write(false);
					}
					// turn on the one LED on this string
					fretLEDs[stringNum][fretNum-1].write(true);
					fretLEDsTurnedOn[stringNum] = fretLEDs[stringNum][fretNum-1];
				}
			}
			
			@Override
			public void disconnected() {
				LOG.info("IOIO disconnected");
				frmBlueAhuizote.setTitle(messages.getString("robotar.ioio.disconnected"));
				ioioReconnectAction.setEnabled(true);
			}

			@Override
			public void incompatible() {
				LOG.error("Incompatible firmware version of IOIO");
			}
			
		};
	}

	public String getLocalVersion() {
		return localVersion;
	}
}
