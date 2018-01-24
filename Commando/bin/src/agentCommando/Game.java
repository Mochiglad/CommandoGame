/********************************************************************
* Name: Agent Commando
* Authors: Steven Zhu, Anthony Bolyos
* Date: April 28, 2015
* Purpose: To play a modern, tweaked version of the game "Commando 2"
* 		   for Commodore 64.
*********************************************************************/
package agentCommando;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;
public class Game extends Canvas {

	
      	private BufferStrategy strategy;   // take advantage of accelerated graphics
        private boolean waitingForKeyPress = true;  // true if game held up until
                                                    // a key is pressed
        private boolean leftPressed = false;  // true if left arrow key currently pressed
        private boolean rightPressed = false; // true if right arrow key currently pressed
        private boolean upPressed = false; // true if up arrow key currently pressed
        private boolean downPressed = false; // true if down arrow key currently pressed
        private boolean firePressed = false; // true if firing
        private boolean grenadePressed = false; // true of grenade button is pressed
        private boolean grenadeExploded = false; // true if grenade has exploded
        private boolean keyPressed = false; // true if any key is pressed
        private boolean enemySpriteSwitch = false; // true if enemy entity switches sprites
        private boolean gameRunning = true; // true if the game is running
        private boolean logicRequiredThisLoop = false; // true if logic
        												// needs to be 
        												// applied this loop

        private ArrayList entities = new ArrayList(); // list of entities in game
        public static ArrayList obstacles = new ArrayList(); // list of obstacle entities in game
        private ArrayList enemies = new ArrayList(); //list of enemy entities in game
        private ArrayList removeEntities = new ArrayList(); // list of entities
                                                            // to remove this loop
        private Entity player;  // the player
        private Entity back;  //the background
        private double moveSpeed = 150; // hor. vel. of player (px/s)
        public static double moveWithBack = 0; // speed to make everything moves with the background
        public static boolean moveEnvironment = false; // true if the background is moving instead of the player
        public static long delta = 0; // time between game loops
        private long lastFire = 0; // time last shot fired
        private long firingInterval = 500; // interval between shots (ms)
        private int grenadeTimer = 0; // timer for grenade to explode
        private int spriteStageC = 0; // counter to switch sprites of player
        private int spriteStageCEnemy; // counter to switch sprites of enemy entities
        private int shotAddX = 0; // add a certain x value to the spawn of the bullet
        private int shotAddY = 0; // add a certain y value to the spawn of the bullet
        private int numOfEnemiesKilled = 0;  // number of enemies killed by the player
        private int numOfEnemiesKilledTemp = 0; // temporarily stores the number of enemies killed by the player
        private int level = 1; //current level of the game
        private int highScore = 0; // stores the high score of the game
        private boolean gameHasLoaded = false; // true if game has started game loop
        private String screen = "sprites/title.png"; // source of the current background image

        
    	/*
    	 * Construct our game and set it running.
    	 */
    	public Game() {
    		// create a frame to contain game
    		JFrame container = new JFrame("Agent Commando");
    
    		// get hold the content of the frame
    		JPanel panel = (JPanel) container.getContentPane();
    
    		// set up the resolution of the game
    		panel.setPreferredSize(new Dimension(800,600));
    		panel.setLayout(null);
    
    		// set up canvas size (this) and add to frame
    		setBounds(0,0,800,600);
    		panel.add(this);
    
    		// Tell AWT not to bother repainting canvas since that will
            // be done using graphics acceleration
    		setIgnoreRepaint(true);
    
    		// make the window visible
    		container.pack();
    		container.setResizable(false);
    		container.setVisible(true);
    
    
            // if user closes window, shutdown game and jre
    		container.addWindowListener(new WindowAdapter() {
    			public void windowClosing(WindowEvent e) {
    				System.exit(0);
    			}// windowClosing
    		});
    
    		// add key listener to this canvas
    		addKeyListener(new KeyInputHandler());
    
    		// request focus so key events are handled by this canvas
    		requestFocus();
    
    		// create buffer strategy to take advantage of accelerated graphics
    		createBufferStrategy(2);
    		strategy = getBufferStrategy();
    
    		// initialize entities
    		initEntities();
    
    		// start the game
    		gameLoop();
        } // constructor
    

