package bran.parser.matching;

import bran.parser.abst.StringPart;
import bran.parser.abst.TypelessStringPart;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class EnumerableRange<T> implements Iterable<T> {

	public abstract T at(int ind);
	public abstract T set(int ind, T t);
	public abstract int size();

	public T first() {
		return at(0);
	}

	public T last() {
		return at(size() - 1);
	}

	/**
	 * @param to inclusive index
	 * @return if a call to .at(to) is possible without ArrayIndexOutOfBoundsException
	 */
	public abstract boolean inRange(int to);

	public abstract EnumerableRange<T> replaced(int to, T[] with);

	public static <TT> EnumerableRange<TT> of(TT[] tts, int from) {
		return new ArrayRange<>(tts, from);
	}

	public static <TT> EnumerableRange<TT> of(List<TT> tts, int from) {
		return new ListRange<>(tts, from);
	}

	public static TypelessStringPart reduce(EnumerableRange<StringPart> e) {
		return new TypelessStringPart(e.stream().map(StringPart::string).collect(Collectors.joining()), e.first().from(), e.last().to());
	}

	public Stream<T> stream() {
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED), false);
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<>() {
			int i = 0;
			@Override public boolean hasNext() {
				return i < size();
			}
			@Override public T next() {
				return at(i++);
			}
		};
	}

}
