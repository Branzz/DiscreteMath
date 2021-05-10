package bran.mathexprs.treeparts.functions;

import bran.logic.statements.Statement;
import bran.logic.tree.Operator;
import bran.mathexprs.treeparts.Expression;
import bran.mathexprs.treeparts.Variable;

import java.util.Collection;

public interface Function extends Operator {

	double function(double... a);

	Expression derive(Expression... exp);

	Statement domain(Expression... expressions);

	void checkArguments(int length) throws IllegalArgumentAmountException;

}
