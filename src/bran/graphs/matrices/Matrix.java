package bran.graphs.matrices;

public class Matrix<T> {

	T[][] values;

	public T[][] getValues() {
		return values;
	}

	public Matrix() {

	}

	public Matrix(T[][] values) {
		this.values = values;
	}


}
