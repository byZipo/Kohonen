package modele;

public class Neurone {
	
	
	//4 composantes du vecteur de x 
	public double x1;
	public double x2;
	public double x3;
	public double x4;
	
	
	public Neurone(double a, double b, double c, double d){
		this.x1=a;
		this.x2=b;
		this.x3=c;
		this.x4=d;
	}
	
	public Neurone(){
		
	}


	public double getX1() {
		return x1;
	}


	public void setX1(double x1) {
		this.x1 = x1;
	}


	public double getX2() {
		return x2;
	}


	public void setX2(double x2) {
		this.x2 = x2;
	}


	public double getX3() {
		return x3;
	}


	public void setX3(double x3) {
		this.x3 = x3;
	}


	public double getX4() {
		return x4;
	}


	public void setX4(double x4) {
		this.x4 = x4;
	}

	@Override
	public String toString() {
		return ("("+x1+","+x2+","+x3+","+x4+")");
	}
}