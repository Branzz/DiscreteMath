package bran.draw.exprs;

import bran.applet.Applet;
import bran.draw.GraphViewer;
import bran.draw.StartViewer;
import bran.graphs.Edge;
import bran.graphs.Graph;
import bran.graphs.Vertex;
import bran.mathexprs.treeparts.Constant;
import bran.mathexprs.treeparts.Expression;
import bran.mathexprs.treeparts.Variable;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.io.InputStream;
import java.util.*;

import static bran.mathexprs.treeparts.functions.MultivariableFunction.*;

public class StartExpressionViewer {

	public static void start() {

		// JTextField bC = new JTextField(4);
		// JPanel parameters = new JPanel();
		// parameters.add(new JLabel("A: "));
		// parameters.add(bC);
		//
		// int result = JOptionPane.showConfirmDialog(null, parameters,
		//          "Enter Parameters", JOptionPane.OK_CANCEL_OPTION);
		// if (result == JOptionPane.OK_OPTION) {

		ExpressionViewer viewer = new ExpressionViewer();

		JFrame jFrame = new JFrame("Expressions");

		jFrame.setContentPane(viewer);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//  window.setPreferredSize(new Dimension(500, 500));
		//  window.pack();
		//  window.setLocationRelativeTo(null);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

		try {
			jFrame.setIconImage(ImageIO.read(
					StartViewer.class.getResourceAsStream("icon.png")));
		} catch (Exception e) {
			e.printStackTrace();
		}

		jFrame.setVisible(true);
		Insets insets = jFrame.getInsets();
		jFrame.setSize(451 + insets.left + insets.right, 451 + insets.top + insets.bottom);
		jFrame.setLocation(dim.width / 2 - jFrame.getSize().width / 2, dim.height / 2 - jFrame.getSize().height / 2);
		jFrame.setBackground(Color.WHITE);
		viewer.setBackground(Color.WHITE);
		viewer.init();
		viewer.start();
	}

}
