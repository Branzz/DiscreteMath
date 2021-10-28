package bran.mathexprs.treeparts.operators;

import bran.logic.statements.Statement;
import bran.mathexprs.treeparts.Constant;
import bran.mathexprs.treeparts.Expression;

import static bran.mathexprs.treeparts.Expression.defaultConditions;

@FunctionalInterface
interface DomainSupplier {

	Statement get(Expression left, Expression right);

	DomainSupplier DENOM_NOT_ZERO = (l, r) -> defaultConditions(l).and(r.notEquates(Constant.ZERO));

}
