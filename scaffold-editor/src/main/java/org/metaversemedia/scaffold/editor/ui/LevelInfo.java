package org.metaversemedia.scaffold.editor.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.metaversemedia.scaffold.level.Level;
import org.metaversemedia.scaffold.level.LevelData;

import javax.swing.JScrollBar;
import javax.swing.Box;
import javax.swing.JScrollPane;
import java.awt.GridLayout;
import javax.swing.JLabel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Insets;
import javax.swing.SwingConstants;
import javax.swing.JCheckBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Class that edits the level info
 * @author Sam54123
 */
public class LevelInfo extends JDialog {
	private JTextField prettyNameField;
	private JTextField difficultyField;
	protected Level level;
	private JCheckBox hardcoreCheck;

	/**
	 * Create the dialog.
	 * @param level Level to edit.
	 */
	public LevelInfo(Level level) {
		this.level = level;
		setTitle("Edit Level Settings");
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
						saveLevelInfo();
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
			JScrollPane scrollPane = new JScrollPane();
			getContentPane().add(scrollPane, BorderLayout.CENTER);
			{
				JPanel panel = new JPanel();
				scrollPane.setViewportView(panel);
				GridBagLayout gbl_panel = new GridBagLayout();
				gbl_panel.columnWidths = new int[]{0, 0, 0, 0, 0};
				gbl_panel.rowHeights = new int[] {30, 0, 0, 0};
				gbl_panel.columnWeights = new double[]{0.0, 0.0, 1.0, 1.0, Double.MIN_VALUE};
				gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
				panel.setLayout(gbl_panel);
				{
					JLabel lblPrettyName = new JLabel("Pretty Name");
					lblPrettyName.setToolTipText("The name this level shows up as in the world browser");
					GridBagConstraints gbc_lblPrettyName = new GridBagConstraints();
					gbc_lblPrettyName.anchor = GridBagConstraints.EAST;
					gbc_lblPrettyName.insets = new Insets(0, 0, 5, 5);
					gbc_lblPrettyName.gridx = 1;
					gbc_lblPrettyName.gridy = 0;
					panel.add(lblPrettyName, gbc_lblPrettyName);
				}
				{
					prettyNameField = new JTextField();
					prettyNameField.setText(level.getPrettyName());
					prettyNameField.setHorizontalAlignment(SwingConstants.LEFT);
					GridBagConstraints gbc_prettyNameField = new GridBagConstraints();
					gbc_prettyNameField.anchor = GridBagConstraints.WEST;
					gbc_prettyNameField.insets = new Insets(0, 0, 5, 5);
					gbc_prettyNameField.gridx = 2;
					gbc_prettyNameField.gridy = 0;
					panel.add(prettyNameField, gbc_prettyNameField);
					prettyNameField.setColumns(10);
				}
				{
					JLabel lblDifficulty = new JLabel("Difficulty");
					lblDifficulty.setToolTipText("The game difficulty of this level");
					GridBagConstraints gbc_lblDifficulty = new GridBagConstraints();
					gbc_lblDifficulty.insets = new Insets(0, 0, 5, 5);
					gbc_lblDifficulty.anchor = GridBagConstraints.EAST;
					gbc_lblDifficulty.gridx = 1;
					gbc_lblDifficulty.gridy = 1;
					panel.add(lblDifficulty, gbc_lblDifficulty);
				}
				{
					difficultyField = new JTextField();
					difficultyField.setText(Integer.toString(level.levelData().getData().getInt("Difficulty")));
					GridBagConstraints gbc_difficultyField = new GridBagConstraints();
					gbc_difficultyField.anchor = GridBagConstraints.WEST;
					gbc_difficultyField.insets = new Insets(0, 0, 5, 5);
					gbc_difficultyField.gridx = 2;
					gbc_difficultyField.gridy = 1;
					panel.add(difficultyField, gbc_difficultyField);
					difficultyField.setColumns(10);
				}
				{
					JLabel lblHardcore = new JLabel("Hardcore");
					lblHardcore.setToolTipText("Should this level be hardcore?");
					GridBagConstraints gbc_lblHardcore = new GridBagConstraints();
					gbc_lblHardcore.insets = new Insets(0, 0, 0, 5);
					gbc_lblHardcore.gridx = 1;
					gbc_lblHardcore.gridy = 2;
					panel.add(lblHardcore, gbc_lblHardcore);
				}
				{
					hardcoreCheck = new JCheckBox("", level.levelData().getData().getBoolean("hardcore"));
					GridBagConstraints gbc_hardcoreCheck = new GridBagConstraints();
					gbc_hardcoreCheck.anchor = GridBagConstraints.WEST;
					gbc_hardcoreCheck.insets = new Insets(0, 0, 0, 5);
					gbc_hardcoreCheck.gridx = 2;
					gbc_hardcoreCheck.gridy = 2;
					panel.add(hardcoreCheck, gbc_hardcoreCheck);
				}
			}
		}
	}
	
	/**
	 * Save level info back into level
	 */
	public void saveLevelInfo() {
		level.setPrettyName(getPrettyNameField().getText());
		LevelData levelData = level.levelData();
		
		try {
			levelData.getData().put("Difficulty", Integer.parseInt(getDifficultyField().getText()));
		} catch (NumberFormatException | NullPointerException nfe) {
			System.out.println("Difficulty must be an integer!");
		}
		
		levelData.getData().put("hardcode", getHardcoreCheck().isSelected());
		
	}

	protected JTextField getPrettyNameField() {
		return prettyNameField;
	}
	protected JTextField getDifficultyField() {
		return difficultyField;
	}
	protected JCheckBox getHardcoreCheck() {
		return hardcoreCheck;
	}
}
