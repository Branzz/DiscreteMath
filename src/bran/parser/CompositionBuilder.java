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
import bran.tree.compositions.expressions.operators.ExpressionOperatorType;
import bran.tree.compositions.expressions.operators.Operator;
import bran.tree.compositions.expressions.operators.OperatorExpression;
import bran.tree.compositions.Composition;
import bran.tree.structure.mapper.ForkOperator;

import java.util.List;

public class CompositionBuilder {

	private final CompositionChain compositionChain;
	// Alternates Statements and Operators
	// ex.: VariableStatement Operator OperationStatement Operator Operator.NOT VariableStatement

	public CompositionBuilder() {
		compositionChain = new CompositionChain();
	}

	public Composition build() throws IllegalArgumentAmountException {
		if (compositionChain.isEmpty())
			return Composition.empty(); // TODO OR throw an error claiming you need to write something
		compositionChain.collect();
		assert(compositionChain.size() == 1);
		return compositionChain.peek();
	}

	public void add(Object obj) {
		if (obj instanceof Composition statement)
			add(statement);
		else if (obj instanceof Operator operator)
			add(operator);
		else if (obj instanceof ExpFunction lineOperator)
			add(lineOperator);
		else if (obj instanceof CommaSeparatedComposition compositions)
			add(compositions);
		else if (obj instanceof LazyTypeVariable lazyTypeVariable)
			add(lazyTypeVariable);
	}

