package bran.parser.matching;

public abstract class EnumerableRange<T> {

	public abstract T at(int ind);
	public abstract T set(int ind, T t);

	/**
	 * @param to inclusive index
	 * @return if a call to .at(to) is possible without ArrayIndexOutOfBoundsException
	 */
	public abstract boolean inRange(int to);

	public abstract EnumerableRange<T> replaced(int to, T[] with);

}
