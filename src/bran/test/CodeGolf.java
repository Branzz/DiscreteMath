package bran.test;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import bran.logic.statements.OperationStatement;
import bran.logic.statements.operators.Operator;

public class CodeGolf {

	static HashMap<ArrayList<Operator>, int[]> solutions = new HashMap<ArrayList<Operator>, int[]>();
	static HashSet<Integer> possibleNumbers = new HashSet<Integer>();
	static HashMap<int[], String> interpolations = new HashMap<int[], String>();
	static HashMap<String, Integer> outputs = new HashMap<String, Integer>();

	public static int op(int type, int value1, int value2) {
		switch(type) {
		case 0: return value1 + value2;
		case 1: return value1 - value2;
		case 2: return value1 * value2;
		case 3: return value1 / value2;
		case 4: return value1 & value2;
		case 5: return value1 | value2;
		case 6: return value1 ^ value2;
		case 7: return value1 % value2;
		default: return -100;
		}
	}
	public static String op(int type) {
		switch(type) {
		case 0: return "+";
		case 1: return "-";
		case 2: return "*";
		case 3: return "/";
		case 4: return "&";
		case 5: return "|";
		case 6: return "^";
		case 7: return "%";
		default: return "x";
		}
	}

	public static void main(String[] args) {

//		System.out.println(10 & 2);
//		System.out.println(9 & 1);
//		System.out.println(0b0101 | 0b0011);
//		System.out.println(0b000 | 0b000);
//		System.out.println(0b000 | 0b010);
//		System.out.println(0b100 | 0b000);
//		System.out.println(0b100 + 0b100>>1);

//		int[] nums = new int[] { 0b000, 0b010, 0b100, 0b110,
//								 0b001, 0b011, 0b101, 0b111 };
//		for (int n : nums)
//			System.out.println(n + " " + ( n ));
////		System.out.println(n + " " + ( (n|1)/3 ));
//		System.out.println();

//			for (int i = 0; i <= 7; i++) {
//				System.out.print(op(i) + ": ");
//				for (int d = 0; d <= 9; d++) {
//					System.out.print(d + ": ");
//					for (int n : nums)
//					try {
//						System.out.print(op(i, n, d) + " ");
//					} catch (Exception e) {
//						System.out.print("X ");
//					}
//					System.out.println();
//					for (int i0 = 0; i0 <= 7; i0++) {
//						System.out.print("\t" + op(i0) + ": ");
//						for (int d0 = 0; d0 <= 5; d0++) {
//							System.out.print("\t" + d0 + ": ");
//							for (int n : nums)
//							try {
//								System.out.print(op(i0, op(i, n, d), d0) + " ");
//							} catch (Exception e) {
//								System.out.print("X ");
//							}
//							System.out.println("\t");
//							
//						}
//					}
//
//				}
//			}
//		n/4&1
//		System.out.println(n + " " + (((~n/2&1)+(n&4)/4)));
//		System.out.println(Integer.parseInt("AND",  36));
//		System.out.println(Integer.parseInt("NAND",  36));
//		System.out.println(Integer.parseInt("OR",  36));
//		System.out.println(Integer.parseInt("NOR",  36));
//		System.out.println(Integer.parseInt("XOR",  36));
//		System.out.println(Integer.parseInt("XNOR",  36));

//		System.out.println(Integer.toString(1570347,  36));
		//or, and, nor, xor, nand, xnor 

//		ArrayList<Operator> potentialOprators = new ArrayList<Operator>();
//		for (Operator o : new Operator[] { Operator.AND, Operator.NAND, Operator.OR, Operator.NOR, Operator.XOR, Operator.XNOR })
//			potentialOprators.add(o);
//		seek(potentialOprators, new ArrayList<Operator>());
//
//		for (int[] nums : solutions.values())
//			interpolations.put(nums, interpolation(nums));
//
//		for (ArrayList<Operator> key : solutions.keySet()) {
//			String nums = "";
//			for (int num : solutions.get(key))
//				nums += " " + num;
//			String interp = interpolations.get(solutions.get(key));
//			outputs.put(key.toString() + " " + nums + " \t" + interp, interp.length());
//		}
//
		ArrayList<String> outputsIntegers = new ArrayList<String>();
		for (String s : outputs.keySet())
			outputsIntegers.add(s);
		Collections.sort(outputsIntegers, (s1, s2) -> {
			int integerCompareTo = outputs.get(s1).compareTo(outputs.get(s2));
			return integerCompareTo == 0 ? s1.compareTo(s2) : integerCompareTo;
		});
//
//		System.out.println("                                 | whether the gate works, from binary to dec, values from 000111 to 111000"
//					   + "\nlength | for this order of gates | 00 01 11  |  interpolation equation (e.g. (0, 19), (1, 28), (2, 37), where the \"x\" is which pair, 00, 01/10, or 11 was selected)");
//		for (String s : outputsIntegers)
//			System.out.println(outputs.get(s) + " " + s);

//		for (Integer num : possibleNumbers)
//			System.out.print(num + ", ");
//		System.out.println();
//
//		int input = 1;
//
//		for (Entry<ArrayList<Operator>, int[]> e : solutions.entrySet()) {
//
//			System.out.print(e.getKey());
//			for (int num : e.getValue())
//				System.out.print(" " + num);
//			System.out.println();
//
//			StringBuilder str = new StringBuilder();
//
//			str.append("001:");
//			for (int i = 0; i < 6; i++) {
////				System.out.println(e.getValue()[0] & 1 << i);
//				if ((e.getValue()[0] >> i & input) == 1) {
//
//					str.append(" ");
//					str.append(e.getKey().get(5 - i));
//				}
//			}
//			str.append("\n011:");
//			for (int i = 0; i < 6; i++)
//				if ((e.getValue()[1] & 1 << i) != 0) {
//					str.append(" ");
//					str.append(e.getKey().get(5 - i));
//				}
//
//			str.append("\n111:");
//			for (int i = 0; i < 6; i++)
//				if ((e.getValue()[2] & 1 << i) != 0) {
//					str.append(" ");
//					str.append(e.getKey().get(5 - i));
//				}
//
//			System.out.println(str.toString());
//			break;
//		}

		for (int i = 0; i <= 7; i++)
			try {
				g.z(i);
			} catch (Exception e) {
				System.out.println();
			}
//		for (int i = 0; i < 8; i++) {
//			System.out.println((i >> 2 & 1) + " " + (i >> 1 & 1) + " " + (i & 1));
//			g.x(i >> 2 & 1, i >> 1 & 1, i & 1);
//			System.out.print("\n---\n");
//		}

	}

