package bran.parser;

import bran.exceptions.ParseException;
import bran.tree.compositions.statements.special.proofs.Argument;
import bran.tree.compositions.statements.*;
import bran.tree.compositions.statements.operators.LineOperator;
import bran.tree.compositions.statements.operators.LogicalOperator;

import java.util.*;

import static bran.parser.CompositionParser.*;
import static bran.parser.StatementParser.TokenType.*;
import static bran.parser.StatementParser.TokenType.ExpressionZone.*;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

public class StatementParser {

	final record StringPart(String string, int from, int to, TokenType tokenType) { }
	final record TokenPart(boolean splittable, List<StringPart> prefixes) {	}

	// private static final String operatorsRegex = Arrays.stream(Operator.values()).flatMap(o -> Arrays.stream(o.getSymbols())).distinct().collect(joining("|"));
	// private static final String lineOperatorsRegex = Arrays.stream(LineOperator.values()).flatMap(o -> Arrays.stream(o.getSymbols())).distinct().collect(joining("|"));
	// public static final Matcher statementMatcher = Pattern.compile("n\\(x\\)|n\\[x]|n\\{x}|nx"
	// 		.replaceAll("x", "\\s*\\(" + lineOperatorsRegex + "\\)\\s+)*[A-Za-z][A-Za-z_\\d]*\\s+\\(" + operatorsRegex + "\\)\\s*X")
	// 		.replaceAll("n", "(\\(" + lineOperatorsRegex + "\\)\\s+)*\\((" + lineOperatorsRegex + "\\)\\s*)?")).matcher("");

	static final Map<String, LogicalOperator> statementOperators = Parser.getSymbolMapping(LogicalOperator.values());
	static final Map<String, LineOperator> statementLineOperators = Parser.getSymbolMapping(LineOperator.values());

	enum TokenType {

		LINE_OPERATOR(START, START), OPERATOR(MIDDLE, START), LEFT_IDENTIFIER(START, MIDDLE), // becomes middle after the (expression) is simplified
		RIGHT_IDENTIFIER(MIDDLE, MIDDLE), VARIABLE(START, MIDDLE), WHITESPACE(ANY, NOWHERE), // the previous type
		UNKNOWN(NOWHERE, NOWHERE);

		private final ExpressionZone currentZone;
		private final ExpressionZone proceedingZone; // zone after current one

		TokenType(ExpressionZone currentZone, ExpressionZone proceedingZone) {
			this.currentZone = currentZone;
			this.proceedingZone = proceedingZone;
		}

