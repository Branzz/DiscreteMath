package bran.tree.structure.mapper;

import bran.parser.matching.Pattern;
import bran.parser.matching.Tokenable;

import java.lang.reflect.Constructor;

public interface OrderedOperator extends Mapper {

	AssociativityPrecedenceLevel level();

	default int precedence() {
		return level().precedence();
	}

	default Associativity associativity() {
		return level().associativity();
	}

	// int maxOrder();

	// int minOrder();

	// default Class<? extends Tokenable> constructedForkClass() {
	// 	throw new RuntimeException("implement constructedForkClass() OR pattern()");
	// }
	//
	// @Override
	// default Pattern<? extends Tokenable> pattern() {
	// 	return makePattern(constructedForkClass());
	// }
	//
	// default <R extends Tokenable> Pattern<R> makePattern(Class<R> rClass) {
	// 	Constructor<R> constructor = constructedForkConstructor(rClass);
	// 	return makePattern(constructor, constructor.getParameterTypes());
	// }
	//
	// default <R extends Tokenable> Pattern<R> makePattern(Constructor<R> constructor, Class<?>[] tokenClasses) {
	// 	return new Pattern<>(precedence(), constructor, tokenClasses);
	// }
	//
	// default <R extends Tokenable> Constructor<R> constructedForkConstructor(Class<R> rClass) {
	// 	return (Constructor<R>) rClass.getConstructors()[0];
	// }

}
