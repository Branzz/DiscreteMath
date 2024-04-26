package bran.lattice.graphs;

import java.util.ArrayList;
import java.util.HashMap;

public class SimpleGraph extends Graph {

	public SimpleGraph(int n) {
		super();
		vertexEdgeTable = new HashMap<>();
		Vertex[] vertices = new Vertex[n];
		for (int i = 0; i < n; i++)
			vertices[i] = new Vertex();

		Edge[] edges = new Edge[n * (n - 1) / 2];
		for (int i = 0; i < edges.length; i++)
			edges[i] = new Edge();

		int currentVertexMax = 0;
		for (int i = 0; i < n; i++) {
			ArrayList<Edge> currentEdges = new ArrayList<>();
			for (int j = 0; j < i; j++)
				currentEdges.add(vertexEdgeTable.get(vertices[j]).get(i - 1));

			for (int j = i; j < n - 1; j++)
				currentEdges.add(edges[currentVertexMax++]);

			vertices[i].setEdges(currentEdges);
			for (Edge e : currentEdges)
				e.addVertex(vertices[i]);

			vertexEdgeTable.put(vertices[i], currentEdges);
		}
//		System.out.println();
	}

	public SimpleGraph(int m, int n) {
		super();
		vertexEdgeTable = new HashMap<>();

		Edge[] edges = new Edge[m * n];
		for (int i = 0; i < edges.length; i++)
			edges[i] = new Edge();

		for (int i = 0; i < m; i++) {
			ArrayList<Edge> currentEdges = new ArrayList<>();
			Vertex v = new Vertex();
			for (int j = 0; j < n * m; j += m) {
				v.addEdge(edges[i + j]);
				edges[i + j].addVertex(v);
				currentEdges.add(edges[i + j]);
			}
			vertexEdgeTable.put(v, currentEdges);
		}

		for (int i = 0; i < n * m; i += m) {
			ArrayList<Edge> currentEdges = new ArrayList<>();
			Vertex v = new Vertex();
			for (int j = 0; j < m; j++) {
				v.addEdge(edges[j + i]);
				edges[j + i].addVertex(v);
				currentEdges.add(edges[j + i]);
			}
			vertexEdgeTable.put(v, currentEdges);
		}

//		vertexEdgeTable.put(vertices[n], edges);
//		System.out.println();

	}

	public SimpleGraph(int n, boolean s) {
		
	}

	public SimpleGraph(int m, int n, boolean s) {
		
	}

}
