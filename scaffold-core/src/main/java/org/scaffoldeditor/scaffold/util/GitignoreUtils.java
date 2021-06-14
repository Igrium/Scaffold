package org.scaffoldeditor.scaffold.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.scaffoldeditor.nbt.util.Pair;
import org.scaffoldeditor.nbt.util.SingleTypePair;

/**
 * <p>
 * Scaffold's <code>.dataignore</code> follow the same format as traditional
 * <code>.gitignore</code> files. This is a utility class to parse them.
 * </p>
 * <p>
 * Derived from <a
 * href=https://github.com/codemix/gitignore-parser>https://github.com/codemix/gitignore-parser</a>
 */
public final class GitignoreUtils {
	private GitignoreUtils() {}
	
	public static class Gitignore {
		public final SingleTypePair<Pattern> positives;
		public final SingleTypePair<Pattern> negatives;
		
		public Gitignore(Pair<SingleTypePair<Pattern>, SingleTypePair<Pattern>> parsed) {
			this.positives = parsed.getFirst();
			this.negatives = parsed.getSecond();
		}

		public boolean accepts(String input) {
			if (input.charAt(0) == '/') input = input.substring(1);
			return negatives.getFirst().matcher(input).find() || !positives.getFirst().matcher(input).find();
		}
		
		public boolean denies(String input) {
			return !accepts(input);
		}
		
		public boolean maybe(String input) {
			if (input.charAt(0) == '/' ) input = input.substring(1);
			return negatives.getSecond().matcher(input).find() || !positives.getSecond().matcher(input).find();
		}
	}

	/**
	 * Compile the given <code>.gitignore</code> content and return an object that
	 * allows you to test against it.
	 * 
	 * @param content The content to compile.
	 * @return The generated Gitignore.
	 */
	public static Gitignore load(String content) {
		return new Gitignore(parse(content));
	}
	
	/**
	 * Parse the given <code>.gitignore</code> and return a pair of two pairs -
	 * positives and negatives. Each of these two pairs in turn contains two
	 * regex patterns, one strict and one for 'maybe'.
	 * 
	 * @param content The content to parse.
	 * @return The parsed positive and negitive definitions.
	 */
	public static SingleTypePair<SingleTypePair<Pattern>> parse(String content) {
		List<SingleTypePair<Pattern>> pats = Arrays.asList(content.split(System.lineSeparator())).stream()
				.map(line -> {
					return line.trim();
				})
				.filter(line -> {
					return line.length() > 0 && line.charAt(0) != '#';
				})
				.reduce(new SingleTypePair<>(new ArrayList<String>(), new ArrayList<String>()),
						(lists, line) -> {
							boolean isNegative = line.charAt(0) == '!';
							if (isNegative) line = line.substring(1);
							if (line.charAt(0) == '/') line = line.substring(1);
							
							if (isNegative) lists.getSecond().add(line);
							else lists.getFirst().add(line);
							
							return lists;
						},
						(lists1, lists2) -> {
							lists1.getFirst().addAll(lists2.getFirst());
							lists1.getSecond().addAll(lists2.getSecond());
							return lists1;
						}
				).stream().map(list -> {
					SingleTypePair<List<Pattern>> patterns = new SingleTypePair<>(new ArrayList<Pattern>(),new ArrayList<Pattern>());

					for (String in : list) {
						Pair<Pattern, Pattern> prepaired = prepairRegexes(in);
						patterns.getFirst().add(prepaired.getFirst());
						patterns.getSecond().add(prepaired.getSecond());
					}

					return patterns;
				}).map(item -> {
					Pattern item1 = item.getFirst().size() > 0 ? Pattern.compile(combineRegex(item.getFirst())) : Pattern.compile("$^");
					Pattern item2 = item.getSecond().size() > 0 ? Pattern.compile(combineRegex(item.getSecond())) : Pattern.compile("$^");
					return new SingleTypePair<>(item1, item2);
				}).collect(Collectors.toList());
		return new SingleTypePair<SingleTypePair<Pattern>>(pats.get(0), pats.get(1));
	}
	
	private static String combineRegex(List<Pattern> patterns) {
		StringJoiner b = new StringJoiner(")|(");
		patterns.forEach(pat -> b.add(pat.toString()));
		return "^((" + b.toString() + "))";
	}
	
	private static Pair<Pattern, Pattern> prepairRegexes(String pattern) {
		return new Pair<Pattern, Pattern>(
				prepareRegexPattern(pattern),
				preparePartialRegex(pattern)
			);

	}
	
	private static Pattern prepareRegexPattern(String pattern) {
		return Pattern.compile(escapeRegex(pattern).replace("**", "(.+)").replace("*", "([^\\/]+)"));
	}
	
	private static Pattern preparePartialRegex(String pattern) {
		String[] patternArray = pattern.split("/");
		String patt = String.join("", IntStream.range(0, patternArray.length).mapToObj(index -> {
			if (index > 0) {
				return "([\\/]?(" + prepareRegexPattern(patternArray[index]) + "\\b|$))";
			} else {
				return "(" + prepareRegexPattern(patternArray[index]) + "\\b)";
			}
		}).collect(Collectors.toList()));
		return Pattern.compile(patt);
	}

	private static String escapeRegex(String pattern) {
		// Dafaq is that string??
		return pattern.replaceAll("/[\\-\\[\\]\\/\\{\\}\\(\\)\\+\\?\\.\\\\\\^\\$\\|]/", "\\$&");
	}
}
