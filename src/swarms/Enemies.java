package swarms;

import image.ImagesLoader;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author Stephen Young
 *
 */
public class Enemies {
	
	private static final Color EDIBLE = Color.green;
	private static final Color NONEDIBLE = Color.red;
	private static final String EDIBLE_IMAGE = "Enemy_eat.gif";
	private static final String NONEDIBLE_IMAGE = "Enemy_non.gif";
	
	//images for edible and nonedible forms of the swarm
	private BufferedImage edible;
	private BufferedImage nonedible;
	
	/**
	 * the list of swarms
	 */
	public ArrayList<Enemy> swarms;
	
	/**
	 * constructor for enemies
	 */
	public Enemies(){
		swarms = new ArrayList<Enemy>();
	}
	
	/**
	 * add an enemy swarm to the game
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
	public void addEnemy(int friends, int x, int y, int speed, BufferedImage member, Color ring, int v_off, int h_off, int width, int height){
		swarms.add(new Enemy(friends,x,y,speed,member,ring,v_off,h_off,width,height));
	}
	
	
	/**
	 * add the specified number of random enemies
	 * @param player
	 * @param number
	 * @param maxSize 
	 * @param minSize 
	 * @param member 
	 * @param ring 
	 * @param v_off 
	 * @param h_off 
	 * @param width 
	 * @param height 
	 */
	public void generateRandom(Swarm player,int number, int maxSize, int minSize, Color member, Color ring, int v_off, int h_off, int width, int height){
		ImagesLoader temp = new ImagesLoader();
		String[] fileName = {EDIBLE_IMAGE};
		temp.loadGroupImages("edible", fileName);
		edible = temp.getImage("edible",-1);
		
		fileName = new String[] {NONEDIBLE_IMAGE};
		temp.loadGroupImages("nonedible", fileName);
		nonedible = temp.getImage("nonedible",-1);
		
		for (int i=0; i<number;i++){
			Random random = new Random();
			int friends = random.nextInt(maxSize-minSize)+minSize;
			int speed = random.nextInt(2)+1;
			int x = random.nextInt(width-friends*2);
			int y = random.nextInt(height-friends*2);

			//check for collision with player
			double xx = player.getCenterX(); 
			double yy = player.getCenterY();
			//could have used an operator the contained paranthetic equations. But I didn't want to.
			int dist = (int)Math.sqrt((xx-(x+friends))*(xx-(x+friends)) + (yy-(y+friends))*(yy-(y+friends)));
			if (dist <= player.getRadius()+friends+40){
				swarms.add(new Enemy(friends,x+40,y,speed, nonedible, Color.red, v_off,h_off,width,height));
				swarms.get(swarms.size()-1).randomVect();
			}else{
			swarms.add(new Enemy(friends,x,y,speed, nonedible, Color.red, v_off,h_off,width,height));
			swarms.get(swarms.size()-1).randomVect();
			}	
		}
	}
	
	/**
	 * Make sure the player hasn't run out of eats.
	 * This is a workaround of sorts to compensate for the random enemy composition
	 * containing enemies that are too large.
	 * @param player
	 */
	public void checkSizes(Swarm player){
		if (swarms.size() == 0) return;
		
		//make sure enemies are smaller than player
		for (int i=0; i<swarms.size();i++){
			//end function if we find at least one small enemy
			if (swarms.get(i).getRadius() < player.getRadius())return;
		}
		//uh oh! we failed to find a smaller enemy! time to rubberband the game
		int smallest = 100, j = 0;
		
		for (int i=0; i<swarms.size();i++){
			//end function if we find at least one small enemy
			if (swarms.get(i).getRadius() < smallest){
				smallest = swarms.get(i).getRadius();
				j = i;
			}
		}
		
		if (j != 0)swarms.get(j).setFriends(player.getFriends()-1);
	}
	
	/**
	 * check for collisions with other swarms or the player
	 * @param player
	 * @return hit
	 */
	public boolean enemyHit(Swarm player){
		for (int i=0; i<swarms.size();i++){
			boolean hit = swarms.get(i).enemyHit(this, player);
			//player was hit, time to end the game
			if (hit) return true;
		}
		//player remains untouched by evil swarms
		return false;
	}
	
	/**
	 * update the position of all enemies
	 */
	public void move() {
		for (int i=0; i<swarms.size();i++){
			swarms.get(i).move();
		}
	}

	/**
	 * call all the draw methods of the swarms
	 * @param g
	 */
	public void draw(Graphics g) {
		for (int i=0; i<swarms.size();i++){
			swarms.get(i).draw(g);
		}
	}

	/**
	 * change the colors of the enemies as needed
	 * @param pRadius
	 */
	public void color(int pRadius){
		for (int i=0; i<swarms.size();i++){
			if (swarms.get(i).getRadius() < pRadius){
				swarms.get(i).setColor(edible,EDIBLE);
			}else 
				swarms.get(i).setColor(nonedible,NONEDIBLE);
		}
	}
	
	/**
	 * cycle through the list of enemies
	 */
	public void wallHit() {
		for (int i=0; i<swarms.size();i++){
			swarms.get(i).wallHit();
		}
	}
}
