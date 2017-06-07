package FirstGame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable, KeyListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 10;
	
	//Background Color
	private Color bgColor;
	
	//Dimensions
	public static int WIDTH;
	public static int HEIGHT;
	
	//FPS
	private int FPS;
	
	//Game Loop
	private boolean running;
	
	//Graphics
	public Graphics2D g;
	public BufferedImage image;
	
	//Thread
	private Thread thread;
	
	//Player
	public static Player player;
	
	//Enemies
	public static ArrayList<Enemy> enemies;
	
	//Bullets
	public static ArrayList<Bullet> bullets;
	
	//explosions
	public static ArrayList<Explosion> explosions;
	
	//PowerUp
	public static ArrayList<PowerUp> powerups;
	
	//Laser
	private Laser laser;
	private boolean laserTaken;
	
	//Wave System
	private long waveStartTimer;
	private long waveStartTimerDiff;
	private int waveDelay;
	private int waveNumber;
	private boolean waveStart;
	
	//Slow Motion
	private long slowStartTimer;
	private int slowLength;
	private long slowElapsed;
	
	//Constructor
	public GamePanel(){
		super();
		
		//background Color Set
		bgColor = new Color(50, 100, 100);
		
		//Dimension Initialize
		WIDTH = 600;
		HEIGHT = 450;
		
		//set size
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		
		//FPS Cap Set
		FPS = 60;
		
		//Focus Panel
		setFocusable(true);
		requestFocus();
		
		//wave
		waveStartTimer = 0;
		waveStartTimerDiff = 0;
		waveDelay = 2000;
		
		//Slow Down
		slowLength = 15000;
		
	}
	
	public void addNotify(){
		
		super.addNotify();
		if(thread == null){
			thread = new Thread(this);
			thread.start();
		}
		addKeyListener(this);
	}
	
	public void run(){
		
		running = true;
		
		//Graphics Initialize
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) image.getGraphics();
		
		//Creating Player
		player = new Player();
		
		//Create Enemies
		enemies = new ArrayList<Enemy>();
		
		//Initialize Bullets
		bullets = new ArrayList<Bullet>();
		
		//Initialize explosions
		explosions = new ArrayList<Explosion>();
		
		//Initialize PowerUps
		powerups = new ArrayList<PowerUp>();
		
		//anti-aliasing
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
				           RenderingHints.VALUE_ANTIALIAS_ON);
		
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
				           RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		// FPS
		long startTime;
		long waitTime;
		long URDTimeMillis;
		long targetTime = 1000/FPS;
		
		/**
		 * GAME LOOP
		 */
