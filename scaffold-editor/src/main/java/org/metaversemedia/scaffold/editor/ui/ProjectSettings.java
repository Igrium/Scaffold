package org.metaversemedia.scaffold.editor.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;

import org.metaversemedia.scaffold.core.GameInfo;
import org.metaversemedia.scaffold.core.Project;

import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ProjectSettings extends JDialog {
	private JTextField titleTextField;
	
	/**
	 * Project to edit
	 */
	protected Project project;


	/**
	 * Create the dialog.
	 * @param project Project to edit.
	 */
	public ProjectSettings(Project project) {
		this.project = project;
		setTitle("Project Settings");
		setBounds(100, 100, 640, 480);
		getContentPane().setLayout(new BorderLayout());
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						updateProject();
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
		{
			JPanel panel = new JPanel();
			getContentPane().add(panel, BorderLayout.CENTER);
			GridBagLayout gbl_panel = new GridBagLayout();
			gbl_panel.columnWidths = new int[]{0, 0, 0, 0, 0};
			gbl_panel.rowHeights = new int[]{0, 0, 0};
			gbl_panel.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
			gbl_panel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
			panel.setLayout(gbl_panel);
			{
				JLabel titleText = new JLabel("Project Title");
				GridBagConstraints gbc_titleText = new GridBagConstraints();
				gbc_titleText.insets = new Insets(0, 0, 0, 5);
				gbc_titleText.gridx = 1;
				gbc_titleText.gridy = 1;
				panel.add(titleText, gbc_titleText);
			}
			{
				titleTextField = new JTextField();
				titleTextField.setText(project.gameInfo().getTitle()); 
				GridBagConstraints gbc_titleTextField = new GridBagConstraints();
				gbc_titleTextField.anchor = GridBagConstraints.WEST;
				gbc_titleTextField.gridx = 3;
				gbc_titleTextField.gridy = 1;
				panel.add(titleTextField, gbc_titleTextField);
				titleTextField.setColumns(10);
			}
		}
	}
	
	/**
	 * Updates the project's gameinfo.json
	 */
	protected void updateProject() {
		GameInfo gameinfo = project.gameInfo();
		gameinfo.setTitle(getTitleTextField().getText());
		
		gameinfo.saveJSON(project.assetManager().getAbsolutePath("gameinfo.json"));
		System.out.println("Saved project settings.");
		
	}
	protected JTextField getTitleTextField() {
		return titleTextField;
	}
}
