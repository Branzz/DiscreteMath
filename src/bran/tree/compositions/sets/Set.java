package bran.tree.compositions.sets;

import bran.exceptions.ArrayUnpopulatedException;
import bran.tree.compositions.Composition;
import bran.tree.compositions.godel.GodelBuilder;
import bran.tree.compositions.godel.GodelNumberSymbols;
import bran.tree.compositions.sets.operators.UnarySetOperator;
import bran.tree.compositions.sets.operators.SetOperator;
import bran.tree.compositions.sets.regular.*;
import bran.tree.compositions.statements.Statement;

import static bran.tree.compositions.sets.SetStatementOperator.*;
import static bran.tree.compositions.sets.regular.ElementSetRelation.*;

public interface Set<E> extends Composition { //Comparable<AbstractSet>,

	boolean subsetImpl(Set<E> s);

	default SetStatement<E> subset(Set<E> s) {
		 return new SetStatement<>(this, SUBSET, s);
	}

	boolean properSubsetImpl(Set<E> s);

	default SetStatement<E> properSubsetOf(Set<E> s) {
		return new SetStatement<>(this, PROPER_SUBSET, s);
	}

	boolean equivalentImpl(Set<E> other);

	default SetStatement<E> equivalent(Set<E> other) {
		return new SetStatement<>(this, SetStatementOperator.BIJECTION, other);
	}

	boolean containsImpl(E e);

	default Statement containsElement(E o) {
		return new ElementSetStatement<>(this, CONTAINS_ELEMENT(), o);
	}

	// public ElementSetStatement<E> containsElement(Holder<E> o) {
	// 	return new ElementSetStatement<>(this, ELEMENT_OF(), o.get());
	// }

	default boolean notContainsImpl(E e) {
		return !containsImpl(e);
	}

	default ElementSetStatement<E> notContains(E o) {
		return new ElementSetStatement<>(this, NOT_CONTAINS_ELEMENT(), o);
	}

	Set<E> complementImpl();

	default UnarySet<E> complement() {
		return new UnarySet<>(UnarySetOperator.COMPLEMENT, this);
	}

	Set<E> intersectionImpl(Set<E> s);

	default SetOperation<E> intersection(Set<E>... s) {
		return operation(SetOperator.INTERSECTION, s);
	}

	Set<E> unionImpl(Set<E> s);

	default SetOperation<E> union(Set<E>... s) {
		return operation(SetOperator.UNION, s);
	}

	Set<E> symmetricDifferenceImpl(Set<E> s);

	default SetOperation<E> symmetricDifference(Set<E>... s) {
		return operation(SetOperator.SYMMETRIC_DIFFERENCE, s);
	}

	// Object clone();

	// int compareTo(AbstractSet s);

	// abstract boolean equals(Object o);

	// public abstract String toString();

	 @Override
	 default void replaceAll(Composition original, Composition replacement) {
		 // do nothing
	 }

	 default SetOperation<E> operation(SetOperator o, Set<E>... s) {
		if (s.length < 0)
			throw new ArrayUnpopulatedException();
		if (s.length == 0)
			return new SetOperation<>(this, o, this);
		SetOperation<E> combinedStatements = new SetOperation<>(this, o, s[0]);
		for (int i = 1; i < s.length; i++)
			combinedStatements = new SetOperation<>(combinedStatements, o, s[i]);
		return combinedStatements;
	}


//	LineStatement self() {
//		return new LineStatement(this.clone(), true);
//	}


//	default OperationSet nand(AbstractSet... s) {
//		return operation(Operator.NAND, s);
//	}
//
//	default OperationSet nor(AbstractSet... s) {
//		return operation(Operator.NOR, s);
//	}

	@Override
	default Composition simplified() {
		 return null;
	}

	// @Override
	// public int compareTo(final Composition statement) {
		//  return 0;
	// }

	@Override
	default void appendGodelNumbers(final GodelBuilder godelBuilder) {
		 godelBuilder.push(GodelNumberSymbols.SYNTAX_ERROR);
	}

	static <EE> Set<EE> emptySet() {
		 return new EmptySet<>();
	}

	//	default OperationSet xnor(AbstractSet... s) {
//		return operation(Operator.XNOR, s);
//	}
//
//	default OperationSet implies(AbstractSet s) {
//		return operation(Operator.IMPLIES, s);
//	}

	@Override
	default String toFullString() {
		return toString();
	}

	@Override
	String toString();

}
