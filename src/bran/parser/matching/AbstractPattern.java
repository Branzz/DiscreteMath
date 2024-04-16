package bran.parser.matching;

import bran.parser.abst.DynamicTokenPart;
import bran.parser.abst.StringPart;
import bran.tree.structure.mapper.Associativity;
import bran.tree.structure.mapper.AssociativityPrecedenceLevel;

import java.util.List;
import java.util.function.Function;

public abstract class AbstractPattern implements Pattern {

	protected final Token[] outputTokens;
	protected final Function<EnumerableRange<StringPart>, StringPart[]> reduce;
	protected final AssociativityPrecedenceLevel level;

	public AbstractPattern(Token[] outputTokens, Function<EnumerableRange<StringPart>, StringPart[]> reduce, int level) {
		this.outputTokens = outputTokens;
		this.reduce = reduce;
		this.level = AssociativityPrecedenceLevel.of(level);
	}

	public Token[] outputTokenPattern() {
		return outputTokens;
	}

	public int precedence() {
		return level.precedence();
	}

	public Associativity associativity() {
		return level.associativity();
	}

	// public boolean matches(List<TypelessStringPart> stringParts, int tokensInd)

	// public abstract boolean matches(DynamicTokenPart tokenPart, String text, int tokensInd);

	// @Override
	// abstract boolean matches(String text, int start, int end, List<List<StringPart>> cache);
	//
	// @Override
	// abstract StringPart[] convert(String text, int start, int end, List<List<StringPart>> cache);

	// public boolean matches(Token[] others)

	// public boolean matches(ArrayRange<Token> others)

	public abstract boolean matches(EnumerableRange<StringPart> arrayRange);

	public abstract StringPart[] reduce(List<StringPart> input, int inputInd);

	public abstract StringPart[] reduce(EnumerableRange<StringPart> input);

}
