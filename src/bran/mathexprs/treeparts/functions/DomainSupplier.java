package bran.mathexprs.treeparts.functions;

import bran.logic.statements.Statement;
import bran.mathexprs.treeparts.Constant;
import bran.mathexprs.treeparts.Expression;

@FunctionalInterface
interface DomainSupplier {

	Statement get(Expression... expressions);

	DomainSupplier NOT_ZERO = e -> e[0].notEquates(Constant.ZERO);

}
