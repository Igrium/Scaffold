package org.metaversemedia.scaffold.level;

import java.nio.file.Path;

import org.metaversemedia.scaffold.core.Project;

/**
 * Represents a single level file
 * @author Sam54123
 *
 */
public class Level {
	
	/* The project this level belongs to */
	private Project project;
	
	/* Relative path to level file */
	private Path path;
	
	/* Has this level been saved? */
	private boolean isSaved = false;
	
	/**
	 * Create a new level
	 * @param project Project to create level in
	 */
	public Level(Project project) {
		this.project = project;
	}
	
	/**
	 * Get the Project the level is a part of
	 * @return Assigned project
	 */
	public Project getProject() {
		return project;
	}
	
	/**
	 * Has this level been saved?
	 * @return Is saved?
	 */
	public boolean isSaved() {
		return isSaved;
	}
	
	/**
	 * Mark the level as unsaved
	 */
	public void markUnsaved() {
		isSaved = false;
	}
	
}
