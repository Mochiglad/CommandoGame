/*****************************************************************************************
 
 * GrayEntity.java
 * Name: Anthony Bolyos, Steven Zhu
 * Date: April 29, 2015
 * Purpose: Stores the properties of the enemy entities.
 
 *****************************************************************************************/
package agentCommando;

import agentCommando.Entity;
import agentCommando.Game;

import java.util.Arrays;

public class GrayEntity extends Entity{

	private double moveSpeed = 50; //the movement speed of the enemy
	private double futureX; //the x value of the entity one game loop in the future
	private double futureY; //the y value of the entity one game loop in the future
	private double [] distances; //the distances for the enemy to travel to player
	private double futureTop; //future top of the obstacle
	private double futureBottom; //future bottom of the obstacle
	private double futureLeft; //future left of the obstacle
	private double futureRight; //future right of the obstacle
	private boolean stuckX; //true if player is stuck at X
	private boolean stuckY; //true if player is stuck at Y
	private double initX; //initial x value of the entity
	private double initY; //initial y value of the entity
	private int bestDirection; //best direction for the enemy to travel to player
	private int randomDirection; //set a random direction
	private Game game; //the game in which the entity exists
	
	//constructor
	public GrayEntity(Game g, String r, int newX, int newY, int playerX, int playerY){
		
		super(r, newX, newY); //call the super class constructor
		this.playerX = playerX;
		this.playerY = playerY;
		this.distances = new double [8];
		this.direction = "S";
		this.game = g;
		moveSpeed = 50 + (game.getLevel() * 30);
		
	}//GrayEntity
	
	//move in a specified direction
	public void setDirection(int i){
		
		switch(i){
		
			case 1: dy = -moveSpeed;
					dx = 0;				
					direction = "N";
					break;
			case 2: dy = -moveSpeed;
					dx = moveSpeed;		
					direction = "NE";//NE
					break;
			case 3: dy = 0;
					dx = moveSpeed;
					direction = "E";//E
					break;
			case 4: dy = moveSpeed;
					dx = moveSpeed;
					direction = "SE";//SE
					break;
			case 5: dy = moveSpeed;
					dx = 0;		
					direction = "S";//S
					break;
			case 6: dy = moveSpeed;		
					dx = -moveSpeed;	
					direction = "SW";//SW
					break;
			case 7: dy = 0;
					dx = -moveSpeed;	
					direction = "W";//W
					break;
			case 8: dy = -moveSpeed;
					dx = -moveSpeed; 
					direction = "NW";//NW
					break;
			default: System.out.println("ERROR - " + this + " direction is not set!");
		
		}//switch
	
	}//setRandomDirection
	
	//set a random direction
	public void wander(){
		
		randomDirection = 1 + (int)(Math.random()*8);  //1-8
		setDirection(randomDirection);
		
	}//wander
	
	//set direction toward player
	public void chasePlayer(){
		
		//calculate all possible distances
		for(int i = 0; i < distances.length; i++){
			
			setDirection(i + 1);
			futureX = x + dx * game.delta * Math.pow(10, -3);
			futureY = y + dy * game.delta * Math.pow(10, -3);
			distances[i] = calcDistance();
			
		}//for
		
		bestDirection = 1;
		
		//find least distance
		for(int i = 0; i < distances.length; i++){
			
			bestDirection = (distances[i] < distances[bestDirection - 1])? i+1 : bestDirection;
			
		}//for
		
		setDirection(bestDirection);
		
	}//chasePlayer
	
	//check if the entity is stuck
	private void checkStuck(){
		
		if(stuckX || stuckY){
			stuckX = (distanceTravelled() > 100)?false:true;
			stuckY = (distanceTravelled() > 100)?false:true;
		} //if
		
	}//checkStuck
	
	//try to fire a shot
	private void tryToFire(){
		
		int i = 1 + (int)(Math.random()*100);  //1-100
		if(i == 4){
			game.addEnemyFire(this);
		}//if
		
	}//tryToFire
	
	//calculate distance from player
	private double calcDistance(){
		
		return Math.sqrt(Math.pow((futureY - playerY), 2) + Math.pow((futureX - playerX), 2));
	
	}//calcDistance
	
	//distance travelled since getting stuck at obstacle
	private double distanceTravelled(){
		
		return Math.sqrt(Math.pow((initY - y), 2) + Math.pow((initX - x), 2));
		
	}//distanceTravelled
	
