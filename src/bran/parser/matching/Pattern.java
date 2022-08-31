package bran.parser.matching;

import java.util.function.Function;

public class Pattern<R> {

	Class[] types;
	Function<Object[], R> reduce;
	int presedence;
	boolean leftAssociativity;

}
