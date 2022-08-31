package bran.tree.compositions.expressions.functions;

import bran.exceptions.IllegalArgumentAmountException;
import bran.tree.structure.TreePart;
import bran.tree.structure.mapper.AssociativityPrecedenceLevel;

import java.util.function.Function;

public class TreeFunction implements AbstractFunction<TreePart> {

	int argAmount;
	private final Function<TreePart[], TreePart> function;

	public TreeFunction(Function<TreePart[], TreePart> function) {
		this.function = function;
	}

	@Override
	public TreePart function(final TreePart[] a) {
		AbstractFunction.super.function(a);
		try {
			return function.apply(a);
		} catch (ClassCastException e) {
			throw new IllegalArgumentAmountException("wrong type inputs: " + e.getMessage());
		}
	}

	@Override
	public int getArgAmount() {
		return argAmount;
	}

	@Override
	public void checkArguments(int length) throws IllegalArgumentAmountException {
		checkArguments(length, argAmount);
	}

	@Override
	public String[] getSymbols() {
		return new String[0];
	}

	@Override
	public AssociativityPrecedenceLevel level() {
		return AssociativityPrecedenceLevel.of(1);
	}

}
