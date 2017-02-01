package modele;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;


public class VueMain extends JFrame{
	
	
	public VueMain(){
		super("Démineur");
		Modele m = new Modele(8, 8);
		VueCarte c = new VueCarte(m);
		this.add(c,BorderLayout.CENTER);
		setPreferredSize(new Dimension(700,700));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	public static void main(String[] args) {
		new VueMain();
	}
	
}
