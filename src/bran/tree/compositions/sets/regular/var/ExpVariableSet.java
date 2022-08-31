package bran.tree.compositions.sets.regular.var;

import bran.tree.compositions.expressions.values.Variable;
import bran.tree.compositions.expressions.values.numbers.NumberLiteral;

import java.util.Arrays;

public class ExpVariableSet extends VariableSet<NumberLiteral, Variable> {

	public ExpVariableSet(NumberLiteral... nums) {
		super(nums);
	}

	public ExpVariableSet(Variable... vars) {
		super(vars);
	}

	public ExpVariableSet(double... nums) {
		this(Arrays.stream(nums).mapToObj(NumberLiteral::new).toArray(NumberLiteral[]::new));
	}

}
