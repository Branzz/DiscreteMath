package bran.parser.abst;

import java.util.List;
import java.util.Objects;

public record TokenPart<T>(boolean splittable, List<StringPart<T>> prefixes) {

	public StringPart<T> lastPrefix() {
		if (prefixes.isEmpty())
			return null;
		return prefixes.get(prefixes.size() - 1);
	}


	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null || obj.getClass() != this.getClass())
			return false;
		var that = (TokenPart<T>) obj;
		return this.splittable == that.splittable && Objects.equals(this.prefixes, that.prefixes);
	}

	@Override
	public int hashCode() {
		return Objects.hash(splittable, prefixes);
	}

	@Override
	public String toString() {
		return "TokenPart2[" + "splittable=" + splittable + ", " + "prefixes=" + prefixes + ']';
	}


}
