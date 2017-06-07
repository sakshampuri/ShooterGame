package FirstGame;

import java.awt.Color;

public class PowerUp {
	
	private double x;
	private double y;
	private int r;
	
	private int type;
	private int speed;
	
	private Color powerUpColor;
	
	private Enemy e;
	
	/**
	 * 1--> +1 Life
	 * 2--> +1 Power
	 * 3--> +2 Power
	 * 4--> Laser
	 * 5--> SlowMotion
	 */
	public PowerUp(Enemy e, int type){
		
		this.x = e.getx();
		this.y = e.gety();
		
		this.type = type;
		
		this.e = e;
		
		speed = 2;
		
		switch(type){
		
		case 1:{
			powerUpColor = Color.WHITE;
			r = 5;
		}
		break;
		case 2:{
			powerUpColor = Color.PINK;
			r = 5;
		}
		break;
		case 3:{
			powerUpColor = Color.GREEN;
			r = 3;
		}
		break;
		case 4:{
			powerUpColor = Color.YELLOW;
			r = 3;
			speed = 4;
		}
		break;
		case 5:{
			powerUpColor = Color.white;
			r = 6;
			speed = 1;
		}
		
		break;
		
		default: System.exit(0);
		
		}
		
	}
	
	//Getter Methods
	public double getx(){return x;}
	public double gety(){return y;}
	public int getr(){return r;}
	public int getType(){return type;}
	
	public boolean update(){
		
		if(e.isSlow())
		 y += speed*0.3;
		else
		 y += speed;
		if(y > GamePanel.HEIGHT - r) return true;
		return false;
		
	}
	
	public void draw(java.awt.Graphics2D g){
		
		g.setColor(powerUpColor);
		g.fillRect((int) x+e.getr() , (int) y+e.getr(), 2*r, 2*r);
		
	}

}
