package bran.tree;

public class Var<E> implements Holder<E> {
	String name;
	E e;

	public Var(String name) { this.name = name; }
	public Var(E e) { this.e = e; }

	@Override public E get() { return e; }
	@Override public void set(E e) { this.e = e; }
	public static <E> Var<E> of(String name) { return new Var<>(name); }

}