	public void add(Composition statement) {
		compositionChain.addNode(new CompositionChain.CompositionNode(statement));
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

	public void add(LazyTypeVariable lazyTypeVariable) {

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
			collectOperators();
			collectEquivalences();
			collectLineOperators();
			collectLogicalOperators();
		}

		private void inferVariableTypes() {
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
				if (x.next instanceof VariableNode variableNode) {
					if (x.next == tail) {
						variableNode.found(x.isExpression());
						break;
					} else { // "a + b = c & d < e" = "a + ((b = c) & (d < e))"
						int leftPrecedence = -1;
						int rightPrecedence = -1;
						if (x instanceof OperatorNode opLeft)
							leftPrecedence = opLeft.value().getOrder();
						else if (x instanceof EquivalenceTypeNode eqTNode)
							leftPrecedence = eqTNode.value().getOrder();

						if (x.next.next instanceof OperatorNode opRight)
							rightPrecedence = opRight.value().getOrder();
						else if (x.next.next instanceof EquivalenceTypeNode eqTRight)
							rightPrecedence = eqTRight.value().getOrder();

						if (leftPrecedence == -1 && rightPrecedence == -1)
							throw new ParseException("unimplemented operator precedences");
						variableNode.found(leftPrecedence < rightPrecedence ? x.isExpression() : x.next.next.isExpression());
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
							EquivalenceNode insert = new EquivalenceNode(Equivalence.of((Expression) x.value(), eN.equivalenceType, (Expression) x.next.next.value()));
							insert.next = x.next.next.next;
							x = head = insert;
							size -= 2;
						} else
							throw new ParseException("type error");
					} else
						x = x.next;
				} else if (x.next.next instanceof EquivalenceTypeNode eN) {
					if (x.next.isExpression() && x.next.next.next.isExpression()) {
						final EquivalenceNode insert = new EquivalenceNode(Equivalence.of((Expression) x.next.value(), eN.equivalenceType, (Expression) x.next.next.next.value()));
						insert.next = x.next.next.next.next;
						x.next = insert;
						size -= 2;
					} else
						throw new ParseException("type error");
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

		void collectLogicalOperators() {
			for (int order = LogicalOperator.MAX_ORDER; size != 1 && order >= LogicalOperator.MIN_ORDER; order--) {
				Node x = head;
				while (x.next != null && x.next.next != null) {
					if (x == head) {
						if (x.next instanceof OperatorNode op && op.operator.getOrder() == order) {
							//	x -> x.next	-> x.next.next -> x.n.n.n
							//	S -> O	    -> S	-> ?/null
							Node insert = new CompositionNode(new OperationStatement(
									(Statement) x.value(), (LogicalOperator) x.next.value(), (Statement) x.next.next.value()));
							insert.next = x.next.next.next;
							x = head = insert;
							size -= 2;
						} else
							x = x.next;
					}
					else if (x.next.next.next != null && x.next.next instanceof OperatorNode op && op.operator.getOrder() == order) {
						//	x -> x.next -> x.next.next	-> x.next.next.next -> x.n.n.n.n
						//	? -> S		-> O			-> S	-> ?/null
						Node insert = new CompositionNode(new OperationStatement(
								(Statement) x.next.value(), (LogicalOperator) x.next.next.value(), (Statement) x.next.next.next.value()));
						insert.next = x.next.next.next.next;
						x.next = insert;
						size -= 2;
					} else
						x = x.next;
				}
			}
		}

		Composition lineStream(Node x, Node start) throws IllegalArgumentAmountException {
			// return x.isExpression() ? new FunctionExpression(x.operator, lineStream0(x.next, start))
			// 			   :
			if (x instanceof LineOperatorNode lONode){
				final Composition compStreamed = lineStream(x.next, start);
				if (compStreamed instanceof Statement expStreamed)
					return new LineStatement(expStreamed, lONode.operator);
				else
					throw new ParseException("wrong type of argument for");
			} else if (x instanceof FunctionNode fNode) {
				final Composition[] compositions = lineStreamArray(x.next, start);
				final Expression[] expressions = new Expression[compositions.length];
				for (int i = 0; i < compositions.length; i++) {
					if (compositions[i] instanceof Expression exp)
						expressions[i] = exp;
					else
						throw new ParseException("wrong type");
				}
				return new FunctionExpression(fNode.function, expressions);
			} else if (x instanceof CompositionNode cNode) { // base case
				start.next = x.next;
				return cNode.composition;
			}
			throw new ParseException(unknownExcMessage);
		}

		Composition[] lineStreamArray(Node x, Node start) throws IllegalArgumentAmountException {
			if (x instanceof LineOperatorNode lONode) {
				return new Composition[] { lineStream(lONode, start) };
			} else if (x instanceof CompositionNode cNode) { // base case
				start.next = x.next;
				return new Composition[] { cNode.composition } ;
			} else if (x instanceof MultiCompositionNode nextStatement) {
				start.next = x.next;
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
				x = head.next;
			}
			return sB.toString();
		}

		void collectOperators() {
			for (int order = ExpressionOperatorType.MAX_ORDER; size != 1 && order >= ExpressionOperatorType.MIN_ORDER; order--) {
				Node x = head;
				while (x.next != null && x.next.next != null) {
					if (x == head) {
						if (x.next instanceof OperatorNode op && op.operator.getOrder() == order) {
							//	x -> x.next	-> x.next.next -> x.n.n.n
							//	S -> O	    -> S	-> ?/null
							if (x.value() instanceof Expression left && x.next.value() instanceof Operator operator && x.next.next.value() instanceof Expression right) {
								Node insert = new CompositionNode(new OperatorExpression(left, operator, right));
								insert.next = x.next.next.next;
								x = head = insert;
								size -= 2;
							} else
								throw new ParseException("type error");

						} else
							x = x.next;
					}
					else if (x.next.next.next != null && x.next.next instanceof OperatorNode op && op.operator.getOrder() == order) {
						//	x -> x.next -> x.next.next	-> x.next.next.next -> x.n.n.n.n
						//	? -> S		-> O			-> S	-> ?/null
						if (x.next.value() instanceof Expression left && x.next.next.value() instanceof Operator operator && x.next.next.next.value() instanceof Expression right) {
							Node insert = new CompositionNode(new OperatorExpression(left, operator, right));
						insert.next = x.next.next.next.next;
						x.next = insert;
						size -= 2;
						} else
							throw new ParseException("type error");

				} else
						x = x.next;
				}
			}
		}

		public Expression peek() {
			if (head.value() instanceof Expression st)
				return st;
			throw new ParseException(unknownExcMessage);
		}

		public boolean isEmpty() {
			return size == 0;
		}

		private static abstract class Node {
			protected Node next = null;
			abstract Object value();
			Node append(Node next) throws ParseException {
				// if (next instanceof VariableNode vNode)
				// 	vNode.found(this.isExpression());
				return this.next = next;
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
