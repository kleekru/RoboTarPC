package ioio.RoboTar.PCconsole;

import java.io.File;
import org.w3c.dom.*;

import javax.swing.ButtonModel;
import javax.swing.JTextField;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException; 

public class XMLChordLoader2 {

    public static String chordloader(String chordNameSend, int lowEstringSend, int AstringSend, int DstringSend, int GstringSend, int BstringSend, int highEstringSend){
    try {

            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse (new File("C:/AndroidWorkspace/RoboTarIOIOforPCConsole/src/data/chordlist2.xml"));

            // normalize text representation
            doc.getDocumentElement ().normalize ();
            System.out.println ("Root element of the doc is " + doc.getDocumentElement().getNodeName());
            
            NodeList listOfChords = doc.getElementsByTagName("name");
            int totalChords = listOfChords.getLength();
            System.out.println("Total no of chords : " + totalChords);
            
            for (int q=0; q<listOfChords.getLength(); q++) {
            	Node node = listOfChords.item(q);
            	if (node.getNodeType()== Node.ELEMENT_NODE){
            		Element element = (Element) node;
            		System.out.println("Chord Name: " + getValue("chord id", element));
            		System.out.println("6th String Address: " + getValue("string name", element));
            		System.out.println("5th String Address: " + getValue("fingeraddress5", element));
            		System.out.println("4th String Address: " + getValue("fingeraddress4", element));
            		System.out.println("3rd String Address: " + getValue("fingeraddress3", element));
            		System.out.println("2nd String Address: " + getValue("fingeraddress2", element));
            		System.out.println("1st String Address: " + getValue("fingeraddress1", element));
            		System.out.println("Chord Image File: " + getValue("chordimage", element));
            		
            	}	
            }
                      
            //this.chordName=chordName;
            System.out.println("chord Name sent from Chords page: " + chordNameSend);
            System.out.println("Low E String value sent from Chords page: " + lowEstringSend);
            System.out.println("A String value sent from Chords page: " + AstringSend);
            System.out.println("D String value sent from Chords page: " + DstringSend);
            System.out.println("G String value sent from Chords page: " + GstringSend);
            System.out.println("B String value sent from Chords page: " + BstringSend);
            System.out.println("High E String value sent from Chords page: " + highEstringSend);
            
            
            
        }catch (SAXParseException err) {
        System.out.println ("** Parsing error" + ", line " 
             + err.getLineNumber () + ", uri " + err.getSystemId ());
        System.out.println(" " + err.getMessage ());

        }catch (SAXException e) {
        Exception x = e.getException ();
        ((x == null) ? e : x).printStackTrace ();

        }catch (Throwable t) {
        t.printStackTrace ();
        }
        //System.exit (0);
	return chordNameSend;

    }//end of main

	private static String getValue(String tag, Element element) {
		NamedNodeMap nodes = element.getElementsByTagName(tag).item(0).getAttributes();
		Node node = (Node) nodes.item(0);
		return node.getNodeValue();
	}

}
