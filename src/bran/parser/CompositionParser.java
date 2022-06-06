package bran.parser;

import bran.exceptions.ParseException;
import bran.tree.compositions.statements.VariableStatement;
import bran.tree.compositions.statements.operators.LineOperator;
import bran.tree.compositions.statements.operators.LogicalOperator;
import bran.tree.compositions.statements.special.equivalences.EquivalenceTypeImpl;
import bran.tree.compositions.statements.special.equivalences.equation.EquationType;
import bran.tree.compositions.statements.special.equivalences.inequality.InequalityType;
import bran.tree.compositions.expressions.values.Constant;
import bran.tree.compositions.expressions.values.Variable;
import bran.exceptions.IllegalArgumentAmountException;
import bran.tree.compositions.expressions.functions.MultiArgFunction;
import bran.tree.compositions.expressions.operators.Operator;
import bran.tree.compositions.Composition;
import bran.tree.structure.mapper.Mapper;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;

import static bran.parser.CompositionParser.TokenType.CompositionType.*;
import static bran.parser.CompositionParser.TokenType.*;
import static bran.parser.CompositionParser.TokenType.OrderZone.*;
import static bran.parser.ExpressionParser.expressionLineOperators;
import static bran.parser.ExpressionParser.expressionOperators;
import static bran.parser.StatementParser.statementLineOperators;
import static bran.parser.StatementParser.statementOperators;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.flatMapping;
import static java.util.stream.Collectors.toMap;

public class CompositionParser {

	final record StringPart(String string, int from, int to, TokenType tokenType) { }
	final record TokenPart(boolean splittable, List<StringPart> prefixes) {

		public StringPart lastPrefix() {
			if (prefixes.isEmpty())
				return null;
			return prefixes.get(prefixes.size() - 1);
		}

	}

	static final Map<String, EquivalenceTypeImpl> equivalenceOperators = Parser.getSymbolMapping(EquivalenceTypeImpl.values());

	static final Map<String, Map.Entry<Mapper, TokenType>> symbolTokens = //expected zone param
			Map.of(MultiArgFunction.class, FUNCTION,
				   LineOperator.class, LINE_OPERATOR,
				   Operator.class, EXP_OPERATOR,
				   LogicalOperator.class, SMT_OPERATOR,
				   EquationType.class, EQUIVALENCE,
				   InequalityType.class, EQUIVALENCE)
			   .entrySet()
			   .stream()
			   .collect(flatMapping(entry -> stream(entry.getKey()
														 .getEnumConstants())
													 .flatMap(mapper -> stream(mapper.getSymbols())
																				.distinct()
																				.map(symbol -> new SimpleEntry<>(symbol, (Mapper) mapper)))
													 .map(symMap -> new SimpleEntry<>(symMap.getKey(), new SimpleEntry<>(symMap.getValue(), entry.getValue()))),
									toMap(SimpleEntry::getKey, SimpleEntry::getValue, (v1, v2) -> v1)));

	// static final Map<String, EquationType> equivalences = Parser.getSymbolMapping()
	static final Set<String> leftIdentifiers = Set.of("(", "[", "{");
	static final Set<String> rightIdentifiers = Set.of(")", "]", "}");

	enum TokenType {
		FUNCTION(START, START, EXPRESSION),
		LINE_OPERATOR(START, START, STATEMENT),
		COMMA(MIDDLE, START, BOTH),
		EQUIVALENCE(MIDDLE, MIDDLE, STATEMENT),
		EXP_OPERATOR(MIDDLE, START, EXPRESSION),
		SMT_OPERATOR(MIDDLE, START, STATEMENT),
		LEFT_IDENTIFIER(START, START, BOTH), // becomes middle after the (expression) is simplified
		RIGHT_IDENTIFIER(MIDDLE, MIDDLE, BOTH),
		VARIABLE(START, MIDDLE, BOTH),
		CONSTANT(START, MIDDLE, EXPRESSION),
		WHITESPACE(ANY, NOWHERE, BOTH), // the previous type
		UNKNOWN(NOWHERE, NOWHERE, NEITHER);

