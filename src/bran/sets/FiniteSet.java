package bran.sets;

import bran.sets.numbers.NumberLiteral;

import java.io.Serial;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

public class FiniteSet<T> extends HashSet<T> implements Set {

	@Serial
	private static final long serialVersionUID = -4214778711499937136L;

//	private T generic;
	// "..." instantiation
	// set bulider notation

	public FiniteSet(T... ts) {
		super();
		this.addAll(Arrays.asList(ts));
	}

	@Override
	public boolean isSubsetOf(Set s) {
		if (s instanceof FiniteSet)
			return ((FiniteSet) s).containsAll(this);
		return containsAll(this);
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

	public boolean contains(Object o) {
		if (o instanceof NumberLiteral) {
			for (Object o0 : this)
				if (o0 instanceof NumberLiteral)
					if (o.equals(o0))
						return true;
		}
		else
			return false; // TODO
		return super.contains(o);
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
