package bran.parser;

import bran.exceptions.ParseException;
import bran.tree.compositions.expressions.functions.FunctionExpression;
import bran.tree.compositions.statements.LineStatement;
import bran.tree.compositions.statements.OperationStatement;
import bran.tree.compositions.statements.Statement;
import bran.tree.compositions.statements.operators.LineOperator;
import bran.tree.compositions.statements.operators.LogicalOperator;
import bran.tree.compositions.statements.special.equivalences.Equivalence;
import bran.tree.compositions.statements.special.equivalences.EquivalenceType;
import bran.tree.compositions.expressions.Expression;
import bran.tree.compositions.expressions.functions.ExpFunction;
import bran.exceptions.IllegalArgumentAmountException;
import bran.tree.compositions.expressions.operators.Operator;
import bran.tree.compositions.expressions.operators.OperatorExpression;
import bran.tree.compositions.Composition;
import bran.tree.structure.mapper.Associativity;
import bran.tree.structure.mapper.AssociativityPrecedenceLevel;
import bran.tree.structure.mapper.ForkOperator;
import bran.tree.structure.mapper.OrderedOperator;

import java.util.function.Function;
import java.util.function.Supplier;

public class CompositionBuilder {

	private final CompositionChain compositionChain;
	// Alternates Statements and Operators
	// ex.: VariableStatement Operator OperationStatement Operator Operator.NOT VariableStatement

	public CompositionBuilder() {
		compositionChain = new CompositionChain();
	}

	public Composition build() throws IllegalArgumentAmountException {
		if (compositionChain.isEmpty())
			throw new ParseException("can't be empty");
		compositionChain.collect();
		// assert(compositionChain.size() == 1);
		if (compositionChain.size() != 1)
			throw new ParseException("unfinished composition");
		return compositionChain.peek();
	}

	public void add(Object obj) {
		if (obj instanceof LazyTypeVariable lazyTypeVariable)
			add(lazyTypeVariable);
		else if (obj instanceof Composition statement)
			add(statement);
		else if (obj instanceof ExpFunction expFunction)
			add(expFunction);
		else if (obj instanceof LineOperator lineOperator)
			add(lineOperator);
		else if (obj instanceof ForkOperator forkOperator)
			add(forkOperator); //
		else if (obj instanceof CommaSeparatedComposition compositions)
			add(compositions);
		else
			throw new ParseException("unknown token (builder)");
	}

	public void add(LazyTypeVariable lazyTypeVariable) {
		compositionChain.addNode(new CompositionChain.VariableNode(lazyTypeVariable));
	}

	public void add(Composition composition) {
		compositionChain.addNode(new CompositionChain.CompositionNode(composition));
	}

	public void add(ExpFunction expFunction) {
		compositionChain.addNode(new CompositionChain.FunctionNode(expFunction));
	}

	public void add(LineOperator lineOperator) {
		compositionChain.addNode(new CompositionChain.LineOperatorNode(lineOperator));
	}

	public void add(ForkOperator forkOperator) {
		compositionChain.addNode(new CompositionChain.OperatorNode(forkOperator));
	}
	// public void add(List<Composition> compositions) {
	// 	compositionChain.addNode(new CompositionChain.MultiCompositionNode(compositions));

	// }

	public void add(CommaSeparatedComposition expressions) {
		if (expressions.isSingleton())
			add(expressions.getAsSingleton());
		else
			add(expressions.getFull());
	}

	private static class CompositionChain {

		final static String unknownExcMessage = "unknown exception (this shouldn't've happened)";

		private Node head;
		private Node tail;
		private int size;

		public CompositionChain() {
			// LinkedList<Object> x;
			// x.iterator();
			// x.get(0)
			head =  tail = null;
			size = 0;
		}

		public int size() {
			return size;
		}

		// @Override
		// public Object get(int index) {
		// 	if (!(index >= 0 && index < size))
		// 		throw new IndexOutOfBoundsException();
		// 	Node x = head;
		// 	for (int i = 0; i < index; i++)
		// 		x = x.next;
		// 	return x;
		// }

