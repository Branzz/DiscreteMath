package bran.draw.exprs;

import bran.logic.statements.special.equivalence.Equivalence;
import bran.mathexprs.treeparts.Constant;
import bran.mathexprs.treeparts.Expression;
import bran.mathexprs.treeparts.Variable;

import javax.swing.Timer;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.*;

import static bran.draw.exprs.ExpressionInputExceptionType.*;
import static bran.mathexprs.treeparts.functions.MultivariableFunction.*;

//TODO parametric, polar, points, tables, graphs somehow, boolean (table)
//TODO make zoom translate to mouse spo1
public final class ExpressionViewer extends Applet implements MouseListener, ComponentListener, MouseWheelListener {

	private final int DELAY = 64;
	int precision; // adjust to size of this?
	final Variable x = new Variable("x");
	private final java.util.List<Entry> entries;
	private final java.util.List<Drawable> drawables;
	private final java.util.List<Declaration> constants;
	private final java.util.List<Declaration> variables; // These CAN contain duplicates, which is an exception.

	private ExpressionCalculator calculator;

	double xMin;
	double xMax;
	double xScale;
	double yMin;
	double yMax;
	double yScale;

	private double mouseDownX;
	private double mouseDownY;
	private int width;
	private int height;

	public ExpressionViewer() {
		precision = 64;
		entries = new ArrayList<>();
		drawables = new ArrayList<>();
		constants = new ArrayList<>();
		variables = new ArrayList<>();
	}

	public void init() {
		this.addMouseListener(this);
		this.addComponentListener(this);
		this.addMouseWheelListener(this);
		resetView();
		// addExpression(COS.ofS(of("b").sqrt()));
		addExpression(COS.ofS(of("b").sqrt()));
		addExpression(LOG.ofS(LOG.ofS(x, Constant.E), LOG.ofS(Constant.E, x)).derive().simplified());
		addExpression(SIN.ofS(of("a")));
		// addDeclaration(new Declaration(Collections.emptySet(), x.squared().plus(Constant.ONE), precision, of("a")));
		// addDeclaration(of("h"), Constant.PI);
		// addDeclaration(of("h"), of("a").dec());

		calculator = new ExpressionCalculator(drawables, this);
		calculator.calculate();
		new Timer(10, e -> calculator.calculate()).start();
		new Timer(DELAY, e -> repaint()).start();
	}

	public void update(Graphics g) {
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

		Image offscreen = createImage(d.width, d.height);
		Graphics offgc = offscreen.getGraphics();

		offgc.setColor(getBackground());
		offgc.fillRect(0, 0, d.width, d.height);
		offgc.setColor(getForeground());

		paint(offgc);
		g.drawImage(offscreen, 0, 0, this);
	}

	private static DecimalFormat dF = new DecimalFormat("0.###");

	public void paint(Graphics g) {
		g.setColor(Color.BLACK);
		g.drawString(dF.format(xMin), 5, height / 2);
		g.drawString(dF.format(xMax), width - 40, height / 2);
		g.drawString(dF.format(yMax), width / 2, 16);
		g.drawString(dF.format(yMin), width / 2, height - 12);
		g.drawLine((int) (- width * xMin / xScale), 0,
				   (int) (- width * xMin / xScale), height);
		g.drawLine(0,	  (int) (height + height * yMin / yScale),
				   width, (int) (height + height * yMin / yScale));
		int shift = 1;
		for (Drawable draw : drawables) {
			g.setColor(draw.getColor());
			g.drawString(draw.toString(), 5, shift++ * 16);
			if (draw.isExcepted()) {
				g.setColor(Color.RED);
				g.drawString(" [" + draw.exception.getMessage() + "]", 5 + g.getFontMetrics().stringWidth(draw.toString()), (shift - 1) * 16);
				g.setColor(draw.getColor());
			}
			if (draw.isDrawn())
				for (int i = 0, dS = draw.dataScale(); i <= precision - dS; i += dS)
					g.drawLine((int) ((double)  i       / precision * width), (int) (height - height * (draw.data()[i	  ] - yMin) / yScale),
							   (int) ((double) (i + dS) / precision * width), (int) (height - height * (draw.data()[i + dS] - yMin) / yScale));
		}
	}

	private void resetPrecision() {
		int nextPrecision = (int) Math.ceil(Math.pow(2, (int) (Math.log(width) / Math.log(2))));
		if (precision != nextPrecision) {
			// if (precision == nextPrecision / 2) {
			// 	for (Drawable d : drawables)
			// 		d.downSizeData(nextPrecision);
			// } else if (precision == nextPrecision * 2) {
			// 	for (Drawable d : drawables)
			// 		d.upSizeData(nextPrecision);
			// } else {
			resetDrawables(nextPrecision);
			// }
			precision = nextPrecision;
		}
	}

	private void resetDrawables(final int nextPrecision) {
		for (Drawable d : drawables)
			d.resetData(nextPrecision);
	}

	private void resetDrawables() {
		for (Drawable d : drawables)
			d.resetData();
	}

	Set<Variable> variablePool = new HashSet<>();

	Variable of(String name) {

		// return variablePool.stream().filter(v -> v.getName().equals(name)).findAny().orElse(next(name));
		// private Variable next(String name) {
		// 	Variable next = new Variable(name);
		// 	variablePool.add(next);
		// 	return next;
		// }
		for (Variable v : variablePool)
			if (v.getName().equals(name))
				return v;
		Variable next = new Variable(name);
		variablePool.add(next);
		return next;
	}

