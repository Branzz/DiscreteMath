package bran.logic.puzzle;

import bran.logic.statements.Statement;
import bran.logic.statements.VariableStatement;
import bran.logic.statements.special.VerbalStatement;
import bran.mathexprs.treeparts.Constant;
import bran.mathexprs.treeparts.Expression;
import bran.mathexprs.treeparts.Value;
import bran.mathexprs.treeparts.Variable;

import java.util.Arrays;

public class SudokuBoard {

	private final int length;
	private final Value[][] board;

	private Statement conditions; // Eager
	private final Constant lengthConstant;
	private final Constant barSum;

	public SudokuBoard(String seed) {
		this((int) Math.sqrt(seed.length()));
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < length; j++) {
				final int num = seed.charAt(i * length + j) - '0';
				if (num <= 0)
					board[i][j] = new Variable("(" + i + "," + j + ")");
				else
					board[i][j] = Constant.of(num);
			}
		}
	}

	public SudokuBoard() {
		this(9);
		for (int i = 0; i < length; i++) {
			board[i] = new Value[length];
			for (int j = 0; j < length; j++) {
				board[i][j] = new Variable("(" + i + "," + j + ")");
			}
		}
	}

	SudokuBoard(final int length) {
		this.length = length;
		lengthConstant = Constant.of(length);
		barSum = Constant.of(length * (length + 1) / 2);
		this.board = new Value[length][length];
		// for (int i = 0; i < length; i++) {
		// 	board[i] = new Variable[length];
		// 	for (int j = 0; j < length; j++) {
		// 		board[i][j] = new Variable("(" + i + "," + j + ")");
		// 	}
		// }
		conditions = VariableStatement.TAUTOLOGY;
	}

	public void applyDefaultConditions() {
		for (final Value[] row : board) {
			Expression rowConds = Constant.ZERO;
			for (int i = 0; i < length; i++) {
				final Value tile = row[i];
				for (int j = i + 1; j < length; j++)
					conditions.and(tile.notEquates(board[i][j]));
				Statement numRange = tile.equates(Constant.ONE);
				for (int n = 2; n <= length; n++)
					numRange = numRange.or(tile.equates(Constant.of(n)));
				conditions = conditions.and(numRange);
				rowConds = rowConds.plus(tile);
			}
			conditions = conditions.and(rowConds.equates(barSum));
		}
		for (int j = 0; j < length; j++) {
			Expression rowConds = Constant.ZERO;
			for (int i = 0; i < length; i++) {
				rowConds = rowConds.plus(board[i][j]);
			}
			conditions = conditions.and(rowConds.equates(barSum));
		}
		final double N = Math.sqrt(length);
		if (N % 1 == 0) {
			final int boxSize = (int) N;
			for (int boxRow = 0; boxRow < length; boxRow += boxSize)
				for (int boxCol = 0; boxCol < length; boxCol += boxSize) {
					Expression rowConds = Constant.ZERO;
					for (int i = 0; i < length; i++) {
						for (int j = i + 1; j < length; j++) {
							if (i % boxSize != j % boxSize && i / boxSize != j / boxSize) // already covered in row/col
								conditions.and(board[i % boxSize + boxRow][i / boxSize + boxCol]
													   .notEquates(board[j % boxSize + boxRow][j / boxSize + boxCol]));
						}
						rowConds = rowConds.plus(board[i % boxSize + boxRow][i / boxSize + boxCol]);
					}
					conditions = conditions.and(rowConds.equates(barSum));

					// for (int r = 0; r < boxSize; r++)
					// 	for (int c = 0; c < boxSize; c++)
				}
		}
	}

	public Statement getConditions() {
		return conditions;
	}

	public void fillIn(int r, int c, Constant num) {
		board[r][c] = num;
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
				 .append(board[i][j] instanceof Constant constant ? constant : ' ')
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
