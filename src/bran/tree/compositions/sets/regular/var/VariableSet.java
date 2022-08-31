package bran.tree.compositions.sets.regular.var;

import bran.tree.Holder;
import bran.tree.compositions.sets.regular.FiniteSet;

import java.util.HashMap;
import java.util.Map;

public class VariableSet<E, V extends Holder<E>> extends FiniteSet<E> {

	private Map<V, E> holderMap;

	public VariableSet(E... es) {
		super(es);
		this.holderMap = new HashMap<>();
	}

	public VariableSet(V... vs) {
		this();
		for (V v : vs) {
			holderMap.put(v, v.get());
		}
	}

	public VariableSet() {
		super();
		this.holderMap = new HashMap<>();
	}

	@Override
	public boolean contains(Object o) {
		return super.contains(o) || holderMap.containsValue(o);
	}

	// @Override
	// public Statement containsElement(E e) {
	// 	return new ElementSetStatement<>(this, CONTAINS_ELEMENT(), e);
	// }

	// @Override
	// public Statement containsElement(V v) {
	// 	return new ElementSetStatement<V>((Set<V>) this, CONTAINS_ELEMENT(), v); // TODO variable accessor special Set Operator
	// }

	public boolean add(V v) {
		holderMap.put(v, v.get());
		return true;
	}

	@Override
	public void clear() {
		holderMap.clear();
		super.clear();
	}

}
