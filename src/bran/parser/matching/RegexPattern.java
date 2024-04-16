package bran.parser.matching;

import java.util.function.Function;
public class RegexPattern extends TokenPattern {

	public RegexPattern(Token[] inputTokens, Token[] outputTokens, Function reduce, int level) {
		super(inputTokens, outputTokens, reduce, level);
	}

}
