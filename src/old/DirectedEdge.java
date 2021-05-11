// package old;
//
// import java.util.ArrayList;
//
// public class DirectedEdge extends Edge {
//
// 	private ArrayList<Vertex> verticesTo;
//
// 	public DirectedEdge() {
// 		super();
// 		verticesTo = new ArrayList<Vertex>(2);
// 	}
//
// 	public DirectedEdge(ArrayList<Vertex> vertices) {
// 		super(vertices);
// 		verticesTo = new ArrayList<Vertex>(2);
// 	}
//
// 	public DirectedEdge(Vertex v0) {
// 		super(v0);
// 		verticesTo = new ArrayList<Vertex>(2);
// 		verticesTo.add(v0);
// 	}
//
// 	public DirectedEdge(Vertex v0, Vertex v1) {
// 		super(v0);
// 		verticesTo = new ArrayList<Vertex>(2);
// 		verticesTo.add(v1);
// 	}
//
// 	public ArrayList<Vertex> getToVertices() {
// 		return verticesTo;
// 	}
//
// 	public void setToVertices(ArrayList<Vertex> vertices) {
// 		this.verticesTo = vertices;
// 	}
//
// 	public void addToVertex(Vertex vertex) {
// 		verticesTo.add(vertex);
// 	}
//
// 	public void addAllToVertex(ArrayList<Vertex> vertex) {
// 		verticesTo.addAll(vertex);
// 	}
//
// 	public synchronized boolean equals(Object other) { // not deep
// 		return (other instanceof DirectedEdge
// 				&& (verticesTo.size() == ((DirectedEdge) other).getVertices().size()
// 				&& (verticesTo.size() == 0
// 				|| (verticesTo.size() == 1
// 				&& verticesTo.get(0) == ((DirectedEdge) other).getVertices().get(0))
// 				|| verticesTo.size() == 2 && (
// 						verticesTo.get(0) == ((DirectedEdge) other).getVertices().get(0) && verticesTo.get(1) == ((DirectedEdge) other).getVertices().get(1)
// 						|| verticesTo.get(0) == ((DirectedEdge) other).getVertices().get(1) && verticesTo.get(1) == ((DirectedEdge) other).getVertices().get(0))
// 						))
// 				&& (getVertices().size() == ((DirectedEdge) other).getVertices().size()
// 				&& (getVertices().size() == 0
// 				|| (getVertices().size() == 1
// 				&& getVertices().get(0) == ((DirectedEdge) other).getVertices().get(0))
// 				|| getVertices().size() == 2 && (
// 						getVertices().get(0) == ((DirectedEdge) other).getVertices().get(0) && getVertices().get(1) == ((DirectedEdge) other).getVertices().get(1)
// 						|| getVertices().get(0) == ((DirectedEdge) other).getVertices().get(1) && getVertices().get(1) == ((DirectedEdge) other).getVertices().get(0))
// 						)));
// 		// for each O for each child if other contains child
// 	}
//
// }
