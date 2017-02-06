package modele;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Observable;
import java.util.Random;

public class Modele extends Observable {

	public Neurone[][] carte;          // carte de neurones
	public ArrayList<Neurone> entrees; // entrées

	/********* PARAMETRES **********/
	public double mu = .5;             // pas d'apprentissage decroissant initial
	public double sigma;               // ecart type deccroissant (calculé)
	public int k = 3;                  // k pour kNN
	public int tailleBaseTest = 75;    //taille de la base de test (en nombre de lignes)
	/******************************/
	
	private int champs;
	public int nbErreurs = 0;
	public int width;
	public int height;

	public Modele(int width, int length) {
		this.carte = new Neurone[width][length];
		this.entrees = new ArrayList<Neurone>();
		this.width = width;
		this.height = length;
		initialisationAleatoirePoids();
		afficherCarte();
	}

	double distance(double[] a, double[] b) {
		double dist = 0.;
		int dim = a.length;
		for (int i = 0; i < dim; i++) {
			dist += (double) Math.abs(b[i] - a[i]);
		}
		return dist;
	}

	// fonction de prédiction kNN
	String prediction(double[] xt, int K) {
		double blocApprentissage[][] = new double[entrees.size()][champs];
		String etiquetteApprentissage[] = new String[entrees.size()];
		for (int i = 0; i < entrees.size(); i++) {

			blocApprentissage[i][0] = entrees.get(i).getX1();
			blocApprentissage[i][1] = entrees.get(i).getX2();
			blocApprentissage[i][2] = entrees.get(i).getX3();
			blocApprentissage[i][3] = entrees.get(i).getX4();
			etiquetteApprentissage[i] = entrees.get(i).getEtiquette();

		}
		double[] distance = new double[blocApprentissage.length];
		for (int i = 0; i < blocApprentissage.length; i++) {
			distance[i] = distance(blocApprentissage[i], xt);
		}

		int indice = 0;
		double[][] kMin = new double[K][2];
		for (int i = 0; i < K; i++) {
			double min = Integer.MAX_VALUE;
			for (int j = 0; j < distance.length; j++) {
				if (distance[j] < min) {
					min = distance[j];
					kMin[i][0] = min;
					kMin[i][1] = j;
					indice = j;
				}
			}
			distance[indice] = Integer.MAX_VALUE;
		}

		HashMap<String, Integer> comptage = new HashMap<>();
		for (int i = 0; i < etiquetteApprentissage.length; i++) {
			if (!comptage.containsKey(etiquetteApprentissage[i])) {
				comptage.put(etiquetteApprentissage[i], 0);
			}
		}

		int indiceK;
		for (int i = 0; i < kMin.length; i++) {
			indiceK = (int) kMin[i][1];
			comptage.put(etiquetteApprentissage[indiceK], comptage.get(etiquetteApprentissage[indiceK]) + 1);
		}

		int maxCorrespondance = Integer.MIN_VALUE;
		String etiquette = null;
		for (String clef : comptage.keySet()) {
			if (comptage.get(clef) > maxCorrespondance) {
				maxCorrespondance = comptage.get(clef);
				etiquette = clef;
			}
		}
		return etiquette;
	}

