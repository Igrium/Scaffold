package org.metaversemedia.scaffold.editor.ui;

import javax.swing.JPanel;

import javax.swing.JLabel;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JComponent;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the entity outliner
 * @author Sam54123
 */
public class Outliner extends JPanel {
	
	protected EditorWindow parent;
	
	protected List<JComponent> labels = new ArrayList<JComponent>();

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
		// Remove previous labels from list
		for (JComponent l : labels) {
			remove(l);
		}
		
		labels.clear();
		
		for (String key : parent.getLevel().getEntities().keySet()){
			JLabel label = new JLabel(key);
			label.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent event) {
					// Check for double click
					if (event.getClickCount() == 2) {
						parent.showEntityEditor(key);
					}
				}
			});
			
			labels.add(label);
			this.add(label);
		}
		revalidate();
		repaint();
	}

}
