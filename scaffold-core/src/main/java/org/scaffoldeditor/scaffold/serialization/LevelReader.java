package org.scaffoldeditor.scaffold.serialization;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.scaffoldeditor.scaffold.core.Project;
import org.scaffoldeditor.scaffold.level.Level;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Deserializes a level file.
 * @author Igrium
 */
public class LevelReader {
	private InputStream in;
	
	private DocumentBuilderFactory dbFactory;
	private DocumentBuilder dBuilder;
	
	public LevelReader(InputStream in) {
		this.in = in;
		this.dbFactory = DocumentBuilderFactory.newInstance();
		
		try {
			dBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw new AssertionError("Unable to create level writer.", e);
		}
	}
	
	/**
	 * Read the level file from this input stream.
	 * @param project Project to assign the level to.
	 * @return Deserialized level.
	 */
	public Level read(Project project) {
		try {
			Document doc = dBuilder.parse(in);
			doc.getDocumentElement().normalize();
			return LevelSerializer.deserialize(doc.getDocumentElement(), project);
			
		} catch (SAXException | IOException e) {
			e.printStackTrace();
			throw new AssertionError("Unable to read level.", e);
		}
	}
}