	/*
	 * gameLoop
         * input: none
         * output: none
         * purpose: Main game loop. Runs throughout game play.
         *          Responsible for the following activities:
	 *           - calculates speed of the game loop to update moves
	 *           - moves the game entities
	 *           - draws the screen contents (entities, text)
	 *           - updates game events
	 *           - checks input
	 */
	public void gameLoop() {
		
		gameHasLoaded = true;
		
		long lastLoopTime = System.currentTimeMillis();
		//keep loop running until game ends
		while(gameRunning){
			
			//calculate time since last update to calculate entities movement
			delta = System.currentTimeMillis() - lastLoopTime;
			lastLoopTime = System.currentTimeMillis();
			
			//get graphics context for the accelerated surface and make it black
			Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
			g.setColor(Color.black);
			g.fillRect(0, 0, 800, 600);
			
			
			//update player's location to enemies
			for(int i = 0; i < enemies.size(); i++){
				((Entity)enemies.get(i)).setPlayerX(((Entity)entities.get(1)).getX());
				((Entity)enemies.get(i)).setPlayerY(((Entity)entities.get(1)).getY());
			}//for
			//move each entity
			
			//move all entities
			if (!waitingForKeyPress){
				spriteStageCEnemy++;
				for(int i = 0; i < entities.size(); i++){
					Entity entity = (Entity) entities.get(i);
					
					//Switch enemy sprites
					if(entity instanceof GrayEntity && spriteStageCEnemy == 15){
						enemySpriteSwitch = true;
						entity.setSpriteStage();
					}//if
					
					entity.move(delta);
				}//for
				
				//reset sprite switch
				if(enemySpriteSwitch){
					enemySpriteSwitch = false;
					spriteStageCEnemy = 0;
				}//if
			}//if
			
			//if waiting for any key press, draw new background
			if (waitingForKeyPress){
				back = new BackgroundEntity(this, screen, 0, 0);
				entities.add(back);
			}//if
			
			//draw all entities
			for(int i = 0; i < entities.size(); i++){
				Entity entity = (Entity) entities.get(i);
				entity.draw(g);
			}//for
			
			//draw number of enemies killed
			if(screen.equals("sprites/gameOver.png") && waitingForKeyPress){
				g.setColor(Color.white);
				g.setFont(new Font("Agency FB", Font.PLAIN, 30)); 
				g.drawString("You killed " + numOfEnemiesKilled + " enemies.", 280, 340);
				g.drawString("Highscore: " + highScore + " enemies.", 280, 390);
			} else if (!waitingForKeyPress){
				g.setColor(Color.white);
				g.setFont(new Font("Agency FB", Font.PLAIN, 30)); 
				g.drawString("You've killed " + numOfEnemiesKilled + " enemies.", 570, 590);
			}//else if
			
			//check collisions by brute force (inefficient)
			for(int i = 0; i < entities.size(); i++){
				for(int j = i + 1; j < entities.size(); j++){
					Entity me = (Entity)entities.get(i);
					Entity him = (Entity)entities.get(j);
					
					if (me.collidesWith(him)){
						me.collidedWith(him);
						him.collidedWith(me);
					}//if
				}//for
			}//for
			
			//remove dead entities
			entities.removeAll(removeEntities);
			enemies.removeAll(removeEntities);
			removeEntities.clear();
			
			
			if(firePressed){
				tryToFire();
			}//if
			
			if(grenadePressed){
				throwGrenade();
			}//if
			if(grenadeExploded){
				grenadeTimer++;
				if(grenadeTimer == 80){
					grenadeExploded = false;
				}//if
			}//if
			
			//clear graphics & flip buffer
			g.dispose();
			strategy.show();
			
			//player should not move w/o user input
			moveWithBack = 0;
			player.setHorizontalMovement(0);
			player.setVerticalMovement(0);
			
			//respond to user moving player
			if((leftPressed) && (!rightPressed)){
				//back.setHorizontalMovement(moveSpeed);
				keyPressed = true;
				player.setHorizontalMovement(-moveSpeed);
				
			}else if((rightPressed) && (!leftPressed)){
				//back.setHorizontalMovement(-moveSpeed);
				player.setHorizontalMovement(moveSpeed);
				keyPressed = true;
			}//else
			if((downPressed) && (!upPressed)){
				//back.setVerticalMovement(-moveSpeed);
				player.setVerticalMovement(moveSpeed);
				keyPressed = true;
			}//if
			else if((upPressed) && (!downPressed)){
				player.setVerticalMovement(-moveSpeed);
				moveWithBack =(moveEnvironment)?moveSpeed:0;
				keyPressed = true;
			}//if
			
			//change the sprite if a key is pressed
			if(keyPressed){
				keyPressed = false;
				spriteStageC++;
				if(spriteStageC == 8){
					spriteStageC = 0;
					player.setSpriteStage();
				}//if
			}//if
			
			//pause
			try{ Thread.sleep(10); } catch (Exception e) {}
			
		}//while
		
	} // gameLoop

