package bran.mathexprs.treeparts;

import bran.tree.Holder;
import bran.sets.numbers.NumberLiteral;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Variable extends Value implements Holder<NumberLiteral> {

	private final String name; // NOT AN ID FOR EXPRESSIONS / Maybe for Logic statements.
	private boolean respects;
	private Differential differential;
	protected int level = 0; // 1 for first derivative, 2 for second, etc.

	public Variable(final String name) {
		this(name, true);
	}

	public Variable(final String name, final boolean respects) {
		super();
		this.name = name;
		this.respects = respects;
	}

	public String getName() {
		return name;
	}

	public void setValue(final double value) {
		number = new NumberLiteral(value);
	}

	@Override
	public List<Variable> getVariables() {
		return Collections.singletonList(this);
	}

	@Override
	public Expression simplified() {
		return this;
	}

	public void respect() {
		respects = true;
	}

	protected Differential getDifferential() {
		if (differential == null)
			differential = new Differential(this, level + 1);
		return differential;
	}

	@Override
	public boolean respect(final Collection<Variable> respectsTo) {
		return respectsTo.contains(this);
	}

	// @Override
	// public Expression clone() {
	// 	return new Variable(name);
	// }

	@Override
	public boolean equals(final Object o) {
		return this == o;
	}

	@Override
	public Expression derive() {
		if (respects)
			return getDifferential();
		else
			return new Constant(0.0D);
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public NumberLiteral get() {
		return number;
	}

	@Override
	public void set(final NumberLiteral number) {
		this.number = number;
	}

}
