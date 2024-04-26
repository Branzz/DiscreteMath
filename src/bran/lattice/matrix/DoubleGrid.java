package bran.lattice.matrix;

import bran.exceptions.IllegalMatrixSizeException;
import bran.lattice.Grid;
import bran.lattice.graphs.Graph;

import java.util.List;

public class DoubleGrid implements Grid<Double> {

	final double[][] values;

	public DoubleGrid(double[][] values) {
		this.values = values;
	}

	public DoubleGrid(int rows, int columns) {
		this(new double[rows][columns]);
	}

	/**
	 * identity matrix
	 * @param i width x height
	 */
	public DoubleGrid(int i) {
		this(identityMatrix(i));
	}

	private static double[][] identityMatrix(int i) {
		double[][] values = new double[i][i];
		for (int j = 0; j < i; i++)
			values[i][i] = 1d;
		return values;
	}

	public DoubleGrid(final String str) {
		this(strTo2Arr(str));
	}

	private static double[][] strTo2Arr(String str) {
		final String[] rows = str.split(";");
		final String[] firstCols = rows[0].split(",");
		final int columns = firstCols.length;
		double[][] values = new double[rows.length][columns];
		values[0] = strToArr(firstCols);
		for (int i = 1; i < rows.length; i++) {
			final String[] cols = rows[i].split(",");
			if (cols.length != columns)
				throw new IllegalMatrixSizeException();
			values[i] = strToArr(cols);
		}
		return values;
	}

	private static double[] strToArr(String[] nums) {
		double[] row = new double[nums.length];
		for (int i = 0; i < nums.length; i++) {
			row[i] = Double.parseDouble(nums[i]);
		}
		return row;
	}

	public DoubleGrid add(DoubleGrid m) {
		if (getRows() != m.getRows() || getColumns() != m.getColumns())
				throw new IllegalMatrixSizeException();
		double[][] sum = values.clone();
		for (int r = 0; r < getRows(); r++)
			for (int c = 0; c < getColumns(); c++)
				sum[r][c] += values[r][c];
		return new DoubleGrid(sum);
	}

	public DoubleGrid subtract(DoubleGrid m) {
		if (getRows() != m.getRows() || getColumns() != m.getColumns())
			throw new IllegalMatrixSizeException();
		double[][] sub = values.clone();
		for (int r = 0; r < getRows(); r++)
			for (int c = 0; c < getColumns(); c++)
				sub[r][c] -= values[r][c];
		return new DoubleGrid(sub);
	}

	public DoubleGrid multiply(DoubleGrid m) {
		if (this.getColumns() != m.getRows())
			throw new IllegalMatrixSizeException();
		final int size = getColumns(); // == m.getRows()
		double[][] prod = new double[this.getRows()][m.getColumns()];
		for (int r = 0; r < prod.length; r++) {
			for (int c = 0; c < prod[r].length; c++) {
				double dotProduct = 0.0;
				for (int i = 0; i < size; i++)
					dotProduct += values[r][i] * values[i][c];
				prod[r][c] = dotProduct;
			}
		}
		return new DoubleGrid(prod);
	}

	public boolean isSymmetric() {
		// TODO Auto-generated method stub
		return true;
	}

	public Graph getDirectedGraph() {
		if (getRows() != getColumns())
			throw new IllegalMatrixSizeException();
		return null;
	}

	public static DoubleGrid getRandomMatrix(int rows, int columns, int lowerBound, int upperBound) {
		double[][] randomMatrix = new double[rows][columns];
		for (int r = 0; r < rows; r++)
			for (int c = 0; c < columns; c++)
				randomMatrix[r][c] = lowerBound + Math.random() * (lowerBound + upperBound);
		return new DoubleGrid(randomMatrix);
	}

	public static DoubleGrid getRandomIntMatrix(int rows, int columns, int lowerBound, int upperBound) {
		double[][] randomMatrix = new double[rows][columns];
		for (int r = 0; r < rows; r++)
			for (int c = 0; c < columns; c++)
				randomMatrix[r][c] = (double) (lowerBound + (int) (Math.random() * (upperBound - lowerBound)));
		return new DoubleGrid(randomMatrix);
	}

	@Override
	public String toString() {
		StringBuilder sB = new StringBuilder();
		for (final double[] row : values) {
			for (final double num : row) {
				sB.append(num % 1 == 0 ? Integer.toString((int) num) : Double.toString(num))
				  .append(' ');
			}
			sB.append('\n');
		}
		return sB.substring(0, sB.length() - 1);
	}

	@Override
	public Double get(int row, int col) {
		return values[row][col];
	}

	@Override
	public void set(int row, int col, Double val) {

	}

	@Override
	public int getRows() {
		return values.length;
	}

	@Override
	public int getColumns() {
		return values.length == 0 ? 0 : values[0].length;
	}

}
