package org.scaffoldeditor.editor.ui;

import java.awt.BorderLayout;
import java.io.File;
import java.nio.file.Path;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.FilenameUtils;
import org.scaffoldeditor.editor.Scaffold;
import org.scaffoldeditor.editor.editor3d.AppPanel;
import org.scaffoldeditor.editor.editor3d.EditorApp;
import org.scaffoldeditor.editor.ui.ClassBrowser.ClassSelectListener;
import org.scaffoldeditor.scaffold.core.Project;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.math.Vector;

import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.EtchedBorder;

@SuppressWarnings("serial")
public class EditorWindow extends JFrame {

	private JPanel contentPane;
	private JFileChooser fileChooser;

	
	/* Current level */
	private Level level;
	
	/* File where level is stored */
	private File levelFile;
	
	/* Does this level have unsaved changes? */
	private boolean unsavedChanges;
	
	private JMenuItem mntmSave;
	private JMenuItem mntmSaveAs;
	private JMenuItem mntmNew;
	private JMenu editMenu;
	private JMenuItem projectSettingsButton;
	private JMenuItem levelInfoButton;
	private JMenuItem compileButton;
	private Outliner outliner;
	private Action saveAction;
	
	private AppPanel appPanel;
	
	private EntityEditor entityEditor;
	private ClassBrowser<Entity> entityBrowser;
	
	/* The title this window has before the * if unsaved */
	private String desiredTitle = "";
	private JMenuItem mntmNewEntity;
	
	
	/**
	 * Get the loaded project.
	 * @return Project
	 */
	public Project getProject() {
		return Scaffold.project;
	}
	
	/**
	 * Get the loaded level.
	 * @return Level
	 */
	public Level getLevel() {
		return level;
	}
	
	/**
	 * Update window title.
	 */
	private void updateTitle() {
		if (hasUnsavedChanges()) {
			this.setTitle(desiredTitle+"*");
		} else {
			this.setTitle(desiredTitle);
		}
	}
	
	/**
	 * Set the file this level references
	 * @param file New file
	 */
	protected void setLevelFile(File file) {
		levelFile = file;
		desiredTitle = "Scaffold Editor: "+file.toString();
		updateTitle();
	}
	
	/**
	 * Does this level have unsaved changes?
	 * @return Unsaved changes
	 */
	public boolean hasUnsavedChanges() {
		return unsavedChanges;
	}
	
	/**
	 * Set whether this level has unsaved changes.
	 * @param unsavedChanges Now has unsaved changes?
	 */
	protected void setHasUnsavedChanges(boolean unsavedChanges) {
		this.unsavedChanges = unsavedChanges;
		updateTitle();
	}
	
	/**
	 * Mark this editor as unsaved
	 */
	public void markUnsaved() {
		setHasUnsavedChanges(true);
	}

	/**
	 * Create the frame.
	 */
	public EditorWindow(Project project) {
		Scaffold.project = project;
		
		fileChooser = new JFileChooser(project.getProjectFolder().resolve("maps").toFile());
		FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("Minecraft Level Files", "mclevel");
		fileChooser.setFileFilter(fileFilter);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1280, 720);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		
		saveAction = new AbstractAction("Save") {

			@Override
			public void actionPerformed(ActionEvent e) {
				save();
				
			}
		};

		mntmSave = new JMenuItem("Save");
		mntmSave.setEnabled(false);
		mntmSave.setAction(saveAction);
		
