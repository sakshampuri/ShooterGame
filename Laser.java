package FirstGame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class Laser {
	
	private Player player  = GamePanel.player;
	
	private int x;
	private int y;
	
	public static long startTimer;
	public static long laserTimer;
	public static long elapsed;
	
	//Constructor
	public Laser(){
		
		//Coordinates
		x = player.getx();
		y = player.gety();
		startTimer = System.nanoTime();
		laserTimer = 20000;
		
	}
	
	public boolean update(){
		
		x = player.getx();
		y = player.gety();
		
		elapsed = (System.nanoTime() - startTimer)/1000000;
		
		if(elapsed > laserTimer) return true;
		
		return false;
		
	}
	
	public void draw(Graphics2D g){
		
		g.setColor(Color.RED);
		g.setStroke(new BasicStroke(4));
		g.drawLine(x+player.getr(), y+player.getr(), x+player.getr(), 0);
		g.setStroke(new BasicStroke(1));
	}

}
