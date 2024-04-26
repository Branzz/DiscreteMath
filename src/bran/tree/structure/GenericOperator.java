package bran.tree.structure;

import bran.lattice.vector.Vector;
import bran.lattice.vector.VectorOperation;
import bran.lattice.vector.VectorOperator;
import bran.tree.structure.mapper.ForkOperator;

import java.util.function.Function;

/**
 *
 * X Operation(T Left, F ForkOperator, T Right)
 * F ForkOperator(O inner, T Left, T Right)
 * X Operation of(T Left, T Right)
 * @param <O>
 */
public interface GenericOperator<O, T, D extends GenericOperator<O, T, D, X>,
										X extends MonoTypeFork<O, T, D, X>>
		extends ForkOperator<O, T, T> {

	default X of(T left, T right, X dummy) {
		return dummy.getter().apply(left, (D) this, right);
	}

}
