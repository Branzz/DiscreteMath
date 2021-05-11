package bran.mathexprs.treeparts.operators;

import bran.logic.statements.Statement;
import bran.logic.statements.VariableStatement;
import bran.logic.statements.operators.DisplayStyle;
import bran.tree.Associativity;
import bran.tree.ForkOperator;
import bran.mathexprs.treeparts.Constant;
import bran.mathexprs.treeparts.Expression;

import static bran.mathexprs.treeparts.functions.MultivariableFunction.LN;
import static bran.mathexprs.treeparts.operators.DomainSupplier.DENOM_NOT_ZERO;

import static bran.mathexprs.treeparts.operators.OperatorType.*;

public enum Operator implements ForkOperator {
	POW(E, Math::pow, (a, b) -> (a.pow(b)).times((b.div(a).times(a.derive())).plus(LN.ofS(a).times(b.derive()))), "^", "power"), // a^b * ((b/a) * da + ln(a) * db)
	MUL(MD, (a, b) -> a * b, (a, b) -> a.times(b.derive()).plus(a.derive().times(b)), "*", "times"),
	DIV(MD, (a, b) -> a / b, (a, b) -> ((b.times(a.derive())).minus(a.times(b.derive()))).div(b.squared()), DENOM_NOT_ZERO, "/", "over"),
	MOD(MD, (a, b) -> a % b, (a, b) -> Constant.of(0), DENOM_NOT_ZERO, "%", "mod"),
	ADD(AS, Double::sum, (a, b) -> a.derive().plus(b.derive()), "+", "plus"),
	SUB(AS, (a, b) -> a - b, (a, b) -> a.derive().minus(b.derive()), "-", "minus");

	private final OperatorType operatorType;
	private final Operable operable;
	private final Derivable derivable;
	private final DomainSupplier domainSupplier;
	private final String[] symbols;

	Operator(final OperatorType operatorType, final Operable operable, final Derivable derivable, final String... symbols) {
		this(operatorType, operable, derivable, (l, r) -> VariableStatement.TAUTOLOGY, symbols);
	}

	Operator(final OperatorType operatorType, final Operable operable, final Derivable derivable, final DomainSupplier domainSupplier, final String... symbols) {
		this.operatorType = operatorType;
		this.operable = operable;
		this.derivable = derivable;
		this.domainSupplier = domainSupplier;
		this.symbols = symbols;
	}

	@Override
	public int getOrder() {
		return operatorType.precedence();
	}

	@Override
	public int maxOrder() {
		return MAX_ORDER;
	}

	@Override
	public int minOrder() {
		return MIN_ORDER;
	}

	@Override
	public Associativity getDirection() {
		return operatorType.associativity();
	}

	public double operate(double left, double right) {
		return operable.operate(left, right);
	}

	public Expression derive(final Expression left, final Expression right) {
		return derivable.derive(left, right);
	}

	public Statement domain(Expression left, Expression right) {
		return domainSupplier.get(left, right);
	}

	@Override
	public String toString() {
		return switch(DisplayStyle.displayStyle) {
			case NAME -> symbols[1].toUpperCase();
			case LOWERCASE_NAME -> symbols[1];
			default -> symbols[0];
		};
	}

}
