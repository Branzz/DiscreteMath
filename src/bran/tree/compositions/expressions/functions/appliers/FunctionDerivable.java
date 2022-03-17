package bran.tree.compositions.expressions.functions.appliers;

import bran.tree.compositions.expressions.Expression;

@FunctionalInterface
public interface FunctionDerivable {
	Expression derive(Expression... exp);
}
