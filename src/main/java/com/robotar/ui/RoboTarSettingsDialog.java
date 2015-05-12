package com.robotar.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.robotar.util.RoboTarPreferences;

/**
 * Visual dialog for RoboTarPreferences.
 * 
 * @author miira
 * 
 */
public class RoboTarSettingsDialog extends JDialog {

	private static final long serialVersionUID = -8589321792566220127L;

	private static final Logger LOG = LoggerFactory.getLogger(RoboTarSettingsDialog.class);

	private ResourceBundle messages;
	private RoboTarPC mainFrame;
	
	private int selectedPedalMode;
	
	public RoboTarSettingsDialog(final RoboTarPC page) {
		setBackground(Const.BACKGROUND_COLOR);
		setResizable(false);
		setSize(320, 200);
		this.messages = page.getMessages();
		this.mainFrame = page;
		setTitle(messages.getString("robotar.settings.title"));

		/*
		 * this.addWindowListener(new WindowAdapter() {
		 * 
		 * @Override public void windowClosing(WindowEvent e) {
		 * LOG.debug("closing"); } });
		 */
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 150, 150 };
		gridBagLayout.rowHeights = new int[] { 40, 30, 30, 30, 30 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
		getContentPane().setLayout(gridBagLayout);

		JLabel labelPedalMode = new JLabel(messages.getString("robotar.settings.pedal_mode"));
		getContentPane().add(labelPedalMode);

		final String pressAndHold = messages.getString("robotar.settings.press_and_hold");
		String pressAndRelease = messages.getString("robotar.settings.press_and_release");
		String[] styles = { pressAndHold, pressAndRelease };
		JComboBox stylesList = new JComboBox(styles);
		stylesList.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox) e.getSource();
				String selectedStyleStr = (String) cb.getSelectedItem();
				LOG.info(selectedStyleStr);
				if (pressAndHold.equals(selectedStyleStr)) {
					selectedPedalMode = RoboTarPreferences.PRESS_AND_HOLD;
				} else {
					selectedPedalMode = RoboTarPreferences.PRESS_AND_RELEASE;
				}
			}

		});
		getContentPane().add(stylesList);
		
		// set actual value
		int pedalMode = mainFrame.getPreferences().getPedalMode();
		if (RoboTarPreferences.PRESS_AND_HOLD == pedalMode) {
			stylesList.setSelectedIndex(0);
		} else {
			stylesList.setSelectedIndex(1);
		}

		// OK
		JButton btnOK = new JButton(messages.getString("robotar.settings.ok"));
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainFrame.getPreferences().setPedalMode(selectedPedalMode);
				dispose();
			}
		});
		GridBagConstraints gbc_btnOKButton = new GridBagConstraints();
		gbc_btnOKButton.gridwidth = 2;
		gbc_btnOKButton.insets = new Insets(0, 0, 5, 5);
		gbc_btnOKButton.gridx = 0;
		gbc_btnOKButton.gridy = 4;
		getContentPane().add(btnOK, gbc_btnOKButton);
		
		// cancel
		JButton btnCancel = new JButton(messages.getString("robotar.settings.cancel"));
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		GridBagConstraints gbc_btnCancelButton = new GridBagConstraints();
		gbc_btnCancelButton.gridwidth = 2;
		gbc_btnCancelButton.insets = new Insets(0, 0, 5, 5);
		gbc_btnCancelButton.gridx = 1;
		gbc_btnCancelButton.gridy = 4;
		getContentPane().add(btnCancel, gbc_btnCancelButton);
		
	}

}
