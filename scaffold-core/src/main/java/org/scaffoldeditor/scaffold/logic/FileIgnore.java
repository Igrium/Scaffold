package org.scaffoldeditor.scaffold.logic;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents an ignore file (ex: dataignore.txt)
 * @author Igrium
 */
public abstract class FileIgnore {
	/**
	 * List of all files to ignore.
	 */
	public final List<Path> ignoredFiles = new ArrayList<Path>();
	
	/**
	 * List of strings that cause any files containing them to be ignored.
	 */
	public final List<String> ignoredStrings = new ArrayList<String>();
	
	/**
	 * Path to ignore file.
	 */
	protected Path filePath;
	
	/**
	 * Create a new FileIngnore with a path
	 * @param path Path to ignore file.
	 */
	public FileIgnore(Path path) {
		this.filePath = path;
		try {
			load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		addDefaults();
	}
	
	/**
	 * Filter to determine whether a file can be copied
	 * @author Igrium
	 *
	 */
	public class Filter implements FileFilter {

		@Override
		public boolean accept(File file) {
			// Name matching requires relative path
			Path relativePath = filePath.getParent().relativize(file.toPath());
			
			// Check for ignored strings
			String pathString = relativePath.toString();
			for (String str : ignoredStrings) {
				if (pathString.contains(str)) {
					return false;
				}
			}
			
			
			for (Path path : ignoredFiles) {
				if (stringStartsWith(pathString, path.toString())) {
					return false;
				}
			}
			
			return true;
		}
		

		
		/* Gets whether string 1 starts with string 2 */
		private boolean stringStartsWith(String str1, String str2) {
			char[] chars1 = str1.toCharArray();
			char[] chars2 = str2.toCharArray();
			
			if (chars1.length < chars2.length) {
				return false;
			}	
			
			for (int i = 0; i < chars2.length; i++) {
				if (chars2[i] != chars1[i])  {
					return false;
				}
			}
			
			return true;
		}
		
	}
	
	/**
	 * Load an ignore file
	 * @throws IOException
	 */
	protected void load() throws IOException {
//		System.out.println("Loading dataignore: "+file);
		
		// If file doesn't exist, return empty file
		if (!Files.exists(filePath)) {
			return;
		}
						
		// Read all lines
		List<String> lines = Files.readAllLines(filePath);
		

		for (String line : lines) {
			ingestLine(line);
		}
		

	}
	
	/* Read and ingest a line in the dataignore */
	protected void ingestLine(String line) {
		if (line.length() == 0) {
			return;
		}
		
		
		// Remove trailing whitespace
		line = line.trim();
		
		// Remove comments
		if (line.contains("#")) {
			ingestLine(line.substring(line.indexOf('#'+1)));
			return;
		}
				
		int firstChar = firstNonWhitespace(line);
		
		
		// Ignored strings
		if (line.charAt(firstChar) == '*') {
			ignoredStrings.add(line.substring(firstChar+1));
			return;
		}
		
		// Add path (relative to data folder)
		ignoredFiles.add(Paths.get(line));
	}
	
	/* Get the index of the first non-whitespace character in a string */
	protected int firstNonWhitespace(String input) {
		char[] characters = input.toCharArray();
		
		for (int i = 0; i < characters.length; i++) {
			if (!Character.isWhitespace(characters[i])) {
				return i;
			}
		}
		
		return 0;
	}
	
	/**
	 * Called by constructor to set default values
	 */
	protected abstract void addDefaults();
}
