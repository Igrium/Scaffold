package org.scaffoldeditor.scaffold.serialization;

import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.scaffoldeditor.scaffold.level.Level;
import org.w3c.dom.Document;

/**
 * Serializes and writes a level to file.
 * @author Igrium
 */
public class LevelWriter {
	private OutputStream out;
	private DocumentBuilderFactory dbFactory;
	private DocumentBuilder dBuilder;
	private TransformerFactory tFactory;
	private Transformer transformer;
	
	/**
	 * Create a level writer.
	 * @param out Output stream to write to.
	 */
	public LevelWriter(OutputStream out) {
		this.out = out;
		this.dbFactory = DocumentBuilderFactory.newInstance();
		this.tFactory = TransformerFactory.newInstance();
		try {
			this.dBuilder = dbFactory.newDocumentBuilder();
			this.transformer = tFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		} catch (ParserConfigurationException | TransformerConfigurationException e) {
			e.printStackTrace();
			throw new AssertionError("Unable to create level writer.", e);
		}
	}
	
	public void write(Level level) {
		try {
			Document doc = new LevelSerializer(level).createXMLDocument(dBuilder);
			DOMSource source = new DOMSource(doc);
			StreamResult output = new StreamResult(out);
			
			transformer.transform(source, output);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
