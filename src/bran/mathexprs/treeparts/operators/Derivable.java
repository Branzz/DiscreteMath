package bran.mathexprs.treeparts.operators;

import bran.mathexprs.treeparts.Expression;

@FunctionalInterface
interface Derivable {
	Expression derive(Expression left, Expression right);
}
