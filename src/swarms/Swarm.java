package swarms;

import image.ImageSFXs;
import image.ImagesLoader;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * This contains the properties and methods of a swarm object
 * @author Stephen Young
 */
public abstract class Swarm {
	protected int x;
	protected int y;
	protected double center_x;
	protected double center_y;
	protected int speed;
	protected double v_x;
	protected double v_y;
	protected int radius;
	protected int friends;
	//bounds of the box
	protected int top;
	protected int bottom;
	protected int left;
	protected int right;
	//eventually these will be different. Possible translucent effect
	//will be used on the ring as well color later down the line
	protected BufferedImage memberImage;
	protected Color ringColor;

	//particle size of the swarm. For now this is not configurable
	protected int size = 4;
	
	//the particles that form the the members of the swarm
	protected ArrayList<Particle> members;
	
	//image stuff
	protected static final String SHADOW_IMAGE = "shadow.gif";
	protected BufferedImage shadow;
	
	/**
	 * @param friends
	 * @param x 
	 * @param y 
	 * @param speed 
	 * @param memberImage 
	 * @param ringColor 
	 * @param v_off 
	 * @param h_off 
	 * @param width 
	 * @param height 
	 */
	public Swarm(int friends, int x, int y, int speed, BufferedImage memberImage, Color ringColor, int v_off, int h_off, int width, int height){
		this.friends = friends;
		this.left = h_off;
		this.right = h_off + width;
		this.top = v_off;
		this.bottom = v_off + height;
		this.x = x + h_off;
		this.y = y + v_off;
		this.speed = speed;
		this.v_x = 0;
		this.v_y = 0;
		this.memberImage = memberImage;
		this.ringColor = ringColor;
		members = new ArrayList<Particle>();
		this.center_x = this.x+radius;
		this.center_y = this.y+radius;
		setRadius();
		addMembers();
		
		ImagesLoader imageLoad = new ImagesLoader();
		String[] name = {SHADOW_IMAGE};
		
		imageLoad.loadGroupImages("shadow", name);
		
		shadow = imageLoad.getImage("shadow", -1);
	}
	
	/**
	 * 
	 */
	protected void addMembers() {
		if (members.size() < friends){
			int adds = friends - members.size();
			for (int i = 0; i < adds;i++){
				members.add(new Particle(center_x, center_y, size, memberImage, radius));
			}
		}
		for (int i = 0; i < members.size();i++){
			members.get(i).setRadius(radius);
		}
	}
	
	/**
	 * change the image and color of the enemy
	 * @param memberImage 
	 * @param ring 
	 */
	public void setColor(BufferedImage memberImage, Color ring) {
		this.ringColor = ring;
		this.memberImage = memberImage;
		setMemberImage(memberImage);
	}
	
	/**
	 * set the member colors
	 * @param color
	 */
	protected void setMemberImage(BufferedImage memberImage){
		for(int i = 0; i < members.size();i++){
			members.get(i).setImage(memberImage);
		}
	}

	protected void drawMembers(Graphics g){
		for(int i = 0; i < members.size(); i++){
			members.get(i).draw(g);
		}
	}
	
	protected void moveMembers(){
		for(int i = 0; i < members.size(); i++){
			members.get(i).move(center_x, center_y);
		}
	}
	
	/**
	 * Grow the swarm by the given number of friends
	 * @param newFriends 
	 */
	public abstract void grow(int newFriends);
	
	/**
	 * move the swarm along vector path
	 */
	public abstract void move();
	
	/**
	 * set the x and y coordinates for draw location
	 */
	protected abstract void updateDrawCoords();
	
	/**
	 * set the radius based on the number of friends
	 * @param friends
	 */
	protected void setRadius(){
		radius = friends;
		addMembers();
	}
	
	/**
	 * returns the friends in the swarm
	 * @return friends
	 */
	public int getFriends(){
		return friends;
	}
	
	/**
	 * return the radius of the swarm
	 * @return radius
	 */
	public int getRadius(){
		return radius;
	}
	
	/**
	 * bounce swarm off of wall
	 */
	public abstract void wallHit();
	
	/**
	 * @return center_x
	 */
	public double getCenterX() {
		return center_x;
	}

	/**
	 * @return center_y
	 */
	public double getCenterY() {
		return center_y;
	}
	
	/**
	 * draw the swarm
	 * @param g
	 */
	synchronized public void draw(Graphics g){
		//draw edge highlight to enhance gameplay
		//g.setColor(ringColor);
		//g.drawOval(x, y, radius*2, radius*2);
		ImageSFXs sfx = new ImageSFXs();
		sfx.drawFadedResizedImage((Graphics2D)g,shadow,x,y,radius+radius-100, (float).07);
		
		//draw el swarmo grande
		drawMembers(g);
	}
}
