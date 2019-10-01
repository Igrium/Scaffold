package org.metaversemedia.scaffold.editor;

import java.awt.EventQueue;
import java.io.File;
import org.metaversemedia.scaffold.core.Project;
import org.metaversemedia.scaffold.editor.ui.EditorWindow;
import org.metaversemedia.scaffold.editor.ui.WelcomePanel;

/**
 * Class that launches the editor
 * @author Sam54123
 */
public class Launch {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		// Get given path
		String projectFolder = null;
		
		if (args.length >= 1) {
			projectFolder = args[0];
		}
		
		if (projectFolder != null && new File(projectFolder).isDirectory()) {
			Project project = Project.loadProject(projectFolder);
			launchEditor(project);
		} else {
			WelcomePanel welcomePanel = new WelcomePanel();
			welcomePanel.setVisible(true);
		}
		
	}
	
	public static void launchEditor(Project project) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					EditorWindow window = new EditorWindow(project);
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
}
