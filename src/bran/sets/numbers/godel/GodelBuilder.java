package bran.sets.numbers.godel;

import bran.mathexprs.treeparts.Variable;
import bran.tree.Holder;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class GodelBuilder {

	private final Stack<GodelNumber> godelNumbers;
	private final Map<Holder<?>, GodelVariable> variables;
	private int variableTypeAmount;
	private int propertyTypeAmount;

	public GodelBuilder() {
		godelNumbers = new Stack<>();
		variables = new HashMap<>();
		variableTypeAmount = 0;
		propertyTypeAmount = 0;
	}

	public Stack<GodelNumber> getNumbers() {
		return godelNumbers;
	}

	public GodelNumber push(final GodelNumber symbol) {
		return godelNumbers.push(symbol);
	}

	public GodelVariable getVar(Holder<?> key, boolean isVariableType) {
		return variables.computeIfAbsent(key, k -> new GodelVariable(isVariableType ? variableTypeAmount++ : propertyTypeAmount++, isVariableType));
	}

	public GodelVariable getVar(Holder<?> key) {
		return getVar(key, key instanceof Variable);
	}

}
