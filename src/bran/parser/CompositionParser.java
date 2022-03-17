package bran.parser;

import bran.exceptions.ParseException;
import bran.tree.compositions.statements.operators.LineOperator;
import bran.tree.compositions.statements.operators.LogicalOperator;
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
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.flatMapping;
import static java.util.stream.Collectors.toMap;

public class CompositionParser {

	final record StringPart(String string, int from, int to, TokenType tokenType) { }
	final record TokenPart(boolean splittable, List<StringPart> prefixes) {	}



	static final Map<String, Map.Entry<Mapper, TokenType>> symbolTokens = //expected zone param
			Map.of(MultiArgFunction.class, FUNCTION,
				   LineOperator.class, LINE_OPERATOR,
				   Operator.class, EXP_OPERATOR,
				   LogicalOperator.class, SNT_OPERATOR,
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
									toMap(SimpleEntry::getKey, SimpleEntry::getValue)));

	// static final Map<String, EquationType> equivalences = Parser.getSymbolMapping()
	static final Set<String> leftIdentifiers = Set.of("(", "[", "{");
	static final Set<String> rightIdentifiers = Set.of(")", "]", "}");

	enum TokenType {
		FUNCTION(START, START, EXPRESSION),
		LINE_OPERATOR(START, START, STATEMENT),
		COMMA(MIDDLE, START, BOTH),
		EQUIVALENCE(MIDDLE, MIDDLE, STATEMENT),
		EXP_OPERATOR(MIDDLE, START, EXPRESSION),
		SNT_OPERATOR(MIDDLE, START, STATEMENT),
		LEFT_IDENTIFIER(START, MIDDLE, BOTH), // becomes middle after the (expression) is simplified
		RIGHT_IDENTIFIER(MIDDLE, MIDDLE, BOTH),
		EXP_VARIABLE(START, MIDDLE, EXPRESSION),
		SNT_VARIABLE(START, MIDDLE, STATEMENT),
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
					   : Constant.validName(prefix) ? CONSTANT
					   : Variable.validName(prefix) ? EXP_VARIABLE
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

		final Map<String, Variable> localVariables = new HashMap<>();

		final CommaSeparatedExpression expression = parse(tokenize(str), localVariables, 0);
		if (!expression.isSingleton())
			throw new ParseException("misplaced comma or function call");
		return expression.getAsSingleton();
	}

	// private static record StatementEnd(Expression statement, int last) { }

	private static CommaSeparatedExpression parse(List<StringPart> tokens, final Map<String, Variable> localVariables, int start) {
		boolean inner = start != 0;
		OrderZone expectingZone = START;
		ExpressionBuilder expBuilder = new ExpressionBuilder();
		for (int i = start; i < tokens.size(); i++) {
			TokenType currentTokenType = tokens.get(i).tokenType();
			OrderZone nextProceedingZone = currentTokenType.proceedingZone;
			if (!currentTokenType.currentZone.inZoneOf(expectingZone)) {
				// if (currentTokenType == RIGHT_IDENTIFIER && tokens.get(i - 1))
				throw new ParseException("unexpected token %s at index %d", tokens.get(i).string(), tokens.get(i).from());
			} else {
				String tokenString = tokens.get(i).string();
				switch (currentTokenType) {		// NO FALL THROUGH
					case EXP_OPERATOR, FUNCTION, LINE_OPERATOR, SNT_OPERATOR, EQUIVALENCE:
						expBuilder.add(symbolTokens.get(tokenString).getKey()); break;
					case LEFT_IDENTIFIER:
						CommaSeparatedExpression innerExpressions = CompositionParser.parse(tokens, localVariables, i + 1);
						expBuilder.add(innerExpressions); break;
					case RIGHT_IDENTIFIER:
						if (inner) {
							tokens.subList(start, i + 1).clear();
							try {
								return new CommaSeparatedExpression(expBuilder.build());
							} catch (IllegalArgumentAmountException e) {
								throw new ParseException("illegal amount of arguments near %s near index %d: %s",
														 tokens.get(i).string(), tokens.get(i).from(), e.getMessage());
							}
						} else
							throw new ParseException("unbalanced right bracket \"%s\" at index %d", tokens.get(i).string(), tokens.get(i).from());
					case COMMA:
						if (!expectingZone.inZoneOf(MIDDLE))
							throw new ParseException("unfinished expression at index %d", tokens.get(tokens.size() - 1).from());
						try {
							return new CommaSeparatedExpression(expBuilder.build(),
																parse(tokens, localVariables, i + 1));
						} catch (IllegalArgumentAmountException e) {
							throw new ParseException("illegal amount of arguments near %s near index %d: %s",
													 tokens.get(i).string(), tokens.get(i).from(), e.getMessage());
						}
					case EXP_VARIABLE:
						expBuilder.add(localVariables.computeIfAbsent(tokenString, Variable::new)); break;
					case CONSTANT:
						expBuilder.add(Constant.of(tokenString)); break;
					case UNKNOWN:
						throw new ParseException("unknown token \"%s\" at index %d", tokenString, tokens.get(tokens.size() - 1).from());
					case WHITESPACE:
						assert(i != 0);
						nextProceedingZone = i == start + 1 ? MIDDLE // "( a"
													 : tokens.get(i - 1).tokenType().proceedingZone;
						break;
					default:
						throw new ParseException("this shouldn't've happened");
				}
				expectingZone = nextProceedingZone;
			}
		}
		if (!expectingZone.inZoneOf(MIDDLE))
			throw new ParseException("unfinished expression at index %d", tokens.get(tokens.size() - 1).from());
		try {
			return new CommaSeparatedExpression(expBuilder.build());
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
					TokenType currentTokenType = TokenType.tokenTypeOf(prefix);
					if (currentTokenType != TokenType.UNKNOWN) { // valid
						prefixes.addAll(tokenParts[j].prefixes());
						prefixes.add(new StringPart(prefix, j, i, currentTokenType));
						break;
					}
				}
			}
			tokenParts[i] = new TokenPart(!prefixes.isEmpty(), prefixes);
		}

		List<StringPart> lastParts = tokenParts[tokenParts.length - 1].prefixes();
		if (lastParts.size() == 0)
			throw new ParseException("unknown token");
		else if (lastParts.get(lastParts.size() - 1).tokenType == TokenType.UNKNOWN)
			throw new ParseException("unknown token: %s at index %d",
									 lastParts.get(lastParts.size() - 1).string(), lastParts.get(lastParts.size() - 1).from());

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
