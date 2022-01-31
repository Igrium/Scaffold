package org.scaffoldeditor.scaffold.entity;

/**
 * Represents a simple, arguementless function that can be called on an entity
 * from the UI.
 * 
 * @author Igrium
 */
public class Macro {
	public static class Confirmation {
		public final String header;
		public final String body;
		
		public Confirmation(String header, String body) {
			this.header = header;
			this.body = body;
		}
	}
	
	public final String name;
	public final Runnable function;
	public final Confirmation confirmation;
	
	/**
	 * Construct a macro.
	 * 
	 * @param name         Name to be displayed in the UI.
	 * @param function     Macro implementation.
	 * @param confirmation Confirmation dialog to display to the user.
	 */
	public Macro(String name, Runnable function, Confirmation confirmation) {
		this.name = name;
		this.function = function;
		this.confirmation = confirmation;
	}
	
	/**
	 * Construct a macro.
	 * 
	 * @param name     Name to be displayed in the UI.
	 * @param function Macro implementation.
	 */
	public Macro(String name, Runnable function) {
		this(name, function, null);
	}
	
	/**
	 * Execute this macro.
	 */
	public void run() {
		function.run();
	}
}
