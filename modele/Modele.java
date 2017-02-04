package modele;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Random;


public class Modele extends Observable{
	
	
	public Neurone[][] carte;
	public ArrayList<Neurone> entrees;
	public double mu = 0.05; //pas d'apprentissage decroissant
	public double sigma = 0.5; //ecart type deccroissant
	public int nombreDeClasse = 3;
	private int champs;
	public int k = 3;
	public int width;
	public int height;
	
	public Modele(int width, int length){
		this.carte = new Neurone[width][length];
		this.entrees = new ArrayList<Neurone>();
		this.width=width;
		this.height=length;
		initialisationAleatoirePoids();
		
		afficherCarte();
		
	//	apprentissage("src/iris.data.txt");
		//afficherEntrees();
		
	}
	
	
	double distance(double[] a, double[] b) {
		double dist = 0.;
		int dim = a.length;
		for(int i = 0; i < dim; i++){
			dist += (double)Math.abs(b[i]-a[i]);
			
		}
		return dist;
	}
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
			distance[i] = distance(blocApprentissage[i],xt);
		}
		
		int indice = 0;
		double[][] kMin = new double[K][2];
		for(int i = 0; i < K; i++){
			double min = Integer.MAX_VALUE;
			for(int j = 0; j < distance.length; j++){
				if(distance[j] < min){
					min = distance[j];
					kMin[i][0] = min;
					kMin[i][1] = j;
					indice = j;
				}
			}
			distance[indice] = Integer.MAX_VALUE;
		}
		
		HashMap<String,Integer> comptage = new HashMap<>();
		for (int i = 0; i < etiquetteApprentissage.length; i++) {
			if(!comptage.containsKey(etiquetteApprentissage[i])){
				comptage.put(etiquetteApprentissage[i], 0);
			}
		}
		
		int indiceK;
		for(int i = 0; i < kMin.length; i++){
			indiceK = (int)kMin[i][1];
			comptage.put(etiquetteApprentissage[indiceK], comptage.get(etiquetteApprentissage[indiceK])+1);
		}
		
		int maxCorrespondance = Integer.MIN_VALUE;
		String etiquette = null;
		for (String clef : comptage.keySet()) {
			if(comptage.get(clef) > maxCorrespondance){
				maxCorrespondance = comptage.get(clef);
				etiquette = clef;
			}
		}
		return etiquette;
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
			int xMin = 0;
			int yMin = 0;
			//Récuperation du neurone de la carte le plus proche
			for (int x = 0; x < carte.length; x++) {
				for (int y = 0; y < carte.length; y++) {
					Neurone ni = carte[x][y];
					double distance = getDistance(xi, ni);
					if(distance < min){
						min = distance;
						xMin = x;
						yMin = y;
					}
				}
			}
			
			// mise à jour de ivainqueur
			/*
			carte[xMin][yMin].x1 += mu * gausienne((carte[xMin][yMin]),xi);
			carte[xMin][yMin].x2 += mu * gausienne((carte[xMin][yMin]),xi);
			carte[xMin][yMin].x3 += mu * gausienne((carte[xMin][yMin]),xi);
			carte[xMin][yMin].x4 += mu * gausienne((carte[xMin][yMin]),xi);
			
			
			//mise à jour de ses voisins proches (ses voisins en 4-connexité) 
			if(xMin<carte.length-1){
				carte[xMin+1][yMin].x1 += mu * gausienne((carte[xMin+1][yMin]),xi);
				carte[xMin+1][yMin].x2 += mu * gausienne((carte[xMin+1][yMin]),xi);
				carte[xMin+1][yMin].x3 += mu * gausienne((carte[xMin+1][yMin]),xi);
				carte[xMin+1][yMin].x4 += mu * gausienne((carte[xMin+1][yMin]),xi);
			}
			
			if(xMin > 0){
				carte[xMin-1][yMin].x1 += mu * gausienne((carte[xMin-1][yMin]),xi);
				carte[xMin-1][yMin].x2 += mu * gausienne((carte[xMin-1][yMin]),xi);
				carte[xMin-1][yMin].x3 += mu * gausienne((carte[xMin-1][yMin]),xi);
				carte[xMin-1][yMin].x4 += mu * gausienne((carte[xMin-1][yMin]),xi);
			}
			
			if(yMin<carte.length-1){
				carte[xMin][yMin+1].x1 += mu * gausienne((carte[xMin][yMin+1]),xi);
				carte[xMin][yMin+1].x2 += mu * gausienne((carte[xMin][yMin+1]),xi);
				carte[xMin][yMin+1].x3 += mu * gausienne((carte[xMin][yMin+1]),xi);
				carte[xMin][yMin+1].x4 += mu * gausienne((carte[xMin][yMin+1]),xi);
			}
			
			if(yMin > 0){
				carte[xMin][yMin-1].x1 += mu * gausienne((carte[xMin][yMin-1]),xi);
				carte[xMin][yMin-1].x2 += mu * gausienne((carte[xMin][yMin-1]),xi);
				carte[xMin][yMin-1].x3 += mu * gausienne((carte[xMin][yMin-1]),xi);
				carte[xMin][yMin-1].x4 += mu * gausienne((carte[xMin][yMin-1]),xi);
			}*/
			
			
			
			//Mise à jour des poids pour chaque neurone de la carte
			for (int x = 0; x < carte.length; x++) {
				for (int y = 0; y < carte[0].length ; y++) {
					carte[x][y].x1 += mu * gausienne(carte[x][y],carte[xMin][yMin]) * (entrees.get(i).x1 - carte[x][y].x1);
					carte[x][y].x2 += mu * gausienne(carte[x][y],carte[xMin][yMin]) * (entrees.get(i).x2 - carte[x][y].x2);
					carte[x][y].x3 += mu * gausienne(carte[x][y],carte[xMin][yMin]) * (entrees.get(i).x3 - carte[x][y].x3);
					carte[x][y].x4 += mu * gausienne(carte[x][y],carte[xMin][yMin]) * (entrees.get(i).x4 - carte[x][y].x4);
					//System.out.println((mu * gausienne(carte[x][y],carte[xMin][yMin]) * (entrees.get(i).x1 - carte[x][y].x1)));
					double[] ligne = new double[champs];
					ligne[0] = carte[x][y].getX1();
					ligne[1] = carte[x][y].getX2();
					ligne[2] = carte[x][y].getX3();
					ligne[3] = carte[x][y].getX4();
					carte[x][y].setEtiquette(prediction(ligne, this.k));
				
				}
			}
			setChanged();
			notifyObservers();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			
			//System.out.println(mu+" "+sigma);
			afficherCarte();
			if(mu>0.005)mu -= 0.0005; 
			if(sigma>0.05)sigma -= 0.005; 
		}
		
		System.out.println("FINI");
	}
	
	
	public double gausienne(Neurone x, Neurone y){
		
		double distance = getDistance(x, y);
		distance = distance*distance;
		
		double ecart = (2*sigma)*(2*sigma);
		
		return Math.exp(-(distance/ecart));
		//return Math.exp(-((x*x)/(Math.pow(2*sigma,2))));
		
	}
	
	//lecture du fichier des entrées
	public void lectureFichier(String fichier)throws IOException{
		
			BufferedReader br2;
			String st;
			int cmpt=0;/*** POUR LE MOMENT JE NE FAIS QUE 10 TESTS *****/ 
			//System.out.println("Lecture fichier... ");
			String[] separated;
			br2 = new BufferedReader(new FileReader(fichier));
			
			while ((st = br2.readLine()) != null) {
				separated = st.split(",");
				this.champs = separated.length-1;
				double x1 = Double.parseDouble(separated[0]);
				double x2 = Double.parseDouble(separated[1]);
				double x3 = Double.parseDouble(separated[2]);
				double x4 = Double.parseDouble(separated[3]);
				String x5 = separated[4];
				Neurone n = new Neurone(x1,x2,x3,x4,x5);
				entrees.add(n);
				cmpt++;
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
				carte[i][j].x1= r.nextInt(8);
				carte[i][j].x2= r.nextInt(4);
				carte[i][j].x3= r.nextInt(7);
				carte[i][j].x4= r.nextInt(3);
				carte[i][j].etiquette = "";
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
		st.append("Carte de Kohonen (pour chaque neurone I on a (i1,i2,i3,i4)) 8x8: \n");
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
