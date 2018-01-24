/*****************************************************************************************
 
 * Sprite.java
 * Name: Anthony Bolyos, Steven Zhu
 * Date: April 1, 2015
 * Purpose: stores properties for the sprites of the objects in game.
 
 *****************************************************************************************/
package agentCommando;

import java.awt.Graphics;
import java.awt.Image;

public class Sprite {

	private Image image;//image of the entity
	
	//constructor
	Sprite(Image image){
		this.image = image;
	}//Sprite

	//draw the sprite in the provided graphics object at (x,y)
	public void draw(Graphics g, int x, int y){
		
		g.drawImage(image, x, y, null);
	
	}//draw
	
	//return the height of the sprite
	public int getHeight(){	
		return this.image.getHeight(null);
	}//getHeight
	
	//return the width of the sprite
	public int getWidth(){
		return this.image.getWidth(null);
	}//getWidth
	
}//Sprite
