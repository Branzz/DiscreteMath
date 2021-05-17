package bran.draw.exprs;

import bran.mathexprs.treeparts.Expression;
import bran.mathexprs.treeparts.Variable;

import java.awt.*;
import java.util.Arrays;
import java.util.Set;

public class Drawable extends Entry {

	private final Expression expression;
	private boolean drawn;
	private int dataScale;
	private double[] data;
	private Color color;

	public Drawable(final Set<Entry> subEntries, Expression expression, boolean drawn, double[] data, Color color) {
		super(subEntries);
		this.expression = expression;
		this.drawn = drawn;
		this.dataScale = data.length - 1;
		this.data = data;
		this.color = color;
	}

	public Drawable(final Set<Entry> subEntries, final Expression expression, final boolean drawn, final double[] data, final Color color, final ExpressionInputException e) {
		this(subEntries, expression, drawn, data, color);
		this.exception = e;
	}

	public Drawable(final Set<Entry> subEntries, final Expression expression, int size, final ExpressionInputException e) {
		this(subEntries, expression, true, new double[size + 1],  new Color(Color.HSBtoRGB((float) Math.random(), 0.9F, 0.9F)), e);
	}

	public Drawable(final Set<Entry> subEntries, final Expression expression, int size) {
		this(subEntries, expression, size, null);
	}

	public Expression expression() {
		return expression;
	}

	public boolean isDrawn() {
		return !isExcepted() && drawn;
	}

	public void setDrawn(final boolean drawn) {
		this.drawn = drawn;
	}

	public int dataScale() {
		return dataScale;
	}

	public void upScale() {
		dataScale /= 2;
	}

	public void setDataScale(final int dataScale) {
		this.dataScale = dataScale;
	}

	public double[] data() {
		return data;
	}

	public synchronized void resetData() {
		// Arrays.fill(data, 0);
		dataScale = data.length - 1;
	}

	public synchronized void resetData(final int newPrecision) {
		data = new double[newPrecision + 1];
		resetData();
	}

	// public synchronized void downSizeData(final int nextPrecision) {
	// 	double[] nextData = new double[nextPrecision + 1];
	// 	for (int i = 0; i < nextPrecision; i++)
	// 		nextData[i] = data[i / 2];
	// 	data = nextData;
	// 	dataScale /= 2;
	// }
	//
	// public synchronized void upSizeData(final int nextPrecision) {
	// 	double[] nextData = new double[nextPrecision + 1];
	// 	for (int i = 0; i < nextPrecision; i++)
	// 		nextData[i] = data[i * 2];
	// 	data = nextData;
	// }

	public Color getColor() {
		return color;
	}

	public void setColor(final Color color) {
		this.color = color;
	}

	@Override
	public boolean calculated(final int i) {
		return i % dataScale == 0;
	}

	/**
	 * Must precalculate required indices to use.
	 */
	@Override
	public void refresh(final int i) {
		for (Entry e : subEntries)
			e.refresh(i);
		data[i] = expression.evaluate();
	}

	@Override
	public String toString() {
		return "y = " + expression.toString();
	}

}
