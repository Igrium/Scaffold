package org.metaversemedia.scaffold.logic;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;

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
	 * List of all filetypes to ignore
	 */
	public final List<String> ignoredFileTypes = new ArrayList<String>();
	
	public DataIgnore() {
		ignoredFiles.add(Paths.get("dataignore.txt"));
		ignoredFiles.add(Paths.get("compile.json"));
	}
	
	
	/**
	 * Filter to determine whether a file can be copied
	 * @author Sam54123
	 *
	 */
	public class Filter implements FileFilter {

		@Override
		public boolean accept(File file) {
			// Check file type
			if (listContainsString(ignoredFileTypes, FilenameUtils.getExtension(file.toString()))) {
				return false;
			}
			
			// Check for ignored file
			String fileStr = file.toString();
			
			for (Path path : ignoredFiles) {
				if (fileStr.contains(path.toString())) {
					return false;
				}
			}
			
			return true;
		}
		
		/* Does a string list contain a string? */
		private boolean listContainsString(List<String> list, String string) {
			for (String str : list) {
				if (str.matches(string)) {
					return true;
				}
			}
			return false;
		}
		
	}
	
	
	
	/**
	 * Load from dataignore file
	 * @param file File to load
	 * @return Loaded object
	 * @throws IOException 
	 */
	public static DataIgnore load(Path file) throws IOException {
		
		DataIgnore ignoreFile = new DataIgnore();
		
		System.out.println("Loading dataignore: "+file);
		
		// If file doesn't exist, return empty file
		if (!Files.exists(file)) {
			return ignoreFile;
		}
						
		// Read all lines
		List<String> lines = Files.readAllLines(file);
		

		for (String line : lines) {
			ignoreFile.ingestLine(line);
		}
		
		System.out.println(ignoreFile.ignoredFiles);
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
		
		
		// File types
		if (line.charAt(firstChar) == '*') {
			ignoredFileTypes.add(line.substring(firstChar+1));
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
