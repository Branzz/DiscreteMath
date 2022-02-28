package bran.logic.puzzle;

public class Sudoku {

	public static void main(String[] args) {
		SudokuBoard board = new SudokuBoard(
				  "400230000"
				+ "259670018"
				+ "031980264"
				+ "090008720"
			    + "000409031"
				+ "500100000"
				+ "805000140"
				+ "000002007"
				+ "017800000");
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
