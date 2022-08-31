package bran.tree.compositions.sets.regular;

import bran.tree.compositions.godel.GodelBuilder;
import bran.tree.compositions.sets.Set;
import bran.tree.compositions.statements.Statement;
import bran.tree.compositions.statements.VariableStatement;
import bran.tree.structure.Fork;
import bran.tree.structure.TreePart;

import java.util.List;

public class ElementSetStatement<E> extends Statement implements Fork<Boolean, Set<E>, ElementSetRelation<E>, E> {

	private final Set<E> left;
	private final ElementSetRelation<E> operator;
	private final E right;

	public ElementSetStatement(Set<E> left, ElementSetRelation<E> operator, E right) {
		this.left = left;
		this.operator = operator;
		this.right = right;
	}

	@Override
	public Set<E> getLeft() {
		return left;
	}

	@Override
	public ElementSetRelation<E> getOperator() {
		return operator;
	}

	@Override
	public E getRight() {
		return right;
	}

	@Override
	protected boolean getTruth() {
		return false;
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
	public List<Statement> getChildren() {
		return null;
	}

	@Override
	public void appendGodelNumbers(GodelBuilder godelBuilder) {

	}

}
