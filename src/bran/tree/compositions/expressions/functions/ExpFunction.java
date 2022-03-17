package bran.tree.compositions.expressions.functions;

import bran.tree.compositions.statements.Statement;
import bran.exceptions.IllegalArgumentAmountException;
import bran.tree.structure.mapper.Mapper;
import bran.tree.compositions.expressions.Expression;

public interface ExpFunction extends Mapper {

	double function(double... a);

	Expression derive(Expression... exp);

	Statement domain(Expression... expressions);

	void checkArguments(int length) throws IllegalArgumentAmountException;

	ExpFunction inverse();

	// default ExpFunction inverse(int arg) {
	// 	return inverse();
	// }

}
