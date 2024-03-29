package bran.combinatorics;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class CombinatorList<E> {

	final int arguments;
	final List<E> choices;

	public static List<List<Integer>> basicCombinations(int size) {
		return new CombinatorList<>(size, IntStream.range(0, size).boxed().collect(toList())).getCombinations();
	}

	public CombinatorList(final int arguments, final List<E> choices) {
		this.arguments = arguments;
		this.choices = choices;
	}

	public List<List<E>> getCombinationsNoRepeats() { // TODO
		List<List<E>> combinations = new ArrayList<>(); // N!; only goes up to 12
		Counter counter = new Counter(arguments, choices.size());
		while (counter.hasNext()) {
			// List<E> combination = new ArrayList<>(arguments);
			// for (int j : counter.next())
			// 	combination.add(choices.get(j));
			combinations.add(IntStream.of(counter.next()).mapToObj(choices::get).collect(Collectors.toList()));
		}
		return combinations;
	}

	public List<List<E>> getCombinations() {
		List<List<E>> combinations = new ArrayList<>((int) Math.pow(choices.size(), arguments));
		Counter counter = new Counter(arguments, choices.size());
		while (counter.hasNext()) {
			// List<E> combination = new ArrayList<>(arguments);
			// for (int j : counter.next())
			// 	combination.add(choices.get(j));
			combinations.add(IntStream.of(counter.next()).mapToObj(choices::get).collect(Collectors.toList()));
		}
		return combinations;
	}

}
