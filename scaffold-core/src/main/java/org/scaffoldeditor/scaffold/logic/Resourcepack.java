package org.scaffoldeditor.scaffold.logic;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.scaffoldeditor.scaffold.util.ZipUtils;

/**
 * Represents a Minecraft resourecpack.
 * @author Igrium
 */
public class Resourcepack {

	private Path assetFolder;
	private String name;
	private String description;
	
	/**
	 * Create a new Resourcepack.
	 * @param assetFolder Path to resourcepack's assets folder.
	 */
	public Resourcepack(Path assetFolder) {
		this.assetFolder = assetFolder; 
	}
	
	/**
	 * Get the path to this resourcepack's assets folder.
	 * @return Assets folder.
	 */
	public Path getAssetFolder() {
		return assetFolder;
	}
	
	/**
	 * Set the path to this resourcepack's assets folder.
	 * @param path Assets folder.
	 */
	public void setAssetFolder(Path path) {
		this.assetFolder = path;
	}
	
	/**
	 * Get the title this resourcepack will show up as in the resourcepacks screen.
	 * @return Pack name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Set the title this resourcepack will show up as in the resourcepacks screen.
	 * @param name Pack name.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Get the description this resourcepack will show up with in the resourcepacks screen.
	 * @return Pack description.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Set the description this resourcepack will show up with in the resourcepacks screen.
	 * @param description Pack description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Compile the datapack.
	 * @param compilePath Folder to compile to.
	 * @param compress Should the resourcepack be compressed into a zip?
	 * @throws IOException If an IO Exception occurs.
	 */
	public void compile(Path compilePath, Boolean compress) throws IOException {
		Path assetPath = compilePath.resolve("assets");
		
		// Reset folder
		if (compilePath.toFile().exists()) {
			FileUtils.deleteDirectory(compilePath.toFile());
		}
		
//		// Create assetIgnore.
//		FileIgnore assetIgnore = new FileIgnore(assetPath.resolve("assetignore.txt")) {
//
//			@Override
//			protected void addDefaults() {
//				ignoredFiles.add(Paths.get("assetignore.txt"));
//			}
//		};
		
		// Copy resources.
//		FileUtils.copyDirectory(assetFolder.toFile(), assetPath.toFile(), assetIgnore.new Filter(), true);
		
		// Generat pack.mcmeta.
		File packMeta = compilePath.resolve("pack.mcmeta").toFile();
		packMeta.createNewFile();
		
		FileWriter writer = new FileWriter(packMeta);
		packMeta().write(writer);
		writer.close();
		
		// Zip file
		if (compress) {
			// Create zip file.
			File zipFile = compilePath.getParent().resolve(compilePath.getFileName().toString()+".zip").toFile();		
			
			if (zipFile.exists()) {
				zipFile.delete();
			}
			
			List<File> directoryListing = Arrays.asList(compilePath.toFile().listFiles());
			ZipUtils.zip(directoryListing, zipFile.toString());
			
			FileUtils.deleteDirectory(compilePath.toFile());
		}
	}
	
	/**
	 * Generate the data for the pack.mcmeta file.
	 * @return Pack metadata.
	 */
	public JSONObject packMeta() {
		JSONObject root = new JSONObject();
		JSONObject pack = new JSONObject();
		pack.put("pack_format", 4);
		pack.put("description", description);
		root.put("pack", pack);
		return root;
	}
}
