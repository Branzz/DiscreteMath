package bran.draw;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import bran.graphs.Graph;

public class StartViewer {

    public static void startViewerGraph(Graph graph) {

//        JTextField bC = new JTextField(4);
//        JPanel parameters = new JPanel();
//        parameters.add(new JLabel("A: "));
//        parameters.add(bC);
//
//        int result = JOptionPane.showConfirmDialog(null, parameters, 
//                 "Enter Parameters", JOptionPane.OK_CANCEL_OPTION);
//        if (result == JOptionPane.OK_OPTION) {

        	Viewer trial = new Viewer(graph);

            JFrame viewer = new JFrame("Graph");
            viewer.setContentPane(trial);
            viewer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

//            window.setPreferredSize(new Dimension(500, 500));
//            window.pack();
//            window.setLocationRelativeTo(null);
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

    		InputStream input = StartViewer.class.getResourceAsStream("icon.png");
    		Image image = null;
    		try {
    			image = ImageIO.read(input);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
            viewer.setIconImage(image);

            viewer.setVisible(true);
            Insets insets = viewer.getInsets();
            viewer.setSize(451 + insets.left + insets.right, 451 + insets.top + insets.bottom);
            viewer.setLocation(dim.width / 2 - viewer.getSize().width / 2, dim.height / 2 - viewer.getSize().height / 2);
            viewer.setBackground(Color.WHITE);
            trial.init();
            trial.start();
//        }
//        else {
//            trial.stop()? trial.destroy()?
//        	parameters.setVisible(false);
//        }
    }
 
}
