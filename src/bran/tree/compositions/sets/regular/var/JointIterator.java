package bran.tree.compositions.sets.regular.var;

import java.util.Iterator;

public class JointIterator<E, H> implements Iterator<Object> {

	private final Iterator<E> iterator1;
	private final Iterator<H> iterator2;

	public JointIterator(Iterator<E> iterator1, Iterator<H> iterator2) {
		this.iterator1 = iterator1;
		this.iterator2 = iterator2;
	}

	@Override
	public boolean hasNext() {
		return iterator1.hasNext() && iterator2.hasNext();
	}

	@Override
	public Object next() {
		if (!iterator1.hasNext())
			return iterator2.next();
		else
			return iterator1.next();
	}

}