		static TokenType tokenTypeOf(String prefix) {
			return statementOperators.containsKey(prefix) ? OPERATOR
			: statementLineOperators.containsKey(prefix) ? LINE_OPERATOR
			: leftIdentifiers.contains(prefix) ? LEFT_IDENTIFIER
			: rightIdentifiers.contains(prefix) ? RIGHT_IDENTIFIER
			: VariableStatement.validName(prefix) ? VARIABLE
			: prefix.isBlank() ? WHITESPACE
			: UNKNOWN;
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

	public static Argument parseArgument(String str) {
		// if (str.substring(0, 8).equalsIgnoreCase("argument"))
		// 	str = str.substring(7);
		return null;
	}

	public static Statement parseStatement(String str) {
		str = str.trim();
		if (str.length() == 0)
			return Statement.empty();

		final Map<String, VariableStatement> localVariables = new HashMap<>();

		return parseStatementExpression(tokenizeStatement(str), localVariables, 0);
	}

	private static record StatementEnd(Statement statement, int last) { }

	private static Statement parseStatementExpression(List<StringPart> tokens, final Map<String, VariableStatement> localVariables, int start) {
		boolean inner = start != 0;
		ExpressionZone expectingZone = START;
		StatementBuilder statementBuilder = new StatementBuilder();
		for (int i = start; i < tokens.size(); i++) {
			TokenType currentTokenType = tokens.get(i).tokenType();
			ExpressionZone nextProceedingZone = currentTokenType.proceedingZone;
			if (!currentTokenType.currentZone.inZoneOf(expectingZone)) {
				// if (currentTokenType == RIGHT_IDENTIFIER && tokens.get(i - 1))
				throw new ParseException("statement parsing, unexpected token %s at index %d", tokens.get(i).string(), tokens.get(i).from());
			} else {
				String tokenString = tokens.get(i).string();
				switch (currentTokenType) {
					case LEFT_IDENTIFIER:
 						statementBuilder.add(parseStatementExpression(tokens, localVariables, i + 1));
						// tokens.remove(start);
						break;
					case RIGHT_IDENTIFIER:
						if (inner) { // or if at very end
							tokens.subList(start, i + 1).clear();
							return statementBuilder.build();
						} else
							throw new ParseException("statement parsing, unbalanced right bracket \"%s\" at index %d", tokens.get(i).string(), tokens.get(i).from());
					case OPERATOR:
						statementBuilder.add(statementOperators.get(tokenString)); break;
					case LINE_OPERATOR:
						statementBuilder.add(statementLineOperators.get(tokenString)); break;
					case VARIABLE:
						statementBuilder.add(localVariables.computeIfAbsent(tokenString, VariableStatement::new)); break;
					case UNKNOWN:
						throw new ParseException("statement parsing, unknown token \"%s\" at index %d", tokenString, tokens.get(tokens.size() - 1).from());
					case WHITESPACE:
						assert(i != 0);
						nextProceedingZone = i == start ? MIDDLE // "( a"
											: tokens.get(i - 1).tokenType().proceedingZone;
						break;
					default:
						throw new ParseException("statement parsing, this shouldn't've happened");
				}
				expectingZone = nextProceedingZone;
			}
		}
		if (!expectingZone.inZoneOf(MIDDLE))
			throw new ParseException("statement parsing, unfinished expression at the end");
		return statementBuilder.build();
	}

	// public static Statement parseInnerStatementExpression(List<StringPart> tokens, Map<String, VariableStatement> localVariables, int start) {
	// 	ExpressionZone expectingZone = START;
	// 	StatementBuilder statementBuilder = new StatementBuilder();
	// 	for (int i = start; i < tokens.size(); i++) {
	// 		TokenType currentTokenType = tokens.get(i).tokenType();
	// 		if (!currentTokenType.expressionZone.inZoneOf(expectingZone))
	// 			throw new ParseException("statement parsing, unknown token");
	// 		else {
	// 			String tokenString = tokens.get(i).string();
	// 			switch (currentTokenType) {
	// 				case LEFT_IDENTIFIER:
	// 					statementBuilder.add(parseInnerStatementExpression(tokens, localVariables, i + 1));
	// 					break;
	// 				case RIGHT_IDENTIFIER:
	// 					for (int j = start; j <= i; j++)
	// 						tokens.remove(i);
	// 					return statementBuilder.build();
	// 				case OPERATOR:
	// 					statementBuilder.add(statementOperators.get(tokenString));
	// 				case LINE_OPERATOR:
	// 					statementBuilder.add(statementLineOperators.get(tokenString));
	// 				case VARIABLE:
	// 					statementBuilder.add(localVariables.computeIfAbsent(tokenString, VariableStatement::new));
	// 				case WHITESPACE:
	// 					// continue;
	// 				case UNKNOWN:
	// 					throw new ParseException("statement parsing, unknown token \"%s\" at index %d", tokenString, tokens.get(tokens.size() - 1).from());
	// 			}
	// 			expectingZone = currentTokenType.proceedingZone;
	// 		}
	// 	}
	// 	throw new ParseException("statement parsing, unbalanced right bracket at index %d", tokens.get(tokens.size() - 1).from());
	// }


	private static List<StringPart> tokenizeStatement(String str) {
		TokenPart[] tokenParts = new TokenPart[str.length() + 1];
		tokenParts[0] = new TokenPart(true, Collections.emptyList());
		for (int i = 1; i <= str.length(); i++) {
			List<StringPart> prefixes = new ArrayList<>();
			for (int j = 0; j < i; j++) {
				if (tokenParts[j].splittable()) {
					String prefix = str.substring(j, i);
					TokenType currentTokenType = tokenTypeOf(prefix);
					if (currentTokenType != UNKNOWN) { // valid
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
		else if (lastParts.get(lastParts.size() - 1).tokenType == UNKNOWN)
			throw new ParseException("unknown token: %s at index %d",
									 lastParts.get(lastParts.size() - 1).string(), lastParts.get(lastParts.size() - 1).from());

		return lastParts;
	}

	public static Statement parseWithRegex(String str) {
		return null;
	}

}
