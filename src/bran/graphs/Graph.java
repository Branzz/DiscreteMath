package bran.graphs;

import java.util.*;
import java.util.stream.Collectors;

import bran.exceptions.IllegalMatrixSizeException;
import bran.matrices.Matrix;

public class Graph {

	protected Map<Vertex, List<Edge>> vertexEdgeTable;
	protected long edgesSize;

	public Graph() {
		Vertex.resetNames();
		Edge.resetNames();
		vertexEdgeTable = new HashMap<>();
		edgesSize = 0L;
	}

	public Graph(Map<Vertex, List<Edge>> vertexEdgeTable) {
		this.vertexEdgeTable = vertexEdgeTable;
		Vertex.resetNames();
		Edge.resetNames();
		edgesSize = vertexEdgeTable.values().stream().map(List::size).reduce(0, Integer::sum);
	}

	public Graph(Matrix m) {
		Vertex.resetNames();
		Edge.resetNames();
		if (!m.isSymmetric() || m.getRows() != m.getColumns())
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
		vertexEdgeTable = new HashMap<>();
		for (Vertex v : vertices)
			vertexEdgeTable.put(v, v.getEdges());
		long sum = 0L;
		for (int r = 0; r < size; r++)
			for (int c = 0; c < size; c++)
				sum += m.get(r, c);
		edgesSize = sum;
	}

	public ArrayList<Vertex> getVertices() {
		return new ArrayList<>(vertexEdgeTable.keySet());
	}

	public Map<Vertex, List<Edge>> getVertexEdgeTable() {
		return vertexEdgeTable;
	}

	public boolean isConnected() {
		//TODO
		return false;
	}

	public boolean isTree() {
		//TODO //edges = vertices - 1. it does not contain any loops. connected?
		// trivial tree possible
		return false;
	}

	public void h() {
		/*
		 * add support for:
		 * if it has at least one /
		 * how many it has of /
		 * list one / all of the:
		 * paths/trails/circuits/simple circuits/walk/ etc... /trivial ___ Hamiltonian / Euler circuit
		 */
	}

	public String toString() {
		StringBuilder table = new StringBuilder();
		for (Vertex v : vertexEdgeTable.keySet()) {
			table.append(v.toString()).append(" | { ");
			for (Edge e : v.getEdges())
				table.append(e.toString()).append(",");
			table.deleteCharAt(table.length() - 1);
			table.append(" }\n");
		}
		return table.toString();
	}

	public boolean isHamiltonianPath() {
		Stack<Vertex> path = new Stack<>();
		path.add(vertexEdgeTable.keySet().stream().findFirst().orElse(null));
		return searchHamiltonian(path);
	}

	private boolean searchHamiltonian(Stack<Vertex> path) {
		printSequence(path);
		if (path.size() == vertexEdgeTable.size())
			return true;
		Vertex v = path.peek();
		for (Edge edge : v.getEdges()) {
			Vertex other = edge.getOther(v);
			if (!path.contains(other)) {
				path.add(other);
				if (searchHamiltonian(path))
					return true;
			}
			path.pop();
		}
		return false;
	}

		/**
		 * Trails must have at least one edge AND graph must be connected
		 */
	public boolean isEulerianTrailDeepSearch() {
		if (vertexEdgeTable.size() == 0)
			return false;
		Stack<Edge> trailEdges = new Stack<>();
		return searchEulerian(vertexEdgeTable.keySet().stream().findFirst().get(), trailEdges);
	}

	private boolean searchEulerian(Vertex currentVertex, final Stack<Edge> trailEdges) {
		printSequence(trailEdges);
		if (trailEdges.size() == edgesSize)
			return true;
		for (Edge edge : currentVertex.getEdges()) {
			if (!trailEdges.contains(edge)) {
				trailEdges.add(edge);
				if (searchEulerian(edge.getOther(currentVertex), trailEdges))
					return true;
				trailEdges.pop();
			}
		}
		return false;
	}

	public boolean isEulerianTrail() {
		long amountOfOdds = vertexEdgeTable.values().stream()
													.map(List::size)
													.filter(s -> s % 2 == 1)
													.count();
		return amountOfOdds == 0 || amountOfOdds == 2;
	}

	private static <T> void printSequence(Stack<T> seq) {
		System.out.println(seq.stream().map(T::toString).collect(Collectors.joining("->")));
	}

}
