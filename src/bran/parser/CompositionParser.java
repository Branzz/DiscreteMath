package bran.parser;

import bran.exceptions.IllegalArgumentAmountException;
import bran.exceptions.ParseException;
import bran.tree.compositions.Composition;
import bran.tree.compositions.expressions.Expression;
import bran.tree.compositions.expressions.functions.MultiArgFunction;
import bran.tree.compositions.expressions.operators.Operator;
import bran.tree.compositions.expressions.values.Constant;
import bran.tree.compositions.expressions.values.Variable;
import bran.tree.compositions.statements.Statement;
import bran.tree.compositions.statements.operators.LineOperator;
import bran.tree.compositions.statements.operators.LogicalOperator;
import bran.tree.compositions.statements.special.equivalences.EquivalenceTypeImpl;
import bran.tree.structure.mapper.Mapper;
import bran.tree.structure.mapper.OrderedOperator;

import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static bran.parser.CompositionParser.TokenType.CompositionType.*;
import static bran.parser.CompositionParser.TokenType.*;
import static bran.parser.CompositionParser.TokenType.OrderZone.*;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.*;

public class CompositionParser {

	final record TokenPart(boolean splittable, List<StringPart> prefixes) {

		public StringPart lastPrefix() {
			if (prefixes.isEmpty())
				return null;
			return prefixes.get(prefixes.size() - 1);
		}

	}

	static final Map<String, EquivalenceTypeImpl> equivalenceOperators = Parser.getSymbolMapping(EquivalenceTypeImpl.values());

	private enum NumberSuperScript implements Mapper {
		S0(0, "\u2070"),
		S1(1, "\u00B9"),
		S2(2, "\u00B2"),
		S3(3, "\u00B3"),
		S4(4, "\u2074"),
		S5(5, "\u2075"),
		S6(6, "\u2076"),
		S7(7, "\u2077"),
		S8(8, "\u2078"),
		S9(9, "\u2079");

		private final int value;
		private final String[] symbols;

		NumberSuperScript(int value, String... symbols) {
			this.value = value;
			this.symbols = symbols;
		}

		@Override
		public String[] getSymbols() {
			return symbols;
		}
	}

	static final Map<String, Map.Entry<Mapper, TokenType>> symbolTokens = //expected zone param
			// Map.of(MultiArgFunction.class, FUNCTION,
			// 	   LineOperator.class, LINE_OPERATOR,
			// 	   Operator.class, EXP_OPERATOR,
			// 	   LogicalOperator.class, SMT_OPERATOR,
			// 	   EquivalenceTypeImpl.class, EQUIVALENCE)
			stream(TokenType.values())
					.filter(t -> t.associatedClass != null)
					.collect(toMap(t -> t.associatedClass, t -> t))
			   .entrySet()
			   .stream()
			   .collect(flatMapping(entry -> stream(entry.getKey().getEnumConstants())
													 .flatMap(mapper -> stream(mapper.getSymbols())
																				.map(String::toLowerCase)
																				.distinct()
																				.map(symbol -> new SimpleEntry<>(symbol, (Mapper) mapper)))
													 .map(symMap -> new SimpleEntry<>(symMap.getKey(), new SimpleEntry<>(symMap.getValue(), entry.getValue()))),
									toMap(SimpleEntry::getKey, SimpleEntry::getValue, (v1, v2) -> v1)));

	private static <E> E randElement(double random, E[] es) {
		return es[(int) (random * es.length)];
	}

	private static String generate(double random, Class<? extends OrderedOperator> tokenClass) {
		final String[] symbols = randElement(random, tokenClass.getEnumConstants()).getSymbols();
		return symbols[(int) ((random * 100) % symbols.length)];
	}

	// static final Map<String, EquationType> equivalences = Parser.getSymbolMapping()
	static final Set<String> leftIdentifiers = Set.of("(", "[", "{");
	static final Set<String> rightIdentifiers = Set.of(")", "]", "}");

	enum TokenType {
		LEFT_IDENTIFIER(START, START, BOTH, r -> "("), // becomes middle after the (expression) is simplified
		FUNCTION(START, START, true, EXPRESSION, MultiArgFunction.class),
		LINE_OPERATOR(START, START, true, STATEMENT, LineOperator.class),
		VARIABLE(START, MIDDLE, BOTH, r -> String.valueOf((char) ('a' + (int) (r * 3)))),
		CONSTANT(START, MIDDLE, EXPRESSION, r -> r > .2 ? String.valueOf((int) ((r - .8) * 50 - 5)) : String.valueOf(r * 12.5 - 5).substring(0, 5)),
		COMMA(MIDDLE, START, BOTH, r -> ","),
		EQUIVALENCE(MIDDLE, START, true, STATEMENT, EquivalenceTypeImpl.class),
		EXP_OPERATOR(MIDDLE, START, true, EXPRESSION, Operator.class),
		SMT_OPERATOR(MIDDLE, START, true, STATEMENT, LogicalOperator.class),
		RIGHT_IDENTIFIER(MIDDLE, MIDDLE, BOTH, r -> ")"),
		WHITESPACE(ANY, NOWHERE, BOTH, r -> " "), // the previous type
		UNKNOWN(NOWHERE, NOWHERE, NEITHER, r -> "UNKNOWN");

