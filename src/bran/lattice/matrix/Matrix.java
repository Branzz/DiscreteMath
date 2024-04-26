package bran.lattice.matrix;

import java.util.List;
public abstract class Matrix<T, M extends Matrix<T, M>> {

	final List<List<T>> values;

	public List<List<T>> getValues() {
		return values;
	}

	public Matrix(List<List<T>> values) {
		this.values = values;
	}

	public abstract M add(M m);

	public abstract M subtract(M m);

	public abstract M multiply(M m);


	public T get(int row, int col) {
		return values.get(row).get(col);
	}

	protected void set(int row, int col, T val) {
		values.get(row).set(col, val);
	}

	public int getRows() {
		return values.size();
	}

	public int getColumns() {
		if (getRows() == 0)
			return 0;
		return values.get(0).size();
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (List<T> ts : values) {
			str.append("| ");
			for (T t : ts) {
				str.append(t.toString()).append(" ");
			}
			str.append("|\n");
		}
		return str.toString();
	}

}
