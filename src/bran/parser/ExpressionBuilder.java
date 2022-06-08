package bran.parser;

import bran.exceptions.IllegalArgumentAmountException;
import bran.exceptions.ParseException;
import bran.tree.compositions.expressions.Expression;
import bran.tree.compositions.expressions.functions.ExpFunction;
import bran.tree.compositions.expressions.functions.FunctionExpression;
import bran.tree.compositions.expressions.operators.Operator;
import bran.tree.compositions.expressions.operators.OperatorExpression;

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.List;
public class ExpressionBuilder {

	private final ExpressionChain expressionChain;
	// Alternates Statements and Operators
	// ex.: VariableStatement Operator OperationStatement Operator Operator.NOT VariableStatement

	public ExpressionBuilder() {
		expressionChain = new ExpressionChain();
	}

	public Expression build() throws IllegalArgumentAmountException {
		if (expressionChain.isEmpty())
			return Expression.empty(); // TODO OR throw an error claiming you need to write something
		expressionChain.collectLineOperators();
		expressionChain.collectOperators();
		assert(expressionChain.size() == 1);
		return expressionChain.peek();
	}

	public void add(Object obj) {
		if (obj instanceof Expression statement)
			add(statement);
		else if (obj instanceof Operator operator)
			add(operator);
		else if (obj instanceof ExpFunction lineOperator)
			add(lineOperator);
		else if (obj instanceof CommaSeparatedExpression expressions)
			add(expressions);
	}

	public void add(Expression statement) {
		expressionChain.addNode(new ExpressionChain.ExpressionNode(statement));
	}

	public void add(Operator operator) {
		expressionChain.addNode(new ExpressionChain.OperatorNode(operator));
	}

	public void add(ExpFunction lineOperator) {
		expressionChain.addNode(new ExpressionChain.LineOperatorNode(lineOperator));
	}

	public void add(List<Expression> expressions) {
		expressionChain.addNode(new ExpressionChain.MultiExpressionNode(expressions));
	}

	public void add(CommaSeparatedExpression expressions) {
		if (expressions.isSingleton())
			add(expressions.getAsSingleton());
		else
			add(expressions.getFull());
	}

	private static class ExpressionChain extends AbstractCollection<ExpressionChain.Node> {

		final static String unknownExcMessage = "unknown exception (this shouldn't've happened)";

		private Node head;
		private Node tail;
		private int size;

		public ExpressionChain() {
			head = tail = null;
			size = 0;
		}

		@Override
		public Iterator<Node> iterator() {
			return new Iterator<>() {
				private Node x = head;
				@Override public boolean hasNext() { return x != null; }
				@Override public Node next() { return x = x.next; }
			};
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
				head = tail = next;
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
						Node insert = new ExpressionNode(lineStream(loN, x));
						insert.next = x.next;
						x = head = insert;
					} else
						x = x.next;
				} else {
					if (x.next instanceof LineOperatorNode loN) {
						//	x -> x.next -> x.next.next	-> x.next.next.next
						//	? -> LO		-> Statement	-> ?/null
						Node insert = new ExpressionNode(lineStream(loN, x.next));
						insert.next = x.next.next;
						x.next = insert;
					} else
						x = x.next;
				}
			}
		}

		Expression lineStream(LineOperatorNode x, Node start) throws IllegalArgumentAmountException {
			return new FunctionExpression(x.function, lineStream0(x.next, start));
		}

		Expression[] lineStream0(Node x, Node start) throws IllegalArgumentAmountException {
			if (x instanceof LineOperatorNode lONnext) {
				return new Expression[] { lineStream(lONnext, start) };
			} else if (x instanceof ExpressionNode nextStatement) { // base case
				start.next = x.next;
				return new Expression[] { nextStatement.expression } ;
			} else if (x instanceof MultiExpressionNode nextStatement) {
				start.next = x.next;
				return nextStatement.expressions.toArray(Expression[]::new);
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
			for (int order = 10; size != 1 && order >= 1; order--) {
				Node x = head;
				while (x.next != null && x.next.next != null) {
					if (x == head) { // x can be the head after the 1st iteration
						if (x.next instanceof OperatorNode op && op.operator.precedence() == order) {
							//	x -> x.next	-> x.next.next -> x.n.n.n
							//	S -> O	    -> S	-> ?/null
							Node insert = new ExpressionNode(new OperatorExpression(
									(Expression) x.value(), (Operator) x.next.value(), (Expression) x.next.next.value()));
							insert.next = x.next.next.next;
							x = head = insert;
							size -= 2;
						} else
							x = x.next;
					}
					else if (x.next.next.next != null && x.next.next instanceof OperatorNode op && op.operator.precedence() == order) {
						//	x -> x.next -> x.next.next	-> x.next.next.next -> x.n.n.n.n
						//	? -> S		-> O			-> S	-> ?/null
						Node insert = new ExpressionNode(new OperatorExpression(
								(Expression) x.next.value(), (Operator) x.next.next.value(), (Expression) x.next.next.next.value()));
						insert.next = x.next.next.next.next;
						x.next = insert;
						size -= 2;
					} else
						x = x.next;
				}
			}
		}

		private static void throwUnknown() throws ParseException {
			throw new ParseException(unknownExcMessage);
		}

		public Expression peek() {
			if (head.value() instanceof Expression st)
				return st;
			throw new ParseException(unknownExcMessage);
		}

		private static abstract class Node {
			protected Node next = null;
			abstract Object value();
			abstract Node append(Object next) throws ParseException;
			static Node of(Object o) {
				if (o instanceof Expression st)
					return new ExpressionNode(st);
				else if (o instanceof Operator op)
					return new OperatorNode(op);
				else if (o instanceof ExpFunction lO)
					return new LineOperatorNode(lO);
				else if (o instanceof CommaSeparatedExpression cE)
					return new MultiExpressionNode(cE.expressions());
				throw new ParseException(unknownExcMessage);
			}
		}

		private static class ExpressionNode extends Node {
			final Expression expression;
			@Override Object value() { return expression; }
			public ExpressionNode(final Expression expression) {
				this.expression = expression;
			}
			@Override Node append(Object next) {
				return this.next = (OperatorNode) next;
				// throw new ParseException(unknownExcMessage);
			}
		}

		private static class OperatorNode extends Node {
			final Operator operator;
			@Override Object value() { return operator; }
			public OperatorNode(final Operator operator) {
				this.operator = operator;
			}
			@Override Node append(Object next) {
				return this.next = (ExpressionNode) next;
			}
		}

		private static class LineOperatorNode extends ExpressionNode {
			final ExpFunction function;
			@Override Object value() { return function; }
			public LineOperatorNode(final ExpFunction function) {
				super(null);
				this.function = function;
			}
			@Override Node append(Object next) {
				if (next instanceof LineOperatorNode lO)
					return this.next = lO;
				else if (next instanceof ExpressionNode nS)
					return this.next = nS;
				else if (next instanceof MultiExpressionNode mE)
					return this.next = mE;
				throw new ParseException(unknownExcMessage);
			}
		}

		private static class MultiExpressionNode extends Node {
			List<Expression> expressions;
			public MultiExpressionNode(final List<Expression> expressions) {
				this.expressions = expressions;
			}
			@Override Object value() { return expressions; }
			@Override Node append(final Object next) throws ParseException {
				if (next instanceof MultiExpressionNode mE)
					return this.next = mE;
				else if (next instanceof OperatorNode oN)
					return this.next = oN;
				throw new ParseException(unknownExcMessage);
			}
		}

	}
}
