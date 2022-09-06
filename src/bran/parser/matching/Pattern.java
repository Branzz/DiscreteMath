package bran.parser.matching;

import bran.parser.abst.AbstractCompiler;
import bran.parser.abst.StringPart2;
import bran.parser.abst.CompositionTokens;
import bran.parser.composition.CompositionParser;
import bran.tree.structure.mapper.Associativity;
import bran.tree.structure.mapper.AssociativityPrecedenceLevel;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

public class Pattern<R extends Tokenable> { // TODO super pattern for tree and pre pattern for non Tokenable conversions

	private final int size;
	private final Token[] tokens;
	private final Function<EnumerableRange<StringPart2>, StringPart2[]> reduce;
	private final AssociativityPrecedenceLevel level;

	PatternBuilder
	public Pattern(Token[] tokens, Function<EnumerableRange<StringPart2>, StringPart2[]> reduce, int level) {
		this.size = tokens.length;
		this.tokens = tokens;
		this.reduce = reduce;
		this.level = AssociativityPrecedenceLevel.of(level);
	}

	public Pattern(int level, Function<EnumerableRange<StringPart2>, StringPart2[]> reduce, Token... tokens) {
		this(tokens, reduce, level);
	}

	public Pattern(Function<EnumerableRange<StringPart2>, StringPart2> reduce, int level, Token... tokens) {
		this(tokens, e -> AbstractCompiler.asArray(reduce.apply(e)), level);
	}

	public Pattern(int level, Function<EnumerableRange<StringPart2>, StringPart2[]> reduce, Class... tokenClasses) {
		this(Arrays.stream(tokenClasses).map(CompositionTokens::token).toArray(Token[]::new), reduce, level);
	}

	public Pattern(int level, Constructor<R> constructor, Class... tokenClasses) {
		this(Arrays.stream(tokenClasses).map(CompositionTokens::token).toArray(Token[]::new),
			 e -> {
				 try {
					 return AbstractCompiler.asArray(constructor.newInstance(IntStream.range(0, tokenClasses.length).mapToObj(i -> tokenClasses[i].cast(e.at(i)))));
				 } catch (InstantiationException | IllegalAccessException | InvocationTargetException x) {
					 x.printStackTrace();
					 throw new RuntimeException("Pattern has bad type checking");
				 }
			 },
			 level);
	}

	public int size() {
		return size;
	}

	public Token[] tokens() {
		return tokens;
	}

	public int precedence() {
		return level.precedence();
	}

	public Associativity associativity() {
		return level.associativity();
	}

	public boolean matches(List<StringPart2> stringParts, int tokensInd) {
		return matches(new ListRange<>(stringParts, tokensInd));
	}

	// public boolean matches(Token[] others) {
	// 	return others.length == size
	// 		   && IntStream.range(0, tokens.length)
	// 					   .allMatch(i -> tokens[i].equalsToken(others[i]));
	// }

	// public boolean matches(ArrayRange<Token> others) {
	// 	return others.inRange(tokens.length)
	// 		   && IntStream.range(0, tokens.length)
	// 					   .allMatch(i -> tokens[i].equalsToken(others.at(i)));
	// }

	public boolean matches(EnumerableRange<StringPart2> arrayRange) {
		// for (int i = 0; i < size; i++) {
		// 	if (!tokens[i].matches(arrayRange.at(i, associativity()).string()))
		// 		return false;
		// }
		// return true;
			return arrayRange.inRange(tokens.length) &&
				   IntStream.range(0, tokens.length)
							.allMatch(i -> tokens[i].matches(arrayRange.at(i).string()));

	}

	public StringPart2[] reduce(List<StringPart2> input, int inputInd) {
		ListRange<StringPart2> listRange = new ListRange<>(input, inputInd);
		StringPart2[] reduce = reduce(listRange);
		listRange.replaced(size, Arrays.stream(reduce).map(r -> new StringPart2<R>("", 0, 0, r, r.token())).toArray(StringPart2[]::new));
		return reduce;
	}

	public StringPart2[] reduce(EnumerableRange<StringPart2> input) {
		return reduce.apply(input);
	}

}