	//try to spawn a shot when the shoot button is pressed
	public void tryToFire() {
		
		// check that we've waiting long enough to fire
		if((System.currentTimeMillis() - lastFire) <  firingInterval) {
			return;
		}//if
		
		//otherwise add a shot
		lastFire = System.currentTimeMillis();
		
		//add x and y values to make shot spawn at glasses
		switch(player.getDirection()){
			case "N": shotAddX = 22; shotAddY = 5;break;
			case "S": shotAddX = 24; shotAddY = 30;break;
			case "W": shotAddX = 12; shotAddY = 25;break;
			case "E": shotAddX = 40; shotAddY = 25;break;
			case "NW": shotAddX = 22; shotAddY = 5;break;
			case "NE": shotAddX = 22; shotAddY = 5;break;
			case "SW": shotAddX = 24; shotAddY = 30;break;
			case "SE": shotAddX = 24; shotAddY = 30;break;
		}//switch
		PlayerShot shot = new PlayerShot(this, "sprites/bullet.png", player.getX() + shotAddX, player.getY() + shotAddY, player);
		entities.add(2,shot);
		
	}//tryToFire
	
	//add a enemy shot to the game
	public void addEnemyFire(Entity soldier) {
		EnemyShot shot = new EnemyShot(this, "sprites/bullet.png", soldier.getX() + 10, soldier.getY() + 30, soldier);
		entities.add(3,shot);
	}//tryToFire
	
	//try to throw a grenade
	public void throwGrenade(){
		
		//checks to see if grenade can be thrown
		if((System.currentTimeMillis() - lastFire) <  firingInterval * 5) {
			return;
		}//if
	
		//otherwise add a shot
		lastFire = System.currentTimeMillis();
		Grenade grenade = new Grenade(this, "sprites/grenade.png", player.getX() + 10, player.getY() - 30, player);
		entities.add(grenade);
	}//throwGrenade
	
	//activates the explosion of the grenade
	public void grenadeActivated(double x, double y) {
    	grenadeExploded = true;
		entities.add(new GrenadeExplosion(this, "sprites/explosion.png", (int)x - 48, (int)y - 35, player));
    }//grenadeActivated
    
	//returns the number of game loops that have occurred to activate the explosion
    public int getGrenadeTimer(){
    	return grenadeTimer;
    }//getGrenadeTimer
    
    //restart the grenade timer
    public void restartGrenadeTimer(){
    	grenadeTimer = 0;
    }//restartGrenadeTimer
	
