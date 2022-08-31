package bran.tree.group;

import bran.tree.compositions.statements.Statement;

public interface Element<E extends Element<E>> {

	boolean equivalent(E other);

}
