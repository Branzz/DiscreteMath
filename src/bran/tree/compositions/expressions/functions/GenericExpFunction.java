package bran.tree.compositions.expressions.functions;

import bran.tree.Holder;
import bran.tree.compositions.Composition;
import bran.tree.compositions.expressions.Expression;
import bran.tree.compositions.expressions.values.Variable;
import bran.tree.compositions.godel.GodelBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import static bran.tree.compositions.expressions.functions.rec.RecFunction.DERIVE;

// public class GenericExpFunction extends Expression implements Holder<FunctionExpression> {
//
// 	private final String name;
// 	private FunctionExpression functionExpression;
//
// 	public GenericExpFunction(String name, FunctionExpression functionExpression) {
// 		this.name = name;
// 		this.functionExpression = functionExpression;
// 	}
//
// 	public GenericExpFunction(String name) {
// 		this.name = name;
// 	}
//
// 	@Override
// 	public FunctionExpression get() {
// 		return functionExpression;
// 	}
//
// 	@Override
// 	public void set(final FunctionExpression functionExpression) {
// 		this.functionExpression = functionExpression;
// 	}
//
// 	@Override
// 	public GenericExpFunction simplified() {
// 		return new GenericExpFunction(name, functionExpression);
// 	}
//
// 	@Override
// 	public Set<Variable> getVariables() {
// 		return Collections.emptySet();
// 	}
//
// 	@Override
// 	public double evaluate() {
// 		return 0;
// 	}
//
// 	@Override
// 	public Expression derive() {
// 		return DERIVE.function(this);
// 		// return new GenericFunction(name, expFunction.de);
// 	}
//
// 	@Override
// 	public boolean respect(final Collection<Variable> respectsTo) {
// 		return false;
// 	}
//
// 	@Override
// 	public void appendGodelNumbers(final GodelBuilder godelBuilder) {
//
// 	}
//
// 	@Override
// 	public boolean equals(final Object other) {
// 		return false;
// 	}
//
// 	@Override
// 	public String toFullString() {
// 		return name + "()";
// 	}
//
// 	@Override
// 	public void replaceAll(final Composition original, final Composition replacement) {
//
// 	}
//
// }

public class GenericExpFunction extends Expression implements Holder<ExpFunction> {

	private final String name;
	private ExpFunction expFunction;

	public GenericExpFunction(String name, ExpFunction expFunction) {
		this.name = name;
		this.expFunction = expFunction;
	}

	public GenericExpFunction(String name) {
		this.name = name;
	}

	public FunctionExpression instantiate(Expression... expressions) {
		return new FunctionExpression(expFunction, expressions);
	}

	@Override
	public ExpFunction get() {
		return expFunction;
	}

	@Override
	public void set(ExpFunction expFunction) {
		this.expFunction = expFunction;
	}

	// private <R, A> R execute(Function<A, R> method, A arg) {
	// 	if (hasRealFunction)
	// 		return method.apply(arg);
	// 	else
	// 		throw new UnsupportedOperationException();
	// }

	// private <A> void executeVoid(Consumer<A> voidMethod, A arg) {
	// 	if (hasRealFunction)
	// 		voidMethod.accept(arg);
	// 	else
	// 		throw new UnsupportedOperationException();
	// }

	@Override
	public GenericExpFunction simplified() {
		return new GenericExpFunction(name, expFunction);
	}

	@Override
	public Set<Variable> getVariables() {
		return Collections.emptySet();
	}

	@Override
	public double evaluate() {
		return 0;
	}

	@Override
	public Expression derive() {
		return DERIVE.operate(this);
		// return new GenericFunction(name, expFunction.de);
	}

	@Override
	public boolean respect(final Collection<Variable> respectsTo) {
		return false;
	}

	@Override
	public void appendGodelNumbers(final GodelBuilder godelBuilder) {

	}

	@Override
	public boolean equals(final Object other) {
		return false;
	}

	@Override
	public String toFullString() {
		return name + "()";
	}

	@Override
	public void replaceAll(final Composition original, final Composition replacement) {

	}

}

/*
public class GenericFunction implements ExpFunction {

	private String name;
	private int argAmount;
	private boolean hasRealFunction;
	private ExpFunction realFunction;

	public GenericFunction(ExpFunction realFunction) {
		loadFunction(realFunction);
	}

	public GenericFunction(String name, int argAmount) {
		this.name = name;
		this.argAmount = argAmount;
	}

	public void loadFunction(ExpFunction realFunction) {
		// this.name = realFunction.toString();
		this.realFunction = realFunction;
		this.argAmount = realFunction.getArgAmount();
		hasRealFunction = (realFunction != null);
	}

	@Override
	public double function(double... a) {
		return execute(realFunction::function, a);
	}

	@Override
	public int getArgAmount() {
		return argAmount;
	}

	@Override
	public Expression derive(Expression... exp) {
		return execute(realFunction::derive, exp);
	}

	@Override
	public Statement domain(Expression... expressions) throws IllegalArgumentAmountException {
		return execute(realFunction::domain, expressions);
	}

	@Override
	public void checkArguments(int length) throws IllegalArgumentAmountException {
		executeVoid(realFunction::checkArguments, length);
	}

	@Override
	public String[] getSymbols() {
		return new String[] { name };
	}

	@Override
	public ExpFunction inverse() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Expression inverse(int arg, Expression... expressions) {
		throw new UnsupportedOperationException();
	}

	private <R, A> R execute(Function<A, R> method, A arg) {
		if (hasRealFunction)
			return method.apply(arg);
		else
			throw new UnsupportedOperationException();
	}

	private <A> void executeVoid(Consumer<A> voidMethod, A arg) {
		if (hasRealFunction)
			voidMethod.accept(arg);
		else
			throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		return name;
	}

}

 */