		JMenuItem mntmOpenLevel = new JMenuItem("Open");
		mntmOpenLevel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadLevelDialog();
			}
		});
		
		mntmNew = new JMenuItem("New");
		mntmNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newLevelDialog();
			}
		});
		fileMenu.add(mntmNew);
		fileMenu.add(mntmOpenLevel);
		fileMenu.add(mntmSave);
		
		mntmSaveAs = new JMenuItem("Save As...");
		mntmSaveAs.setEnabled(false);
		mntmSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveAsDialog();
			}
		});
		fileMenu.add(mntmSaveAs);
		
		compileButton = new JMenuItem("Compile");
		compileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				compile();
			}
		});
		compileButton.setEnabled(false);
		fileMenu.add(compileButton);
		
		editMenu = new JMenu("Edit");
		menuBar.add(editMenu);
		
		projectSettingsButton = new JMenuItem("Project Settings");
		projectSettingsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showProjectSettings();
			}
		});
		
		levelInfoButton = new JMenuItem("Level Info");
		levelInfoButton.setEnabled(false);
		levelInfoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showLevelInfo();
			}
		});
		editMenu.add(levelInfoButton);
		editMenu.add(projectSettingsButton);
		
		mntmNewEntity = new JMenuItem("New Entity");
		mntmNewEntity.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newEntityDialog();
			}
		});
		editMenu.add(mntmNewEntity);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		appPanel = new AppPanel(this);
		contentPane.add(appPanel, BorderLayout.CENTER);
		
		outliner = new Outliner(this);
		outliner.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		contentPane.add(outliner, BorderLayout.EAST);

		entityEditor = new EntityEditor(this);
		
		entityBrowser = new ClassBrowser<Entity>(Entity.class);
		entityBrowser.setClassSelectListener(new ClassSelectListener<Entity>() {

			@Override
			public void classSelected(Class<? extends Entity> selectedClass) {
				newEntity(selectedClass);
			}
			
		});
		
	}
	
	/**
	 * Open the save as dialog
	 */
	public void saveAsDialog() {
		int returnVal = fileChooser.showSaveDialog(this);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File levelFile = fileChooser.getSelectedFile();
			saveAs(levelFile);
		}
	}
	
	/**
	 * Save the loaded level to a file.
	 * @param file File to save to.
	 * @return Success.
	 */
	public boolean saveAs(File file) {
		if (level == null) {
			return false;
		}
		
		boolean success = level.saveFile(file);
		
		if (success) {
			setHasUnsavedChanges(false);
			setLevelFile(file);
			System.out.println("Saved level to: "+file);
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
	
	/**
	 * Open the Open dialog
	 */
	public void loadLevelDialog() {
		int returnVal = fileChooser.showOpenDialog(this);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File levelFile = fileChooser.getSelectedFile();
			loadLevel(levelFile.toPath());
		}
	}
	
	/**
	 * Show the new entity dialog.
	 */
	public void newEntityDialog() {
		entityBrowser.setVisible(true);
	}
	
	/**
	 * Create a new entity
	 * @param type Type of entity to create.
	 */
	public void newEntity(Class<? extends Entity> type) {
		Entity entity = level.newEntity(type, type.getSimpleName(), new Vector(0,0,0));
		level.renameEntity(entity.getName(), entity.getDefaultName());
		markUnsaved();
		reload();
		showEntityEditor(entity);
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
		
		setLevelFile(file.toFile());
		this.level = newLevel;
		
		System.out.println("Loaded level: "+file);
		
		reload();
		
		return true;
	}
	
	public void reload() {
		getMntmSave().setEnabled(true);
		getMntmSaveAs().setEnabled(true);
		getLevelInfoButton().setEnabled(true);
		getCompileButton().setEnabled(true);
		
		getOutliner().reload();
		appPanel.reload();
	}
	
	/**
	 * Show the new level dialog
	 */
	public void newLevelDialog() {
		int returnVal = fileChooser.showSaveDialog(this);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File levelFile = fileChooser.getSelectedFile();
			if (FilenameUtils.getExtension(levelFile.getName()).equalsIgnoreCase(".mclevel")) {
				levelFile = new File(levelFile.toString()+".mclevel"); // Enforce file extension
			}
			newLevel(levelFile);
			showLevelInfo();
		}
	}
	
	/**
	 * Create a new level
	 * @param file File to save as
	 * @return New level
	 */
	public Level newLevel(File file) {
		Level newLevel = new Level(getProject());
		
		if (newLevel == null || !newLevel.saveFile(file)) {
			return null;
		}
		System.out.println("Saved new level to: "+file);
		
		loadLevel(file.toPath());
		
		return null;
	}
	
	/**
	 * Show the edit entity dialog
	 * @param entity Entity to edit
	 */
	public void showEntityEditor(Entity entity) {
		// Check that entity is part of level
		if (!level.getEntities().containsKey(entity.getName())) {
			return;
		}
		
		entityEditor.setEntity(entity);
		entityEditor.setVisible(true);
	}
	
	/**
	 * Show edit entity dialog by entity name.
	 * @param entityName Entity name.
	 */
	public void showEntityEditor(String entityName) {
		if (!level.getEntities().containsKey(entityName)) {return;}
		showEntityEditor(level.getEntity(entityName));
	}
	
	/**
	 * Compile the current level
	 */
	public void compile() {
		level.compile(getProject().assetManager().getAbsolutePath("game/saves/world"));
	}
	
	public void showProjectSettings() {
		ProjectSettings projectSettings = new ProjectSettings(getProject());
		projectSettings.setVisible(true);
	}
	
	public void showLevelInfo() {
		LevelInfo levelInfo = new LevelInfo(this);
		levelInfo.setVisible(true);
	}

	protected JMenuItem getMntmSave() {
		return mntmSave;
	}
	protected JMenuItem getMntmSaveAs() {
		return mntmSaveAs;
	}
	protected JMenuItem getLevelInfoButton() {
		return levelInfoButton;
	}
	public JMenuItem getCompileButton() {
		return compileButton;
	}
	protected Outliner getOutliner() {
		return outliner;
	}
	
	/**
	 * Get the 3d engine app displayed in the editor.
	 * @return 3d app.
	 */
	protected EditorApp get3dApp() {
		return appPanel.getApp();
	}
}
