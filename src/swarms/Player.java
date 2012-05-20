package swarms;

import java.awt.Color;
import java.awt.image.BufferedImage;

import sound.ClipsLoader;

/**
 * This contains the properties and methods of a swarm object
 * @author Stephen Young
 */
public class Player extends Swarm {
	private double d_x;
	private double d_y;
	ClipsLoader soundClip;
	
	//sound to play when eating another swarm
	private static final String SOUND_FILE_NAME = "munching.wav";
	
	/**
	 * @param friends
	 * @param x 
	 * @param y 
	 * @param speed 
	 * @param member 
	 * @param ring 
	 * @param v_off 
	 * @param h_off 
	 * @param width 
	 * @param height 
	 */
	public Player(int friends, int x, int y, int speed, BufferedImage member, Color ring,int v_off, int h_off, int width, int height){
		super(friends, x, y, speed, member, ring, v_off, h_off, width, height);
		this.d_x = center_x;
		this.d_y = center_y;
		
		//define sound
		soundClip = new ClipsLoader();
		soundClip.load("eat", SOUND_FILE_NAME);
	}
	
	/**
	 * Grow the swarm by the given number of friends
	 * @param newFriends 
	 */
	synchronized public void grow(int newFriends){
		this.friends += 1;
		setRadius();
	}

	/**
	 * alter the object vector to point towards the destination point
	 * @param newX
	 * @param newY
	 */
	synchronized public void setDestination(double newX, double newY){
		d_x = newX;
		d_y = newY;
		if (Math.abs(d_x-center_x) > 5 || Math.abs(d_y-center_y) > 5){
			double hyp, xx, yy;
			xx = d_x - center_x;
			yy = d_y - center_y;
			hyp = Math.sqrt(xx*xx + yy*yy);
			v_x = xx/hyp;
			v_y = yy/hyp;
		}
	}
		
	/**
	 * move the swarm along vector path
	 */
	synchronized public void move(){
		if (Math.abs(d_x-center_x) >= 3 || Math.abs(d_y-center_y) >= 3){
			center_x += v_x * speed;
			center_y += v_y * speed;
			updateDrawCoords();
			setDestination(d_x,d_y);
		}
		moveMembers();
	}
	
	/**
	 * set the x and y coordinates for draw location
	 */
	protected void updateDrawCoords(){
		x = (int)Math.round(center_x - radius);
		y = (int)Math.round(center_y - radius);
	}
		
	/**Lightly bounce the object off the wall
	 */
	synchronized public void wallHit(){
		int bounce = 10;
		//check right
		if (center_x+radius >= right){
			setDestination(center_x-bounce,center_y);
		}
		//check left
		if (center_x-radius <= left){
			setDestination(center_x+bounce,center_y);		
		}
		//check bottom
		if (center_y+radius >= bottom){
			setDestination(center_x,center_y-bounce);		
		}
		//check top
		if(center_y-radius <= top){
			setDestination(center_x,center_y+bounce);			
		}
	}

	/**
	 * check for collision
	 * @param enemies
	 * @return true for collision
	 */
	synchronized public boolean enemyHit(Enemies enemies) {
		Swarm badguy;
		for (int i = 0; i < enemies.swarms.size();i++) {
			badguy = enemies.swarms.get(i);
			double xx = badguy.getCenterX(); 
			double yy = badguy.getCenterY();
			//find the distance between the two points
			int dist = (int)Math.sqrt((xx-center_x)*(xx-center_x) + (yy-center_y)*(yy-center_y));
			//check for collision, also make sure it's not me, also make sure it's smaller than me
			if (dist <= badguy.getRadius()+radius && badguy.getRadius() < radius){
				soundClip.stop("eat");
				soundClip.play("eat", false);
				grow(enemies.swarms.get(i).getFriends());
				enemies.swarms.remove(i);
				return true;
			}
		}
		
		return false;
	} // end of intersects()
	
}