package org.scaffoldeditor.scaffold.level.entity.block;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.scaffoldeditor.nbt.schematic.Structure;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.Entity.AttributeDeclaration;
import org.scaffoldeditor.scaffold.level.entity.Entity.FileAttribute;

/**
 * Represents a Minecraft structure in the level (.nbt);
 * @author Sam54123
 */
public class StructureEntity extends BlockCollectionEntity {
	
	public class StructureFile extends FileAttribute {
		@Override
		public String getFileType() {
			return "nbt";
		}
	}
	
	// Keep track of the old file path in case we need to check against it once it's been updated
	private String oldFile;

	public StructureEntity(Level level, String name) {
		super(level, name);
		// Reference to schematic file to use
		attributes().put("file", "");
	}
	
	@Override
	public List<AttributeDeclaration> getAttributeFields() {
		List<AttributeDeclaration> attributeFields = super.getAttributeFields();
		
		attributeFields.add(new AttributeDeclaration("file", StructureFile.class));
		
		return attributeFields;
	}

	@Override
	public void reload(boolean force) {
		
		// Only reload if we have to
		if (force || !getAttribute("file").equals(oldFile)) {
			// Load structure
			File structureFile = getLevel().getProject().assetManager().findAsset((String) getAttribute("file")).toFile();

			try {
				this.blockCollection = Structure.fromFile(structureFile);
			} catch (FileNotFoundException e) {
				System.out.println("Unable to find structure: " + getAttribute("file"));
			} catch (IOException e) {
				System.out.println("Unable to load structure: " + structureFile);
				e.printStackTrace();
			}
			
			oldFile = (String) getAttribute("file");
		}
	}
}
