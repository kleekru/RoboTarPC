package ioio.RoboTar.PCconsole;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;

public class RoboTarSongsPage extends JFrame {

	private JPanel frmBlueAhuizoteSongs;

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
		setBounds(100, 100, 450, 300);
		frmBlueAhuizoteSongs = new JPanel();
		frmBlueAhuizoteSongs.setBackground(Color.RED);
		frmBlueAhuizoteSongs.setBorder(new EmptyBorder(5, 5, 5, 5));
		frmBlueAhuizoteSongs.setLayout(new BorderLayout(0, 0));
		setContentPane(frmBlueAhuizoteSongs);
	}

	public JPanel getFrmBlueAhuizoteSongs() {
		return frmBlueAhuizoteSongs;
	}

	public void setFrmBlueAhuizoteSongs(JPanel frmBlueAhuizoteSongs) {
		this.frmBlueAhuizoteSongs = frmBlueAhuizoteSongs;
	}

}
