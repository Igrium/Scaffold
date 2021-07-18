package org.scaffoldeditor.cmd;

import java.io.PrintStream;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;

public interface ScaffoldCommandSource {
	
	public static enum AnsiCode {
		
		RESET ("\u001B[0m"),
		BLACK ("\u001B[30m"),
		RED ("\u001B[31m"),
		GREEN ("\u001B[32m"),
		YELLOW ("\u001B[33m"),
		BLUE ("\u001B[34m"),
		CYAN ("\u001B[36m"),
		WHITE("\u001B[37m")
		;
		
		private final String code;
		AnsiCode(String code) {
			this.code = code;
		}
		
		public String getCode() {
			return code;
		}
	}
	
	/**
	 * Get the print stream to write console output to.
	 */
	public PrintStream getOut();
	
	/**
	 * Get the context of this terminal.
	 */
	public ScaffoldTerminalContext getContext();
	
	public void exit();
	
	/**
	 * Print a message to the print stream in a cerian color.
	 * @param color Ansi color to print in.
	 * @param message Message to print.
	 */
	default void printColor(Color color, Object message) {
		PrintStream out = getOut();
		out.println(Ansi.ansi().fg(color).a(message).reset());
	}
	
	/**
	 * Print a message to the print stream in red.
	 * Shortcut for {@link #printColor(Color, Object)}.
	 * @param message Message to print.
	 */
	default void printError(Object message) {
		printColor(Color.RED, message);
	}
}