	// apprentissage de la carte
	public void apprentissage(String fichier) {
		try {
			lectureFichier(fichier);
		} catch (IOException e) {
			e.printStackTrace();
		}

		double sigma0 = carte.length / 2;
		double mu0 = mu;
		double lambda = (double) entrees.size() / (double) Math.log(sigma0);

		// mélange de la base d'apprentissage
		Collections.shuffle(entrees);

		// création de la base de test (50 dernieres lignes des entrées)
		ArrayList<Neurone> baseTest = new ArrayList<Neurone>();
		for (int i = entrees.size()-tailleBaseTest; i < entrees.size(); i++) {
			baseTest.add(entrees.get(i));
		}

		// pour chaque entrée :
		for (int i = 0; i < entrees.size() - tailleBaseTest; i++) {
			System.out.println("Iteration apprentissage : " + (i+1));
			Neurone xi = entrees.get(i);
			double min = Integer.MAX_VALUE;
			int xMin = 0;
			int yMin = 0;
			// Récuperation du neurone de la carte le plus proche (vainqueur)
			for (int x = 0; x < carte.length; x++) {
				for (int y = 0; y < carte.length; y++) {
					Neurone ni = carte[x][y];
					double distance = getDistance(xi, ni);
					if (distance < min) {
						min = distance;
						xMin = x;
						yMin = y;
					}
				}
			}

			// Mise à jour des poids pour chaque neurone de la carte en fonction
			// du vainqueur
			for (int x = 0; x < carte.length; x++) {
				for (int y = 0; y < carte[0].length; y++) {

					// sigma décroissant
					this.sigma = (double) (sigma0 * Math.exp(-(double) i / lambda));

					// pas d'apprentissage décroissant
					this.mu = (double) (mu0 * Math.exp(-(double) i / entrees.size()));

					// gausienne
					double g = gausienne(carte[x][y], carte[xMin][yMin], x, y, xMin, yMin);
					
					// mise à jour des poids des neurones
					carte[x][y].x1 += mu * g * (double) (entrees.get(i).x1 - carte[x][y].x1);
					carte[x][y].x2 += mu * g * (double) (entrees.get(i).x2 - carte[x][y].x2);
					carte[x][y].x3 += mu * g * (double) (entrees.get(i).x3 - carte[x][y].x3);
					carte[x][y].x4 += mu * g * (double) (entrees.get(i).x4 - carte[x][y].x4);

					// kNN
					double[] ligne = new double[champs];
					ligne[0] = carte[x][y].getX1();
					ligne[1] = carte[x][y].getX2();
					ligne[2] = carte[x][y].getX3();
					ligne[3] = carte[x][y].getX4();
					carte[x][y].setEtiquette(prediction(ligne, this.k));

				}
			}

			// comptage des erreurs d'apprentissage
			if (carte[xMin][yMin].etiquette.equals(entrees.get(i).etiquette)) {}
			else nbErreurs++;
			
			
			// mise à jour gaphique
			setChanged();
			notifyObservers();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// affichage de la carte mise à jour
			afficherCarte();

			// if(mu>0.005)mu -= 0.0005;
			// if(sigma>0.05)sigma -= 0.005;
			
		}

		// affichage des résultats pour la base d'apprentissage
		double taux = (100 * nbErreurs) / (entrees.size()-tailleBaseTest);
		System.out.println("-------------------------------------------");
		System.out.println("TAILLE DE LA BASE D'APPRENTISSAGE : "+(entrees.size()-tailleBaseTest));
		System.out.println("NOMBRE D'ERREURS D'APPRENTISSAGE:" + nbErreurs);
		System.out.println("POURCENTAGE D'ERREUR D'APPRENTISSAGE: " + taux + "%");
		System.out.println("TAUX DE BONNE RECONNAISSANCE :" + (100 - taux) + "%\n");

		
		// Test de la carte apprise
		int nbErreursTest = 0;
		for (int i = 0; i < baseTest.size(); i++) {
			double min = Integer.MAX_VALUE;
			int xMin = 0;
			int yMin = 0;
			for (int x = 0; x < carte.length; x++) {
				for (int y = 0; y < carte.length; y++) {
					Neurone ni = carte[x][y];
					double distance = getDistance(baseTest.get(i), ni);
					if (distance < min) {
						min = distance;
						xMin = x;
						yMin = y;
					}
				}
			}
			Neurone n = new Neurone();
			n.setEtiquette(carte[xMin][yMin].getEtiquette());
			if (n.getEtiquette().equals(baseTest.get(i).getEtiquette())) {} 
			else nbErreursTest++;
		}
		
		// affichage des résultats pour la base de test
		double tauxTest = (100 * nbErreursTest) / baseTest.size();
		System.out.println("TAILLE DE LA BASE DE TEST :"+baseTest.size());
		System.out.println("NOMBRE D'ERREURS DE TEST :"+nbErreursTest);
		System.out.println("POURCENTAGE D'ERREUR DE TEST: " + tauxTest + "%");
		System.out.println("TAUX DE BONNE RECONNAISSANCE :" + (100 - tauxTest) + "%");
		System.out.println("-------------------------------------------");
		System.out.println("FINI");

	}

