package bran.tree.structure;

import bran.tree.Holder;
import bran.tree.structure.mapper.ForkOperator;
import bran.tree.structure.mapper.Mapper;

public interface TreePart {

	static <P extends TreePart,							  		Pc extends Class<P>,
			O,													Oc extends Class<O>,
			FO extends ForkOperator<O, P, P>,					FOc extends Class<FO>,
			F extends MonoTypeFork<O, P, FO, F>,				Fc extends Class<F>,
			M extends Mapper,							  		Mc extends Class<M>,
			B extends MonoTypeChildBranch<M, ? extends P>,		Bc extends Class<B>,
			V extends Holder<?>,								Vc extends Class<V>>
	String treeVerifier(Pc mainType, Oc representingType, FOc forkOp, Fc fork, Mc branchOp, Bc branch, Vc variable) {
		return mainType.getSimpleName() + ": "
			   + fork.getSimpleName() + "(" + mainType.getSimpleName() + " " + forkOp.getSimpleName() + " " + mainType.getSimpleName()
			   + "), " + (variable == null ? "null" : variable.getSimpleName()) + "(" + representingType.getSimpleName() + "), "
			   + branch.getSimpleName() + "(" + branchOp.getSimpleName() + " " + mainType.getSimpleName() + ")";
	}

	static <P extends TreePart,
			O, FO extends ForkOperator<O, P, P>,
			F extends MonoTypeFork<O, P, FO, F>,
			M extends Mapper,
			B extends MonoTypeChildBranch<M, P>,
			V extends Holder<O>>
	String treeVerifier(P p, O o, FO fo, F f, M m, B b, V v) {
		return treeVerifier(p.getClass(), o.getClass(), fo.getClass(), f.getClass(), m.getClass(), b.getClass(), v.getClass());
	}

	// static boolean treeVerifier(String className) {
	// 	// if (!parent.getName().equals(TreePart.class.getName())) {
	// 	// 	return false;
	// 	// }
	// }

}
