package bran.parser;

import bran.exceptions.ParseException;
import bran.tree.compositions.expressions.values.Constant;
import bran.tree.compositions.expressions.Expression;
import bran.tree.compositions.expressions.values.Variable;
import bran.exceptions.IllegalArgumentAmountException;
import bran.tree.compositions.expressions.functions.MultiArgFunction;
import bran.tree.compositions.expressions.operators.ArithmeticOperator;
import bran.tree.structure.TreePart;

import java.util.*;
import java.util.function.Function;

import static bran.parser.CompositionParser.*;
import static bran.parser.ExpressionParser.TokenType.ExpressionZone.*;
import static bran.parser.ExpressionParser.TokenType.*;
import static java.util.stream.Collectors.toMap;

public class ExpressionParser {

	final record StringPart(String string, int from, int to, TokenType tokenType) { }
	final record TokenPart(boolean splittable, List<StringPart> prefixes) {

		public StringPart lastPrefix() {
			if (prefixes.isEmpty())
				return null;
			return prefixes.get(prefixes.size() - 1);
		}

	}

	// private static final String operatorsRegex = Arrays.stream(Operator.values()).flatMap(o -> Arrays.stream(o.getSymbols())).distinct().collect(joining("|"));
	// private static final String lineOperatorsRegex = Arrays.stream(LineOperator.values()).flatMap(o -> Arrays.stream(o.getSymbols())).distinct().collect(joining("|"));
	// public static final Matcher statementMatcher = Pattern.compile("n\\(x\\)|n\\[x]|n\\{x}|nx"
	// 		.replaceAll("x", "\\s*\\(" + lineOperatorsRegex + "\\)\\s+)*[A-Za-z][A-Za-z_\\d]*\\s+\\(" + operatorsRegex + "\\)\\s*X")
	// 		.replaceAll("n", "(\\(" + lineOperatorsRegex + "\\)\\s+)*\\((" + lineOperatorsRegex + "\\)\\s*)?")).matcher("");

	public static final Map<String, ArithmeticOperator> expressionOperators = Parser.getSymbolMapping(ArithmeticOperator.values());
	public static final Map<String, MultiArgFunction> expressionLineOperators = Parser.getSymbolMapping(MultiArgFunction.values());

	// private static final Set<String> statementLeftIdentifiers = "([{".chars().mapToObj(Character::toString).collect(Collectors.toSet());

	enum TokenType {
		FUNCTION(START, START, expressionLineOperators::get),
		COMMA(MIDDLE, START),
		OPERATOR(MIDDLE, START, expressionOperators::get),
		LEFT_IDENTIFIER(START, START), // becomes middle after the (expression) is simplified
		RIGHT_IDENTIFIER(MIDDLE, MIDDLE),
		VARIABLE(START, MIDDLE),
		CONSTANT(START, MIDDLE, Constant::of),
		WHITESPACE(ANY, NOWHERE), // the previous type
		UNKNOWN(NOWHERE, NOWHERE);

		private final ExpressionZone currentZone;
		private final ExpressionZone proceedingZone; // zone after current one
		private Function<String, TreePart> convertFromString;

		TokenType(ExpressionZone currentZone, ExpressionZone proceedingZone,
				  Function<String, TreePart> convertFromString) {
			this.currentZone = currentZone;
			this.proceedingZone = proceedingZone;
			this.convertFromString = convertFromString;
		}

		TokenType(ExpressionZone currentZone, ExpressionZone proceedingZone) {
			this.currentZone = currentZone;
			this.proceedingZone = proceedingZone;
		}

		static TokenType tokenTypeOf(String prefix) {
			return prefix.equals(",") ? COMMA
					: prefix.isBlank() ? WHITESPACE
					: expressionOperators.containsKey(prefix) ? OPERATOR
					: expressionLineOperators.containsKey(prefix) ? FUNCTION
					: leftIdentifiers.contains(prefix) ? LEFT_IDENTIFIER
					: rightIdentifiers.contains(prefix) ? RIGHT_IDENTIFIER
					: Constant.validName(prefix) ? CONSTANT
					: Variable.validName(prefix) ? VARIABLE
					: UNKNOWN;
		}

