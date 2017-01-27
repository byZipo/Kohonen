package modele;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


public class Modele {
	
	
	public Neurone[][] carte;
	public ArrayList<Neurone> entrees;
	public double mu = 5.;
	public double sigma = 2.;
	
	public Modele(int width, int length){
		this.carte = new Neurone[width][length];
		this.entrees = new ArrayList<Neurone>();
		
		initialisationAleatoirePoids();
		
		afficherCarte();
		
		apprentissage("src/iris.data.txt");
		//afficherEntrees();
		
	}
	
	
	
	//apprentissage de la carte
	public void apprentissage(String fichier){
		try {
			lectureFichier(fichier);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//pour chaque entrée x (ligne du fichier), soit un vecteur dimesion 4 :
			//pour chaque noeurone i de la carte :
				//getDistance(x,i) --> recherche du min --> i vainqueur
			//fpour
			//recupération des voisins de i selon la gaussienne
			//mise à jour des poids des neurones wi(t+1) = wi(t)+mu*f(wi(t)-x)
		//fpour

		//pour chaque entrée
		for (int i = 0; i < entrees.size() ;i++) {
			Neurone xi = entrees.get(i);
			double min = Integer.MAX_VALUE;
			//Récuperation du neurone de la carte le plus proche
			for (int x = 0; x < carte.length; x++) {
				for (int y = 0; y < carte.length; y++) {
					Neurone ni = carte[x][y];
					double distance = getDistance(xi, ni);
					if(distance < min)min = distance;
				}
			}
			
			//Mise à jour des poids pour chaque neurone de la carte
			for (int x = 0; x < carte.length; x++) {
				for (int y = 0; y < carte.length; y++) {
					carte[x][y].x1 += mu * gausienne(min);
					carte[x][y].x2 += mu * gausienne(min);
					carte[x][y].x3 += mu * gausienne(min);
					carte[x][y].x4 += mu * gausienne(min);
				}
			}
			afficherCarte();
			mu -= 0.01;
			sigma -= 0.01;
		}
		
		
	}
	
	
	public double gausienne(double x){
		return Math.exp(-((x*x)/(Math.pow(2*sigma,2))));
		
	}
	
	//lecture du fichier des entrées
	public void lectureFichier(String fichier)throws IOException{
		
			BufferedReader br2;
			String st;

			//System.out.println("Lecture fichier... ");
			String[] separated;
			br2 = new BufferedReader(new FileReader(fichier));
			while ((st = br2.readLine()) != null) {
				separated = st.split(",");
				double x1 = Double.parseDouble(separated[0]);
				double x2 = Double.parseDouble(separated[1]);
				double x3 = Double.parseDouble(separated[2]);
				double x4 = Double.parseDouble(separated[3]);
				Neurone n = new Neurone(x1,x2,x3,x4);
				entrees.add(n);
			}
			br2.close();
	}
	
	
	//initialise les poids des neurones aléatoirement
	public void initialisationAleatoirePoids(){
		for (int i = 0; i < carte.length; i++) {
			for (int j = 0; j < carte[0].length; j++) {
				Random r = new Random();
				Neurone n = new Neurone();
				carte[i][j] = n;
				carte[i][j].x1= r.nextInt(10)-10;
				carte[i][j].x2= r.nextInt(10)-10;
				carte[i][j].x3= r.nextInt(10)-10;
				carte[i][j].x4= r.nextInt(10)-10;
				
			}
		}
	}
	
	//calcul de distance entre deux Neurones
	public double getDistance(Neurone n1, Neurone n2){
		double dx1 = Math.abs((double)n1.x1-n2.x1);
		double dx2 = Math.abs((double)n1.x2-n2.x2);
		double dx3 = Math.abs((double)n1.x3-n2.x3);
		double dx4 = Math.abs((double)n1.x4-n2.x4);
		
		return (double)(dx1+dx2+dx3+dx4);
	}
	
	//affichage carte
	public String toString(){
		StringBuilder st = new StringBuilder();
		st.append("Carte de Kohonen : \n");
		for (int i = 0; i < carte.length; i++) {
			for (int j = 0; j < carte[0].length; j++) {
				st.append(carte[i][j].toString());
			}
			st.append("\n");
		}
		return st.toString();
	}
	
	public void afficherCarte(){
		System.out.println(this.toString());
	}
	
	public void afficherEntrees(){
		System.out.println("Neurones d'entrée : ");
		for (int i = 0; i < entrees.size(); i++) {
			System.out.println(entrees.get(i).toString());
		}
	}
	
	
	public static void main(String[] args) {
		new Modele(8, 8);
	}

}
