package bran.parser.abst;

import bran.parser.matching.EnumerableRange;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class StringPart {

	//	private final EnumerableRange<Character> string = new ArrayRange<>();
	protected final String string; // TODO lazily create? (PatternBuilder greedily)
	protected final int from;
	protected final int to;

	public StringPart(String string, int from, int to) {
		this.string = string;
		this.from = from;
		this.to = to;
	}

	public StringPart(int from, int to) {
		this.string = null;
		this.from = from;
		this.to = to;
	}

	public String string() {
		return string;
	}

	public int from() {
		return from;
	}

	public int to() {
		return to;
	}

	public StringPart withInstance(Object instance) {
		instanceCache.add(instance);
		return this;
	}

//	private static class ClassInstance
	private final Set<Object> instanceCache = new HashSet<>();

	public <T> T getTokenInstance(Class<T> tClass) { // it should match by now TODO ?
		return instanceCache.stream()
				.filter(tClass::isInstance)
				.findAny()
				.map(t -> (T) t)
				.or(() -> Optional.ofNullable(CompositionTokens.constructedTokenPatterns.get(tClass))
								  .map(p -> {
									StringPart[] reduce = p.reduce(EnumerableRange.of(this));
									if (reduce.length != 1)
										return Optional.empty();
									else
										return reduce[0].instanceCache.stream().findFirst();
								  }).orElseThrow()
								  .map(t -> (T) t))
					   .map(t -> { instanceCache.add(t); return t; })
				.orElseThrow();

	}

	public Set<Object> getInstanceCache() {
		return instanceCache;
	}

	@Override
	public String toString() {
		return string();
	}

}
