package bran.parser;

import bran.exceptions.ParseException;
import bran.parser.CompositionParser.StringPart;
import bran.tree.compositions.expressions.functions.FunctionExpression;
import bran.tree.compositions.expressions.functions.MultiArgFunction;
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
import bran.tree.structure.mapper.*;
import org.jetbrains.annotations.NotNull;

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
			return Composition.empty();
		compositionChain.collect();
		// assert(compositionChain.size() == 1);
		if (compositionChain.size() != 1)
			throw new ParseException("unfinished composition %s", CompositionChain.OpCollector.combine(compositionChain.head, compositionChain.tail));
		return compositionChain.peek();
	}

	public void add(Object obj, StringPart sP) {
		if (obj instanceof LazyTypeVariable lazyTypeVariable)
			add(lazyTypeVariable, sP);
		else if (obj instanceof Composition statement)
			add(statement, sP);
		else if (obj instanceof ExpFunction expFunction)
			add(expFunction, sP);
		else if (obj instanceof LineOperator lineOperator)
			add(lineOperator, sP);
		else if (obj instanceof ForkOperator forkOperator)
			add(forkOperator, sP); //
		else if (obj instanceof CommaSeparatedComposition compositions)
			add(compositions, sP);
		else
			throw new ParseException("unknown token (builder)");
	}

	public void add(LazyTypeVariable lazyTypeVariable, StringPart sP) {
		compositionChain.addNode(new CompositionChain.VariableNode(lazyTypeVariable, sP));
	}

	public void add(Composition composition, StringPart sP) {
		compositionChain.addNode(new CompositionChain.CompositionNode(composition, sP));
	}

	public void add(ExpFunction expFunction, StringPart sP) {
		compositionChain.addNode(new CompositionChain.FunctionNode(expFunction, sP));
	}

	public void add(LineOperator lineOperator, StringPart sP) {
		compositionChain.addNode(new CompositionChain.LineOperatorNode(lineOperator, sP));
	}

	public void add(ForkOperator forkOperator, StringPart sP) {
		compositionChain.addNode(new CompositionChain.OperatorNode(forkOperator, sP));
	}
	// public void add(List<Composition> compositions) {
	// 	compositionChain.addNode(new CompositionChain.MultiCompositionNode(compositions));

	// }

	public void add(CommaSeparatedComposition expressions, StringPart sP) {
		if (expressions.isSingleton())
			add(expressions.getAsSingleton(), sP);
		else
			add(expressions.getFull(), sP);
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
			head = tail = null;
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
			// inferVariableTypes();
			// collectEquivalences();
			// collectLineOperators();
			collectOrderedOperators();
			// collectLogicalOperators();
		}

		private void inferVariableTypes() { // TODO MERGE WITH collect Ordered OPs
			// for (Node z = head; z.next != null; z = z.next) {
			// 	if (z instanceof )
			// }
			Node x = head;
			while (x instanceof VariableNode) { // implicit null check
				x = x.next;
			}
			if (x == null)
				throw new ParseException("unexpected repeating variable");
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

		// void collectLineOperators() throws IllegalArgumentAmountException {
		// 	Node x = head;
		// 	while (x.next != null) {
		// 		if (x == head) {
		// 			//	x  -> x.next 	-> x.next.next	-> x.next.next.next
		// 			//	LO -> Statement	-> ?/null
		// 			if (x instanceof LineOperatorNode || x instanceof FunctionNode) {
		// 				Node insert = new CompositionNode(lineStream(x));
		// 				insert.append(x.next);
		// 				x = head = insert;
		// 			} else
		// 				x = x.next;
		// 		} else {
		// 			if (x.next instanceof LineOperatorNode || x.next instanceof FunctionNode) {
		// 				//	x -> x.next -> x.next.next	-> x.next.next.next
		// 				//	? -> LO		-> Statement	-> ?/null
		// 				Node insert = new CompositionNode(lineStream(x.next));
		// 				insert.append(x.next.next);
		// 				x.append(insert);
		// 			} else
		// 				x = x.next;
		// 		}
		// 	}
		// }
		// void collectLineOperators() throws IllegalArgumentAmountException {
		// 	Node x = tail;
		// 	while (x.prev != null) {
		// 		if (x == tail) {
		// 			//	x  -> x.next 	-> x.next.next	-> x.next.next.next
		// 			//	LO -> Statement	-> ?/null
		// 			if (x.prev instanceof LineOperatorNode || x.prev instanceof FunctionNode) {
		// 				// Node insert = new CompositionNode(new LineStatement(x, x.prev), new FunctionExpression(x.prev, x));
		// 				insert.append(x.prev);
		// 				x = tail = insert;
		// 			} else
		// 				x = x.prev;
		// 		} else {
		// 			if (x.prev instanceof LineOperatorNode || x.prev instanceof FunctionNode) {
		// 				//	x -> x.next -> x.next.next	-> x.next.next.next
		// 				//	? -> LO		-> Statement	-> ?/null
		// 				Node insert = new CompositionNode(lineStream(x.prev));
		// 				insert.append(x.prev.prev);
		// 				x.append(insert);
		// 			} else
		// 				x = x.prev;
		// 		}
		// 	}
		// }

		// Composition lineStream(Node x) throws IllegalArgumentAmountException {
		// 	return lineStream(x, x);
		// }

		// Composition lineStream(Node x, Node start) throws IllegalArgumentAmountException {
		// 	// return x.isExpression() ? new FunctionExpression(x.operator, lineStream0(x.next, start))
		// 	// 			   :
		// 	if (x instanceof LineOperatorNode lONode) {
		// 		Composition compStreamed = lineStream(x.next, start);
		// 		if (compStreamed instanceof LazyTypeVariable lazyVar)
		// 			compStreamed = lazyVar.found(false);
		// 		if (compStreamed instanceof Statement smtStreamed)
		// 			return new LineStatement(lONode.operator, smtStreamed);
		// 		else
		// 			throw new ParseException("wrong type of argument for3");
		// 	} else if (x instanceof FunctionNode fNode) {
		// 		final Composition[] compositions = lineStreamArray(x.next, start);
		// 		final Expression[] expressions = new Expression[compositions.length];
		// 		for (int i = 0; i < compositions.length; i++) {
		// 			if (compositions[i] instanceof Expression exp)
		// 				expressions[i] = exp;
		// 			else
		// 				throw new ParseException("wrong type4");
		// 		}
		// 		return new FunctionExpression(fNode.function, expressions);
		// 	} else if (x.value() instanceof Composition comp) { // base case
		// 		// start.append(x.next);
		// 		if (comp instanceof LazyTypeVariable v)
		// 			return v;
		// 		else
		// 			return (Composition) x.value();
		// 	}
		// 	throw new ParseException(unknownExcMessage);
		// }

		// Composition[] lineStreamArray(Node x, Node start) throws IllegalArgumentAmountException {
		// 	if (x instanceof LineOperatorNode lONode) {
		// 		return new Composition[] { lineStream(lONode, start) };
		// 	} else if (x instanceof CompositionNode cNode) { // base case
		// 		start.append(x.next);
		// 		return new Composition[] { cNode.composition } ;
		// 	} else if (x instanceof MultiCompositionNode nextStatement) {
		// 		start.append(x.next);
		// 		return nextStatement.compositions.compositions().toArray(Composition[]::new);
		// 	}
		// 	throw new ParseException(unknownExcMessage);
		// }

		@Override
		public String toString() {
			StringBuilder sB = new StringBuilder();
			Node x = head;
			while (x != null) {
				sB.append(x.value()).append(' ');
				x = x.next;
			}
			return sB.toString();
		}

		void collectOrderedOperators() {
			for (int order = AssociativityPrecedenceLevel.MIN; size != 1 && order <= AssociativityPrecedenceLevel.MAX; order++) {
				new OpCollector(AssociativityPrecedenceLevel.of(order).associativity() == Associativity.LEFT_TO_RIGHT, order)
						.iterateAlong();
			}
		}

		private class OpCollector {
			private Node x;
			private final boolean LTR;
			private final int order;

			public OpCollector(boolean LTR, int order) {
				this.LTR = LTR;
				this.order = order;
			}

			void ltr(Supplier<Node> leftRun, Supplier<Node> rightRun) { if (LTR) rightRun.get(); else leftRun.get(); }
			Node ltr(Node leftNode, Node rightNode) { return LTR ? leftNode : rightNode; }
			Node iter(Node node) { return node == null ? null : (LTR ? node.next : node.prev); }
			Node revIter(Node node) { return node == null ? null : (LTR ? node.prev : node.next); }
			Node iter() { return iter(x); }
			Node iter(int n, Node y) { return n == 1 ? y : iter(n - 1, iter(y)); }
			Node iter(int n) { return iter(n, x); }
			Node setIter(Node n, Node to) { return LTR ? n.append(to) : n.prepend(to); }
			Node setIter(Node to) { return setIter(x, to); }
			Node start() { return LTR ? head : tail; }
			boolean isStart() { return x == start(); }
			Node setStart(Node to) { if (LTR) return head = to; else return tail = to; }
			Node end() { return LTR ? tail : head; }
			boolean isEnd() { return x == end(); }
			Node setEnd(Node to) { if (LTR) return tail = to; else return head = to; }

			void set(Node node, Node replace) {
				if (node == head) {
					node.next.prev = replace;
					replace.next = node.next;
					head = replace;
				} else if (node == tail) {
					node.prev.next = replace;
					replace.prev = node.prev;
					tail = replace;
				} else {
					replace.prev = node.prev;
					replace.next = node.next;
					node.prev.next = replace;
					node.next.prev = replace;
				}
			}

			// result: from -> node -> to
			void insert(Node prev, @NotNull Node node, Node next) {
				if (prev == null)
					head = node;
				if (next == null)
					tail = node;
				node.prepend(prev);
				node.append(next);
			}

			static StringPart combine(Node from, Node to) {
				StringBuilder combinedStr = new StringBuilder();
				for (Node n = from; n != to; n = n.next)
					combinedStr.append(n.stringPart.string()).append(' ');
				combinedStr.append(to.stringPart.string());
				return new StringPart(combinedStr.toString(), from.stringPart.from(), to.stringPart.to(), CompositionParser.TokenType.UNKNOWN);
			}

			// a -> x.prev -> op -> x.next -> b ===> a -> new -> b
			void iterateAlong() {
				for (x = start(); x != null; x = iter(x)) {
					if (x.value() instanceof OrderedOperator op
						&& op.precedence() == order) {
						if (x.next != null && op instanceof BranchOperator bOp) {
							insert(x.prev, new CompositionNode(getBranchComposition(bOp, x.next), combine(x, x.next)), x.next.next);
							size -= 1;
						} else if (x.prev != null && x.next != null && op instanceof ForkOperator fOp) {
							insert(x.prev.prev, new CompositionNode(getComposition(x.prev, fOp, x.next), combine(x.prev, x.next)), x.next.next);
							size -= 2;
						} else throw new ParseException("misplaced function/operation \"%s\" at index %d", op, x.stringPart.from());
					}
				}
			}

			private Composition getBranchComposition(BranchOperator op, Node child) {
				if (op instanceof LineOperator lOp) {
					return new LineStatement(lOp, getAsStatement(child));
				} else if (op instanceof MultiArgFunction mAF) {
					try {
						return new FunctionExpression(mAF, getAsExpression(child));
					} catch (IllegalArgumentAmountException e) {
						throw new ParseException(e.getMessage()); // + " for " + op
					}
				}
				else throw new ParseException("unknown/unimplemented branch operator");
			}

			private Composition getComposition(Node left, ForkOperator op, Node right) {
				if (op instanceof Operator operator)
					return new OperatorExpression(getAsExpression(left), operator, getAsExpression(right));
				else if (op instanceof LogicalOperator logOp)
					return new OperationStatement(getAsStatement(left), logOp, getAsStatement(right));
				else if (op instanceof EquivalenceType eN)
					return Equivalence.of(getAsExpression(left), eN, getAsExpression(right));
				else
					throw new ParseException("unknown/unimplemented fork operator");
			}

			private Expression getAsExpression(Node node) { // *MUTABLE*
				Expression exp;
				if (node.value() instanceof LazyTypeVariable lazyVar) {
					if (lazyVar.isStatement())
						throw new ParseException("found statement variable \"%s\" at index %d, expected expression", lazyVar.toString(), node.stringPart.from());
					else
						exp = lazyVar.foundAsExpression();
				} else {
					if (node.value() instanceof Expression nodeExp)
						exp = nodeExp;
					else
						throw new ParseException("found statement \"%s\" at index %d, expected expression", node.toString(), node.stringPart.from());
				}
				return exp;
			}

			private Statement getAsStatement(Node node) { // *MUTABLE*
				Statement smt;
				if (node.value() instanceof LazyTypeVariable lazyVar) {
					if (lazyVar.isExpression())
						throw new ParseException("found expression variable \"%s\" at index %d, expected statement", lazyVar.toString(), node.stringPart.from());
					else
						smt = lazyVar.foundAsStatement();
				} else {
					if (node.value() instanceof Statement nodeExp)
						smt = nodeExp;
					else
						throw new ParseException("found expression \"%s\" at index %d, expected statement", node.toString(), node.stringPart.from());
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
			private final StringPart stringPart;
			// public Node() {
			// 	stringPart = null;
			// }
			public Node(StringPart stringPart) {
				this.stringPart = stringPart;
			}
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
			// static Node of(Object o) {
			// 	if (o instanceof Composition c)
			// 		return new CompositionNode(c);
			// 	else if (o instanceof Operator op)
			// 		return new OperatorNode(op);
			// 	else if (o instanceof LineOperator lO)
			// 		return new LineOperatorNode(lO);
			// 	else if (o instanceof ExpFunction f)
			// 		return new FunctionNode(f);
			// 	else if (o instanceof CommaSeparatedComposition cE)
			// 		return new MultiCompositionNode(cE);
			// 	throw new ParseException(unknownExcMessage);
			// }
			public abstract boolean isExpression();

			// @Override
			// public String toString() {
			// 	StringBuilder sB = new StringBuilder();
			// 	Node x = this;
			// 	while (x != null) {
			// 		sB.append(x.value()).append(' ');
			// 		x = x.next;
			// 	}
			// 	return sB.toString();
			// }

			@Override
			public String toString() {
				return value().toString();
			}

		}

		private static class EquivalenceTypeNode extends Node {
			final EquivalenceType equivalenceType;
			public EquivalenceTypeNode(EquivalenceType equivalenceType, StringPart stringPart) {
				super(stringPart);
				this.equivalenceType = equivalenceType;
			}
			@Override EquivalenceType value() { return equivalenceType; }
			@Override public boolean isExpression() { return false; }
		}

		private static class CompositionNode extends Node {
			final Composition composition;
			final boolean isExpression;
			@Override Composition value() { return composition; }
			public CompositionNode(Composition composition, StringPart stringPart) {
				super(stringPart);
				this.composition = composition;
				this.isExpression = composition instanceof Expression;
			}
			@Override public boolean isExpression() { return isExpression; }
		}

		private static class VariableNode extends Node {
			final LazyTypeVariable variable;
			/* NON final */ boolean isExpression;
			@Override LazyTypeVariable value() { return variable; }
			public VariableNode(LazyTypeVariable variable, StringPart stringPart) {
				super(stringPart);
				this.variable = variable;
			}
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
			public OperatorNode(ForkOperator operator, StringPart stringPart) {
				super(stringPart);
				this.operator = operator;
				this.isExpression = operator instanceof Operator;
			}
			@Override public boolean isExpression() { return isExpression; }
		}

		private static class LineOperatorNode extends Node {
			final LineOperator operator;
			@Override LineOperator value() { return operator; }
			public LineOperatorNode(LineOperator operator, StringPart stringPart) {
				super(stringPart);
				this.operator = operator;
			}
			@Override public boolean isExpression() { return false; }
		}

		private static class FunctionNode extends Node {
			final ExpFunction function;
			@Override ExpFunction value() { return function; }
			public FunctionNode(ExpFunction function, StringPart stringPart) {
				super(stringPart);
				this.function = function;
			}
			@Override public boolean isExpression() { return true; }
		}

		private static class MultiCompositionNode extends Node {
			CommaSeparatedComposition compositions;
			public MultiCompositionNode(CommaSeparatedComposition compositions, StringPart stringPart) {
				super(stringPart);
				this.compositions = compositions;
			}
			@Override CommaSeparatedComposition value() { return compositions; }
			@Override public boolean isExpression() { return !compositions.compositions().isEmpty()
															 && compositions.compositions().get(0) instanceof Expression; } // vague
		}

	}

}
