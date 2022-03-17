package bran.application.draw.exprs;

import bran.application.draw.StartViewer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

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

		Insets insets = jFrame.getInsets();
		jFrame.setSize(451 + insets.left + insets.right, 451 + insets.top + insets.bottom);
		jFrame.setLocation(dim.width / 2 - jFrame.getSize().width / 2, dim.height / 2 - jFrame.getSize().height / 2);
		jFrame.setBackground(Color.WHITE);
		viewer.setBackground(Color.WHITE);
		viewer.init();
		jFrame.setVisible(true);
		jFrame.setContentPane(viewer);
		viewer.start();
	}

}
