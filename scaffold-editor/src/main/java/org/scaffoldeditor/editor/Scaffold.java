package org.scaffoldeditor.editor;

import java.awt.EventQueue;
import java.io.File;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.scaffoldeditor.editor.ui.EditorWindow;
import org.scaffoldeditor.editor.ui.WelcomePanel;
import org.scaffoldeditor.scaffold.core.Project;

/**
 * The main class of scaffold. Contains static session variables and launches the editor.
 * @author Sam54123
 */
public class Scaffold {
	
	public static Project project;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		// Get given path
		String projectFolder = null;
		
		if (args.length >= 1) {
			projectFolder = args[0];
		}
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		
		if (projectFolder != null && new File(projectFolder).isDirectory()) {
			Project project = Project.loadProject(projectFolder);
			launchEditor(project);
		} else {
			WelcomePanel welcomePanel = new WelcomePanel();
			welcomePanel.setVisible(true);
		}
		
	}
	
	/**
	 * Launch the level editor.
	 * @param project Project to launch into.
	 * @param showProjectProperties Should show project properties on launch?
	 */
	public static void launchEditor(Project project, boolean showProjectProperties) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					EditorWindow window = new EditorWindow(project);
					window.setVisible(true);
					
					if (showProjectProperties) {
						window.showProjectSettings();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Launch the level editor.
	 * @param project Project to launch into.
	 */
	public static void launchEditor(Project project) {
		launchEditor(project, false);
	}
	
}
