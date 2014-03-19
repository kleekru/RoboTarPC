package com.robotar.ui;

import javax.swing.JDialog;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.SpinnerNumberModel;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JSpinner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.robotar.ioio.ServoSettings;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ResourceBundle;

public class CorrectionsDialog extends JDialog {

	private static final Logger LOG = LoggerFactory.getLogger(CorrectionsDialog.class);
	
	private RoboTarPC page;
	private ResourceBundle messages;
	
	private float VAL = 0.0f;
	private float MIN = -0.2f;
	private float MAX = 0.2f;
	private float STEP = 0.001f;
	
	JSpinner[][] spinners;
	
	public CorrectionsDialog(final RoboTarPC page) {
		setResizable(false);
		setSize(440, 370);
		this.page = page;
		this.messages = page.getMessages();
		setTitle(messages.getString("robotar.corrections.title"));
		
		this.addWindowListener(new WindowAdapter() {
		    @Override
		    public void windowClosing(WindowEvent e) {
		    	LOG.debug("closing");
		    	CorrectionsDialog.this.page.getServoSettings().setCorrections(getValues());
		    	ServoSettings.saveCorrectionsAs(new File(page.getPreferences().getCorrectionsFile()), page.getServoSettings());
		    }
		});
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {80, 80, 80, 80, 80};
		gridBagLayout.rowHeights = new int[]{23, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);

		// load button
		JButton btnLoadCorrections = new JButton(messages.getString("robotar.corrections.load_from"));
		btnLoadCorrections.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					loadCorrections(e);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
			}
		});
		GridBagConstraints gbc_btnLoadButton = new GridBagConstraints();
		gbc_btnLoadButton.gridwidth = 2;
		gbc_btnLoadButton.insets = new Insets(0, 0, 5, 5);
		gbc_btnLoadButton.gridx = 1;
		gbc_btnLoadButton.gridy = 0;
		getContentPane().add(btnLoadCorrections, gbc_btnLoadButton);
		
		// save button
		JButton btnSaveCorrections = new JButton(messages.getString("robotar.corrections.save_as"));
		btnSaveCorrections.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveCorrections(e);
			}
		});
		GridBagConstraints gbc_btnSaveButton = new GridBagConstraints();
		gbc_btnSaveButton.gridwidth = 2;
		gbc_btnSaveButton.insets = new Insets(0, 0, 5, 5);
		gbc_btnSaveButton.gridx = 3;
		gbc_btnSaveButton.gridy = 0;
		getContentPane().add(btnSaveCorrections, gbc_btnSaveButton);

		// labels
		String prefix = messages.getString("robotar.corrections.servo") + " ";
		for (int i=0; i<12; i++) {
			JLabel lblServo = new JLabel(prefix + i);
			GridBagConstraints gbc_lblServo = new GridBagConstraints();
			gbc_lblServo.insets = new Insets(0, 0, 5, 5);
			gbc_lblServo.gridx = 0;
			gbc_lblServo.gridy = i+1;
			getContentPane().add(lblServo, gbc_lblServo);
		}
		
		// servo settings
		spinners = new JSpinner[12][];
		for (int i = 0; i<12; i++) {
			spinners[i] = new JSpinner[4];
			for (int j = 0; j<4; j++) {
				JSpinner spinner = new JSpinner();
				SpinnerNumberModel snm = new SpinnerNumberModel(VAL, MIN, MAX, STEP);
				spinner.setModel(snm);
				GridBagConstraints gbc_spinner = new GridBagConstraints();
				gbc_spinner.fill = GridBagConstraints.HORIZONTAL;
				gbc_spinner.insets = new Insets(0, 0, 5, 0);
				gbc_spinner.gridx = j+1;
				gbc_spinner.gridy = i+1;
				getContentPane().add(spinner, gbc_spinner);
				spinners[i][j] = spinner;
			}
		}

		// display values from actual settings
		updateControls(page.getServoSettings().getCorrections());
		
		pack();
		setLocationByPlatform(true);
		setVisible(true);
	}

	protected void loadCorrections(ActionEvent evt) throws FileNotFoundException {
		JFileChooser fc = new JFileChooser();
		int returnValue = fc.showOpenDialog(this);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            ServoSettings sett = ServoSettings.loadCorrectionsFrom(file, RoboTarPC.ROBOTAR_FOLDER);
            page.setServoSettings(sett);
            updateControls(page.getServoSettings().getCorrections());
		}
	}
	
	protected void updateControls(float[][] v) {
		for (int i=0; i<12; i++) {
			for (int j=0; j<4; j++) {
				spinners[i][j].setValue(checkLimit(v[i][j]));
			}
		}
	}
	
	private float checkLimit(float value) {
		//LOG.debug("checking value: {}", value);
		if (value < MIN) {
			return MIN;
		} else if (value > MAX) {
			return MAX;
		} else {
			return value;
		}
	}
	protected float[][] getValues() {
		float [][] vals = new float[12][];
		for (int i=0; i<12; i++) {
			vals[i] = new float[4];
			for (int j=0; j<4; j++) {
				vals[i][j] = checkLimit(((Number) spinners[i][j].getValue()).floatValue());
			}
		}
		return vals;
	}
	
	protected void saveCorrections(ActionEvent e) {
		JFileChooser fc = new JFileChooser();
		int returnValue = fc.showSaveDialog(this);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            page.getServoSettings().setCorrections(getValues());
            ServoSettings.saveCorrectionsAs(file, page.getServoSettings());
		}
		
	}
}
