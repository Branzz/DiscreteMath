package bran.tree.compositions.sets.regular;

import bran.tree.compositions.expressions.values.numbers.NumberLiteral;
import bran.tree.compositions.sets.Set;
import bran.tree.structure.Leaf;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.stream.Collectors;

public class FiniteSet<T> extends Set implements Leaf, java.util.Set<T> {

//	private T generic;
	// "..." instantiation
	// set bulider notation

	private final java.util.Set<T> set;

	public FiniteSet(T... ts) {
		super();
		set = new HashSet<>();
		set.addAll(Arrays.asList(ts));
	}

	@Override
	public boolean isSubsetOf(Set s) {
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
	public boolean isProperSubsetOf(Set s) {
		return isSubsetOf(s) && !s.isSubsetOf(this);
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

	public boolean contains(Object o) {
		if (o instanceof NumberLiteral) {
			for (Object o0 : this)
				if (o0 instanceof NumberLiteral)
					if (o.equals(o0))
						return true;
		}
		return set.contains(o);
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
	public boolean add(final T t) {
		return set.add(t);
	}

	@Override
	public boolean remove(final Object o) {
		return set.remove(o);
	}

	@Override
	public boolean containsAll(final Collection<?> c) {
		return set.containsAll(c);
	}

	@Override
	public boolean addAll(final Collection<? extends T> c) {
		return set.addAll(c);
	}

	@Override
	public boolean retainAll(final Collection<?> c) {
		return set.retainAll(c);
	}

	@Override
	public boolean removeAll(final Collection<?> c) {
		return set.removeAll(c);
	}

	@Override
	public void clear() {
		set.clear();
	}

	public Set complement() {
		// special: when this is an empty set or of just 0
		return new SpecialSet(SpecialSetType.C).symmetricDifference(this);
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
		return this.stream().map(Object::toString).collect(Collectors.joining(", ", "{", "}"));
	}

}
