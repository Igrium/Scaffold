package org.scaffoldeditor.scaffold.logic;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.scaffoldeditor.nbt.util.Pair;
import org.scaffoldeditor.scaffold.core.Project;
import org.scaffoldeditor.scaffold.logic.datapack.AbstractFunction;
import org.scaffoldeditor.scaffold.logic.datapack.FunctionNameUtils;

/**
 * Represents the main game datapack
 * @author Igrium
 *
 */
public class Datapack extends AbstractPack {
	
	public static final String LOAD_TAG_FILE = "minecraft/tags/functions/load.json";
	public static final String TICK_TAG_FILE = "minecraft/tags/functions/tick.json";
	
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
	
	/**
	 * A mutable list of all the functions that will be compiled into the datapack.
	 */
	public final Collection<AbstractFunction> functions = new ArrayList<>();
	
	/**
	 * Create a new datapack object.
	 * @param project Project to associate with.
	 * @param defaultNamespace Namespace to put defined function objects in.
	 */
	public Datapack(Project project, String defaultNamespace) {
		super(project, "data");
		setDefaultNamespace(defaultNamespace);
		setDescription("Map datapack");
		setPackFormat(7);
	}
	
	/**
	 * Create a new datapack object
	 * @param project Project to associate with
	 */
	public Datapack(Project project) {
		super(project, "data");
		setDefaultNamespace(project.getName());
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

	
	private JSONArray loadCache;
	private JSONArray tickCache;

	@Override
	protected void preCompile(OutputWriter writer) throws IOException {
		loadCache = new JSONArray();
		tickCache = new JSONArray();
	}
	
	@Override
	protected void processFile(InputStream in, OutputStream out, String filepath) throws IOException {
		if (FilenameUtils.equalsNormalized(filepath, LOAD_TAG_FILE)) {
			try {
				JSONTokener tokener = new JSONTokener(in);
				JSONObject object = new JSONObject(tokener);
				for (Object obj : object.getJSONArray("values")) {
					loadCache.put(obj);
				}			
			} catch (JSONException e) {
				LogManager.getLogger().error("Error parsing JSON file: filepath!", e);
			}
			
		} else if (FilenameUtils.equalsNormalized(filepath, TICK_TAG_FILE)) {
			try {
				JSONTokener tokener = new JSONTokener(in);
				JSONObject object = new JSONObject(tokener);
				for (Object obj : object.getJSONArray("values")) {
					tickCache.put(obj);
				}		
			} catch (JSONException e) {
				LogManager.getLogger().error("Error parsing JSON file: filepath!", e);
			}
		} else {
			super.processFile(in, out, filepath);
		}
	}

	@Override
	protected void postCompile(OutputWriter writer) throws IOException {
		for (Pair<String, String> function : loadFunctions) {
			loadCache.put(function.getFirst()+":"+function.getSecond());
		}
		for (Pair<String, String> function : tickFunctions) {
			tickCache.put(function.getFirst()+":"+function.getSecond());
		}
		
		PackOutputStream out1 = writer.openStream("data/" + LOAD_TAG_FILE);
		OutputStreamWriter writer1 = new OutputStreamWriter(out1);
		JSONObject loadFunctions = new JSONObject();
		loadFunctions.put("values", loadCache);
		writer1.write(loadFunctions.toString(4));
		writer1.close();
		
		PackOutputStream out2 = writer.openStream("data/" + TICK_TAG_FILE);
		OutputStreamWriter writer2 = new OutputStreamWriter(out2);
		JSONObject tickFunctions = new JSONObject();
		tickFunctions.put("values", tickCache);
		writer2.write(tickFunctions.toString(4));
		writer2.close();
		
		// Compile functions.
		for (AbstractFunction function : functions) {
			PackOutputStream out = writer.openStream("data/" + FunctionNameUtils.metaToPath(function.getMeta()).toString());
			BufferedWriter bufWriter = new BufferedWriter(new OutputStreamWriter(out));
			bufWriter.write(function.compile());
			bufWriter.close();
		}
	}

}
