package bran.tree;

public interface Operator extends TreePart {

	String[] getSymbols();

	default Operator inverse() {
		return null;
	}

	// R of(A arg);

}
