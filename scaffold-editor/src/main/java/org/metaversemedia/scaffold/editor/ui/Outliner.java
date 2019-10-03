package org.metaversemedia.scaffold.editor.ui;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JLabel;
import javax.swing.JMenuItem;

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
	
	protected class PopUpMenu extends JPopupMenu {
		JMenuItem newEntity;
		
		public PopUpMenu() {
			newEntity = new JMenuItem("New Entity");
			this.add(newEntity);
		}
	}
	
	protected EditorWindow parent;
	protected PopUpMenu menu;
	
	protected List<JComponent> labels = new ArrayList<JComponent>();

	/**
	 * Create the panel.
	 * @param parent Parent editor window
	 */
	public Outliner(EditorWindow parent) {
		menu = new PopUpMenu();
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.isPopupTrigger()) { // Check for right click
					menu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
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
