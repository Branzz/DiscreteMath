package bran.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class Scope {

	Scope parentScope;
	Map<String, Object> scope;

	public Scope() {
		this.parentScope = null; // highest parent
		this.scope = new HashMap<>();
	}

	public Scope(Scope parentScope) {
		this.parentScope = parentScope; // doesn't check if it forms a loop
		this.scope = new HashMap<>();
	}

	public Object find(String key) {
		Optional.ofNullable(scope.get(key)).or(() -> parentScope == null ? Optional.empty() : Optional.of(parentScope.find(key)));
		return scope.getOrDefault(key, parentScope == null ? null : parentScope.find(key));
	}

	public static <T> Optional<T> orNullable(Optional<T> optional, Supplier<T> or) { // TODO Util
		return optional.or(() -> Optional.ofNullable(or.get()));
	}

}
