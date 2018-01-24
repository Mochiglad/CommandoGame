package agentCommando;

import agentCommando.Entity;
import agentCommando.Game;

public class BackgroundEntity extends Entity {

	private Game game;  //the game in which the player exists
	
	//construct the background of the game
	BackgroundEntity(Game game, String ref, int newX, int newY){
		super(ref, newX, newY);
		this.dx = 0;  //prevents unnecessary movement
		this.dy = 0;  // ""
		this.game = game;
	}//BackgroundEntity
	
	public void collidedWith(Entity other){}//collidedWith
	
	//move background when necessary
	public void move(long delta){
				
		//stop at top side of environment & level up
		if((y + dy) > 0){
			game.levelUp();
			return;
		}//if
		
		super.move(delta);  //move the background
		
	}//move
	
}//BackgroundEntity