		public void addNode(Node next) throws ParseException {
			if (head == null) {
				head = tail = next;
			} else {
				tail = tail.append(next);
			}
			size++;
		}

		public void collect() throws IllegalArgumentAmountException {
			if (size <= 1)
				return; // this leaves the only variable here as un found
			inferVariableTypes();
			// collectEquivalences();
			collectLineOperators();
			collectForkOperators();
			// collectLogicalOperators();
		}

		private void inferVariableTypes() {
			// for (Node z = head; z.next != null; z = z.next) {
			// 	if (z instanceof )
			// }
			Node x = head;
			while (x instanceof VariableNode) { // implicit null check
				x = x.next;
			}
			if (x == null)
				throw new ParseException("unexpected repeating variables");
			Node y = head;
			for (; y != x; y = y.next)
				((VariableNode) y).found(x.isExpression());
			// assert(x is not a VariableNode)
			for (; x.next != null; x = x.next) {
				if (x.next.value() instanceof LazyTypeVariable lazyVariable) {
					if (x.next == tail) {
						lazyVariable.found(x.isExpression());
						break;
					} else { // "a + b = c & d < e" = "a + ((b = c) & (d < e))"
						int leftPrecedence = -1;
						int rightPrecedence = -1;
						if (x instanceof OperatorNode opLeft)
							leftPrecedence = opLeft.value().precedence();
						else if (x instanceof EquivalenceTypeNode eqTNode)
							leftPrecedence = eqTNode.value().precedence();

						if (x.next.next instanceof OperatorNode opRight)
							rightPrecedence = opRight.value().precedence();
						else if (x.next.next instanceof EquivalenceTypeNode eqTRight)
							rightPrecedence = eqTRight.value().precedence();

						if (leftPrecedence == -1 && rightPrecedence == -1)
							throw new ParseException("unimplemented operator precedences");
						lazyVariable.found(leftPrecedence < rightPrecedence ? x.isExpression() : x.next.next.isExpression());
					}

				}
			}
		}

		void collectEquivalences() { // could combine with collectOperators
			Node x = head;
			while (x.next != null && x.next.next != null && x.next.next.next != null) {
				// x -> x.next -> x.next.next -> x.next.next.next
				// ? -> l      -> equiv type  -> r
				if (x == head) {
					if (x.next instanceof EquivalenceTypeNode eN) {
						if (x.isExpression() && x.next.next.isExpression()) {
							if (x.value() instanceof Expression left
								&& x.next.next.value() instanceof Expression right) {
								final EquivalenceNode insert = new EquivalenceNode(Equivalence.of(left, eN.equivalenceType, right));
								insert.next = x.next.next.next;
								x = head = insert;
								size -= 2;
							} else throw new ParseException("type mismatch");
						} else
							throw new ParseException("type error1");
					} else
						x = x.next;
				} else if (x.next.next instanceof EquivalenceTypeNode eN) {
					if (x.next.isExpression() && x.next.next.next.isExpression()) {
						if (x.next.value() instanceof Expression left
						 && x.next.next.next.value() instanceof Expression right) {
							final EquivalenceNode insert = new EquivalenceNode(Equivalence.of(left, eN.equivalenceType, right));
							insert.next = x.next.next.next.next;
							x.next = insert;
							size -= 2;
						} else throw new ParseException("type mismatch");
					} else
						throw new ParseException("type error2");
				} else
					x = x.next;
			}
		}

		void collectLineOperators() throws IllegalArgumentAmountException {
			Node x = head;
			while (x.next != null) {
				if (x == head) {
					//	x  -> x.next 	-> x.next.next	-> x.next.next.next
					//	LO -> Statement	-> ?/null
					if (x instanceof LineOperatorNode loN) {
						Node insert = new CompositionNode(lineStream(loN, x));
						insert.next = x.next;
						x = head = insert;
					} else
						x = x.next;
				} else {
					if (x.next instanceof LineOperatorNode loN) {
						//	x -> x.next -> x.next.next	-> x.next.next.next
						//	? -> LO		-> Statement	-> ?/null
						Node insert = new CompositionNode(lineStream(loN, x.next));
						insert.next = x.next.next;
						x.next = insert;
					} else
						x = x.next;
				}
			}
		}

