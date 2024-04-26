package bran.application.draw.graphs;

import bran.lattice.graphs.Edge;
import bran.lattice.graphs.Graph;
import bran.lattice.graphs.Vertex;

import java.applet.Applet;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.HashMap;

public class GraphViewer extends Applet {

    private final int DELAY = 20;
	private Graph graph;

    public GraphViewer(Graph graph) {
    	this.graph = graph;
    }

    public void init() {

//        this.resize();

//        width = this.getWidth();
//        height = this.getHeight();

//        ActionListener taskPerformer = new ActionListener() {
//            public void actionPerformed(ActionEvent evt) {
//                repaint();
//            }
//        };
//
//        new Timer(DELAY, taskPerformer).start();

        repaint();

    }

    public void update(Graphics g) {
        Graphics offgc;
        Image offscreen = null;
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

        offscreen = createImage(d.width, d.height);
        offgc = offscreen.getGraphics();

        offgc.setColor(getBackground());
        offgc.fillRect(0, 0, d.width, d.height);
        offgc.setColor(getForeground());

        paint(offgc);
        g.drawImage(offscreen, 0, 0, this);
    }

//    private final double standardLength = 200;
    private final double shiftX = 100;
    private final double shiftY = 100;

    public void paint(Graphics g) {

    	Ellipse2D.Double x;
//		graph = new Graph(new Matrix(Matrix.getRandomIntMatrix(5, 5, 0, 2)));
//		System.out.println(graph.toString());

    	double standardLength = Math.min(getWidth() / 2 - shiftX, getHeight() / 2 - shiftY);
    	double loopRadius = standardLength / 4;
        Graphics2D g2 = (Graphics2D) g;

//        g2.drawArc(200, 200, 100, 100, 0, 180);

        double count = 0;
        HashMap<Vertex, Double> degree = new HashMap<Vertex, Double>();
        ArrayList<Vertex> vertices = graph.getVertices();
        double shift = Math.PI / (vertices.size() % 2 == 0 ? vertices.size() <= 4 ? 4 : 2 : - 2);
        for (Vertex v : vertices) {
        	degree.put(v, shift + count * 2 * Math.PI / (vertices.size()));
        	// v.setXPos((int) (shiftX + standardLength * (1 + Math.cos(degree.get(v))))); // TODO temp comment
        	// v.setYPos((int) (shiftY + standardLength * (1 + Math.sin(degree.get(v)))));
        	// g2.fillOval(v.getXPos() - 4, v.getYPos() - 4, 8, 8);
        	g2.drawString(v.toString(), (int) (shiftX + - 5 + standardLength + (15 + standardLength) * Math.cos(degree.get(v))),
        			(int) (shiftY + 5 + standardLength + (15 + standardLength) * Math.sin(degree.get(v))));
        	count++;
        }
        for (Vertex v : vertices) {
        	HashMap<Edge, Integer> repeats = new HashMap<Edge, Integer>();
        	HashMap<Edge, Integer> repeatsMax = new HashMap<Edge, Integer>();
        	for (Edge e : v.getEdges()) {
        		repeats.put(e, 0);
        		repeatsMax.put(e, 0);
        	}
        	for (int i = 0; i < v.getEdges().size(); i++)
            	for (int j = 0; j < v.getEdges().size(); j++)
            		if (i != j && v.getEdges().get(i).equals(v.getEdges().get(j))) {
        					repeatsMax.put(v.getEdges().get(i), repeatsMax.get(v.getEdges().get(i)) + 1);
            			if (j >= i + 1)
            				repeats.put(v.getEdges().get(i), repeats.get(v.getEdges().get(i)) + 1);
            		}
            for (Edge e : v.getEdges()) {
            	if (e.getVertices().size() == 1) {
//            		for (int i = 0; i < repeats.get(e) + 1; i++) {
            			double loopRadius0 = loopRadius * (.5 * repeats.get(e) + 1);
	            		// g2.drawOval((int) (v.getXPos() + loopRadius0 * (-1 + Math.cos(degree.get(v)))), (int) (v.getYPos() + loopRadius0  * (-1 + Math.sin(degree.get(v)))), (int) loopRadius0 * 2, (int) loopRadius0 * 2);  // TODO temp comment
	    	        	// g2.drawString(e.toString(), (int) (v.getXPos() -5 + (8 + loopRadius0 * 2) * Math.cos(degree.get(v))), (int) (v.getYPos() + 5 + (8 + loopRadius0  * 2) * Math.sin(degree.get(v))));  // TODO temp comment
//            		}
//        			repeats.put(e, repeats.get(e) - 1);
            	}
            	else {
            		if (repeatsMax.get(e) % 2 == 0) {
            			double loopRadius0 = loopRadius * (.5 * repeats.get(e) + 1);
            			Vertex w = e.getVertices().get(e.getVertices().indexOf(v) == 0 ? 1 : 0);
            			// g2.drawLine(v.getXPos(), v.getYPos(), w.getXPos(), w.getYPos());  // TODO temp comment
            			// g2.drawString(e.toString(), (v.getXPos() + w.getXPos()) / 2, (v.getYPos() + w.getYPos()) / 2);  // TODO temp comment
            		}
//            		else {
//            		if (repeats.get(e) == 0) {
//            			double p = -.1; //loopRadius * (.5 * repeats.get(e) + 1)
//            			Vertex w = e.getVertices().get(e.getVertices().indexOf(v) == 0 ? 1 : 0);
//            			double loopRadius0 = 2 * Math.sqrt((Math.pow(v.getYPos() - w.getYPos(), 2) + Math.pow(v.getXPos() - w.getXPos(), 2)) * (p * p + .25));
////            			g2.drawLine(v.getXPos(), v.getYPos(), w.getXPos(), w.getYPos());
//            			g2.drawArc((int) (-loopRadius0 / 2 + (v.getXPos() + w.getXPos()) / 2 + (v.getYPos() - w.getYPos()) * p),
//            					(int) (-loopRadius0  / 2 + (v.getYPos() + w.getYPos()) / 2 + (w.getXPos() - v.getXPos()) * p),
//            					(int) (loopRadius0), (int) (loopRadius0),
////            					0,
////            					360);
//            			(int) ((180 / Math.PI) * Math.asin((v.getYPos() - ((v.getYPos() + w.getYPos()) / 2 - (v.getXPos() - w.getXPos()) * p)) / loopRadius0)),
//            			(int) ((180 / Math.PI) * (Math.PI - 2 * Math.atan(2 * p))));
//            			g2.drawString(e.toString(), (v.getXPos() + w.getXPos()) / 2, (v.getYPos() + w.getYPos()) / 2);
////            		}
//            		}
            	}
			}
        }
//        HashMap<Vertex, Integer> vertexX = new HashMap<Vertex, Integer>();
//        HashMap<Vertex, Integer> vertexY = new HashMap<Vertex, Integer>();
//        Set<Entry<Vertex, ArrayList<Edge>>> entrySet = graph.getVertexEdgeTable().entrySet();
//        double count = 0;
////        		360 / entrySet.size();
//        for (Entry<Vertex, ArrayList<Edge>> v : entrySet) {
//        	vertexX.put(v.getKey(), (int) (shiftX + standardLength * (1 + Math.cos(count * 2 * Math.PI / (entrySet.size())))));
//        	vertexY.put(v.getKey(), (int) (shiftY + standardLength * (1 + Math.sin(count * 2 * Math.PI / (entrySet.size())))));
//        	g2.fillOval((int) (shiftX + standardLength * (1 + Math.cos(count * 2 * Math.PI / (entrySet.size())))) - 4,
//        			(int) (shiftY + standardLength * (1 + Math.sin(count * 2 * Math.PI / (entrySet.size())))) - 4, 8, 8);
//        	g2.drawString(v.getKey().toString(), (int) (shiftX + - 5 + standardLength + (15 + standardLength) * Math.cos(count * 2 * Math.PI / (entrySet.size()))),
//        			(int) (shiftY + 5 + standardLength + (15 + standardLength) * Math.sin(count * 2 * Math.PI / (entrySet.size()))));
//        	count++;
//        }
//
//        for (Entry<Vertex, ArrayList<Edge>> v : entrySet) {
//        	for (Entry<Vertex, ArrayList<Edge>> w : entrySet) {
//        		if (v != w) {
//        			for (Edge e : v.getValue())
//        				for (Edge f : w.getValue())
//        					if (e == f) {
//        						g2.drawLine(vertexX.get(v.getKey()), vertexY.get(v.getKey()), vertexX.get(w.getKey()), vertexY.get(w.getKey()));
//        						g2.drawString(e.toString(), (int) ((vertexX.get(v.getKey()) + vertexX.get(w.getKey())) / 2),
//        								(int) ((vertexY.get(v.getKey()) + vertexY.get(w.getKey())) / 2));
//
//        					}
//        		}
//        	}
//        }
        // calculate vertex positions, then draw lines between them
    }

}
