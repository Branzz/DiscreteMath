package bran.tree.compositions.sets.regular;

import bran.parser.abst.CompilerOp;
import bran.parser.matching.Tokenable;
import bran.tree.compositions.expressions.functions.MultiArgFunction;
import bran.tree.compositions.sets.Set;
import bran.tree.structure.mapper.AssociativityPrecedenceLevel;
import bran.tree.structure.mapper.ForkOperator;

import java.util.function.BiFunction;

import static bran.tree.compositions.sets.regular.ElementSetRelation.ElementSetRelationInfo.CONTAINS_ELEMENT;
import static bran.tree.compositions.sets.regular.ElementSetRelation.ElementSetRelationInfo.NOT_CONTAINS_ELEMENT;

public class ElementSetRelation<E> implements ForkOperator<Boolean, Set<E>, E> { // E, Set<E>

	enum ElementSetRelationInfo {
		CONTAINS_ELEMENT(0, "\u2208", "contains"),
		NOT_CONTAINS_ELEMENT(1, "\u2209", "doesn't contain");
		private final int ID;
		private final String[] symbols;
		ElementSetRelationInfo(int ID, String... symbols) {
			this.ID = ID;
			this.symbols = symbols;
		}
	}

	public static <EE> ElementSetRelation<EE> CONTAINS_ELEMENT() {
		return new ElementSetRelation<>(CONTAINS_ELEMENT, Set::containsImpl);
	}

	public static <EE> ElementSetRelation<EE> NOT_CONTAINS_ELEMENT() {
		return new ElementSetRelation<>(NOT_CONTAINS_ELEMENT, Set::notContainsImpl);
	}

	private final ElementSetRelationInfo info;
	private final BiFunction<Set<E>, E, Boolean> operator;

	private ElementSetRelation(ElementSetRelationInfo info, BiFunction<Set<E>, E, Boolean> operator) {
		this.info = info;
		this.operator = operator;
	}

	@Override
	public String[] getSymbols() {
		return info.symbols;
	}

	@Override
	public BiFunction<Set<E>, E, Boolean> operator() {
		return operator;
	}

	@Override
	public AssociativityPrecedenceLevel level() {
		return AssociativityPrecedenceLevel.of(12);
	}

	@Override
	public String toString() {
		return getSymbols()[0];
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		ElementSetRelation<?> that = (ElementSetRelation<?>) o;

		return info.ID == that.info.ID;
	}

	@Override
	public int hashCode() {
		return info.ID;
	}

	@CompilerOp
	public static ElementSetRelation[] getTokens() {
		return new ElementSetRelation[] { CONTAINS_ELEMENT(), NOT_CONTAINS_ELEMENT() };
	}

}
