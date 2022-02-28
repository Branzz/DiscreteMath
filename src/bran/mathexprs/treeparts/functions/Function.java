package bran.mathexprs.treeparts.functions;

import bran.logic.statements.Statement;
import bran.tree.Mapper;
import bran.mathexprs.treeparts.Expression;

public interface Function extends Mapper {

	double function(double... a);

	Expression derive(Expression... exp);

	Statement domain(Expression... expressions);

	void checkArguments(int length) throws IllegalArgumentAmountException;

}
