package bran.test;

public class BitTest {

	public static void main(String[] args) {

		for (int i = 0; i < 8; i++) {
			for (int j = 2; j >= 0; j--)
				System.out.print(((i >> j & 1) == 1 ? "1" : "0") + " ");
			System.out.println();
		}

	}

}
