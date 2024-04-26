package bran.tree.structure;

import bran.tree.structure.mapper.ForkOperator;

import java.util.function.BiFunction;

public interface UnifiedOperable<T, F extends ForkOperator<T, ?, ?>> extends Operable<T, T, T, F> {

}
