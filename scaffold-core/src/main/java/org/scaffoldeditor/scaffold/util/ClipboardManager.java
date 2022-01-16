package org.scaffoldeditor.scaffold.util;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.stack.StackGroup;
import org.scaffoldeditor.scaffold.level.stack.StackItem;
import org.scaffoldeditor.scaffold.serialization.LoadContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
	
	/**
	 * Serialize a stack group into XML.
	 * @param group Group to serialize.
	 * @param raw Whether to include the group itself when pasting.
	 * @return XML string.
	 * @throws TransformerException If an unrecoverable error occursduring the course of the transformation.
	 */
	public String serializeGroup(StackGroup group, boolean raw) throws TransformerException {
		Document doc = dBuilder.newDocument();
		Element root = group.serialize(doc);
		root.setAttribute("raw", Boolean.toString(raw));
		doc.appendChild(root);
		
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(doc), new StreamResult(writer));
		return writer.getBuffer().toString();
	}
	
	/**
	 * Serialize a list of stack items into XML.
	 * @param items Items to serialize.
	 * @return XML string.
	 * @throws TransformerException If an unrecoverable error occursduring the course of the transformation.
	 */
	public String serializeItems(List<StackItem> items) throws TransformerException {
		StackGroup group = new StackGroup(items, "clipboard");
		return serializeGroup(group, true);
	}
	
	/**
	 * Copy a stack group to the clipboard.
	 * @param group Group to copy.
	 */
	public void copyGroup(StackGroup group) {
		try {
			String serialized = serializeGroup(group, false);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(new StringSelection(serialized), null);
		} catch (TransformerException e) {
			throw new AssertionError(e);
		}
	}
	
	/**
	 * Copy a list of stack items to the clipboard.
	 * @param items Items to copy.
	 */
	public void copyItems(List<StackItem> items) {
		try {
			String serialized = serializeItems(items);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(new StringSelection(serialized), null);
		} catch (TransformerException e) {
			throw new AssertionError(e);
		}
	}
	
	/**
	 * Deserialize string-based clipboard content.
	 * @param in XML string.
	 * @param level Level to assign to entities. Doesn't actually add to the level.
	 * @return Deserialized stack items.
	 */
	@SuppressWarnings("deprecation")
	public List<StackItem> deserialize(String in, Level level) {
		try {
			Document doc = dBuilder.parse(new InputSource(new StringReader(in)));
			Element root = doc.getDocumentElement();
			LoadContext context = new LoadContext();
			StackGroup group = StackGroup.deserialize(root, level, context);
			
			// Fix naming conflicts.
			Set<String> takenNames = new HashSet<>();
			for (Entity entity : level.getLevelStack()) {
				takenNames.add(entity.getName());
			}
			
			for (Entity entity : group) {
				String oldName = entity.getName();
				String newName = LevelOperations.validateName(oldName, takenNames);
				takenNames.add(newName);
				
				if (!oldName.equals(newName)) {
					entity.setName(newName);
					for (Entity ent : group) {
						ent.refactorName(oldName, newName);
					}
				}
				
				entity.onAdded();
			}
			
			boolean raw = "true".equals(root.getAttribute("raw"));	
			if (raw) {
				return group.items;
			} else {
				return List.of(new StackItem(group));
			}

		} catch (SAXException | IOException e) {
			throw new RuntimeException("Unable to deserialize clipboard content!", e);
		}
	}

	
	/**
	 * Paste the current clipboard content into the level.
	 * @param level Level to paste into.
	 * @param parent Group to paste under.
	 * @return The pasted items.
	 */
	public List<StackItem> paste(Level level, StackGroup parent) throws IOException {
		if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
			String data;
			try {
				data = (String) clipboard.getData(DataFlavor.stringFlavor);
			} catch (UnsupportedFlavorException | IOException e) {
				throw new RuntimeException("Unable to paste clipboard content!", e);
			}
			List<StackItem> parsed;
			try {
				parsed = deserialize(data, level);
			} catch (Throwable e) {
				throw new IOException("Unable to paste clipboard content!", e);
			}
			parent.items.addAll(parsed);
			level.updateLevelStack();

			return parsed;
		} else {
			return Collections.emptyList();
		}
	}
}
