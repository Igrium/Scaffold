package org.metaversemedia.scaffold.editor.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.NumberFormatter;

import org.metaversemedia.scaffold.level.entity.Entity;
import org.metaversemedia.scaffold.math.Vector;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JFormattedTextField;

/**
 * Editor window to edit an entity.
 * @author Sam54123
 */
public class EntityEditor extends JDialog {
	
	public abstract class AttributeField extends JPanel {
		public abstract String getAttributeName();
		public abstract Object getAttributeValue();
	}
	
	/**
	 * Represents a single field with an attribute
	 * @author Sam54123
	 */
	protected class StringAttributeField extends AttributeField {
		private String attributeName;
		private JTextField textField;
		
		public StringAttributeField(String attribute, String defaultValue) {
			attributeName = attribute;
			
			this.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
			JLabel label = new JLabel(attributeName+": ");
			this.add(label);
			
			textField = new JTextField(defaultValue.toString());
			this.add(textField);
		}

		@Override
		public String getAttributeName() {
			return attributeName;
		}

		@Override
		public String getAttributeValue() {
			return textField.getText();
		}

	}
	
	protected class IntAttributeField extends AttributeField {
		private String attributeName;
		private JTextField textField;
		private Integer oldValue;
		
		public IntAttributeField(String attribute, Integer defaultValue) {
			attributeName = attribute;
			oldValue = defaultValue;
			
			this.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
			JLabel label = new JLabel(attributeName+": ");
			this.add(label);
			
			textField = new JTextField(defaultValue.toString());
			this.add(textField);
		}
		@Override
		public String getAttributeName() {
			return attributeName;
		}
		@Override
		public Integer getAttributeValue() {
			try {
				return Integer.valueOf(textField.getText());
			} catch (NumberFormatException e) {
				return oldValue;
			}
		}
	}
	
	protected class FloatAttributeField extends AttributeField {
		private String attributeName;
		private JTextField textField;
		private Float oldValue;
		
		public FloatAttributeField(String attribute, Float defaultValue) {
			attributeName = attribute;
			oldValue = defaultValue;
			
			this.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
			JLabel label = new JLabel(attributeName+": ");
			this.add(label);
			
			textField = new JTextField(defaultValue.toString());
			this.add(textField);
		}
		@Override
		public String getAttributeName() {
			return attributeName;
		}
		@Override
		public Float getAttributeValue() {
			try {
				return Float.valueOf(textField.getText());
			} catch (NumberFormatException e) {
				return oldValue;
			}
			
		}
	}	
	
	private EditorWindow parent;
	private Entity entity;
	
	private final JPanel contentPanel = new JPanel();
	private JTextField nameField;
	
	private List<AttributeField> attributeFields = new ArrayList<AttributeField>();
	private JLabel typeLabel;
	private JTextField positionX;
	private JTextField positionY;
	private JTextField positionZ;

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
		setTitle("Entity Properties");
		this.parent = parent;
		
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		{
			JPanel panel = new JPanel();
			FlowLayout flowLayout = (FlowLayout) panel.getLayout();
			flowLayout.setAlignment(FlowLayout.LEFT);
			contentPanel.add(panel);
			{
				JLabel lblType = new JLabel("Type: ");
				panel.add(lblType);
			}
			{
				typeLabel = new JLabel("[type]");
				panel.add(typeLabel);
			}
		}
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel);
			panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
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
			JPanel panel = new JPanel();
			FlowLayout flowLayout = (FlowLayout) panel.getLayout();
			flowLayout.setAlignment(FlowLayout.LEFT);
			contentPanel.add(panel);
			{
				JLabel lblPosition = new JLabel("Position");
				panel.add(lblPosition);
			}
			{
				positionX = new JTextField();
				panel.add(positionX);
				positionX.setColumns(10);
			}
			{
				positionY = new JTextField();
				panel.add(positionY);
				positionY.setColumns(10);
			}
			{
				positionZ = new JTextField();
				panel.add(positionZ);
				positionZ.setColumns(10);
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
		// Remove old attribute fields
		for (AttributeField f : attributeFields) {
			contentPanel.remove(f);
		}
		attributeFields.clear();
		
		typeLabel.setText(entity.getClass().getSimpleName());
		nameField.setText(entity.getName());
		
		positionX.setText(Float.toString(entity.getPosition().X()));
		positionY.setText(Float.toString(entity.getPosition().Y()));
		positionZ.setText(Float.toString(entity.getPosition().Z()));
		
		for (String attribute : entity.getAttributes()) {
			Object attributeObj = entity.getAttribute(attribute);
			
			// Try to cast to right class
			if (attributeObj == null || String.class.isAssignableFrom(attributeObj.getClass())) {
				StringAttributeField field = new StringAttributeField(attribute, (String) attributeObj);
				attributeFields.add(field);
				contentPanel.add(field);
			} else if (Integer.class.isAssignableFrom(attributeObj.getClass())) {
				IntAttributeField field = new IntAttributeField(attribute, (Integer) attributeObj);
				attributeFields.add(field);
				contentPanel.add(field);
			} else if (Float.class.isAssignableFrom(attributeObj.getClass())) {
				FloatAttributeField field = new FloatAttributeField(attribute, (Float) attributeObj);
				attributeFields.add(field);
				contentPanel.add(field);
			} else if (Double.class.isAssignableFrom(attributeObj.getClass())) {
				FloatAttributeField field = new FloatAttributeField(attribute, ((Double) attributeObj).floatValue());
				attributeFields.add(field);
				contentPanel.add(field);
			}
		}
		
		revalidate();
		repaint();
	}
	
	/**
	 * Save info back into entity
	 */
	public void save() {
		// Save position
		try {
			entity.setPosition(new Vector(
					Float.valueOf(positionX.getText()),
					Float.valueOf(positionY.getText()),
					Float.valueOf(positionZ.getText())
					));
			
		} catch (NumberFormatException e) {
			System.out.println("Position must be a series of 3 floats! Not setting!");
		}
		
		entity.setName(nameField.getText());
		for (AttributeField f : attributeFields) {
			entity.setAttribute(f.getAttributeName(), f.getAttributeValue());
		}
		
		parent.getOutliner().reload();
	}

	protected JLabel getTypeLabel() {
		return typeLabel;
	}
}
