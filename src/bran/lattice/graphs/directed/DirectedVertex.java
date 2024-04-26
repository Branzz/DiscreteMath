package bran.lattice.graphs.directed;

import java.awt.Color;
import java.awt.Font;

import bran.lattice.graphs.Vertex;
import bran.tree.compositions.sets.regular.FiniteSet;

public class DirectedVertex {
	static int nameNum = 0;
	private String name;

	private FiniteSet<DirectedEdge> edges;

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

	public DirectedVertex() {
		setName();
		edges = new FiniteSet<DirectedEdge>();
	}

	public DirectedVertex(String name) {
		this.name = name;
		if (name.equals("v" + nameNum))
			nameNum++;
		edges = new FiniteSet<DirectedEdge>();
	}

	public DirectedVertex(FiniteSet<DirectedEdge> edges) {
		setName();
		this.edges = edges;
	}

	public DirectedVertex(DirectedEdge... edges) {
		setName();
		for (DirectedEdge e : edges)
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

	public FiniteSet<DirectedEdge> getEdges() {
		return edges;
	}

	public void setEdges(FiniteSet<DirectedEdge> edges) {
		this.edges = edges;
	}

	public void addEdge(DirectedEdge edge) {
		edges.add(edge);
	}
	
	public String toString() {
		return name;
	}

}
