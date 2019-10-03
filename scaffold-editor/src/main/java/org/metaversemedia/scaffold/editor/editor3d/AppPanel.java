package org.metaversemedia.scaffold.editor.editor3d;

import java.awt.Dimension;

import javax.swing.JPanel;

import org.metaversemedia.scaffold.editor.ui.EditorWindow;

import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;

/**
 * Represents the 3d editor app in a panel.
 * @author Sam54123
 */
public class AppPanel extends JPanel {

	EditorWindow parent;
	EditorApp app;
	
	/**
	 * Create the panel.
	 */
	public AppPanel(EditorWindow parent) {
		this.parent = parent;
	}
	
	/**
	 * Starts the editor using the parent's level.
	 * Restarts if already active.
	 */
	public void start() {
		if (app != null) {
			app.stop();
		}
		
		AppSettings settings = new AppSettings(true);
		settings.setWidth(getWidth());
		settings.setHeight(getHeight());
		
		app = new EditorApp(parent);
		app.setSettings(settings);
		app.createCanvas();	
		
		JmeCanvasContext ctx = (JmeCanvasContext) app.getContext();
		ctx.setSystemListener(app);
		Dimension dim = new Dimension(getWidth(), getHeight());
		ctx.getCanvas().setPreferredSize(dim);
		
		this.add(ctx.getCanvas());
		
		app.startCanvas();
	}

}
