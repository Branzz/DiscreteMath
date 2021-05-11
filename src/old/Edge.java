// package old;
//
// import java.util.ArrayList;
//
// public class Edge {
//
// 	static int nameNum = 0;
// 	private String name;
// 	private ArrayList<Vertex> verticesTo;
//
// 	public Edge() {
// 		setName();
// 		verticesTo = new ArrayList<Vertex>(2);
// 	}
//
// 	public Edge(ArrayList<Vertex> vertices) {
// 		setName();
// 		this.verticesTo = vertices;
// 	}
//
// 	public Edge(Vertex v0) {
// 		setName();
// 		verticesTo = new ArrayList<Vertex>(2);
// 		verticesTo.add(v0);
// 	}
//
// 	public Edge(Vertex v0, Vertex v1) {
// 		setName();
// 		verticesTo = new ArrayList<Vertex>(2);
// 		verticesTo.add(v0);
// 		verticesTo.add(v1);
// 	}
//
// 	private void setName() {
// 		name = "e" + nameNum++;
// 	}
//
// 	public boolean isIncidentTo(Edge other) {
// 		return false;
// 	}
//
// 	public static void resetNames() {
// 		nameNum = 0;
// 	}
//
// 	public ArrayList<Vertex> getVertices() {
// 		return verticesTo;
// 	}
//
// 	public void setVertices(ArrayList<Vertex> vertices) {
// 		this.verticesTo = vertices;
// 	}
//
// 	public void addVertex(Vertex vertex) {
// 		verticesTo.add(vertex);
// 	}
//
// 	public void addAllVertex(ArrayList<Vertex> vertex) {
// 		verticesTo.addAll(vertex);
// 	}
//
// 	public synchronized boolean equals(Object other) { // not deep
// 		return (other instanceof Edge
// 				&& verticesTo.size() == ((Edge) other).getVertices().size()
// 				&& (verticesTo.size() == 0
// 				|| (verticesTo.size() == 1
// 				&& verticesTo.get(0) == ((Edge) other).getVertices().get(0))
// 				|| verticesTo.size() == 2 && (
// 						verticesTo.get(0) == ((Edge) other).getVertices().get(0) && verticesTo.get(1) == ((Edge) other).getVertices().get(1)
// 						|| verticesTo.get(0) == ((Edge) other).getVertices().get(1) && verticesTo.get(1) == ((Edge) other).getVertices().get(0))
// 						));
// 		// for each O for each child if other contains child
// 	}
//
// 	public String toString() {
// 		return name;
// 	}
//
// }
