package bran.tree.compositions.sets;

import java.util.Comparator;
import java.util.List;

/**
 * Partially Ordered (Set)
 */
public interface Po<E> {

	Comparator<E> orderBy();

	boolean isSorted();

	List<E> sort();

}
