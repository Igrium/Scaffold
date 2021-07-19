package org.scaffoldeditor.cmd;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.fusesource.jansi.AnsiConsole;
import org.scaffoldeditor.cmd.commands.CompileCommand;
import org.scaffoldeditor.cmd.commands.EntityCommand;
import org.scaffoldeditor.cmd.commands.ExitCommand;
import org.scaffoldeditor.cmd.commands.HelpCommand;
import org.scaffoldeditor.cmd.commands.LevelCommand;
import org.scaffoldeditor.cmd.commands.ProjectCommand;
import org.scaffoldeditor.scaffold.core.Constants;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

/**
 * Allows uses to work with Scaffold from a command line.
 * @author Igrium
 *
 */
public class Terminal implements ScaffoldCommandSource {
	
	/**
	 * The brigadier command dispatcher used for executing commands.
	 */
	public static final CommandDispatcher<ScaffoldCommandSource> dispatcher = new CommandDispatcher<>();
	
	/**
	 * The print stream to write console output to.
	 */
	public final PrintStream out;
	private List<String> history = new ArrayList<>();
	private int index = 0;
	private ScaffoldTerminalContext context = new ScaffoldTerminalContext();
	public boolean debugMode = false;
	public boolean shouldExit = false;
	
	/**
	 * Construct a terminal.
	 * @param out The print stream to write console output to.
	 */
	public Terminal(PrintStream out) {
		this.out = out;
	}

	@Override
	public PrintStream getOut() {
		return out;
	}
	

	@Override
	public ScaffoldTerminalContext getContext() {
		return context;
	}
	
	@Override
	public void exit() {
		shouldExit = true;
	};
	
	/**
	 * Parse and execute a command.
	 * 
	 * @param command Command to execute.
	 * @return Numeric result from the command executed. {@code 0} if the command
	 *         failed.
	 */
	public int execute(String command) {
		history.subList(index, history.size()).clear();
		history.add(command);
		index = history.size();
		return executeImpl(command);
	}
	
	private int executeImpl(String command) {
		try {
			return dispatcher.execute(command, this);
		} catch (CommandSyntaxException | RuntimeException e) {
			if (debugMode) {
				e.printStackTrace(out);
			} else {
				printError(e.getLocalizedMessage());
			}
			return 0;
		}
		
	}
	
	private static class TerminalEntrypoint implements AutoCloseable {
		
		Terminal terminal;
		Scanner scanner;
		
		public TerminalEntrypoint(Terminal terminal) {
			this.terminal = terminal;
			this.scanner = new Scanner(System.in);
		}
		
		public void start() {
			System.out.println("Scaffold v"+Constants.VERSION);
			System.out.println("Type 'help' for a list of commands.");
			
			while (!Thread.interrupted() && !terminal.shouldExit) {
				System.out.print("> ");
				terminal.execute(scanner.nextLine());
			}		
		}

		@Override
		public void close() throws Exception {
			scanner.close();
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		AnsiConsole.systemInstall();
		Terminal terminal = new Terminal(System.out);
		
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-c")) {
				try {
					terminal.execute(args[i+1]);
				} catch (ArrayIndexOutOfBoundsException e) {
					terminal.printError("Improper arguements for flag '-c'");
					return;
				}
			} else if (args[i].equals("--debug")) {
				terminal.debugMode = true;
			}
		}
		
		if (terminal.shouldExit) {
			return;
		}
		
		TerminalEntrypoint entry = new TerminalEntrypoint(terminal);
		entry.start();
		entry.close();
		
	}
	
	/**
	 * Register all the default commands. These are the only commands that can be
	 * run before a project is loaded.
	 */
	public static void registerDefaults() {
		ProjectCommand.register(dispatcher);
		HelpCommand.register(dispatcher);
		ExitCommand.register(dispatcher);
		EntityCommand.register(dispatcher);
		LevelCommand.register(dispatcher);
		CompileCommand.register(dispatcher);
	}
	
	static {
		registerDefaults();
	}
}