    //add all of the entities in the game to the field
	public void initEntities(){
	
		//create background & start in middle
		back = new BackgroundEntity(this, "sprites/back2.png", 0, -7400);
		entities.add(back);
		
		//create the player & put in center of screen
		player = new PlayerEntity(this, "sprites/shipS1.png", 375, 500);
		entities.add(player);
		
		//add all entities for the stage
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 500, 20, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
	
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 500, -100, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
	
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 20, 50, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
		
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 352, 100, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
		
		entities.add(new TreeEntity(this, "sprites/boulder.png", 0, -800));
		obstacles.add(entities.get(entities.size() - 1));
		
		entities.add(new TreeEntity(this, "sprites/boulder.png", 350, -800));
		obstacles.add(entities.get(entities.size() - 1));
		
		entities.add(new TreeEntity(this, "sprites/boulder.png", 700, -800));
		obstacles.add(entities.get(entities.size() - 1));
		
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 110, -680, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
	
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 310, -680, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
	
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 510, -680, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
		
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 500, -980, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
	
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 500, -1100, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
	
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 0, -1200, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
		
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 20, -950, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
		
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 450, -980, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
	
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 214, -1100, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
	
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 23, -1200, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
	
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 700, -950, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
	
		entities.add(new TreeEntity(this, "sprites/boulder.png", 250, -1800));
		obstacles.add(entities.get(entities.size() - 1));
	
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 100, -2100, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
	
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 300, -2100, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
	
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 500, -2100, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
	
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 700, -2100, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
		
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 100, -2500, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
	
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 300, -2500, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
	
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 500, -2500, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
	
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 700, -2500, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
	
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 100, -2900, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
	
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 300, -2900, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
	
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 500, -2900, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
	
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 700, -2900, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
		
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 100, -3400, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
	
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 300, -3400, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
	
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 500, -3400, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
	
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 700, -3400, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
	
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 400, -3980, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
	
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 324, -4150, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
	
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 710, -4200, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
		
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 256, -3950, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
		
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 567, -3900, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
	
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 492, -4100, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
	
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 623, -4230, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
		
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 0, -3950, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
	
		entities.add(new TreeEntity(this, "sprites/boulder.png", 250, -4550));
		obstacles.add(entities.get(entities.size() - 1));
		
		entities.add(new TreeEntity(this, "sprites/boulder.png", 650, -4500));
		obstacles.add(entities.get(entities.size() - 1));
	
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 500, -4980, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
	
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 500, -5100, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
	
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 00, -5200, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
	
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 20, -4950, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
		
		entities.add(new TreeEntity(this, "sprites/boulder.png", 250, -5800));
		obstacles.add(entities.get(entities.size() - 1));
	
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 100, -6000, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
	
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 250, -6000, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
	
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 400, -6000, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
	
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 550, -6000, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
		
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 700, -6000, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
	
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 100, -6500, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
	
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 250, -6500, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
	
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 400, -6500, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
	
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 550, -6500, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
		
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 700, -6500, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
		
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 100, -6700, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
	
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 250, -6700, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
	
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 400, -6700, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
	
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 550, -6700, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
		
		entities.add(new GrayEntity(this, "sprites/shipS1.png", 700, -6700, ((Entity)entities.get(1)).getX(), ((Entity)entities.get(1)).getY()));
		enemies.add(entities.get(entities.size() - 1));
		
		entities.add(new TreeEntity(this, "sprites/tree.png", 660, -500));
		entities.add(new TreeTop(this, "sprites/treeTop.png", 643, -655));
		obstacles.add(entities.get(entities.size() - 2));
		
		entities.add(new TreeEntity(this, "sprites/tree.png", 225, -980));
		entities.add(new TreeTop(this, "sprites/treetop.png", 207, -1135));
		obstacles.add(entities.get(entities.size() - 2));

		entities.add(new TreeEntity(this, "sprites/tree.png", 500, -1200));
		entities.add(new TreeTop(this, "sprites/treeTop.png", 483, -1355));
		obstacles.add(entities.get(entities.size() - 2));
		
		entities.add(new TreeEntity(this, "sprites/tree.png", 225, 100));
		entities.add(new TreeTop(this, "sprites/treetop.png", 207, -55));
		obstacles.add(entities.get(entities.size() - 2));
		
		entities.add(new TreeEntity(this, "sprites/tree.png", 500, -200));
		entities.add(new TreeTop(this, "sprites/treeTop.png", 483, -355));
		obstacles.add(entities.get(entities.size() - 2));
		
		entities.add(new TreeEntity(this, "sprites/tree.png", 650, 100));
		entities.add(new TreeTop(this, "sprites/treeTop.png", 633, -55));
		obstacles.add(entities.get(entities.size() - 2));
		
		entities.add(new TreeEntity(this, "sprites/tree.png", 225, -3900));
		entities.add(new TreeTop(this, "sprites/treetop.png", 207, -4055));
		obstacles.add(entities.get(entities.size() - 2));
	
		entities.add(new TreeEntity(this, "sprites/tree.png", 500, -4200));
		entities.add(new TreeTop(this, "sprites/treeTop.png", 483, -4355));
		obstacles.add(entities.get(entities.size() - 2));
		
		entities.add(new TreeEntity(this, "sprites/tree.png", 30, -4030));
		entities.add(new TreeTop(this, "sprites/treetop.png", 13, -4185));
		obstacles.add(entities.get(entities.size() - 2));
	
		entities.add(new TreeEntity(this, "sprites/tree.png", 50, -4300));
		entities.add(new TreeTop(this, "sprites/treeTop.png", 33, -4455));
		obstacles.add(entities.get(entities.size() - 2));
		
		entities.add(new TreeEntity(this, "sprites/tree.png", 50, -4700));
		entities.add(new TreeTop(this, "sprites/treetop.png", 33, -4855));
		obstacles.add(entities.get(entities.size() - 2));
		
		entities.add(new TreeEntity(this, "sprites/tree.png", 350, -4700));
		entities.add(new TreeTop(this, "sprites/treetop.png", 333, -4855));
		obstacles.add(entities.get(entities.size() - 2));
		
		entities.add(new TreeEntity(this, "sprites/tree.png", 650, -4700));
		entities.add(new TreeTop(this, "sprites/treetop.png", 633, -4855));
		obstacles.add(entities.get(entities.size() - 2));
		
	}//initEntities
	
