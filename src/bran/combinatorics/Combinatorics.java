package bran.combinatorics;

import java.util.ArrayList;
import java.util.List;

public class Combinatorics<T> {

//	public static <T extends Combinator<? extends Object>> List<Object[]> combinationsOf(Object[] initial, T c) {
//		List<Object[]> cases = new ArrayList<Object[]>();
////		cases.add(initial);
//		cases.addAll(combinationsOfH(initial, c));
//		return cases;
//	}

//	public static linkedCombinationsOf

//	private final static ArrayList<Object> EMPTY_LIST = new ArrayList<Object>();

	private List<T> cases;

	public synchronized List<T> combinationsOf(T current, Combinator<T> c) {
		cases = new ArrayList<T>();
		combinationsOfH(current, c);
		return cases;
	}

	private synchronized void combinationsOfH(T current, Combinator<T> c) {
//		if (c.end(current))
//			cases.add(current);
//		else
		for (int i = 0; i < c.caseMax(); i++) {
			T invoked = c.invoke(current, i);
			if (!cases.contains(invoked)) {
				cases.add(current);
				combinationsOfH(invoked, c);
			}
		}
	}

}
