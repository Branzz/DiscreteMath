package bran.lattice.vector;

import bran.Cache;
import bran.lattice.DefaultLattice;
import bran.lattice.Lattice;
import bran.lattice.Space;
import bran.lattice.SystemOfEquations;
import bran.lattice.matrix.ExpressionMatrix;
import bran.tree.compositions.expressions.Expression;
import bran.tree.compositions.expressions.values.Constant;
import bran.tree.compositions.sets.Po;
import bran.tree.compositions.sets.regular.FiniteSet;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class VectorSet extends FiniteSet<Vector> implements Po<Vector>, DefaultLattice<VectorSet> {

	private final Cache<ExpressionMatrix> inner;

	public VectorSet(Vector... vs) {
		super(vs);
		inner = new Cache<ExpressionMatrix>() {
			private ExpressionMatrix inner;
			@Override public ExpressionMatrix access() { return inner; }
			@Override public ExpressionMatrix create() { return inner = toMatrix(); }
		};
	}

	boolean isSpan(Space space) {
		return false;
		// linear combination of
	}

	boolean isBasis(Space space) {
		return isSpan(space); //&& isLinearlyIndependent();
	}

	static final Comparator<Vector> order = (u, v) -> {
		assert(u.size() == v.size());
		for (int i = 0; i < u.size(); i++) {
			Expression u_e = u.get(i);
			Expression v_e = v.get(i);
			if (u_e instanceof Constant u_c) {
				if (v_e instanceof Constant v_c) {
					return u_c.compareTo(v_c);
				} else {
					return 1;
				}
			} else if (v_e instanceof Constant) {
				return -1;
			}
		}
		return 0;
	};

	boolean isStandardBasis() {
		return false;
	}

	@Override
	public Comparator<Vector> orderBy() {
		return null;
	}

	@Override
	public boolean isSorted() {
		return false;
	}

	@Override
	public List<Vector> sort() {
		return null;
	}

	@Override
	public Lattice inner() {
		return inner.get();
	}

	@Override
	public SystemOfEquations toSystemOfEquations(List<Expression> variables) {
		return toMatrix().toSystemOfEquations(variables);
	}

	@Override
	public VectorSet toVectorSet() {
		return this;
	}

	@Override
	public ExpressionMatrix toMatrix() {
		List<List<Expression>> expressions = new ArrayList<>();
		for (int i = 0; i < height(); i++) {
			final ArrayList<Expression> e = new ArrayList<>();
			for (int j = 0; j < width(); j++) {
				e.add(set.get(j).elements[i]);
			}
			expressions.add(e);
		}
		return new ExpressionMatrix(expressions, false);
	}

	@Override
	public int height() {
		if (set.size() == 0)
			return 0;
		return set.get(0).size();
	}

	@Override
	public int width() {
		return set.size();
	}

	@Override
	public VectorSet solved() {
		return toMatrix().rref().toVectorSet();
	}

	@Override
	public <R extends Lattice<R>> boolean equivalent(Lattice<R> lattice) {
		return false;
	}

}
