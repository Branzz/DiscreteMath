package bran.mathexprs.treeparts;

public class Differential extends Variable {

	// Variable it's a differential of.
	final Variable difVariable;

	public Differential(final Variable difVariable, final int level) {
		super("d".repeat(level) + difVariable.getName(), true);
		this.difVariable = difVariable;
		this.level = level;
	}

	@Override
	public Expression derive() {
		return getDifferential();
	}

}