	final static CodeGolf g = new CodeGolf();

	public static void solve(Entry<ArrayList<Operator>, int[]> e, int a, int b, int input) {

		/*
		 * 0, 0: 0
		 * 0, 1: 1
		 * 1, 0: 1
		 * 1, 1: 2
		 */
		for (int i = -1; i++ < 6;)
			if ((e.getValue()[a + b] >> i & input) == 1)
				System.out.print(e.getKey().get(5 - i));
	}

	public static String interpolation(int[] nums) { // Lagrange for degree 3
		StringBuilder str = new StringBuilder();
//		final String[] wrap = { "(x-", ")*(x-", ")/", "*", "+" };
//		for (int i = 0; i < 3; i++) {
//			int[] values = { (i + 1) % 3, (i + 2) % 3, (i - (i + 1) % 3) * (i - (i + 2) % 3), nums[i] };
//			for (int j = 0; j < 4; j++) {
//				str.append(wrap[j]);
//				str.append(values[j]);
//			}
//			if (i != 2)
//				str.append(wrap[4]);
//		}

		//px^2-p6x+p2 + qx^2-q2x + rx^2-rx
		//p+q+r x^2  +  -6p-2q-r x +  2p
		double u = nums[0] / 2.0  - nums[1] + nums[2] / 2.0,
				v = -1.5 * nums[0] + 2 * nums[1] - nums[2] / 2.0;

		str.append(nums[0]);
		if (u != 0) {
			if (u > 0)
				str.append("+");
			str.append(new DecimalFormat(u % 1 == 0 ? "#.#" : ".##").format(u));
		str.append("*x*x");
		}
		if (v != 0) {
			if (v > 0)
				str.append("+");
			str.append(new DecimalFormat(v % 1 == 0 ? "#.#" : ".##").format(v));
			str.append("*x");
		}
		return str.toString();
	}
//			str.append("(x-" + ((i + 1) % 3) + ")*(x-" + ((i + 2) % 3) + ")/("
//					+ (i - (i + 1) % 3) + ")*(" + (i - (i + 2) % 3) + ")*" + nums[i]);

