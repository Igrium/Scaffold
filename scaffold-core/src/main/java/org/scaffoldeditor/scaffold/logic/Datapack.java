package org.scaffoldeditor.scaffold.logic;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.python.core.PyException;
import org.scaffoldeditor.scaffold.core.Project;
import org.scaffoldeditor.scaffold.core.PythonUtils;

/**
 * Represents the main game datapack
 * @author Sam54123
 *
 */
public class Datapack {
	/* The datapack's main data folder */
	private Path dataFolder;
	
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
	 * Format a function name as if it were in the default namespace.
	 * @param function Function name to format.
	 * @return Formatted name.
	 */
	public String formatFunctionCall(String name) {
		return getDefaultNamespace()+":"+name;
	}
	
	/**
	 * Format a function name as if it were in the default namespace.
	 * @param function Function to format.
	 * @return Formatted name.
	 */
	public String formatFunctionCall(MCFunction function) {
		return formatFunctionCall(function.getName());
	}
	
	/**
	 * Compile this datapack into a runnable datapack
	 * @param compilePath Absolute path to folder to compile datapack in (datapacks/name)
	 * @return Success
	 * @throws IOException 
	 */
	public boolean compile(Path compilePath) throws IOException {
		System.out.println("Starting datapack compile.");
		System.out.println();
		
		// Run pre-compile script
		preCompileScript();
		
		// Reset folder
		if (compilePath.toFile().exists()) {
			FileUtils.deleteDirectory(compilePath.toFile());
		}
		
		// Load dataignore
		FileIgnore dataignore = new DataIgnore(Paths.get(dataFolder.toString(), "dataignore.txt"));
		
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
		for (String f : loadFunctions) {
			loadValues.put(f);
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
		for (String f : tickFunctions) {
			tickValues.put(f);
		}
		
		// Save JSON object
		FileWriter tickWriter = new FileWriter(functionTags.resolve("tick.json").toFile());
		tick.write(tickWriter, 4, 0);
		tickWriter.close();
		
		postCompileScript(compilePath);
		
		return true;
	}
	
	/**
	 * Run pre-compile scipt
	 */
	private void preCompileScript() {
		try {
			if (dataFolder.resolve("compile.json").toFile().exists()) {
				JSONObject compile = loadJSON(dataFolder.resolve("compile.json"));
				if (!compile.has("preCompileScript")) { // May not have a pre-compile script
					return;
				}
				String scriptName = compile.getString("preCompileScript");
				String absName = project.assetManager().getAbsolutePath(scriptName).toString();
				
				if (!new File(absName).exists()) {
					System.out.println("Unable to find pre-compile script: "+absName);
					return;
				}
				
				// Setup and run script
				PythonUtils.setArgs(new String[] {dataFolder.toString()});
				
				System.out.println("Running python script: "+absName);
				
				try {
					PythonUtils.getInterpreter().execfile(absName);
				} catch (PyException e) { // User may write python file incorrectly
					if (e.traceback == null) {
						System.out.println("Improperly formatted Python file: "+absName);
					} else {
						System.out.println(e.traceback.dumpStack());
					}
				}
				System.out.println();
				
			}
		} catch (IOException e) {
			System.out.println("Unable to run pre-compile script!");
		}
		
	}
	
	/**
	 * Run post-compile script
	 */
	private void postCompileScript(Path compilePath) {
		try {
			if (dataFolder.resolve("compile.json").toFile().exists()) {
				JSONObject compile = loadJSON(dataFolder.resolve("compile.json"));
				if (!compile.has("postCompileScript")) { // May not have a post-compile script
					return;
				}
				String scriptName = compile.getString("postCompileScript");
				String absName = project.assetManager().getAbsolutePath(scriptName).toString();
				
				if (!new File(absName).exists()) {
					System.out.println("Unable to find post-compile script: "+absName);
					return;
				}
				
				// Setup and run script
				PythonUtils.setArgs(new String[] {compilePath.toString()});
				
				System.out.println("Running python script: "+absName);
				
				try {
					PythonUtils.getInterpreter().execfile(absName);
				} catch (PyException e) { // User may write python file incorrectly
					if (e.traceback == null) {
						System.out.println("Improperly formatted Python file: "+absName);
					} else {
						System.out.println(e.traceback.dumpStack());
					}
				}
				System.out.println();
			}
		} catch (IOException e) {
			System.out.println("Unable to run pre-compile script!");
		}
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
	
	/* Load a JSONObject from a file */
	private static JSONObject loadJSON(Path inputPath) throws IOException, JSONException {
		List<String> jsonFile = Files.readAllLines(inputPath);
		JSONObject jsonObject = new JSONObject(String.join("", jsonFile));
		return jsonObject;
	}

}