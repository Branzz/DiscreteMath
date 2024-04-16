package bran.parser.abst;

import bran.exceptions.ParseException;
import bran.parser.NumberSuperScript;
import bran.parser.composition.CommaSeparatedComposition;
import bran.parser.matching.TokenPattern;
import bran.parser.matching.TokenPattern.PatternBuilder;
import bran.parser.matching.Token;
import bran.parser.matching.Tokenable;
import bran.tree.compositions.Composition;
import bran.tree.compositions.expressions.Expression;
import bran.tree.compositions.expressions.functions.FunctionExpression;
import bran.tree.compositions.expressions.functions.MultiArgFunction;
import bran.tree.compositions.expressions.functions.rec.RecFunctionExpression;
import bran.tree.compositions.expressions.operators.ExpressionOperation;
import bran.tree.compositions.expressions.values.Constant;
import bran.tree.compositions.sets.SetOperation;
import bran.tree.compositions.sets.SetStatement;
import bran.tree.compositions.sets.UnarySet;
import bran.tree.compositions.sets.regular.ElementSetStatement;
import bran.tree.compositions.statements.StatementOperation;
import bran.tree.compositions.statements.UnaryStatement;
import bran.tree.compositions.statements.special.equivalences.EquivalenceImpl;
import bran.tree.structure.MonoTypeFork;
import bran.tree.structure.PolyTypeChildBranch;
import bran.tree.structure.TreePart;
import bran.tree.structure.mapper.OrderedOperator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static bran.parser.abst.AbstractCompiler.asArray;
import static bran.tree.compositions.expressions.functions.MultiArgFunction.ABS;
import static bran.tree.compositions.expressions.operators.ArithmeticOperator.MUL;
import static bran.tree.compositions.expressions.operators.ArithmeticOperator.POW;

public class CompositionTokens {

	// public static final Token<Statement> Statement_TOKEN		= new SimpleToken<>(Statement.class); // AUTOed
	// public static final Token<Expression> Expression_TOKEN	= new SimpleToken<>(Expression.class);
	// public static final Token<Set> Set_TOKEN					= new SimpleToken<>(Set.class);
	// public static final Token<Composition> Composition_TOKEN	= new SimpleToken<>(Composition.class, Statement_TOKEN, Expression_TOKEN, Set_TOKEN);

	public static final Token LEFT_PAREN = new SimpleToken("(");
	public static final Token RIGHT_PAREN = new SimpleToken(")");
	public static final Token COMMA = new SimpleToken(",");
	public static final Token DELIMIT = new SimpleRegexToken("\\s+");
	public static final Token FUNCTION_COMPOSITION = new SimpleToken("."); // Haskell
	public static final Token FUNCTION_SEPARATION = new SimpleToken("$");

	private final static Map<Class, Token> tokens = new HashMap<>();
	private final static Map<Class, Token> constructedTokens = new HashMap<>();
	/**
	 * UPDATING AFTER STATIC INITIALIZATION WILL NOT BE INCLUDED IN MASTER PATTERN SET
	 */
	public final static Map<Class, TokenPattern> constructedTokenPatterns = new HashMap<>();

	public static <T> Token token(Class<T> tokenClass) {
		return tokens.computeIfAbsent(tokenClass, SimpleToken<T>::new);
	}

	static {
		generateForkPatterns();
	}

	private static <T extends Tokenable & PolyTypeChildBranch<O>, O extends OrderedOperator> void generateForkPatterns() {
		Stream.of(ElementSetStatement.class, EquivalenceImpl.class, ExpressionOperation.class,
				  SetOperation.class, SetStatement.class, StatementOperation.class,
				  FunctionExpression.class, RecFunctionExpression.class, UnarySet.class, UnaryStatement.class)
			  .map((Class c) -> (Class<T>) c)
			  .forEach(CompositionTokens::addPatternFrom);
		// get all patterns and put in branching tree?
	}

