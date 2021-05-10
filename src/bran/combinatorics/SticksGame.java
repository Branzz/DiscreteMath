package bran.combinatorics;

import java.util.List;

import bran.sets.numbers.NumberLiteral;

public class SticksGame {

	public static void main(String[] args) {

		Sticks sticks = new Sticks(new long[] {1, 1, 1, 1});
		CbrC c = new CbrC();
		List<Sticks> ends = new Combinatorics<Sticks>().combinationsOf(sticks, new CbrC());
//		for (Object o : ends)
//			System.out.println(o);
		for (Sticks s : ends)
			System.out.println(s + "\n" + (c.end(s) ? "end" : ""));

	}

	static class Sticks {

		private NumberLiteral[] data;

		public Sticks(int length) {
			data = new NumberLiteral[length];
		}

		public Sticks(NumberLiteral[] data) {
			this.data = data;
		}

		public Sticks(long[] data) {
			this.data = new NumberLiteral[data.length];
			for (int i = 0; i < 4; i++)
				this.data[i] = new NumberLiteral(data[i]);
		}
		
		public NumberLiteral[] getData() {
			return data;
		}

		public void setIndex(int i, NumberLiteral num) {
			data[i] = num;
		}

		@Override
		public boolean equals(Object other) {
			if (!(other instanceof Sticks) || ((Sticks) other).getData().length != data.length)
				return false;
			NumberLiteral[] otherData = ((Sticks) other).getData();
			for (int i = 0; i < data.length; i++)
				if (!data[i].equals(otherData[i]))
				return false;
			return true;
		}

		public String toString() {
			StringBuilder str = new StringBuilder(7);
			for (String s : new String[] { data[0].toString(), " ", data[1].toString(),
									 "\n", data[2].toString(), " ", data[3].toString() })
				str.append(s);
			return str.toString();
		}

	}

	static class CbrC implements Combinator<Sticks> {

		private final static NumberLiteral ZERO = new NumberLiteral(0);

		@Override
		public boolean end(Sticks c) {
			for (NumberLiteral n : ((Sticks) c).getData())
				if (n.equals(ZERO))
					return true;
			return false;
		}

		@Override
		public int caseMax() {
			return 28;
		}

		@Override
		public Sticks invoke(Sticks current, int type) {
			NumberLiteral[] invoked = ((Sticks) current).getData().clone();
			if (type < 8) {
				int posA = type / 2;
				int posB = type % 2 + 2 * (1 - type / 4);
				invoked[posB] = new NumberLiteral((invoked[posA].getValue() + invoked[posB].getValue()) % 5);
			}
			else {
				int pos = (type - 8) / 5;
				int toPos = (pos / 2) * 2 + 1 - pos % 2;
				
			}
			return new Sticks(invoked);
		}

//	interface Cbr extends Combinator<Object> {
//
//		@Override
//		public default boolean end(T c) {
//			for (NumberLiteral n : ((Sticks) c).getData())
//				if (n.equals(ZERO))
//					return true;
//			return false;
//		}
//
//		@Override
//		public default int caseMax() {
//			return 4;
//		}
//
//		@Override
//		public default T invoke(T current, int type) {
//			NumberLiteral[] invoked = ((Sticks) current).getData().clone();
//			invoked[type % 2 + 1] = new NumberLiteral((invoked[type / 1].getValue() + invoked[type % 2 + 1].getValue()) % 5);
//			NumberLiteral in0 = new NumberLiteral(invoked[0].getValue());
//			NumberLiteral in1 = new NumberLiteral(invoked[1].getValue());
//			invoked[0] = invoked[2];
//			invoked[2] = in0;
//			invoked[1] = invoked[3];
//			invoked[3] = in1;
//			return new Sticks(invoked);
//		}
//
////		@Override
////		public boolean end(Integer[] c) {
////			for (int i = 0; i < c.length; i++)
////				if (c[i] == 0)
////					return true;
////			return false;
////		}
////
////		@Override
////		public int caseMax() {
////			return 4;
////		}
////
////		@Override
////		public Integer[] invoke(Integer[] current, int type) {
////			Integer[] invoked = current.clone();
////			invoked[type % 2 + 1] = (invoked[type / 1] + invoked[type % 2 + 1]) % 5;
////			invoked[0] = invoked[2] + (invoked[2] = invoked[0]) - invoked[0];
////			invoked[1] = invoked[3] + (invoked[1] = invoked[3]) - invoked[1];
////			return invoked;
////		}
//
	}


}
