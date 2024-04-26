package bran.tree.theory.category;

import bran.tree.theory.Element;

public interface Morphism<O extends Element<O>> {

	O hom(O a);

}