while(running) {
			
			startTime = System.nanoTime();
			
			gameUpdate();
			gameRender();
			gameDraw();
			
			URDTimeMillis = (System.nanoTime() - startTime) / 1000000;
			
			waitTime = targetTime - URDTimeMillis;
			
			try {
				Thread.sleep(waitTime);
			}
			catch(Exception e) {
			}
			
		}
		
	}
	
	public void gameUpdate(){
		
		//wave system
		if(waveStartTimer == 0 && enemies.size() == 0){
			waveNumber++;
			waveStart = false;
			waveStartTimer = System.nanoTime();
			
		} else{
			waveStartTimerDiff = (System.nanoTime() - waveStartTimer)/1000000;
			if(waveStartTimerDiff > waveDelay){
				waveStart = true;
				waveStartTimer = 0;
				waveStartTimerDiff = 0;
			}
		}
		
		//create enemies
		if(waveStart && enemies.size() == 0)
			createNewEnemies();
		
		//Player Update
		player.update();
		
		//Bullets
		for(int i = 0; i < bullets.size(); i++){
			
			boolean remove = bullets.get(i).update();
			if(remove){
				bullets.remove(i);
				i--;
			}
			
		}
		
		//Enemy
		for(int i = 0; i < enemies.size(); i++){
			enemies.get(i).update();
		}
		
		//Bullet-Enemy Collision
		if(!laserTaken)
		for(int i = 0; i < bullets.size(); i++){
			
			Bullet b = bullets.get(i);
			
			//Getting Coordinates of Bullet
			double bx = b.getx();
			double by = b.gety();
			double br = b.getr();
			
			for(int j = 0; j < enemies.size(); j++){
				
				Enemy e = enemies.get(j);
				//Getting coordinates of enemy
				double ex = e.getx();
				double ey = e.gety();
				double er = e.getr();
				
				//Calculating Distance 
				double dx = bx - ex;
				double dy = by - ey;
				
				//Distance Formula
				double dist = Math.sqrt(dx*dx + dy*dy);
				boolean removed = false;
				if(dist < br +er){
					
					//Collision Detected
					
					//Create New
					if(e.getHealth() == 2){
					
							explosions.add(new Explosion(e, e.getr()*5));
						enemies.add(
								new Enemy(
										(e.getRank() - 1 > 0)?e.getRank() - 1:1,
										(e.getType() - 1 > 0)?e.getType() - 1:1,
								        e.getHealth()-1, e.getAngle() + Math.toRadians(70),
								        e.getx() - e.getr(), e.gety()
								      ) 
						);
						enemies.add(
								new Enemy(
										(e.getRank() - 1 > 0)?e.getRank() - 1:1,
										(e.getType() - 1 > 0)?e.getType() - 1:1,
								        e.getHealth()-1, e.getAngle() - Math.toRadians(60),
								        e.getx() + e.getr(), e.gety()
								      ) 
						);
						enemies.remove(j);
						if(slowStartTimer!=0)
						for(int k = 0; k < enemies.size(); k++)
							enemies.get(k).setSlow(true);
						removed = true;
						
					}
					if(!removed)
					e.hit();
					bullets.remove(i);
					player.addScore(e.getType()+e.getRank());
					i--;
					break;
					
				}
				
			}
			
		}
		
		//enemy dead check
		for(int i = 0; i < enemies.size(); i++){
			if(enemies.get(i).isDead()){
				explosions.add(new Explosion(enemies.get(i), enemies.get(i).getr() * 5));
				
				//Chance for PowerUp
				double rand = Math.random();
				if(rand < 0.001) powerups.add(new PowerUp(enemies.get(i), 1));
				else if(rand < 0.005) powerups.add(new PowerUp(enemies.get(i),3));
				else if(rand < 0.03) powerups.add(new PowerUp(enemies.get(i),2));
				else if(rand < 0.07)powerups.add(new PowerUp(enemies.get(i),5));
				else if(rand < 0.1)powerups.add(new PowerUp(enemies.get(i),4));
				
				
				//Remove
				enemies.remove(i);
				i--;
			}
		}
		
		//Player-Enemy Collision
		if(!player.isRecovering() && !player.isOver()){
			
			//Player Coordinates
			int px = player.getx();
			int py = player.gety();
			int pr = player.getr();
			
			for(int i = 0; i < enemies.size(); i++){
				
				Enemy e = enemies.get(i);
				
				//Enemy Coordinates
				double ex = e.getx();
				double ey = e.gety();
				double er = e.getr();
				
				//Distance
				double dx = px - ex;
				double dy = py - ey;
				
				//Distance Formula
				double dist = Math.sqrt(dx*dx + dy*dy);
				
				if(dist < pr + er){
					player.loseLife();
					player.addScore(-(e.getRank()+e.getType()));
					explosions.add(new Explosion(enemies.get(i), enemies.get(i).getr() * 5));
					enemies.remove(i);
					i--;
					player.addPower(1);
				}
				
			}
			
		}
		
		
		//explosion
		for(int i = 0; i < explosions.size(); i++){
			boolean remove = explosions.get(i).update();
			if(remove){
				explosions.remove(i);
				i--;
			}
		}
		
		//PowerUp
		for(int i = 0; i < powerups.size(); i++){
			boolean remove = powerups.get(i).update();
			if(remove) powerups.remove(i);
		}
		
		//PowerUp - Player Collision
		for(int i = 0; i < powerups.size(); i++){
			
			PowerUp p = powerups.get(i);
			
			double px = p.getx();
			double py = p.gety();
			int pr = p.getr();
			
			int ppx = player.getx();
			int ppy = player.gety();
			int ppr = player.getr();
			
			double dx = px - ppx;
			double dy = py - ppy;
			
			//Distance
			double dist = Math.sqrt(dx*dx + dy*dy);
			
			if(dist < pr+ppr){
				//Collision Detected
				
				int type = p.getType();
				switch(type){
				
				case 1: player.addLife();
					break;
				case 2: player.addPower(1);
					break;
				case 3: player.addPower(2);
					break;
				case 4: laser = new Laser(); laserTaken = true;
				    break;
				case 5:{
					slowStartTimer = System.nanoTime();
					for(int j = 0;j < enemies.size(); j++)
						enemies.get(j).setSlow(true);
				}
				    break;
			    default: System.exit(0);
				
				}
				
				//Remove From Screen
				powerups.remove(i);
			}
			
		}
		
		//laser - enemy Collision
		if(laser!=null && laserTaken && !player.isOver()){
			
			int x = player.getx() + player.getr();
			int y = player.gety();
			
			for(int i = 0;i < enemies.size(); i++){
				Enemy e = enemies.get(i);
				double ex = e.getx();
				double ey = e.gety();
				int r = e.getr();
				double dist = (ex < x)? x - ex: ex - x;
				if(ey < y){
						if(dist < r){
							//Collision Detected
							explosions.add(new Explosion(e, r*5));
							player.addScore(e.getType()+e.getRank());
							enemies.remove(i);
						}
				}
			}
			
		}
		
		if(laser!=null){
			boolean remove = laser.update();
			if(player.isRecovering()) remove = player.isRecovering();
			if(remove) {
				laserTaken = false;
				laser = null;
			}
		}
		
		//Slow Down Update
		if(slowStartTimer!=0){
			slowElapsed = (System.nanoTime() - slowStartTimer)/1000000;
			if(slowElapsed > slowLength){
				slowStartTimer = 0;
				for(int i = 0; i < enemies.size(); i++)
					enemies.get(i).setSlow(false);
			}
		}
		
		
	}
	
	public void gameRender(){
		
		//Draw Background
		if(!player.isOver()){

			g.setColor(bgColor);
			g.fillRect(0, 0 , WIDTH, HEIGHT);
			
		}
		else{
		    g.setColor(new Color(200,120,100));
	     	g.fillRect(0, 0 , WIDTH, HEIGHT);
		}
		
		if(slowStartTimer!=0){
			g.setColor(new Color(255, 255, 255, 64));
			g.fillRect(0, 0, WIDTH, HEIGHT);
		}
		
		
		//Score
		g.setFont(new Font("Century Gothic", Font.BOLD, 12));
		g.setColor(Color.WHITE);
		g.drawString("SCORE: "+player.getScore(), WIDTH - 80, 20);
		
		//Credits
		g.setFont(new Font("Century Gothic", Font.BOLD, 15));
		g.drawString("Made By Saksham Puri", WIDTH-170, HEIGHT-10);
	
		//Wave Number
		if(waveStartTimer != 0){
			
			g.setFont(new Font("Century Gothic", Font.PLAIN, 30));
			String s = String.format("- W A V E  %d -", waveNumber);
			int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
			int alpha = (int) (255 * Math.sin(3.14 * waveStartTimerDiff/waveDelay));
			if(alpha > 255) alpha = 255;
			g.setColor(new Color(255, 255, 255, alpha));
			g.drawString(s, WIDTH/2 - length/2, HEIGHT/2);
			
		}
		
		
		//Lives
		for(int i = 0; i < player.getLives(); i++){
			
			g.setColor(bgColor.brighter().brighter());
			g.fillOval(15+(25*i), 15, player.getr()*2, player.getr()*2);
			g.setStroke(new java.awt.BasicStroke(2));
			g.setColor(Color.BLACK);
			g.drawOval(15+(25*i),15,player.getr()*2,player.getr()*2);
			g.setStroke(new java.awt.BasicStroke(1));
			
		}
		
		//Power
		g.setColor(Color.YELLOW);
		g.fillRect(20, 60, player.getPower()*8, 8);
		g.setStroke(new BasicStroke(2));
		g.setColor(Color.YELLOW.darker());
		for(int i = 0; i < player.getRequiredPower(); i++){
			g.drawRect(20 + 8*i, 60, 8, 8);
		}
		g.setStroke(new BasicStroke(1));
		
		//Draw Laser
		if(laserTaken)
		laser.draw(g);
		
		//Draw Player
		player.draw(g);
		
		//draw enemies
		for(int i = 0; i < enemies.size(); i++)
			enemies.get(i).draw(g);
		
		//Bullet
		if(!laserTaken)
		for(int i = 0; i < bullets.size(); i++){
			bullets.get(i).draw(g);
		}
		
		//explosion
		for(int i = 0;i < explosions.size(); i++)
			explosions.get(i).draw(g);
		
		//PowerUp
		for(int i = 0; i < powerups.size(); i++) powerups.get(i).draw(g);

		// draw slow down meter
		if(slowStartTimer != 0 && !laserTaken) {
			g.setColor(Color.WHITE);
			g.drawRect(10, 80, 100, 10);
			g.fillRect(10, 80,
				(int) (100 - 100.0 * slowElapsed / slowLength), 10);
		}
		else if(slowStartTimer != 0) {
			g.setColor(Color.WHITE);
			g.drawRect(10, 100, 100, 10);
			g.fillRect(10, 100,
				(int) (100 - 100.0 * slowElapsed / slowLength), 10);
		}
		
		//Laser timer
		if(laserTaken){
			g.setColor(Color.WHITE);
			g.setStroke(new BasicStroke(1));
			g.drawRect(10, 80,
					100, 8);
			g.fillRect(10, 80,
					(int) (100 - 100.0 * Laser.elapsed / Laser.laserTimer), 8);
			g.setStroke(new BasicStroke(1));
		}
		
		//over
		if(player.isOver()){

			//Over Display
	        g.setFont(new Font("Century Gothic", Font.PLAIN, 30));
	        String s="G A M E    O V E R";
	        int length=(int)g.getFontMetrics().getStringBounds(s,g).getWidth();
	        g.setColor(Color.WHITE);
	        g.drawString(s, WIDTH/2-length/2,HEIGHT/2);
	        //setters off
	        player.setLeft(false);
	        player.setRight(false);
	        player.setUp(false);
	        player.setDown(false);
	        player.setFiring(false);
	        //Listener off
	        removeKeyListener(this);
	        
		}
		
	}
	
	public void gameDraw(){
		
		//get graphics
		Graphics g2 = this.getGraphics();
		
		//draw
		g2.drawImage(image, 0, 0, null);
		
		//refresh
		g2.dispose();
		
	}

	//Create Enemies
	private void createNewEnemies(){
		
		int n = waveNumber *3
				;
		int type = 0;
		int rank = 0;
		
		for(int i = 0; i < n; i++){

	         type=(waveNumber<3)?1:(int) (Math.random() * (3)) + 1;
	         rank=(type==1)?(int) (Math.random() * (2)) + 1:(int) (Math.random() * (2)) + 1;
	         enemies.add(new Enemy(type, rank));
		}
		
	}
	
	//Key Pressed
	public void keyPressed(KeyEvent e){
		
		int keyCode = e.getKeyCode();
		if(keyCode == KeyEvent.VK_LEFT) player.setLeft(true);
		if(keyCode == KeyEvent.VK_RIGHT) player.setRight(true);
		if(keyCode == KeyEvent.VK_UP) player.setUp(true);
		if(keyCode == KeyEvent.VK_DOWN) player.setDown(true);
		if(keyCode == KeyEvent.VK_SPACE) player.setFiring(true);
		
	}
	
	//Key Released
	public void keyReleased(KeyEvent e){

		int keyCode = e.getKeyCode();
		if(keyCode == KeyEvent.VK_LEFT) player.setLeft(false);
		if(keyCode == KeyEvent.VK_RIGHT) player.setRight(false);
		if(keyCode == KeyEvent.VK_UP) player.setUp(false);
		if(keyCode == KeyEvent.VK_DOWN) player.setDown(false);
		if(keyCode == KeyEvent.VK_SPACE) player.setFiring(false);
		
	}
	
	public void keyTyped(KeyEvent e){}
	

}
