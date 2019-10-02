package org.metaversemedia.scaffold.editor.ui;

import javax.swing.JPanel;

import org.metaversemedia.scaffold.level.Level;

import javax.swing.JLabel;

import java.awt.Dimension;

import javax.swing.BoxLayout;

/**
 * Represents the entity outliner
 * @author Sam54123
 */
public class Outliner extends JPanel {
	
	protected EditorWindow parent;

	/**
	 * Create the panel.
	 * @param parent Parent editor window
	 */
	public Outliner(EditorWindow parent) {
		this.parent = parent;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		setPreferredSize(new Dimension(200, 720));

	}
	
	/**
	 * Reload the entity list
	 */
	public void reload() {
		for (String key : parent.getLevel().getEntities().keySet()){
			JLabel label = new JLabel(key);
			this.add(label);
		}
		revalidate();
		repaint();
	}

}
