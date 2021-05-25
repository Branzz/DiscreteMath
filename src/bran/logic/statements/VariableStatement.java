package bran.logic.statements;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bran.sets.numbers.godel.GodelNumber;
import bran.sets.numbers.godel.GodelVariableMap;
import bran.tree.Holder;
import bran.tree.Leaf;

public class VariableStatement extends Statement implements Leaf, Holder<Boolean> { // No Child

	protected String name;
	protected boolean value;
	protected boolean constant;

	private final static Matcher variableMatcher = Pattern.compile("[A-Za-z][A-Za-z_\\d]*").matcher("");

	public static final VariableStatement TAUTOLOGY = new VariableStatement("t", true, true);
	public static final VariableStatement CONTRADICTION = new VariableStatement("c", false, true);

	public VariableStatement(String name) {
		this.name = name;
		constant = name.equals("t") || name.equals("c"); // tautology contradiction
		value = constant && name.equals("t");
	}

	public VariableStatement(final char charName) {
		this.name = String.valueOf(charName);
		constant = charName == 't' || charName == 'c'; // tautology contradiction
		value = constant && charName == 't';
	}

	public VariableStatement(String name, boolean value) {
		this.name = name;
		constant = name.equals("t") || name.equals("c");
		this.value = constant ? name.equals("t") : value;
	}

	private VariableStatement(String name, boolean value, boolean constant) {
		this.name = name;
		this.value = value;
		this.constant = constant;
	}

	public static boolean validName(final String prefix) {
		return variableMatcher.reset(prefix).matches();
	}

	public static Statement of(final boolean truth) {
		return truth ? TAUTOLOGY : CONTRADICTION;
	}

	public void setValue(boolean value) {
//		if (!constant)
		this.value = value;
	}

	public boolean isConstant() {
		return constant;
	}

	public boolean equals(Object s) {
		return this == s || (s instanceof VariableStatement && ((VariableStatement) s).name.equals(name));
	}

	@Override
	public Statement simplified() {
		return this;
	}

	@Override
	public void appendGodelNumbers(final Stack<GodelNumber> godelNumbers, final GodelVariableMap variables) {
		godelNumbers.push(variables.get(this, false));
	}

	public boolean getTruth() {
		return value;
	}

	public List<Statement> getChildren() {
		return new ArrayList<>();
	}

	public List<VariableStatement> getVariables() {
		ArrayList<VariableStatement> variable = new ArrayList<>();
		variable.add(this);
		return variable;
	}

// 	public VariableStatement clone() {
// 		return this;
// //		return new VariableStatement(name, value);
// 	}

	@Override
	public boolean equivalentTo(final Statement other) {
		return value == other.getTruth();
	}

	@Override
	public Boolean get() {
		return value;
	}

	@Override
	public void set(final Boolean value) {
		this.value = value;
	}

	public String toString() {
		return name;
	}

}
