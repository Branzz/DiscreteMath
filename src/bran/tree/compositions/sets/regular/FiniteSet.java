package bran.tree.compositions.sets.regular;

import bran.tree.compositions.expressions.values.numbers.NumberLiteral;
import bran.tree.compositions.sets.Set;
import bran.tree.structure.Leaf;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class FiniteSet<T> implements Set<T>, Leaf, java.util.Set<T>, java.util.List<T> {

//	private T generic;
	// "..." instantiation
	// set builder notation

	protected final java.util.List<T> set; // visually, finite sets are a list

	public FiniteSet() {
		super();
		set = new ArrayList<>();
	}

	public FiniteSet(List<T> ts) {
		super();
		this.set = ts;
	}

	public FiniteSet(T... ts) {
		super();
		set = new ArrayList<>(Arrays.asList(ts));
	}

	// public FiniteSet(E... es) {
	// 	super();
	// 	set = new HashSet<>();
	// 	Arrays.stream(es)
	// 		  .map(SetElement::new)
	// 		  .map(t -> (T) t)
	// 		  .forEach(set::add);
	// }

	@Override
	public boolean subsetImpl(Set<T> s) {
		if (s instanceof FiniteSet)
			return ((FiniteSet) s).containsAll(this);
		return set.containsAll(this);
////		if (s instanceof FiniteSet)
//			for (Object o0 : this)
//				if (!s.contains(o0))
//					return false;
////		else if (s instanceof SpecialSet) {
////			if (!(generic instanceof Number))
////				for (Object o1 : this)
////					if (!(o1 instanceof Number))
////						return false;
////			for (Object o1 : this)
////				if (!((SpecialSet) s).contains(o1))
////					return false;
////		}
////		else
////			return false;
//		return true;
	}

	@Override
	public boolean properSubsetImpl(Set<T> s) {
		return subsetImpl(s) && !s.subsetImpl(this);
	}

	@Override
	public boolean equivalentImpl(Set<T> other) {
		return false;
	}

	public boolean containsImpl(T e) {
		if (e instanceof NumberLiteral) {
			for (Object o0 : this)
				if (o0 instanceof NumberLiteral)
					if (e.equals(o0))
						return true;
		}
		return set.contains(e);
	}

	@Override
	public Set<T> complementImpl() {
		// special: when this is an empty set or of just 0
		// return new SpecialSet(SpecialSetType.C).symmetricDifferenceImpl(this);

		return null;
	}

	@Override
	public Set<T> intersectionImpl(Set<T> s) {
		return null;
	}

	@Override
	public Set<T> unionImpl(Set<T> s) {
		return null;
	}

	@Override
	public Set<T> symmetricDifferenceImpl(Set<T> s) {
		return null;
	}

	//	@Override
//	public Object clone() {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public int size() {
		return set.size();
	}

	@Override
	public boolean isEmpty() {
		return set.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return containsImpl((T) o);
	}

	@Override
	public Iterator<T> iterator() {
		return set.iterator();
	}

	@Override
	public Object[] toArray() {
		return set.toArray();
	}

	@Override
	public <T1> T1[] toArray(final T1[] a) {
		return set.toArray(a);
	}

	@Override
	public boolean add(T t) {
		return set.add(t);
	}

	@Override
	public boolean remove(Object o) {
		boolean remove;
		do {
			remove = set.remove(o);
		} while (remove);
		return true;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return set.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		return set.addAll(c);
	}

	@Override
	public boolean addAll(int index, @NotNull Collection<? extends T> c) {
		return set.addAll(index, c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return set.retainAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return set.removeAll(c);
	}

	@Override
	public void clear() {
		set.clear();
	}

	@Override
	public Spliterator<T> spliterator() {
		return java.util.List.super.spliterator();
	}

	@Override
	public T get(int index) {
		return set.get(index);
	}

	@Override
	public T set(int index, T element) {
		return set.set(index, element);
	}

	@Override
	public void add(int index, T element) {
		set.add(index, element);
	}

	@Override
	public T remove(int index) {
		return set.remove(index);
	}

	@Override
	public int indexOf(Object o) {
		return set.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return set.lastIndexOf(o);
	}

	@NotNull
	@Override
	public ListIterator<T> listIterator() {
		return set.listIterator();
	}

	@NotNull
	@Override
	public ListIterator<T> listIterator(int index) {
		return set.listIterator(index);
	}

	@NotNull
	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		return set.subList(fromIndex, toIndex);
	}

	//	@Override
//	public int compareTo(AbstractSet s) {
//		// TODO Auto-generated method stub
//		return 0;
//	}

//	@Override
//    public boolean containsAll(Collection<?> c) {
//        for (Object e : c)
//            if (!contains(e))
//                return false;
//        return true;
//    }

//	@Override
//	public boolean equals(Object o) {
//		return super.equals(o);
//	}

//	@Override
//	public boolean equals(Object o) {
//        if (o == this)
//            return true;
//
//        if (!(o instanceof Set))
//            return false;
//        Collection<?> c = (Collection<?>) o;
//        if (c.size() != size())
//            return false;
//        try {
//            return containsAll(c);
//        } catch (ClassCastException | NullPointerException unused) {
//            return false;
//        }
//	}

	// @Override
	// public Object clone() {
	// 	return (FiniteSet<T>) super.clone();
	// }

	@Override
	public String toString() {
		return this.stream().map(Object::toString).collect(Collectors.joining(", ", "{ ", " }"));
	}

}
