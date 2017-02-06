package modele;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;


@SuppressWarnings("serial")
public class VueMain extends JFrame{
	
	
	public VueMain(){
		super("Kohonen");
		Modele m = new Modele(8,8);
		VueCarte c = new VueCarte(m);
		this.add(c,BorderLayout.CENTER);
		setPreferredSize(new Dimension(700,700));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
		m.apprentissage("src/iris.data.txt");
	}

	public static void main(String[] args) {
		new VueMain();
	}
	
}
