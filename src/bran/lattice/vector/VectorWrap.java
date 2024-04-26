package bran.lattice.vector;

import bran.Cache;
import bran.tree.compositions.expressions.Expression;
import bran.tree.compositions.expressions.operators.ExpressionOperation;

/**
 * some Expressions simplify to Vector, but they are typed as Expression
 * Warning: not very modular
 */
public class VectorWrap extends Vector {

	private final Expression inner; // non-Vector, probably StatementOperation
	private final Cache<Vector> simplified;

	public VectorWrap(Expression inner) {
		super(null);
		this.inner = inner;
		simplified = new Cache<Vector>() {
			private Vector vector;
			@Override public Vector access() { return vector; }
			@Override
			public Vector create() {
				if (inner.simplified() instanceof Vector simplifiedVector)
					vector = simplifiedVector;
				else
					throw new RuntimeException("Programmer error");
				return vector;
			}
		};
	}

	public static Vector of(Expression exp) {
		return new VectorWrap(exp);
	}

	@Override
	public int size() {
		return simplified.get().size();
	}

	@Override
	public Expression get(int i) {
		return simplified.get().get(i);
	}

	@Override
	public Expression simplified() {
		return simplified.get().simplified();
	}

	@Override
	public String toFullString() {
		return inner.toFullString();
	}

}
