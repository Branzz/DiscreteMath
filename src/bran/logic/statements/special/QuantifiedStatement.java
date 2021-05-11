package bran.logic.statements.special;

public abstract class QuantifiedStatement extends SpecialStatement {

	@Override
	protected abstract boolean getTruth();

	public abstract boolean exhaustiveProof();

	public abstract String exhaustiveProofString();

}