		// void collectLogicalOperators() {
		// 	for (int order = LogicalOperator.MAX_ORDER; size != 1 && order >= LogicalOperator.MIN_ORDER; order--) {
		// 		Node x = head;
		// 		while (x.next != null && x.next.next != null) {
		// 			if (x == head) {
		// 				if (!x.next.isExpression() && x.next instanceof OperatorNode op && op.operator.getOrder() == order) {
		// 					//	x -> x.next	-> x.next.next -> x.n.n.n
		// 					//	S -> O	    -> S	-> ?/null
		// 				if (x.value() instanceof Statement left
		// 				 && x.next.value() instanceof LogicalOperator operator
		// 				 && x.next.next.value() instanceof Statement right) {
		// 					Node insert = new CompositionNode(new OperationStatement(left, operator, right));
		// 					insert.next = x.next.next.next;
		// 					x = head = insert;
		// 					size -= 2;
		// 					} else
		// 						throw new ParseException("type mismatch");
		// 				} else
		// 					x = x.next;
		// 			}
		// 			else if (!x.next.isExpression() && x.next.next.next != null && x.next.next instanceof OperatorNode op && op.operator.getOrder() == order) {
		// 				//	x -> x.next -> x.next.next	-> x.next.next.next -> x.n.n.n.n
		// 				//	? -> S		-> O			-> S	-> ?/null
		// 				if (x.next.value() instanceof Statement left
		// 				 && x.next.next.value() instanceof LogicalOperator operator
		// 				 && x.next.next.next.value() instanceof Statement right) {
		// 					Node insert = new CompositionNode(new OperationStatement(left, operator, right));
		// 					insert.next = x.next.next.next.next;
		// 					x.next = insert;
		// 					size -= 2;
		// 				} else throw new ParseException("type mismatch");
		// 			} else
		// 				x = x.next;
		// 		}
		// 	}
		// }

		Composition lineStream(Node x, Node start) throws IllegalArgumentAmountException {
			// return x.isExpression() ? new FunctionExpression(x.operator, lineStream0(x.next, start))
			// 			   :
			if (x instanceof LineOperatorNode lONode) {
				Composition compStreamed = lineStream(x.next, start);
				if (compStreamed instanceof LazyTypeVariable lazyVar)
					compStreamed = lazyVar.found(false);
				if (compStreamed instanceof Statement smtStreamed)
					return new LineStatement(smtStreamed, lONode.operator);
				else
					throw new ParseException("wrong type of argument for3");
			} else if (x instanceof FunctionNode fNode) {
				final Composition[] compositions = lineStreamArray(x.next, start);
				final Expression[] expressions = new Expression[compositions.length];
				for (int i = 0; i < compositions.length; i++) {
					if (compositions[i] instanceof Expression exp)
						expressions[i] = exp;
					else
						throw new ParseException("wrong type4");
				}
				return new FunctionExpression(fNode.function, expressions);
			} else if (x.value() instanceof Composition comp) { // base case
				// start.append(x.next);
				if (comp instanceof LazyTypeVariable v)
					return v;
				else
					return (Composition) x.value();
			}
			throw new ParseException(unknownExcMessage);
		}

		Composition[] lineStreamArray(Node x, Node start) throws IllegalArgumentAmountException {
			if (x instanceof LineOperatorNode lONode) {
				return new Composition[] { lineStream(lONode, start) };
			} else if (x instanceof CompositionNode cNode) { // base case
				// start.append(x.next);
				return new Composition[] { cNode.composition } ;
			} else if (x instanceof MultiCompositionNode nextStatement) {
				// start.append(x.next);
				return nextStatement.compositions.compositions().toArray(Composition[]::new);
			}
			throw new ParseException(unknownExcMessage);
		}

