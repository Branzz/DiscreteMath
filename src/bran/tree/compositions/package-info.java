/**
 * <pre>
 * Composition Tree Relations:
 *
 * Naming	Type:	Statement		Expression		Set
 *
 * Actual		Boolean			Double			Set
 * ForkOperator		LogicalOperator		ArithmeticOperator	SetOperator
 * Fork			StatementOperation	ExpressionOperation	SetOperation
 * Mapper		UnaryStatementOperator	ExpFunction		UnarySetOperator
 * Branch		UnaryStatement		FunctionExpression	UnarySet
 * Holder		VariableStatement	Variable
 * AsStatement?					Equivalence		SetStatement
 * </pre>
 */
package bran.tree.compositions;
