package bran.parser.abst;

import bran.exceptions.ParseException;
import bran.parser.composition.TokenType;
import bran.parser.matching.Pattern;
import bran.tree.structure.mapper.Associativity;
import bran.tree.structure.mapper.AssociativityPrecedenceLevel;
import org.intellij.lang.annotations.RegExp;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

import static bran.parser.composition.TokenType.UNKNOWN;
import static bran.parser.composition.TokenType.WHITESPACE;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

public class AbstractCompiler<T> {

	private List<Set<Pattern>> patterns;
	private String delimiter; // TODO anything but lettel next to letter???

	// TODO String input for compiler symbols/patterns/tokens? (and link with java class?)
	/*
	  tokens: Word(a,b,z,abc,bc,abc a,+abc), Op(*,+), Number(0-9), Space( )
	  patterns: Word|Number Op Word|Number
	  a *9+abc a bc
	  ^ ^^
	      ^
	      ^^^^ <
	       ^
	       ^^^
	       ^^^^^
	        ^
	        ^^
	         ^
	          ^^^^^ <
	 */
	// public static class AbstractCompilerBuilder<TT> {
	// 	public AbstractCompilerBuilder<TT>() {}
	// }

	public AbstractCompiler(Set<Pattern> patterns, @RegExp String delimiter, boolean delimitByLetterSymbolChange) {
		this.patterns = patterns.stream()
								.collect(groupingBy(Pattern::precedence, toSet()))
								.entrySet()
								.stream()
								.sorted(Map.Entry.comparingByKey())
								.map(Map.Entry::getValue)
								.collect(Collectors.toList());
		this.delimiter = delimiter;
	}

	public AbstractCompiler(Set<Pattern> patterns, boolean delimitByLetterSymbolChange) {
		this(patterns, "\\s+", delimitByLetterSymbolChange);
	}

	public T compile(String text) {
		List<DynamicTokenPart> tokenParts = new ArrayList<>(text.length());

		// List<TypelessStringPart> tokens = tokenize(text);
		List<TypelessStringPart> tokens = null;
		for (int assoc = AssociativityPrecedenceLevel.MIN; assoc < AssociativityPrecedenceLevel.MAX; assoc++) {
			Set<Pattern> precedencePatterns = this.patterns.get(assoc);
			boolean ltr = AssociativityPrecedenceLevel.of(assoc).associativity() != Associativity.RIGHT_TO_LEFT; // NON_ASSOCIATIVE is LTR here
			for (int tokenInd = 0; tokenInd < tokens.size(); tokenInd++) {
				int flippedInd = ltr ? tokenInd : tokens.size() - tokenInd - 1;
				Set<Pattern> matches = precedencePatterns.stream()
														 // .filter(p -> p.matches(tokens, flippedInd))
														 .collect(Collectors.toSet());
				if (matches.size() > 1) {
					throw new ParseException("ambiguous operation call \"%s\" at index %d", tokens.get(flippedInd), tokens.get(flippedInd).from());
				} else if (matches.size() == 1) {
					Pattern pattern = matches.iterator().next();
					pattern.reduce(tokens, flippedInd);
				}
			}
		}
		if (tokens.size() == 0)
			// empty input...
			;
		else if (tokens.size() > 1)
			// couldn't reduce...
			;
		// if all of the last things aren't tokens
		//error hangling
		// return tokens.get(0).actual();
		return null;
	}

	// public TokenType tokenTypeOf(String prefix) { // bad to have this; type is dependent on neighbor and found later
	//
	// }

	/**
	 * mixture of text and compiled elements
	 */
	private static class DynamicCodeText {
		private String originalText;
		// data about which parts were taken up; probably a boolean array and correlating collection of real values
	}

	private void dynamicTokenize(String str) {
		List<DynamicTokenPart> tokenParts = new ArrayList<>();

	}

	// private List<TypelessStringPart> tokenize(String str) {
	// 	TokenPart[] tokenParts = new TokenPart[str.length() + 1];
	// 	tokenParts[0] = new TokenPart(true, Collections.emptyList());
	// 	for (int i = 1; i <= str.length(); i++) {
	// 		List<TypelessStringPart> prefixes = new ArrayList<>();
	// 		for (int j = 0; j < i; j++) {
	// 			if (tokenParts[j].splittable()) {
	// 				String prefix = str.substring(j, i);
	// 				TokenType currentTokenType = TokenType.tokenTypeOf(prefix.toLowerCase());
	// 				if (currentTokenType == UNKNOWN)
	// 					currentTokenType = TokenType.tokenTypeOf(prefix);
	// 				if (currentTokenType != TokenType.UNKNOWN) { // valid
	// 					prefixes.addAll(tokenParts[j].prefixes());
	// 					prefixes.add(new TypelessStringPart(currentTokenType.isMapperType() ? prefix.toLowerCase() : prefix, j, i));
	// 					break;
	// 				}
	// 			}
	// 		}
	// 		tokenParts[i] = new TokenPart(!prefixes.isEmpty(), prefixes);
	// 	}
	//
	// 	List<TypelessStringPart<T>> lastParts = tokenParts[tokenParts.length - 1].prefixes();
	// 	if (lastParts.size() == 0) {
	// 		for (int i = tokenParts.length - 1; i >= 0; i--) {
	// 			if (!tokenParts[i].prefixes().isEmpty()) {
	// 				TypelessStringPart unknownPartLastPrefix = tokenParts[i].lastPrefix();
	// 				throw new ParseException("unknown token around index %d", unknownPartLastPrefix.from() + 1);
	// 			}
	// 		}
	// 		throw new ParseException("unknown token");
	// 	} else {
	// 		TypelessStringPart lastPartLastPrefix = tokenParts[tokenParts.length - 1].lastPrefix();
	// 		if (lastPartLastPrefix.token() == UNKNOWN)
	// 			throw new ParseException("unknown token %s at index %d",
	// 									 lastPartLastPrefix.string(), lastPartLastPrefix.from());
	// 	}
	// 	lastParts.removeIf(s -> s.token() == WHITESPACE);
	// 	return lastParts;
	// }

	static <T> T getSingleton(List<T> list) { // TODO integrate / deprecate
		if (list.size() == 0)
			return null;
		else if (list.size() > 1)
			return null;
		return list.get(0);
	}

	public static <T> T[] newArray(Class<T> c, int length) {
		return (T[]) Array.newInstance(c, length);
	}

	public static <T> T[] asArray(T... ts) {
		return ts;
	}

}