		@Override
		public String toString() {
			StringBuilder sB = new StringBuilder();
			Node x = head;
			while (x != null) {
				sB.append(x.value());
				x = x.next;
			}
			return sB.toString();
		}


		// @FunctionalInterface interface Iter { Node iter(Node n); }
		// @FunctionalInterface interface Setr { Node set(Node n, Node to); }
		// @FunctionalInterface interface SetEnd { Node set(Node to); }

		void collectForkOperators() {
			for (int order = AssociativityPrecedenceLevel.MAX; size != 1 && order >= AssociativityPrecedenceLevel.MIN; order--) {
				new ForkOpCollector(AssociativityPrecedenceLevel.of(order).associativity() == Associativity.LEFT_TO_RIGHT, order)
						.iterateAlong();
				// Node x = (LTR ? head : tail);
				// Iter i = LTR ? (n -> n.next) : (n -> n.prev);
				// Setr s = LTR ? ((n, to) -> n.next = to) : ((n, to) -> n.prev = to);
				// SetEnd end = LTR ? (to -> head = to) : (to -> tail = to);
				// while (i.iter(x) != null && i.iter(i.iter(x)) != null) {
				// 	if (x == (LTR ? head : tail)) {
				// 		if (i.iter(x).value() instanceof ForkOperator op && op.precedence() == order) {
				// 			Node insert = new CompositionNode(getComposition(x, op, i.iter(i.iter(x))));
				// 			s.set(insert, i.iter(i.iter(i.iter(x))));
				// 			x = end.set(insert);
				// 			size -= 2;
				// 		}
				// 		else {
				// 			x = i.iter(x);
				// 		}
				// 	}
				// 	else if (i.iter(i.iter(x)).value() instanceof ForkOperator op && op.precedence() == order) {
				// 		if (i.iter(i.iter(i.iter(x))) == null)
				// 			throw new ParseException("missing right side of exp");
				// 		Node insert = new CompositionNode(getComposition(i.iter(x), op, i.iter(i.iter(i.iter(x)))));
				// 		s.set(insert, i.iter(i.iter(i.iter(i.iter(x)))));
				// 		s.set(x, insert);
				// 		size -= 2;
				// 	} else
				// 		x = i.iter(x);
				// }

				// if (LTR)
				// while (x.next != null && x.next.next != null) {
				// 	if (x == head) {
				// 		if (x.next.value() instanceof ForkOperator op && op.precedence() == order) {
				// 			Node insert = new CompositionNode(getComposition(x, op, x.next.next));
				// 			insert.next = x.next.next.next;
				// 			x = head = insert;
				// 			size -= 2;
				// 		}
				// 		else {
				// 			x = x.next;
				// 		}
				// 	}
				// 	else if (x.next.next.value() instanceof ForkOperator op && op.precedence() == order) {
				// 		if (x.next.next.next == null)
				// 			throw new ParseException("missing right side of exp");
				// 		Node insert = new CompositionNode(getComposition(x.next, op, x.next.next.next));
				// 		insert.next = x.next.next.next.next;
				// 		x.next = insert;
				// 		size -= 2;
				// 	} else
				// 		x = x.next;
				// }
				// else
				// while (x.prev != null && x.prev.prev != null) {
				// 	if (x == tail) {
				// 		if (x.prev.value() instanceof ForkOperator op && op.precedence() == order) {
				// 			Node insert = new CompositionNode(getComposition(x.prev.prev, op, x));
				// 			insert.prev = x.prev.prev.prev;
				// 			x = tail = insert;
				// 			size -= 2;
				// 		}
				// 		else {
				// 			x = x.prev;
				// 		}
				// 	}
				// 	else if (x.prev.prev.value() instanceof ForkOperator op && op.precedence() == order) {
				// 		if (x.prev.prev.prev == null)
				// 			throw new ParseException("missing left side of exp");
				// 		Node insert = new CompositionNode(getComposition(x.prev.prev.prev, op, x.prev));
				// 		insert.prev = x.prev.prev.prev.prev;
				// 		x.prev = insert;
				// 		size -= 2;
				// 	} else
				// 		x = x.prev;
				// }
			}
		}

