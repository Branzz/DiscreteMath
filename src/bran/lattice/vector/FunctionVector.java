package bran.lattice.vector;

import bran.tree.compositions.Composition;
import bran.tree.compositions.expressions.Expression;
import bran.tree.compositions.expressions.values.Variable;
import bran.tree.structure.MultiBranch;
import bran.tree.structure.TreePart;
import bran.tree.structure.mapper.Mapper;

import java.util.List;
import java.util.Set;

public class FunctionVector extends Expression implements MultiBranch<VectorBranchFunction, Vector> {

	private final VectorBranchFunction function;
	private final Vector arg;

	public FunctionVector(VectorBranchFunction function, Vector arg) {
		this.function = function;
		this.arg = arg;
	}

	@Override
	public VectorBranchFunction getFunction() {
		return function;
	}

	@Override
	public void replaceAll(Composition original, Composition replacement) {

	}

	@Override
	public Expression simplified() {
		return function.operate(arg).simplified();
	}

	@Override
	public Set<Variable> getVariables() {
		return null;
	}

	@Override
	public double evaluate() {
		return 0;
	}

	@Override
	public Expression derive() {
		return null;
	}

	@Override
	public boolean equals(Object other) {
		return false;
	}

	@Override
	public String toFullString() {
		return null;
	}

	@Override
	public List<? super Vector> getChildren() {
		return null;
	}

}