		private final OrderZone currentZone;
		private final OrderZone proceedingZone; // zone after current one
		private final boolean mapperType; // Mapper.java; non-case sensitive
		private final CompositionType compositionType;
		private final Function<Double, String> generator;
		private final Class<? extends OrderedOperator> associatedClass;
		private final Map<String, Mapper> tokenMap;

			TokenType(OrderZone currentZone, OrderZone proceedingZone, CompositionType compositionType, Function<Double, String> generator) {
			this(currentZone, proceedingZone, false, compositionType, generator);
		}

		TokenType(OrderZone currentZone, OrderZone proceedingZone, boolean mapperType, CompositionType compositionType, Function<Double, String> generator) {
			this.currentZone = currentZone;
			this.proceedingZone = proceedingZone;
			this.mapperType = mapperType;
			this.compositionType = compositionType;
			this.generator = generator;
			this.associatedClass = null;
			this.tokenMap = null;
		}

		TokenType(OrderZone currentZone, OrderZone proceedingZone, CompositionType compositionType, Class<? extends OrderedOperator> associatedClass) {
			this(currentZone, proceedingZone, false, compositionType, associatedClass);
		}

		TokenType(OrderZone currentZone, OrderZone proceedingZone, boolean mapperType, CompositionType compositionType, Class<? extends OrderedOperator> associatedClass) {
			this.currentZone = currentZone;
			this.proceedingZone = proceedingZone;
			this.mapperType = mapperType;
			this.compositionType = compositionType;
			this.tokenMap = Parser.getSymbolMapping(associatedClass.getEnumConstants());
			this.generator = r -> String.valueOf(randElement(r, tokenMap.keySet().toArray()));
			this.associatedClass = associatedClass;
		}

		public boolean isMapperType() {
			return mapperType;
		}

		static TokenType getPrefixFromMap(String prefix) {
			for (TokenType tokenType : values()) {
				if (tokenType.tokenMap != null && tokenType.tokenMap.containsKey(prefix))
					return tokenType;
			}
			return UNKNOWN;
		}

		static TokenType tokenTypeOf(String prefix) {
			final Map.Entry<Mapper, TokenType> tokenInfo = symbolTokens.get(prefix);
			if(tokenInfo != null)
				return tokenInfo.getValue();
			return prefix.equals(",") ? COMMA
				 : prefix.isBlank() ? WHITESPACE
				 : leftIdentifiers.contains(prefix) ? LEFT_IDENTIFIER
				 : rightIdentifiers.contains(prefix) ? RIGHT_IDENTIFIER
				 // : expressionOperators.containsKey(prefix) ? EXP_OPERATOR
				 // : statementOperators.containsKey(prefix) ? SMT_OPERATOR
				 // : expressionLineOperators.containsKey(prefix) ? FUNCTION
				 // : statementLineOperators.containsKey(prefix) ? LINE_OPERATOR
				 // : equivalenceOperators.containsKey(prefix) ? EQUIVALENCE
				 : Constant.validName(prefix) ? CONSTANT
				 : Variable.validName(prefix) ? VARIABLE
				 : getPrefixFromMap(prefix);
		}

		enum OrderZone {
			START,		// Start or of expression - or end of one, to start a new one
			MIDDLE,		// After right bracket or variable
			ANY,		//
			NOWHERE;	//
			public boolean inZoneOf(OrderZone other) {
				return this == other || other == ANY || this == ANY;
			}
		}

		enum CompositionType {
			STATEMENT, EXPRESSION, BOTH, NEITHER
		}

	}

	public static Expression parseExpression(String str) {
		Composition composition = parse(str);
		if (composition instanceof Expression expression) {
			return expression;
		} else {
			throw new ParseException("not an expression");
		}
	}

	public static Statement parseStatement(String str) {
		Composition composition = parse(str);
		if (composition instanceof Statement statement) {
			return statement;
		} else {
			throw new ParseException("not an statement");
		}
	}

	public static Composition parse(String str) {
		str = str.trim();
		if (str.length() == 0)
			return Composition.empty();

		return parse(tokenize(str));
	}

