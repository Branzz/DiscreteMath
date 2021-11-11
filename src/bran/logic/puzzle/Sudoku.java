package bran.logic.puzzle;

public class Sudoku {

	public static void main(String[] args) {
		SudokuBoard board = new SudokuBoard("400230000259670018031980264090008720000409031500100000805000140000002007017800000");
		board.applyDefaultConditions();
		System.out.println(board);
		System.out.println(board.getConditions());
		System.out.println(board.getConditions().simplified());
		System.out.println(board.applyTileConditions());
		System.out.println(board.applyTileConditions().simplified());
		board.solve();
		board.printConditions();
	}

}
