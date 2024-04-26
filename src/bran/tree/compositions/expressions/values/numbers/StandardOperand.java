package bran.tree.compositions.expressions.values.numbers;

public interface StandardOperand<T, R> {

	R add(T t);
	R subtract(T t);
	R multiply(T t);
	R divide(T t);

}
