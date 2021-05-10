package bran.combinatorics;

import java.util.Iterator;
public class Counter implements Iterator<int[]> {

	private final int arguments;
	private final int elementSize;
	int[] counter;
	private boolean endReached;

	public Counter(final int arguments, final int elementSize) {
		this.arguments = arguments;
		this.elementSize = elementSize;
		counter = new int[arguments];
		counter[0]--;
		endReached = false;
	}

	@Override
	public boolean hasNext() {
		return !endReached;
	}

	@Override
	public int[] next() {
		endReached = !increment();
		return counter;
	}

	private boolean increment() {
		int i = 0;
		while (incrementIndex(i)) {
			i++;
		}
		return i != arguments;
	}

	private boolean incrementIndex(int i) { // returns if end reached
		if (i >= arguments) {
			// endReached = true;
			return false;
		}
		counter[i]++;
		if (counter[i] >= elementSize) {
			counter[i] = 0;
			return true;
		}
		return false;
	}

	public int get(final int index) {
		return counter[index];
	}

}