	private static Composition parse(List<StringPart> tokens) {
		final CommaSeparatedComposition comp = parse(tokens, new HashMap<>(), 0);
		if (!comp.isSingleton())
			throw new ParseException("misplaced comma or function call");
		return comp.getAsSingleton();
	}

	// private static record StatementEnd(Expression statement, int last) { }

	private static CommaSeparatedComposition parse(List<StringPart> tokens, Map<String, LazyTypeVariable> localVariables, int start) {
		boolean inner = start != 0;
		OrderZone expectingZone = START;
		CompositionBuilder compBuilder = new CompositionBuilder();
		for (int i = start; i < tokens.size(); i++) {
			final StringPart stringPart = tokens.get(i);
			TokenType currentTokenType = stringPart.tokenType();
			OrderZone nextProceedingZone = currentTokenType.proceedingZone;
			if (!currentTokenType.currentZone.inZoneOf(expectingZone)
				&& !(i == start && currentTokenType == RIGHT_IDENTIFIER)) {
				throw new ParseException("unexpected token %s at index %d", stringPart.string(), stringPart.from());
			} else {
				String tokenString = stringPart.string();
				if (currentTokenType.isMapperType())
					compBuilder.add(symbolTokens.get(tokenString).getKey(), stringPart);
				else
				switch (currentTokenType) {		// NO FALL THROUGH
					case LEFT_IDENTIFIER:
						CommaSeparatedComposition innerExpressions = CompositionParser.parse(tokens, localVariables, i + 1);
						compBuilder.add(innerExpressions, stringPart);
						if (tokens.get(i + 1).tokenType == RIGHT_IDENTIFIER) {
							nextProceedingZone = MIDDLE;
							i++; // last token was )
						}
						break;
					case RIGHT_IDENTIFIER:
						if (inner) {
							tokens.subList(start, i).clear();
							try {
								return new CommaSeparatedComposition(compBuilder.build());
							} catch (IllegalArgumentAmountException e) {
								throw new ParseException("illegal amount of arguments near %s near index %d: %s",
														 stringPart.string(), stringPart.from(), e.getMessage());
							}
						} else
							throw new ParseException("unbalanced right bracket \"%s\" at index %d", stringPart.string(), stringPart.from());
					case COMMA:
						if (inner) {
							tokens.subList(start, i + 1).clear();
						} else
							throw new ParseException("comma not within function call at index %d", stringPart.from());
						// if (!expectingZone.inZoneOf(MIDDLE))
						// 	throw new ParseException("unfinished expression at index %d", tokens.get(tokens.size() - 1).from());
						try {
							return new CommaSeparatedComposition(compBuilder.build(),
																parse(tokens, localVariables, start));
						} catch (IllegalArgumentAmountException e) {
							throw new ParseException("illegal amount of arguments near %s near index %d: %s",
													 stringPart.string(), stringPart.from(), e.getMessage());
						}
					case VARIABLE:
						compBuilder.add(localVariables.computeIfAbsent(tokenString, LazyTypeVariable::new), stringPart);
						break;
					case CONSTANT:
						compBuilder.add(Constant.of(tokenString), stringPart);
						break;
					case UNKNOWN:
						throw new ParseException("unknown token \"%s\" at index %d", tokenString, tokens.get(tokens.size() - 1).from());
					case WHITESPACE:
						assert(i != 0);
						nextProceedingZone = tokens.get(i - 1).tokenType().proceedingZone;
						break;
					default:
						throw new ParseException("this shouldn't've happened");
				}
				expectingZone = nextProceedingZone;
			}
		}
		if (inner || !expectingZone.inZoneOf(MIDDLE))
			throw new ParseException("missing closing bracket at index %d", tokens.get(tokens.size() - 1).from());
		try {
			return new CommaSeparatedComposition(compBuilder.build());
		} catch (IllegalArgumentAmountException e) {
			throw new ParseException("illegal amount of arguments in the last function: " + e.getMessage());
		}
	}