	public static
	void s(int a,int b,int c) {
		for(int i=-1;i++<5;)
			if(((new int[]{37,42,22}[a+b]>>i&1)==c))
				System.out.print(new String[]{"NAND","AND","XOR","XNOR","OR","NOR"}[5-i]+" ");
//		System.out.print("NANDXNORNORANDXOROR".substring((int)(4.4*i-.2*i*i),(int)(4.1*(i+1)-.2*i*i))+" ");
	/*"NANDXORXNOR"
	*  0123456789
	*  a  ac cdf f
	*   b b ee   d
	*           ee
	*/	
	}

//	public static
//	void w(int a,int b,int c) {
//		for(int i=-1;i++<5;)
//			if(((new int[]{37,42,22}[a+b]>>i&1)==c))
//				System.out.print(new String[]{"NAND","XNOR","NOR"}[i%3].substring(c)+" ");
//	}

	/**
	 * @param n - the input, 3 digit number <b>in base 2</b>
	 */
	public static
	void v(int n) {
		for(int i=-1;i++<5;)
			if((new int[]{37,42,22}[(n>>2+n>>1)&1]>>(int)i&n&1)==1)
				System.out.print(new String[]{"NAND","AND","XOR","XNOR","OR","NOR"}[5-i]);
	}

	//treating each gate's string like a base 36 number (minimum of 34 because of "X"), stored as an interpolated polynomial
	void u(Integer a,int b,int c) { // Integer to avoid "Integer.toString"
		a+=b;
		for(double i=-1;i++<5;)
			if((11-8*a*a+35*a>>(int)i&c)==1)
				System.out.print(a.toString((int)(891-i*i*i*(152766.2*i*i-1395817*i/44)-2662433*i*i*i/32+8045047*i*i/24-985536.3*i),36));
	}

	void x(int a,int b,int c) {
//		for(int i=-1;i++<5;)
		for(int i=0;;)
			if(((7+21*(a+b))>>i++&1)==c)
				System.out.print(new String[]{"AND","OR","XOR","NAND","NOR","XNOR"}[5-i]+" ");
//					System.out.print(((7+21*(a+b))>>i&1)==c?new String[]{"AND","OR","XOR","NAND","NOR","XNOR"}[5-i]+" ":"");
	}
	void y(int a) { //dec
		for(int i=0;;)
			if(((7+21*(a/100+a%100/10))>>i++&1)==a%10)
				System.out.print(new String[]{"AND","OR","XOR","NAND","NOR","XNOR"}[5-i]+" ");
	}
	void z(int a){ //bin
		for(int i=0;;)
			if((7+21*((a+2)/4)>>i++&1)==a%2)
				System.out.print(new String[]{"AND","OR","XOR","NAND","NOR","XNOR"}[6-i]+" "); //5 - (i++ -1)
}

	void t(int a) {
		for(int i=-1;i++<5;)
			if((new int[]{37,42,22}[a/100+a%100/10]>>i&a%10)==1)
				System.out.print(new String[]{"NAND","","XOR","XNOR","OR",""}[5-i]);
//				System.out.print(new String[]{"NAND","AND","XOR","XNOR","OR","NOR"}[5-i]);
	}

	public static void seek(ArrayList<Operator> potentialOperators, ArrayList<Operator> currentList) {
		if (potentialOperators.size() == 0) {
			calculateAdd(currentList);
			solutions.put(currentList, calculateAdd(currentList));
		}
		else {
			for (int i = 0; i < potentialOperators.size(); i++) {
				ArrayList<Operator> nextOperators = (ArrayList<Operator>) potentialOperators.clone();
				ArrayList<Operator> nextList = (ArrayList<Operator>) currentList.clone();
				nextList.add(nextOperators.remove(i));
				seek(nextOperators, nextList);
			}
		}
	}

	private static int[] calculateAdd(ArrayList<Operator> currentList) {
		int[] nums = new int[3];
		for (int j = 0; j < 3; j++) {
			for (int i = 0; i < currentList.size(); i++)
				if (OperationStatement.operate(j == 2, currentList.get(i), j == 1 || j == 2))
					nums[j] += 1 << (currentList.size() - i - 1);
			possibleNumbers.add(nums[j]);
		}
//		for (int i = 0; i < currentList.size(); i++)
//			if (OperationStatement.operate(false, currentList.get(i), true))
//				nums[1] += 1 << (currentList.size() - i);
//		for (int i = 0; i < currentList.size(); i++)
//			if (OperationStatement.operate(true, currentList.get(i), true))
//				nums[2] += 1 << (currentList.size() - i);
		return nums;
	}
}


