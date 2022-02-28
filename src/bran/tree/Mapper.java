package bran.tree;

public interface Mapper extends TreePart {

	String[] getSymbols();

	default Mapper inverse() {
		return null;
	}

	// R of(A arg);

}
