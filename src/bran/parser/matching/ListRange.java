package bran.parser.matching;

import java.util.List;

public class ListRange<T> extends EnumerableRange<T> {

	List<T> list;
	int from;
	// int to; // exclusive

	public ListRange(List<T> list, int from) {
		this.list = list;
		this.from = from;
		// this.to = to;
	}

	@Override
	public T at(int ind) {
		return list.get(from + ind);
	}

	@Override
	public T set(int ind, T t) {
		return list.set(from + ind, t);
	}

	@Override
	public int size() {
		return list.size();
	}

	/**
	 * @param to inclusive index
	 * @return if a call to .at(to) is possible without ArrayIndexOutOfBoundsException
	 */
	@Override
	public boolean inRange(int to) {
		return from + to >= 0 && from + to < list.size();
	}

	public EnumerableRange<T> replaced(int to, T[] with) {
		for (int i = 0; i < to; i++) {
			list.remove(from);
		}
		for (int i = 0; i < with.length; i++) {
			list.add(from + i, with[i]);
		}
		return null;
	}

}
