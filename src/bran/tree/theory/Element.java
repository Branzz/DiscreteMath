package bran.tree.theory;

public interface Element<E extends Element<E>> {

	boolean equivalent(E other);

}
