package bran.graphs;

public class DirectedEdge extends Edge {

	public DirectedEdge() {
		super();
	}

	public DirectedEdge(Vertex v0) {
		super(v0);
	}

	public DirectedEdge(Vertex v0, Vertex v1) {
		super(v0, v1);
	}

	public synchronized boolean equals(Object other) { // not deep
		return this == other || (other instanceof DirectedEdge && getFirst() == ((DirectedEdge) other).getFirst() && getSecond() == ((DirectedEdge) other).getSecond());
	}

}
