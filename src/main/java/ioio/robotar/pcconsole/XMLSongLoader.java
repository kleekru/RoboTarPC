package ioio.robotar.pcconsole;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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

import cz.versarius.xsong.ChordRef;
import cz.versarius.xsong.Line;
import cz.versarius.xsong.Part;
import cz.versarius.xsong.Song;
import cz.versarius.xsong.Verse;
import cz.versarius.xsong.Chorus;

public class XMLSongLoader extends XMLChordLoader3 {
	private static final Logger LOG = LoggerFactory.getLogger(XMLSongLoader.class);

	public Song loadSong(File file) {
		try {
			Song song = loadSong(new FileInputStream(file));
			if (song != null) {
				LOG.info("Song {} loaded from {}", song.getFullTitle(), file.getPath());
				song.setPath(file.getPath());
				return song;
			}
			LOG.error("Exception when loading {}", file.getPath());
		} catch (FileNotFoundException e) {
			LOG.error("Song was not loaded. Can not find {}. ", file.getPath());
			LOG.error(e.getMessage(), e);
		}
		return null;
	}
	
	protected Song loadSong(InputStream stream) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(stream);
			doc.getDocumentElement().normalize();

			if (doc.hasChildNodes()) {
				Song song = readSong(doc.getChildNodes().item(0));
				return song;
			}
			
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		} catch (ParserConfigurationException e) {
			LOG.error(e.getMessage(), e);
		} catch (SAXException e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}
	
	private Song readSong(Node node) {
		Song song = new Song();
		for (int count = 0; count < node.getChildNodes().getLength(); count++) {
			Node aNode = node.getChildNodes().item(count);
			if (aNode.getNodeType() == Node.ELEMENT_NODE) {
				if ("title".equals(aNode.getNodeName())) {
					song.setTitle(aNode.getTextContent());
				} else if ("interpret".equals(aNode.getNodeName())) {
					song.setInterpret(aNode.getTextContent());
				} else if ("info".equals(aNode.getNodeName())) {
					song.setInfo(aNode.getTextContent());
				} else if ("verse".equals(aNode.getNodeName())) {
					readPart(song, new Verse(), aNode.getChildNodes());
				} else if ("chorus".equals(aNode.getNodeName())) {
					readPart(song, new Chorus(), aNode.getChildNodes());
				} else if ("usedchords".equals(aNode.getNodeName())) {
					readChordList(aNode.getChildNodes(), song.getUsedChords());
				}
			}
		}
		return song;
	}

	private void readPart(Song song, Part part, NodeList nodeList) {
		List<Line> lines = new ArrayList<Line>();
		for (int count = 0; count < nodeList.getLength(); count++) {
			Node sNode = nodeList.item(count);
			if (sNode.getNodeType() == Node.ELEMENT_NODE) {
				if ("line".equals(sNode.getNodeName())) {
					lines.add(readLine(sNode.getChildNodes()));
				}
			}
		}
		part.setLines(lines);
		song.getParts().add(part);
	}
	
	private Line readLine(NodeList nodeList) {
		Line line = new Line();
		for (int count = 0; count < nodeList.getLength(); count++) {
			Node sNode = nodeList.item(count);
			if (sNode.getNodeType() == Node.ELEMENT_NODE) {
				if ("text".equals(sNode.getNodeName())) {
					line.setText(sNode.getTextContent());
				} else if ("chords".equals(sNode.getNodeName())) {
					line.setChords(readRefChords(sNode.getChildNodes()));
				}
			}
		}
		return line;
	}

	private List<ChordRef> readRefChords(NodeList nodeList) {
		List<ChordRef> chords = new ArrayList<ChordRef>();
		for (int count = 0; count < nodeList.getLength(); count++) {
			Node sNode = nodeList.item(count);
			if (sNode.getNodeType() == Node.ELEMENT_NODE) {
				if ("chordref".equals(sNode.getNodeName())) {
					if (sNode.hasAttributes()) {
						NamedNodeMap nodeMap = sNode.getAttributes();
						ChordRef ref = new ChordRef();
						ref.setChordId(nodeMap.getNamedItem("id").getNodeValue());
						ref.setPosition(Integer.parseInt(nodeMap.getNamedItem("position").getNodeValue(), 10));
						chords.add(ref);
					}
				}		
			}
		}
		return chords;
	}
	
	
}
