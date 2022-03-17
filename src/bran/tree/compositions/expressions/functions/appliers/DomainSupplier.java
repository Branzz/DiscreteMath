package bran.tree.compositions.expressions.functions.appliers;

import bran.tree.compositions.statements.Statement;
import bran.tree.compositions.expressions.values.Constant;
import bran.tree.compositions.expressions.Expression;

import java.util.function.Function;

@FunctionalInterface
public interface DomainSupplier {

	Statement get(Expression... expressions);

	Function<Expression, Statement> NOT_ZERO = e -> e.notEquates(Constant.ZERO);

}