	@SuppressWarnings("unchecked cast")
	private static <T extends Tokenable & PolyTypeChildBranch<O>, O extends OrderedOperator> void addPatternFrom(Class<T> c) {
		// Method patternMethod;
		// try {
		// 	patternMethod = c.getMethod("pattern");
		// } catch (NoSuchMethodException e) {
		// 	throw new ParseException("bad reflection analysis " + e.getMessage());
		// }
		boolean isFork = java.util.Set.of(c.getInterfaces()).contains(MonoTypeFork.class);
		Constructor<T> constructor = (Constructor<T>) AbstractCompiler.getSingleton(
				Arrays.stream(c.getDeclaredConstructors())
					  .filter(c0 -> c0.getParameterCount() == (isFork ? 3 : 2))
					  .collect(Collectors.toList()));
		// Fork: [left, op, right]; Other Branch: [op, arg]
		Class<?>[] parameterTypes = constructor.getParameterTypes();
		Class<O> actualOpClass = (Class<O>) parameterTypes[isFork ? 1 : 0];
		(actualOpClass.isEnum()
				 ? Arrays.stream(actualOpClass.getEnumConstants())
				 : Arrays.stream(actualOpClass.getDeclaredMethods())
						 .filter(m -> m.isAnnotationPresent(CompilerOp.class))
						 .flatMap(m -> {
						 	try {
						 		return (Stream.of(m.invoke(null))); // TODO null?
							} catch (IllegalAccessException | InvocationTargetException e) {
								throw new ParseException("bad reflection analysis " + e.getMessage());
							}
						 })
						 .map(o -> (O) o)
		).forEach(o -> {
			SimpleToken<O> oToken = new SimpleToken<>(actualOpClass, o.getSymbols());
			constructedTokenPatterns.put(c, new PatternBuilder<>(o.precedence(),
																 constructor,
																 addToken(oToken),
																 parameterTypes).build());
			for (String symbol : o.getSymbols()) {
				symbolTokens.putIfAbsent(symbol, new HashSet<>());
				symbolTokens.get(symbol).add(oToken);
			}
		});
	}

	static final Map<String, Set<Token>> symbolTokens = new HashMap<>(); // only has enum symbols

	public static Set<Token> getTokenSymbols(String tokenSymbol) { // O(1)
		return symbolTokens.get(tokenSymbol);
	}

	private static Token addToken(SimpleToken token) {
		return constructedTokens.put(token.representingClass(), token);
	}

	public static final AbstractCompiler<TreePart> compositionCompiler;

	/**
	 * when we must pattern match on token(SuperClass.class), then check all sub classes
	 *  0 matches: pattern fails
	 *  1 match: correct
	 *  >1 match: TODO program fails?
	 * if any of them do match, TODO should this be cached?
	 */
	static {
		Set<TokenPattern> patterns = new HashSet<>();
		patterns.addAll(constructedTokenPatterns.values());
		Collections.addAll(	// (anonymous) patterns
				patterns,
				/*
				comments -> backslash -> string -> parens
				 */

				new PatternBuilder<>(0)
						.tokens(TreePart.class, DELIMIT, TreePart.class)
						.reduce(e -> asArray(e.at(0), e.at(2))).build(),
				// TODO superscript a superscript b = ^ab not ^a^b
				new PatternBuilder<>(POW) // TODO correct precedence?
						.tokens(NumberSuperScript.class)
						.reduce(e -> {
							final int value = e.at(0).getTokenInstance(NumberSuperScript.class).exponentValue();
							return asArray(
									new StringPart("^", e.at(0).from(), e.at(0).to()).withInstance(POW),
									new StringPart(Integer.toString(value), e.at(0).from(), e.at(0).to()).withInstance(Constant.of(value)));
							})
						.build(),
				new PatternBuilder<>(1)
						.tokens(LEFT_PAREN, Composition.class, RIGHT_PAREN)
						.pureReduceToOne(e -> e.at(1),
								token(Composition.class)).build(),
//				new PatternBuilder<>(16)
//						.tokens(LEFT_PAREN, Composition.class, COMMA, Composition.class, RIGHT_PAREN)
//						.reduce(e -> asArray(e.at(1), e.at(3))).build(),
				new PatternBuilder<>(MUL) // (a)(b) = a*b TODO: = a(b)
						.tokens(LEFT_PAREN, Expression.class, RIGHT_PAREN, LEFT_PAREN, Expression.class, RIGHT_PAREN)
						.pureReduceToOne(e -> e.at(1).getTokenInstance(Expression.class).times(e.at(4).getTokenInstance(Expression.class)),
								token(ExpressionOperation.class)).build(),
				new PatternBuilder<>(1) // because it's bracket-like
						.tokens("|", Expression.class, "|")
						.pureReduceToOne(e -> ABS.of(e.at(1).getTokenInstance(Expression.class)),
								token(FunctionExpression.class)).build(),
				new PatternBuilder<>(POW)
						.tokens(MultiArgFunction.class, new ConstantToken(POW), Constant.class, CommaSeparatedComposition.class)
						.pureReduceToOne(e -> (e.at(0).getTokenInstance(MultiArgFunction.class))
													  .of((e.at(3).getTokenInstance(CommaSeparatedComposition.class))
															  .asExpressions().toArray(Expression[]::new))
													  .pow(e.at(2).getTokenInstance(Constant.class)),
										 token(ExpressionOperation.class)).build()

//				new PatternBuilder<>(0)
//						.tokens(token(TreePart.class), DELIMIT, token(TreePart.class))
//						.reduce(e -> new TreePart[] {((TreePart) e.at(0)), ((TreePart) e.at(2))}).build()
		);
		compositionCompiler = new AbstractCompiler<>(patterns, true);
	}

}
