package bran.tree.compositions.expressions.operators;

import bran.tree.compositions.expressions.Expression;

@FunctionalInterface
interface OperatorDerivable {
	Expression derive(Expression left, Expression right);
}
