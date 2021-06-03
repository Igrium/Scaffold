package org.scaffoldeditor.scaffold.level.entity.attribute;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.transform.TransformerException;
import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.scaffold.block_textures.BlockTextureRegistry;
import org.scaffoldeditor.scaffold.block_textures.SerializableBlockTexture;
import org.scaffoldeditor.scaffold.block_textures.SingleBlockTexture;
import org.scaffoldeditor.scaffold.io.AssetManager;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.serialization.BlockTextureWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class BlockTextureAttribute extends Attribute<SerializableBlockTexture> {
	
	public static final String REGISTRY_NAME = "block_texture";
	
	public static void register() {
		AttributeRegistry.registry.put(REGISTRY_NAME, new AttributeFactory<BlockTextureAttribute>() {

			@Override
			public BlockTextureAttribute create() {
				return new BlockTextureAttribute(new SingleBlockTexture(new Block("minecraft:stone")));
			}

			@Override
			public BlockTextureAttribute deserialize(Element element) {
				if (Boolean.parseBoolean(element.getAttribute("external"))) {
					return new BlockTextureAttribute("externalPath");
				} else {
					NodeList children = element.getChildNodes();
					for (int i = 0; i < children.getLength(); i++) {
						Node child = children.item(i);
						if (child.getNodeType() == Node.ELEMENT_NODE) {
							SerializableBlockTexture value = BlockTextureRegistry.deserializeBlockTexture((Element) child);
							return new BlockTextureAttribute(value);
						}
					}
				}
				throw new AssertionError("Unable to parse block texture!");
			}
		});
	}
	
	private boolean external;
	private String externalPath = "";
	
	private SerializableBlockTexture value;
	
	/**
	 * Create a block texture attribute that uses an internal texture.
	 * @param value Texture to use.
	 */
	public BlockTextureAttribute(SerializableBlockTexture value) {
		this.value = value;
		external = false;
	}
	
	/**
	 * Create a block texture attribute that references an external texture.
	 * @param path Asset path to the texture.
	 */
	public BlockTextureAttribute(String path) {
		AssetManager assetManager = AssetManager.getInstance();
		if (!assetManager.getLoader(path).isAssignableTo(SerializableBlockTexture.class)) {
			handleLoadFailed();
		}
		
		try {
			this.value = (SerializableBlockTexture) assetManager.loadAsset(path, false);
			this.external = true;
		} catch (IOException e) {
			e.printStackTrace();
			handleLoadFailed();
		}
		
	}
	
	/**
	 * Clone a block texture attribute, transferring it's internal value into an external file.
	 * @param old Old texture.
	 * @param externalPath
	 */
	public BlockTextureAttribute(BlockTextureAttribute old, String externalPath) {
		if (old.isExternal()) {
			this.value = old.value;
			this.externalPath = old.getExternalPath();
		} else {
			this.value = old.value;
			this.externalPath = externalPath;
			try {
				save();
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("Unable to save new block texture.");
			}
		}
	}
	
	private void handleLoadFailed() {
		this.value = new SingleBlockTexture(new Block("minecraft:stone"));
		external = false;
	}

	/**
	 * Get the block texture this attribute is referencing.<br>
	 * {@link Entity#onUpdateAttributes} should be called after updating if a
	 * recompilation is desired.
	 */
	public SerializableBlockTexture getValue() {
		return value;
	}

	@Override
	public Element serialize(Document document) {
		Element element = document.createElement(REGISTRY_NAME);
		element.setAttribute("external", Boolean.toString(isExternal()));
		if (isExternal()) {
			element.setAttribute("externalPath", getExternalPath());
		} else {
			element.appendChild(getValue().serialize(document));
		}
		return element;
	}

	@Override
	public Attribute<SerializableBlockTexture> clone() {
		if (external) {
			return this;
		} else {
			return this;
		}
	}
	
	/**
	 * If true, this attribute is referencing a block texture in an external file.
	 */
	public boolean isExternal() {
		return external;
	}
	
	/**
	 * Get the asset path to the external texture file.
	 * @return The path, or "" if we're using an internal texture.
	 */
	public String getExternalPath() {
		return externalPath;
	}
	
	/**
	 * If the texture is external, save it to file.
	 * @throws IOException If an IO exception occurs. 
	 */
	public void save() throws IOException {
		if (isExternal()) {
			AssetManager assetManager = AssetManager.getInstance();
			File file = assetManager.getAbsoluteFile(externalPath);
			BlockTextureWriter writer = new BlockTextureWriter(new FileOutputStream(file));
			try {
				writer.write(getValue());
			} catch (TransformerException e) {
				throw new AssertionError("Unable to compile block texture XML.", e);
			}
			
			assetManager.forceCache(externalPath, getValue());
		}
	}
}
