package bran.tree.compositions.sets;

import bran.tree.compositions.Composition;
import bran.tree.compositions.godel.GodelBuilder;
import bran.tree.compositions.statements.Statement;
import bran.tree.compositions.statements.VariableStatement;
import bran.tree.structure.MonoTypeFork;

import java.util.List;

public class SetStatement<E> extends Statement implements MonoTypeFork<Boolean, Set, SetStatementOperator> {

	private final Set<E> left;
	private final SetStatementOperator operator;
	private final Set<E> right;

	public SetStatement(Set<E> left, SetStatementOperator operator, Set<E> right) {
		this.left = left;
		this.operator = operator;
		this.right = right;
	}

	@Override
	public Set<E> getLeft() {
		return left;
	}

	@Override
	public SetStatementOperator getOperator() {
		return operator;
	}

	@Override
	public Set<E> getRight() {
		return right;
	}

	@Override
	public boolean equals(Object s) {
		return false;
	}

	@Override
	protected boolean getTruth() {
		return operator.operate(left, right);
	}

	@Override
	public String toFullString() {
		return null;
	}

	@Override
	public String toString() {
		return left + " " + operator + " " + right;
	}

	@Override
	public List<VariableStatement> getVariables() {
		return null;
	}

	@Override
	public Statement simplified() {
		return null;
	}

	@Override
	public List<Set> getChildren() {
		return (List<Set>) MonoTypeFork.super.getChildren();
	}

	@Override
	public void appendGodelNumbers(GodelBuilder godelBuilder) {

	}

}
