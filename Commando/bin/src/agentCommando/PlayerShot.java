/*****************************************************************************************
 
 * PlayerShot.java
 * Name: Anthony Bolyos, Steven Zhu
 * Date: April 29, 2015
 * Purpose: stores properties for the shot from the player
 
 *****************************************************************************************/
package agentCommando;

import agentCommando.PlayerEntity;
import agentCommando.GrayEntity;

public class PlayerShot extends Entity{
	
	private Game game; //the game in which the shot exists
	private double moveSpeed = 700; //movement speed of the shot
	private boolean used = false; //true if the shot has been used
	private int xI = 0; //initial x coordinate of the shot
	private int yI = 0; //initial y coordinate of the shot
	
	//constructor
	public PlayerShot(Game g, String r, int newX, int newY, Entity player) {
		
		super(r, newX, newY); //calls Entity constructor
		game = g;
		dx = player.getHorizontalMovement();
		dy = player.getVerticalMovement();
		xI = player.getX();
		yI = player.getY();
		direction = player.getDirection();
		switch(direction){
			case "N": dx = 0; dy = -moveSpeed; break;
			case "S": dx = 0; dy = moveSpeed; break;
			case "W": dx = -moveSpeed; dy = 0; break;
			case "E": dx = moveSpeed; dy = 0; break;
			case "NW": dx = -moveSpeed; dy = -moveSpeed; break;
			case "NE": dx = moveSpeed; dy = -moveSpeed; break;
			case "SE": dx = moveSpeed; dy = moveSpeed;;break;
			case "SW": dx = -moveSpeed; dy = moveSpeed; break;
		}//switch
	}//constructor
	
	//move the shot
	public void move (long delta) {

		super.move(delta); //call super class move method
		
		//if shot has travelled a distance of 300px, remove it
		if(calcDistance() >= 300){
			game.removeEntity(this);
		}//if
		
	} //move
	
	//calculate the distance the shot has travelled
	public int calcDistance () {
		return (int) Math.sqrt(Math.pow((x - xI),2) + Math.pow((y - yI),2));
	}//calcDistance
	
	//deals with shot collisions
	public void collidedWith(Entity other) {
		
		//enemy entity collision
		if(other instanceof GrayEntity){
			game.removeEntity(other);
			game.addEnemiesKilled();
		}//if
		
		//tree entity collision
		if(other instanceof TreeEntity){
			game.removeEntity(this);
		}//if
	}//collidedWith
}//PlayerShot