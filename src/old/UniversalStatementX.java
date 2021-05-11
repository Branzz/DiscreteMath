package old;

// import bran.combinatorics.Combinator;
// import bran.combinatorics.CombinatorList;
// import bran.combinatorics.Combinatorics;
// import bran.logic.statements.Statement;
// import bran.logic.statements.special.SpecialStatement;
// import bran.logic.statements.special.UniversalStatementArguments;
// import bran.logic.tree.Equivalable;
// import bran.logic.tree.Holder;
// import bran.logic.tree.Leaf;
// import bran.logic.tree.TreePart;
// import bran.sets.FiniteSet;
//
// import java.util.Arrays;
// import java.util.List;
// import java.util.function.Supplier;
// import java.util.stream.Collectors;
// import java.util.stream.Stream;
//
// import static bran.logic.statements.operators.DisplayStyle.*;
//
// public class UniversalStatementX<E extends Holder & Equivalable<? super E>> extends SpecialStatement {
//
// 	private final int argumentSize;
// 	private final UniversalStatementArguments<E> universalStatement;
// 	private final E[] variables;
// 	private final FiniteSet<E> domain;
// 	private final Supplier<E> argSupplier;
// 	private boolean proven;
//
// 	public static final String[] forAllSymbols = { "\u2200", "\u2200", "\u2200", "\u2200", "\u2200", "\u2200" };
// 	public static final String[] inSetSymbols  = { "\u2208", "\u2208", "\u2208", "\u2208", "\u2208", "\u2208" };
//
// 	public UniversalStatementX(final int argumentSize, final UniversalStatementArguments<E> universalStatement, final FiniteSet<E> domain, Supplier<E> argSupplier, final boolean proven) {
// 		this.argumentSize = argumentSize;
// 		this.universalStatement = universalStatement;
// 		this.variables = (E[]) Stream.generate(argSupplier).limit(argumentSize).toArray();
// 		this.domain = domain;
// 		this.argSupplier = argSupplier;
// 		this.proven = proven;
// 	}
//
// 	@Override
// 	protected boolean getTruth() {
// 		// return proven;
// 		return exhaustiveProof();
// 	}
//
// 	public boolean exhaustiveProof() {
// 		CombinatorList<E> prover = new CombinatorList<>(argumentSize, domain.stream().toList(), argSupplier);
// 		// E[] z = (E[]) Stream.generate(argSupplier).limit(argumentSize).toArray();
// 		for (List<E> es : prover.getCombinations()) {
// 			var uS = universalStatement.state(toArray(es));
// 			System.out.println(uS);
// 			if (uS.truth())
// 				return true;
// 		}
// 		return false;
// 	}
//
// 	private static <T> T[] toArray(List<T> list) {
// 		T[] toR = (T[]) java.lang.reflect.Array.newInstance(list.get(0).getClass(), list.size());
// 		for (int i = 0; i < list.size(); i++) {
// 			toR[i] = list.get(i);
// 		}
// 		return toR;
// 	}
//
// 	@Override
// 	public String toString() {
// 		StringBuilder sb = new StringBuilder();
// 		sb.append(switch (displayStyle) {
// 			case NAME -> "For all ";
// 			case LOWERCASE_NAME -> "for all ";
// 			default -> forAllSymbols[displayStyle.index()];
// 		});
// 		sb.append(Arrays.stream(variables).map(Object::toString).collect(Collectors.joining(",")));
// 		sb.append(switch (displayStyle) {
// 			case NAME, LOWERCASE_NAME -> " in the set of ";
// 			default -> inSetSymbols[displayStyle.index()];
// 		});
// 		sb.append(domain);
// 		return sb.append(" ").append(universalStatement.state(variables)).toString();
// 	}
//
// 	// @Override
// 	// public Statement clone() {
// 	// 	return new UniversalStatement<E>(argumentSize, universalStatement, (FiniteSet<E>) domain.clone(), argSupplier, proven);
// 	// }
//
// }
