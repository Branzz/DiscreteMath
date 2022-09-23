package bran.parser.abst;

import bran.exceptions.ParseException;
import bran.parser.matching.Pattern;
import bran.parser.matching.Pattern.PatternBuilder;
import bran.parser.matching.Token;
import bran.parser.matching.Tokenable;
import bran.tree.compositions.Composition;
import bran.tree.compositions.expressions.functions.FunctionExpression;
import bran.tree.compositions.expressions.functions.rec.RecFunctionExpression;
import bran.tree.compositions.expressions.operators.ExpressionOperation;
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

public class CompositionTokens {

	// public static final Token<Statement> Statement_TOKEN		= new SimpleToken<>(Statement.class);
	// public static final Token<Expression> Expression_TOKEN		= new SimpleToken<>(Expression.class);
	// public static final Token<Set> Set_TOKEN					= new SimpleToken<>(Set.class);
	// public static final Token<Composition> Composition_TOKEN	= new SimpleToken<>(Composition.class, Statement_TOKEN, Expression_TOKEN, Set_TOKEN);

	public static final Token LEFT_PAREN	= new SimpleToken("\\(");
	public static final Token RIGHT_PAREN	= new SimpleToken("\\)");
	public static final Token COMMA			= new SimpleToken(",");
	public static final Token DELIMIT = new SimpleToken("\\s+");
	public static final Token FUNCTION_COMPOSITION = new SimpleToken("\\."); // Haskell
	public static final Token FUNCTION_SEPARATION = new SimpleToken("\\$");

	private final static Map<Class, Token> tokens = new HashMap<>();
	private final static Map<Class, Token> constructedTokens = new HashMap<>();
	public final static Map<Class, Pattern> constructedTokenPatterns = new HashMap<>();

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
						 		return (Stream.of(m.invoke(null)));
							} catch (IllegalAccessException | InvocationTargetException e) {
								throw new ParseException("bad reflection analysis " + e.getMessage());
							}
						 })
						 .map(o -> (O) o)
		).forEach(o -> {
			addToken(new SimpleToken<>(actualOpClass, o.getSymbols()));
			constructedTokenPatterns.put(c, new Pattern<>(o.precedence(), constructor, parameterTypes));
		});
	}

	private static void addToken(SimpleToken token) {
		constructedTokens.put(token.representingClass(), token);
	}

	public static final AbstractCompiler<TreePart> compositionCompiler;

	static {
		Set<Pattern> patterns = new HashSet<>();
		patterns.addAll(constructedTokenPatterns.values());
		Collections.addAll(patterns,
				new PatternBuilder<>(16)
						.tokens(LEFT_PAREN, token(Composition.class), RIGHT_PAREN)
						.pureReduceToOne(e -> (Composition) e.at(1).actual(), token(Composition.class)).build()

//				new PatternBuilder<>(0)
//						.tokens(token(TreePart.class), DELIMIT, token(TreePart.class))
//						.reduce(e -> new TreePart[] {((TreePart) e.at(0).actual()), ((TreePart) e.at(2).actual())}).build()
		);
		compositionCompiler = new AbstractCompiler<>(patterns, true);
	}

}
