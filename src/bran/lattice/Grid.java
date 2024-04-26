package bran.lattice;

public interface Grid<T> {

	T get(int row, int col);

	void set(int row, int col, T val);

	int getRows();

	int getColumns();

}
