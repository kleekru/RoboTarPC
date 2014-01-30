
/** 
 * This class creates a frame with a JEditorPane for loading HTML Help files.
 */
package ioio.robotar.pcconsole;

import java.io.*;
import javax.swing.event.*;
import javax.swing.*;
import java.net.*;
import java.awt.event.*;
import java.awt.*;

public class RoboTarHelp extends JFrame implements ActionListener {
	private final int WIDTH = 600;
	private final int HEIGHT = 400;
    private JEditorPane editorpane;
    private URL helpURL;
  
  /**
  * HelpWindow constructor
  * @param String and URL
  */
  public RoboTarHelp(String title, URL hlpURL) {
      super(title);
      helpURL = hlpURL;  
      editorpane = new JEditorPane();
      editorpane.setEditable(false);
      try {
          editorpane.setPage(helpURL);
      } catch (Exception ex) {
          ex.printStackTrace();
      }
      //anonymous inner listener
      editorpane.addHyperlinkListener(new HyperlinkListener() {
          public void hyperlinkUpdate(HyperlinkEvent ev) {
              try {
                  if (ev.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                      editorpane.setPage(ev.getURL());
                  }
              } catch (IOException ex) {
                  //put message in window
                  ex.printStackTrace();
              }
          }
      });
      getContentPane().add(new JScrollPane(editorpane));
      addButtons();
      // no need for listener just dispose
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      // dynamically set location
      calculateLocation();
      setVisible(true);
      // end constructor
  }
  public RoboTarHelp(RoboTarStartPage roboTarStartPage) {
	// TODO Auto-generated constructor stub
}
/**
   * An Actionlistener so must implement this method
   *
   */
  public void actionPerformed(ActionEvent e) {
      String strAction = e.getActionCommand();
      URL tempURL;
      try {
          if (strAction == "Contents") {
              tempURL = editorpane.getPage();
              editorpane.setPage(helpURL);
          }
          if (strAction == "Close") {
              // more portable if delegated
              processWindowEvent(new WindowEvent(this,
                  WindowEvent.WINDOW_CLOSING));
          }
      } catch (IOException ex) {
          ex.printStackTrace();
      }
  }
  /**
   * add buttons at the south
   */
  private void addButtons() {
      JButton btncontents = new JButton("Contents");
      btncontents.addActionListener(this);
      JButton btnclose = new JButton("Close");
      btnclose.addActionListener(this);
      //put into JPanel
      JPanel panebuttons = new JPanel();
      panebuttons.add(btncontents);
      panebuttons.add(btnclose);
      //add panel south
      getContentPane().add(panebuttons, BorderLayout.SOUTH);
  }
  /**
   * locate in middle of screen
   */
  private void calculateLocation() {
      Dimension screendim = Toolkit.getDefaultToolkit().getScreenSize();
      setSize(new Dimension(WIDTH, HEIGHT));
      int locationx = (screendim.width - WIDTH) / 2;
      int locationy = (screendim.height - HEIGHT) / 2;
    setLocation(locationx, locationy);
  }
}