		public boolean isConvertableIdentifier() {
			return convertFromString != null;
		}

		public TreePart getOperator(final String tokenString) {
			return convertFromString.apply(tokenString);
		}

		enum ExpressionZone {
			START,		// Start or of expression - or end of one, to start a new one
			MIDDLE,		// After right bracket or variable
			ANY,		//
			NOWHERE;	//
			public boolean inZoneOf(ExpressionZone other) {
				return this == other || other == ANY || this == ANY;
			}
		}

	}

	// public static Equation parseEquation(String str) {
	// 	// if (str.substring(0, 8).equalsIgnoreCase("argument"))
	// 	// 	str = str.substring(7);
	// 	return null;
	// }

	public static Expression parse(String str) throws ParseException {
		str = str.trim();
		if (str.length() == 0)
			return Expression.empty();

		final Map<String, Variable> localVariables = new HashMap<>();

		final CommaSeparatedExpression expression = parse(tokenizeExpression(str), localVariables, 0);
		if (!expression.isSingleton())
			throw new ParseException("misplaced comma or function call");
		return expression.getAsSingleton();
	}

	// private static record StatementEnd(Expression statement, int last) { }

	private static CommaSeparatedExpression parse(List<StringPart> tokens, final Map<String, Variable> localVariables, int start) {
		boolean inner = start != 0;
		TokenType.ExpressionZone expectingZone = START;
		ExpressionBuilder expBuilder = new ExpressionBuilder();
		for (int i = start; i < tokens.size(); i++) {
			TokenType currentTokenType = tokens.get(i).tokenType();
			TokenType.ExpressionZone nextProceedingZone = currentTokenType.proceedingZone;
			if (!currentTokenType.currentZone.inZoneOf(expectingZone)) {
				throw new ParseException("unexpected token %s at index %d", tokens.get(i).string(), tokens.get(i).from());
			} else {
				String tokenString = tokens.get(i).string();
				if (currentTokenType.isConvertableIdentifier()) {
					expBuilder.add(currentTokenType.getOperator(tokenString));
				} else switch (currentTokenType) {		// NO FALL THROUGH
					case LEFT_IDENTIFIER:
						CommaSeparatedExpression innerExpressions = parse(tokens, localVariables, i + 1);
						expBuilder.add(innerExpressions);
						if (tokens.get(i + 1).tokenType == RIGHT_IDENTIFIER) {
							nextProceedingZone = MIDDLE;
							i++; // last token was )
						}
						break;
					case RIGHT_IDENTIFIER:
						if (inner) {
							tokens.subList(start, i).clear();
							try {
								return new CommaSeparatedExpression(expBuilder.build());
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
						try {
							return new CommaSeparatedExpression(expBuilder.build(), parse(tokens, localVariables, start));
						} catch (IllegalArgumentAmountException e) {
							throw new ParseException("illegal amount of arguments near %s near index %d: %s",
													 tokens.get(i).string(), tokens.get(i).from(), e.getMessage());
						}
					case VARIABLE:
						expBuilder.add(localVariables.computeIfAbsent(tokenString, Variable::new));
						break;
					case UNKNOWN:
						throw new ParseException("unknown token \"%s\" at index %d", tokenString, tokens.get(tokens.size() - 1).from());
					case WHITESPACE:
						assert(i != 0);
						nextProceedingZone =
								// i == start + 1 ? MIDDLE // "( a"
								// 					 :
													 tokens.get(i - 1).tokenType().proceedingZone;
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
			return new CommaSeparatedExpression(expBuilder.build());
		} catch (IllegalArgumentAmountException e) {
			throw new ParseException("illegal amount of arguments in the last function: " + e.getMessage());
		}
	}

	private static List<StringPart> tokenizeExpression(String str) {
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
					if (currentTokenType != UNKNOWN) { // valid
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

}
