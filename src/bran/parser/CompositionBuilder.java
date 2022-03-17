package bran.parser;

import bran.exceptions.ParseException;
import bran.tree.compositions.statements.operators.LineOperator;
import bran.tree.compositions.statements.special.equivalences.EquivalenceType;
import bran.tree.compositions.expressions.Expression;
import bran.tree.compositions.expressions.functions.ExpFunction;
import bran.exceptions.IllegalArgumentAmountException;
import bran.tree.compositions.expressions.operators.ExpressionOperatorType;
import bran.tree.compositions.expressions.operators.Operator;
import bran.tree.compositions.expressions.operators.OperatorExpression;
import bran.tree.compositions.Composition;
import bran.tree.structure.mapper.ForkOperator;

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.List;

public class CompositionBuilder {

	private final CompositionChain statementChain;
	// Alternates Statements and Operators
	// ex.: VariableStatement Operator OperationStatement Operator Operator.NOT VariableStatement

	public CompositionBuilder() {
		statementChain = new CompositionChain();
	}

	public Composition build() throws IllegalArgumentAmountException {
		if (statementChain.isEmpty())
			return Composition.empty(); // TODO OR throw an error claiming you need to write something
		statementChain.collectLineOperators();
		statementChain.collectOperators();
		assert(statementChain.size() == 1);
		return statementChain.peek();
	}

	public void add(Object obj) {
		if (obj instanceof Composition statement)
			add(statement);
		else if (obj instanceof Operator operator)
			add(operator);
		else if (obj instanceof ExpFunction lineOperator)
			add(lineOperator);
		else if (obj instanceof CommaSeparatedExpression expressions)
			add(expressions);
	}

	public void add(Composition statement) {
		statementChain.addNode(new CompositionChain.CompositionNode(statement));
	}

	public void add(LineOperator lineOperator) {
		statementChain.addNode(new CompositionChain.LineOperatorNode(lineOperator));
	}

	public void add(ForkOperator forkOperator) {
		statementChain.addNode(new CompositionChain.OperatorNode(forkOperator));
	}

	public void add(List<Expression> expressions) {
		statementChain.addNode(new CompositionChain.MultiExpressionNode(expressions));
	}

	public void add(CommaSeparatedExpression expressions) {
		if (expressions.isSingleton())
			add(expressions.getAsSingleton());
		else
			add(expressions.getFull());
	}

	private static class CompositionChain extends AbstractCollection<Object> {

		private Node head;
		private Node tail;
		private int size;

		public CompositionChain() {
			// LinkedList<Object> x;
			// x.iterator();
			// x.get(0)
			head = null;
			tail = null;
			size = 0;
		}

		@Override
		public Iterator<Object> iterator() {
			return null;
		}

		@Override
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

		public boolean addNode(Node next) throws ParseException {
			if (head == null) {
				head = next;
				tail = head;
			} else {
				tail = tail.append(next);
			}
			size++;
			return true;
		}

		@Override
		public void clear() {
			head = null;
			tail = null;
			size = 0;
		}

		// @Override
		// public boolean remove(Object o) {
		// }

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

		Composition lineStream(LineOperatorNode x, Node start) throws IllegalArgumentAmountException {
			return null;
			// return x.isExpression() ? new FunctionExpression(x.function, lineStream0(x.next, start))
			// 			   :
		}

		Composition[] lineStream0(Node x, Node start) throws IllegalArgumentAmountException {
			if (x instanceof LineOperatorNode lONnext) {
				return new Composition[] { lineStream(lONnext, start) };
			} else if (x instanceof CompositionNode nextStatement) { // base case
				start.next = x.next;
				return new Composition[] { nextStatement.composition } ;
			} else if (x instanceof MultiExpressionNode nextStatement) {
				start.next = x.next;
				return nextStatement.expressions.toArray(Expression[]::new);
			}
			throw unknownParsing();
		}

