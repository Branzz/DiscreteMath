package bran.tree.compositions.sets.regular;

import bran.tree.Holder;
import bran.tree.compositions.sets.Set;
import bran.tree.compositions.statements.Statement;
import bran.tree.structure.Leaf;

public class SetElement<T> implements Leaf, Holder<T> {

	// TODO namespaces
	private final String name;
	private final T realValue;

	public SetElement(String name, T realValue) {
		this.name = name;
		this.realValue = realValue;
	}

	public SetElement(T realValue) {
		this("?", realValue);
	}

	public SetElement(String name) {
		this(name, null);
	}

	public static <T0> SetElement<T0> of(String name) {
		return new SetElement<>(name);
	}

	public Boolean elementOfSet(Set<SetElement<T>> s) {
		return s.containsImpl(this);
	}

	public Boolean notElementOfSet(Set<SetElement<T>> s) {
		return s.notContainsImpl(this);
	}

	public Statement elementOf(Set<SetElement<T>> s) {
		return s.containsElement(this);
	}

	public Statement notElementOf(Set<SetElement<T>> s) {
		return s.notContains(this);
	}

	public String getName() {
		return name;
	}

	public T value() {
		return realValue;
	}

	@Override
	public T get() {
		return value();
	}

	@Override
	public void set(T t) {
		// TODO
	}

}
