package org.metaversemedia.scaffold.logic;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the dataignore.txt file used when compiling datapack
 * @author Sam54123
 *
 */
public final class DataIgnore {
	/**
	 * List of all files to ignore
	 */
	public final List<Path> ignoredFiles = new ArrayList<Path>();
	
	/**
	 * List of strings that cause any files containing them to be ignored
	 */
	public final List<String> ignoredStrings = new ArrayList<String>();
	
	private Path dataPath;
	
	/**
	 * Create a new DataIgnore.
	 * @param dataPath The folder this DataIgnore is acting on.
	 */
	public DataIgnore(Path dataPath) {
		ignoredFiles.add(Paths.get("dataignore.txt"));
		ignoredFiles.add(Paths.get("compile.json"));
		
		this.dataPath = dataPath;
	}
	
	
	/**
	 * Filter to determine whether a file can be copied
	 * @author Sam54123
	 *
	 */
	public class Filter implements FileFilter {

		@Override
		public boolean accept(File file) {
			// Name matching requires relative path
			Path relativePath = dataPath.relativize(file.toPath());
			
//			System.out.println(relativePath);
			
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
	 * Load from dataignore file
	 * @param file File to load
	 * @return Loaded object
	 * @throws IOException 
	 */
	public static DataIgnore load(Path file) throws IOException {
		
		DataIgnore ignoreFile = new DataIgnore(file.getParent());
		
//		System.out.println("Loading dataignore: "+file);
		
		// If file doesn't exist, return empty file
		if (!Files.exists(file)) {
			return ignoreFile;
		}
						
		// Read all lines
		List<String> lines = Files.readAllLines(file);
		

		for (String line : lines) {
			ignoreFile.ingestLine(line);
		}
		
//		System.out.println(ignoreFile.ignoredStrings);
		return ignoreFile;
		
	}
	
	/* Read and ingest a line in the dataignore */
	private void ingestLine(String line) {
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
	private int firstNonWhitespace(String input) {
		char[] characters = input.toCharArray();
		
		for (int i = 0; i < characters.length; i++) {
			if (!Character.isWhitespace(characters[i])) {
				return i;
			}
		}
		
		return 0;
	}
}