		void collectOperators() { // TODO
			for (int order = ExpressionOperatorType.MAX_ORDER; size != 1 && order >= ExpressionOperatorType.MIN_ORDER; order--) {
				Node x = head;
				while (x.next != null && x.next.next != null) {
					if (x == head) {
						if (x.next instanceof OperatorNode op && op.operator.getOrder() == order) {
							//	x -> x.next	-> x.next.next -> x.n.n.n
							//	S -> O	    -> S	-> ?/null
							Node insert = new CompositionNode(new OperatorExpression(
									(Expression) x.value(), (Operator) x.next.value(), (Expression) x.next.next.value()));
							insert.next = x.next.next.next;
							x = head = insert;
							size -= 2;
						} else
							x = x.next;
					}
					else if (x.next.next.next != null && x.next.next instanceof OperatorNode op && op.operator.getOrder() == order) {
						//	x -> x.next -> x.next.next	-> x.next.next.next -> x.n.n.n.n
						//	? -> S		-> O			-> S	-> ?/null
						Node insert = new CompositionNode(new OperatorExpression(
								(Expression) x.next.value(), (Operator) x.next.next.value(), (Expression) x.next.next.next.value()));
						insert.next = x.next.next.next.next;
						x.next = insert;
						size -= 2;
					} else
						x = x.next;
				}
			}
		}

		public Expression peek() {
			if (head.value() instanceof Expression st)
				return st;
			throw unknownParsing();
		}

		private static abstract class Node {
			protected Node next = null;
			abstract Object value();
			abstract Node append(Object next) throws ParseException;
			static Node of(Object o) {
				if (o instanceof Composition c)
					return new CompositionNode(c);
				else if (o instanceof Operator op)
					return new OperatorNode(op);
				else if (o instanceof LineOperator lO)
					return new LineOperatorNode(lO);
				else if (o instanceof ExpFunction f)
					return new FunctionNode(f);
				else if (o instanceof CommaSeparatedExpression cE)
					return new MultiExpressionNode(cE.expressions());
				throw unknownParsing();
			}
			public abstract boolean isExpression();
		}

		private static class EquivalenceNode extends Node {
			final EquivalenceType equivalenceType;
			public EquivalenceNode(final EquivalenceType equivalenceType) {
				this.equivalenceType = equivalenceType;
			}
			@Override
			Object value() { return null; }
			@Override Node append(final Object next) throws ParseException {
				return null;
			}
			@Override public boolean isExpression() { return false; }
		}

		private static class CompositionNode extends Node {
			final Composition composition;
			final boolean isExpression;
			@Override
			Object value() { return composition; }
			public CompositionNode(final Composition composition) {
				this.composition = composition;
				this.isExpression = composition instanceof Expression;
			}
			@Override Node append(Object next) {
				return this.next = (OperatorNode) next;
				// throw new ParseException("statement parsing, unknown (this shouldn't've happened)");
			}
			@Override public boolean isExpression() { return isExpression; }
		}

		private static class OperatorNode extends Node {
			final ForkOperator operator;
			final boolean isExpression;
			@Override
			Object value() { return operator; }
			public OperatorNode(final ForkOperator operator) {
				this.operator = operator;
				this.isExpression = operator instanceof Operator; // !ForkOperator
			}
			@Override Node append(Object next) {
				return this.next = (CompositionNode) next;
			}
			@Override public boolean isExpression() { return isExpression; }
		}

		private static class LineOperatorNode extends Node {
			final LineOperator operator;
			Object value() { return operator; }
			public LineOperatorNode(final LineOperator operator) {
				this.operator = operator;
			}
			@Override Node append(Object next) {
				if (next instanceof LineOperatorNode lO)
					return this.next = lO;
				else if (next instanceof CompositionNode nS)
					return this.next = nS;
				throw unknownParsing();
			}
			@Override public boolean isExpression() { return false; }
		}

		private static class FunctionNode extends Node {
			final ExpFunction function;
			@Override
			Object value() { return function; }
			public FunctionNode(final ExpFunction function) {
				this.function = function;
			}
			@Override
			Node append(Object next) {
				if (next instanceof LineOperatorNode lO)
					return this.next = lO;
				else if (next instanceof CompositionNode nS)
					return this.next = nS;
				throw unknownParsing();
			}
			@Override public boolean isExpression() { return true; }
		}

		private static class MultiExpressionNode extends Node {
			List<Expression> expressions;
			public MultiExpressionNode(final List<Expression> expressions) {
				this.expressions = expressions;
			}
			@Override
			Object value() { return expressions; }
			@Override Node append(final Object next) throws ParseException {
				return this.next = (MultiExpressionNode) next;
			}
			@Override public boolean isExpression() { return true; }
		}

	}

	private static ParseException unknownParsing() {
		// coder's fault
		throw new ParseException("statement parsing, unknown (this shouldn't've happened)");
	}

}
