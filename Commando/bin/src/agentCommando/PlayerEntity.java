/*****************************************************************************************
 
 * PlayerEntity.java
 * Name: Anthony Bolyos, Steven Zhu
 * Date: April 1, 2015
 * Purpose: stores properties for the player entity.
 
 *****************************************************************************************/
package agentCommando;

import agentCommando.Entity;
import agentCommando.Game;

public class PlayerEntity extends Entity {

	private Game game;  //the game in which the player exists
	private boolean stuckAtObstacle; //true if the player is stuck at an obstacle
	private double futureLeft; //future left value
	private double futureRight; //future right value
	private double futureTop; //future top value
	private double futureBottom; //future bottom value
	
	//construct player's player
	PlayerEntity(Game game, String ref, int newX, int newY){
		super(ref, newX, newY);
		this.game = game;
		this.stuckAtObstacle = false;
		this.direction = "N";  //player starts facing up
	}//constructor
	
	//all collisions are dealt with in the other classes
	public void collidedWith(Entity other){
	}//collidedWith
	
	//set the direction of this entity
	private void setDirection(){
		
		//checks for travel in the east direction
		if(dx > 0){
			if(dy < 0){
				direction = "NE";
			}else if(dy > 0){
				direction = "SE";
			}else {
				direction = "E";
			}//else
			setSprite();
		}//if
				
		//checks for travel in the west direction
		else if(dx < 0){
			if(dy < 0){
				direction = "NW";
			}else if(dy > 0){
				direction = "SW";
			}else {
				direction = "W";
			}//else
			setSprite();
		}//if
				
		//checks for travel in the north or south direction
		else if(dx == 0){
			if(dy > 0){
				direction = "S";
			}else if(dy < 0){
				direction = "N";
			}//else if
			setSprite();
		}//else
				
	}//setDirection
	
	//change the sprite image
	public void setSpriteStage () {
		if(spriteStage == 9){
			spriteStage = 1;
		}//if
		spriteStage++;
	}//setSpriteStage
	
	
	//move player
	public void move(long delta){
		
		setDirection();//set the direction of the player
		futureTop = y + dy * delta * Math.pow(10, -3);
		futureBottom = y + dy * delta * Math.pow(10, -3) + 50;
		futureRight = x + dx * delta * Math.pow(10, -3) + 50;
		futureLeft = x + dx * delta * Math.pow(10, -3);
		
		Game.moveEnvironment = false;//background doesn't move
		
		stuckAtObstacle = false;//player is not stuck at obstacle
		
		//stop at any obstacles
		for(int i = 0; i < game.obstacles.size(); i++){
			//at left of obstacle
			if(futureLeft < ((TreeEntity)game.obstacles.get(i)).getMaxX() && futureLeft > ((TreeEntity)game.obstacles.get(i)).getX()){	
				
				//only if beside obstacle, stop
				if(futureBottom > ((TreeEntity)game.obstacles.get(i)).getY() && futureTop < ((TreeEntity)game.obstacles.get(i)).getMaxY()){
					dx = 0;
				}//if
					
			}//if
			
			//at right of obstacle
			else if(futureRight < ((TreeEntity)game.obstacles.get(i)).getMaxX() && futureRight > ((TreeEntity)game.obstacles.get(i)).getX()){
				
				//only if beside obstacle, stop
				if(futureBottom > ((TreeEntity)game.obstacles.get(i)).getY() && futureTop < ((TreeEntity)game.obstacles.get(i)).getMaxY()){
					dx = 0;
				}//if
			
			}//else if
			
			//at top of obstacle
			if(futureBottom < ((TreeEntity)game.obstacles.get(i)).getMaxY() - 10 && futureBottom > ((TreeEntity)game.obstacles.get(i)).getY()){
				
				//only if beside the obstacle, stop
				if(futureRight > ((TreeEntity)game.obstacles.get(i)).getX() && futureLeft < ((TreeEntity)game.obstacles.get(i)).getMaxX()){
					dy = 0;
				}//if
				
			}//if
			
			//at bottom of obstacle
			else if(futureTop < ((TreeEntity)game.obstacles.get(i)).getMaxY() && futureTop > ((TreeEntity)game.obstacles.get(i)).getY()){
				
				//only if beside the obstacle, stop
				if(futureRight > ((TreeEntity)game.obstacles.get(i)).getX() && futureLeft < ((TreeEntity)game.obstacles.get(i)).getMaxX()){
					dy = 0;
					stuckAtObstacle = true;
				}//if
			
			}//else if
			
			//change the sprite image
			setSprite();
		}//for
		
		//stop at left side of screen
		if(futureLeft < 5){
			dx = 0;
		}//if
		
		//stop at right side of screen
		if(futureRight > 795){
			dx = 0;
		}//if
		
		//stop at middle of screen
		if(futureTop < 400){
			dy = 0;
			Game.moveEnvironment = (stuckAtObstacle)?false:true;
		}//if
		
		//stop at bottom of screen
		if(futureBottom > 595){
			dy = 0;
		}//if
		
		//change the coordinates of the player
		x += dx * delta * Math.pow(10, -3);
		y += dy * delta * Math.pow(10, -3);
	}//move
	
	//change the sprite image
	public void setSprite () {
		this.sprite = (SpriteStore.get()).getSprite("sprites/ship" + direction + spriteStage + ".png");
	}//setSprite
	
	//return the direction of this entity
	public String getDirection () {
		return direction;
	}//getDirection
	
}//PlayerEntity