package bran.combinatorics;

public interface Combinator<T> {

	//how to iterate over

	//when it should add

	//when a path should end
	boolean end(T c);

	//amount of actions
	int caseMax();

	//action, return null if type is unavailable
	T invoke(T current, int type);

	//all types
	//List<T> invokeOf(T current);

}
