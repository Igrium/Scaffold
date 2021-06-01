package org.scaffoldeditor.scaffold.serialization;

import java.io.OutputStream;

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

import org.scaffoldeditor.scaffold.block_textures.SerializableBlockTexture;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class BlockTextureWriter {
	final OutputStream out;
	private DocumentBuilderFactory dbFactory;
	private DocumentBuilder dBuilder;
	private TransformerFactory tFactory;
	private Transformer transformer;
	
	public BlockTextureWriter(OutputStream out) {
		this.out = out;
		this.dbFactory = DocumentBuilderFactory.newInstance();
		this.tFactory = TransformerFactory.newInstance();
		try {
			this.dBuilder = dbFactory.newDocumentBuilder();
			this.transformer = tFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		} catch (ParserConfigurationException | TransformerConfigurationException e) {
			e.printStackTrace();
			throw new AssertionError("Unable to create block texture writer.", e);
		}
	}
	
	public void write(SerializableBlockTexture blockTexture) throws TransformerException {
		Document doc = createXMLDocument(blockTexture);
		DOMSource source = new DOMSource(doc);
		StreamResult output = new StreamResult(out);
		
		transformer.transform(source, output);
	}
	
	private Document createXMLDocument(SerializableBlockTexture blockTexture) {
		Document doc = dBuilder.newDocument();
		Element element = blockTexture.serialize(doc);
		doc.appendChild(element);
		
		return doc;
	}
}
