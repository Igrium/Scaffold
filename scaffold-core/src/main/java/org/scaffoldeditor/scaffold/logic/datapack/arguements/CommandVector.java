package org.scaffoldeditor.scaffold.logic.datapack.arguements;

public class CommandVector {
	public enum Mode { GLOBAL, RELATIVE, LOCAL }
	
	public static class ModeStrings {
		public static final String GLOBAL = "";
		public static final String RELATIVE = "~";
		public static final String LOCAL = "^";
	}
}
