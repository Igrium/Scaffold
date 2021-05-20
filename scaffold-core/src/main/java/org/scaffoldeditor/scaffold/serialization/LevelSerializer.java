package org.scaffoldeditor.scaffold.serialization;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.scaffoldeditor.scaffold.core.Constants;
import org.scaffoldeditor.scaffold.core.Project;
import org.scaffoldeditor.scaffold.level.Level;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class LevelSerializer implements XMLSerializable<Level> {
		
	private Level level;
	
	public LevelSerializer(Level level) {
		this.level = level;
	}

	@Override
	public Element serialize(Document document) {
		Element root = document.createElement("level");
		root.setAttribute("editor_version", Constants.VERSION);
		root.setAttribute("pretty_name", level.getPrettyName());
		
		Element entities = document.createElement("entities");
		for (String name : level.getEntityStack()) {
			Element entity = new EntitySerializer(level.getEntity(name)).serialize(document);
			entities.appendChild(entity);
		}
		
		root.appendChild(entities);
		return root;
	}
	
	/**
	 * Deserialize a level XML element and all its entities.
	 * @param xml XML to deserialize.
	 * @param project Project to deserialize into.
	 * @return Deserialized level.
	 */
	public static Level deserialize(Element xml, Project project) {
		String prettyName = xml.getAttribute("pretty_name");
		Level level = new Level(project, prettyName);
		
		NodeList children = xml.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) child;
				if (element.getTagName().equals("entities")) {
					loadEntities(level, element);
				}
			}
		}
		
		return level;
	}
	
	private static void loadEntities(Level level, Element xml) {
		NodeList children = xml.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				EntitySerializer.deserialize((Element) child, level);
			}
		}
		level.updateEntityStack();
	}
	
	/**
	 * Create an XML document with a serialized level.
	 * @param db Document builder to use.
	 * @throws ParserConfigurationException If the document builder factory is improperly formatted.
	 */
	public Document createXMLDocument(DocumentBuilder db) throws ParserConfigurationException {
		DocumentBuilder dBuilder = db;
		Document doc = dBuilder.newDocument();
		Element element = serialize(doc);
		doc.appendChild(element);
		
		return doc;
	}
	
	/**
	 * Get the level element out of an XML document.
	 * @param doc Document to read.
	 * @return Level element.
	 */
	public static Element readXMLDocument(Document doc) {
		doc.getDocumentElement().normalize();
		return doc.getDocumentElement();
	}
}
