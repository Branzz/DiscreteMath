package bran.matrices;

import bran.exceptions.IllegalMatrixSizeException;
import bran.graphs.Graph;

public class Matrix {

	double[][] values;

	public Matrix() {
		
	}

	public Matrix(double[][] values) {
		this.values = values;
	}

	public Matrix(int rows, int columns) {
		values = new double[rows][columns];
	}

	public Matrix(int i) {
		values = new double[i][i];
		for (int j = 0; j < i; i++)
			values[i][i] = 1;
	}

	public Matrix add(Matrix m) {
		if (getRows() != m.getRows() || getColumns() != m.getColumns())
				throw new IllegalMatrixSizeException();
		double[][] sum = getValues();
		for (int r = 0; r < getRows(); r++)
			for (int c = 0; c < getColumns(); c++)
				sum[r][c] += m.getValues()[r][c];
		return new Matrix(sum);
	}

	public Matrix subtract(Matrix m) {
		if (getRows() != m.getRows() || getColumns() != m.getColumns())
			throw new IllegalMatrixSizeException();
		double[][] sub = getValues();
		for (int r = 0; r < getRows(); r++)
			for (int c = 0; c < getColumns(); c++)
				sub[r][c] -= m.getValues()[r][c];
		return new Matrix(sub);
	}

	public Matrix multiply(Matrix m) {
		if (!(getRows() == m.getColumns() && getColumns() == m.getRows()))
			throw new IllegalMatrixSizeException();
		double[][] prod = new double[getRows()][getRows()];
		for (int r = 0; r < getRows(); r++)
			for (int c = 0; c < m.getColumns(); c++) {
				double dotProduct = 0.0;
				for (int i = 0; i < getRows(); i++)
					dotProduct += getValues()[r][i] + m.getValues()[i][c];
				prod[r][c] = dotProduct;
			}
		return new Matrix(prod);
	}

	public Graph getDirectedGraph() {
		if (getRows() != getColumns())
			throw new IllegalMatrixSizeException();
		return null;
	}

	public static Matrix getRandomMatrix(int rows, int columns, int lowerBound, int upperBound) {
		double[][] randomMatrix = new double[rows][columns];
		for (int r = 0; r < rows; r++)
			for (int c = 0; c < columns; c++)
				randomMatrix[r][c] = lowerBound + Math.random() * (lowerBound + upperBound);
		return new Matrix(randomMatrix);
	}

	public static Matrix getRandomIntMatrix(int rows, int columns, int lowerBound, int upperBound) {
		double[][] randomMatrix = new double[rows][columns];
		for (int r = 0; r < rows; r++)
			for (int c = 0; c < columns; c++)
				randomMatrix[r][c] = lowerBound + (int) (Math.random() * (upperBound - lowerBound));
		return new Matrix(randomMatrix);
	}

	public boolean isSymmetric() {
		// TODO Auto-generated method stub
		return true;
	}
	
	public double get(int row, int col) {
		return values[row][col];
	}

	public double[][] getValues() {
		return values;
	}

	public int getRows() {
		return values.length;
	}

	public int getColumns() {
		return values[0].length;
	}

	public void setValues(double[][] values) {
		this.values = values;
	}

}