		private class ForkOpCollector {
			private Node x;
			private final boolean LTR;
			private final int order;

			public ForkOpCollector(boolean LTR, int order) {
				this.LTR = LTR;
				this.order = order;
				x = end();
			}

			void ltr(Supplier<Node> leftRun, Supplier<Node> rightRun) { if (LTR) rightRun.get(); else leftRun.get(); }
			Node ltr(Node leftNode, Node rightNode) { return LTR ? leftNode : rightNode; }
			Node iter(Node node) { return LTR ? node.next : node.prev; }
			Node iter() { return iter(x); }
			Node iter(int n, Node y) { return n == 1 ? y : iter(n - 1, iter(y)); }
			Node iter(int n) { return iter(n, x); }
			Node setIter(Node n, Node to) { return LTR ? (n.append(to)) : (n.prepend(to)); }
			// Node setIter(Node n, Node to) { return ltr(()-> n.next = to, () -> n.prev = to); }
			Node setIter(Node to) { return setIter(x, to); }
			Node setEnd(Node to) { if (LTR) return head = to; else return tail = to; }
			// Node setEnd(Node to) { return ltr(() -> head = to, () -> tail = to); }
			Node end() { return LTR ? head : tail; }
			boolean isEnd() { return x == end(); }

			void iterateAlong() {
				while (iter() != null && iter(iter()) != null) {
					Node y = isEnd() ? x : iter();
					if (iter(y).value() instanceof ForkOperator op && op.precedence() == order) {
						// final Node otherSide = iter(iter(y));
						Node insert = new CompositionNode(getComposition(y, op, iter(iter(y))));
						setIter(insert, iter(iter(iter(y))));
						if (isEnd())
							x = setEnd(insert);
						else
							setIter(insert);
						size -= 2;
					} else
						x = iter(y);
				}
			}

			private Composition getComposition(Node left, OrderedOperator op, Node right) {
				if (op instanceof Operator operator) {
					return new OperatorExpression(getAsExpression(left), operator, getAsExpression(right));
				} else if (op instanceof LogicalOperator logOp) {
					return new OperationStatement(getAsStatement(left), logOp, getAsStatement(right));
				} else if (op instanceof EquivalenceType eN) {
					return Equivalence.of(getAsExpression(left), eN, getAsExpression(right));
				} else throw new ParseException("weird type");
			}

			private Expression getAsExpression(Node node) {
				Expression exp;
				if (node.value() instanceof LazyTypeVariable lazyVar) {
					if (lazyVar.isExpression())
						exp = lazyVar.getAsVariable();
					else throw new ParseException("x");
				} else {
					if (node.value() instanceof Expression nodeExp)
						exp = nodeExp;
					else throw new ParseException("y");
				}
				return exp;
			}

			private Statement getAsStatement(Node node) {
				Statement smt;
				if (node.value() instanceof LazyTypeVariable lazyVar) {
					if (lazyVar.isStatement())
						smt = lazyVar.getAsVariableStatement();
					else
						throw new ParseException("used expression variable for a statement");
				} else {
					if (node.value() instanceof Statement nodeExp)
						smt = nodeExp;
					else
						throw new ParseException("used statement variable for a expression");
				}
				return smt;
			}

		}

		public Composition peek() {
			if (head.value() instanceof Composition comp)
				return comp;
			throw new ParseException(unknownExcMessage);
		}

		public boolean isEmpty() {
			return size == 0;
		}

		private static abstract class Node {
			protected Node prev = null;
			protected Node next = null;
			abstract Object value();
			Node append(Node next) throws ParseException {
				if (next != null)
					next.prev = this;
				return this.next = next;
			}
			Node prepend(Node prev) throws ParseException {
				if (prev != null)
					prev.next = this;
				return this.prev = prev;
			}
			static Node of(Object o) {
				if (o instanceof Composition c)
					return new CompositionNode(c);
				else if (o instanceof Operator op)
					return new OperatorNode(op);
				else if (o instanceof LineOperator lO)
					return new LineOperatorNode(lO);
				else if (o instanceof ExpFunction f)
					return new FunctionNode(f);
				else if (o instanceof CommaSeparatedComposition cE)
					return new MultiCompositionNode(cE);
				throw new ParseException(unknownExcMessage);
			}
			public abstract boolean isExpression();
		}

