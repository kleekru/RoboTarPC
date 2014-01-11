package ioio.robotar.pcconsole;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cz.versarius.xchords.Chord;
import cz.versarius.xchords.ChordBag;
import cz.versarius.xchords.StringInfo;
import cz.versarius.xchords.StringState;

public class XMLChordLoader3 {

	/** 
	 * Load from inputstream into the given chord bag.
	 * 
	 * @param stream
	 * @param bag
	 * @return
	 */
	public ChordBag loadChords(InputStream stream, ChordBag bag) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(stream);
			doc.getDocumentElement().normalize();

			if (doc.hasChildNodes()) {
				// going into <chordlibrary> element
				readChordList(doc.getChildNodes().item(0).getChildNodes(), bag);
				return bag;
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

	protected ChordBag readChordList(NodeList nodeList, ChordBag bag) {
		for (int count = 0; count < nodeList.getLength(); count++) {
			Node tempNode = nodeList.item(count);
			// make sure it's element node.
			if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
				if ("chord".equals(tempNode.getNodeName())) {
					if (tempNode.hasChildNodes()) {
						// loop through properties
						Chord chord = readChord(tempNode.getChildNodes());
						bag.add(chord);
						
						// set id
						if (tempNode.hasAttributes()) {
							NamedNodeMap nodeMap = tempNode.getAttributes();
							Node node = nodeMap.getNamedItem("id");
							chord.setId(node.getNodeValue());
						}
					}
				}
			}
		}
		return bag;
	}
	
	private Chord readChord(NodeList nodeList) {
		Chord chord = new Chord();
		for (int count = 0; count < nodeList.getLength(); count++) {
			Node aNode = nodeList.item(count);
			if (aNode.getNodeType() == Node.ELEMENT_NODE) {
				if ("name".equals(aNode.getNodeName())) {
					chord.setName(aNode.getTextContent());
				} else if ("position".equals(aNode.getNodeName())) {
					readPosition(chord, aNode.getChildNodes());
				}
			}
		}
		return chord;
	}
	
	private void readPosition(Chord chord, NodeList nodeList) {
		for (int count = 0; count < nodeList.getLength(); count++) {
			Node sNode = nodeList.item(count);
			if (sNode.getNodeType() == Node.ELEMENT_NODE) {
				if ("string".equals(sNode.getNodeName())) {
					if (sNode.hasAttributes()) {
						NamedNodeMap nodeMap = sNode.getAttributes();
						StringInfo si = new StringInfo();
						for (int i = 0; i < nodeMap.getLength(); i++) {

							Node node = nodeMap.item(i);
							String nodeName = node.getNodeName();
							String nodeValue = node.getNodeValue();
							if ("fret".equals(nodeName)) {
								si.setFret(Integer.parseInt(nodeValue));
							} else if ("finger".equals(nodeName)) {
								si.setFinger(nodeValue);
							} else if ("low".equals(nodeName)) {
								si.setLow(Integer.parseInt(nodeValue));
							} else if ("high".equals(nodeName)) {
								si.setHigh(Integer.parseInt(nodeValue));
							} else if ("state".equals(nodeName)) {
								si.setState(StringState.fromValue(nodeValue));
							} else if ("name".equals(nodeName)) {
								si.setName(nodeValue);
							}

						}
						int idx = convertName2Idx(si.getName());
						if (si.getState() == null) {
							si.setState(StringState.OK);
						}
						chord.setString(idx, si);
					}
				}			
			}
		}
	}
	
	private int convertName2Idx(String name) {
		if ("e6".equals(name)) {
			return 0;
		} else if ("a".equals(name)) {
			return 1;
		} else if ("d".equals(name)) {
			return 2;
		} else if ("g".equals(name)) {
			return 3;
		} else if ("b".equals(name)) {
			return 4;
		} else if ("e1".equals(name)) {
			return 5;
		}
		throw new RuntimeException("todo...");
	}
}
