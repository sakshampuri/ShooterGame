package FirstGame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class Bullet {
	
	//Coordinates
	private int x;
	private int y;
	//Vector Components
	private double dx;
	private double dy;
	//Speed
	private int speed;
	//Radius
	private int r;
	//Angle
	public double angle;
	//Bullet Colors
	private Color bulletColor;
	private Color bulletBoundaryColor;
	
	public Bullet(int angle, int x, int y){
		
		//set coordinates
		this.x = x;
		this.y = y;
		
		//Set Speed
		speed = 10 ;
		
		//set radius
		r = 5;
		
		//Set Angle
		this.angle = Math.toRadians(angle);
		
		//Set Components
		dx = Math.cos(this.angle)*speed;
		dy = Math.sin(this.angle)*speed;
		
		//Set Bullet Color
        bulletColor = Color.YELLOW;
	   	bulletBoundaryColor = bulletColor.darker().darker();
	
	}
	
	public int getx(){return x;}
	public int gety(){return y;}
	public int getr(){return r;}
	public double getAngle(){return angle;}
	
	public boolean update(){
		
		x+= dx;
		y+= dy;
		//Return Condition of out of Frame
		if(x > GamePanel.WIDTH-r || y > GamePanel.HEIGHT-r || x < -r || y < -r) return true;
		//Return Default
		return false;
		
	}
	
	public void draw(Graphics2D g){
		
		//Set Stroke
		g.setStroke(new BasicStroke(2));
		//Set Color
		g.setColor(bulletColor);
		//Fill
		g.fillOval(x+r, y+r, 2*r, 2*r);
		//Boundary
		g.setColor(bulletBoundaryColor);
		//Draw Boundary
		g.drawOval(x+r, y+r, 2*r, 2*r);
		//Restore Stroke
		g.setStroke(new BasicStroke(1));
		
	}

}