	//set the direction of the entity
	private void setDirection(){
		
		//checks for travel in the east direction
		if(dx > 0){
			if(dy < 0){
				direction = "NE";
			} else if(dy > 0){
				direction = "SE";
			} else {
				direction = "E";
			} //else
			setSprite();
		} //if
				
		//checks for travel in the west direction
		else if(dx < 0){
			if(dy < 0){
				direction = "NW";
			} else if(dy > 0){
				direction = "SW";
			} else {
				direction = "W";
			} //else
			setSprite();
		} //if
				
		//checks for travel in the north or south direction
		else if(dx == 0){
			if(dy > 0){
				direction = "S";
			} else if(dy < 0){
				direction = "N";
			} //else if
			setSprite();
		} //else
				
	}//setDirection
	
	//move this entity
	public void move (long delta){
		
		//get all of the future coordinates
		futureTop = y + dy * delta * Math.pow(10, -3);
		futureBottom = y + dy * delta * Math.pow(10, -3) + 50;
		futureRight = x + dx * delta * Math.pow(10, -3) + 50;
		futureLeft = x + dx * delta * Math.pow(10, -3);
		
		//chase the player and fire
		if(y > -50 && y < 600){
			checkStuck();
			if(!stuckX && !stuckY){
				chasePlayer();
			}//if
			tryToFire();
		} else {
			dx = 0;
			dy = 0;
		}//else
		
		//stop at any obstacles
		for(int i = 0; i < game.obstacles.size(); i++){
			
			//at left of obstacle
			if(futureLeft < ((TreeEntity)game.obstacles.get(i)).getMaxX() && futureLeft > ((TreeEntity)game.obstacles.get(i)).getX()){	
				
				//only if beside obstacle, stop
				if(futureBottom > ((TreeEntity)game.obstacles.get(i)).getY() && futureTop < ((TreeEntity)game.obstacles.get(i)).getMaxY()){
					dx *= -1;
					stuckX = true;
					initX = x;
				}//if
			}//if
			
			//at right of obstacle
			if(futureRight < ((TreeEntity)game.obstacles.get(i)).getMaxX() && futureRight > ((TreeEntity)game.obstacles.get(i)).getX()){
				
				//only if beside obstacle, stop
				if(futureBottom > ((TreeEntity)game.obstacles.get(i)).getY() && futureTop < ((TreeEntity)game.obstacles.get(i)).getMaxY()){
					dx *= -1;
					stuckX = true;
					initX = x;
				}//if
			}//if
			
			//at top of obstacle
			if(futureBottom < ((TreeEntity)game.obstacles.get(i)).getMaxY() && futureBottom > ((TreeEntity)game.obstacles.get(i)).getY()){
				
				//only if beside the obstacle, stop
				if(futureRight > ((TreeEntity)game.obstacles.get(i)).getX() && futureLeft < ((TreeEntity)game.obstacles.get(i)).getMaxX()){
					dy *= -1;
					stuckY = true;
					initY = y;
				}//if
			}//if
			
			//at bottom of obstacle
			if(futureTop < ((TreeEntity)game.obstacles.get(i)).getMaxY() && futureTop > ((TreeEntity)game.obstacles.get(i)).getY()){
				
				//only if beside the obstacle, stop
				if(futureRight > ((TreeEntity)game.obstacles.get(i)).getX() && futureLeft < ((TreeEntity)game.obstacles.get(i)).getMaxX()){
					dy *= -1;
					stuckY = true;
					initY = y;
				}//if
			
			}//if
			
		}//for
		
		super.move(delta);
		setSprite();
	}//move
	
	public void setSpriteStage () {
		if(spriteStage == 9){
			spriteStage = 1;
		}//if
		spriteStage++;
	}//setSpriteStage
	
	//deals with collisions for this entity
	public void collidedWith(Entity other){
		
		//kill the player if there's a collision with the player
		if(other instanceof PlayerEntity){
			game.notifyDeath();
		}//if
		
	}//collidedWith
	
	//change the sprite according the spriteStage and direction
	public void setSprite () {
		this.sprite = (SpriteStore.get()).getSprite("sprites/ship" + direction + spriteStage + ".png");
	}//setSprite
	
	//get the direction for this entity
	public String getDirection() {
		return direction;
	}//getDirection
}//GrayEntity
