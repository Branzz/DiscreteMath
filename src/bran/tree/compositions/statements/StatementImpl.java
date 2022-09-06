package bran.tree.compositions.statements;

import bran.tree.compositions.Composition;
import bran.tree.compositions.godel.GodelBuilder;

import java.util.List;

public class StatementImpl extends Statement {
	@Override public boolean isConstant() { throw new UnsupportedOperationException(); }
	@Override protected boolean getTruth() { throw new UnsupportedOperationException(); }
	@Override public String toFullString() { throw new UnsupportedOperationException(); }
	@Override public boolean equals(final Object s) { throw new UnsupportedOperationException(); }
	@Override public List<VariableStatement> getVariables() { throw new UnsupportedOperationException(); }
	@Override public Statement simplified() { throw new UnsupportedOperationException(); }
	@Override public List<Composition> getChildren() { throw new UnsupportedOperationException(); }
	@Override public void appendGodelNumbers(final GodelBuilder godelBuilder) { throw new UnsupportedOperationException(); }
}
