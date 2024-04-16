package bran.graphs.matrices;

import bran.exceptions.IllegalMatrixSizeException;
import bran.graphs.Graph;

public class DoubleMatrix extends Matrix<Double> {

	public DoubleMatrix() {
	}

	public DoubleMatrix(Double[][] values) {
		super(values);
	}

	public DoubleMatrix(int rows, int columns) {
		values = new Double[rows][columns];
	}

	public DoubleMatrix(int i) {
		values = new Double[i][i];
		for (int j = 0; j < i; i++)
			values[i][i] = 1d;
	}

	public DoubleMatrix(final String str) {
		final String[] rows = str.split(";");
		final String[] firstCols = rows[0].split(",");
		final int columns = firstCols.length;
		values = new Double[rows.length][columns];
		values[0] = strToArr(firstCols);
		for (int i = 1; i < rows.length; i++) {
			final String[] cols = rows[i].split(",");
			if (cols.length != columns)
				throw new IllegalMatrixSizeException();
			values[i] = strToArr(cols);
		}
	}

	private static Double[] strToArr(String[] nums) {
		Double[] row = new Double[nums.length];
		for (int i = 0; i < nums.length; i++) {
			row[i] = Double.parseDouble(nums[i]);
		}
		return row;
	}

	public DoubleMatrix add(DoubleMatrix m) {
		if (getRows() != m.getRows() || getColumns() != m.getColumns())
				throw new IllegalMatrixSizeException();
		Double[][] sum = getValues();
		for (int r = 0; r < getRows(); r++)
			for (int c = 0; c < getColumns(); c++)
				sum[r][c] += m.getValues()[r][c];
		return new DoubleMatrix(sum);
	}

	public DoubleMatrix subtract(DoubleMatrix m) {
		if (getRows() != m.getRows() || getColumns() != m.getColumns())
			throw new IllegalMatrixSizeException();
		Double[][] sub = getValues();
		for (int r = 0; r < getRows(); r++)
			for (int c = 0; c < getColumns(); c++)
				sub[r][c] -= m.getValues()[r][c];
		return new DoubleMatrix(sub);
	}

	public DoubleMatrix multiply(DoubleMatrix other) {
		if (this.getColumns() != other.getRows())
			throw new IllegalMatrixSizeException();
		final int size = getColumns(); // == other.getRows()
		Double[][] prod = new Double[this.getRows()][other.getColumns()];
		for (int r = 0; r < prod.length; r++) {
			for (int c = 0; c < prod[r].length; c++) {
				Double dotProduct = 0.0;
				for (int i = 0; i < size; i++)
					dotProduct += this.getValues()[r][i] * other.getValues()[i][c];
				prod[r][c] = dotProduct;
			}
		}
		return new DoubleMatrix(prod);
	}

	public Graph getDirectedGraph() {
		if (getRows() != getColumns())
			throw new IllegalMatrixSizeException();
		return null;
	}

	public static DoubleMatrix getRandomMatrix(int rows, int columns, int lowerBound, int upperBound) {
		Double[][] randomMatrix = new Double[rows][columns];
		for (int r = 0; r < rows; r++)
			for (int c = 0; c < columns; c++)
				randomMatrix[r][c] = lowerBound + Math.random() * (lowerBound + upperBound);
		return new DoubleMatrix(randomMatrix);
	}

	public static DoubleMatrix getRandomIntMatrix(int rows, int columns, int lowerBound, int upperBound) {
		Double[][] randomMatrix = new Double[rows][columns];
		for (int r = 0; r < rows; r++)
			for (int c = 0; c < columns; c++)
				randomMatrix[r][c] = (double) (lowerBound + (int) (Math.random() * (upperBound - lowerBound)));
		return new DoubleMatrix(randomMatrix);
	}

	public boolean isSymmetric() {
		// TODO Auto-generated method stub
		return true;
	}
	
	public double get(int row, int col) {
		return values[row][col];
	}

	public int getRows() {
		return values.length;
	}

	public int getColumns() {
		return values[0].length;
	}

	public void setValues(Double[][] values) {
		this.values = values;
	}

	@Override
	public String toString() {
		StringBuilder sB = new StringBuilder();
		for (final Double[] row : values) {
			for (final double num : row) {
				sB.append(num % 1 == 0 ? Integer.toString((int) num) : Double.toString(num))
				  .append(' ');
			}
			sB.append('\n');
		}
		return sB.substring(0, sB.length() - 1);
	}

}
