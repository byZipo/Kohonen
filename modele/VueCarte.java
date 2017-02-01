package modele;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JPanel;

public class VueCarte extends JPanel implements Observer {

	protected Modele m;
	protected JButton[][] carte;
	protected JButton bouton;

	public VueCarte(Modele m) {
		super();
		this.m = m;
		m.addObserver(this);
		//this.setPreferredSize(new Dimension(600, 600));
		carte = new JButton[m.width][m.height];
		for (int i = 0; i < carte.length; i++) {
			for (int j = 0; j < carte[0].length; j++) {
				bouton = new JButton();
				bouton.setBackground(Color.WHITE);
				carte[i][j] = bouton;
			}
		}

	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		for (int i = 0; i < carte.length; i++) {
			for (int j = 0; j < carte[0].length; j++) {
				if(m.carte[i][j].etiquette.equals("Iris-setosa"))carte[i][j].setBackground(Color.BLUE);
				else if(m.carte[i][j].etiquette.equals("Iris-setosa"))carte[i][j].setBackground(Color.RED);
				else if(m.carte[i][j].etiquette.equals("Iris-setosa"))carte[i][j].setBackground(Color.GREEN);
				else carte[i][j].setBackground(Color.WHITE);
			}

		}
	}

}
