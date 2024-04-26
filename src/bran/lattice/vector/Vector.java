package bran.lattice.vector;

import bran.lattice.matrix.ExpressionMatrix;
import bran.tree.compositions.Composition;
import bran.tree.compositions.expressions.Expression;
import bran.tree.compositions.expressions.values.Constant;
import bran.tree.compositions.expressions.values.Variable;
import bran.tree.compositions.statements.Statement;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static bran.lattice.vector.VectorBranchFunction.Magnitude;
import static bran.lattice.vector.VectorBranchFunction.Normalized;
import static bran.lattice.vector.VectorOperator.*;

/**
 * TODO:
 * 0 operations between vectors and expressions
 * 0 Variables;
 * / dependent / free variables --> extracted vectors
 * X equivalent systems ~~
 * X Homogeneous System of linear equations if all constants are zero on right side
 * X System Of Eq's <toVectors->   Set<Vector>   <toMatrix->   Matrix
 * X   |solve()                                                            | rref()
 * Solved System        <->       solved as vectors         <->       RREF Matrix
 *     |
 * parametric representation {(vars) | vars in set} C R^<=number of vars
 * Gauss-Jordan elimination method - for equations vs for matrices

 * X isSubspace(VectorSpace R^n) { if nonempty & all algebraic properties of R^n:
 * /  (closed under addition and multiplication); always contains 0 vector }
 * X Set<Vector>s is a span of some vector space if they're a linear combination of V:
 * 0 Linear Combination c1v1 + c2v2 ... lincomb OF v1,v2,...
 * / identity matrix required if vars = equations
 * /  ^ includes {0}, through origin, less or equal to dimensional than n
 * basis - set of vectors if they are a span and linearly independent
 * X dimensions
 * / dimension = vector space: number of vectors
 * X StandardBasis = set (1,0... (0,1,0... ... (0,...0,1) x dimension
 * X linearly independent if none multiples of each other
 *
 *
 * Vector has a set size
 */
public class Vector extends Expression {

	Expression[] elements;

	public Vector(Expression[] elements) {
		this.elements = elements;
	}

	public int size() {
		return elements.length;
	}

	public Expression get(int i) {
		return elements[i];
	}

	public Vector map(Function<Expression, Expression> mapper) {
		return new Vector(Arrays.stream(elements).map(mapper).toArray(Expression[]::new));
	}

	public VectorOperation dotProduct(Vector v) {
		return DotProduct.of(this, v);
	}

	/** norm, length */
	public Expression magnitude() {
		return Magnitude.of(this);
	}

	public Expression normalized() {
		return Normalized.of(this);
	}

	public Statement isNormal() {
		return this.magnitude().equates(Constant.ONE);
	}

	public VectorOperation angleBetween(Vector v) {
		return Angle.of(this, v);
	}

	public Statement orthogonalBetween(Vector v) {
		return dotProduct(v).equates(Constant.ZERO);
	}

	public VectorOperation distanceBetween(Vector v) {
		return Distance.of(this, v);
	}

	public ExpressionMatrix asColumnMatrix() {
		return new ExpressionMatrix(this);
	}

	public static final Vector UNDEFINED = new Vector(null) {};
	public static final Vector EMPTY = new Vector(new Expression[] {});

	// Expression methods

	@Override
	public Expression simplified() {
		return null;
	}

	@Override
	public Set<Variable> getVariables() {
		return null;
	}

	@Override
	public double evaluate() {
		return 0;
	}

	@Override
	public Expression derive() {
		return this.map(Expression::derive);
	}

	@Override
	public void replaceAll(Composition original, Composition replacement) { }

	@Override
	public boolean equals(Object other) {
		return false;
	}

	@Override
	public String toFullString() {
		return null;
	}

	public boolean isZero() {
		return Arrays.stream(elements).allMatch(e -> e == Constant.ZERO);
	}

	@Override
	public String toString() {
		return Arrays.stream(elements).map(Expression::toString).collect(Collectors.joining(", ", "(", ")"));
	}

}
