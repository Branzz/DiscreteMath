package bran.sets.numbers.godel;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class GodelNumberFactors {

	private final GodelNumber[] godelNumbers;
	private final BigInteger number;

	public GodelNumberFactors(final GodelNumber[] godelNumbers) {
		this.godelNumbers = godelNumbers;
		int i = 0;
		BigInteger product = new BigInteger("1");
		for (GodelNumber godelNumber : godelNumbers) {
			product = product.multiply(BigInteger.valueOf((long) PublicPrimes.getInstance().get(i++).doubleValue())
												 .pow(godelNumber.number())); // doubleValue will be int
		}
		number = product;
	}

	public GodelNumberFactors(final long number) {
		this(BigInteger.valueOf(number));
	}

	public GodelNumberFactors(final BigInteger number) {
		this.number = number;
		BigInteger reduction = number;
		BigInteger currentDivisor;
		int i = 0;
		List<GodelNumber> godelNumbers = new ArrayList<>();
		GodelVariableMap variables = new GodelVariableMap();
		while (!reduction.equals(BigInteger.ZERO) && !reduction.equals(BigInteger.ONE)) {
			int factors = 0;
			currentDivisor = BigInteger.valueOf(PublicPrimes.getInstance().get(i++).longValue());
			BigInteger[] divAndRem = reduction.divideAndRemainder(currentDivisor);
			while (divAndRem[1].equals(BigInteger.ZERO)) {
				reduction = divAndRem[0];
				factors++;
				divAndRem = reduction.divideAndRemainder(currentDivisor);
			}
			// reduction = reduction.divide(currentDivisor);
			if (factors == 0)
				break;
			if (factors - 1 > GodelNumberSymbols.values().length) {
				// variables.get(GodelVariable.variableOf(factors));
				godelNumbers.add(GodelVariable.variableOf(factors));
			} else
				godelNumbers.add(GodelNumberSymbols.values()[factors - 1]);
		}
		this.godelNumbers = godelNumbers.toArray(GodelNumber[]::new);
	}

	public BigInteger getNumber() {
		return number;
	}

	public GodelNumber[] getGodelNumberArray() {
		return godelNumbers;
	}

	public String symbols() {
		return Arrays.stream(godelNumbers).map(GodelNumber::toString).collect(Collectors.joining());
	}

	@Override
	public String toString() {
		return symbols() + " Gödel Number: " + number.toString();
	}

}
