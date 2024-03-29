package bran.parser;

// import bran.logic.tree.TreeBuilder;


import bran.exceptions.ParseException;
import bran.tree.compositions.Composition;
import bran.tree.structure.mapper.Mapper;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class Parser {

	static void test() {
		// TreeBuilder<Statement, Operator, OperationStatement, LineOperator, LineStatement> x
		// 		= new TreeBuilder<>(LineStatement::new, OperationStatement::new);
		// TreeBuilder<AbstractSet, Operator, OperationSet, LineOperator, LineSet> y
		// 		= new TreeBuilder<>(LineSet::new, OperationSet::new);
	}

	public static <T extends Mapper> Map<String, T> getSymbolMapping(T[] mapper) {
		return Stream.of(mapper)
					 .flatMap(o -> Arrays.stream(o.getSymbols())
										 .distinct()
										 .map(s -> new AbstractMap.SimpleEntry<>(s, o)))
					 .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	public static <C extends Composition> String parseAndExcept(String str, Function<String, C> parser) {
		return parseAndExcept(str, C::toString, Throwable::getMessage, parser);
	}

	public static <C extends Composition> String parseAndExcept(String str, Function<C, String> toString, Function<String, C> parser) {
		return parseAndExcept(str, toString, Throwable::getMessage, parser);
	}

	public static <C extends Composition, T> T parseAndExcept(String str, Function<C, T> toT,
															  Function<ParseException, T> onFailure,
															  Function<String, C> parser) {
		try {
			return toT.apply(parser.apply(str));
		} catch (ParseException e) {
			return onFailure.apply(e);
		}
	}

	// private static List<StringPart> tokenizeStatement(String str) {
	// 	TokenPart[] tokenParts = new TokenPart[str.length() + 1];
	// 	tokenParts[0] = new TokenPart(true, Collections.emptyList());
	// 	for (int i = 1; i <= str.length(); i++) {
	// 		List<StringPart> prefixes = new ArrayList<>();
	// 		for (int j = 0; j < i; j++) {
	// 			if (tokenParts[j].splittable()) {
	// 				String prefix = str.substring(j, i);
	// 				TokenType currentTokenType = tokenTypeOf(prefix);
	// 				if (currentTokenType != UNKNOWN) { // valid
	// 					prefixes.addAll(tokenParts[j].prefixes());
	// 					prefixes.add(new StringPart(prefix, j, i, currentTokenType));
	// 					break;
	// 				}
	// 			}
	// 		}
	// 		tokenParts[i] = new TokenPart(!prefixes.isEmpty(), prefixes);
	// 	}
	//
	// 	List<StringPart> lastParts = tokenParts[tokenParts.length - 1].prefixes();
	// 	if (lastParts.size() == 0)
	// 		throw new ParseException("unknown token");
	// 	else if (lastParts.get(lastParts.size() - 1).tokenType == UNKNOWN)
	// 		throw new ParseException("unknown token: %s at index %d",
	// 								 lastParts.get(lastParts.size() - 1).string(), lastParts.get(lastParts.size() - 1).from());
	//
	// 	return lastParts;
	// }

}
