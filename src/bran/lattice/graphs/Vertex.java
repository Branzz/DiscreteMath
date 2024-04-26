package bran.lattice.graphs;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

public class Vertex {

	static int nameNum = 0;
	private String name;

	private ArrayList<Edge> edges;

	private GraphData data;

	private class GraphData {

		private Color color;
		private Font font;
		private int fontSize;
		private int xPos;
		private int yPos;

		private GraphData(Color color, Font font, int fontSize) {
			this.color = color;
			this.font = font;
			this.fontSize = fontSize;
		}

		public int getXPos() {
			return xPos;
		}

		public void setXPos(int xPos) {
			this.xPos = xPos;
		}

		public int getYPos() {
			return yPos;
		}

		public void setYPos(int yPos) {
			this.yPos = yPos;
		}

	}

	public Vertex() {
		setName();
		edges = new ArrayList<Edge>();
	}

	public Vertex(String name) {
		this.name = name;
		if (name.equals("v" + nameNum))
			nameNum++;
		edges = new ArrayList<Edge>();
	}
	
	public Vertex(ArrayList<Edge> edges) {
		setName();
		this.edges = edges;
	}

	public Vertex(Edge... edges) {
		setName();
		for (Edge e : edges)
			this.edges.add(e);
	}

	private void setName() {
		name = "v" + nameNum++;		
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

	public boolean isAdjacentTo(Vertex other) {
		return false;
	}

	public ArrayList<Edge> getEdges() {
		return edges;
	}

	public void setEdges(ArrayList<Edge> edges) {
		this.edges = edges;
	}

	public void addEdge(Edge edge) {
		edges.add(edge);
	}

	public String toString() {
		return name;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		final Vertex vertex = (Vertex) o;

		return name.equals(vertex.name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

}
