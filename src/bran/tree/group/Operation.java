package bran.tree.group;

public interface Operation<E extends Element<E>, I extends E> {

	E operate(E e1, E e2); // closure

}
