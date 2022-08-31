package bran.tree.compositions.sets.regular;

import bran.tree.compositions.expressions.values.numbers.NumberLiteral;

import java.util.Arrays;

public class FiniteNumberSet extends FiniteSet<NumberLiteral> {

	public FiniteNumberSet(NumberLiteral... numberLiterals) {
		super(numberLiterals);
	}

	public FiniteNumberSet(double... nums) {
		super(Arrays.stream(nums).mapToObj(NumberLiteral::new).toArray(NumberLiteral[]::new));
	}

}
