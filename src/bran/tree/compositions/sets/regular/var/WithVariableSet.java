package bran.tree.compositions.sets.regular.var;

import bran.tree.Holder;
import bran.tree.compositions.expressions.values.numbers.NumberLiteral;
import bran.tree.compositions.sets.regular.FiniteSet;

import java.util.Arrays;
import java.util.Collection;

public class WithVariableSet<E, V extends Holder<E>> extends FiniteSet<Object> {

	private final Class<E> eClass;
	private final Class<V> vClass;

	public WithVariableSet(Class<E> eClass, Class<V> vClass, Object... evs) {
		super();
		this.eClass = eClass;
		this.vClass = vClass;
		for (Object o : evs) {
			try {
				this.add(new NumberLiteral((Number) o));
			} catch (ClassCastException e) {
				this.clear();
				this.addAll(Arrays.asList(evs));
				break;
			}
		}
		// if (evs instanceof Double[] ds) {
		// 	this.addAll(NumberLiteral.of(ds));
		// } else
		// this.addAll(Arrays.asList(evs));
	}

	// public WithVariableSet(Class<E> eClass, Class<V> vClass, E... es) {
	// 	super(es);
	// 	this.eClass = eClass;
	// 	this.vClass = vClass;
	// }
	//
	// public WithVariableSet(Class<E> eClass, Class<V> vClass, V... vs) {
	// 	super(vs);
	// 	this.eClass = eClass;
	// 	this.vClass = vClass;
	// }

	public WithVariableSet(E... es) { // immutable
		super(es);
		this.eClass = null;
		this.vClass = null;
	}

	public WithVariableSet(double... ds) { // immutable
		super(Arrays.stream(ds).mapToObj(NumberLiteral::new).toArray(NumberLiteral[]::new));
		this.eClass = null;
		this.vClass = null;
	}

	public WithVariableSet(V... vs) {
		super(vs);
		this.eClass = null;
		this.vClass = null;
	}

	private void CheckCanContain(Object o) {
		if (eClass == null || vClass == null)
			throw new UnsupportedOperationException();
		if (!o.getClass().isAssignableFrom(eClass) && !o.getClass().isAssignableFrom(vClass))
			throw new ClassCastException();
	}

	@Override
	public boolean add(Object o) {
		CheckCanContain(o);
		return super.add(o);
	}

	@Override
	public boolean remove(Object o) {
		CheckCanContain(o);
		return super.remove(o);
	}

	@Override
	public boolean addAll(Collection<?> c) {
		c.forEach(this::CheckCanContain);
		return super.addAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		c.forEach(this::CheckCanContain);
		return super.removeAll(c);
	}

}
