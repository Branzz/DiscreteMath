package bran.parser.matching;

import bran.exceptions.IllegalArgumentAmountException;
import bran.parser.abst.*;
import bran.tree.structure.mapper.Associativity;
import bran.tree.structure.mapper.OrderedOperator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * Sequence of tokens which can be reduced (or expanded) to other tokens (or to nothing)
 * Different patterns may work on the same StringPart, so the type depends on the context
 * When given the needed type of some range, test all known patterns which return the type of this token/token
 *
 */
public class TokenPattern<R extends Tokenable> extends AbstractPattern { // TODO super pattern for tree and pre pattern for non Tokenable conversions

	private final int size;
	private final Token[] inputTokens;

//	PatternBuilder
	public TokenPattern(Token[] inputTokens, Token[] outputTokens, Function<EnumerableRange<StringPart>, StringPart[]> reduce, int level) {
		super(outputTokens, reduce, level);
		this.size = inputTokens.length;
		this.inputTokens = inputTokens;
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
						 return AbstractCompiler.asArray(EnumerableRange.reduce(e).withInstance(
						 			constructor.newInstance(
										 IntStream.range(0, tokenClasses.length)
												 .mapToObj(i -> new StringPart("", 0, 0).withInstance(e.at(i).getTokenInstance(tokenClasses[i]))))
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

		public TokenPattern<RR> build() {
			return new TokenPattern<>(tokens, null, reduce, level);
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
			// this.reduce = e -> AbstractCompiler.asArray(EnumerableRange.reduce(e).casted(reduce.apply(e), token));
			return this;
		}

		public PatternBuilder<RR> reduceCasting(Function<EnumerableRange<?>, Object> reduce) {
//			// TODO this.reduce = e ->
			return this;
		}

//		public PatternBuilder<RR> tokens(Token... tokens) {
//			this.tokens = tokens;
//			return this;
//		}
//		public PatternBuilder<RR> tokens(Class... tokenClasses) {
//			this.tokens = Arrays.stream(tokenClasses).map(CompositionTokens::token).toArray(Token[]::new);
//			return this;
//		}

		public PatternBuilder<RR> tokens(Object... tokenClasses) {
			this.tokens = Arrays.stream(tokenClasses)
								  .map(o -> {
									  if (o instanceof Token t)
										  return t;
									  else if (o instanceof Class<?> c)
										  return CompositionTokens.token(c);
									  else if (o instanceof String s)
										  return new SimpleToken<>(s);
									  // TODO else if (o instanceof Mapper m)
									  // 	return new SimpleToken(m);
									  else
										  throw new IllegalArgumentAmountException("Input must be token or class");
								  })//.map(o -> (Token) o)
					.toArray(Token[]::new);
			return this;
		}
	}

	public int size() {
		return size;
	}

	public Token[] tokens() {
		return inputTokens;
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

	public boolean matches(String text, int start, int end, List<List<StringPart>> cache) {
		return matches(new ListRange<>(cache.get(start), 0));
	}

	public StringPart[] convert(String text, int start, int end, List<List<StringPart>> cache) {
		return reduce(new ListRange<>(cache.get(start), 0));
	}

	public boolean matches(EnumerableRange<StringPart> arrayRange) {
		// for (int i = 0; i < size; i++) {
		// 	if (!tokens[i].matches(arrayRange.at(i, associativity()).string()))
		// 		return false;
		// }
		// return true;
			return arrayRange.inRange(inputTokens.length) &&
				   IntStream.range(0, inputTokens.length)
							.allMatch(i -> inputTokens[i].matches(arrayRange.at(i).string()));

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
