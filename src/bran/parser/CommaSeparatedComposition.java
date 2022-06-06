package bran.parser;

import bran.tree.compositions.Composition;
import bran.tree.compositions.expressions.Expression;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommaSeparatedComposition {

	private final List<Composition> compositions;

	CommaSeparatedComposition() {
		this.compositions = new ArrayList<>();
	}

	CommaSeparatedComposition(List<Composition> compositions) {
		this.compositions = compositions;
	}

	CommaSeparatedComposition(Composition singleton) {
		this();
		compositions.add(singleton);
	}

	public CommaSeparatedComposition(Composition current, CommaSeparatedComposition proceeding) {
		this();
		compositions.add(current);
		compositions.addAll(proceeding.compositions);
	}

	public boolean isSingleton() {
		return compositions.size() == 1;
	}

	public Composition getAsSingleton() {
		return compositions.get(0);
	}

	public List<Composition> getFull() {
		return compositions;
	}

	public List<Composition> compositions() {
		return compositions;
	}

	@Override
	public String toString() {
		return compositions.stream().map(Composition::toString).collect(Collectors.joining(","));
	}

}
