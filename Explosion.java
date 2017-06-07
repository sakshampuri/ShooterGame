package FirstGame;

import java.awt.Color;

public class Explosion {
	
	private double x;
	private double y;
	private int r;
	private int rMax;
	private Enemy e;
	
	public Explosion(Enemy e, int rMax){
		
		this.e = e;
		r = e.getr();
		x = e.getx();
		y = e.gety();
		this.rMax = rMax; 
		
	}
	
	public boolean update(){
		r+=3;
		if(r >= rMax) return true;
		return false;
	}
	
	public void draw(java.awt.Graphics2D g){
		
		int R = e.getColor().getRed();
		int G = e.getColor().getGreen();
		int B = e.getColor().getBlue();
		g.setColor(new Color(R, G, B, 70));
		g.fillOval((int) x - r,(int) y - r, 2*r, 2*r);
		
	}

}
