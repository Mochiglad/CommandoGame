/*****************************************************************************************
 
 * Entity.java
 * Name: Anthony Bolyos, Steven Zhu
 * Date: April 1, 2015
 * Purpose: stores properties of any object in the game & resolves collisions & movement
 
 *****************************************************************************************/

package agentCommando;

import java.awt.*;
import java.util.ArrayList;

public abstract class Entity {
	
	protected double x;  //current x location
	protected double y;  //current y location
	protected double dx;  //horizontal speed (px/s) + -> Right
	protected double dy; //vertical speed (px/s) + -> Down
	protected double playerX; //player's location for enemies' AI
	protected double playerY; //player's location for enemies' AI
	protected String direction; //current direction
	protected Sprite sprite; //this entity's sprite
	private Rectangle me = new Rectangle();  //bounding rectangle of this entity
	private Rectangle him = new Rectangle();  //bounding rectangle of other entities
	protected int spriteStage = 1; //stage for sprite changing
	
	//constructor
	public Entity (String r, int newX, int newY){
		this.x = newX;
		this.y = newY;
		this.sprite = (SpriteStore.get()).getSprite(r);
	}//Entity
	
	//after a certain amount of time has passed, update the location
	public void move(long delta){
		
		x += dx * delta * Math.pow(10, -3);
		y += (dy + Game.moveWithBack) * delta * Math.pow(10, -3);
		
	}//move
	
	//draw entity at (x,y)
	public void draw (Graphics g){
		sprite.draw(g, (int)x, (int)y);
	}//draw
	
	public void doLogic(){}  //do logic associated with entity
	
	//checks if this entity collides with another
	public boolean collidesWith (Entity other){
		me.setBounds((int)x, (int)y, sprite.getWidth(), sprite.getHeight());
		him.setBounds(other.getX(), other.getY(), other.sprite.getWidth(), other.sprite.getHeight());
		return me.intersects(him);
	}//collidesWith
	
	public abstract void collidedWith(Entity other);  //notifies of collision
	
	//get the horizontal movement of this entity
	public double getHorizontalMovement(){
		return dx;
	}//getHorizontalMovement
	
	//get the vertical movement
	public double getVerticalMovement(){
		return dy;
	}//getVerticalMovement
	
	//get the direction of this entity
	public String getDirection(){
		return this.direction;
	}//getDirection
	
	//set the direction of this entity
	public void setDirection(String direction){
		this.direction = direction;
	}//setDirection
	
	//set the horizontal movement of this entity
	public void setHorizontalMovement(double dx){
		this.dx = dx;
	}//setHorizontalMovement
	
	//set the vertical movement of this entity
	public void setVerticalMovement(double dy){
		this.dy = dy;
	}//setVerticalMovement
	
	//get the x coordinate of this entity
	public int getX(){
		return (int)this.x;
	}//getX
	
	//get the y coordinate of this entity
	public int getY(){
		return (int)this.y;
	}//getY
	
	//set the player x coordinate
	public void setPlayerX(int x){
		this.playerX = (double)x;
	}//setX
	
	//set the player y coordinate
	public void setPlayerY(int y){
		this.playerY = (double)y;
	}//setY
	
	//set the spriteStage for this entity
	public void setSpriteStage() {
		return;
	}//setSpriteStage
}//Entity
