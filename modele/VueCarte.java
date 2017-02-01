package modele;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;


@SuppressWarnings("serial")
public class VueCarte extends JPanel implements Observer {

	protected Modele m;
	protected JButton[][] carteGraphique;
	protected JButton bouton;
	ImageIcon c1 = new ImageIcon(VueCarte.class.getResource("/modele/img/bleu.png"));
	ImageIcon c2 = new ImageIcon(VueCarte.class.getResource("/modele/img/rouge.png"));
	ImageIcon c3 = new ImageIcon(VueCarte.class.getResource("/modele/img/vert.png"));
	

	public VueCarte(Modele m) {
		super();
		this.m = m;
		m.addObserver(this);
		//this.setPreferredSize(new Dimension(600, 600));
		carteGraphique = new JButton[m.width][m.height];
		setLayout(new GridLayout(m.width,m.height));
		for (int i = 0; i < carteGraphique.length; i++) {
			for (int j = 0; j < carteGraphique[0].length; j++) {
				bouton = new JButton();
				bouton.setBackground(Color.WHITE);
				carteGraphique[i][j] = bouton;
				this.add(bouton);
			}
		}

	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		for (int i = 0; i < carteGraphique.length; i++) {
			for (int j = 0; j < carteGraphique[0].length; j++) {
				if(m.carte[i][j].etiquette.equals("Iris-setosa"))carteGraphique[i][j].setIcon(c1);
				else if(m.carte[i][j].etiquette.equals("Iris-versicolor"))carteGraphique[i][j].setIcon(c2);
				else if(m.carte[i][j].etiquette.equals("Iris-virginica"))carteGraphique[i][j].setIcon(c3);
				//else carteGraphique[i][j].setBackground(Color.WHITE);
			}

		}
	}

}
