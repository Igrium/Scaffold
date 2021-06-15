package org.scaffoldeditor.scaffold.logic;

import java.io.IOException;

import org.scaffoldeditor.scaffold.core.Project;

/**
 * Represents a Minecraft resourecpack.
 * @author Igrium
 */
public class Resourcepack extends AbstractPack {

	public Resourcepack(Project project) {
		super(project, "assets");
	}

	@Override
	protected void preCompile(OutputWriter writer) throws IOException {
	}

	@Override
	protected void postCompile(OutputWriter writer) throws IOException {
	}
}

