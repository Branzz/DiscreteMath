package bran.parser.abst;

import bran.parser.matching.Pattern;
import bran.parser.matching.Token;

import java.util.Set;

public class SQLTokens {

	Token SELECT = new SimpleToken("SELECT");

	AbstractCompiler SQLCompiler = new AbstractCompiler(Set.of(
			new Pattern(0, e -> SELECT)
	));

}
