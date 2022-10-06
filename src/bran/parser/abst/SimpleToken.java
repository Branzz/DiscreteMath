package bran.parser.abst;

import bran.parser.matching.Token;
import org.intellij.lang.annotations.RegExp;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SimpleToken<T> implements Token {

	private Matcher matcher;
	private Set<Token> children;
	private Class<T> representingClass;
	private Set<String> symbols;

	public SimpleToken(@RegExp String regex, Set<Token> children) {
		this.matcher = Pattern.compile(regex).matcher("");
		this.children = children;
	}

	public SimpleToken(Class<T> representingClass, String[] symbols) {
		this.representingClass = representingClass;
		this.symbols = Set.of(symbols);
	}

	public SimpleToken(Set<Token> children) {
		this.children = children;
	}

	public SimpleToken(Class<T> representingClass, Token... children) {
		this.representingClass = representingClass;
		this.children = Arrays.stream(children).collect(Collectors.toSet());
	}

	public SimpleToken(@RegExp String regex) {
		this(regex, Collections.emptySet());
	}

	@Override
	public boolean matches(String token) {
		return (matcher != null && matcher.reset(token).matches())
				|| (symbols != null && symbols.contains(token));
	}

	@Override
	public boolean equalSubToken(Token child) {
		return (children != null && children.contains(child))
				|| (child instanceof ConstantToken cT && cT.equalSubToken(this)); // TODO strange
	}

	public Class<T> representingClass() {
		return representingClass;
	}

}