	// calcul de la gausienne en fonction de sigma(décroissant) et de la
	// distance
	public double gausienne(Neurone x, Neurone y, int x2, int y2, int xMin, int yMin) {

		double distance = getDistanceCarre(x, y, x2, y2, xMin, yMin);
		double ecart = (double) 2 * ((double) sigma * sigma);
		return (double) Math.exp(-((double) distance / ecart));
	}

	// lecture du fichier des entrées
	public void lectureFichier(String fichier) throws IOException {

		BufferedReader br2;
		String st;
		// System.out.println("Lecture fichier... ");
		String[] separated;
		br2 = new BufferedReader(new FileReader(fichier));

		while ((st = br2.readLine()) != null) {
			separated = st.split(",");
			this.champs = separated.length - 1;
			double x1 = Double.parseDouble(separated[0]);
			double x2 = Double.parseDouble(separated[1]);
			double x3 = Double.parseDouble(separated[2]);
			double x4 = Double.parseDouble(separated[3]);
			String x5 = separated[4];
			Neurone n = new Neurone(x1, x2, x3, x4, x5);
			entrees.add(n);
		}
		br2.close();
	}

	// initialisation des poids des neurones aléatoirement entre 0 et 1
	public void initialisationAleatoirePoids() {
		for (int i = 0; i < carte.length; i++) {
			for (int j = 0; j < carte[0].length; j++) {
				Random r = new Random();
				Neurone n = new Neurone();
				carte[i][j] = n;
				carte[i][j].x1 = r.nextDouble();
				carte[i][j].x2 = r.nextDouble();
				carte[i][j].x3 = r.nextDouble();
				carte[i][j].x4 = r.nextDouble();
				carte[i][j].etiquette = "";
			}
		}
	}

	// calcul de distance entre deux Neurones
	public double getDistance(Neurone n1, Neurone n2) {
		double dx1 = Math.abs((double) n1.x1 - n2.x1);
		double dx2 = Math.abs((double) n1.x2 - n2.x2);
		double dx3 = Math.abs((double) n1.x3 - n2.x3);
		double dx4 = Math.abs((double) n1.x4 - n2.x4);

		return (double) (dx1 + dx2 + dx3 + dx4);
	}

	// non utilisé
	public double getDistance2(Neurone n1, Neurone n2) {
		double dx1 = (double) Math.sqrt((double) (n1.x1 - n2.x1) * (n1.x1 - n2.x1));
		double dx2 = (double) Math.sqrt((double) (n1.x2 - n2.x2) * (n1.x1 - n2.x2));
		double dx3 = (double) Math.sqrt((double) (n1.x3 - n2.x3) * (n1.x1 - n2.x3));
		double dx4 = (double) Math.sqrt((double) (n1.x4 - n2.x4) * (n1.x1 - n2.x4));

		return (double) (dx1 + dx2 + dx3 + dx4);
	}

	// distance au carré entre deux neurones dans la carte
	public double getDistanceCarre(Neurone n1, Neurone n2, int x2, int y2, int xMin, int yMin) {

		double a = (double) Math.abs(x2 - xMin) * Math.abs(x2 - xMin);
		double b = (double) Math.abs(y2 - yMin) * Math.abs(y2 - yMin);
		return (double) a + b;

	}

	// affichage carte
	public String toString() {
		StringBuilder st = new StringBuilder();
		st.append("Carte de Kohonen (pour chaque neurone I on a (i1,i2,i3,i4)) 8x8: \n");
		for (int i = 0; i < carte.length; i++) {
			for (int j = 0; j < carte[0].length; j++) {
				st.append(carte[i][j].toString());
			}
			st.append("\n");
		}
		return st.toString();
	}

	public void afficherCarte() {
		System.out.println(this.toString());
	}

	public void afficherEntrees() {
		System.out.println("Neurones d'entrée : ");
		for (int i = 0; i < entrees.size(); i++) {
			System.out.println(entrees.get(i).toString());
		}
	}

	/*
	 * public static void main(String[] args) { new Modele(8, 8); }
	 */

}
