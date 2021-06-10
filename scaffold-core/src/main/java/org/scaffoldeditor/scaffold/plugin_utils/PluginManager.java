package org.scaffoldeditor.scaffold.plugin_utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.json.JSONObject;
import org.json.JSONTokener;

public class PluginManager {
	
	
	public static class Plugin {
		public final JSONObject definition;
		public final ClassLoader classLoader;
		private String name;
		private PluginInitializer initializer;
		
		public Plugin(JSONObject definition, ClassLoader classLoader) {
			this.definition = definition;
			this.classLoader = classLoader;
			this.name = definition.getString("name");
		}
		
		public boolean initialize() {
			String className = definition.getString("initializer");
			LogManager.getLogger().info("Initializing plugin: "+name);
			
			try {
				Class<?> initClass = Class.forName(className, true, classLoader);
				Constructor<?> constructor = initClass.getConstructor();
				Object init = constructor.newInstance();
				if (!(init instanceof PluginInitializer)) {
					LogManager.getLogger().error("Initializer for "+name+" does not implement PluginInitializer!");
					return false;
				}
				initializer = (PluginInitializer) init;
				
			} catch (ClassNotFoundException e) {
				LogManager.getLogger().error("Unable to find plugin initializer class: "+className);
				return false;
			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException |
					IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
				return false;
			}
			
			try {
				initializer.initialize();
				return true;
			} catch (Exception e) {
				throw new IllegalStateException("An error occured in the init function of "+name, e);
			}
		}
		
		public PluginInitializer getInitializer() {
			return initializer;
		}

		public String getName() {
			return name;
		}
	}
	
	private ClassLoader classLoader;
	private List<Plugin> plugins = new ArrayList<>();
	
	public void loadPlugins(URL[] plugins) {
		if (classLoader != null) {
			throw new IllegalStateException("Plugins can only be loaded once per session!");
		}
		LogManager.getLogger().info("Loading plugins...");
		
		classLoader = new URLClassLoader(plugins, getClass().getClassLoader());
		
		// Load main class names.
		for (URL url : plugins) {
			try {
				URL jsonURL = new URL("jar:"+url.toString()+"!/plugin.json");
				JSONObject json = new JSONObject(new JSONTokener(jsonURL.openStream()));
				Plugin plugin = new Plugin(json, classLoader);
				if (plugin.initialize()) {
					this.plugins.add(plugin);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Get the class loader that plugins are loaded under.
	 */
	public ClassLoader getClassLoader() {
		return classLoader;
	}
	
	public void closePlugins() {
		for (Plugin plugin : plugins) {
			plugin.getInitializer().close();
		}
	}
}
