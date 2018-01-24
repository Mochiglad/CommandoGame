/*****************************************************************************************
 
 * TreeTop.java
 * Name: Anthony Bolyos, Steven Zhu
 * Date: April 29, 2015
 * Purpose: stores properties for the tree tops.
 
 *****************************************************************************************/
package agentCommando;

import agentCommando.Entity;
import agentCommando.Game;

public class TreeTop extends Entity {

	private Game game;  //the game in which the tree top exists
	
	//construct tree top entity
	TreeTop(Game game, String ref, int newX, int newY){
		
		super(ref, newX, newY);
		this.dx = 0;  //prevents unnecessary movement
		this.dy = 0;  // ""
		this.game = game;
		
	}//TreeTop
	
	//get the x coordinate of where the image stops
	public double getMaxX(){
		return this.x + 50;
	}//getMaxX
	
	//get the y coordinate of where the image stops
	public double getMaxY(){
		return this.y + 50;
	}//getMaxY
	
	//no collisions
	public void collidedWith(Entity other){}//collidedWith
	
	//move tree top
	public void move(long delta){
				
		super.move(delta);//call super move method
		
	}//move
	
}//ShipEntity