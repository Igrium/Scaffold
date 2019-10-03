package org.metaversemedia.scaffold.editor.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.metaversemedia.scaffold.level.entity.Entity;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Editor window to edit an entity.
 * @author Sam54123
 */
public class EntityEditor extends JDialog {
	
	private EditorWindow parent;
	private Entity entity;
	
	private final JPanel contentPanel = new JPanel();
	private JTextField nameField;

	/**
	 * Get the entity the editor is currently editing.
	 * @return Entity
	 */
	public Entity getEntity() {
		return entity;
	}
	
	/**
	 * Set the entity the editor is currently editing.
	 * @param entity Entity to edit.
	 */
	public void setEntity(Entity entity) {
		this.entity = entity;
		reload();
	}


	/**
	 * Create the dialog.
	 */
	public EntityEditor(EditorWindow parent) {
		this.parent = parent;
		
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel);
			panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			{
				JLabel lblName = new JLabel("Name");
				panel.add(lblName);
			}
			{
				nameField = new JTextField();
				panel.add(nameField);
				nameField.setColumns(10);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						save();
						setVisible(false);
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	/**
	 * Reload the editor with the current entity object.
	 */
	public void reload() {
		nameField.setText(entity.getName());
		
		revalidate();
		repaint();
	}
	
	/**
	 * Save info back into entity
	 */
	public void save() {
		entity.setName(nameField.getText());
		parent.getOutliner().reload();
	}

}
