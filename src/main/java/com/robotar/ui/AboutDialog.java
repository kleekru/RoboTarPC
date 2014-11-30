package com.robotar.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AboutDialog extends JDialog {

	private static final long serialVersionUID = -9205125740551612479L;

	private static final Logger LOG = LoggerFactory.getLogger(AboutDialog.class);
	
	private ResourceBundle messages;
	
	public AboutDialog(final RoboTarPC page) {
		setBackground(Const.BACKGROUND_COLOR);
		setResizable(false);
		setSize(460, 400);
		this.messages = page.getMessages();
		setTitle(messages.getString("robotar.about.title"));
		
		/*this.addWindowListener(new WindowAdapter() {
		    @Override
		    public void windowClosing(WindowEvent e) {
		    	LOG.debug("closing");
		    }
		});*/
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {80, 200};
		gridBagLayout.rowHeights = new int[]{0, 0, 20, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		
		// icon
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		java.net.URL res = RoboTarPC.class.getResource("/data/BlueAhuizoteIconSmall.png");
		lblNewLabel.setIcon(new ImageIcon(res));
		//lblNewLabel.setIcon(new ImageIcon(RoboTarStartPage.class.getResource("/data/BlueAhuizoteIcon.png")));
		lblNewLabel.setBorder(null);
		GridBagConstraints gbc_icon = new GridBagConstraints();
		// top, left, bottom, right
		gbc_icon.insets = new Insets(5, 5, 5, 5);
		gbc_icon.gridx = 0;
		gbc_icon.gridy = 0;
		gbc_icon.gridheight = 5;
		getContentPane().add(lblNewLabel, gbc_icon);

		
		// header
		JLabel lblHeader = new JLabel(MessageFormat.format(messages.getString("robotar.about.header"), page.getLocalVersion()));
		lblHeader.setMaximumSize(new Dimension(200, 20));
		GridBagConstraints gbc_lblRange = new GridBagConstraints();
		gbc_lblRange.insets = new Insets(15, 5, 5, 5);
		gbc_lblRange.gridx = 1;
		gbc_lblRange.gridy = 0;
		getContentPane().add(lblHeader, gbc_lblRange);

		JLabel lblUrl = new JLabel("http://www.robo-tar.com");
		lblHeader.setMaximumSize(new Dimension(200, 30));
		GridBagConstraints gbc_lblUrl = new GridBagConstraints();
		gbc_lblUrl.insets = new Insets(0, 0, 5, 5);
		gbc_lblUrl.gridx = 1;
		gbc_lblUrl.gridy = 1;
		getContentPane().add(lblUrl, gbc_lblUrl);
		
		// authors
		JLabel lblAuthor1 = new JLabel("Kevin Krumwiede");
		lblAuthor1.setMaximumSize(new Dimension(400, 20));
		GridBagConstraints gbc_lblAuthor1 = new GridBagConstraints();
		gbc_lblAuthor1.insets = new Insets(0, 0, 5, 5);
		gbc_lblAuthor1.gridx = 1;
		gbc_lblAuthor1.gridy = 3;
		getContentPane().add(lblAuthor1, gbc_lblAuthor1);
		
		JLabel lblAuthor2 = new JLabel("Miroslav Mocek");
		lblAuthor2.setMaximumSize(new Dimension(400, 20));
		GridBagConstraints gbc_lblAuthor2 = new GridBagConstraints();
		gbc_lblAuthor2.insets = new Insets(0, 0, 10, 5);
		gbc_lblAuthor2.gridx = 1;
		gbc_lblAuthor2.gridy = 4;
		getContentPane().add(lblAuthor2, gbc_lblAuthor2);
		
		pack();
		setLocationByPlatform(true);
		setVisible(true);
	}

}
