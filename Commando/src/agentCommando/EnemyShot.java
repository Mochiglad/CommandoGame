package agentCommando;

import agentCommando.PlayerEntity;
import agentCommando.GrayEntity;

public class EnemyShot extends Entity{
	
	private Game game; //the game in which the enemy shot exists
	private double moveSpeed = 200; //movement speed of the shot
	private int xI = 0; //initial x value of the shot
	private int yI = 0; //initial y value of the shot
	
	//constructor
	public EnemyShot(Game g, String r, int newX, int newY, Entity Player) {
		
		super(r, newX, newY); //calls Entity constructor
		
		//give all the variables of the shot values
		game = g;
		dx = Player.getHorizontalMovement();
		dy = Player.getVerticalMovement();
		xI = Player.getX();
		yI = Player.getY();
		direction = Player.getDirection(); //get the direction of the player
		
		//go in different directions according to the enemy direction
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
		
	}//EnemyShot
	
	//move the enemy shot
	public void move (long delta) {

		super.move(delta);//call the super class method
		
		if(calcDistance() >= 300){
			game.removeEntity(this);
		}//if

	}//move
	
	//calculate the distance travelled
	public int calcDistance () {
		return (int) Math.sqrt(Math.pow((x - xI),2) + Math.pow((y - yI),2));
	}//calcDistance
	
	//deals with collisions
	public void collidedWith(Entity other) {
		
		//deals with player collisions
		if(other instanceof PlayerEntity){
			game.notifyDeath();
		}//if
		
		//deals with tree collisions
		if(other instanceof TreeEntity){
			game.removeEntity(this);
		}//if
	}//collidedWith
}//EnemyShot