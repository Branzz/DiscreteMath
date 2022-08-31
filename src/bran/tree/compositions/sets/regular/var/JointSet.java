package bran.tree.compositions.sets.regular.var;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public interface JointSet<E, H> extends Set<Object> {

	Set<E> set1();
	Set<H> set2();

	// public JointSet(Set<E> set1(), Set<H> set1()) {
	// 	this.set1() = set1();
	// 	this.set1() = set1();
	// }
	//
	// public JointSet() {
	// 	this(new HashSet<>(), new HashSet<>());
	// }

	@Override
	default int size() {
		return set1().size() + set2().size();
	}

	@Override
	default boolean isEmpty() {
		return set1().isEmpty() && set2().isEmpty();
	}

	@Override
	default boolean contains(Object o) {
		return set1().contains(o) || set2().contains(o);
	}

	@NotNull
	@Override
	default Iterator<Object> iterator() {
		return new JointIterator<>(set1().iterator(), set2().iterator());
	}

	@NotNull
	@Override
	default Object @NotNull [] toArray() {
		final Object[] array1 = set1().toArray();
		final Object[] array2 = set2().toArray();
		final Object[] array = new Object[array1.length + array2.length];
		System.arraycopy(array1, 0, array, 0, array1.length);
		System.arraycopy(array2, array1.length, array, array1.length, array2.length);
		return array;
	}

	@NotNull
	@Override
	default <T> T[] toArray(@NotNull T[] a) {
		return null;
	}

	@Override
	default boolean add(Object o) {
		return false;
	}

	@Override
	default boolean remove(Object o) {
		return false;
	}

	@Override
	default boolean containsAll(@NotNull Collection<?> c) {
		return false;
	}

	@Override
	default boolean addAll(@NotNull Collection<?> c) {
		return false;
	}

	@Override
	default boolean retainAll(@NotNull Collection<?> c) {
		return false;
	}

	@Override
	default boolean removeAll(@NotNull Collection<?> c) {
		return false;
	}

	@Override
	default void clear() {
		set1().clear();
		set2().clear();
	}

}
