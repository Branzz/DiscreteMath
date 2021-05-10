package bran.test;

public class CodeGolf2 {

	final static CodeGolf2 g = new CodeGolf2();

	public static void main(String[] args) {

		for (int i = 0; i <= 7; i++) {
			System.out.print((i >> 2 & 1) + " " + (i >> 1 & 1) + " " + (i & 1) + ": ");
			g.A(i);

			System.out.println();
		}

//		for (int i = 0; i < 8; i++) {
//			System.out.println((i >> 2 & 1) + " " + (i >> 1 & 1) + " " + (i & 1));
//			g.x(i >> 2 & 1, i >> 1 & 1, i & 1);
//			System.out.print("\n---\n");
//		}

	}

void A(int a){for(int i=-1;i++<5;)if((7+21*((a+2)/4)>>i&1)==a%2)System.out.print(new String[]{"AND","OR","XOR","NAND","NOR","XNOR"}[5-i]+" ");}

	/*
	 * Explanation:
	 * (...)
	 * if((
	 *     7+21*(       )									# The shortest interpolation equation (see bottom of file) I could find, for the "x" values of
	 *     													# 0, 1, 2, representing 00_, 01_ / 10_, 11_ respectively.
	 *           (a+2)/4 									# The sum of the second and third bit, found this equation with brute force,
	 *        											    # alternative: (a|1)/3, needs parentheses because of bit operation order.
	 *           		 >>i&1 								# The bit at i, delete all bits except the first to compare it with:
	 *           				==a%2)						# The first digit of a. (like "number % 10", except for binary)
	 * {"AND","OR","XOR","NAND","NOR","XNOR"}[6-i]+" ");	# (5 - (i++ -1))th element of a predetermined list of gates as strings.
	 * 							
	 */

	/*
	 * Other alt function approaches:
	 */
	void s(int a,int b,int c) {
		for(int i=-1;i++<5;)
			if(((new int[]{37,42,22}[a+b]>>i&1)==c))
				System.out.print(new String[]{"NAND","AND","XOR","XNOR","OR","NOR"}[5-i]+" ");
//				System.out.print("NANDXNORNORANDXOROR".substring((int)(4.4*i-.2*i*i),(int)(4.1*(i+1)-.2*i*i))+" ");
	}

	void t(int a) {
		for(int i=-1;i++<5;)
			if((new int[]{37,42,22}[a/100+a%100/10]>>i&a%10)==1)
				System.out.print(new String[]{"NAND","","XOR","XNOR","OR",""}[5-i]);
//				System.out.print(new String[]{"NAND","AND","XOR","XNOR","OR","NOR"}[5-i]);
	}

