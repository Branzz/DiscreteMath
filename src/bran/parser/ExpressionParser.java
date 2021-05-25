package bran.parser;

import bran.exceptions.ParseException;
import bran.mathexprs.treeparts.Expression;
import bran.mathexprs.treeparts.Variable;
import bran.mathexprs.treeparts.functions.IllegalArgumentAmountException;
import bran.mathexprs.treeparts.functions.MultivariableFunction;
import bran.mathexprs.treeparts.operators.Operator;

import java.util.*;
import java.util.stream.Stream;

import static bran.parser.ExpressionParser.TokenType.ExpressionZone.*;
import static java.util.stream.Collectors.toMap;

public class ExpressionParser {

	private static List<StringPart> tokens;
	private static Map<String, Variable> localVariables;
	private static int start;

	final record StringPart(String string, int from, int to, TokenType tokenType) { }
	final record TokenPart(boolean splittable, List<StringPart> prefixes) {	}

	// private static final String operatorsRegex = Arrays.stream(Operator.values()).flatMap(o -> Arrays.stream(o.getSymbols())).distinct().collect(joining("|"));
	// private static final String lineOperatorsRegex = Arrays.stream(LineOperator.values()).flatMap(o -> Arrays.stream(o.getSymbols())).distinct().collect(joining("|"));
	// public static final Matcher statementMatcher = Pattern.compile("n\\(x\\)|n\\[x]|n\\{x}|nx"
	// 		.replaceAll("x", "\\s*\\(" + lineOperatorsRegex + "\\)\\s+)*[A-Za-z][A-Za-z_\\d]*\\s+\\(" + operatorsRegex + "\\)\\s*X")
	// 		.replaceAll("n", "(\\(" + lineOperatorsRegex + "\\)\\s+)*\\((" + lineOperatorsRegex + "\\)\\s*)?")).matcher("");

	private static final Map<String, Operator> statementOperators =
			Stream.of(Operator.values())
				  .flatMap(o -> Arrays.stream(o.getSymbols())
									  .distinct()
									  .map(s -> new AbstractMap.SimpleEntry<>(s, o)))
				  .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
	private static final Map<String, MultivariableFunction> statementLineOperators =
			Stream.of(MultivariableFunction.values())
				  .flatMap(o -> Arrays.stream(o.getSymbols())
									  .distinct()
									  .map(s -> new AbstractMap.SimpleEntry<>(s, o)))
				  .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

	private static final Set<String> statementLeftIdentifiers = Set.of("(", "[", "{");
	private static final Set<String> statementRightIdentifiers = Set.of(")", "]", "}");

	enum TokenType {
		FUNCTION(START, START), COMMA(START, START), OPERATOR(MIDDLE, START), LEFT_IDENTIFIER(START, MIDDLE), // becomes middle after the (expression) is simplified
		RIGHT_IDENTIFIER(MIDDLE, MIDDLE), VARIABLE(START, MIDDLE), CONSTANT(START, MIDDLE), WHITESPACE(ANY, NOWHERE), // the previous type
		UNKNOWN(NOWHERE, NOWHERE);

		private final ExpressionZone expressionZone;
		private final ExpressionZone proceedingZone; // zone after current one

		TokenType(ExpressionZone expressionZone, ExpressionZone proceedingZone) {
			this.expressionZone = expressionZone;
			this.proceedingZone = proceedingZone;
		}

		static TokenType tokenTypeOf(String prefix) {
			return prefix.equals(",") ? COMMA
					: statementOperators.containsKey(prefix) ? OPERATOR
					: statementLineOperators.containsKey(prefix) ? FUNCTION
					: statementLeftIdentifiers.contains(prefix) ? LEFT_IDENTIFIER
					: statementRightIdentifiers.contains(prefix) ? RIGHT_IDENTIFIER
					: Variable.validName(prefix) ? VARIABLE
					: prefix.isBlank() ? WHITESPACE
					: UNKNOWN;
		}

		final static String startExceptionMessage = "expected a variable or not";
		final static String startBracketsExceptionMessage = "expected a variable, not, or right bracket";
		final static String middleExceptionMessage = "expected an operator";
		final static String middleBracketsExceptionMessage = "expected an operator or left bracket";

		enum ExpressionZone {
			START,		// Start or of expression - or end of one, to start a new one
			MIDDLE,		// After right bracket or variable
			ANY,		//
			NOWHERE;	//
			public boolean inZoneOf(TokenType.ExpressionZone other) {
				return this == other || other == ANY || this == ANY;
			}
		}

	}

	// public static Equation parseEquation(String str) {
	// 	// if (str.substring(0, 8).equalsIgnoreCase("argument"))
	// 	// 	str = str.substring(7);
	// 	return null;
	// }

	public static Expression parseExpression(String str) {
		str = str.trim();
		if (str.length() == 0)
			return Expression.empty();

		final Map<String, Variable> localVariables = new HashMap<>();

		return parseExpression(tokenizeStatement(str), localVariables, 0).get(0);
	}

	// private static record StatementEnd(Expression statement, int last) { }

	private static List<Expression> parseExpression(List<StringPart> tokens, final Map<String, Variable> localVariables, int start) {
		boolean inner = start != 0;
		TokenType.ExpressionZone expectingZone = START;
		ExpressionBuilder expBuilder = new ExpressionBuilder();
		for (int i = start; i < tokens.size(); i++) {
			TokenType currentTokenType = tokens.get(i).tokenType();
			TokenType.ExpressionZone nextProceedingZone = currentTokenType.proceedingZone;
			if (!currentTokenType.expressionZone.inZoneOf(expectingZone)) {
				// if (currentTokenType == RIGHT_IDENTIFIER && tokens.get(i - 1))
				throw new ParseException("unexpected token %s at index %d", tokens.get(i).string(), tokens.get(i).from());
			} else {
				String tokenString = tokens.get(i).string();
				switch (currentTokenType) {		// NO FALL THROUGH
					case LEFT_IDENTIFIER:
						List<Expression> innerExpressions = parseExpression(tokens, localVariables, i + 1);
						expBuilder.add(innerExpressions.get(0)); break;
					case RIGHT_IDENTIFIER:
						if (inner) {
							tokens.subList(start, i + 1).clear();
							try {
								return Collections.singletonList(expBuilder.build());
							} catch (IllegalArgumentAmountException e) {
								throw new ParseException("illegal amount of arguments near %s near index %d: %s",
														 tokens.get(i).string(), tokens.get(i).from(), e.getMessage());
							}
						} else
							throw new ParseException("unbalanced right bracket \"%s\" at index %d", tokens.get(i).string(), tokens.get(i).from());
					case OPERATOR:
						expBuilder.add(statementOperators.get(tokenString)); break;
					case FUNCTION:
						expBuilder.add(statementLineOperators.get(tokenString)); break;
					case COMMA:
						parseExpression(tokens, localVariables, i + 1); // TODO
						// expBuilder.add()
					case VARIABLE:
						expBuilder.add(localVariables.computeIfAbsent(tokenString, Variable::new)); break;
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
			throw new ParseException("unfinished expression at the end");
		try {
			return Collections.singletonList(expBuilder.build());
		} catch (IllegalArgumentAmountException e) {
			throw new ParseException("illegal amount of arguments in the last function: " + e.getMessage());
		}
	}

	private static List<StringPart> tokenizeStatement(String str) {
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

}
