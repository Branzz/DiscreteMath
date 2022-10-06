package bran.parser.matching;

import bran.parser.abst.*;
import bran.tree.structure.mapper.Associativity;
import bran.tree.structure.mapper.AssociativityPrecedenceLevel;
import bran.tree.structure.mapper.OrderedOperator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

public class Pattern<R extends Tokenable> { // TODO super pattern for tree and pre pattern for non Tokenable conversions

	private final int size;
	private final Token[] tokens;
	private final Function<EnumerableRange<StringPart>, StringPart[]> reduce;
	private final AssociativityPrecedenceLevel level;

//	PatternBuilder
	public Pattern(Token[] tokens, Function<EnumerableRange<StringPart>, StringPart[]> reduce, int level) {
		this.size = tokens.length;
		this.tokens = tokens;
		this.reduce = reduce;
		this.level = AssociativityPrecedenceLevel.of(level);
	}

	public static class PatternBuilder<RR extends Tokenable> {
		private int level;
		private Token[] tokens;
		private Function<EnumerableRange<StringPart>, StringPart[]> reduce;

		public PatternBuilder(int level) {
			this.level = level;
		}

		public PatternBuilder(int level, Constructor<RR> constructor, Token token, Class... tokenClasses) {
			tokens = Arrays.stream(tokenClasses).map(CompositionTokens::token).toArray(Token[]::new);
			reduce = e -> {
					 try {
						 return AbstractCompiler.asArray(EnumerableRange.reduce(e).casted(
						 		constructor.newInstance(IntStream.range(0, tokenClasses.length)
										 .mapToObj(i -> new TypedStringPart("", 0, 0, tokenClasses[i].cast(e.at(i)), tokens[i]))),
								token
								));
					 } catch (InstantiationException | IllegalAccessException | InvocationTargetException x) {
						 x.printStackTrace();
						 throw new RuntimeException("Pattern has bad type checking");
					 }
				 };
			this.level = level;
		}

		public PatternBuilder(OrderedOperator relativeOp) {
			this.level = relativeOp.precedence();
		}

		public Pattern<RR> build() {
			return new Pattern<>(tokens, reduce, level);
		}
		public PatternBuilder<RR> reduce(Function<EnumerableRange<StringPart>, StringPart[]> reduce) {
			this.reduce = reduce;
			return this;
		}
		// public PatternBuilder<RR> pureReduce(Function<EnumerableRange<StringPart>, Object[]> reduce, Token[] tokens) {
		// 	this.reduce = e -> AbstractCompiler.asArray(EnumerableRange.reduce(e).casted(reduce.apply(e), token));
		// 	return this;
		// }
		public PatternBuilder<RR> reduceToOne(Function<EnumerableRange<StringPart>, StringPart> reduce) {
			this.reduce = e -> AbstractCompiler.asArray(reduce.apply(e));
			return this;
		}
		public PatternBuilder<RR> pureReduceToOne(Function<EnumerableRange<StringPart>, Object> reduce, Token token) {
			this.reduce = e -> AbstractCompiler.asArray(EnumerableRange.reduce(e).casted(reduce.apply(e), token));
			return this;
		}
		public PatternBuilder<RR> tokens(Token... tokens) {
			this.tokens = tokens;
			return this;
		}
		public PatternBuilder<RR> tokens(Class... tokenClasses) {
			this.tokens = Arrays.stream(tokenClasses).map(CompositionTokens::token).toArray(Token[]::new);
			return this;
		}
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

	// public boolean matches(List<TypelessStringPart> stringParts, int tokensInd) {
	// 	return matches(new ListRange<TypelessStringPart>(stringParts, tokensInd));
	// }

	public boolean matches(DynamicTokenPart tokenPart, String text, int tokensInd) {
		// tokenPart.search(tokens)
		return false;
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

	public boolean matches(EnumerableRange<StringPart> arrayRange) {
		// for (int i = 0; i < size; i++) {
		// 	if (!tokens[i].matches(arrayRange.at(i, associativity()).string()))
		// 		return false;
		// }
		// return true;
			return arrayRange.inRange(tokens.length) &&
				   IntStream.range(0, tokens.length)
							.allMatch(i -> tokens[i].matches(arrayRange.at(i).string()));

	}

	public StringPart[] reduce(List<StringPart> input, int inputInd) {
		ListRange<StringPart> listRange = new ListRange<>(input, inputInd);
		StringPart[] reduce = reduce(listRange);
		listRange.replaced(size, reduce);
		return reduce;
	}

	public StringPart[] reduce(EnumerableRange<StringPart> input) {
		return reduce.apply(input);
	}

}
