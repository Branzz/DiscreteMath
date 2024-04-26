package bran.tree.theory.category;

import bran.tree.theory.Element;
import bran.tree.theory.Identity;

public interface Functor<O1 extends Element<O1>, H1 extends Morphism<O1>, I1 extends Identity, C extends Category<O1, H1, I1>,
						 O2 extends Element<O2>, H2 extends Morphism<O2>, I2 extends Identity, D extends Category<O2, H2, I2>> {

	O2 associate(O1 a);
	H2 associate(H1 a);

}
