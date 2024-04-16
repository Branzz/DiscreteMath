package bran.parser.matching;

import bran.parser.abst.StringPart;

import java.util.List;

public interface Pattern {

	boolean matches(String text, int start, int end, List<List<StringPart>> cache);

	StringPart[] convert(String text, int start, int end, List<List<StringPart>> cache);

	/**
	 * @return the order of expected tokens this compiles in to (could be 0)
	 */
	Token[] outputTokenPattern();

}
