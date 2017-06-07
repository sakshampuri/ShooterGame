package FirstGame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class Player {
	
	//check for firing bullets
	private boolean firing;
	
	//check for directions set
	private boolean left;
	private boolean right;
	private boolean up;
	private boolean down;
	
	//drawing coordinates
	private int x;
	private int y;
	
	//change in position
	private int dx;
	private int dy;
	
	//size
	private int r;
	
	//lives
	private int lives;
	
	//speed (in pixels)
	private int speed;
	
	//Player Color
	private Color playerColor;
	private Color playerBoundaryColor;
	
	//Score
	private int score;
	
	//Fire
	private long firingTimer;
	private int firingDelay;
	
	//Recover
	private long recoveryTimer;
	private boolean recovering;
	private long switchTimer = 0;
	
	//PowerUp System
	private int powerLevel;
	private int power;
	private int[] requiredPower = {
			1,2,3,4,5,6,7,8,9,10,11,12,13,14,15
	};
	
	//dead
	private boolean dead;
	
	public Player(){
		
		//Set initial Coordinates
		x = GamePanel.WIDTH/2;
	    y = GamePanel.HEIGHT/2;
	    
	    //Set Speed
	    speed = 6;
	    
	    //Set Radius
	    r = 10;
	    
	    //Set Default Firing
		firing = false;
		
		//Set Player Colors
		playerColor = Color.RED;
		playerBoundaryColor = Color.WHITE;
		
		//set life
		lives = 5;
		
		//Firing Timer
		firingTimer = System.nanoTime();
		
		//Firing Delay Set
		firingDelay = 150;
		
		//set recovering
		recovering = false;
		
		//dead initialize
		dead = false;
		
		//score
		score = 0;
		
		
	}
	
	//Getter Methods
	public int getx(){return x;}
	public int gety(){return y;}
	public int getr(){return r;}
	public int getLives(){return lives;}
	public int getScore(){return score;}
	public int getPower(){return power;}
	public int getPowerLevel(){return powerLevel;}
	public int getRequiredPower(){return requiredPower[powerLevel];}
	
	//Setter methods
	public void setLeft(boolean set){left = set;}
	public void setRight(boolean set){right = set;}
	public void setUp(boolean set){up = set;}
	public void setDown(boolean set){down = set;}
	public void setFiring(boolean set){firing = set;}
	
	//Life methods
	public boolean isRecovering(){return recovering;}
	public boolean isOver(){return dead;}
	
	//collision method
	public void loseLife(){
		
		lives--;
		if(lives <= 0) dead = true;
		recovering = true;
		recoveryTimer = System.nanoTime();
		
	}
	
	//PowerUp Methods
	public void addLife(){lives++;}
	public void addPower(int n){
		power += n;
		if(power >= requiredPower[powerLevel]){
			power -= requiredPower[powerLevel];
			powerLevel++;
		}
	}
	
	//addScore
	public void addScore(int n){score+=n;}
	
	public void update(){
		
		if(left) dx = -speed;
		if(right) dx = speed;
		if(up) dy = -speed;
		if(down) dy = speed;
		x+=dx;
		y+=dy;
		if(x < 2) x = 1;
		if(y < 2) y = 1;
		if(x > GamePanel.WIDTH - 2*r-3) x = GamePanel.WIDTH - 2*r-3;
		if(y > GamePanel.HEIGHT - 2*r-3) y = GamePanel.HEIGHT - 2*r-3;
		dx=0;
		dy=0;
		if(firing){
			
			long elapsed = (System.nanoTime() - firingTimer)/1000000;
			if(elapsed > firingDelay){
				firingTimer = System.nanoTime();
				if(powerLevel < 2) GamePanel.bullets.add(new Bullet(270, x, y));
				else if(powerLevel < 4){
					GamePanel.bullets.add(new Bullet(270, x + 5, y));
				    GamePanel.bullets.add(new Bullet(270, x - 5, y));
				}
				else{
					GamePanel.bullets.add(new Bullet(270, x, y));
					GamePanel.bullets.add(new Bullet(280, x + 5, y));
				    GamePanel.bullets.add(new Bullet(265, x - 5, y));
				}
			}
		}
		
		long elapsed = (System.nanoTime() - recoveryTimer)/1000000;
		if(elapsed > 2000 && recovering){
			recovering = false;
			recoveryTimer = 0;
		}
		
	}
	
	public void draw(Graphics2D g){
		
		if(!recovering && !GamePanel.player.isOver()){

			g.setStroke(new BasicStroke(2));
			g.setColor(playerColor);
			g.fillOval(x, y, 2*r, 2*r);
			g.setColor(playerBoundaryColor);
			g.drawOval(x, y, 2*r, 2*r);
			g.setStroke(new BasicStroke(1));
			
		}else{
			long elapsed = (System.nanoTime() - switchTimer)/1000000;
			if(elapsed > 50 || GamePanel.player.isOver()){

				g.setStroke(new BasicStroke(3));
				g.setColor(Color.WHITE);
				g.fillOval(x, y, 2*r, 2*r);
				g.setColor(Color.RED);
				g.drawOval(x, y, 2*r, 2*r);
				g.setStroke(new BasicStroke(1));
				switchTimer = System.nanoTime();
				
			}
			
		}
		
	}

}
