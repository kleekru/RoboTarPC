package ioio.RoboTar.PCconsole;


import java.awt.EventQueue;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.FormSpecs;

import cz.versarius.xchords.Chord;

import javax.swing.JLabel;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

public class RoboTarChordsPage extends JFrame implements ActionListener,
		ListSelectionListener {
	static final Logger LOG = LoggerFactory.getLogger(RoboTarChordsPage.class);

	private static final long serialVersionUID = 1L;
	
	private JPanel frmBlueAhuizoteChords;
	
	private JButton btnNewChord;
	
	private String chordNameSend;
	private int channelSend;
	
	protected int lowEstringSend;
	protected int AstringSend;
	protected int DstringSend;
	protected int GstringSend;
	protected int BstringSend;
	protected int highEstringSend;
	protected static int[] chordSend;
	
	private DefaultListModel chordList;
	private JList listChords;
	private JLabel lblChordPicture;
	private ResourceBundle messages;
	
	public static String ROOT_DIR = "C:/AndroidWorkspace";
	//public static String ROOT_DIR = "d:/projects/kleekru/";

	private ChordRadioPanel radioPanel;

	/**
	 * Launch the application.
	 */
	public static void StartChordsPage() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RoboTarChordsPage frame = new RoboTarChordsPage(chordSend);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * 
	 * @param chordReceived
	 */
	public RoboTarChordsPage(int[] chordReceived) {
		messages = ResourceBundle.getBundle("ioio.RoboTar.PCconsole.RoboTarBundle", Locale.ENGLISH);
		
		setVisible(true);
		setTitle(messages.getString("robotar.chords.title"));
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				RoboTarChordsPage.class
						.getResource("/data/BlueAhuizoteIcon.png")));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 800, 400);

		frmBlueAhuizoteChords = new JPanel();
		frmBlueAhuizoteChords.setAlignmentY(Component.TOP_ALIGNMENT);
		frmBlueAhuizoteChords.setAlignmentX(Component.LEFT_ALIGNMENT);
		frmBlueAhuizoteChords.setBackground(Color.BLUE);
		frmBlueAhuizoteChords.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(frmBlueAhuizoteChords);
		frmBlueAhuizoteChords.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("max(62dlu;pref)"),
				ColumnSpec.decode("center:pref:grow"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(50dlu;min):grow"), }, new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(49dlu;default)"),
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(76dlu;default)"),
				FormSpecs.LINE_GAP_ROWSPEC, RowSpec.decode("92px"), }));

		JLabel activeSongLbl = new JLabel(messages.getString("robotar.chords.active_song"));
		activeSongLbl.setForeground(Color.WHITE);
		activeSongLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
		frmBlueAhuizoteChords.add(activeSongLbl, "3, 2");

		JLabel activeSong = new JLabel(messages.getString("robotar.chords.no_song_selected"));
		activeSong.setForeground(Color.WHITE);
		frmBlueAhuizoteChords.add(activeSong, "5, 2");
		activeSong.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null,
				null, null));
		activeSong.setBackground(Color.BLACK);
		activeSong.setVerticalAlignment(SwingConstants.TOP);
		activeSong.setFont(new Font("Segoe UI", Font.BOLD, 14));

		// new chord button
		btnNewChord = new JButton("");
		btnNewChord.setPressedIcon(new ImageIcon(RoboTarChordsPage.class
				.getResource("/data/NewChordPressed.png")));
		frmBlueAhuizoteChords.add(btnNewChord, "2, 4");
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

			private void newChordButtonActionPerformed(ActionEvent evt) {
				radioPanel.clear();
				radioPanel.setChordName(messages.getString("robotar.chords.enter_chord_name"));
				clearSelection();
			}
		});

		// test chord button
		JToggleButton tglbtnTestChord = new JToggleButton("");
		tglbtnTestChord.setPressedIcon(new ImageIcon(RoboTarChordsPage.class
				.getResource("/data/TestChordOn.png")));
		frmBlueAhuizoteChords.add(tglbtnTestChord, "3, 4");
		tglbtnTestChord.setSelectedIcon(new ImageIcon(RoboTarChordsPage.class
				.getResource("/data/TestChordOn.png")));
		tglbtnTestChord.setIcon(new ImageIcon(RoboTarChordsPage.class
				.getResource("/data/TestChordOff.png")));
		tglbtnTestChord.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				tglbtnTestChordActionPerformed(evt);
			}

			private void tglbtnTestChordActionPerformed(ActionEvent evt) {
				try {
					int[] chord = Chords.Chords(lowEstringSend, AstringSend,
							DstringSend, GstringSend, BstringSend,
							highEstringSend);
					if (isGridFilled(chord)) {
						LOG.debug("Chord values from Chords Constructor: ");
						for (int i = 0; i < chord.length; i++) {
							LOG.debug("{}. - {}", i, chord[i]);
						}
						logValues("Low E", Chords.getChannelLowE(), Chords.getLowEstringPosition());
						logValues("A", Chords.getChannelA(), Chords.getAStringPosition());
						logValues("D", Chords.getChannelD(), Chords.getDStringPosition());
						logValues("G", Chords.getChannelG(), Chords.getGStringPosition());
						logValues("B", Chords.getChannelB(), Chords.getBStringPosition());
						logValues("High E", Chords.getChannelHighE(), Chords.getHighEStringPosition());
					} else {
						JOptionPane.showMessageDialog(RoboTarChordsPage.this, messages.getString("robotar.chords.fill_chord_grid"));
					}
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}

			}
			private void logValues(String stringName, int channel, float position) {
				LOG.debug("{} channel: {}, value: {}", stringName, channel, position);
			}
			// TODO this is not working now - Chords.Chords returns array of 0's
			private boolean isGridFilled(int[] chord) {
				boolean result = true;
				for (int i = 0; i<chord.length; i++) {
					result &= (chord[i]>0);
				}
				return result;
			}
		});

		// add chord to song button
		JButton btnAddToSong = new JButton("");
		frmBlueAhuizoteChords.add(btnAddToSong, "5, 4");
		btnAddToSong.setMinimumSize(new Dimension(20, 9));
		btnAddToSong.setMaximumSize(new Dimension(20, 9));
		btnAddToSong.setIcon(new ImageIcon(RoboTarChordsPage.class
				.getResource("/data/ArrowUp.png")));
		btnAddToSong.setFont(new Font("Segoe UI", Font.BOLD, 13));
		btnAddToSong.setToolTipText(messages.getString("robotar.chords.add_chord_to_song.tooltip"));

		radioPanel = new ChordRadioPanel(messages);
		frmBlueAhuizoteChords.add(radioPanel, "5, 6");
		
		// add chord to list button
		JButton btnAddToChordList = new JButton("");
		frmBlueAhuizoteChords.add(btnAddToChordList, "5, 8, default, center");
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
				if (!chordNameSend.isEmpty()) {
					LOG.info("add chord to song... TODO");
					/*XMLChordLoader.chordloader(chordNameSend, lowEstringSend,
						AstringSend, DstringSend, GstringSend, BstringSend,
						highEstringSend);*/
					radioPanel.setChordName(null);
					clearSelection();
				} else {
					JOptionPane.showMessageDialog(RoboTarChordsPage.this, messages.getString("robotar.chords.fill_chord_name"));
				}

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
		frmBlueAhuizoteChords.add(lblChordPicture, "3, 6, 1, 1, center, center");

		// populate the list based on XML file load.
		XMLChordLoader3 loader = new XMLChordLoader3();
		List<Chord> chords = loader.load(new File("src/data/default-chords/robotar-default.xml"));
		chordList = new DefaultListModel();
		for (Chord chord : chords) {
			chordList.addElement(chord);
		}
		JScrollPane scrollPane = new JScrollPane();
		frmBlueAhuizoteChords.add(scrollPane, "3, 8, fill, fill");
		listChords = new JList(chordList);
		scrollPane.setViewportView(listChords);
		listChords.setFont(new Font("Tahoma", Font.BOLD, 11));

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
		listChords.setCellRenderer(new ChordListCellRenderer());
		listChords.setVisibleRowCount(4);
		listChords.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listChords.setBorder(new LineBorder(Color.BLUE, 3, true));
		listChords.addListSelectionListener(this);

		/* set size of the frame */
		setSize(800, 409);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	}

	private void clearSelection() {
		listChords.setSelectedIndex(0);
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

	/**
	 * If chord list selection is changed, this method is called.
	 */
	@Override
	public void valueChanged(ListSelectionEvent event) {
		if (!event.getValueIsAdjusting()) {
			JList theList = (JList) event.getSource();
			DefaultListModel model = (DefaultListModel) theList.getModel();
			int selIdx = (int) theList.getSelectedIndex();
			Chord chord = (Chord) model.get(selIdx);
			setChord(chord);
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
	}
	
	/**
	 * TODO currently reads fixed images. after adding own new chords -> generate image on the fly
	 * @param chord
	 */
	private void showChordImage(Chord chord) {
		String chordName = chord.getName();
		String imageName = "src/data/default-chords/" + chordName + ".png";
		try {
			lblChordPicture.setIcon(new ImageIcon(ImageIO
					.read(new File(imageName))));
			lblChordPicture.setText(chordName);
		} catch (IOException e) {
			LOG.error("file not found: {}", imageName);
			lblChordPicture.setText(messages.getString("robotar.chords.image_not_found"));
			lblChordPicture.setIcon(null);
			LOG.debug("stacktrace", e);
		}
	}




	public String getChordNameSend() {
		return chordNameSend;
	}

	public int getLowEstringSend() {
		return lowEstringSend;
	}

	public int getAstringSend() {
		return AstringSend;
	}

	public int getDstringSend() {
		return DstringSend;
	}

	public int getGstringSend() {
		return GstringSend;
	}

	public int getBstringSend() {
		return BstringSend;
	}

	public int getHighEstringSend() {
		return highEstringSend;
	}

	public static int[] getChordSend() {
		return chordSend;
	}

	public void setChordNameSend(String chordNameSend) {
		this.chordNameSend = chordNameSend;
	}

	public void setLowEstringSend(int lowEstringSend) {
		this.lowEstringSend = lowEstringSend;
	}

	public void setAstringSend(int astringSend) {
		this.AstringSend = astringSend;
	}

	public void setDstringSend(int dstringSend) {
		this.DstringSend = dstringSend;
	}

	public void setGstringSend(int gstringSend) {
		this.GstringSend = gstringSend;
	}

	public void setBstringSend(int bstringSend) {
		this.BstringSend = bstringSend;
	}

	public void setHighEstringSend(int highEstringSend) {
		this.highEstringSend = highEstringSend;
	}

	public static void setChordSend(int[] chordSend) {
		RoboTarChordsPage.chordSend = chordSend;
	}

	public JButton getBtnNewChord() {
		return btnNewChord;
	}

	public int getChannelSend() {
		return channelSend;
	}

	public void setBtnNewChord(JButton btnNewChord) {
		this.btnNewChord = btnNewChord;
	}

	public void setChannelSend(int channelSend) {
		this.channelSend = channelSend;
	}

	public DefaultListModel getChordList() {
		return chordList;
	}

	public void setChordList(DefaultListModel chordList) {
		this.chordList = chordList;
	}

}
