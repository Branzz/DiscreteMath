package bran.tree.compositions.statements.special.quantifier;

import bran.tree.compositions.statements.special.SpecialStatement;
public abstract class QuantifiedStatement extends SpecialStatement {

	@Override
	protected abstract boolean getTruth();

	public abstract boolean exhaustiveProof();

	public abstract String exhaustiveProofString();

}