	//let the game know that player has died
	public void notifyDeath(){
		level = 1; //go back to level one
		screen = "sprites/gameOver.png"; //print the game over screen
		
		//reset all playing variables
		leftPressed = false;
    	rightPressed = false;
    	upPressed = false;
    	downPressed = false;
    	firePressed = false;
		waitingForKeyPress = true;
		numOfEnemiesKilledTemp = 0;
		getHighScore(); //get the high score from a text file
	}//notifyDeath
	
	//increment to the number of enemies killed
	public void addEnemiesKilled () {                
		numOfEnemiesKilled++;
	}//addEnemiesKilled
	
	//add removed entities to another Arraylist to be removed later
	public void removeEntity(Entity entity){
		removeEntities.add(entity);
	}//removeEntity
	
	//get the current level of the game
	public int getLevel() {
		return level;
	}//getLevel
	
	//add one level to the game
	public void levelUp(){
		level++;
		screen = "sprites/levelUp.png"; //change screen to level up screen
		leftPressed = false;
    	rightPressed = false;
    	upPressed = false;
    	downPressed = false;
    	firePressed = false;
		waitingForKeyPress = true;
		numOfEnemiesKilledTemp = numOfEnemiesKilled; //save the number of enemies killed for next round.
	}//levelUp
	
	/* startGame
    * input: none
    * output: none
    * purpose: start a fresh game, clear old data
    */
    private void startGame() {
    	
    	//clear existing entities & initialize a new set
    	entities.clear();
    	obstacles.clear();
    	enemies.clear();
    	
    	initEntities(); //initialize all entities
    	
    	//blank out any keyboard settings that may exist
    	leftPressed = false;
    	rightPressed = false;
    	upPressed = false;
    	downPressed = false;
    	firePressed = false;
    	grenadeTimer = 0;
    	
    	//get the number of enemies killed from the previous level
    	numOfEnemiesKilled = numOfEnemiesKilledTemp; 
    	
    }// startGame

