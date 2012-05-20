package swarms;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * @author Stephen Young
 *
 */
public class Particle {
	protected int x;
	protected int y;
	protected double center_x = 0;
	protected double center_y = 0;
	protected double v_x;
	protected double v_y;
	
	protected double true_x;
	protected double true_y;
	
	protected double speed;
	protected int radius;
	protected BufferedImage image;
	private int size;
	
	/**
	 * @param trueX
	 * @param trueY
	 * @param size 
	 * @param image 
	 * @param radius
	 */
	public Particle(double trueX, double trueY, int size, BufferedImage image, int radius) {
		super();
		true_x = trueX;
		true_y = trueY;
		this.size = size;
		this.radius = radius;
		this.image = image;
		Random random = new Random();
		this.speed = random.nextInt(1)+random.nextDouble()+0.1;
		startVect();
		updateDrawCoords();
	}
	
	/**
	 * set the original foci
	 */
	private void startVect(){
		double xx,yy, hyp;
		Random random = new Random();
		xx = random.nextInt(101)-50;
		yy = random.nextInt(101)-50;
		
		hyp = Math.sqrt(xx*xx + yy*yy);
		v_x = xx/hyp;
		v_y = yy/hyp;
	}
	
	/**
	 * update the heading of the particle
	 */
	private void updateVect(){
		double dist = (int)Math.sqrt((center_x)*(center_x) + (center_y)*(center_y));
		if (dist >= radius){
			setHeading(0,0);
		}
	}
	
	/**
	 * set new vector for the particle
	 * @param newX
	 * @param newY
	 */
	synchronized public void setHeading(double newX, double newY){
		double hyp, xx, yy;
		xx = newX - center_x;
		yy = newY - center_y;
		hyp = Math.sqrt(xx*xx + yy*yy);
		v_x = xx/hyp;
		v_y = yy/hyp;
	}
	
	/**
	 * update display coordinates
	 */
	private void updateDrawCoords(){
		x = (int)Math.round(center_x - size/2 + true_x);
		y = (int)Math.round(center_y - size/2 + true_y);
	}
	
	/**
	 * move el particle
	 * @param trueX 
	 * @param trueY 
	 */
	synchronized public void move(double trueX,double trueY){
		center_x += v_x * speed;
		center_y += v_y * speed;
		true_x = trueX;
		true_y = trueY;
		updateDrawCoords();
		updateVect();
	}
	
	/**
	 * draw the swarm
	 * @param g
	 */
	synchronized public void draw(Graphics g){
		g.drawImage(image, this.x, this.y, this.size, this.size, null);
	}
	
	/**
	 * change the color of the enemy
	 * @param image 
	 */
	public void setImage(BufferedImage image) {
		this.image = image;
	}
	
	/**
	 * set the radius bounds
	 * @param radius
	 */
	public void setRadius(int radius){
		this.radius = radius;
	}
}
