package swarms;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * This contains the properties and methods of a swarm object
 * @author Stephen Young
 */
public class Enemy extends Swarm{
	
	private int v_off;
	private int h_off;
	
	private static final int GRACE = 2;
	
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
	public Enemy(int friends, int x, int y, int speed, BufferedImage member, Color ring, int v_off, int h_off, int width, int height){
		super(friends, x, y, speed, member, ring, v_off, h_off, width, height);
		this.v_off = v_off;
		this.h_off = h_off;
	}
	
	/**
	 * Grow the swarm by the given number of friends
	 * @param newFriends 
	 */
	public void grow(int newFriends){
		this.friends += 1;
		setRadius();
	}
	
	/**
	 * manually adjust the number of friends
	 * @param newFriends
	 */
	public void setFriends(int newFriends){
		friends = newFriends;
		setRadius();
	}
	
	/**
	 * Generate a random vector to follow
	 */
	public void randomVect(){
		Random random = new Random();
		int d_x,d_y;
		//pick a random point for the swarm to avoid, thus causing a random vector
		d_x = random.nextInt(right-left)+1+h_off;
		d_y = random.nextInt(bottom-top)+1+v_off;
		setAvoid(d_x,d_y);
	}
	
	/**
	 * alter the object vector to point away from from the given location
	 * @param newX
	 * @param newY
	 */
	synchronized public void setAvoid(double newX, double newY){
		double hyp, xx, yy;
		xx = newX - center_x;
		yy = newY - center_y; 
		hyp = Math.sqrt(xx*xx + yy*yy);
		v_x = (xx/hyp)*-1;
		v_y = (yy/hyp)*-1;
	}
	
	/**
	 * alter the object vector to point away from from the given location
	 * @param newX
	 * @param newY
	 */
	synchronized public void addVoidVect(double newX, double newY){
		double hyp, xx, yy;
		xx = newX - center_x;
		yy = newY - center_y; 
		hyp = Math.sqrt(xx*xx + yy*yy);
		double tempv_x = (xx/hyp)*-1;
		double tempv_y = (yy/hyp)*-1;
		tempv_x -= v_x;
		tempv_y -= v_y;
		tempv_x/= 2;
		tempv_y/= 2;
		v_x += tempv_x;
		v_y += tempv_y;
	}
	
	/**
	 * move the swarm along vector path
	 */
	synchronized public void move(){
		center_x += v_x * speed;
		center_y += v_y * speed;
		updateDrawCoords();
		moveMembers();
	}
	
	/**
	 * set the x and y coordinates for draw location
	 */
	protected void updateDrawCoords(){
		x = (int)Math.round(center_x - radius);
		y = (int)Math.round(center_y - radius);
	}
	
	/**
	 * bounce swarm off of wall
	 */
	synchronized public void wallHit(){
		//basic angle in = angle out vector manipulation
		if (center_x+radius >= right){
			if (v_x > 0)v_x *= -1;
		}
		if (center_x-radius <= left){
			if (v_x < 0)v_x *= -1;
		}
		if (center_y+radius >= bottom){
			if (v_y > 0)v_y *= -1;
		}
		if(center_y-radius <= top){
			if (v_y < 0)v_y *= -1;
		}
	}

	/**
	 * check for collision
	 * @param enemies
	 * @param player 
	 * @return true for collision
	 */
	synchronized public boolean enemyHit(Enemies enemies, Swarm player) {
		Swarm badguy;
		boolean first = true;
		for (int i = 0; i < enemies.swarms.size();i++) {
			badguy = enemies.swarms.get(i);
			if (badguy != this){
				double xx = badguy.getCenterX(); 
				double yy = badguy.getCenterY();
				//find the distance between the two points
				double dist = (int)Math.sqrt((xx-center_x)*(xx-center_x) + (yy-center_y)*(yy-center_y));
				//check for collision, also make sure it's not me, also make sure it's smaller than me
				if (dist <= badguy.getRadius()+radius && badguy.getRadius() < radius){
					//avoid this member
					if (!first){
						first = false;
						setAvoid(badguy.getCenterX(),badguy.getCenterY());
					}else{
						addVoidVect(badguy.getCenterX(),badguy.getCenterY());
					}
					
				}
			}
		}
		
		//check for collision with player
		double xx = player.getCenterX(); 
		double yy = player.getCenterY();
		int dist = (int)Math.sqrt((xx-center_x)*(xx-center_x) + (yy-center_y)*(yy-center_y));
		if (dist+GRACE <= player.getRadius()+radius && player.getRadius() < radius){
			//player has been eaten. end the game
			return true;
		}
		
		return false;
	} // end of intersects()

}
