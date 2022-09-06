package bran.tree.compositions.expressions;

import bran.tree.compositions.statements.Statement;
import bran.tree.structure.MultiBranch;
import bran.tree.structure.TreePart;
import bran.tree.structure.mapper.Mapper;

public abstract class AbstractFunctionExpression<F extends Mapper, E extends TreePart> extends Expression implements MultiBranch<F, E>  {

	public AbstractFunctionExpression(Statement... domainConditions) {
		super(domainConditions);
	}

	public AbstractFunctionExpression(Statement domainConditions) {
		super(domainConditions);
	}

	public AbstractFunctionExpression() {
	}

}
