package bran.tree.structure.mapper;

import bran.parser.matching.TokenPattern;
import bran.parser.matching.Tokenable;

public interface Patternable {

	/**
	 * for constructable objects
	 */
	TokenPattern<? extends Tokenable> pattern();

}
