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

import cz.versarius.xchords.Chord;
import cz.versarius.xchords.ChordBag;
import cz.versarius.xchords.ChordLibrary;
import cz.versarius.xchords.StringInfo;
import cz.versarius.xchords.StringState;

public class XMLChordSaver extends XMLSaver {
	public void save(ChordLibrary chords, File file) {
		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("chordlibrary");
			doc.appendChild(rootElement);

			buildDom(doc, rootElement, chords);

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
	
	protected void buildDom(Document doc, Element root, ChordLibrary chords) {
		// header
		buildTextNode(doc, "desc", chords.getDesc(), root);
		
		buildChords(doc, root, chords);
	}
	
	protected void buildChords(Document doc, Element where, ChordBag chords) {
		for (Chord chord : chords.getChords()) {
			// filtering? maybe no, because it's only temporary, it will be different in future...
			// ok, save whole chord library
			Element chordElement = buildChord(doc, chord, where);
			buildTextNode(doc, "name", chord.getName(), chordElement);
			Element positionElement = buildPosition(doc, chord, chordElement);
			for (StringInfo si : chord.getStrings()) {
				if (si != null) {
					Element stringElement = buildString(doc, si, positionElement);
				}
			}
		}
	}
	
	protected Element buildChord(Document doc, Chord chord, Element parent) {
		Element child = doc.createElement("chord");
		child.setAttribute("id", chord.getId());
		parent.appendChild(child);
		return child;
	}

	protected Element buildPosition(Document doc, Chord chord, Element parent) {
		Element child = doc.createElement("position");
		child.setAttribute("base", "1");
		parent.appendChild(child);
		return child;
	}
	
	protected Element buildString(Document doc, StringInfo si,
			Element parent) {
		Element child = doc.createElement("string");
		child.setAttribute("name", si.getName());
		if (si.getState() == StringState.DISABLED) {
			child.setAttribute("state", "no");	
		} else {
			child.setAttribute("fret", Integer.toString(si.getFret(), 10));
			if (si.getFinger() != null && !"".equals(si.getFinger().trim())) {
				child.setAttribute("finger", si.getFinger());
			}
		}
		parent.appendChild(child);
		return child;
	}

}
