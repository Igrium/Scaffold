package org.scaffoldeditor.scaffold.serialization;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.scaffoldeditor.scaffold.block_textures.BlockTextureRegistry;
import org.scaffoldeditor.scaffold.block_textures.SerializableBlockTexture;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class BlockTextureReader {
	final InputStream in;
	
	private DocumentBuilderFactory dbFactory;
	private DocumentBuilder dBuilder;
	
	public BlockTextureReader(InputStream in) {
		this.in = in;
		this.dbFactory = DocumentBuilderFactory.newInstance();

		try {
			dBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw new AssertionError("Unable to create level writer.", e);
		}
	}
	
	public SerializableBlockTexture read() throws IOException {
		try {
			Document doc = dBuilder.parse(in);
			doc.getDocumentElement().normalize();
			return BlockTextureRegistry.deserializeBlockTexture(doc.getDocumentElement());
			
		} catch (SAXException e) {
			e.printStackTrace();
			throw new AssertionError("Unable to read block texture.", e);
		}
	}
}
