package bran.application;

import bran.tree.compositions.Composition;
import bran.tree.compositions.statements.Statement;
import bran.tree.compositions.statements.VariableStatement;
import bran.tree.compositions.statements.special.equivalences.equation.Equation;
import bran.tree.compositions.statements.special.equivalences.equation.EquationType;
import bran.tree.compositions.expressions.values.Constant;
import bran.tree.compositions.expressions.Expression;
import bran.tree.compositions.expressions.values.Variable;
import bran.tree.compositions.godel.GodelBuilder;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class SudokuBoard {

	void printConditions() {
		forAllTiles(tile -> {
			System.out.println(tile + ": " + tile.conditions());
		});
	}

	private static class SudokuTile extends Expression {

		private Expression exp; // wrapper
		private boolean solved;

		public SudokuTile(final Expression exp) {
			update(exp);
		}

		public void update(final Expression exp) {
			this.exp = exp;
			solved = exp instanceof Constant;
		}

		public boolean solved() {
			return solved;
		}

		public Statement conditions() {
			return getDomainConditions();
		}

		public void setConditions(Statement conditions) {
			domainConditions = conditions;
		}

		public void condition(Statement condition) {
			limitDomain(condition);
		}

		public void reduceConditions() {
			domainConditions = domainConditions.simplified();
		}

		public boolean solve() {
			if (conditions() instanceof Equation equation && equation.getEquivalenceType() == EquationType.EQUAL) {
				final Expression left = equation.getLeft();
				final Expression right = equation.getRight();
				if (left == this || left == exp) {
					update(right);
					return true;
				} else if (right == this || right == exp) {
					update(left);
					return true;
				}
			}
			return false;
		}

		@Override
		public Expression simplified() {
			return exp.simplified();
		}

		@Override
		public double evaluate() {
			return exp.evaluate();
		}

		@Override
		public String toFullString() {
			return exp.toFullString();
		}

		@Override public Set<Variable> getVariables() { return exp.getVariables(); }
		@Override public Expression derive() { return null; }
		@Override public boolean respect(final Collection<Variable> respectsTo) { return false; }
		@Override public void replaceAll(final Composition original, final Composition replacement) { }
		@Override public void appendGodelNumbers(final GodelBuilder godelBuilder) { }
		@Override public boolean equals(final Object other) { return false; }

	}

	private final int length;
	private final SudokuTile[][] board;

	private Statement conditions; // Eager
	private final Constant lengthConstant;
	private final Constant barSum;

	public SudokuBoard(String seed) {
		this((int) Math.sqrt(seed.length()));
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < length; j++) {
				final int num = seed.charAt(i * length + j) - '0';
				if (num <= 0)
					board[i][j] = new SudokuTile(new Variable("(" + i + "," + j + ")"));
				else
					board[i][j] = new SudokuTile(Constant.of(num));
			}
		}
	}

	public SudokuBoard() {
		this(9);
		for (int i = 0; i < length; i++) {
			board[i] = new SudokuTile[length];
			for (int j = 0; j < length; j++) {
				board[i][j] = new SudokuTile(new Variable("(" + i + "," + j + ")"));
			}
		}
	}

	SudokuBoard(final int length) {
		this.length = length;
		lengthConstant = Constant.of(length);
		barSum = Constant.of(length * (length + 1) / 2);
		board = new SudokuTile[length][length];
		// for (int i = 0; i < length; i++) {
		// 	board[i] = new Variable[length];
		// 	for (int j = 0; j < length; j++) {
		// 		board[i][j] = new Variable("(" + i + "," + j + ")");
		// 	}
		// }
		conditions = VariableStatement.TAUTOLOGY;
	}

	public void applyDefaultConditions() {
		// for (int row = 0; row < length; row++) {
		// 	for (int col = 0; col < length; col++) {
		// 		Statement conditions = VariableStatement.TAUTOLOGY;
		// 	}
		// }

		for (final SudokuTile[] row : board) {
			// Expression rowConds = Constant.ZERO;
			for (int i = 0; i < length; i++) {
				final SudokuTile tile = row[i];
				for (int j = i + 1; j < length; j++) {
					final Equation cond = tile.notEquates(board[i][j]);
					conditions = conditions.and(cond);
					tile.condition(cond);
				}
				Statement numRange = tile.equates(Constant.ONE);
				for (int n = 2; n <= length; n++)
					numRange = numRange.or(tile.equates(Constant.of(n)));
				conditions = conditions.and(numRange);
				tile.condition(numRange);
				// rowConds = rowConds.plus(tile);
			}
			// conditions = conditions.and(rowConds.equates(barSum));
		}
		// for (int j = 0; j < length; j++) {
		// 	Expression rowConds = Constant.ZERO;
		// 	for (int i = 0; i < length; i++) {
		// 		rowConds = rowConds.plus(board[i][j]);
		// 	}
		// 	conditions = conditions.and(rowConds.equates(barSum));
		// }
		final double N = Math.sqrt(length);
		if (N % 1 == 0) {
			final int boxSize = (int) N;
			for (int boxRow = 0; boxRow < length; boxRow += boxSize)
				for (int boxCol = 0; boxCol < length; boxCol += boxSize) {
					// Expression rowConds = Constant.ZERO;
					for (int i = 0; i < length; i++) {
						final SudokuTile iTile = board[i % boxSize + boxRow][i / boxSize + boxCol];
						for (int j = 0; j < length; j++) {
							if (i != j && i % boxSize != j % boxSize && i / boxSize != j / boxSize) { // already covered in row/col
								final SudokuTile jTile = board[j % boxSize + boxRow][j / boxSize + boxCol];
								final Equation cond = iTile.notEquates(jTile);
								conditions = conditions.and(cond);
								// iTile.condition(cond);
								jTile.condition(cond);
							}
						}
						// rowConds = rowConds.plus(board[i % boxSize + boxRow][i / boxSize + boxCol]);
					}
					// conditions = conditions.and(rowConds.equates(barSum));
				}
		}
	}

	public Statement getConditions() {
		return conditions;
	}

	public void fillIn(int r, int c, Constant num) {
		board[r][c].update(num);
	}

	public void resetConditions(int tileRow, int tileCol) {
		final SudokuTile tile = board[tileRow][tileCol];
		if (tile.solved())
			tile.setConditions(VariableStatement.TAUTOLOGY);
		else {
			Statement conditions = VariableStatement.TAUTOLOGY;
			final int boxSize = (int) Math.sqrt(length);
			int boxRow = tileRow / boxSize;
			int boxCol = tileCol / boxSize;
			for (int r = 0; r < boxSize; r++)
				for (int c = 0; c < boxSize; c++) {
					if (r != c && r != tileRow && c != tileCol)
						conditions = conditions.and(tile.notEquates(board[boxRow * boxSize + r][boxCol * boxSize + c].exp));
				}
			for (int r = 0; r < boxSize; r++)
				if (r != tileRow)
					conditions = conditions.and(tile.notEquates(board[r][tileCol].exp));
			for (int c = 0; c < boxSize; c++)
				if (c != tileCol)
					conditions = conditions.and(tile.notEquates(board[tileRow][c].exp));

			Statement numRange = tile.equates(Constant.ONE);
			for (int n = 2; n <= length; n++)
				numRange = numRange.or(tile.equates(Constant.of(n)));
			conditions = conditions.and(numRange);

			tile.setConditions(conditions);
		}
	}

	public void solve() {
		for (int layers = 1; layers <= 10; layers++) {
			resetTileConditions();
			reduceTileConditions();
			if (!solveTiles())
				break;
			System.out.println(this);
		}
	}

	public void resetTileConditions() {
		for (int r = 0; r < length; r++) {
			for (int c = 0; c < length; c++) {
				resetConditions(r, c);
			}
		}
	}

	public Statement applyTileConditions() {
		final Statement[] conditions = { VariableStatement.TAUTOLOGY };
		forAllTiles(tile -> {
				if (!tile.solved())
					conditions[0] = conditions[0].and(tile.conditions());
		});
		return conditions[0];
	}

	public void reduceTileConditions() {
		forAllTiles(tile -> {
			if (!tile.solved())
				tile.reduceConditions();
		});
	}

	public boolean solveTiles() {
		final AtomicBoolean solvedATile = new AtomicBoolean(false);
		forAllTiles(tile -> {
			if (!tile.solved())
				solvedATile.set(solvedATile.get() | tile.solve());
		});
		return solvedATile.get();
	}

	void forAllTiles(Consumer<SudokuTile> consumer) {
		for (final SudokuTile[] row : board)
			for (final SudokuTile tile : row)
				consumer.accept(tile);
	}

	@Override
	public String toString() {
		final int boxSize = (int) Math.sqrt(length);
		StringBuilder b = new StringBuilder();
		b.append('\u250F');
		for (int j = 1; j < length; j++)
			b.append("\u2501\u2501\u2501").append(j % boxSize == 0 ? '\u2533' : '\u252F');
		b.append("\u2501\u2501\u2501\u2513\n");
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < length; j++) {
				b.append(j % boxSize == 0 ? '\u2503' : '\u2502')
				 .append(' ')
				 .append(board[i][j].solved() ? board[i][j] : ' ')
				 .append(' ');
			}
			b.append("\u2503\n");
			if (i != length - 1) {
				b.append(i % boxSize == 2 ? '\u2523' : '\u2520');
				for (int j = 1; j < length; j++) {
					b.append(i % boxSize == 2 ? "\u2501\u2501\u2501" : "\u2500\u2500\u2500");
					b.append(i % boxSize == 2 ?
									  j % boxSize == 0 ? '\u254B' : '\u253F'
									: j % boxSize == 0 ? '\u2542' : '\u253C');
				}
				b.append(i % boxSize == 2 ? "\u2501\u2501\u2501\u252B" : "\u2500\u2500\u2500\u2528")
				 .append('\n');
			}
		}
		b.append('\u2517');
		for (int j = 1; j < length; j++)
			b.append("\u2501\u2501\u2501").append(j % boxSize == 0 ? '\u253B' : '\u2537');
		b.append("\u2501\u2501\u2501\u251B");
		return b.toString();
	}

}
