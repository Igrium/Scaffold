package org.metaversemedia.scaffold.level.entity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.metaversemedia.scaffold.level.Level;
import org.metaversemedia.scaffold.nbt.schematic.Structure;

/**
 * Represents a Minecraft structure in the level (.nbt);
 * @author Sam54123
 */
public class StructureEntity extends BlockCollectionEntity {
	
	// Keep track of the old file path in case we need to check against it once it's been updated
	private String oldFile;

	public StructureEntity(Level level, String name) {
		super(level, name);
		// Reference to schematic file to use
		attributes().put("file", "");
	}

	@Override
	public void reload(boolean force) {
		
		// Only reload if we have to
		if (force || !getAttribute("file").equals(oldFile)) {
			// Load structure
			File structureFile = getLevel().getProject().assetManager().findAsset((String) getAttribute("file")).toFile();

			try {
				blockCollection = Structure.fromFile(structureFile);
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
