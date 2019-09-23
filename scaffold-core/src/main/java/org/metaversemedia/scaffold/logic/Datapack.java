package org.metaversemedia.scaffold.logic;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.metaversemedia.scaffold.core.Project;

/**
 * Represents the main game datapack
 * @author Sam54123
 *
 */
public class Datapack {
	/* The datapack's main data folder */
	private Path dataFolder;
	
	/* Project this datapack belongs to */
	private Project project;
	
	private String defaultNamespace;
	
	/**
	 * Function names that will be called on datapack load
	 */
	public final List<String> loadFunctions = new ArrayList<String>();
	
	/**
	 *  Function names that will be called every tick 
	 */
	public final List<String> tickFunctions = new ArrayList<String>();
	
	/**
	 * Additional functions to be compiled into datapack
	 */
	public final List<MCFunction> functions = new ArrayList<MCFunction>();
	
	/**
	 * The description which is written in pack.mcmeta
	 */
	private String description = "Map Datapack";
	
	/**
	 * Create a new datapack object.
	 * @param project Project to associate with.
	 * @param dataFolder Datapack's main data folder.
	 * @param defaultNamespace Namespace to put defined function objects in.
	 */
	public Datapack(Project project, Path dataFolder, String defaultNamespace) {
		this.project = project;
		this.dataFolder = dataFolder;
		setDefaultNamespace(defaultNamespace);
	}
	
	/**
	 * Create a new datapack object
	 * @param project Project to associate with
	 * @param dataFolder Datapack's main data folder
	 */
	public Datapack(Project project, Path dataFolder) {
		this.project = project;
		this.dataFolder = dataFolder;
		setDefaultNamespace(project.getName());
	}
	
	/**
	 * Create a new datapack object
	 * @param project Project to associate with
	 */
	public Datapack(Project project) {
		this.project = project;
		this.dataFolder = project.assetManager().getAbsolutePath("data");
		setDefaultNamespace(project.getName());
	}
	
	/**
	 * Get the datapack's description
	 * @return Description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Set the datapack's description
	 * @param newDescription New description
	 */
	public void setDescription(String newDescription) {
		description = newDescription;
	}
	
	/**
	 * Set the datapack's default namespace
	 * @param namespace New namespace
	 */
	public void setDefaultNamespace(String namespace) {
		defaultNamespace = namespace.replaceAll("\\s+","").toLowerCase();
	}
	
	/**
	 * Get the datapack's default namespace
	 * @return Namespace
	 */
	public String getDefaultNamespace() {
		return defaultNamespace;
	}
	
	/**
	 * Compile this datapack into a runnable datapack
	 * @param compilePath Absolute path to folder to compile datapack in (datapacks/name)
	 * @return Success
	 * @throws IOException 
	 */
	public boolean compile(Path compilePath) throws IOException {
		// Reset folder
		if (compilePath.toFile().exists()) {
			FileUtils.deleteDirectory(compilePath.toFile());
		}
		
		// Load dataignore
		DataIgnore dataignore = DataIgnore.load(Paths.get(compilePath.toString(), "dataignore.txt"));
		
		
		// Copy the directory
		FileUtils.copyDirectory(dataFolder.toFile(), compilePath.resolve("data").toFile(), dataignore.new Filter(), true);
		
		
		// Write pack.mcmeta
		generatePackMCMeta(compilePath);
		
		
		// Write MCFunctions
		Path functionFolder = compilePath.resolve(Paths.get("data", defaultNamespace, "functions"));
		functionFolder.toFile().mkdirs();
		
		for (MCFunction func : functions) {
			func.variables().put("namespace", defaultNamespace);
		}
		
		for (MCFunction func : functions) {
			func.compile(functionFolder.resolve(func.getName()+".mcfunction").toFile());
		}
		
		for (MCFunction func : functions) {
			func.variables().remove("namespace");
		}
		
		return false;
	}
	
	/**
	 * Generate the pack.mcmeta file
	 * @param compilePath Absolute path to folder datapack is compiling in
	 * @throws IOException 
	 */
	private void generatePackMCMeta(Path compilePath) throws IOException {
		JSONObject root = new JSONObject();
		JSONObject pack = new JSONObject();
		
		pack.put("pack_format", 1);
		pack.put("description", description);
		root.put("pack", pack);
		
		// Save to file
		FileWriter writer = new FileWriter(new File(compilePath.toString(), "pack.mcmeta"));
		root.write(writer, 4, 0);
		writer.close();
	}

}
