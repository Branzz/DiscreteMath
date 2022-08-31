package bran.tree.compositions.sets;

public class EmptySet<E> implements Set<E> {

	@Override
	public boolean subsetImpl(Set<E> s) {
		return true;
	}

	@Override
	public boolean properSubsetImpl(Set<E> s) {
		return true;
	}

	@Override
	public boolean equivalentImpl(Set<E> other) {
		return false; // TODO
	}

	@Override
	public boolean containsImpl(E e) {
		return false;
	}

	@Override
	public Set<E> complementImpl() {
		return null;
	}

	@Override
	public Set<E> intersectionImpl(Set<E> s) {
		return new EmptySet<>();
	}

	@Override
	public Set<E> unionImpl(Set<E> s) {
		return s;
	}

	@Override
	public Set<E> symmetricDifferenceImpl(Set<E> s) {
		return s;
	}

}
