package bran.tree.structure.mapper;

import bran.parser.matching.Pattern;
import bran.parser.matching.Tokenable;

public interface Patternable {

	/**
	 * for constructable objects
	 */
	Pattern<? extends Tokenable> pattern();

}
