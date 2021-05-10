package bran.graphs;

import java.util.ArrayList;
import java.util.HashMap;

import bran.exceptions.IllegalMatrixSizeException;
import bran.matrices.Matrix;

public class DirectedGraph extends Graph {

	protected HashMap<Vertex, ArrayList<DirectedEdge>> vertexEdgeTable;

	public DirectedGraph() {
		Vertex.resetNames();
		Edge.resetNames();
		vertexEdgeTable = new HashMap<Vertex, ArrayList<DirectedEdge>>();
	}

	public DirectedGraph(HashMap<Vertex, ArrayList<DirectedEdge>> vertexEdgeTable) {
		this.vertexEdgeTable = vertexEdgeTable;
		Vertex.resetNames();
		Edge.resetNames();
	}


	public DirectedGraph(Matrix m) {
		Vertex.resetNames();
		Edge.resetNames();
		if (m.getRows() != m.getColumns())
			throw new IllegalMatrixSizeException();
		int size = m.getRows();
		Vertex[] vertices = new Vertex[size];
		for (int i = 0; i < size; i++) {
			vertices[i] = new Vertex();
//			edges[i] = new Edge();
		}
		for(int r = 0; r < size; r++) {
			for (int i = 0; i < m.get(r, r); i++)
				vertices[r].addEdge(new Edge(vertices[r])); // loops
			for(int c = r + 1; c < size; c++)
				for (int i = 0; i < m.get(r, c); i++) {
					Edge edge = new Edge(vertices[r], vertices[c]);
					vertices[r].addEdge(edge);
					vertices[c].addEdge(edge);
				}
		}
		vertexEdgeTable = new HashMap<Vertex, ArrayList<DirectedEdge>>();
		// for (Vertex v : vertices)  // TODO temp comment
		// 	vertexEdgeTable.put(v, v.getEdges());  // TODO temp comment
	}

}
