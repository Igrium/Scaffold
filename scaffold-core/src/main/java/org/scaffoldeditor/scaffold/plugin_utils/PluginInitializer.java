package org.scaffoldeditor.scaffold.plugin_utils;

public interface PluginInitializer {
	
	/**
	 * Called when the Scaffold project is initialized.
	 */
	public void initialize();
	
	/**
	 * Called when the project is closing and we need to clean up.
	 */
	default void close() {};
}
