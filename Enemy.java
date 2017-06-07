package FirstGame;

import java.awt.Color;

public class Enemy {

	//Coordinates
	private double x;
	private double y;
	
	//Vector Components
	private double dx;
	private double dy;
	
	//Radius
	private int r;
	
	//speed
	private int speed;
	
	//Health
	private boolean setHealth;
	private int health;
	
	//dead
	private boolean dead;
	
	//Angle Vector
	private double angle;
	private int maxAngle;
	private int minAngle;
	
	//Type and Rank
	private int type;
	private int rank;
	
	//Set check
	private boolean setXY;
	
	//Colors
	private Color enemyColor;
	private Color enemyBoundaryColor;
	
	//Flash
	private boolean setFlash;
	private long startFlashTimer;
	private int flashTimer;
	
	//SlowDown
	private boolean slow;
	
	//Constructor
	public Enemy(int type, int rank){
		
		//type
		this.type = type;
		
		//rank
		this.rank = rank;
		
		//Angle set
		maxAngle = 130;
		minAngle = 45;
		angle = Math.toRadians(Math.random()*maxAngle + minAngle);
		
		//health
		setHealth = true;
		
		//set XY
		setXY = true;
		
		//Initialize
		init();
		
	}
	
	public Enemy(int type, int rank, int health, double angle, double x, double y){
		
		//type
		this.type = type;
		
		//rank
		this.rank = rank;
		
		//angle
		this.angle = angle;
		
		//health
		this.health = health;
		if(rank == 1 && type == 1) health =1;
		setHealth = false;
		
		//set X and Y
		this.x = x;
		this.y = y;
		setXY = false;
		
		//Initialize
		init();
		
	}
	
	public void init(){

		//set dead
		dead = false;
		
		//flash
		flashTimer = 80;
		
		//check type and rank
		if(type == 1){
			
			if(rank == 1){
				
				r = 9;
				if(setHealth)
				health = 1;
				speed=3;
				enemyColor = Color.MAGENTA;
				enemyBoundaryColor = Color.BLACK;
				
			}
			
			if(rank == 2){
				
				r = 11;
				if(setHealth)
				health=2;
				speed=2;
				enemyColor = Color.BLACK.brighter();
				enemyBoundaryColor = Color.MAGENTA;
				
			}
			
		}
		else if(type == 2){
			
			if(rank == 1){
				r = 12;
				if(setHealth)
				health = 3;
				speed = 2;
				enemyColor = Color.PINK;
				enemyBoundaryColor = Color.PINK.darker().darker();
			}
			else if(rank == 2){
				r = 13;
				if(setHealth)
				health = 4;
				speed = 2;
				enemyColor = Color.ORANGE;
				enemyBoundaryColor = enemyColor.darker().darker();
			}
			
		}
		else if(type == 3){
			
			if(rank == 1){
				r = 14;
				if(setHealth)
				health = 5;
				speed = 2;
				enemyColor = Color.MAGENTA.brighter();
				enemyBoundaryColor = Color.BLUE;
			}
			else if(rank == 2){
				r = 15;
				if(setHealth)
				health = 6;
				speed = 1;
				enemyColor = Color.DARK_GRAY;
				enemyBoundaryColor = Color.CYAN;
			}
			
		}
		//Coordinates Set
		if(setXY){
	 	    x = (Math.random()*GamePanel.WIDTH-(4*r)) + 2*r;
	    	y = -r; 
		}
		//Vector Components Set
		dx =  Math.cos(angle) * speed;
		dy =  Math.sin(angle) * speed;
		
	}
	
	//Getter Methods
	public double getx(){return x;}
	public double gety(){return y;}
	public int getr(){return r;}
	public int getHealth(){return health;}
	public double getAngle(){return angle;}
	public Color getColor(){return enemyColor;}
	public int getType(){return type;}
	public int getRank(){return rank;}
	
	//Dead Check
	public boolean isDead(){return dead;}
	
	//Setter
	public void setSlow(boolean b){slow = b;}
	
	//Getter
	public boolean isSlow(){return slow;}
	
	public void hit(){
		health--;
		if(health > 0){
			setFlash = true;
			startFlashTimer = System.nanoTime();
		}
		if(enemyColor == Color.MAGENTA && enemyBoundaryColor == Color.BLACK) dead =true;
		dead = (health<=0)?true:false;
	}
	
	public void update(){
		
		//Slow Down
		if(slow){
			x+=dx*0.3;
			y+=dy*0.3;
			flashTimer = 500;
		}
	   	else{
	   		x+=dx;
	   		y+=dy;
	   		flashTimer = 80;
	   	}
			
		if(type == 1 && rank ==  1) health = 1;
		if(x < r && dx < 0) dx = -dx;
		if(y < r && dy < 0) dy = -dy;
		if(x > GamePanel.WIDTH-r && dx > 0) dx = -dx;
		if(y > GamePanel.HEIGHT-r && dy > 0) dy = -dy;
		
		if(health > 1){

			long elapsed = (System.nanoTime() - startFlashTimer)/1000000;
			if(elapsed > flashTimer){
				setFlash = false;
				startFlashTimer = 0;
			}
		}
	}
	
	public void draw(java.awt.Graphics2D g){
		
		    //Put Alpha 
		    int R = enemyColor.getRed();
		    int G = enemyColor.getGreen();
		    int B = enemyColor.getBlue();
		    enemyColor = new Color(R, G, B, 150);
		    
		    //Put Alpha Boundary
		    int R1 = enemyBoundaryColor.getRed();
		    int G1= enemyBoundaryColor.getGreen();
		    int B1 = enemyBoundaryColor.getBlue();
		    enemyBoundaryColor = new Color(R1, G1, B1, 155);
		    
		    //Draw
		    if(health > 0 && setFlash && startFlashTimer!=0){
		    	
	    		g.setColor(Color.WHITE);
		    	g.setStroke(new java.awt.BasicStroke(2));
		    	g.fillOval((int) x, (int) y - r, 2*r, 2*r);
		    	g.setColor(enemyBoundaryColor.brighter());
			    g.drawOval((int) x, (int) y-r, 2*r, 2*r);
	    		g.setStroke(new java.awt.BasicStroke(1));
	    		
		    }else{

	    		g.setColor(enemyColor);
		    	g.setStroke(new java.awt.BasicStroke(2));
		    	g.fillOval((int) x, (int) y - r, 2*r, 2*r);
		    	g.setColor(enemyBoundaryColor);
			    g.drawOval((int) x, (int) y - r, 2*r, 2*r);
	    		g.setStroke(new java.awt.BasicStroke(1));
	    		
		    }
    		
	}
		
}
	
