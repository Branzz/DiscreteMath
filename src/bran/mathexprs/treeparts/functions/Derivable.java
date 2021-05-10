package bran.mathexprs.treeparts.functions;

import bran.mathexprs.treeparts.Expression;
import bran.mathexprs.treeparts.Variable;

import java.util.Collection;

@FunctionalInterface
interface Derivable {
	Expression derive(Expression... exp);
}