		private final OrderZone currentZone;
		private final OrderZone proceedingZone; // zone after current one
		private CompositionType compositionType;

		TokenType(OrderZone currentZone, OrderZone proceedingZone, CompositionType compositionType) {
			this.currentZone = currentZone;
			this.proceedingZone = proceedingZone;
			this.compositionType = compositionType;
		}

		static TokenType tokenTypeOf(String prefix) {
			final Map.Entry<Mapper, TokenType> tokenInfo = symbolTokens.get(prefix);
			if(tokenInfo != null)
				return tokenInfo.getValue();
			return prefix.equals(",") ? COMMA
				 : prefix.isBlank() ? WHITESPACE
				 : leftIdentifiers.contains(prefix) ? LEFT_IDENTIFIER
				 : rightIdentifiers.contains(prefix) ? RIGHT_IDENTIFIER
				 : expressionOperators.containsKey(prefix) ? EXP_OPERATOR
				 : statementOperators.containsKey(prefix) ? SMT_OPERATOR
				 : expressionLineOperators.containsKey(prefix) ? FUNCTION
				 : statementLineOperators.containsKey(prefix) ? LINE_OPERATOR
				 : equivalenceOperators.containsKey(prefix) ? EQUIVALENCE
				 : Constant.validName(prefix) ? CONSTANT
				 : Variable.validName(prefix) ? VARIABLE
				 : UNKNOWN;
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

	public static Composition parse(String str) {
		str = str.trim();
		if (str.length() == 0)
			return Composition.empty();

		final CommaSeparatedComposition comp = parse(tokenize(str), new HashMap<>(),0);

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
			TokenType currentTokenType = tokens.get(i).tokenType();
			OrderZone nextProceedingZone = currentTokenType.proceedingZone;
			if (!currentTokenType.currentZone.inZoneOf(expectingZone)) {
				throw new ParseException("unexpected token %s at index %d", tokens.get(i).string(), tokens.get(i).from());
			} else {
				String tokenString = tokens.get(i).string();
				switch (currentTokenType) {		// NO FALL THROUGH
					case EXP_OPERATOR, FUNCTION, LINE_OPERATOR, SMT_OPERATOR, EQUIVALENCE:
						compBuilder.add(symbolTokens.get(tokenString).getKey());
						break;
					case LEFT_IDENTIFIER:
						CommaSeparatedComposition innerExpressions = CompositionParser.parse(tokens, localVariables, i + 1);
						compBuilder.add(innerExpressions);
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
														 tokens.get(i).string(), tokens.get(i).from(), e.getMessage());
							}
						} else
							throw new ParseException("unbalanced right bracket \"%s\" at index %d", tokens.get(i).string(), tokens.get(i).from());
					case COMMA:
						if (inner) {
							tokens.subList(start, i + 1).clear();
						}
						// if (!expectingZone.inZoneOf(MIDDLE))
						// 	throw new ParseException("unfinished expression at index %d", tokens.get(tokens.size() - 1).from());
						try {
							return new CommaSeparatedComposition(compBuilder.build(),
																parse(tokens, localVariables, start));
						} catch (IllegalArgumentAmountException e) {
							throw new ParseException("illegal amount of arguments near %s near index %d: %s",
													 tokens.get(i).string(), tokens.get(i).from(), e.getMessage());
						}
					case VARIABLE:
						compBuilder.add(localVariables.computeIfAbsent(tokenString, LazyTypeVariable::new));
						break;
					case CONSTANT:
						compBuilder.add(Constant.of(tokenString));
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
						prefixes.add(new StringPart(currentTokenType == FUNCTION ? prefix.toLowerCase() : prefix, j, i, currentTokenType));
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
		return lastParts;
	}

	public static String randomGarbageComposition(int size) {
		final List<String> symbols = symbolTokens.keySet()
												 .stream()
												 .toList();
		StringBuilder sB = new StringBuilder();
		for (int i = 0; i < size; i++)
			sB.append(symbols.get(((int) (Math.random() * symbols.size()))))
			  .append(' ');
		return sB.toString();
	}

}
