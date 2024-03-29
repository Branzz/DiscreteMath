package bran.tree;

/**
 * Holds a <b>mutable</b> type, E, that can be changed. Used like a variable, where
 * a variable isn't necessarily a number, but can be thought of a hypothetical one.
 * @param <E>
 */
public interface Holder<E> { // TODO extends HolderFinal

	E get();

	void set(E e);

}