	public void addExpression(Expression expression) {
		try {
			drawables.add(new Drawable(getSubEntries(expression), expression, precision));
		} catch (ExpressionInputException exception) {
			drawables.add(new Drawable(Collections.emptySet(), expression, precision, exception));
		}
	}

	public void addEquivalence(Equivalence equivalence) {

	}

	//TODO function declaration

	public void addDeclaration(Variable variable, Expression expression) {
		try {
			addDeclaration(new Declaration(getSubEntries(expression), expression, precision, variable));
		} catch (ExpressionInputException exception) {
			addDeclaration(new Declaration(Collections.emptySet(), expression, precision, variable, exception));
		}
	}

	public void addDeclaration(Declaration addition) {
		//check to see if it's not already in there, then make the ones that match an error
		if (addition.isConstant()) {
			constants.add(addition);
		} else {
			boolean additionException = false;
			for (Declaration declaration : variables)
				if (addition.isDuplicate(declaration)) {
					declaration.except(new ExpressionInputException("duplicate variable declaration: " + addition.variable, DUP_VAR_DECLARATION));
					additionException = true;
				}
			if (additionException) {
				addition.except(new ExpressionInputException("duplicate variable declaration: " + addition.variable, DUP_VAR_DECLARATION));
			}
			variables.add(addition);
			drawables.add(addition);
		}
		refreshDependentEntries();
	}

	// refresh entries that rely on this declaration (do they know they rely on it yet if this is new?)
	private void refreshDependentEntries() {
		boolean mustRetry; // if one is fixed, then maybe it will fix another (order independent)
		do {
			mustRetry = false;
			for (Drawable drawable : drawables) // TODO change to Entry?
				if (drawable.isExcepted() && drawable.exception.type == MISSING_SUB_ENTRY) {
					try {
						drawable.setSubEntries(getSubEntries(drawable.expression()));
						drawable.unexcept();
						mustRetry = true;
					} catch (ExpressionInputException e) {
						drawable.except(e);
					}
				}
		} while (mustRetry);
	}

	/**
	 * for when a variable declaration is renamed
	 */
	private void refreshVariableEntries() {

	}

	private Set<Entry> getSubEntries(Expression expression) throws ExpressionInputException {
		Set<Entry> subEntries = new HashSet<>();
		StringBuilder undefinedVariables = new StringBuilder();
		boolean undefined = false;
		boolean singular = false;
		for (Variable v : expression.getVariables()) {
			if (!variableDefined(v, subEntries) && !v.parseEquals(x)) {
				if (undefined)
					undefinedVariables.append(", ");
				singular = !undefined;
				undefinedVariables.append(v);
				undefined = true;
			}
		}
		if (undefined)
			throw new ExpressionInputException("undefined variable" + (singular ? "" : "s") + ": " + undefinedVariables, MISSING_SUB_ENTRY);
		return subEntries;
	}

	// private String getPluralS(Collection c) {
	// 	return c.size() == 1 ? "" : "s";
	// }

	private boolean variableDefined(Variable v, Set<Entry> subEntries) {
		for (Declaration d : variables) {
			if (d.variable.parseEquals(v) && !d.isExcepted()) {
				subEntries.add(d);
				return true;
			}
		}
		return false;
	}

	@Override
	public void componentResized(final ComponentEvent e) {
		width = this.getWidth();
		height = this.getHeight();
		resetPrecision();
	}

	@Override
	public void mousePressed(final MouseEvent e) {
		mouseDownX = e.getX();
		mouseDownY = e.getY();
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
		double xIncrease = -(e.getX() - mouseDownX) / width  * xScale;
		double yIncrease =  (e.getY() - mouseDownY) / height * yScale;
		xMin += xIncrease;
		xMax += xIncrease;
		yMin += yIncrease;
		yMax += yIncrease;
		checkBoundaries();
		resetDrawables();
	}

	private static final double OUT_FACTOR = 3D;
	private static final double IN_FACTOR = 1 / (OUT_FACTOR);

	@Override
	public void mouseWheelMoved(final MouseWheelEvent e) {
		double zoomFactor = e.getWheelRotation() < 0 ? OUT_FACTOR : IN_FACTOR;
		double x = ((double) e.getX() / width) * xScale + xMin;
		double y = yScale - ((double) e.getY() / height) * yScale + yMin;
		yScale = (yMax - yMin) / zoomFactor;
		xScale = (xMax - xMin) / zoomFactor;
		xMax = x + xScale / 2;
		xMin = x - xScale / 2;
		yMax = y + yScale / 2;
		yMin = y - yScale / 2;
		checkBoundaries();
		resetDrawables();
	}

	private void checkBoundaries() {
		if (xMin == xMax || yMin == yMax || Double.isNaN(xMin) || Double.isNaN(xMax) || Double.isNaN(yMin) || Double.isNaN(yMax))
			resetView();
	}

	private void resetView() {
		xMin = -10.0;
		xMax = 10.0;
		xScale = xMax - xMin;
		yMin = -10.0;
		yMax = 10.0;
		yScale = yMax - yMin;
	}

	@Override public void componentMoved(final ComponentEvent e) { }
	@Override public void componentShown(final ComponentEvent e) { }
	@Override public void componentHidden(final ComponentEvent e) { }
	@Override public void mouseClicked(final MouseEvent e) { }
	@Override public void mouseEntered(final MouseEvent e) { }
	@Override public void mouseExited(final MouseEvent e) { }

}
