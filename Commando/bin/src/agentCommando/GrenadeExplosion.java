/*****************************************************************************************
 
 * GrenadeExplosion.java
 * Name: Anthony Bolyos, Steven Zhu
 * Date: April 29, 2015
 * Purpose: stores properties of the grenade explosion.
 
 *****************************************************************************************/
package agentCommando;

import agentCommando.PlayerEntity;
import agentCommando.GrayEntity;

public class GrenadeExplosion extends Entity{
	private Game game; // the game in which the explosion exists
	
	//constructor for the explosion
	public GrenadeExplosion(Game g, String r, int newX, int newY, Entity player) {
		super(r, newX, newY); //calls Entity constructor
		game = g;
		// TODO Auto-generated constructor stub
	}//GrenadeExplosion
	
	//move the explosion
	public void move (long delta) {
		super.move(delta);
		dx = 0;
		
		//remove the explosion after a certain amount of time
		if(game.getGrenadeTimer() == 80){
			game.removeEntity(this);
			game.restartGrenadeTimer();
		}//if
		
	}//move
	
	//finds if the explosion collides with anything
	public void collidedWith(Entity other) {
		
		if(other instanceof GrayEntity){
			game.removeEntity(other);
			game.addEnemiesKilled();
		}//if
	}//collidedWith
}//GrenadeExplosion