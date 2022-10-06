package bran.parser.abst;

import bran.parser.matching.Token;
import bran.tree.structure.mapper.Mapper;

import java.util.Arrays;

public class ConstantToken implements Token {

	private final Mapper m;

	public ConstantToken(Mapper m) {
		this.m = m;
	}

	@Override
	public boolean matches(String token) {
		return Arrays.asList(m.getSymbols()).contains(token); // TODO equalsIgnoreCase
	}

	@Override
	public boolean equalSubToken(Token child) {
		return child instanceof ConstantToken cT && cT.m.equals(m);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		ConstantToken that = (ConstantToken) o;

		return m.equals(that.m);
	}

	@Override
	public int hashCode() {
		return m.hashCode();
	}

}
