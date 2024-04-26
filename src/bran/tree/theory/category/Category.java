package bran.tree.theory.category;

import bran.tree.compositions.sets.Set;
import bran.tree.theory.Element;
import bran.tree.theory.Identity;

import java.util.function.Function;

public interface Category<Obj extends Element<Obj>, Hom extends Morphism<Obj>, I extends Identity> {

	/**
	 * @param H1 b, c
	 * @param H2 a, b
	 * @return -> H3 - a, c
	 */
	Hom bin_op(Hom H1, Hom H2);
	I identity();
	Set<Obj> objects();

}
