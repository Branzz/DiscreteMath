package bran.parser.composition;

import bran.tree.compositions.Composition;
import bran.tree.compositions.expressions.Expression;
import bran.tree.compositions.statements.Statement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CommaSeparatedComposition {

	private final List<Composition> compositions;

	boolean empty;
	boolean checkedType = false;
	boolean isExpressions;
	boolean isStatements;
	private List<Expression> asExpressions;
	private List<Statement> asStatements;

	public CommaSeparatedComposition() {
		this.compositions = new ArrayList<>();
	}

	public CommaSeparatedComposition(List<Composition> compositions) {
		this.compositions = (compositions.size() == 1 && compositions.get(0) != Composition.empty())
									? Collections.emptyList()
									: compositions;
	}

	public CommaSeparatedComposition(Composition singleton) {
		this();
		if (singleton != Composition.empty())
			compositions.add(singleton);
	}

	public CommaSeparatedComposition(Composition current, CommaSeparatedComposition proceeding) {
		this();
		compositions.add(current);
		compositions.addAll(proceeding.compositions);
	}

	public boolean isSingleton() {
		return compositions.size() == 1;
	}

	public Composition getAsSingleton() {
		return compositions.get(0);
	}

	public List<Composition> getFull() {
		return compositions;
	}

	public List<Composition> compositions() {
		return compositions;
	}

	/**
	 * MUTABLE!!
	 */
	public boolean isExpressions() {
		checkType(true);
		return isExpressions;
	}

	/**
	 * MUTABLE!!
	 */
	public boolean isStatements() {
		checkType(false);
		return isStatements;
	}

	/**
	 * if all elements are of type Expression, asExpressions is converted and asStatements is null, and vice versa
	 * if the elements are of mixed types, then asExpressions and asStatemnts will be null
	 */
	private void checkType(boolean desiredExpressionType) {
		if (checkedType)
			return;
		if (compositions.size() == 0) {
			empty = true;
			return;
		}
		isExpressions = true;
		isStatements = true;
		asExpressions = new ArrayList<>();
		asStatements = new ArrayList<>();
		Consumer<Composition> adder = c -> {
			if (c instanceof Expression expression) {
				isStatements = false;
				if (isExpressions) {
					asExpressions.add(expression);
				}
			} else if (c instanceof Statement statement) {
				isExpressions = false;
				if (isStatements) {
					asStatements.add(statement);
				}
			}
		};
		for (Composition c : compositions) {
			 if (c instanceof LazyTypeVariable variable) {
				if (!variable.isFound()) {
					if (desiredExpressionType) {
						asExpressions.add(variable.foundAsExpression());
					} else {
						asStatements.add(variable.foundAsStatement());
					}
				} else {
					adder.accept(variable.getAsComposition());
				}
			} else {
			 	adder.accept(c);
			 }
		}
		if (!isStatements)
			asStatements = null;
		if (!isExpressions)
			asExpressions = null;
		checkedType = true;
	}

	@Override
	public String toString() {
		return compositions.stream().map(Composition::toString).collect(Collectors.joining(","));
	}

	public List<Expression> asExpressions() {
		return asExpressions;
	}

	public List<Statement> asStatements() {
		return asStatements;
	}

}