	private static List<StringPart> tokenize(String str) {
		TokenPart[] tokenParts = new TokenPart[str.length() + 1];
		tokenParts[0] = new TokenPart(true, Collections.emptyList());
		for (int i = 1; i <= str.length(); i++) {
			List<StringPart> prefixes = new ArrayList<>();
			for (int j = 0; j < i; j++) {
				if (tokenParts[j].splittable()) {
					String prefix = str.substring(j, i);
					TokenType currentTokenType = TokenType.tokenTypeOf(prefix.toLowerCase());
					if (currentTokenType == UNKNOWN)
						currentTokenType = TokenType.tokenTypeOf(prefix);
					if (currentTokenType != TokenType.UNKNOWN) { // valid
						prefixes.addAll(tokenParts[j].prefixes());
						prefixes.add(new StringPart(currentTokenType.isMapperType() ? prefix.toLowerCase() : prefix, j, i, currentTokenType, prefix));
						break;
					}
				}
			}
			tokenParts[i] = new TokenPart(!prefixes.isEmpty(), prefixes);
		}

		List<StringPart> lastParts = tokenParts[tokenParts.length - 1].prefixes();
		if (lastParts.size() == 0) {
			for (int i = tokenParts.length - 1; i >= 0; i--) {
				if (!tokenParts[i].prefixes().isEmpty()) {
					StringPart unknownPartLastPrefix = tokenParts[i].lastPrefix();
					throw new ParseException("unknown token around index %d", unknownPartLastPrefix.from() + 1);
				}
			}
			throw new ParseException("unknown token");
		} else {
			StringPart lastPartLastPrefix = tokenParts[tokenParts.length - 1].lastPrefix();
			if (lastPartLastPrefix.tokenType == UNKNOWN)
				throw new ParseException("unknown token %s at index %d",
										 lastPartLastPrefix.string(), lastPartLastPrefix.from());
		}
		lastParts.removeIf(s -> s.tokenType == WHITESPACE);
		return lastParts;
	}

	public static String randomGarbageComposition(int size) {
		final List<String> symbols = symbolTokens.keySet()
												 .stream()
												 .toList();
		StringBuilder sB = new StringBuilder();
		for (int i = 0; i < size; i++)
			sB.append(i % 2 == 0 ? "a" : symbols.get(((int) (Math.random() * symbols.size()))))
			  .append(' ');
		return sB.toString();
	}

	public static String generate(int size) {
		return generate(size, System.currentTimeMillis());
	}

	public static String generate(int size, long seed) {
		Random random = new Random(seed);
		final Map<OrderZone, List<TokenType>> currentZones =
				stream(TokenType.values()).collect(groupingBy(t -> t.currentZone, Collectors.toList()));
		// final Map<OrderZone, List<TokenType>> proceedingZones =
		// 		stream(TokenType.values()).collect(groupingBy(t -> t.proceedingZone, Collectors.toList()));
		List<TokenType> tokens = new ArrayList<>();
		OrderZone current = START;
		int parenDepth = 0;
		for (int i = 0; i < size; i++) {
			TokenType nextToken;
			do {
				nextToken = getRand(TokenType.values());
			} while (nextToken.currentZone != current || (parenDepth == 0 && nextToken == RIGHT_IDENTIFIER));
			tokens.add(nextToken);
			current = nextToken.proceedingZone;
			if (nextToken == LEFT_IDENTIFIER)
				parenDepth++;
			else if (nextToken == RIGHT_IDENTIFIER)
				parenDepth--;
		}

		while (current == START || parenDepth > 0) {
			TokenType nextToken;
			if (current == START) {
				if (parenDepth > 0 && Math.random() < .5) {
					nextToken = RIGHT_IDENTIFIER;
					parenDepth--;
				} else
					do nextToken = getRand(TokenType.values());
					while (nextToken.currentZone != current || (nextToken == LEFT_IDENTIFIER || nextToken == RIGHT_IDENTIFIER));
			} else {
				nextToken = RIGHT_IDENTIFIER;
				parenDepth--;
			}
			tokens.add(nextToken);
			current = nextToken.proceedingZone;
		}
		// StringPart[] stringParts = new StringPart[tokens.size()];
		// int strLen = 0;
		// for (int i = 0; i < tokens.size(); i++) {
		// 	TokenType token = tokens.get(i);
		// 	String str = token.generator.apply(random.nextDouble());
		// 	stringParts[i] = new StringPart(str, strLen, strLen += str.length() + 1, token);
		// }
		return tokens.stream().map(t -> t.generator.apply(random.nextDouble())).collect(joining(" "));
	}

	private static <T> T getRand(List<T> ts) {
		return ts.get((int) (Math.random() * ts.size()));
	}

	private static <T> T getRand(T[] ts) {
		return ts[((int) (Math.random() * ts.length))];
	}

	final static class StringPart {

		private final String string;
		private final int from;
		private final int to;
		private final TokenType tokenType;
		private final String visualString;

		StringPart(String string, int from, int to, TokenType tokenType) {
			this.string = string;
			this.from = from;
			this.to = to;
			this.tokenType = tokenType;
			visualString = string;
		}

		StringPart(String string, int from, int to, TokenType tokenType, String visualString) {
			this.string = string;
			this.from = from;
			this.to = to;
			this.tokenType = tokenType;
			this.visualString = visualString;
		}

		public String string() {
			return string;
		}

		public int from() {
			return from;
		}

		public int to() {
			return to;
		}

		public TokenType tokenType() {
			return tokenType;
		}

		@Override
		public String toString() {
			return visualString;
		}

	}

}
