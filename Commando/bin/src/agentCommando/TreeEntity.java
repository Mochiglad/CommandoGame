/*****************************************************************************************
 
 * TreeEntity.java
 * Name: Anthony Bolyos, Steven Zhu
 * Date: April 29, 2015
 * Purpose: stores properties of the tree bottom.
 
 *****************************************************************************************/
package agentCommando;

import agentCommando.Entity;
import agentCommando.Game;

public class TreeEntity extends Entity {

	private Game game;  //the game in which the tree exists
	
	//constructor for tree
	TreeEntity(Game game, String ref, int newX, int newY){
		
		super(ref, newX, newY);
		this.dx = 0;  //prevents unnecessary movement
		this.dy = 0;  //prevents unnecessary movement
		this.game = game;
		
	}//TreeEntity
	
	//get the x coordinate of where the image stops
	public double getMaxX(){
		return this.x + this.sprite.getWidth();
	}//getMaxX
	
	//get the y coordinate of where the image stops
	public double getMaxY(){
		return this.y + this.sprite.getHeight();
	}//getMaxY
	
	//all collisions are dealt with in other classes
	public void collidedWith(Entity other){}//collidedWith
	
	//move player
	public void move(long delta){
				
		super.move(delta);
		
	}//move
	
}//TreeEntity