package bran.tree.compositions.expressions;

import bran.tree.compositions.Composition;
import bran.tree.compositions.expressions.values.Variable;
import bran.tree.compositions.godel.GodelBuilder;

import java.util.Collection;
import java.util.Set;
public class Vector extends Expression {

	@Override
	public Expression simplified() {
		return null;
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
	public void replaceAll(Composition original, Composition replacement) {

	}

	@Override
	public boolean respect(Collection<Variable> respectsTo) {
		return false;
	}

	@Override
	public void appendGodelNumbers(GodelBuilder godelBuilder) {

	}

	@Override
	public boolean equals(Object other) {
		return false;
	}

	@Override
	public String toFullString() {
		return null;
	}

}
