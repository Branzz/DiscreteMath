package bran.parser;

import bran.exceptions.ParseException;
import bran.mathexprs.treeparts.Expression;
import bran.mathexprs.treeparts.functions.Function;
import bran.mathexprs.treeparts.functions.FunctionExpression;
import bran.mathexprs.treeparts.functions.IllegalArgumentAmountException;
import bran.mathexprs.treeparts.operators.Operator;
import bran.mathexprs.treeparts.operators.OperatorExpression;
import bran.mathexprs.treeparts.operators.OperatorType;

import java.util.AbstractCollection;
import java.util.Iterator;
public class ExpressionBuilder {

	private final ExpressionChain statementChain;
	// Alternates Statements and Operators
	// ex.: VariableStatement Operator OperationStatement Operator Operator.NOT VariableStatement

	public ExpressionBuilder() {
		statementChain = new ExpressionChain();
	}

	public Expression build() throws IllegalArgumentAmountException {
		if (statementChain.isEmpty())
			return Expression.empty(); // TODO OR throw an error claiming you need to write something
		statementChain.collectLineOperators();
		statementChain.collectOperators();
		assert(statementChain.size() == 1);
		return statementChain.peek();
	}

	public void add(Expression statement) {
		statementChain.addNode(new ExpressionChain.StatementNode(statement));
	}

	public void add(Operator operator) {
		statementChain.addNode(new ExpressionChain.OperatorNode(operator));
	}

	public void add(Function lineOperator) {
		statementChain.addNode(new ExpressionChain.LineOperatorNode(lineOperator));
	}

	private static class ExpressionChain extends AbstractCollection<Object> {

		private Node head;
		private Node tail;
		private int size;

		public ExpressionChain() {
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
					if (x instanceof LineOperatorNode) {
						Node insert = new StatementNode(lineStream(x, x));
						insert.next = x.next;
						x = head = insert;
					} else
						x = x.next;
				} else {
					if (x.next instanceof LineOperatorNode) {
						//	x -> x.next -> x.next.next	-> x.next.next.next
						//	? -> LO		-> Statement	-> ?/null
						Node insert = new StatementNode(lineStream(x.next, x.next));
						insert.next = x.next.next;
						x.next = insert;
					} else
						x = x.next;
				}
			}
		}

		Expression lineStream(Node x, Node start) throws IllegalArgumentAmountException {
			if (x instanceof LineOperatorNode lONnext){
				return new FunctionExpression(lONnext.operator, lineStream(x.next, start));
			} else if (x instanceof StatementNode nextStatement) { // base case
				start.next = x.next;
				return nextStatement.statement;
			}
			throw new ParseException("statement parsing, unknown (this shouldn't've happened)");
		}

		void collectOperators() { // TODO
			for (int order = OperatorType.MAX_ORDER; size != 1 && order >= OperatorType.MIN_ORDER; order--) {
				Node x = head;
				while (x.next != null && x.next.next != null) {
					if (x == head) {
						if (x.next instanceof OperatorNode op && op.operator.getOrder() == order) {
							//	x -> x.next	-> x.next.next -> x.n.n.n
							//	S -> O	    -> S	-> ?/null
							Node insert = new StatementNode(new OperatorExpression(
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
						Node insert = new StatementNode(new OperatorExpression(
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
			throw new ParseException("statement parsing, unknown (this shouldn't've happened)");
		}

		public Expression peek() {
			if (head.value() instanceof Expression st)
				return st;
			throw new ParseException("statement parsing, unknown (this shouldn't've happened)");
		}

		private static abstract class Node {
			Node next = null;
			abstract Object value();
			abstract Node append(Object next) throws ParseException;
			static Node of(Object o) {
				if (o instanceof Expression st)
					return new StatementNode(st);
				else if (o instanceof Operator op)
					return new OperatorNode(op);
				else if (o instanceof Function lO)
					return new LineOperatorNode(lO);
				throw new ParseException("statement parsing, unknown (this shouldn't've happened)");
			}
		}

		private static class StatementNode extends Node {
			final Expression statement;
			Object value() { return statement; }
			public StatementNode(final Expression statement) {
				this.statement = statement;
			}
			Node append(Object next) {
				return this.next = (OperatorNode) next;
				// throw new ParseException("statement parsing, unknown (this shouldn't've happened)");
			}
		}

		private static class OperatorNode extends Node {
			final Operator operator;
			Object value() { return operator; }
			public OperatorNode(final Operator operator) {
				this.operator = operator;
			}
			Node append(Object next) {
				return this.next = (StatementNode) next;
			}
		}

		private static class LineOperatorNode extends StatementNode {
			final Function operator;
			Object value() { return operator; }
			public LineOperatorNode(final Function operator) {
				super(null);
				this.operator = operator;
			}
			Node append(Object next) {
				if (next instanceof LineOperatorNode lO)
					return this.next = lO;
				else if (next instanceof StatementNode nS)
					return this.next = nS;
				throw new ParseException("statement parsing, unknown (this shouldn't've happened)");
			}
		}

	}
}