    //get the high score of the game from a text file
    public void getHighScore () {
         try {

             //input
             BufferedReader in = new BufferedReader(new FileReader("highScore.txt"));
             in.mark(Short.MAX_VALUE);

             //read in high score
             highScore = Integer.parseInt(in.readLine());
             
             //if current score is greater than high score, overwrite the high score
             if(numOfEnemiesKilled > highScore){
            	 try {
                     BufferedWriter out = new BufferedWriter(new FileWriter("highScore.txt"));

                     //write highScore to file
                     out.write(numOfEnemiesKilled + "");
                     out.close();

                 } catch (Exception e) {
                     System.out.println("File Output Error");
                 }//catch
             }//if
             in.close();
         } catch (Exception e) {
             System.out.println("File Input Error");
         } //catch

    } //getHighScore
    
    /* inner class KeyInputHandler
    * handles keyboard input from the user
    */
	private class KeyInputHandler extends KeyAdapter {

        private int pressCount = 1;  // the number of key presses since
                                              // waiting for 'any' key press

        /* The following methods are required
        * for any class that extends the abstract
        * class KeyAdapter.  They handle keyPressed,
        * keyReleased and keyTyped events.
        */
        
        //turns player control booleans true if the keys are pressed
		public void keyPressed(KeyEvent e) {

			//if waiting for key press to start game, do nothing
			if(waitingForKeyPress){
				return;
			}//if
			
			//respond to left, right, fire, and throw grenade
			if(e.getKeyCode() == KeyEvent.VK_A){
				leftPressed = true;
			}//if
			
			if(e.getKeyCode() == KeyEvent.VK_D){
				rightPressed = true;
			}//if
			
			if(e.getKeyCode() == KeyEvent.VK_W){
				upPressed = true;
			}//if
			
			if(e.getKeyCode() == KeyEvent.VK_S){
				downPressed = true;
			}//if
			
			
			if(e.getKeyCode() == KeyEvent.VK_COMMA){
				firePressed = true;
			}//if
			
			if(e.getKeyCode() == KeyEvent.VK_PERIOD){
				grenadePressed = true;
			}//if

		}//keyPressed
		
		//turns the player control booleans false if keys are released
		public void keyReleased(KeyEvent e) {

			//if waiting for key press to start game, do nothing
			if(waitingForKeyPress){
				return;
			}//if
			
			//respond to move or fire
			if(e.getKeyCode() == KeyEvent.VK_A){
				leftPressed = false;
			}//if
			
			if(e.getKeyCode() == KeyEvent.VK_D){
				rightPressed = false;
			}//if
			
			if(e.getKeyCode() == KeyEvent.VK_W){
				upPressed = false;
			}//if
			
			if(e.getKeyCode() == KeyEvent.VK_S){
				downPressed = false;
			}//if
			
			
			
			if(e.getKeyCode() == KeyEvent.VK_COMMA){
				firePressed = false;
			}//if
			
			if(e.getKeyCode() == KeyEvent.VK_PERIOD){
				grenadePressed = false;
			}//if
			
		}//keyReleased
		
		//handles the special key presses
 	    public void keyTyped(KeyEvent e) {
 	    	if(e.getKeyChar() == 32 && !gameHasLoaded){
 	    		return;
 	    	}//if
 	    	
 	    	//if waiting for key press to start game
 	    	if(e.getKeyChar() == 44 && waitingForKeyPress){
 	    		if(pressCount == 1){
 	    			waitingForKeyPress = false;
 	    			startGame();
 	    			pressCount = 0;
 	    		}else{
 	    			pressCount++;
 	    		}//else
 	    	}//if
 	    	
 	    	//if escape is pressed, end game
 	    	if(e.getKeyChar() == 27){
 	    		System.exit(0);
 	    	}//if

		}//keyTyped

	}//class KeyInputHandler
	

	/**
	 * Main Program
	 */
	public static void main(String [] args) {
		new Game();
	}//main
}//Game
