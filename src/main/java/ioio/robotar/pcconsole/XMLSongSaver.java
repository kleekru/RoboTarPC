package ioio.robotar.pcconsole;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cz.versarius.xsong.ChordRef;
import cz.versarius.xsong.Line;
import cz.versarius.xsong.Part;
import cz.versarius.xsong.Song;

public class XMLSongSaver {
	public void save(Song song, File file) {
		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("song");
			doc.appendChild(rootElement);

			buildDom(doc, rootElement, song);

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(file);

			// Output to console for testing
			//StreamResult result = new StreamResult(System.out);

			transformer.transform(source, result);

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}

	private void buildDom(Document doc, Element root, Song song) {
		// header
		buildTextNode(doc, "title", song.getTitle(), root);
		buildTextNode(doc, "interpret", song.getInterpret(), root);
		buildTextNode(doc, "info", song.getInfo(), root);
		
		for (Part part : song.getParts()) {
			Element partElement = buildContainer(doc, part.getTypeName(), root);
			for (Line line : part.getLines()) {
				Element lineElement = buildContainer(doc, "line", partElement);
				buildTextNode(doc, "text", line.getText(), lineElement);
				Element chordsElement = buildContainer(doc, "chords", lineElement);
				for (ChordRef chref : line.getChords()) {
					buildChordRef(doc, "chordref", chref, chordsElement);	
				}
			}
		}
	}
	
	private Element buildChordRef(Document doc, String name, ChordRef chref,
			Element parent) {
		Element child = doc.createElement(name);
		child.setAttribute("id", chref.getChordId());
		child.setAttribute("position", Integer.toString(chref.getPosition(), 10));
		parent.appendChild(child);
		return child;
	}

	private Element buildContainer(Document doc, String name, Element parent) {
		Element child = doc.createElement(name);
		// more .. attributes...
		parent.appendChild(child);
		return child;
	}

	private Element buildTextNode(Document doc, String name, String value, Element parent) {
		Element child = doc.createElement(name);
		child.appendChild(doc.createTextNode((value == null)?"":value));
		parent.appendChild(child);
		return child;
	}
}
