package com.robotar.ui;

import javax.swing.JDialog;

import java.awt.GridBagLayout;

import javax.swing.JLabel;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class NewSongDialog extends JDialog {
	private boolean pressedOK;
	
	public NewSongDialog(Frame owner) {
		super(owner, true);
		setResizable(false);
		setSize(400, 300);
		setModal(true);
		setLocationRelativeTo(owner);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		pressedOK = false;
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {30, 50, 100, 30, 0};
		gridBagLayout.rowHeights = new int[] {30, 30, 30, 100, 30, 30, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		
		JLabel lblTitle = new JLabel("Title");
		GridBagConstraints gbc_lblTitle = new GridBagConstraints();
		gbc_lblTitle.anchor = GridBagConstraints.EAST;
		gbc_lblTitle.insets = new Insets(0, 0, 5, 5);
		gbc_lblTitle.gridx = 1;
		gbc_lblTitle.gridy = 1;
		getContentPane().add(lblTitle, gbc_lblTitle);
		
		titleTxt = new JTextField();
		GridBagConstraints gbc_titleTxt = new GridBagConstraints();
		gbc_titleTxt.fill = GridBagConstraints.HORIZONTAL;
		gbc_titleTxt.anchor = GridBagConstraints.NORTHWEST;
		gbc_titleTxt.insets = new Insets(0, 0, 5, 5);
		gbc_titleTxt.gridx = 2;
		gbc_titleTxt.gridy = 1;
		getContentPane().add(titleTxt, gbc_titleTxt);
		titleTxt.setColumns(10);
		
		JLabel lblInterpret = new JLabel("Interpret");
		GridBagConstraints gbc_lblInterpret = new GridBagConstraints();
		gbc_lblInterpret.anchor = GridBagConstraints.EAST;
		gbc_lblInterpret.insets = new Insets(0, 0, 5, 5);
		gbc_lblInterpret.gridx = 1;
		gbc_lblInterpret.gridy = 2;
		getContentPane().add(lblInterpret, gbc_lblInterpret);
		
		interpretTxt = new JTextField();
		GridBagConstraints gbc_interpretTxt = new GridBagConstraints();
		gbc_interpretTxt.fill = GridBagConstraints.HORIZONTAL;
		gbc_interpretTxt.anchor = GridBagConstraints.WEST;
		gbc_interpretTxt.insets = new Insets(0, 0, 5, 5);
		gbc_interpretTxt.gridx = 2;
		gbc_interpretTxt.gridy = 2;
		getContentPane().add(interpretTxt, gbc_interpretTxt);
		interpretTxt.setColumns(10);
		
		JLabel lblInfo = new JLabel("Info");
		GridBagConstraints gbc_lblInfo = new GridBagConstraints();
		gbc_lblInfo.insets = new Insets(0, 0, 5, 5);
		gbc_lblInfo.gridx = 1;
		gbc_lblInfo.gridy = 3;
		getContentPane().add(lblInfo, gbc_lblInfo);
		
		infoArea = new JTextArea();
		infoArea.setRows(5);
		GridBagConstraints gbc_infoArea = new GridBagConstraints();
		gbc_infoArea.fill = GridBagConstraints.HORIZONTAL;
		gbc_infoArea.insets = new Insets(0, 0, 5, 5);
		gbc_infoArea.gridx = 2;
		gbc_infoArea.gridy = 3;
		getContentPane().add(infoArea, gbc_infoArea);
		
		JButton btnOk = new JButton("OK");
		btnOk.setHorizontalAlignment(SwingConstants.LEFT);
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pressedOK = true;
                dispose();
			}
		});
		GridBagConstraints gbc_btnOk = new GridBagConstraints();
		gbc_btnOk.insets = new Insets(0, 0, 5, 5);
		gbc_btnOk.gridx = 2;
		gbc_btnOk.gridy = 4;
		getContentPane().add(btnOk, gbc_btnOk);
	}

	public String getTitleText() {
		return titleTxt.getText();
	}
	public void setTitleText(String titleText) {
		titleTxt.setText(titleText);
	}

	public String getInterepretText() {
		return interpretTxt.getText();
	}
	public void setInterpretText(String artist) {
		interpretTxt.setText(artist);
	}

	public String getInfoText() {
		return infoArea.getText();
	}
	public void setInfoText(String info) {
		infoArea.setText(info);
	}
		
	public boolean isPressedOK() {
		return pressedOK;
	}


	private static final long serialVersionUID = 7598552564266162370L;
	private JTextField titleTxt;
	private JTextField interpretTxt;
	private JTextArea infoArea;

}
