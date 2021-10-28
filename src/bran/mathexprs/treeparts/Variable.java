package bran.mathexprs.treeparts;

import bran.sets.numbers.godel.GodelNumber;
import bran.sets.numbers.godel.GodelVariableMap;
import bran.tree.Holder;
import bran.sets.numbers.NumberLiteral;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Variable extends Value implements Holder<NumberLiteral> {

	private final String name; // NOT AN ID FOR EXPRESSIONS / Maybe for Logic statements.
	private boolean respects;
	private Differential differential;
	protected int level = 0; // 1 for first derivative, 2 for second, etc.

	private final static Matcher variableMatcher = Pattern.compile("[A-Za-z][A-Za-z_\\d]*").matcher("");

	public Variable(final String name, final boolean respects) {
		super();
		this.name = name;
		this.respects = respects;
	}

	public Variable(final String name) {
		this(name, true);
	}

	public Variable(final Character name) {
		this(String.valueOf(name));
	}

	public static boolean validName(final String prefix) {
		return variableMatcher.reset(prefix).matches();
	}

	public String getName() {
		return name;
	}

	public void setValue(final double value) {
		number = new NumberLiteral(value);
	}

	@Override
	public Set<Variable> getVariables() {
		return Collections.singleton(this);
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
			return Constant.ONE;
			// return getDifferential();
		else
			return Constant.ZERO;
	}

	@Override
	public String toFullString() {
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

	public boolean parseEquals(final Variable v) {
		return name.equals(v.name);
	}

	@Override
	public void appendGodelNumbers(final Stack<GodelNumber> godelNumbers, final GodelVariableMap variables) {
		godelNumbers.push(variables.get(this, true));
	}

}
