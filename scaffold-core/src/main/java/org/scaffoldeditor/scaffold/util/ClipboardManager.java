package org.scaffoldeditor.scaffold.util;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.BlockEntity;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.serialization.EntitySerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Manages copying and pasting entities
 * @author Igrium
 */
public class ClipboardManager {
	final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	private DocumentBuilderFactory dbFactory;
	private DocumentBuilder dBuilder;
	private TransformerFactory tFactory;
	private Transformer transformer;
	
	private static ClipboardManager instance;
	public static ClipboardManager getInstance() {
		if (instance == null) {
			instance = new ClipboardManager();
		}
		return instance;
	}
	
	private ClipboardManager() {
		this.dbFactory = DocumentBuilderFactory.newInstance();
		this.tFactory = TransformerFactory.newInstance();
		try {
			this.dBuilder = dbFactory.newDocumentBuilder();
			this.transformer = tFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		} catch (ParserConfigurationException | TransformerConfigurationException e) {
			e.printStackTrace();
			throw new AssertionError("Unable to create scaffold clipboard.", e);
		}
	}
	
	public String serializeEntities(Set<Entity> entities) throws TransformerException {	

		Document doc = dBuilder.newDocument();
		Element root = doc.createElement("entities");
		for (Entity entity : entities) {
			Element element = new EntitySerializer(entity).serialize(doc);
			root.appendChild(element);
		}
		doc.appendChild(root);

		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(doc), new StreamResult(writer));
		return writer.getBuffer().toString();

	}
	
	public Set<Entity> deserializeEntities(String in, Level level) {
		Set<Entity> entities = new HashSet<>();
		try {
			Document doc = dBuilder.parse(new InputSource(new StringReader(in)));
			Element entitiesElement = doc.getDocumentElement();
			NodeList children = entitiesElement.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				if (child.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) child;
					entities.add(EntitySerializer.deserialize(element, level));
				}
			}
			level.updateEntityStack();

		} catch (SAXException | IOException e) {
			throw new AssertionError(e);
		}

		for (Entity ent : entities) {
			if (ent instanceof BlockEntity) {
				level.dirtySections.addAll(((BlockEntity) ent).getOverlappingSections());
			}
		}
		if (level.autoRecompile) {
			level.quickRecompile();
		}
		return entities;
	}
	
	/**
	 * Copy a set of entities to the clipboard.
	 * @param entities Entities to copy.
	 */
	public void copyEntities(Set<Entity> entities) {
		try {
			StringSelection data = new StringSelection(serializeEntities(entities));
			clipboard.setContents(data, data);
		} catch (Exception e) {
			throw new AssertionError("Unable to copy entities!", e);
		}
		
	}
	
	/**
	 * Paste the entities from the clipboard.
	 * @param level Level to paste into.
	 * @return The pasted entities.
	 */
	public Set<Entity> pasteEntities(Level level) {
		if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
			try {
				String data = (String) clipboard.getData(DataFlavor.stringFlavor);
				return deserializeEntities(data, level);
			} catch (Exception e) {
				throw new AssertionError("Unable to paste entities!", e);
			}
		} else {
			return new HashSet<>();
		}
	}	
}
