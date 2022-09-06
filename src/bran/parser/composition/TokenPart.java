package bran.parser.composition;

import java.util.List;

public final record TokenPart(boolean splittable, List<StringPart> prefixes) {

	public StringPart lastPrefix() {
		if (prefixes.isEmpty())
			return null;
		return prefixes.get(prefixes.size() - 1);
	}

}
