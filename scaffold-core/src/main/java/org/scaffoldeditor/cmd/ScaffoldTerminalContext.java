package org.scaffoldeditor.cmd;

import java.io.File;
import java.io.IOException;

import org.scaffoldeditor.scaffold.core.Project;
import org.scaffoldeditor.scaffold.level.Level;

public class ScaffoldTerminalContext {
	private Project project;
	private Level level;
	
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
		if (project == null) {
			throw new IllegalStateException("Cannot open a level without a project loaded!");
		}
		if (!project.equals(level.getProject())) {
			throw new IllegalStateException("Level's project does not equal loaded project!");
		}
		
		this.level = level;
	}
	
	public void openLevel(File levelFile) {
		if (project == null) {
			throw new IllegalStateException("Cannot open a level without a project loaded!");
		}
		
		Level level = Level.loadFile(project, levelFile);
		setLevel(level);
	}
}
