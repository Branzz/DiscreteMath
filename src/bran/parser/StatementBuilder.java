package bran.parser;

import bran.exceptions.ParseException;
import bran.logic.statements.*;
import bran.logic.statements.operators.LineOperator;
import bran.logic.statements.operators.LogicalOperator;

import java.util.AbstractCollection;
import java.util.Iterator;

public class StatementBuilder {

	private final StatementChain statementChain;
	// Alternates Statements and Operators
	// ex.: VariableStatement Operator OperationStatement Operator Operator.NOT VariableStatement

	public StatementBuilder() {
		statementChain = new StatementChain();
	}

	public Statement build() {
		if (statementChain.isEmpty())
			return Statement.empty(); // TODO OR throw an error claiming you need to write something
		statementChain.collectLineOperators();
		statementChain.collectOperators();
		assert(statementChain.size() == 1);
		return statementChain.peek();
	}

	public void add(Statement statement) {
		statementChain.addNode(new StatementChain.StatementNode(statement));
	}

	public void add(LogicalOperator operator) {
		statementChain.addNode(new StatementChain.OperatorNode(operator));
	}

	public void add(LineOperator lineOperator) {
		statementChain.addNode(new StatementChain.LineOperatorNode(lineOperator));
	}

	private static class StatementChain extends AbstractCollection<Object> {

		private Node head;
		private Node tail;
		private int size;

		public StatementChain() {
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

		void collectLineOperators() {
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

		Statement lineStream(Node x, Node start) {
			if (x instanceof LineOperatorNode lONnext){
				return new LineStatement(lineStream(x.next, start), lONnext.operator);
			} else if (x instanceof StatementNode nextStatement) { // base case
				start.next = x.next;
				return nextStatement.statement;
			}
			throw new ParseException("statement parsing, unknown (this shouldn't've happened)");
		}

		void collectOperators() {
			for (int order = LogicalOperator.MAX_ORDER; size != 1 && order >= LogicalOperator.MIN_ORDER; order--) {
				Node x = head;
				while (x.next != null && x.next.next != null) {
					if (x == head) {
						if (x.next instanceof OperatorNode op && op.operator.getOrder() == order) {
							//	x -> x.next	-> x.next.next -> x.n.n.n
							//	S -> O	    -> S	-> ?/null
							Node insert = new StatementNode(new OperationStatement(
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
						Node insert = new StatementNode(new OperationStatement(
								(Statement) x.next.value(), (LogicalOperator) x.next.next.value(), (Statement) x.next.next.next.value()));
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

		public Statement peek() {
			if (head.value() instanceof Statement st)
				return st;
			throw new ParseException("statement parsing, unknown (this shouldn't've happened)");
		}

		private static abstract class Node {
			Node next = null;
			abstract Object value();
			abstract Node append(Object next) throws ParseException;
			static Node of(Object o) {
				if (o instanceof Statement st)
					return new StatementNode(st);
				else if (o instanceof LogicalOperator op)
					return new OperatorNode(op);
				else if (o instanceof LineOperator lO)
					return new LineOperatorNode(lO);
				throw new ParseException("statement parsing, unknown (this shouldn't've happened)");
			}
		}

		private static class StatementNode extends Node {
			final Statement statement;
			Object value() { return statement; }
			public StatementNode(final Statement statement) {
				this.statement = statement;
			}
			Node append(Object next) {
				return this.next = (OperatorNode) next;
				// throw new ParseException("statement parsing, unknown (this shouldn't've happened)");
			}
		}

		private static class OperatorNode extends Node {
			final LogicalOperator operator;
			Object value() { return operator; }
			public OperatorNode(final LogicalOperator operator) {
				this.operator = operator;
			}
			Node append(Object next) {
				return this.next = (StatementNode) next;
			}
		}

		private static class LineOperatorNode extends StatementNode {
			final LineOperator operator;
			Object value() { return operator; }
			public LineOperatorNode(final LineOperator operator) {
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
