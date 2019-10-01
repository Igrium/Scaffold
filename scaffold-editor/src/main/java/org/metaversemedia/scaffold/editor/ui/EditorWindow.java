package org.metaversemedia.scaffold.editor.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.metaversemedia.scaffold.core.Project;
import org.metaversemedia.scaffold.level.Level;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class EditorWindow extends JFrame {

	private JPanel contentPane;
	private JFileChooser fileChooser;

	
	/* Current project */
	private Project project;
	
	/* Current level */
	private Level level;
	
	/* File where level is stored */
	private File levelFile;
	
	/* Does this level have unsaved changes? */
	private boolean unsavedChanges;
	
	/**
	 * Get the loaded project.
	 * @return Project
	 */
	public Project getProject() {
		return project;
	}
	
	/**
	 * Get the loaded level.
	 * @return Level
	 */
	public Level getLevel() {
		return level;
	}
	
	/**
	 * Does this level have unsaved changes?
	 * @return Unsaved changes
	 */
	public boolean hasUnsavedChanges() {
		return unsavedChanges;
	}

	/**
	 * Create the frame.
	 */
	public EditorWindow(Project project) {
		this.project = project;
		
		fileChooser = new JFileChooser(project.getProjectFolder().resolve("maps").toFile());
		FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("Minecraft Level Files", "mclevel");
		fileChooser.setFileFilter(fileFilter);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1280, 720);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		
		JButton btnOpenLevel = new JButton("Open Level");
		btnOpenLevel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				loadLevelDialouge();
			}
		});
		fileMenu.add(btnOpenLevel);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
	}
	
	/**
	 * Save the loaded level to a file.
	 * @param file File to save to.
	 * @return Success.
	 */
	public boolean saveAs(File file) {
		boolean success = level.saveFile(file);
		
		if (success) {
			unsavedChanges = false;
		}
		
		return success;
	}
	
	/**
	 * Save the loaded level out to file.
	 * @return Success
	 */
	public boolean save() {
		return saveAs(levelFile);
	}
	
	public void loadLevelDialouge() {
		int returnVal = fileChooser.showOpenDialog(this);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File levelFile = fileChooser.getSelectedFile();
			loadLevel(levelFile.toPath());
		}
	}
	
	/**
	 * Load a level from a file.
	 * @param level Level to load
	 * @return success
	 */
	public boolean loadLevel(Path file) {
		Level newLevel = Level.loadFile(getProject(), file);
		
		if (newLevel == null) {
			return false;
		}
		
		this.levelFile = file.toFile();
		this.level = newLevel;
		
		System.out.println("Loaded level: "+file);
		
		return true;
	}

}
