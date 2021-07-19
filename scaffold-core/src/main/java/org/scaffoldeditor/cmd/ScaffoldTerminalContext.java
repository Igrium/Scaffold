package org.scaffoldeditor.cmd;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.scaffoldeditor.scaffold.core.Project;
import org.scaffoldeditor.scaffold.level.Level;

public class ScaffoldTerminalContext {
	private Project project;
	private Level level;
	private File levelFile;
	
	public Project getProject() {
		return project;
	}
	
	public void setProject(Project project) {
		if (project == this.project) {
			return;
		}
		
		level = null;
		
		this.project = project;
	}
	
	public void loadProject(String folder) throws IOException {
		setProject(Project.loadProject(folder));
	}
	
	public Level getLevel() {
		return level;
	}
	
	public void setLevel(Level level) {
		if (level == null) {
			this.level = null;
			this.levelFile = null;
		}
		
		if (project == null) {
			throw new IllegalStateException("Cannot open a level without a project loaded!");
		}
		if (!project.equals(level.getProject())) {
			throw new IllegalStateException("Level's project does not equal loaded project!");
		}
		
		this.level = level;
		this.levelFile = null;
	}
	
	public void openLevel(File levelFile) throws IOException {
		if (project == null) {
			throw new IllegalStateException("Cannot open a level without a project loaded!");
		}
		
		Level level = Level.loadFile(project, levelFile);
		setLevel(level);
		setLevelFile(levelFile);

	}

	public File getLevelFile() {
		return levelFile;
	}

	public void setLevelFile(File levelFile) {
		this.levelFile = levelFile;
		level.setName(FilenameUtils.getBaseName(levelFile.getName()));
	}
	
	public void save() throws IOException {
		if (levelFile == null) {
			throw new IllegalStateException("Level has no associated level file!");
		}
		save(levelFile);
	}
	
	public void save(File levelFile) throws IOException {
		if (level == null) {
			throw new IllegalStateException("No level loaded!");
		}
		
		setLevelFile(levelFile);
		level.saveFile(levelFile);
	}
}
