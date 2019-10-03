package org.metaversemedia.scaffold.editor.editor3d;

import org.metaversemedia.scaffold.editor.ui.EditorWindow;

import com.jme3.app.SimpleApplication;

/**
 * The 3d rendered part of the editor.
 * @author Sam54123
 */
public class EditorApp extends SimpleApplication {
	
	private EditorWindow parent;
	
	/**
	 * Get this app's parent window.
	 * @return Parent.
	 */
	public EditorWindow getParent() {
		return parent;
	}
	
	/**
	 * Create an editor app.
	 * @param parent Parent window.
	 */
	public EditorApp(EditorWindow parent) {
		this.parent = parent;
	}
	
	
	
	@Override
	public void simpleInitApp() {
		
	}

}
