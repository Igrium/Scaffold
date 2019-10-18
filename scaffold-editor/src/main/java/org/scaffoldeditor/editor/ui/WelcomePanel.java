package org.scaffoldeditor.editor.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.SwingConstants;

import org.scaffoldeditor.editor.Scaffold;
import org.scaffoldeditor.scaffold.core.Project;

import java.awt.event.ActionListener;
import java.io.File;
import java.awt.event.ActionEvent;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class WelcomePanel extends JFrame {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WelcomePanel frame = new WelcomePanel();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public WelcomePanel() {
		setTitle("Scaffold Editor");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 720, 480);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		JLabel title = new JLabel("SCAFFOLD");
		title.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(title);
		
		JButton loadProjectButton = new JButton("Load Project");
		loadProjectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				loadProject();
			}
		});
		loadProjectButton.setToolTipText("Load a project from the file system.");
		panel.add(loadProjectButton);
		
		JButton btnCreateProject = new JButton("Create Project");
		btnCreateProject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				createProject();
			}
		});
		btnCreateProject.setToolTipText("Create a new project.");
		panel.add(btnCreateProject);
		
	}
	
	public void loadProject() {
		// Show file choosing dialouge
		JFileChooser fileChooser = new JFileChooser();
		int returnVal = fileChooser.showOpenDialog(this);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			
			Project project = Project.loadProject(file.getParent());
			
			if (project == null) {
				return;
			}
			System.out.println("Loaded project: "+file.getParent());
			Scaffold.launchEditor(project);
			setVisible(false);
		}
	}
	
	public void createProject() {
		// Show file choosing dialouge
		JFileChooser fileChooser = new JFileChooser();
		int returnVal = fileChooser.showSaveDialog(this);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File projectDir = fileChooser.getSelectedFile();
			
			System.out.println("Creating a project at "+projectDir);
			
			if (!projectDir.isDirectory()) {
				projectDir.mkdirs();
			}
			
			Project project = Project.init(projectDir.toString(), projectDir.getName());
			Scaffold.launchEditor(project, true);
			setVisible(false);
		}
	}

}