	//treating each gate's string like a base 36 number (minimum of 34 because of "X"), stored as an interpolated polynomial
	void u(Integer a,int b,int c) { // "Integer a" to avoid using "Integer.toString" with "a.toString"
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

	/**
	 * the solution
	 * @param a - the input, 3 digit number <b>in base 2</b>
	 */
	void z(int a){
		for(int i=0;;)
			if((7+21*((a+2)/4)>>i++&1)==a%2)
//			if((new int[]{37,42,22}[(n>>2+n>>1)&1]>>(int)i&n&1)==1)
				System.out.print(new String[]{"AND","OR","XOR","NAND","NOR","XNOR"}[6-i]+" "); //5 - (i++ -1)
	}

}

/*
	A list of all 6! interpolation functions, ordered by size then alphabetically:
	There's a chance this could be applied to other posted solutions, especially because 7 is a possible number, which is only 1 digit rather than 2

	AND  NAND XOR  OR   NOR  XNOR
00  0    1    0    0    1    1    = 19
01  0    1    1    1    0    0    = 28
11  1    0    0    1    0    1    = 37
									 |Whether the gate works, from binary to dec, which has binary values with exactly three 1s, thus from 000111 to 111000.
  length |  for this order of gates  |^00 01 11 |  interpolation equation (e.g. (0, 19), (1, 28), (2, 37), where the "x" is which pair, 00_, 01_ / 10_, or 11_ was inputed)
	6 [AND, NAND, XOR, OR, NOR, XNOR]  19 28 37 	19+9*x
	6 [AND, OR, XOR, NAND, NOR, XNOR]  7 28 49 		7+21*x // The shortest ones are where one of the coefficients is 0
	6 [NAND, AND, XNOR, NOR, OR, XOR]  44 35 26 	44-9*x // x^2 coeff is 0
	6 [NOR, OR, XOR, NAND, AND, XNOR]  37 28 19 	37-9*x
	6 [OR, NOR, XNOR, AND, NAND, XOR]  26 35 44 	26+9*x
	7 [NAND, NOR, XNOR, AND, OR, XOR]  56 35 14 	56-21*x
	7 [NOR, NAND, XOR, OR, AND, XNOR]  49 28 7 		49-21*x
	7 [OR, AND, XNOR, NOR, NAND, XOR]  14 35 56 	14+21*x
	8 [AND, NAND, OR, NOR, XOR, XNOR]  21 26 41 	21+5*x*x // x coeff is 0
	8 [AND, OR, NOR, NAND, XOR, XNOR]  13 22 49 	13+9*x*x
	8 [NAND, AND, NOR, OR, XNOR, XOR]  42 37 22 	42-5*x*x
	8 [NAND, NOR, OR, AND, XNOR, XOR]  50 41 14 	50-9*x*x
	12 [AND, NAND, OR, NOR, XNOR, XOR]  22 25 42 	22+7*x*x-4*x
	12 [AND, NAND, OR, XNOR, NOR, XOR]  22 25 44 	22+8*x*x-5*x
	12 [AND, NAND, OR, XOR, NOR, XNOR]  19 28 41 	19+2*x*x+7*x
	12 [AND, NAND, XOR, NOR, OR, XNOR]  21 26 35 	21+2*x*x+3*x
	12 [AND, OR, NAND, NOR, XNOR, XOR]  14 25 50 	14+7*x*x+4*x
	12 [AND, OR, NAND, NOR, XOR, XNOR]  13 26 49 	13+5*x*x+8*x
	12 [AND, OR, NAND, XNOR, NOR, XOR]  14 25 52 	14+8*x*x+3*x
	12 [AND, OR, NOR, XOR, NAND, XNOR]  11 22 49 	11+8*x*x+3*x
	12 [AND, OR, XOR, NOR, NAND, XNOR]  7 26 49 	7+2*x*x+17*x
	12 [AND, XOR, NOR, NAND, OR, XNOR]  13 22 35 	13+2*x*x+7*x
	12 [AND, XOR, NOR, OR, NAND, XNOR]  11 22 37 	11+2*x*x+9*x
	12 [AND, XOR, OR, NAND, NOR, XNOR]  7 28 41 	7-4*x*x+25*x
	12 [AND, XOR, OR, NOR, NAND, XNOR]  7 26 41 	7-2*x*x+21*x
	12 [NAND, AND, NOR, OR, XOR, XNOR]  41 38 21 	41-7*x*x+4*x
	12 [NAND, AND, NOR, XNOR, OR, XOR]  44 35 22 	44-2*x*x-7*x
	12 [NAND, AND, NOR, XOR, OR, XNOR]  41 38 19 	41-8*x*x+5*x
	12 [NAND, AND, XNOR, OR, NOR, XOR]  42 37 28 	42-2*x*x-3*x
	12 [NAND, NOR, AND, OR, XNOR, XOR]  50 37 14 	50-5*x*x-8*x
	12 [NAND, NOR, AND, OR, XOR, XNOR]  49 38 13 	49-7*x*x-4*x
	12 [NAND, NOR, AND, XOR, OR, XNOR]  49 38 11 	49-8*x*x-3*x
	12 [NAND, NOR, OR, XNOR, AND, XOR]  52 41 14 	52-8*x*x-3*x
	12 [NAND, XNOR, OR, AND, NOR, XOR]  50 41 28 	50-2*x*x-7*x
	12 [NAND, XNOR, OR, NOR, AND, XOR]  52 41 26 	52-2*x*x-9*x
	12 [NOR, XOR, NAND, OR, AND, XNOR]  41 28 7 	41-4*x*x-9*x
	12 [NOR, XOR, OR, AND, NAND, XNOR]  35 26 13 	35-2*x*x-7*x
	12 [NOR, XOR, OR, NAND, AND, XNOR]  37 28 11 	37-4*x*x-5*x
	12 [OR, XNOR, AND, NOR, NAND, XOR]  22 35 56 	22+4*x*x+9*x
	12 [OR, XNOR, NOR, AND, NAND, XOR]  26 35 52 	26+4*x*x+5*x
	12 [OR, XNOR, NOR, NAND, AND, XOR]  28 37 50 	28+2*x*x+7*x
	13 [AND, NAND, NOR, OR, XOR, XNOR]  25 22 37 	25+9*x*x-12*x
	13 [AND, NAND, NOR, XOR, OR, XNOR]  25 22 35 	25+8*x*x-11*x
	13 [AND, OR, NAND, XOR, NOR, XNOR]  11 28 49 	11+2*x*x+15*x
	13 [AND, OR, NOR, NAND, XNOR, XOR]  14 21 50 	14+11*x*x-4*x
	13 [AND, OR, NOR, XNOR, NAND, XOR]  14 19 52 	14+14*x*x-9*x
	13 [AND, OR, XNOR, NAND, NOR, XOR]  14 21 56 	14+14*x*x-7*x
	13 [AND, XOR, NAND, NOR, OR, XNOR]  13 26 35 	13-2*x*x+15*x
	13 [AND, XOR, NAND, OR, NOR, XNOR]  11 28 37 	11-4*x*x+21*x
	13 [NAND, AND, OR, NOR, XNOR, XOR]  38 41 26 	38-9*x*x+12*x
	13 [NAND, AND, OR, XNOR, NOR, XOR]  38 41 28 	38-8*x*x+11*x
	13 [NAND, NOR, AND, XNOR, OR, XOR]  52 35 14 	52-2*x*x-15*x
	13 [NAND, NOR, OR, AND, XOR, XNOR]  49 42 13 	49-11*x*x+4*x
	13 [NAND, NOR, OR, XOR, AND, XNOR]  49 44 11 	49-14*x*x+9*x
	13 [NAND, NOR, XNOR, OR, AND, XOR]  56 37 14 	56-2*x*x-17*x
	13 [NAND, NOR, XOR, AND, OR, XNOR]  49 42 7 	49-14*x*x+7*x
	13 [NAND, XNOR, AND, NOR, OR, XOR]  52 35 26 	52+4*x*x-21*x
	13 [NAND, XNOR, AND, OR, NOR, XOR]  50 37 28 	50+2*x*x-15*x
	13 [NAND, XNOR, NOR, AND, OR, XOR]  56 35 22 	56+4*x*x-25*x
	13 [NAND, XNOR, NOR, OR, AND, XOR]  56 37 22 	56+2*x*x-21*x
	13 [NOR, NAND, AND, OR, XOR, XNOR]  49 22 13 	49+9*x*x-36*x
	13 [NOR, NAND, AND, XOR, OR, XNOR]  49 22 11 	49+8*x*x-35*x
	13 [NOR, NAND, OR, AND, XNOR, XOR]  50 25 14 	50+7*x*x-32*x
	13 [NOR, NAND, OR, AND, XOR, XNOR]  49 26 13 	49+5*x*x-28*x
	13 [NOR, NAND, OR, XNOR, AND, XOR]  52 25 14 	52+8*x*x-35*x
	13 [NOR, NAND, OR, XOR, AND, XNOR]  49 28 11 	49+2*x*x-23*x
	13 [NOR, NAND, XOR, AND, OR, XNOR]  49 26 7 	49+2*x*x-25*x
	13 [NOR, OR, AND, NAND, XOR, XNOR]  37 22 25 	37+9*x*x-24*x
	13 [NOR, OR, AND, XOR, NAND, XNOR]  35 22 25 	35+8*x*x-21*x
	13 [NOR, OR, NAND, AND, XNOR, XOR]  42 25 22 	42+7*x*x-24*x
	13 [NOR, OR, NAND, AND, XOR, XNOR]  41 26 21 	41+5*x*x-20*x
	13 [NOR, OR, NAND, XNOR, AND, XOR]  44 25 22 	44+8*x*x-27*x
	13 [NOR, OR, NAND, XOR, AND, XNOR]  41 28 19 	41+2*x*x-15*x
	13 [NOR, OR, XOR, AND, NAND, XNOR]  35 26 21 	35+2*x*x-11*x
	13 [NOR, XOR, AND, NAND, OR, XNOR]  37 22 11 	37+2*x*x-17*x
	13 [NOR, XOR, AND, OR, NAND, XNOR]  35 22 13 	35+2*x*x-15*x
	13 [NOR, XOR, NAND, AND, OR, XNOR]  41 26 7 	41-2*x*x-13*x
	13 [OR, AND, NAND, NOR, XNOR, XOR]  14 41 50 	14-9*x*x+36*x
	13 [OR, AND, NAND, XNOR, NOR, XOR]  14 41 52 	14-8*x*x+35*x
	13 [OR, AND, NOR, NAND, XNOR, XOR]  14 37 50 	14-5*x*x+28*x
	13 [OR, AND, NOR, NAND, XOR, XNOR]  13 38 49 	13-7*x*x+32*x
	13 [OR, AND, NOR, XNOR, NAND, XOR]  14 35 52 	14-2*x*x+23*x
	13 [OR, AND, NOR, XOR, NAND, XNOR]  11 38 49 	11-8*x*x+35*x
	13 [OR, AND, XNOR, NAND, NOR, XOR]  14 37 56 	14-2*x*x+25*x
	13 [OR, AND, XOR, NAND, NOR, XNOR]  7 44 49 	7-16*x*x+53*x
	13 [OR, AND, XOR, NOR, NAND, XNOR]  7 42 49 	7-14*x*x+49*x
	13 [OR, NOR, AND, NAND, XNOR, XOR]  22 37 42 	22-5*x*x+20*x
	13 [OR, NOR, AND, NAND, XOR, XNOR]  21 38 41 	21-7*x*x+24*x
	13 [OR, NOR, AND, XNOR, NAND, XOR]  22 35 44 	22-2*x*x+15*x
	13 [OR, NOR, AND, XOR, NAND, XNOR]  19 38 41 	19-8*x*x+27*x
	13 [OR, NOR, NAND, AND, XNOR, XOR]  26 41 38 	26-9*x*x+24*x
	13 [OR, NOR, NAND, XNOR, AND, XOR]  28 41 38 	28-8*x*x+21*x
	13 [OR, NOR, XNOR, NAND, AND, XOR]  28 37 42 	28-2*x*x+11*x
	13 [OR, XNOR, AND, NAND, NOR, XOR]  22 37 56 	22+2*x*x+13*x
	13 [OR, XNOR, NAND, AND, NOR, XOR]  26 41 52 	26-2*x*x+17*x
	13 [OR, XNOR, NAND, NOR, AND, XOR]  28 41 50 	28-2*x*x+15*x
	13 [OR, XOR, AND, NAND, NOR, XNOR]  7 52 41 	7-28*x*x+73*x
	13 [OR, XOR, AND, NOR, NAND, XNOR]  7 50 41 	7-26*x*x+69*x
	13 [XOR, AND, OR, NAND, NOR, XNOR]  7 44 25 	7-28*x*x+65*x
	13 [XOR, AND, OR, NOR, NAND, XNOR]  7 42 25 	7-26*x*x+61*x
	13 [XOR, OR, AND, NAND, NOR, XNOR]  7 52 25 	7-36*x*x+81*x
	13 [XOR, OR, AND, NOR, NAND, XNOR]  7 50 25 	7-34*x*x+77*x
	14 [AND, NAND, NOR, OR, XNOR, XOR]  26 21 38 	26+11*x*x-16*x
	14 [AND, NAND, NOR, XNOR, OR, XOR]  28 19 38 	28+14*x*x-23*x
	14 [AND, NAND, XNOR, NOR, OR, XOR]  28 19 42 	28+16*x*x-25*x
	14 [AND, NAND, XNOR, OR, NOR, XOR]  26 21 44 	26+14*x*x-19*x
	14 [AND, NOR, NAND, OR, XNOR, XOR]  26 13 38 	26+19*x*x-32*x
	14 [AND, NOR, NAND, OR, XOR, XNOR]  25 14 37 	25+17*x*x-28*x
	14 [AND, NOR, NAND, XNOR, OR, XOR]  28 11 38 	28+22*x*x-39*x
	14 [AND, NOR, NAND, XOR, OR, XNOR]  25 14 35 	25+16*x*x-27*x
	14 [AND, NOR, OR, NAND, XNOR, XOR]  22 13 42 	22+19*x*x-28*x
	14 [AND, NOR, OR, NAND, XOR, XNOR]  21 14 41 	21+17*x*x-24*x
	14 [AND, NOR, OR, XNOR, NAND, XOR]  22 11 44 	22+22*x*x-33*x
	14 [AND, NOR, OR, XOR, NAND, XNOR]  19 14 41 	19+16*x*x-21*x
	14 [AND, NOR, XNOR, NAND, OR, XOR]  28 7 42 	28+28*x*x-49*x
	14 [AND, NOR, XNOR, OR, NAND, XOR]  26 7 44 	26+28*x*x-47*x
	14 [AND, NOR, XOR, NAND, OR, XNOR]  21 14 35 	21+14*x*x-21*x
	14 [AND, NOR, XOR, OR, NAND, XNOR]  19 14 37 	19+14*x*x-19*x
	14 [AND, OR, XNOR, NOR, NAND, XOR]  14 19 56 	14+16*x*x-11*x
	14 [AND, XNOR, NAND, NOR, OR, XOR]  28 11 50 	28+28*x*x-45*x
	14 [AND, XNOR, NAND, OR, NOR, XOR]  26 13 52 	26+26*x*x-39*x
	14 [AND, XNOR, NOR, NAND, OR, XOR]  28 7 50 	28+32*x*x-53*x
	14 [AND, XNOR, NOR, OR, NAND, XOR]  26 7 52 	26+32*x*x-51*x
	14 [AND, XNOR, OR, NAND, NOR, XOR]  22 13 56 	22+26*x*x-35*x
	14 [AND, XNOR, OR, NOR, NAND, XOR]  22 11 56 	22+28*x*x-39*x
	14 [NAND, AND, OR, NOR, XOR, XNOR]  37 42 25 	37-11*x*x+16*x
	14 [NAND, AND, OR, XOR, NOR, XNOR]  35 44 25 	35-14*x*x+23*x
	14 [NAND, AND, XOR, NOR, OR, XNOR]  37 42 19 	37-14*x*x+19*x
	14 [NAND, AND, XOR, OR, NOR, XNOR]  35 44 21 	35-16*x*x+25*x
	14 [NAND, NOR, XOR, OR, AND, XNOR]  49 44 7 	49-16*x*x+11*x
	14 [NAND, OR, AND, NOR, XNOR, XOR]  38 49 26 	38-17*x*x+28*x
	14 [NAND, OR, AND, NOR, XOR, XNOR]  37 50 25 	37-19*x*x+32*x
	14 [NAND, OR, AND, XNOR, NOR, XOR]  38 49 28 	38-16*x*x+27*x
	14 [NAND, OR, AND, XOR, NOR, XNOR]  35 52 25 	35-22*x*x+39*x
	14 [NAND, OR, NOR, AND, XNOR, XOR]  42 49 22 	42-17*x*x+24*x
	14 [NAND, OR, NOR, AND, XOR, XNOR]  41 50 21 	41-19*x*x+28*x
	14 [NAND, OR, NOR, XNOR, AND, XOR]  44 49 22 	44-16*x*x+21*x
	14 [NAND, OR, NOR, XOR, AND, XNOR]  41 52 19 	41-22*x*x+33*x
	14 [NAND, OR, XNOR, AND, NOR, XOR]  42 49 28 	42-14*x*x+21*x
	14 [NAND, OR, XNOR, NOR, AND, XOR]  44 49 26 	44-14*x*x+19*x
	14 [NAND, OR, XOR, AND, NOR, XNOR]  35 56 21 	35-28*x*x+49*x
	14 [NAND, OR, XOR, NOR, AND, XNOR]  37 56 19 	37-28*x*x+47*x
	14 [NAND, XOR, AND, NOR, OR, XNOR]  37 50 11 	37-26*x*x+39*x
	14 [NAND, XOR, AND, OR, NOR, XNOR]  35 52 13 	35-28*x*x+45*x
	14 [NAND, XOR, NOR, AND, OR, XNOR]  41 50 7 	41-26*x*x+35*x
	14 [NAND, XOR, NOR, OR, AND, XNOR]  41 52 7 	41-28*x*x+39*x
	14 [NAND, XOR, OR, AND, NOR, XNOR]  35 56 13 	35-32*x*x+53*x
	14 [NAND, XOR, OR, NOR, AND, XNOR]  37 56 11 	37-32*x*x+51*x
	14 [NOR, AND, NAND, OR, XNOR, XOR]  42 13 22 	42+19*x*x-48*x
	14 [NOR, AND, NAND, OR, XOR, XNOR]  41 14 21 	41+17*x*x-44*x
	14 [NOR, AND, NAND, XNOR, OR, XOR]  44 11 22 	44+22*x*x-55*x
	14 [NOR, AND, NAND, XOR, OR, XNOR]  41 14 19 	41+16*x*x-43*x
	14 [NOR, AND, OR, NAND, XNOR, XOR]  38 13 26 	38+19*x*x-44*x
	14 [NOR, AND, OR, NAND, XOR, XNOR]  37 14 25 	37+17*x*x-40*x
	14 [NOR, AND, OR, XNOR, NAND, XOR]  38 11 28 	38+22*x*x-49*x
	14 [NOR, AND, OR, XOR, NAND, XNOR]  35 14 25 	35+16*x*x-37*x
	14 [NOR, AND, XNOR, NAND, OR, XOR]  44 7 26 	44+28*x*x-65*x
	14 [NOR, AND, XNOR, OR, NAND, XOR]  42 7 28 	42+28*x*x-63*x
	14 [NOR, AND, XOR, NAND, OR, XNOR]  37 14 19 	37+14*x*x-37*x
	14 [NOR, AND, XOR, OR, NAND, XNOR]  35 14 21 	35+14*x*x-35*x
	14 [NOR, NAND, AND, OR, XNOR, XOR]  50 21 14 	50+11*x*x-40*x
	14 [NOR, NAND, AND, XNOR, OR, XOR]  52 19 14 	52+14*x*x-47*x
	14 [NOR, NAND, XNOR, AND, OR, XOR]  56 19 14 	56+16*x*x-53*x
	14 [NOR, NAND, XNOR, OR, AND, XOR]  56 21 14 	56+14*x*x-49*x
	14 [NOR, OR, AND, NAND, XNOR, XOR]  38 21 26 	38+11*x*x-28*x
	14 [NOR, OR, AND, XNOR, NAND, XOR]  38 19 28 	38+14*x*x-33*x
	14 [NOR, OR, XNOR, AND, NAND, XOR]  42 19 28 	42+16*x*x-39*x
	14 [NOR, OR, XNOR, NAND, AND, XOR]  44 21 26 	44+14*x*x-37*x
	14 [NOR, XNOR, AND, NAND, OR, XOR]  52 7 26 	52+32*x*x-77*x
	14 [NOR, XNOR, AND, OR, NAND, XOR]  50 7 28 	50+32*x*x-75*x
	14 [NOR, XNOR, NAND, AND, OR, XOR]  56 11 22 	56+28*x*x-73*x
	14 [NOR, XNOR, NAND, OR, AND, XOR]  56 13 22 	56+26*x*x-69*x
	14 [NOR, XNOR, OR, AND, NAND, XOR]  50 11 28 	50+28*x*x-67*x
	14 [NOR, XNOR, OR, NAND, AND, XOR]  52 13 26 	52+26*x*x-65*x
	14 [OR, AND, NAND, NOR, XOR, XNOR]  13 42 49 	13-11*x*x+40*x
	14 [OR, AND, NAND, XOR, NOR, XNOR]  11 44 49 	11-14*x*x+47*x
	14 [OR, NAND, AND, NOR, XNOR, XOR]  22 49 42 	22-17*x*x+44*x
	14 [OR, NAND, AND, NOR, XOR, XNOR]  21 50 41 	21-19*x*x+48*x
	14 [OR, NAND, AND, XNOR, NOR, XOR]  22 49 44 	22-16*x*x+43*x
	14 [OR, NAND, AND, XOR, NOR, XNOR]  19 52 41 	19-22*x*x+55*x
	14 [OR, NAND, NOR, AND, XNOR, XOR]  26 49 38 	26-17*x*x+40*x
	14 [OR, NAND, NOR, AND, XOR, XNOR]  25 50 37 	25-19*x*x+44*x
	14 [OR, NAND, NOR, XNOR, AND, XOR]  28 49 38 	28-16*x*x+37*x
	14 [OR, NAND, NOR, XOR, AND, XNOR]  25 52 35 	25-22*x*x+49*x
	14 [OR, NAND, XNOR, AND, NOR, XOR]  26 49 44 	26-14*x*x+37*x
	14 [OR, NAND, XNOR, NOR, AND, XOR]  28 49 42 	28-14*x*x+35*x
	14 [OR, NAND, XOR, AND, NOR, XNOR]  19 56 37 	19-28*x*x+65*x
	14 [OR, NAND, XOR, NOR, AND, XNOR]  21 56 35 	21-28*x*x+63*x
	14 [OR, NOR, NAND, AND, XOR, XNOR]  25 42 37 	25-11*x*x+28*x
	14 [OR, NOR, NAND, XOR, AND, XNOR]  25 44 35 	25-14*x*x+33*x
	14 [OR, NOR, XOR, AND, NAND, XNOR]  19 42 37 	19-14*x*x+37*x
	14 [OR, NOR, XOR, NAND, AND, XNOR]  21 44 35 	21-16*x*x+39*x
	14 [OR, XOR, NAND, AND, NOR, XNOR]  11 56 37 	11-32*x*x+77*x
	14 [OR, XOR, NAND, NOR, AND, XNOR]  13 56 35 	13-32*x*x+75*x
	14 [OR, XOR, NOR, AND, NAND, XNOR]  11 50 37 	11-26*x*x+65*x
	14 [OR, XOR, NOR, NAND, AND, XNOR]  13 52 35 	13-28*x*x+67*x
	14 [XNOR, AND, NAND, NOR, OR, XOR]  44 11 50 	44+36*x*x-69*x
	14 [XNOR, AND, NAND, OR, NOR, XOR]  42 13 52 	42+34*x*x-63*x
	14 [XNOR, AND, NOR, NAND, OR, XOR]  44 7 50 	44+40*x*x-77*x
	14 [XNOR, AND, NOR, OR, NAND, XOR]  42 7 52 	42+40*x*x-75*x
	14 [XNOR, AND, OR, NAND, NOR, XOR]  38 13 56 	38+34*x*x-59*x
	14 [XNOR, AND, OR, NOR, NAND, XOR]  38 11 56 	38+36*x*x-63*x
	14 [XNOR, NAND, AND, NOR, OR, XOR]  52 19 42 	52+28*x*x-61*x
	14 [XNOR, NAND, AND, OR, NOR, XOR]  50 21 44 	50+26*x*x-55*x
	14 [XNOR, NAND, NOR, AND, OR, XOR]  56 19 38 	56+28*x*x-65*x
	14 [XNOR, NAND, NOR, OR, AND, XOR]  56 21 38 	56+26*x*x-61*x
	14 [XNOR, NAND, OR, AND, NOR, XOR]  50 25 44 	50+22*x*x-47*x
	14 [XNOR, NAND, OR, NOR, AND, XOR]  52 25 42 	52+22*x*x-49*x
	14 [XNOR, NOR, AND, NAND, OR, XOR]  52 7 42 	52+40*x*x-85*x
	14 [XNOR, NOR, AND, OR, NAND, XOR]  50 7 44 	50+40*x*x-83*x
	14 [XNOR, NOR, NAND, AND, OR, XOR]  56 11 38 	56+36*x*x-81*x
	14 [XNOR, NOR, NAND, OR, AND, XOR]  56 13 38 	56+34*x*x-77*x
	14 [XNOR, NOR, OR, AND, NAND, XOR]  50 11 44 	50+36*x*x-75*x
	14 [XNOR, NOR, OR, NAND, AND, XOR]  52 13 42 	52+34*x*x-73*x
	14 [XNOR, OR, AND, NAND, NOR, XOR]  38 21 56 	38+26*x*x-43*x
	14 [XNOR, OR, AND, NOR, NAND, XOR]  38 19 56 	38+28*x*x-47*x
	14 [XNOR, OR, NAND, AND, NOR, XOR]  42 25 52 	42+22*x*x-39*x
	14 [XNOR, OR, NAND, NOR, AND, XOR]  44 25 50 	44+22*x*x-41*x
	14 [XNOR, OR, NOR, AND, NAND, XOR]  42 19 52 	42+28*x*x-51*x
	14 [XNOR, OR, NOR, NAND, AND, XOR]  44 21 50 	44+26*x*x-49*x
	14 [XOR, AND, NAND, NOR, OR, XNOR]  13 42 19 	13-26*x*x+55*x
	14 [XOR, AND, NAND, OR, NOR, XNOR]  11 44 21 	11-28*x*x+61*x
	14 [XOR, AND, NOR, NAND, OR, XNOR]  13 38 19 	13-22*x*x+47*x
	14 [XOR, AND, NOR, OR, NAND, XNOR]  11 38 21 	11-22*x*x+49*x
	14 [XOR, NAND, AND, NOR, OR, XNOR]  21 50 11 	21-34*x*x+63*x
	14 [XOR, NAND, AND, OR, NOR, XNOR]  19 52 13 	19-36*x*x+69*x
	14 [XOR, NAND, NOR, AND, OR, XNOR]  25 50 7 	25-34*x*x+59*x
	14 [XOR, NAND, NOR, OR, AND, XNOR]  25 52 7 	25-36*x*x+63*x
	14 [XOR, NAND, OR, AND, NOR, XNOR]  19 56 13 	19-40*x*x+77*x
	14 [XOR, NAND, OR, NOR, AND, XNOR]  21 56 11 	21-40*x*x+75*x
	14 [XOR, NOR, AND, NAND, OR, XNOR]  21 38 11 	21-22*x*x+39*x
	14 [XOR, NOR, AND, OR, NAND, XNOR]  19 38 13 	19-22*x*x+41*x
	14 [XOR, NOR, NAND, AND, OR, XNOR]  25 42 7 	25-26*x*x+43*x
	14 [XOR, NOR, NAND, OR, AND, XNOR]  25 44 7 	25-28*x*x+47*x
	14 [XOR, NOR, OR, AND, NAND, XNOR]  19 42 13 	19-26*x*x+49*x
	14 [XOR, NOR, OR, NAND, AND, XNOR]  21 44 11 	21-28*x*x+51*x
	14 [XOR, OR, NAND, AND, NOR, XNOR]  11 56 21 	11-40*x*x+85*x
	14 [XOR, OR, NAND, NOR, AND, XNOR]  13 56 19 	13-40*x*x+83*x
	14 [XOR, OR, NOR, AND, NAND, XNOR]  11 50 21 	11-34*x*x+73*x
	14 [XOR, OR, NOR, NAND, AND, XNOR]  13 52 19 	13-36*x*x+75*x
	15 [AND, NAND, XOR, NOR, XNOR, OR]  22 25 35 	22+3.5*x*x-.5*x // the rest have .5
	15 [AND, NAND, XOR, OR, XNOR, NOR]  19 28 38 	19+.5*x*x+8.5*x
	15 [AND, OR, NOR, XOR, XNOR, NAND]  11 21 50 	11+9.5*x*x+.5*x
	15 [AND, OR, XOR, NAND, XNOR, NOR]  7 28 50 	7+.5*x*x+20.5*x
	15 [AND, XOR, NOR, XNOR, OR, NAND]  13 19 38 	13+6.5*x*x-.5*x
	15 [AND, XOR, OR, NOR, XNOR, NAND]  7 25 42 	7-.5*x*x+18.5*x
	15 [AND, XOR, OR, XNOR, NAND, NOR]  7 26 44 	7-.5*x*x+19.5*x
	15 [AND, XOR, OR, XNOR, NOR, NAND]  7 25 44 	7+.5*x*x+17.5*x
	15 [AND, XOR, XNOR, NAND, NOR, OR]  14 21 41 	14+6.5*x*x+.5*x
	15 [NAND, AND, XNOR, NOR, XOR, OR]  44 35 25 	44-.5*x*x-8.5*x
	15 [NAND, AND, XNOR, OR, XOR, NOR]  41 38 28 	41-3.5*x*x+.5*x
	15 [NAND, NOR, OR, XNOR, XOR, AND]  52 42 13 	52-9.5*x*x-.5*x
	15 [NAND, XNOR, OR, XOR, NOR, AND]  50 44 25 	50-6.5*x*x+.5*x
	15 [NAND, XNOR, XOR, AND, OR, NOR]  49 42 22 	49-6.5*x*x-.5*x
	15 [NOR, XOR, OR, AND, XNOR, NAND]  35 25 14 	35-.5*x*x-9.5*x
	15 [OR, XNOR, NOR, NAND, XOR, AND]  28 38 49 	28+.5*x*x+9.5*x
	16 [AND, NAND, OR, XNOR, XOR, NOR]  21 26 44 	21+6.5*x*x-1.5*x
	16 [AND, NAND, OR, XOR, XNOR, NOR]  19 28 42 	19+2.5*x*x+6.5*x
	16 [AND, NAND, XOR, XNOR, NOR, OR]  22 25 37 	22+4.5*x*x-1.5*x
	16 [AND, NAND, XOR, XNOR, OR, NOR]  21 26 38 	21+3.5*x*x+1.5*x
	16 [AND, OR, NAND, XNOR, XOR, NOR]  13 26 52 	13+6.5*x*x+6.5*x
	16 [AND, OR, XNOR, XOR, NAND, NOR]  11 22 56 	11+11.5*x*x-.5*x
	16 [AND, OR, XOR, NOR, XNOR, NAND]  7 25 50 	7+3.5*x*x+14.5*x
	16 [AND, OR, XOR, XNOR, NAND, NOR]  7 26 52 	7+3.5*x*x+15.5*x
	16 [AND, OR, XOR, XNOR, NOR, NAND]  7 25 52 	7+4.5*x*x+13.5*x
	16 [AND, XOR, NAND, NOR, XNOR, OR]  14 25 35 	14-.5*x*x+11.5*x
	16 [AND, XOR, NAND, XNOR, NOR, OR]  14 25 37 	14+.5*x*x+10.5*x
	16 [AND, XOR, NAND, XNOR, OR, NOR]  13 26 38 	13-.5*x*x+13.5*x
	16 [AND, XOR, NOR, NAND, XNOR, OR]  14 21 35 	14+3.5*x*x+3.5*x
	16 [AND, XOR, NOR, OR, XNOR, NAND]  11 21 38 	11+3.5*x*x+6.5*x
	16 [AND, XOR, NOR, XNOR, NAND, OR]  14 19 37 	14+6.5*x*x-1.5*x
	16 [AND, XOR, OR, NAND, XNOR, NOR]  7 28 42 	7-3.5*x*x+24.5*x
	16 [AND, XOR, XNOR, NAND, OR, NOR]  13 22 42 	13+5.5*x*x+3.5*x
	16 [AND, XOR, XNOR, NOR, NAND, OR]  14 19 41 	14+8.5*x*x-3.5*x
	16 [AND, XOR, XNOR, NOR, OR, NAND]  13 19 42 	13+8.5*x*x-2.5*x
	16 [AND, XOR, XNOR, OR, NAND, NOR]  11 22 44 	11+5.5*x*x+5.5*x
	16 [AND, XOR, XNOR, OR, NOR, NAND]  11 21 44 	11+6.5*x*x+3.5*x
	16 [NAND, AND, NOR, XNOR, XOR, OR]  44 35 21 	44-2.5*x*x-6.5*x
	16 [NAND, AND, NOR, XOR, XNOR, OR]  42 37 19 	42-6.5*x*x+1.5*x
	16 [NAND, AND, XNOR, XOR, NOR, OR]  42 37 25 	42-3.5*x*x-1.5*x
	16 [NAND, AND, XNOR, XOR, OR, NOR]  41 38 26 	41-4.5*x*x+1.5*x
	16 [NAND, NOR, AND, XOR, XNOR, OR]  50 37 11 	50-6.5*x*x-6.5*x
	16 [NAND, NOR, XNOR, AND, XOR, OR]  56 35 13 	56-.5*x*x-20.5*x
	16 [NAND, NOR, XOR, XNOR, AND, OR]  52 41 7 	52-11.5*x*x+.5*x
	16 [NAND, XNOR, AND, OR, XOR, NOR]  49 38 28 	49+.5*x*x-11.5*x
	16 [NAND, XNOR, AND, XOR, NOR, OR]  50 37 25 	50+.5*x*x-13.5*x
	16 [NAND, XNOR, AND, XOR, OR, NOR]  49 38 26 	49-.5*x*x-10.5*x
	16 [NAND, XNOR, NOR, OR, XOR, AND]  56 38 21 	56+.5*x*x-18.5*x
	16 [NAND, XNOR, NOR, XOR, AND, OR]  56 37 19 	56+.5*x*x-19.5*x
	16 [NAND, XNOR, NOR, XOR, OR, AND]  56 38 19 	56-.5*x*x-17.5*x
	16 [NAND, XNOR, OR, AND, XOR, NOR]  49 42 28 	49-3.5*x*x-3.5*x
	16 [NAND, XNOR, OR, NOR, XOR, AND]  52 42 25 	52-3.5*x*x-6.5*x
	16 [NAND, XNOR, OR, XOR, AND, NOR]  49 44 26 	49-6.5*x*x+1.5*x
	16 [NAND, XNOR, XOR, AND, NOR, OR]  50 41 21 	50-5.5*x*x-3.5*x
	16 [NAND, XNOR, XOR, NOR, AND, OR]  52 41 19 	52-5.5*x*x-5.5*x
	16 [NAND, XNOR, XOR, NOR, OR, AND]  52 42 19 	52-6.5*x*x-3.5*x
	16 [NAND, XNOR, XOR, OR, AND, NOR]  49 44 22 	49-8.5*x*x+3.5*x
	16 [NAND, XNOR, XOR, OR, NOR, AND]  50 44 21 	50-8.5*x*x+2.5*x
	16 [NOR, NAND, XOR, OR, XNOR, AND]  50 28 7 	50+.5*x*x-22.5*x
	16 [NOR, OR, XOR, NAND, XNOR, AND]  38 28 19 	38+.5*x*x-10.5*x
	16 [NOR, XOR, NAND, AND, XNOR, OR]  42 25 7 	42-.5*x*x-16.5*x
	16 [NOR, XOR, NAND, XNOR, AND, OR]  44 25 7 	44+.5*x*x-19.5*x
	16 [NOR, XOR, NAND, XNOR, OR, AND]  44 26 7 	44-.5*x*x-17.5*x
	16 [NOR, XOR, OR, NAND, XNOR, AND]  38 28 11 	38-3.5*x*x-6.5*x
	16 [NOR, XOR, OR, XNOR, AND, NAND]  37 25 14 	37+.5*x*x-12.5*x
	16 [NOR, XOR, OR, XNOR, NAND, AND]  38 26 13 	38-.5*x*x-11.5*x
	16 [OR, AND, XNOR, NOR, XOR, NAND]  13 35 56 	13-.5*x*x+22.5*x
	16 [OR, NOR, XNOR, AND, XOR, NAND]  25 35 44 	25-.5*x*x+10.5*x
	16 [OR, XNOR, AND, NAND, XOR, NOR]  21 38 56 	21+.5*x*x+16.5*x
	16 [OR, XNOR, AND, XOR, NAND, NOR]  19 38 56 	19-.5*x*x+19.5*x
	16 [OR, XNOR, AND, XOR, NOR, NAND]  19 37 56 	19+.5*x*x+17.5*x
	16 [OR, XNOR, NOR, AND, XOR, NAND]  25 35 52 	25+3.5*x*x+6.5*x
	16 [OR, XNOR, NOR, XOR, AND, NAND]  25 37 50 	25+.5*x*x+11.5*x
	16 [OR, XNOR, NOR, XOR, NAND, AND]  26 38 49 	26-.5*x*x+12.5*x
	17 [AND, NAND, NOR, XOR, XNOR, OR]  26 21 35 	26+9.5*x*x-14.5*x
	17 [AND, OR, NAND, XOR, XNOR, NOR]  11 28 50 	11+2.5*x*x+14.5*x
	17 [AND, OR, NOR, XNOR, XOR, NAND]  13 19 52 	13+13.5*x*x-7.5*x
	17 [AND, OR, XNOR, NAND, XOR, NOR]  13 22 56 	13+12.5*x*x-3.5*x
	17 [AND, OR, XNOR, NOR, XOR, NAND]  13 19 56 	13+15.5*x*x-9.5*x
	17 [AND, OR, XNOR, XOR, NOR, NAND]  11 21 56 	11+12.5*x*x-2.5*x
	17 [AND, XOR, NAND, OR, XNOR, NOR]  11 28 38 	11-3.5*x*x+20.5*x
	17 [NAND, AND, OR, XNOR, XOR, NOR]  37 42 28 	37-9.5*x*x+14.5*x
	17 [NAND, NOR, AND, XNOR, XOR, OR]  52 35 13 	52-2.5*x*x-14.5*x
	17 [NAND, NOR, OR, XOR, XNOR, AND]  50 44 11 	50-13.5*x*x+7.5*x
	17 [NAND, NOR, XNOR, OR, XOR, AND]  56 38 13 	56-3.5*x*x-14.5*x
	17 [NAND, NOR, XNOR, XOR, AND, OR]  56 37 11 	56-3.5*x*x-15.5*x
	17 [NAND, NOR, XNOR, XOR, OR, AND]  56 38 11 	56-4.5*x*x-13.5*x
	17 [NAND, NOR, XOR, AND, XNOR, OR]  50 41 7 	50-12.5*x*x+3.5*x
	17 [NAND, NOR, XOR, OR, XNOR, AND]  50 44 7 	50-15.5*x*x+9.5*x
	17 [NAND, NOR, XOR, XNOR, OR, AND]  52 42 7 	52-12.5*x*x+2.5*x
	17 [NAND, XNOR, AND, NOR, XOR, OR]  52 35 25 	52+3.5*x*x-20.5*x
	17 [NAND, XNOR, NOR, AND, XOR, OR]  56 35 21 	56+3.5*x*x-24.5*x
	17 [NOR, NAND, AND, XOR, XNOR, OR]  50 21 11 	50+9.5*x*x-38.5*x
	17 [NOR, NAND, OR, XNOR, XOR, AND]  52 26 13 	52+6.5*x*x-32.5*x
	17 [NOR, NAND, OR, XOR, XNOR, AND]  50 28 11 	50+2.5*x*x-24.5*x
	17 [NOR, NAND, XOR, AND, XNOR, OR]  50 25 7 	50+3.5*x*x-28.5*x
	17 [NOR, NAND, XOR, XNOR, AND, OR]  52 25 7 	52+4.5*x*x-31.5*x
	17 [NOR, NAND, XOR, XNOR, OR, AND]  52 26 7 	52+3.5*x*x-29.5*x
	17 [NOR, OR, AND, XOR, XNOR, NAND]  35 21 26 	35+9.5*x*x-23.5*x
	17 [NOR, OR, NAND, XNOR, XOR, AND]  44 26 21 	44+6.5*x*x-24.5*x
	17 [NOR, OR, NAND, XOR, XNOR, AND]  42 28 19 	42+2.5*x*x-16.5*x
	17 [NOR, OR, XOR, AND, XNOR, NAND]  35 25 22 	35+3.5*x*x-13.5*x
	17 [NOR, OR, XOR, XNOR, AND, NAND]  37 25 22 	37+4.5*x*x-16.5*x
	17 [NOR, OR, XOR, XNOR, NAND, AND]  38 26 21 	38+3.5*x*x-15.5*x
	17 [NOR, XOR, AND, NAND, XNOR, OR]  38 21 11 	38+3.5*x*x-20.5*x
	17 [NOR, XOR, AND, OR, XNOR, NAND]  35 21 14 	35+3.5*x*x-17.5*x
	17 [NOR, XOR, AND, XNOR, NAND, OR]  38 19 13 	38+6.5*x*x-25.5*x
	17 [NOR, XOR, AND, XNOR, OR, NAND]  37 19 14 	37+6.5*x*x-24.5*x
	17 [NOR, XOR, NAND, OR, XNOR, AND]  42 28 7 	42-3.5*x*x-10.5*x
	17 [NOR, XOR, XNOR, AND, NAND, OR]  42 19 13 	42+8.5*x*x-31.5*x
	17 [NOR, XOR, XNOR, AND, OR, NAND]  41 19 14 	41+8.5*x*x-30.5*x
	17 [NOR, XOR, XNOR, NAND, AND, OR]  44 21 11 	44+6.5*x*x-29.5*x
	17 [NOR, XOR, XNOR, NAND, OR, AND]  44 22 11 	44+5.5*x*x-27.5*x
	17 [NOR, XOR, XNOR, OR, AND, NAND]  41 21 14 	41+6.5*x*x-26.5*x
	17 [NOR, XOR, XNOR, OR, NAND, AND]  42 22 13 	42+5.5*x*x-25.5*x
	17 [OR, AND, NAND, XNOR, XOR, NOR]  13 42 52 	13-9.5*x*x+38.5*x
	17 [OR, AND, NOR, XNOR, XOR, NAND]  13 35 52 	13-2.5*x*x+24.5*x
	17 [OR, AND, NOR, XOR, XNOR, NAND]  11 37 50 	11-6.5*x*x+32.5*x
	17 [OR, AND, XNOR, NAND, XOR, NOR]  13 38 56 	13-3.5*x*x+28.5*x
	17 [OR, AND, XNOR, XOR, NAND, NOR]  11 38 56 	11-4.5*x*x+31.5*x
	17 [OR, AND, XNOR, XOR, NOR, NAND]  11 37 56 	11-3.5*x*x+29.5*x
	17 [OR, AND, XOR, NAND, XNOR, NOR]  7 44 50 	7-15.5*x*x+52.5*x
	17 [OR, AND, XOR, NOR, XNOR, NAND]  7 41 50 	7-12.5*x*x+46.5*x
	17 [OR, AND, XOR, XNOR, NAND, NOR]  7 42 52 	7-12.5*x*x+47.5*x
	17 [OR, AND, XOR, XNOR, NOR, NAND]  7 41 52 	7-11.5*x*x+45.5*x
	17 [OR, NOR, AND, XNOR, XOR, NAND]  21 35 44 	21-2.5*x*x+16.5*x
	17 [OR, NOR, AND, XOR, XNOR, NAND]  19 37 42 	19-6.5*x*x+24.5*x
	17 [OR, NOR, NAND, XNOR, XOR, AND]  28 42 37 	28-9.5*x*x+23.5*x
	17 [OR, NOR, XNOR, NAND, XOR, AND]  28 38 41 	28-3.5*x*x+13.5*x
	17 [OR, NOR, XNOR, XOR, AND, NAND]  25 37 42 	25-3.5*x*x+15.5*x
	17 [OR, NOR, XNOR, XOR, NAND, AND]  26 38 41 	26-4.5*x*x+16.5*x
	17 [OR, XNOR, AND, NOR, XOR, NAND]  21 35 56 	21+3.5*x*x+10.5*x
	17 [OR, XNOR, NAND, AND, XOR, NOR]  25 42 52 	25-3.5*x*x+20.5*x
	17 [OR, XNOR, NAND, NOR, XOR, AND]  28 42 49 	28-3.5*x*x+17.5*x
	17 [OR, XNOR, NAND, XOR, AND, NOR]  25 44 50 	25-6.5*x*x+25.5*x
	17 [OR, XNOR, NAND, XOR, NOR, AND]  26 44 49 	26-6.5*x*x+24.5*x
	17 [OR, XNOR, XOR, AND, NAND, NOR]  19 42 52 	19-6.5*x*x+29.5*x
	17 [OR, XNOR, XOR, AND, NOR, NAND]  19 41 52 	19-5.5*x*x+27.5*x
	17 [OR, XNOR, XOR, NAND, AND, NOR]  21 44 50 	21-8.5*x*x+31.5*x
	17 [OR, XNOR, XOR, NAND, NOR, AND]  22 44 49 	22-8.5*x*x+30.5*x
	17 [OR, XNOR, XOR, NOR, AND, NAND]  21 41 50 	21-5.5*x*x+25.5*x
	17 [OR, XNOR, XOR, NOR, NAND, AND]  22 42 49 	22-6.5*x*x+26.5*x
	17 [OR, XOR, AND, NAND, XNOR, NOR]  7 52 42 	7-27.5*x*x+72.5*x
	17 [OR, XOR, AND, NOR, XNOR, NAND]  7 49 42 	7-24.5*x*x+66.5*x
	17 [OR, XOR, AND, XNOR, NAND, NOR]  7 50 44 	7-24.5*x*x+67.5*x
	17 [OR, XOR, AND, XNOR, NOR, NAND]  7 49 44 	7-23.5*x*x+65.5*x
	17 [XOR, AND, OR, NAND, XNOR, NOR]  7 44 26 	7-27.5*x*x+64.5*x
	17 [XOR, AND, OR, NOR, XNOR, NAND]  7 41 26 	7-24.5*x*x+58.5*x
	17 [XOR, AND, OR, XNOR, NAND, NOR]  7 42 28 	7-24.5*x*x+59.5*x
	17 [XOR, AND, OR, XNOR, NOR, NAND]  7 41 28 	7-23.5*x*x+57.5*x
	17 [XOR, OR, AND, NAND, XNOR, NOR]  7 52 26 	7-35.5*x*x+80.5*x
	17 [XOR, OR, AND, NOR, XNOR, NAND]  7 49 26 	7-32.5*x*x+74.5*x
	17 [XOR, OR, AND, XNOR, NAND, NOR]  7 50 28 	7-32.5*x*x+75.5*x
	17 [XOR, OR, AND, XNOR, NOR, NAND]  7 49 28 	7-31.5*x*x+73.5*x
	18 [AND, NAND, NOR, XNOR, XOR, OR]  28 19 37 	28+13.5*x*x-22.5*x
	18 [AND, NAND, XNOR, NOR, XOR, OR]  28 19 41 	28+15.5*x*x-24.5*x
	18 [AND, NAND, XNOR, OR, XOR, NOR]  25 22 44 	25+12.5*x*x-15.5*x
	18 [AND, NAND, XNOR, XOR, NOR, OR]  26 21 41 	26+12.5*x*x-17.5*x
	18 [AND, NAND, XNOR, XOR, OR, NOR]  25 22 42 	25+11.5*x*x-14.5*x
	18 [AND, NOR, NAND, XNOR, XOR, OR]  28 11 37 	28+21.5*x*x-38.5*x
	18 [AND, NOR, NAND, XOR, XNOR, OR]  26 13 35 	26+17.5*x*x-30.5*x
	18 [AND, NOR, OR, XNOR, XOR, NAND]  21 11 44 	21+21.5*x*x-31.5*x
	18 [AND, NOR, OR, XOR, XNOR, NAND]  19 13 42 	19+17.5*x*x-23.5*x
	18 [AND, NOR, XNOR, NAND, XOR, OR]  28 7 41 	28+27.5*x*x-48.5*x
	18 [AND, NOR, XNOR, OR, XOR, NAND]  25 7 44 	25+27.5*x*x-45.5*x
	18 [AND, NOR, XNOR, XOR, NAND, OR]  26 7 41 	26+26.5*x*x-45.5*x
	18 [AND, NOR, XNOR, XOR, OR, NAND]  25 7 42 	25+26.5*x*x-44.5*x
	18 [AND, NOR, XOR, NAND, XNOR, OR]  22 13 35 	22+15.5*x*x-24.5*x
	18 [AND, NOR, XOR, OR, XNOR, NAND]  19 13 38 	19+15.5*x*x-21.5*x
	18 [AND, NOR, XOR, XNOR, NAND, OR]  22 11 37 	22+18.5*x*x-29.5*x
	18 [AND, NOR, XOR, XNOR, OR, NAND]  21 11 38 	21+18.5*x*x-28.5*x
	18 [AND, XNOR, NAND, NOR, XOR, OR]  28 11 49 	28+27.5*x*x-44.5*x
	18 [AND, XNOR, NAND, OR, XOR, NOR]  25 14 52 	25+24.5*x*x-35.5*x
	18 [AND, XNOR, NAND, XOR, NOR, OR]  26 13 49 	26+24.5*x*x-37.5*x
	18 [AND, XNOR, NAND, XOR, OR, NOR]  25 14 50 	25+23.5*x*x-34.5*x
	18 [AND, XNOR, NOR, NAND, XOR, OR]  28 7 49 	28+31.5*x*x-52.5*x
	18 [AND, XNOR, NOR, OR, XOR, NAND]  25 7 52 	25+31.5*x*x-49.5*x
	18 [AND, XNOR, NOR, XOR, NAND, OR]  26 7 49 	26+30.5*x*x-49.5*x
	18 [AND, XNOR, NOR, XOR, OR, NAND]  25 7 50 	25+30.5*x*x-48.5*x
	18 [AND, XNOR, OR, NAND, XOR, NOR]  21 14 56 	21+24.5*x*x-31.5*x
	18 [AND, XNOR, OR, NOR, XOR, NAND]  21 11 56 	21+27.5*x*x-37.5*x
	18 [AND, XNOR, OR, XOR, NAND, NOR]  19 14 56 	19+23.5*x*x-28.5*x
	18 [AND, XNOR, OR, XOR, NOR, NAND]  19 13 56 	19+24.5*x*x-30.5*x
	18 [AND, XNOR, XOR, NAND, NOR, OR]  22 13 49 	22+22.5*x*x-31.5*x
	18 [AND, XNOR, XOR, NAND, OR, NOR]  21 14 50 	21+21.5*x*x-28.5*x
	18 [AND, XNOR, XOR, NOR, NAND, OR]  22 11 49 	22+24.5*x*x-35.5*x
	18 [AND, XNOR, XOR, NOR, OR, NAND]  21 11 50 	21+24.5*x*x-34.5*x
	18 [AND, XNOR, XOR, OR, NAND, NOR]  19 14 52 	19+21.5*x*x-26.5*x
	18 [AND, XNOR, XOR, OR, NOR, NAND]  19 13 52 	19+22.5*x*x-28.5*x
	18 [NAND, AND, OR, XOR, XNOR, NOR]  35 44 26 	35-13.5*x*x+22.5*x
	18 [NAND, AND, XOR, NOR, XNOR, OR]  38 41 19 	38-12.5*x*x+15.5*x
	18 [NAND, AND, XOR, OR, XNOR, NOR]  35 44 22 	35-15.5*x*x+24.5*x
	18 [NAND, AND, XOR, XNOR, NOR, OR]  38 41 21 	38-11.5*x*x+14.5*x
	18 [NAND, AND, XOR, XNOR, OR, NOR]  37 42 22 	37-12.5*x*x+17.5*x
	18 [NAND, OR, AND, XNOR, XOR, NOR]  37 50 28 	37-17.5*x*x+30.5*x
	18 [NAND, OR, AND, XOR, XNOR, NOR]  35 52 26 	35-21.5*x*x+38.5*x
	18 [NAND, OR, NOR, XNOR, XOR, AND]  44 50 21 	44-17.5*x*x+23.5*x
	18 [NAND, OR, NOR, XOR, XNOR, AND]  42 52 19 	42-21.5*x*x+31.5*x
	18 [NAND, OR, XNOR, AND, XOR, NOR]  41 50 28 	41-15.5*x*x+24.5*x
	18 [NAND, OR, XNOR, NOR, XOR, AND]  44 50 25 	44-15.5*x*x+21.5*x
	18 [NAND, OR, XNOR, XOR, AND, NOR]  41 52 26 	41-18.5*x*x+29.5*x
	18 [NAND, OR, XNOR, XOR, NOR, AND]  42 52 25 	42-18.5*x*x+28.5*x
	18 [NAND, OR, XOR, AND, XNOR, NOR]  35 56 22 	35-27.5*x*x+48.5*x
	18 [NAND, OR, XOR, NOR, XNOR, AND]  38 56 19 	38-27.5*x*x+45.5*x
	18 [NAND, OR, XOR, XNOR, AND, NOR]  37 56 22 	37-26.5*x*x+45.5*x
	18 [NAND, OR, XOR, XNOR, NOR, AND]  38 56 21 	38-26.5*x*x+44.5*x
	18 [NAND, XOR, AND, NOR, XNOR, OR]  38 49 11 	38-24.5*x*x+35.5*x
	18 [NAND, XOR, AND, OR, XNOR, NOR]  35 52 14 	35-27.5*x*x+44.5*x
	18 [NAND, XOR, AND, XNOR, NOR, OR]  38 49 13 	38-23.5*x*x+34.5*x
	18 [NAND, XOR, AND, XNOR, OR, NOR]  37 50 14 	37-24.5*x*x+37.5*x
	18 [NAND, XOR, NOR, AND, XNOR, OR]  42 49 7 	42-24.5*x*x+31.5*x
	18 [NAND, XOR, NOR, OR, XNOR, AND]  42 52 7 	42-27.5*x*x+37.5*x
	18 [NAND, XOR, NOR, XNOR, AND, OR]  44 49 7 	44-23.5*x*x+28.5*x
	18 [NAND, XOR, NOR, XNOR, OR, AND]  44 50 7 	44-24.5*x*x+30.5*x
	18 [NAND, XOR, OR, AND, XNOR, NOR]  35 56 14 	35-31.5*x*x+52.5*x
	18 [NAND, XOR, OR, NOR, XNOR, AND]  38 56 11 	38-31.5*x*x+49.5*x
	18 [NAND, XOR, OR, XNOR, AND, NOR]  37 56 14 	37-30.5*x*x+49.5*x
	18 [NAND, XOR, OR, XNOR, NOR, AND]  38 56 13 	38-30.5*x*x+48.5*x
	18 [NAND, XOR, XNOR, AND, NOR, OR]  42 49 13 	42-21.5*x*x+28.5*x
	18 [NAND, XOR, XNOR, AND, OR, NOR]  41 50 14 	41-22.5*x*x+31.5*x
	18 [NAND, XOR, XNOR, NOR, AND, OR]  44 49 11 	44-21.5*x*x+26.5*x
	18 [NAND, XOR, XNOR, NOR, OR, AND]  44 50 11 	44-22.5*x*x+28.5*x
	18 [NAND, XOR, XNOR, OR, AND, NOR]  41 52 14 	41-24.5*x*x+35.5*x
	18 [NAND, XOR, XNOR, OR, NOR, AND]  42 52 13 	42-24.5*x*x+34.5*x
	18 [NOR, AND, NAND, XNOR, XOR, OR]  44 11 21 	44+21.5*x*x-54.5*x
	18 [NOR, AND, NAND, XOR, XNOR, OR]  42 13 19 	42+17.5*x*x-46.5*x
	18 [NOR, AND, OR, XNOR, XOR, NAND]  37 11 28 	37+21.5*x*x-47.5*x
	18 [NOR, AND, OR, XOR, XNOR, NAND]  35 13 26 	35+17.5*x*x-39.5*x
	18 [NOR, AND, XNOR, NAND, XOR, OR]  44 7 25 	44+27.5*x*x-64.5*x
	18 [NOR, AND, XNOR, OR, XOR, NAND]  41 7 28 	41+27.5*x*x-61.5*x
	18 [NOR, AND, XNOR, XOR, NAND, OR]  42 7 25 	42+26.5*x*x-61.5*x
	18 [NOR, AND, XNOR, XOR, OR, NAND]  41 7 26 	41+26.5*x*x-60.5*x
	18 [NOR, AND, XOR, NAND, XNOR, OR]  38 13 19 	38+15.5*x*x-40.5*x
	18 [NOR, AND, XOR, OR, XNOR, NAND]  35 13 22 	35+15.5*x*x-37.5*x
	18 [NOR, AND, XOR, XNOR, NAND, OR]  38 11 21 	38+18.5*x*x-45.5*x
	18 [NOR, AND, XOR, XNOR, OR, NAND]  37 11 22 	37+18.5*x*x-44.5*x
	18 [NOR, NAND, AND, XNOR, XOR, OR]  52 19 13 	52+13.5*x*x-46.5*x
	18 [NOR, NAND, XNOR, AND, XOR, OR]  56 19 13 	56+15.5*x*x-52.5*x
	18 [NOR, NAND, XNOR, OR, XOR, AND]  56 22 13 	56+12.5*x*x-46.5*x
	18 [NOR, NAND, XNOR, XOR, AND, OR]  56 21 11 	56+12.5*x*x-47.5*x
	18 [NOR, NAND, XNOR, XOR, OR, AND]  56 22 11 	56+11.5*x*x-45.5*x
	18 [NOR, OR, AND, XNOR, XOR, NAND]  37 19 28 	37+13.5*x*x-31.5*x
	18 [NOR, OR, XNOR, AND, XOR, NAND]  41 19 28 	41+15.5*x*x-37.5*x
	18 [NOR, OR, XNOR, NAND, XOR, AND]  44 22 25 	44+12.5*x*x-34.5*x
	18 [NOR, OR, XNOR, XOR, AND, NAND]  41 21 26 	41+12.5*x*x-32.5*x
	18 [NOR, OR, XNOR, XOR, NAND, AND]  42 22 25 	42+11.5*x*x-31.5*x
	18 [NOR, XNOR, AND, NAND, XOR, OR]  52 7 25 	52+31.5*x*x-76.5*x
	18 [NOR, XNOR, AND, OR, XOR, NAND]  49 7 28 	49+31.5*x*x-73.5*x
	18 [NOR, XNOR, AND, XOR, NAND, OR]  50 7 25 	50+30.5*x*x-73.5*x
	18 [NOR, XNOR, AND, XOR, OR, NAND]  49 7 26 	49+30.5*x*x-72.5*x
	18 [NOR, XNOR, NAND, AND, XOR, OR]  56 11 21 	56+27.5*x*x-72.5*x
	18 [NOR, XNOR, NAND, OR, XOR, AND]  56 14 21 	56+24.5*x*x-66.5*x
	18 [NOR, XNOR, NAND, XOR, AND, OR]  56 13 19 	56+24.5*x*x-67.5*x
	18 [NOR, XNOR, NAND, XOR, OR, AND]  56 14 19 	56+23.5*x*x-65.5*x
	18 [NOR, XNOR, OR, AND, XOR, NAND]  49 11 28 	49+27.5*x*x-65.5*x
	18 [NOR, XNOR, OR, NAND, XOR, AND]  52 14 25 	52+24.5*x*x-62.5*x
	18 [NOR, XNOR, OR, XOR, AND, NAND]  49 13 26 	49+24.5*x*x-60.5*x
	18 [NOR, XNOR, OR, XOR, NAND, AND]  50 14 25 	50+23.5*x*x-59.5*x
	18 [NOR, XNOR, XOR, AND, NAND, OR]  50 11 21 	50+24.5*x*x-63.5*x
	18 [NOR, XNOR, XOR, AND, OR, NAND]  49 11 22 	49+24.5*x*x-62.5*x
	18 [NOR, XNOR, XOR, NAND, AND, OR]  52 13 19 	52+22.5*x*x-61.5*x
	18 [NOR, XNOR, XOR, NAND, OR, AND]  52 14 19 	52+21.5*x*x-59.5*x
	18 [NOR, XNOR, XOR, OR, AND, NAND]  49 13 22 	49+22.5*x*x-58.5*x
	18 [NOR, XNOR, XOR, OR, NAND, AND]  50 14 21 	50+21.5*x*x-57.5*x
	18 [OR, AND, NAND, XOR, XNOR, NOR]  11 44 50 	11-13.5*x*x+46.5*x
	18 [OR, NAND, AND, XNOR, XOR, NOR]  21 50 44 	21-17.5*x*x+46.5*x
	18 [OR, NAND, AND, XOR, XNOR, NOR]  19 52 42 	19-21.5*x*x+54.5*x
	18 [OR, NAND, NOR, XNOR, XOR, AND]  28 50 37 	28-17.5*x*x+39.5*x
	18 [OR, NAND, NOR, XOR, XNOR, AND]  26 52 35 	26-21.5*x*x+47.5*x
	18 [OR, NAND, XNOR, AND, XOR, NOR]  25 50 44 	25-15.5*x*x+40.5*x
	18 [OR, NAND, XNOR, NOR, XOR, AND]  28 50 41 	28-15.5*x*x+37.5*x
	18 [OR, NAND, XNOR, XOR, AND, NOR]  25 52 42 	25-18.5*x*x+45.5*x
	18 [OR, NAND, XNOR, XOR, NOR, AND]  26 52 41 	26-18.5*x*x+44.5*x
	18 [OR, NAND, XOR, AND, XNOR, NOR]  19 56 38 	19-27.5*x*x+64.5*x
	18 [OR, NAND, XOR, NOR, XNOR, AND]  22 56 35 	22-27.5*x*x+61.5*x
	18 [OR, NAND, XOR, XNOR, AND, NOR]  21 56 38 	21-26.5*x*x+61.5*x
	18 [OR, NAND, XOR, XNOR, NOR, AND]  22 56 37 	22-26.5*x*x+60.5*x
	18 [OR, NOR, NAND, XOR, XNOR, AND]  26 44 35 	26-13.5*x*x+31.5*x
	18 [OR, NOR, XOR, AND, XNOR, NAND]  19 41 38 	19-12.5*x*x+34.5*x
	18 [OR, NOR, XOR, NAND, XNOR, AND]  22 44 35 	22-15.5*x*x+37.5*x
	18 [OR, NOR, XOR, XNOR, AND, NAND]  21 41 38 	21-11.5*x*x+31.5*x
	18 [OR, NOR, XOR, XNOR, NAND, AND]  22 42 37 	22-12.5*x*x+32.5*x
	18 [OR, XOR, NAND, AND, XNOR, NOR]  11 56 38 	11-31.5*x*x+76.5*x
	18 [OR, XOR, NAND, NOR, XNOR, AND]  14 56 35 	14-31.5*x*x+73.5*x
	18 [OR, XOR, NAND, XNOR, AND, NOR]  13 56 38 	13-30.5*x*x+73.5*x
	18 [OR, XOR, NAND, XNOR, NOR, AND]  14 56 37 	14-30.5*x*x+72.5*x
	18 [OR, XOR, NOR, AND, XNOR, NAND]  11 49 38 	11-24.5*x*x+62.5*x
	18 [OR, XOR, NOR, NAND, XNOR, AND]  14 52 35 	14-27.5*x*x+65.5*x
	18 [OR, XOR, NOR, XNOR, AND, NAND]  13 49 38 	13-23.5*x*x+59.5*x
	18 [OR, XOR, NOR, XNOR, NAND, AND]  14 50 37 	14-24.5*x*x+60.5*x
	18 [OR, XOR, XNOR, AND, NAND, NOR]  11 50 44 	11-22.5*x*x+61.5*x
	18 [OR, XOR, XNOR, AND, NOR, NAND]  11 49 44 	11-21.5*x*x+59.5*x
	18 [OR, XOR, XNOR, NAND, AND, NOR]  13 52 42 	13-24.5*x*x+63.5*x
	18 [OR, XOR, XNOR, NAND, NOR, AND]  14 52 41 	14-24.5*x*x+62.5*x
	18 [OR, XOR, XNOR, NOR, AND, NAND]  13 49 42 	13-21.5*x*x+57.5*x
	18 [OR, XOR, XNOR, NOR, NAND, AND]  14 50 41 	14-22.5*x*x+58.5*x
	18 [XNOR, AND, NAND, NOR, XOR, OR]  44 11 49 	44+35.5*x*x-68.5*x
	18 [XNOR, AND, NAND, OR, XOR, NOR]  41 14 52 	41+32.5*x*x-59.5*x
	18 [XNOR, AND, NAND, XOR, NOR, OR]  42 13 49 	42+32.5*x*x-61.5*x
	18 [XNOR, AND, NAND, XOR, OR, NOR]  41 14 50 	41+31.5*x*x-58.5*x
	18 [XNOR, AND, NOR, NAND, XOR, OR]  44 7 49 	44+39.5*x*x-76.5*x
	18 [XNOR, AND, NOR, OR, XOR, NAND]  41 7 52 	41+39.5*x*x-73.5*x
	18 [XNOR, AND, NOR, XOR, NAND, OR]  42 7 49 	42+38.5*x*x-73.5*x
	18 [XNOR, AND, NOR, XOR, OR, NAND]  41 7 50 	41+38.5*x*x-72.5*x
	18 [XNOR, AND, OR, NAND, XOR, NOR]  37 14 56 	37+32.5*x*x-55.5*x
	18 [XNOR, AND, OR, NOR, XOR, NAND]  37 11 56 	37+35.5*x*x-61.5*x
	18 [XNOR, AND, OR, XOR, NAND, NOR]  35 14 56 	35+31.5*x*x-52.5*x
	18 [XNOR, AND, OR, XOR, NOR, NAND]  35 13 56 	35+32.5*x*x-54.5*x
	18 [XNOR, AND, XOR, NAND, NOR, OR]  38 13 49 	38+30.5*x*x-55.5*x
	18 [XNOR, AND, XOR, NAND, OR, NOR]  37 14 50 	37+29.5*x*x-52.5*x
	18 [XNOR, AND, XOR, NOR, NAND, OR]  38 11 49 	38+32.5*x*x-59.5*x
	18 [XNOR, AND, XOR, NOR, OR, NAND]  37 11 50 	37+32.5*x*x-58.5*x
	18 [XNOR, AND, XOR, OR, NAND, NOR]  35 14 52 	35+29.5*x*x-50.5*x
	18 [XNOR, AND, XOR, OR, NOR, NAND]  35 13 52 	35+30.5*x*x-52.5*x
	18 [XNOR, NAND, AND, NOR, XOR, OR]  52 19 41 	52+27.5*x*x-60.5*x
	18 [XNOR, NAND, AND, OR, XOR, NOR]  49 22 44 	49+24.5*x*x-51.5*x
	18 [XNOR, NAND, AND, XOR, NOR, OR]  50 21 41 	50+24.5*x*x-53.5*x
	18 [XNOR, NAND, AND, XOR, OR, NOR]  49 22 42 	49+23.5*x*x-50.5*x
	18 [XNOR, NAND, NOR, AND, XOR, OR]  56 19 37 	56+27.5*x*x-64.5*x
	18 [XNOR, NAND, NOR, OR, XOR, AND]  56 22 37 	56+24.5*x*x-58.5*x
	18 [XNOR, NAND, NOR, XOR, AND, OR]  56 21 35 	56+24.5*x*x-59.5*x
	18 [XNOR, NAND, NOR, XOR, OR, AND]  56 22 35 	56+23.5*x*x-57.5*x
	18 [XNOR, NAND, OR, AND, XOR, NOR]  49 26 44 	49+20.5*x*x-43.5*x
	18 [XNOR, NAND, OR, NOR, XOR, AND]  52 26 41 	52+20.5*x*x-46.5*x
	18 [XNOR, NAND, OR, XOR, AND, NOR]  49 28 42 	49+17.5*x*x-38.5*x
	18 [XNOR, NAND, OR, XOR, NOR, AND]  50 28 41 	50+17.5*x*x-39.5*x
	18 [XNOR, NAND, XOR, AND, NOR, OR]  50 25 37 	50+18.5*x*x-43.5*x
	18 [XNOR, NAND, XOR, AND, OR, NOR]  49 26 38 	49+17.5*x*x-40.5*x
	18 [XNOR, NAND, XOR, NOR, AND, OR]  52 25 35 	52+18.5*x*x-45.5*x
	18 [XNOR, NAND, XOR, NOR, OR, AND]  52 26 35 	52+17.5*x*x-43.5*x
	18 [XNOR, NAND, XOR, OR, AND, NOR]  49 28 38 	49+15.5*x*x-36.5*x
	18 [XNOR, NAND, XOR, OR, NOR, AND]  50 28 37 	50+15.5*x*x-37.5*x
	18 [XNOR, NOR, AND, NAND, XOR, OR]  52 7 41 	52+39.5*x*x-84.5*x
	18 [XNOR, NOR, AND, OR, XOR, NAND]  49 7 44 	49+39.5*x*x-81.5*x
	18 [XNOR, NOR, AND, XOR, NAND, OR]  50 7 41 	50+38.5*x*x-81.5*x
	18 [XNOR, NOR, AND, XOR, OR, NAND]  49 7 42 	49+38.5*x*x-80.5*x
	18 [XNOR, NOR, NAND, AND, XOR, OR]  56 11 37 	56+35.5*x*x-80.5*x
	18 [XNOR, NOR, NAND, OR, XOR, AND]  56 14 37 	56+32.5*x*x-74.5*x
	18 [XNOR, NOR, NAND, XOR, AND, OR]  56 13 35 	56+32.5*x*x-75.5*x
	18 [XNOR, NOR, NAND, XOR, OR, AND]  56 14 35 	56+31.5*x*x-73.5*x
	18 [XNOR, NOR, OR, AND, XOR, NAND]  49 11 44 	49+35.5*x*x-73.5*x
	18 [XNOR, NOR, OR, NAND, XOR, AND]  52 14 41 	52+32.5*x*x-70.5*x
	18 [XNOR, NOR, OR, XOR, AND, NAND]  49 13 42 	49+32.5*x*x-68.5*x
	18 [XNOR, NOR, OR, XOR, NAND, AND]  50 14 41 	50+31.5*x*x-67.5*x
	18 [XNOR, NOR, XOR, AND, NAND, OR]  50 11 37 	50+32.5*x*x-71.5*x
	18 [XNOR, NOR, XOR, AND, OR, NAND]  49 11 38 	49+32.5*x*x-70.5*x
	18 [XNOR, NOR, XOR, NAND, AND, OR]  52 13 35 	52+30.5*x*x-69.5*x
	18 [XNOR, NOR, XOR, NAND, OR, AND]  52 14 35 	52+29.5*x*x-67.5*x
	18 [XNOR, NOR, XOR, OR, AND, NAND]  49 13 38 	49+30.5*x*x-66.5*x
	18 [XNOR, NOR, XOR, OR, NAND, AND]  50 14 37 	50+29.5*x*x-65.5*x
	18 [XNOR, OR, AND, NAND, XOR, NOR]  37 22 56 	37+24.5*x*x-39.5*x
	18 [XNOR, OR, AND, NOR, XOR, NAND]  37 19 56 	37+27.5*x*x-45.5*x
	18 [XNOR, OR, AND, XOR, NAND, NOR]  35 22 56 	35+23.5*x*x-36.5*x
	18 [XNOR, OR, AND, XOR, NOR, NAND]  35 21 56 	35+24.5*x*x-38.5*x
	18 [XNOR, OR, NAND, AND, XOR, NOR]  41 26 52 	41+20.5*x*x-35.5*x
	18 [XNOR, OR, NAND, NOR, XOR, AND]  44 26 49 	44+20.5*x*x-38.5*x
	18 [XNOR, OR, NAND, XOR, AND, NOR]  41 28 50 	41+17.5*x*x-30.5*x
	18 [XNOR, OR, NAND, XOR, NOR, AND]  42 28 49 	42+17.5*x*x-31.5*x
	18 [XNOR, OR, NOR, AND, XOR, NAND]  41 19 52 	41+27.5*x*x-49.5*x
	18 [XNOR, OR, NOR, NAND, XOR, AND]  44 22 49 	44+24.5*x*x-46.5*x
	18 [XNOR, OR, NOR, XOR, AND, NAND]  41 21 50 	41+24.5*x*x-44.5*x
	18 [XNOR, OR, NOR, XOR, NAND, AND]  42 22 49 	42+23.5*x*x-43.5*x
	18 [XNOR, OR, XOR, AND, NAND, NOR]  35 26 52 	35+17.5*x*x-26.5*x
	18 [XNOR, OR, XOR, AND, NOR, NAND]  35 25 52 	35+18.5*x*x-28.5*x
	18 [XNOR, OR, XOR, NAND, AND, NOR]  37 28 50 	37+15.5*x*x-24.5*x
	18 [XNOR, OR, XOR, NAND, NOR, AND]  38 28 49 	38+15.5*x*x-25.5*x
	18 [XNOR, OR, XOR, NOR, AND, NAND]  37 25 50 	37+18.5*x*x-30.5*x
	18 [XNOR, OR, XOR, NOR, NAND, AND]  38 26 49 	38+17.5*x*x-29.5*x
	18 [XNOR, XOR, AND, NAND, NOR, OR]  38 21 41 	38+18.5*x*x-35.5*x
	18 [XNOR, XOR, AND, NAND, OR, NOR]  37 22 42 	37+17.5*x*x-32.5*x
	18 [XNOR, XOR, AND, NOR, NAND, OR]  38 19 41 	38+20.5*x*x-39.5*x
	18 [XNOR, XOR, AND, NOR, OR, NAND]  37 19 42 	37+20.5*x*x-38.5*x
	18 [XNOR, XOR, AND, OR, NAND, NOR]  35 22 44 	35+17.5*x*x-30.5*x
	18 [XNOR, XOR, AND, OR, NOR, NAND]  35 21 44 	35+18.5*x*x-32.5*x
	18 [XNOR, XOR, NAND, AND, NOR, OR]  42 25 37 	42+14.5*x*x-31.5*x
	18 [XNOR, XOR, NAND, AND, OR, NOR]  41 26 38 	41+13.5*x*x-28.5*x
	18 [XNOR, XOR, NAND, NOR, AND, OR]  44 25 35 	44+14.5*x*x-33.5*x
	18 [XNOR, XOR, NAND, NOR, OR, AND]  44 26 35 	44+13.5*x*x-31.5*x
	18 [XNOR, XOR, NAND, OR, AND, NOR]  41 28 38 	41+11.5*x*x-24.5*x
	18 [XNOR, XOR, NAND, OR, NOR, AND]  42 28 37 	42+11.5*x*x-25.5*x
	18 [XNOR, XOR, NOR, AND, NAND, OR]  42 19 37 	42+20.5*x*x-43.5*x
	18 [XNOR, XOR, NOR, AND, OR, NAND]  41 19 38 	41+20.5*x*x-42.5*x
	18 [XNOR, XOR, NOR, NAND, AND, OR]  44 21 35 	44+18.5*x*x-41.5*x
	18 [XNOR, XOR, NOR, NAND, OR, AND]  44 22 35 	44+17.5*x*x-39.5*x
	18 [XNOR, XOR, NOR, OR, AND, NAND]  41 21 38 	41+18.5*x*x-38.5*x
	18 [XNOR, XOR, NOR, OR, NAND, AND]  42 22 37 	42+17.5*x*x-37.5*x
	18 [XNOR, XOR, OR, AND, NAND, NOR]  35 26 44 	35+13.5*x*x-22.5*x
	18 [XNOR, XOR, OR, AND, NOR, NAND]  35 25 44 	35+14.5*x*x-24.5*x
	18 [XNOR, XOR, OR, NAND, AND, NOR]  37 28 42 	37+11.5*x*x-20.5*x
	18 [XNOR, XOR, OR, NAND, NOR, AND]  38 28 41 	38+11.5*x*x-21.5*x
	18 [XNOR, XOR, OR, NOR, AND, NAND]  37 25 42 	37+14.5*x*x-26.5*x
	18 [XNOR, XOR, OR, NOR, NAND, AND]  38 26 41 	38+13.5*x*x-25.5*x
	18 [XOR, AND, NAND, NOR, XNOR, OR]  14 41 19 	14-24.5*x*x+51.5*x
	18 [XOR, AND, NAND, OR, XNOR, NOR]  11 44 22 	11-27.5*x*x+60.5*x
	18 [XOR, AND, NAND, XNOR, NOR, OR]  14 41 21 	14-23.5*x*x+50.5*x
	18 [XOR, AND, NAND, XNOR, OR, NOR]  13 42 22 	13-24.5*x*x+53.5*x
	18 [XOR, AND, NOR, NAND, XNOR, OR]  14 37 19 	14-20.5*x*x+43.5*x
	18 [XOR, AND, NOR, OR, XNOR, NAND]  11 37 22 	11-20.5*x*x+46.5*x
	18 [XOR, AND, NOR, XNOR, NAND, OR]  14 35 21 	14-17.5*x*x+38.5*x
	18 [XOR, AND, NOR, XNOR, OR, NAND]  13 35 22 	13-17.5*x*x+39.5*x
	18 [XOR, AND, XNOR, NAND, NOR, OR]  14 37 25 	14-17.5*x*x+40.5*x
	18 [XOR, AND, XNOR, NAND, OR, NOR]  13 38 26 	13-18.5*x*x+43.5*x
	18 [XOR, AND, XNOR, NOR, NAND, OR]  14 35 25 	14-15.5*x*x+36.5*x
	18 [XOR, AND, XNOR, NOR, OR, NAND]  13 35 26 	13-15.5*x*x+37.5*x
	18 [XOR, AND, XNOR, OR, NAND, NOR]  11 38 28 	11-18.5*x*x+45.5*x
	18 [XOR, AND, XNOR, OR, NOR, NAND]  11 37 28 	11-17.5*x*x+43.5*x
	18 [XOR, NAND, AND, NOR, XNOR, OR]  22 49 11 	22-32.5*x*x+59.5*x
	18 [XOR, NAND, AND, OR, XNOR, NOR]  19 52 14 	19-35.5*x*x+68.5*x
	18 [XOR, NAND, AND, XNOR, NOR, OR]  22 49 13 	22-31.5*x*x+58.5*x
	18 [XOR, NAND, AND, XNOR, OR, NOR]  21 50 14 	21-32.5*x*x+61.5*x
	18 [XOR, NAND, NOR, AND, XNOR, OR]  26 49 7 	26-32.5*x*x+55.5*x
	18 [XOR, NAND, NOR, OR, XNOR, AND]  26 52 7 	26-35.5*x*x+61.5*x
	18 [XOR, NAND, NOR, XNOR, AND, OR]  28 49 7 	28-31.5*x*x+52.5*x
	18 [XOR, NAND, NOR, XNOR, OR, AND]  28 50 7 	28-32.5*x*x+54.5*x
	18 [XOR, NAND, OR, AND, XNOR, NOR]  19 56 14 	19-39.5*x*x+76.5*x
	18 [XOR, NAND, OR, NOR, XNOR, AND]  22 56 11 	22-39.5*x*x+73.5*x
	18 [XOR, NAND, OR, XNOR, AND, NOR]  21 56 14 	21-38.5*x*x+73.5*x
	18 [XOR, NAND, OR, XNOR, NOR, AND]  22 56 13 	22-38.5*x*x+72.5*x
	18 [XOR, NAND, XNOR, AND, NOR, OR]  26 49 13 	26-29.5*x*x+52.5*x
	18 [XOR, NAND, XNOR, AND, OR, NOR]  25 50 14 	25-30.5*x*x+55.5*x
	18 [XOR, NAND, XNOR, NOR, AND, OR]  28 49 11 	28-29.5*x*x+50.5*x
	18 [XOR, NAND, XNOR, NOR, OR, AND]  28 50 11 	28-30.5*x*x+52.5*x
	18 [XOR, NAND, XNOR, OR, AND, NOR]  25 52 14 	25-32.5*x*x+59.5*x
	18 [XOR, NAND, XNOR, OR, NOR, AND]  26 52 13 	26-32.5*x*x+58.5*x
	18 [XOR, NOR, AND, NAND, XNOR, OR]  22 37 11 	22-20.5*x*x+35.5*x
	18 [XOR, NOR, AND, OR, XNOR, NAND]  19 37 14 	19-20.5*x*x+38.5*x
	18 [XOR, NOR, AND, XNOR, NAND, OR]  22 35 13 	22-17.5*x*x+30.5*x
	18 [XOR, NOR, AND, XNOR, OR, NAND]  21 35 14 	21-17.5*x*x+31.5*x
	18 [XOR, NOR, NAND, AND, XNOR, OR]  26 41 7 	26-24.5*x*x+39.5*x
	18 [XOR, NOR, NAND, OR, XNOR, AND]  26 44 7 	26-27.5*x*x+45.5*x
	18 [XOR, NOR, NAND, XNOR, AND, OR]  28 41 7 	28-23.5*x*x+36.5*x
	18 [XOR, NOR, NAND, XNOR, OR, AND]  28 42 7 	28-24.5*x*x+38.5*x
	18 [XOR, NOR, OR, AND, XNOR, NAND]  19 41 14 	19-24.5*x*x+46.5*x
	18 [XOR, NOR, OR, NAND, XNOR, AND]  22 44 11 	22-27.5*x*x+49.5*x
	18 [XOR, NOR, OR, XNOR, AND, NAND]  21 41 14 	21-23.5*x*x+43.5*x
	18 [XOR, NOR, OR, XNOR, NAND, AND]  22 42 13 	22-24.5*x*x+44.5*x
	18 [XOR, NOR, XNOR, AND, NAND, OR]  26 35 13 	26-15.5*x*x+24.5*x
	18 [XOR, NOR, XNOR, AND, OR, NAND]  25 35 14 	25-15.5*x*x+25.5*x
	18 [XOR, NOR, XNOR, NAND, AND, OR]  28 37 11 	28-17.5*x*x+26.5*x
	18 [XOR, NOR, XNOR, NAND, OR, AND]  28 38 11 	28-18.5*x*x+28.5*x
	18 [XOR, NOR, XNOR, OR, AND, NAND]  25 37 14 	25-17.5*x*x+29.5*x
	18 [XOR, NOR, XNOR, OR, NAND, AND]  26 38 13 	26-18.5*x*x+30.5*x
	18 [XOR, OR, NAND, AND, XNOR, NOR]  11 56 22 	11-39.5*x*x+84.5*x
	18 [XOR, OR, NAND, NOR, XNOR, AND]  14 56 19 	14-39.5*x*x+81.5*x
	18 [XOR, OR, NAND, XNOR, AND, NOR]  13 56 22 	13-38.5*x*x+81.5*x
	18 [XOR, OR, NAND, XNOR, NOR, AND]  14 56 21 	14-38.5*x*x+80.5*x
	18 [XOR, OR, NOR, AND, XNOR, NAND]  11 49 22 	11-32.5*x*x+70.5*x
	18 [XOR, OR, NOR, NAND, XNOR, AND]  14 52 19 	14-35.5*x*x+73.5*x
	18 [XOR, OR, NOR, XNOR, AND, NAND]  13 49 22 	13-31.5*x*x+67.5*x
	18 [XOR, OR, NOR, XNOR, NAND, AND]  14 50 21 	14-32.5*x*x+68.5*x
	18 [XOR, OR, XNOR, AND, NAND, NOR]  11 50 28 	11-30.5*x*x+69.5*x
	18 [XOR, OR, XNOR, AND, NOR, NAND]  11 49 28 	11-29.5*x*x+67.5*x
	18 [XOR, OR, XNOR, NAND, AND, NOR]  13 52 26 	13-32.5*x*x+71.5*x
	18 [XOR, OR, XNOR, NAND, NOR, AND]  14 52 25 	14-32.5*x*x+70.5*x
	18 [XOR, OR, XNOR, NOR, AND, NAND]  13 49 26 	13-29.5*x*x+65.5*x
	18 [XOR, OR, XNOR, NOR, NAND, AND]  14 50 25 	14-30.5*x*x+66.5*x
	18 [XOR, XNOR, AND, NAND, NOR, OR]  22 37 25 	22-13.5*x*x+28.5*x
	18 [XOR, XNOR, AND, NAND, OR, NOR]  21 38 26 	21-14.5*x*x+31.5*x
	18 [XOR, XNOR, AND, NOR, NAND, OR]  22 35 25 	22-11.5*x*x+24.5*x
	18 [XOR, XNOR, AND, NOR, OR, NAND]  21 35 26 	21-11.5*x*x+25.5*x
	18 [XOR, XNOR, AND, OR, NAND, NOR]  19 38 28 	19-14.5*x*x+33.5*x
	18 [XOR, XNOR, AND, OR, NOR, NAND]  19 37 28 	19-13.5*x*x+31.5*x
	18 [XOR, XNOR, NAND, AND, NOR, OR]  26 41 21 	26-17.5*x*x+32.5*x
	18 [XOR, XNOR, NAND, AND, OR, NOR]  25 42 22 	25-18.5*x*x+35.5*x
	18 [XOR, XNOR, NAND, NOR, AND, OR]  28 41 19 	28-17.5*x*x+30.5*x
	18 [XOR, XNOR, NAND, NOR, OR, AND]  28 42 19 	28-18.5*x*x+32.5*x
	18 [XOR, XNOR, NAND, OR, AND, NOR]  25 44 22 	25-20.5*x*x+39.5*x
	18 [XOR, XNOR, NAND, OR, NOR, AND]  26 44 21 	26-20.5*x*x+38.5*x
	18 [XOR, XNOR, NOR, AND, NAND, OR]  26 35 21 	26-11.5*x*x+20.5*x
	18 [XOR, XNOR, NOR, AND, OR, NAND]  25 35 22 	25-11.5*x*x+21.5*x
	18 [XOR, XNOR, NOR, NAND, AND, OR]  28 37 19 	28-13.5*x*x+22.5*x
	18 [XOR, XNOR, NOR, NAND, OR, AND]  28 38 19 	28-14.5*x*x+24.5*x
	18 [XOR, XNOR, NOR, OR, AND, NAND]  25 37 22 	25-13.5*x*x+25.5*x
	18 [XOR, XNOR, NOR, OR, NAND, AND]  26 38 21 	26-14.5*x*x+26.5*x
	18 [XOR, XNOR, OR, AND, NAND, NOR]  19 42 28 	19-18.5*x*x+41.5*x
	18 [XOR, XNOR, OR, AND, NOR, NAND]  19 41 28 	19-17.5*x*x+39.5*x
	18 [XOR, XNOR, OR, NAND, AND, NOR]  21 44 26 	21-20.5*x*x+43.5*x
	18 [XOR, XNOR, OR, NAND, NOR, AND]  22 44 25 	22-20.5*x*x+42.5*x
	18 [XOR, XNOR, OR, NOR, AND, NAND]  21 41 26 	21-17.5*x*x+37.5*x
	18 [XOR, XNOR, OR, NOR, NAND, AND]  22 42 25 	22-18.5*x*x+38.5*x
 */
