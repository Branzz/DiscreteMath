package bran.parser.matching;

public class ArrayRange<T> extends EnumerableRange<T> {

	T[] arr;
	int from;

	public ArrayRange(T[] arr, int from) {
		this.arr = arr;
		this.from = from;
	}

	@Override
	public T at(int ind) {
		return arr[from + ind];
	}

	@Override
	public T set(int ind, T t) {
		T prev = arr[from + ind];
		arr[from + ind] = t;
		return prev;
	}

	@Override
	public int size() {
		return arr.length;
	}

	/**
	 * @param to inclusive index
	 * @return if a call to .at(to) is possible without ArrayIndexOutOfBoundsException
	 */
	@Override
	public boolean inRange(int to) {
		return from + to >= 0 && from + to < arr.length;
	}

	public EnumerableRange<T> replaced(int to, T[] with) {
		T[] newArr = (T[]) new Object[arr.length - to + with.length];
		int i;
		for (i = 0; i < from; i++)
			newArr[i] = arr[i];
		for (int j = 0; j < with.length; j++)
			newArr[i++] = with[j];
		// System.arraycopy(arr, from + to, newArr, from + 1, arr.length - from + to);
		for (int j = to + 1; j < arr.length; j++)
			newArr[i++] = arr[j];
		return new ArrayRange<>(newArr, from);
	}

}
