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

public class XMLSettingSaver extends XMLSaver {
	public void save(float [][] corr, File file) {
		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("servo-corrections");
			doc.appendChild(rootElement);

			buildDom(doc, rootElement, corr);

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
	
	protected void buildDom(Document doc, Element root, float[][] corr) {
		for (int i=0; i<12; i++) {
			buildCorrection(doc, "correction", i+1, corr[i], root);
		}
	}
	
	protected Element buildCorrection(Document doc, String name, int i, float[] values,
			Element parent) {
		Element child = doc.createElement(name);
		child.setAttribute("servo", Integer.toString(i, 10));
		child.setAttribute("neutral", Float.toString(values[0]));
		child.setAttribute("muted", Float.toString(values[1]));
		child.setAttribute("left", Float.toString(values[2]));
		child.setAttribute("right", Float.toString(values[3]));
		parent.appendChild(child);
		return child;
	}
}
