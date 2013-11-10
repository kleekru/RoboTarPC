package ioio.robotar.pcconsole;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * 	 <servo-corrections>
 *     <correction servo="" neutral="" muted="" left="" right=""/>
 *     <correction servo="" neutral="" muted="" left="" right=""/>
 *   </servo-corrections>
 * 
 *
 */
public class XMLSettingLoader {
	public ServoSettings load(InputStream stream) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(stream);
			doc.getDocumentElement().normalize();

			if (doc.hasChildNodes()) {
				ServoSettings sett = readSetting(doc.getChildNodes().item(0));
				return sett;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		throw new RuntimeException("todo...never do this :)");
	}
	
	private ServoSettings readSetting(Node node) {
		ServoSettings sett = new ServoSettings();
		
		ArrayList<float[]> cors = new ArrayList<float[]>();
        
		for (int count = 0; count < node.getChildNodes().getLength(); count++) {
			Node aNode = node.getChildNodes().item(count);
			if (aNode.getNodeType() == Node.ELEMENT_NODE) {
				if ("correction".equals(aNode.getNodeName())) {
					if (aNode.hasAttributes()) {
						NamedNodeMap nodeMap = aNode.getAttributes();
						float []corr = new float[5];
						//corr[0] = Float.parseFloat(nodeMap.getNamedItem("servo").getNodeValue());
						corr[0] = Float.parseFloat(nodeMap.getNamedItem("neutral").getNodeValue());
						corr[1] = Float.parseFloat(nodeMap.getNamedItem("muted").getNodeValue());
						corr[2] = Float.parseFloat(nodeMap.getNamedItem("left").getNodeValue());
						corr[3] = Float.parseFloat(nodeMap.getNamedItem("right").getNodeValue());
						
						cors.add(corr);
					}
				}		
			}
		}
		float[][] corsArray = (float[][]) cors.toArray(new float[0][0]);
		sett.setCorrections(corsArray);
		return sett;
	}
	
}
