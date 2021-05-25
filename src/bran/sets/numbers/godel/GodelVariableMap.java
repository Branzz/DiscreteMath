package bran.sets.numbers.godel;

import bran.mathexprs.treeparts.Variable;
import bran.tree.Holder;

import java.util.HashMap;
import java.util.Map;
public class GodelVariableMap {

	private final Map<Holder<?>, GodelVariable> variables;
	private int variableTypeAmount;
	private int propertyTypeAmount;

	public GodelVariableMap() {
		variables = new HashMap<>();
		variableTypeAmount = 0;
		propertyTypeAmount = 0;
	}

	public GodelVariable get(Holder<?> key, boolean isVariableType) {
		return variables.computeIfAbsent(key, k -> new GodelVariable(isVariableType ? variableTypeAmount++ : propertyTypeAmount++, isVariableType));
	}

	public GodelVariable get(Holder<?> key) {
		return get(key, key instanceof Variable);
	}

}
