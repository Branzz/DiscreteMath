package bran.lattice.vector;

import bran.tree.compositions.Composition;
import bran.tree.compositions.expressions.Expression;
import bran.tree.compositions.expressions.values.Variable;
import bran.tree.structure.MonoTypeFork;

import java.util.Set;

public class VectorOperation extends Expression implements MonoTypeFork<Expression, Vector, VectorOperator, VectorOperation> {

	Vector left;
	VectorOperator operator;
	Vector right;

	public VectorOperation(Vector left, VectorOperator operator, Vector right) {
		super();
		this.left = left;
		this.operator = operator;
		this.right = right;
	}

	public static final VectorOperation DUMMY = new VectorOperation(null, null, null);

	public TriFunction<Vector, VectorOperator, Vector, VectorOperation> getter() {
		return VectorOperation::new;
	}

	@Override
	public Vector getLeft() {
		return left;
	}

	@Override
	public VectorOperator getOperator() {
		return operator;
	}

	@Override
	public Vector getRight() {
		return right;
	}
	@Override public void replaceAll(Composition original, Composition replacement) { }
	@Override public Expression simplified() { return null; }
	@Override public Set<Variable> getVariables() { return null; }
	@Override public double evaluate() { return 0; }
	@Override public Expression derive() { return null; }
	@Override public boolean equals(Object other) { return false; }

	@Override
	public String toFullString() {
		return null;
	}



}
