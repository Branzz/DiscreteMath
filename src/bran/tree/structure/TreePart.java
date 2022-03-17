package bran.tree.structure;

import bran.tree.structure.mapper.ForkOperator;
import bran.tree.structure.mapper.Mapper;

public interface TreePart {

	static <P extends TreePart,							  Pc extends Class<P>,
			FO extends ForkOperator,					  FOc extends Class<FO>,
			F extends Fork<? extends P, FO, ? extends P>, Fc extends Class<F>,
			M extends Mapper,							  Mc extends Class<M>,
			B extends Branch<? extends P, M>,			  Bc extends Class<B>>
	String treeVerifier(Pc mainType, FOc forkOp, Fc fork, Mc branchOp, Bc branch) {
		return mainType.getSimpleName() + ": "
			   + fork.getSimpleName() + "(" + mainType.getSimpleName() + " " + forkOp.getSimpleName() + " " + mainType.getSimpleName() + "), "
			   + branch.getSimpleName() + "(" + branchOp.getSimpleName() + "(" + mainType.getSimpleName() + "))";
	}

	static <P extends TreePart,
			FO extends ForkOperator,
			F extends Fork<P, FO, P>,
			M extends Mapper,
			B extends Branch<P, M>>
	String treeVerifier(P p, FO fo, F f, M m, B b) {
		return treeVerifier(p.getClass(), fo.getClass(), f.getClass(), m.getClass(), b.getClass());
	}

	// static boolean treeVerifier(String className) {
	// 	// if (!parent.getName().equals(TreePart.class.getName())) {
	// 	// 	return false;
	// 	// }
	// }

}
