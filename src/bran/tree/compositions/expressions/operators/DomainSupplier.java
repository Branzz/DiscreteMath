package bran.tree.compositions.expressions.operators;

import bran.tree.compositions.statements.Statement;
import bran.tree.compositions.expressions.values.Constant;
import bran.tree.compositions.expressions.Expression;

import static bran.tree.compositions.expressions.Expression.defaultConditions;

@FunctionalInterface
interface DomainSupplier {

	Statement get(Expression left, Expression right);

	DomainSupplier DENOM_NOT_ZERO = (l, r) -> r.notEquates(Constant.ZERO);

}
