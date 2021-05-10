package bran.graphs;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

public class Edge {

	static int nameNum = 0;
	private String name;
	private Vertex first;
	private Vertex second;

	private GraphData data;

	private class GraphData {

		private Color color;
		private Font font;
		private int fontSize;

		private GraphData(Color color, Font font, int fontSize) {
			this.color = color;
			this.font = font;
			this.fontSize = fontSize;
		}

	}

	public Edge() {
		setName();
	}

	public Edge(Vertex v0) {
		setName();
		first = v0;
	}

	public Edge(Vertex v0, Vertex v1) {
		setName();
		first = v0;
		second = v1;
	}

	private void setName() {
		name = "e" + nameNum++;		
	}
	
	public static void resetNames() {
		nameNum = 0;
	}

	public void setGraphData(Color color, Font font, int fontSize) {
		data = new GraphData(color, font, fontSize);
	}

	public GraphData getGraphData() {
		return data;
	}

	public boolean isIncidentTo(Edge other) {
		return (first != null && first.getEdges().contains(other)) || (second != null && second.getEdges().contains(other));
	}

	public Vertex getFirst() {
		return first;
	}

	public Vertex getSecond() {
		return second;
	}

	public Vertex getOther(Vertex v) {
		return v.equals(first) ? second : first;
	}

	public void setFirst(Vertex first) {
		this.first = first;
	}

	public void setSecond(Vertex second) {
		this.second = second;
	}

	public void addVertex(Vertex vertex) {
		if (first == null)
			first = vertex;
		else if (second == null)
			second = vertex;
		else
			return;
	}

	public ArrayList<Vertex> getVertices() {
		ArrayList<Vertex> vertices = new ArrayList<Vertex>(2);
		vertices.add(first);
		vertices.add(second);
		return vertices;
	}

	public synchronized boolean equals(Object other) { // not deep
		return (this == other || (other instanceof Edge
				&& ((first == ((Edge) other).getFirst() && second == ((Edge) other).getSecond())
				|| (second == ((Edge) other).getFirst()) && first == ((Edge) other).getSecond())));
				
		// for each O for each child if other contains child
	}

	public String toString() {
		return name;
	}

}
