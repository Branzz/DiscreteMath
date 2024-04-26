package bran.lattice.matrix;

import bran.exceptions.IllegalMatrixSizeException;
import bran.tree.compositions.expressions.values.numbers.StandardOperand;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class OperableMatrix<T extends StandardOperand<T, T>, S extends OperableMatrix<T, S>> extends Matrix<T, OperableMatrix<T, S>> {

	public OperableMatrix(List<List<T>> values) {
		super(values);
	}

	public OperableMatrix<T, S> map(Function<T, T> function) {
		List<List<T>> operated = new ArrayList<>();
		for (int r = 0; r < getRows(); r++) {
			ArrayList<T> e = new ArrayList<>();
			for (int c = 0; c < getColumns(); c++)
				e.add(function.apply(this.get(r, c)));
			operated.add(e);
		}
		return new OperableMatrix<>(operated);
	}

	public OperableMatrix<T, S> operate(OperableMatrix<T, S> m, BiFunction<T, T, T> function) {
		if (getRows() != m.getRows() || getColumns() != m.getColumns())
			throw new IllegalMatrixSizeException();
		List<List<T>> operated = new ArrayList<>();
		for (int r = 0; r < getRows(); r++) {
			ArrayList<T> e = new ArrayList<>();
			for (int c = 0; c < getColumns(); c++)
				e.add(function.apply(this.get(r, c), m.get(r, c)));
			operated.add(e);
		}
		return new OperableMatrix<>(operated);
	}

	@Override
	public OperableMatrix<T, S> add(OperableMatrix<T, S> m) {
		return operate(m, T::add);
	}

	@Override
	public OperableMatrix<T, S> subtract(OperableMatrix<T, S> m) {
		return operate(m, T::subtract);
	}

	@Override
	public OperableMatrix<T, S> multiply(OperableMatrix<T, S> m) {
		// final T[][] prod = m.values.clone(); // get around T array creation
		// if (this.getColumns() != m.getRows())
		// 	throw new IllegalMatrixSizeException();
		// final int size = getColumns(); // == other.getRows()
		// for (int r = 0; r < prod.length; r++) {
		// 	for (int c = 0; c < prod[r].length; c++) {
		// 		T dotProduct = get(r, 0).multiply(m.getValues()[0][c]);
		// 		for (int i = 1; i < size; i++)
		// 			dotProduct = dotProduct.add(this.getValues()[r][i].multiply(m.getValues()[i][c]));
		// 		prod[r][c] = dotProduct;
		// 	}
		// }
		// return (S) new OperableMatrix<T, S>(prod);
		return null;
	}

}
