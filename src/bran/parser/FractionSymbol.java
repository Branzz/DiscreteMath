package bran.parser;

import bran.tree.compositions.expressions.operators.ArithmeticOperator;
import bran.tree.structure.mapper.AssociativityPrecedenceLevel;
import bran.tree.structure.mapper.OrderedOperator;

public enum FractionSymbol implements OrderedOperator {
	OneFourth	(1D / 4D, "\u00BC"),
	OneHalf		(1D / 2D, "\u00BD"),
	ThreeFourths(3D / 4D, "\u00BE"),
	OneSeventh	(1D / 7D, "\u2150"),
	OneNinth	(1D / 9D, "\u2151"),
	OneTenth	(1D /10D, "\u2152"),
	OneThird	(1D / 3D, "\u2153"),
	TwoThirds	(2D / 3D, "\u2154"),
	OneFifth	(1D / 5D, "\u2155"),
	TwoFifths	(2D / 5D, "\u2156"),
	ThreeFifths	(3D / 5D, "\u2157"),
	FourFifths	(4D / 5D, "\u2158"),
	OneSixth	(1D / 6D, "\u2159"),
	FiveSixths	(5D / 6D, "\u215A"),
	OneEighth	(1D / 8D, "\u215B"),
	ThreeEighths(3D / 8D, "\u215C"),
	FiveEighths	(5D / 8D, "\u215D"),
	SevenEighths(7D / 8D, "\u215E");

	private final double value;
	private final String[] symbols;

	FractionSymbol(double value, String... symbols) {
		this.value = value;
		this.symbols = symbols;
	}

	public double fractionValue() {
		return value;
	}

	@Override
	public String[] getSymbols() {
		return symbols;
	}

	@Override
	public AssociativityPrecedenceLevel level() {
		return ArithmeticOperator.DIV.level();
	}

}
