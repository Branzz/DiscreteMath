// package bran.logic.tree;
//
// import bran.exceptions.ParseException;
// import bran.logic.statements.LineStatement;
// import bran.logic.statements.OperationStatement;
// import bran.logic.statements.Statement;
// import bran.logic.statements.operators.LineOperator;
// import bran.logic.statements.operators.Operator;
//
// import java.util.AbstractCollection;
// import java.util.Iterator;
// import java.util.function.Supplier;
// import java.util.stream.Stream;
//
// public class TreeBuilder <T extends TreePart,
// 				FF extends ForkFunction, F extends Fork<T, FF, T>,
// 				BF extends BranchFunction, B extends Branch<T, BF>> {
//
// 	public static void main(String[] args) {
// 		TreeBuilder<Statement, Operator, OperationStatement, LineOperator, LineStatement> x;
// 	}
//
// 	private final TreeNodeChain treeNodeChain;
// 	private final BranchSupplier<T, BF> branchSupplier;
// 	private final ForkSupplier<T, FF, T> forkSupplier;
//
// 	public TreeBuilder(BranchSupplier<T, BF> branchSupplier, ForkSupplier<T, FF, T> forkSupplier) {
// 		treeNodeChain = this.new TreeNodeChain();
// 		this.branchSupplier = branchSupplier;
// 		this.forkSupplier = forkSupplier;
// 	}
//
// 	public T build() {
// 		if (treeNodeChain.isEmpty()) // TODO OR throw an error claiming you need to write something
// 			return null;
// 		// treeNodeChain.collectLineOperators();
// 		treeNodeChain.build();
// 		assert(treeNodeChain.size() == 1);
// 		return treeNodeChain.peek();
// 	}
//
// 	public void add(T treePart) {
// 		treeNodeChain.addNode(new StatementNode<>(treePart));
// 	}
//
// 	public void add(FF forkFunction) {
// 		treeNodeChain.addNode(new OperatorNode<>(forkFunction));
// 	}
//
// 	public void add(BF branchFunction) {
// 		treeNodeChain.addNode(new LineOperatorNode<>(branchFunction));
// 	}
//
// 	private class TreeNodeChain extends AbstractCollection<Node> {
//
// 		private Node head;
// 		private Node tail;
// 		private int size;
//
// 		public TreeNodeChain() {
// 			// LinkedList<Object> x;
// 			// x.iterator();
// 			// x.get(0)
// 			head = null;
// 			tail = null;
// 			size = 0;
// 		}
//
//
// 		@Override
// 		public Iterator<Node> iterator() {
// 			return new Iterator<>() {
// 				Node x = head;
// 				@Override public boolean hasNext() { return x.next != null; }
// 				@Override public Node next() { return x = x.next; }
// 			};
// 		}
//
// 		@Override
// 		public int size() {
// 			return size;
// 		}
//
// 		// @Override
// 		// public Object get(int index) {
// 		// 	if (!(index >= 0 && index < size))
// 		// 		throw new IndexOutOfBoundsException();
// 		// 	Node x = head;
// 		// 	for (int i = 0; i < index; i++)
// 		// 		x = x.next;
// 		// 	return x;
// 		// }
//
// 		public boolean addNode(Node next) throws ParseException {
// 			if (head == null) {
// 				head = next;
// 				tail = head;
// 			} else {
// 				tail = tail.append(next);
// 			}
// 			size++;
// 			return true;
// 		}
//
// 		@Override
// 		public void clear() {
// 			head = null;
// 			tail = null;
// 			size = 0;
// 		}
//
// 		// T lineStream(Node x, Node start) { // to collect right to left operator from the left
// 		// 	if (x instanceof LineOperatorNode<BF> lONnext){
// 		// 		return branchSupplier.get(lineStream(x.next, start), lONnext.branchFunction);
// 		// 		// return new Branch();
// 		// 	} else if (x instanceof StatementNode<T> nextStatement) { // base case
// 		// 		start.next = x.next;
// 		// 		return nextStatement.treePart;
// 		// 	}
// 		// 	throw new ParseException("statement parsing, unknown (this shouldn't've happened)");
// 		// }
//
// 		void build() {
// 			for (int order = Operator.MAX_ORDER; size != 1 && order >= Operator.MIN_ORDER; order--) {
// 				// collectBranchFromLeft(order);
// 				// collectBranchFromRight(order);
// 				// collectForkFromLeft(order);
// 				// collectForkFromRight(order);
// 			}
// 		}
//
// 		private boolean collectForkFromLeft(int order) {
// 			Node x = head;
// 			while (x.next != null) {
// 				if (x.next.next != null)
// 					if (x == head) {
// 						if (x.next instanceof OperatorNode o && o.forkFunction.getOrder() == order
// 							&& x instanceof StatementNode l
// 							&& x.next.next instanceof StatementNode r) {
//
// 							//	x -> x.next	-> x.next.next -> x.n.n.n
// 							//	S -> O	    -> S	-> ?/null
// 							// Node insert = new StatementNode(new F(
// 							// 		(Statement) x.value(), (Operator) x.next.value(), (Statement) x.next.next.value()));
// 							Node insert = new StatementNode<>(forkSupplier.get((T) l.value(), (FF) o.value(), (T) r.value()));
//
// 							insert.next = x.next.next.next;
// 							x = head = insert;
// 							size -= 2;
// 						} else
// 							x = x.next;
// 					}
// 					else if (x.next.next.next != null
// 							 && x.next.next instanceof OperatorNode o && o.forkFunction.getOrder() == order
// 							 && x.next instanceof StatementNode l
// 							 && x.next.next.next instanceof StatementNode r) {
// 						//	x -> x.next -> x.next.next	-> x.next.next.next -> x.n.n.n.n
// 						//	? -> S		-> O			-> S	-> ?/null
// 						// Node insert = new StatementNode(new Fork(
// 						// 		(Statement) x.next.value(), (Operator) x.next.next.value(), (Statement) x.next.next.next.value()));
// 						Node insert = new StatementNode<>(forkSupplier.get((T) l.value(), (FF) o.value(), (T) r.value()));
// 						insert.next = x.next.next.next.next;
// 						x.next = insert;
// 						size -= 2;
// 					} else
// 						x = x.next;
// 			}
// 		}
//
// 		private boolean collectForkFromRight(int order) {
// 			// not used by current implementation.
// 		}
//
// 		private boolean collectBranchFromLeft(int order) {
// 			Node x = head;
// 			while (x.next != null) {
// 				if (x == head) {
// 					//	x  -> x.next 	-> x.next.next	-> x.next.next.next
// 					//	LO -> Statement	-> ?/null
// 					if (x instanceof LineOperatorNode) {
// 						Node insert = new StatementNode<>(lineStream(x, x));
// 						insert.next = x.next;
// 						x = head = insert;
// 					} else
// 						x = x.next;
// 				} else {
// 					if (x.next instanceof LineOperatorNode) {
// 						//	x -> x.next -> x.next.next	-> x.next.next.next
// 						//	? -> LO		-> Statement	-> ?/null
// 						Node insert = new StatementNode<>(lineStream(x.next, x.next));
// 						insert.next = x.next.next;
// 						x.next = insert;
// 					} else
// 						x = x.next;
// 				}
// 			}
// 		}
//
// 		private boolean collectBranchFromRight(int order) {
//
// 		}
//
// 		// private static void throwUnknown() throws ParseException {
// 		// 	throw new ParseException("statement parsing, unknown (this shouldn't've happened)");
// 		// }
//
// 		public T peek() {
// 			return (T) head.value();
// 			// throw new ParseException("statement parsing, unknown (this shouldn't've happened)");
// 		}
//
// 	}
//
// 	private static abstract class Node {
// 		Node next = null;
// 		abstract TreePart value();
// 		abstract Node append(Node next) throws ParseException;
// 		// static Node of(Object o) {
// 		// 	if (o instanceof TreePart st)
// 		// 		return new StatementNode(st);
// 		// 	else if (o instanceof ForkFunction op)
// 		// 		return new OperatorNode(op);
// 		// 	else if (o instanceof BranchFunction lO)
// 		// 		return new LineOperatorNode(lO);
// 		// 	throw new ParseException("statement parsing, unknown (this shouldn't've happened)");
// 		// }
// 	}
//
// 	private static class StatementNode <T extends TreePart> extends Node {
// 		final T treePart;
// 		T value() { return treePart; }
// 		public StatementNode(final T treePart) {
// 			this.treePart = treePart;
// 		}
// 		Node append(Node next) {
// 			return this.next = next;
// 			// throw new ParseException("statement parsing, unknown (this shouldn't've happened)");
// 		}
// 	}
//
// 	private static class OperatorNode <FF extends ForkFunction> extends Node {
// 		final FF forkFunction;
// 		TreePart value() { return forkFunction; }
// 		public OperatorNode(final FF forkFunction) {
// 			this.forkFunction = forkFunction;
// 		}
// 		Node append(Node next) {
// 			return this.next = next;
// 		}
// 	}
//
// 	private static class LineOperatorNode <BF extends BranchFunction> extends StatementNode {
// 		final BF branchFunction;
// 		TreePart value() { return branchFunction; }
// 		public LineOperatorNode(final BF branchFunction) {
// 			super(null);
// 			this.branchFunction = branchFunction;
// 		}
// 		Node append(Node next) {
// 			if (next instanceof LineOperatorNode lO)
// 				return this.next = lO;
// 			else if (next instanceof StatementNode nS)
// 				return this.next = nS;
// 			throw new ParseException("statement parsing, unknown (this shouldn't've happened)");
// 		}
// 	}
//
//
// }
//