		private static class EquivalenceTypeNode extends Node {
			final EquivalenceType equivalenceType;
			public EquivalenceTypeNode(EquivalenceType equivalenceType) {
				this.equivalenceType = equivalenceType;
			}
			@Override EquivalenceType value() { return equivalenceType; }
			@Override public boolean isExpression() { return false; }
		}

		private static class EquivalenceNode extends Node {
			final Equivalence equivalence;
			public EquivalenceNode(Equivalence equivalence) {
				this.equivalence = equivalence;
			}
			@Override Equivalence value() { return equivalence; }
			@Override public boolean isExpression() { return false; }
		}

		private static class CompositionNode extends Node {
			final Composition composition;
			final boolean isExpression;
			@Override Composition value() { return composition; }
			public CompositionNode(Composition composition) {
				this.composition = composition;
				this.isExpression = composition instanceof Expression;
			}
			// @Override Node append(Node next) {
			// 	super.append(next);
			// 	if (next instanceof OperatorNode lO)
			// 		return this.next = lO;
			// 	else if (next instanceof EquivalenceTypeNode eTN)
			// 		return this.next = eTN;
			// 	// else if (next instanceof FunctionNode fN) {
			// 	// 	// implicit multiplication
			// 	// }
			// 	throw new ParseException(unknownExcMessage);
			// }
			@Override public boolean isExpression() { return isExpression; }
		}

		private static class VariableNode extends Node {
			/* NOT final */
			LazyTypeVariable variable;
			boolean isExpression;
			@Override LazyTypeVariable value() { return variable; }
			public VariableNode(LazyTypeVariable variable) {
				this.variable = variable;
			}
			// @Override Node append(Node next) {
			// 	super.append(next);
			// 	if (!variable.foundType()) // when variable is first; ex. "a +", "x*(y&"
			// 		variable.found(next.isExpression());
			// 	// if (next instanceof FunctionNode fN) {
			// 	// 	// implicit multiplication
			// 	// }
			// 	throw new ParseException(unknownExcMessage);
			// }
			@Override public boolean isExpression() { return isExpression; }
			void found(boolean isExpression) {
				this.isExpression = isExpression;
				variable.found(isExpression);
			}
		}

		private static class OperatorNode extends Node {
			final ForkOperator operator;
			final boolean isExpression;
			@Override ForkOperator value() { return operator; }
			public OperatorNode(ForkOperator operator) {
				this.operator = operator;
				this.isExpression = operator instanceof Operator; // !ForkOperator
			}
			@Override public boolean isExpression() { return isExpression; }
		}

		private static class LineOperatorNode extends Node {
			final LineOperator operator;
			@Override LineOperator value() { return operator; }
			public LineOperatorNode(LineOperator operator) {
				this.operator = operator;
			}
			@Override public boolean isExpression() { return false; }
		}

		private static class FunctionNode extends Node {
			final ExpFunction function;
			@Override ExpFunction value() { return function; }
			public FunctionNode(ExpFunction function) {
				this.function = function;
			}
			@Override public boolean isExpression() { return true; }
		}

		private static class MultiCompositionNode extends Node {
			// List<Composition> expressions;
			// public MultiCompositionNode(List<Composition> compositions) {
			// 	this.expressions = compositions;
			// }
			// @Override List<Composition> value() { return expressions; }
			// @Override public boolean isExpression() { return true; }
			CommaSeparatedComposition compositions;
			public MultiCompositionNode(CommaSeparatedComposition compositions) {
				this.compositions = compositions;
			}
			@Override CommaSeparatedComposition value() { return compositions; }
			@Override public boolean isExpression() { return !compositions.compositions().isEmpty()
															 && compositions.compositions().get(0) instanceof Expression; } // vague
		}

	}

}
