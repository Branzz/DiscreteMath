package bran.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;
public class GenericTest {

	static int[][] pascal = new int[15][15];
	static int[][] dfa = new int[100][1000];
	static int[][] amountSumsTo = new int[100][1000];

	static {
		for (int i = 0; i < pascal.length; i++) {
			pascal[0][i] = 1;
			pascal[i][0] = 1;
		}
		for (int row = 1; row < pascal.length; row++)
			for (int col = 1; col < pascal[row].length; col++)
				pascal[row][col] = pascal[row - 1][col] + pascal[row][col - 1];
	}

	public static void main(String[] args) {
		// System.out.println(a(3, 8));
		IntStream.range(1, 45).forEach(n -> System.out.println(n + " " + a(3, n)));
	}

	private static int get(int k, int n) {
		return dfa[k - 1][n - 1] == 0 ? dfa[k - 1][n - 1] = a(k, n) : dfa[k - 1][n - 1];
	}

	public static int a(int k, int n) {
		int binMin = (int) (Math.log(n + .5) / Math.log(2)) + 1;
		// System.out.println(binMin + "<" + k);
		if (binMin <= k)
			return binMin;
		// System.out.println("did a");
		return get(k, n);
	}

	private static int fastCeilLog2(int n) { // TODO try treat n if max 10,000 by remove step 1
		int r, shift;
		r = 	(n > 0xFFFF ? 16 : 0); n >>= r;
		shift = (n > 0x00FF ? 8  : 0); n >>= shift; r |= shift;
		shift = (n > 0x000F ? 4  : 0); n >>= shift; r |= shift;
		shift = (n > 0x0003 ? 2  : 0); n >>= shift; r |= shift;
		return (r | (n >> 1)) + 1;
	}

	public static int a0(int k, int n) {
		// System.out.println(k + " " + n);
		if (k == 1)
			return n;

		findLowestAmountSum(dfa[k], n);

		int i = 0;
		while (pascal[k][i] < n)
			i++;
		// in reality: find the least number of possibilities that sum to the current value and
		// the amount you find replaces the "1 +" part in the return. can range from 1 to many, but
		i = Math.max(i - 1, 0);
		int level = pascal[k - 1][i];
		return 1 + get(k - 1, level);
	}

	private static int findLowestAmountSum(int[] choices, int num) {
		int end = 0;
		while (choices[end] < num)
			end++;
		int sum = 0;
		for (int i = end; i >= 0; i--) {
			if (choices[i] + sum > num)
				continue;
			sum += choices[i];
		}
		return end;
	}

	// private static int minPartitioning(int n) {
	// 	return (int) Math.ceil(Math.sqrt(.25 + 2 * n) - .5);
	// }

	// private long nCk(long n, long k) {
	// 	return fact(n) / (fact(k) * fact(n - k));
	// }

	// private long fact(final long num) {
	// 	long fact = 1;
	// 	for (long i = 2; i <= num; i++) {
	// 		fact = fact * i;
	// 	}
	// 	return fact;
	// }

}

/*
1
2
2
3
3
3
3
4
4
4
4
4
4
4
5
5
5
5
5


1  1
2  2
3  2
4  3
5  3
6  3
7  3
8  3 4 *
9  3 4 *
10 3 4 *
11 4 4
12 4 4
13 4 4
14 4 4
15 4 5 *
16 4 5 *
17 4 5 *
18 4 5 *
19 4 5 *
20 4 5 *
21 5 5
...
26 5 5
27 5 6
...
35 5 6
36 6 6
...
41 6 6
42 6 7




 */
