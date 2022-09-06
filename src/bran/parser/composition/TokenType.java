package bran.parser.composition;

import bran.parser.Parser;
import bran.tree.compositions.expressions.functions.MultiArgFunction;
import bran.tree.compositions.expressions.operators.ArithmeticOperator;
import bran.tree.compositions.expressions.values.Constant;
import bran.tree.compositions.expressions.values.Variable;
import bran.tree.compositions.statements.operators.LogicalOperator;
import bran.tree.compositions.statements.operators.UnaryStatementOperator;
import bran.tree.compositions.statements.special.equivalences.EquivalenceTypeImpl;
import bran.tree.structure.mapper.Mapper;
import bran.tree.structure.mapper.OrderedOperator;

import java.util.Map;
import java.util.function.Function;

import static bran.parser.composition.CompositionType.*;
import static bran.parser.composition.OrderZone.*;

public enum TokenType {
	LEFT_IDENTIFIER(START, START, BOTH, r -> "("), // becomes middle after the (expression) is simplified
	FUNCTION(START, START, true, EXPRESSION, MultiArgFunction.class),
	LINE_OPERATOR(START, START, true, STATEMENT, UnaryStatementOperator.class),
	VARIABLE(START, MIDDLE, BOTH, r -> String.valueOf((char) ('a' + (int) (r * 3)))),
	CONSTANT(START, MIDDLE, EXPRESSION, r -> r > .2 ? String.valueOf((int) ((r - .8) * 50 - 5)) : String.valueOf(r * 12.5 - 5)																																																																						.substring(0, 5)), COMMA(MIDDLE, START, BOTH, r -> ","), EQUIVALENCE(MIDDLE, START, true, STATEMENT, EquivalenceTypeImpl.class), EXP_OPERATOR(MIDDLE, START, true, EXPRESSION, ArithmeticOperator.class), SMT_OPERATOR(MIDDLE, START, true, STATEMENT, LogicalOperator.class), RIGHT_IDENTIFIER(MIDDLE, MIDDLE, BOTH, r -> ")"), WHITESPACE(ANY, NOWHERE, BOTH, r -> " "), // the previous type
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
		this.generator = r -> String.valueOf(CompositionParser.randElement(r, tokenMap.keySet()
																					  .toArray()));
		this.associatedClass = associatedClass;
	}

	public boolean isMapperType() {
		return mapperType;
	}

	static TokenType getPrefixFromMap(String prefix) {
		for (TokenType tokenType: values()) {
			if (tokenType.tokenMap != null && tokenType.tokenMap.containsKey(prefix))
				return tokenType;
		}
		return UNKNOWN;
	}

	public static TokenType tokenTypeOf(String prefix) {
		final Map.Entry<Mapper, TokenType> tokenInfo = CompositionParser.symbolTokens.get(prefix);
		if (tokenInfo != null)
			return tokenInfo.getValue();
		return prefix.equals(",") ? COMMA : prefix.isBlank()
								  ? WHITESPACE : CompositionParser.leftIdentifiers.contains(prefix)
								  ? LEFT_IDENTIFIER : CompositionParser.rightIdentifiers.contains(prefix)
								  ? RIGHT_IDENTIFIER
						// : expressionOperators.containsKey(prefix) ? EXP_OPERATOR
						// : statementOperators.containsKey(prefix) ? SMT_OPERATOR
						// : expressionLineOperators.containsKey(prefix) ? FUNCTION
						// : statementLineOperators.containsKey(prefix) ? LINE_OPERATOR
						// : equivalenceOperators.containsKey(prefix) ? EQUIVALENCE
						: Constant.validName(prefix)
								  ? CONSTANT : Variable.validName(prefix)
								  ? VARIABLE : getPrefixFromMap(prefix);
	}

	public OrderZone currentZone() {
		return currentZone;
	}

	public OrderZone proceedingZone() {
		return proceedingZone;
	}

	public Function<Double, String> generator() {
		return generator;
	}

	public Class<? extends OrderedOperator> associatedClass() {
		return associatedClass;
	}

}
