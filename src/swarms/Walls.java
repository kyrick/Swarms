package swarms;

import java.awt.Color;
import java.awt.Graphics;

/**
 * This class contains all the properties and methods of the walls object. 
 * It is responsible for drawing and managing the walls around the play field.
 * @author Stephen Young
 *
 */
public class Walls {
	private int v_offset;
	private int h_offset;
	private int width;
	private int height;
	private int thickness;
	private Color color;
	
	/**
	 * @param v_offset 
	 * @param h_offset 
	 * @param width 
	 * @param height 
	 * @param thickness 
	 * @param color 
	 */
	public Walls(int v_offset,int h_offset, int width, int height, int thickness, Color color) {
		super();
		this.v_offset = v_offset;
		this.h_offset = h_offset;
		this.width = width;
		this.height = height;
		this.thickness = thickness;
		this.color = color;
	}
	
	/**
	 * draw walls spanning the set coordinates
	 * @param g
	 */
	synchronized public void draw(Graphics g){
		g.setColor(this.color);
		//top
		g.fillRect(h_offset - thickness, v_offset-thickness , width + thickness, thickness);
		//bottom
		g.fillRect(h_offset - thickness, v_offset + height, width + thickness + thickness, thickness);
		//left
		g.fillRect(h_offset - thickness, v_offset - thickness, thickness, height + thickness);
		//right
		g.fillRect(h_offset + width, v_offset - thickness, thickness, height + thickness);
	}
	
}
