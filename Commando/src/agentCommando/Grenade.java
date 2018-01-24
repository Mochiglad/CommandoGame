/*****************************************************************************************
 
 * Grenade.java
 * Name: Anthony Bolyos, Steven Zhu
 * Date: April 29, 2015
 * Purpose: stores the properties of the grenade entity.
 
 *****************************************************************************************/
package agentCommando;

import agentCommando.PlayerEntity;
import agentCommando.GrayEntity;

public class Grenade extends Entity{
	private Game game; //game in which the grenade exists
	private double moveSpeed = 200; //movement speed of the grenade
	private int xI = 0; //initial x coordinate
	private int yI = 0; //initial y coordinate
	
	//constructor
	public Grenade(Game g, String r, int newX, int newY, Entity player) {
		
		//give all the grenade variables values
		super(r, newX, newY); //calls Entity constructor
		game = g;
		dy = -moveSpeed;
		xI = player.getX();
		yI = player.getY();
	}//constructor
	
	//move the grenade
	public void move (long delta) {
		
		super.move(delta); //call entity's move
		
		//if the grenade travels 200px, explode
		if(calcDistance() >= 200){
			game.removeEntity(this);
			game.grenadeActivated(x , y);
			
		}//if
		
	}//move
	
	//calculate the distance
	public int calcDistance () {
		return (int) Math.sqrt(Math.pow((x - xI),2) + Math.pow((y - yI),2));
	}//calcDistance
	
	//no collisions, only the explosion collides with enemies
	public void collidedWith(Entity other) {
	}//collidedWith
}//Grenade