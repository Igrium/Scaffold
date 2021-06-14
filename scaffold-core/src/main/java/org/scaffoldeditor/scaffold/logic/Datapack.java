package org.scaffoldeditor.scaffold.logic;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scaffoldeditor.nbt.util.Pair;
import org.scaffoldeditor.scaffold.core.Project;
import org.scaffoldeditor.scaffold.logic.datapack.AbstractFunction;
import org.scaffoldeditor.scaffold.util.GitignoreUtils;
import org.scaffoldeditor.scaffold.util.GitignoreUtils.Gitignore;

/**
 * Represents the main game datapack
 * @author Igrium
 *
 */
public class Datapack {
	/* The datapack's main data folder */
	private Path dataFolder;
	
	private String defaultNamespace;
	
	/**
	 * Functions that will be called on datapack load.
	 * The first entry in the pair is the function namespace and the second entry is the pathname.
	 */
	public final List<Pair<String, String>> loadFunctions = new ArrayList<>();
	
	/**
	 *  Functions that will be called every tick.
	 *  The first entry in the pair is the function namespace and the second entry is the pathname.
	 */
	public final List<Pair<String, String>> tickFunctions = new ArrayList<>();
	
	public final Collection<AbstractFunction> functions = new ArrayList<>();
	
	/**
	 * The description which is written in pack.mcmeta
	 */
	private String description = "Map Datapack";
	
	/**
	 * Project this datapack is associated with
	 */
	private Project project;
	
	/**
	 * Create a new datapack object.
	 * @param project Project to associate with.
	 * @param dataFolder Datapack's main data folder.
	 * @param defaultNamespace Namespace to put defined function objects in.
	 */
	public Datapack(Project project, Path dataFolder, String defaultNamespace) {
		
		this.dataFolder = dataFolder;
		setDefaultNamespace(defaultNamespace);
	}
	
	/**
	 * Create a new datapack object.
	 * @param project Project to associate with.
	 * @param defaultNamespace Namespace to put defined function objects in.
	 */
	public Datapack(Project project, String defaultNamespace) {
		this.project = project;
		this.dataFolder = project.assetManager().getAbsolutePath("data");
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
	 * Get the project this datapack is associated with
	 * @return
	 */
	public Project getProject() {
		return project;
	}

	
	/**
	 * Compile this datapack into a runnable datapack
	 * @param compilePath Absolute path to folder to compile datapack in (datapacks/name)
	 * @return Success
	 * @throws IOException 
	 */
	public boolean compile(Path compilePath) throws IOException {
		LogManager.getLogger().info("Starting datapack compile.");

		
		// Reset folder
		if (compilePath.toFile().exists()) {
			FileUtils.deleteDirectory(compilePath.toFile());
		}
		
		String dataIgnoreContent = Files.readString(compilePath.resolve(".ignore"));
		Gitignore dataIgnore = GitignoreUtils.load(dataIgnoreContent);
		
		FileUtils.copyDirectory(dataFolder.toFile(), compilePath.resolve("data").toFile(), (pathName) -> {
			return dataIgnore.accepts(pathName.toString());
		});
		
		// Write pack.mcmeta
		generatePackMCMeta(compilePath);
		
		
		/* SET LOAD FUNCTIONS */
		Path functionTags = compilePath.resolve(Paths.get("data","minecraft","tags","functions"));
		if (!functionTags.toFile().exists()) {
			functionTags.toFile().mkdirs();
		}
		
		JSONObject load;
		JSONArray loadValues;
		
		// JSON file may already exist. Load if it does.
		if (functionTags.resolve("load.json").toFile().exists()) {
			load = loadJSON(functionTags.resolve("load.json"));
		} else {
			load = new JSONObject();
			loadValues = new JSONArray();
			load.put("values",loadValues);
		}
		
		// Redundency if values never got assigned due to deserialization
		loadValues = load.getJSONArray("values");
		for (Pair<String, String> f : loadFunctions) {
			loadValues.put(f.getFirst()+":"+f.getSecond());
		}
		
		// Save JSON object
		FileWriter loadWriter = new FileWriter(functionTags.resolve("load.json").toFile());
		load.write(loadWriter, 4, 0);
		loadWriter.close();
		
		
		/* SET TICK FUNCTIONS */
		JSONObject tick;
		JSONArray tickValues;
		
		// JSON file may already exist. Load if it does.
		if (functionTags.resolve("tick.json").toFile().exists()) {
			tick = loadJSON(functionTags.resolve("tick.json"));
		} else {
			tick = new JSONObject();
			tickValues = new JSONArray();
			tick.put("values",tickValues);
		}
				
		// Redundency if values never got assigned due to deserialization
		tickValues = tick.getJSONArray("values");
		for (Pair<String, String> f : tickFunctions) {
			tickValues.put(f.getFirst()+":"+f.getSecond());
		}
		
		// Save JSON object
		FileWriter tickWriter = new FileWriter(functionTags.resolve("tick.json").toFile());
		tick.write(tickWriter, 4, 0);
		tickWriter.close();
		
		
		return true;
	}
	
	/**
	 * Generate the pack.mcmeta file
	 * @param compilePath Absolute path to folder datapack is compiling in
	 * @throws IOException 
	 */
	private void generatePackMCMeta(Path compilePath) throws IOException {
		JSONObject root = new JSONObject();
		JSONObject pack = new JSONObject();
		
		pack.put("pack_format", 7);
		pack.put("description", description);
		root.put("pack", pack);
		
		// Save to file
		FileWriter writer = new FileWriter(new File(compilePath.toString(), "pack.mcmeta"));
		writer.write(root.toString(4));
		writer.close();
	}
	
	/* Load a JSONObject from a file */
	private static JSONObject loadJSON(Path inputPath) throws IOException, JSONException {
		List<String> jsonFile = Files.readAllLines(inputPath);
		JSONObject jsonObject = new JSONObject(String.join("", jsonFile));
		return jsonObject;
	}

}
