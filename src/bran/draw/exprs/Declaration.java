package bran.draw.exprs;

import bran.mathexprs.treeparts.Expression;
import bran.mathexprs.treeparts.Variable;

import java.awt.*;
import java.util.Set;

// Old: sets definitions and rules rather than following them
//get contrapositive / inverse / converse. (do this with .not() statements. define negatives. bran.proofs: even odd have definitions)

/**
 * this is how you define variables in the ExpressionViewer. (It's like a function with no arguments?)
 */
public final class Declaration extends Drawable { // TODO A parser for this

	final Variable variable;

	public Declaration(final Set<Entry> subEntries, final Expression expression, final boolean drawn, final double[] data, final Color color, final Variable variable) {
		super(subEntries, expression, drawn, data, color);
		this.variable = variable;
	}

	public Declaration(final Set<Entry> subEntries, final Expression expression, final boolean drawn, final double[] data, final Color color, final ExpressionInputException e, final Variable variable) {
		super(subEntries, expression, drawn, data, color, e);
		this.variable = variable;
	}

	public Declaration(final Set<Entry> subEntries, final Expression expression, final int size, final ExpressionInputException e, final Variable variable) {
		super(subEntries, expression, size, e);
		this.variable = variable;
	}

	public Declaration(final Set<Entry> subEntries, final Expression expression, final int size, final Variable variable) {
		super(subEntries, expression, size);
		this.variable = variable;
	}

	public Declaration(final Set<Entry> subEntries, final Expression expression, final int precision, final Variable variable, final ExpressionInputException exception) {
		this(subEntries, expression, precision, variable);
		except(exception);
	}

	public void resetVariable() {
		variable.setValue(expression().evaluate());
	}

	public double getValue() {
		return variable.get().doubleValue();
	}

	public boolean isConstant() {
		return false;
	}

	public boolean isDuplicate(Declaration other) {
		return variable.parseEquals(other.variable);
	}

	@Override
	public void refresh(final int i) {
		super.refresh(i);
		variable.setValue(data()[i]);
		// resetVariable();
	}

	@Override
	public String toString() {
		return variable.toString() + " = " + expression();
	}

}
