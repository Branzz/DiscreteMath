package bran.sets.numbers.godel;

import bran.sets.numbers.NumberLiteral;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PublicPrimes {

	private static final PublicPrimes primes = new PublicPrimes();

	public static PublicPrimes getInstance() {
		return primes;
	}

	private final List<NumberLiteral> computedPrimes;

	public PublicPrimes() {
		computedPrimes = new ArrayList<>();
		computedPrimes.add(new NumberLiteral(2));
	}

	public NumberLiteral get(int index) {
		while (index >= computedPrimes.size())
			calculateNext();
		return computedPrimes.get(index);
	}

	NumberLiteral last() {
		return computedPrimes.get(computedPrimes.size() - 1);
	}

	public void computeTo(int value) {
		while (value >= last().intValue())
			calculateNext();
	}

	public void calculateNext() {
		int current = last().intValue() + 1;
		while(isComposite(current))
			current++;
		computedPrimes.add(new NumberLiteral(current));
	}

	private boolean isComposite(final int current) {
		int sqrt = (int) Math.sqrt(current);
		for (int i = 0; computedPrimes.get(i).doubleValue() <= sqrt; i++)
			if (current / computedPrimes.get(i).doubleValue() % 1 == 0)
				return true;
		return false;
	}

	public int indexOf(final int value) {
		return indexOfRange(value, 0);
	}

	int indexOfRange(int value, int start) {
		if (value > last().intValue()) {
			computeTo(value);
			return computedPrimes.size() - 1;
		}
		int low = start;
		int high = computedPrimes.size() - 1;
		while (low <= high) {
			int mid = (low + high) >>> 1;
			long midVal = computedPrimes.get(mid).longValue();
			if (midVal < value)
				low = mid + 1;
			else if (midVal > value)
				high = mid - 1;
			else
				return mid;
		}
		return -1;
	}

}
