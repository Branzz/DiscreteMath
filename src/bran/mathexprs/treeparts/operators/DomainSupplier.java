package bran.mathexprs.treeparts.operators;

import bran.logic.statements.Statement;
import bran.mathexprs.treeparts.Constant;
import bran.mathexprs.treeparts.Expression;

@FunctionalInterface
interface DomainSupplier {

	Statement get(Expression left, Expression right);

	DomainSupplier DENOM_NOT_ZERO = (l, r) -> r.notEquates(Constant.ZERO);

}
