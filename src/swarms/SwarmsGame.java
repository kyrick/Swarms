package swarms;

import image.ImageSFXs;
import image.ImagesLoader;
import image.ImagesPlayer;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import sound.ClipsLoader;
import sound.MidisLoader;


/**
 * @author Segments of this code are adapted from Roger Mailler
 * @author Current version by Stephen Young
 *
 */
public class SwarmsGame extends GameFrame{
	private static final long serialVersionUID = 6895798707428429651L;
	
	
	// a random group of statics that should probably be ordered but aren't.... some should be constant too... so much to do
	private static int DEFAULT_FPS = 100;
	private static int DEFAULT_FRIENDS = 10;
	private static int DEFAULT_HEIGHT = 600;
	private static int DEFAULT_WIDTH = 1000;
	private static int DEFAULT_START_X = 400;
	private static int DEFAULT_START_Y = 400;
	private static int DEFAULT_SPEED = 8;
	private static String PLAYER_IMAGE = "Player.gif";
	private static Color PLAYER_COLOR = Color.blue;
	private static Color ENEMY_COLOR = Color.red;
	private static Color WALLS_COLOR = Color.black;
	private static int V_OFFSET = 0;
	private static int H_OFFSET = 0;
	private static int WALLS_THICKNESS = 5;
	
	private BufferedImage playerImage;
	
	private boolean setup = true;
	private int level = 1;
	

	//set up game objects
	private Player player; //the player
	private Enemies enemies; //the enemies!
	private Walls walls;//the walls! SO EXCITING!
	
	//score and font setup
	private int score = 0;
	private int lastScore = 0;
	private Font font;
	private FontMetrics metrics;

	// used by quit 'button'
	private volatile boolean isOverQuitButton = false;
	private Rectangle quitArea;

	//game background music and sounds
	private MidisLoader midisLoader;
	private ClipsLoader soundClip;
	
	//game background image
	private ImagesPlayer rightPlayer;
	private ImagesPlayer leftPlayer;
	private BufferedImage leftBorder;
	private BufferedImage rightBorder;
	
	private ImagesPlayer bgPlayer;
	private ImagesLoader bgImages;
	private BufferedImage backGround;
	
	private DecimalFormat df = new DecimalFormat("0.##");  // 2 dp
	
	private boolean isWaiting = true;
	
	/**
	 * @param period
	 */
	public SwarmsGame(long period) {
		super(period);
	}

	@Override
	protected void simpleInitialize() {
		//set game area
		H_OFFSET = pWidth/2 - DEFAULT_WIDTH/2;
		V_OFFSET = pHeight/2 - DEFAULT_HEIGHT/2;

		//set up the walls
		walls = new Walls(V_OFFSET,H_OFFSET,DEFAULT_WIDTH,DEFAULT_HEIGHT,WALLS_THICKNESS,WALLS_COLOR);
		
		ImagesLoader temp = new ImagesLoader();
		String[] fileName = {PLAYER_IMAGE};
		temp.loadGroupImages("player", fileName);
		playerImage = temp.getImage("player", -1);
		//set up the player
		player = new Player(DEFAULT_FRIENDS,DEFAULT_START_X,DEFAULT_START_Y,DEFAULT_SPEED, playerImage, PLAYER_COLOR, V_OFFSET, H_OFFSET,DEFAULT_WIDTH,DEFAULT_HEIGHT);
		
		//set up the enemies
		enemies = new Enemies();
		//enemies.addEnemy(10, 20+H_OFFSET, 20+H_OFFSET, 2, Color.red,V_OFFSET, H_OFFSET,DEFAULT_WIDTH,DEFAULT_HEIGHT,5000, 2000);
		enemies.generateRandom(player,10,15,5,ENEMY_COLOR,ENEMY_COLOR,V_OFFSET, H_OFFSET,DEFAULT_WIDTH,DEFAULT_HEIGHT);
		
		enemies.color(player.getRadius());
		
		// set up message font
		font = new Font("SansSerif", Font.BOLD, 24);
		metrics = this.getFontMetrics(font);
		
		// specify screen areas for the buttons
		quitArea = new Rectangle(pWidth - 100, pHeight - 20, 70, 15);
		
		// load the background MIDI music and sound effect(s)
		midisLoader = new MidisLoader();
		midisLoader.load("bgm", "maple_m.mid");
		midisLoader.play("bgm", true);   // play forever
		
		soundClip = new ClipsLoader();
		soundClip.load("reel", "movieprojector.wav");
		soundClip.play("reel", false);
		
		
		//load background animation sequences
		bgImages = new ImagesLoader();
		String leftBase = "Animate_Left/";
		//do to an odd inexplicable animating department workorder confusion (i.e. I was tired)
		//this animation sequence does, in fact, start at frame 2... which is ultimately arbitrary anyway
		String[] leftDir = {leftBase+"left_2.gif",leftBase+"left_3.gif",leftBase+"left_4.gif",leftBase+"left_5.gif",leftBase+"left_6.gif"};
		bgImages.loadGroupImages("leftReel", leftDir);
		
		String rightBase = "Animate_Right/";
		String[] rightDir = {rightBase+"right_1.gif",rightBase+"right_2.gif",rightBase+"right_3.gif",rightBase+"right_4.gif",rightBase+"right_5.gif"};
		bgImages.loadGroupImages("rightReel", rightDir);
		
		String bgBase = "Animated_BG/";
		String[] bgDir = {bgBase+"BG_1.gif",bgBase+"BG_2.gif",bgBase+"BG_3.gif",bgBase+"BG_4.gif",bgBase+"BG_5.gif",bgBase+"BG_6.gif"
				,bgBase+"BG_7.gif",bgBase+"BG_8.gif",bgBase+"BG_9.gif",bgBase+"BG_10.gif",bgBase+"BG_11.gif",bgBase+"BG_12.gif"
				,bgBase+"BG_13.gif",bgBase+"BG_14.gif",bgBase+"BG_15.gif",bgBase+"BG_16.gif",bgBase+"BG_17.gif"};
		bgImages.loadGroupImages("bgReel", bgDir);
		
		int period = 100;
		
		bgPlayer = new ImagesPlayer("bgReel", period,0.75, true, bgImages);
		leftPlayer = new ImagesPlayer("leftReel", period, .75, true, bgImages);
		rightPlayer = new ImagesPlayer("rightReel", period,0.75, true, bgImages);
		
		
		//leftPlayer.resume();
		//rightPlayer.resume();
		//bgPlayer.resume();
	}
	
	/**
	 * @param args
	 */
	public static void main(String args[]) {
		int fps = DEFAULT_FPS;
		if (args.length != 0)
			fps = Integer.parseInt(args[0]);
		
		long period = (long) 1000.0 / fps;
		System.out.println("fps: " + fps + "; period: " + period + " ms");
		new SwarmsGame(period * 1000000L); // ms --> nanosecs
	}
	
	private void drawButtons(Graphics g) {
		g.setColor(Color.black);

		if (isWaiting)
			g.drawString("Paused", DEFAULT_WIDTH/2 + H_OFFSET - 40, DEFAULT_HEIGHT/2 + 10 + V_OFFSET);

		//if (isOverPauseButton)
		//	g.setColor(Color.black);

		// draw the quit 'button'
		if (isOverQuitButton)
			g.setColor(Color.green);

		g.drawOval(quitArea.x, quitArea.y, quitArea.width, quitArea.height);
		g.drawString("Quit", quitArea.x + 15, quitArea.y + 10);

		if (isOverQuitButton)
			g.setColor(Color.black);
	} // drawButtons()

	@Override
	protected void gameOverMessage(Graphics g)
	// center the game-over message in the panel
	{
		String msg = "Game Over. Your Score: " + score;
		int x = (pWidth - metrics.stringWidth(msg)) / 2;
		int y = (pHeight - metrics.getHeight()) / 2;
		g.setColor(Color.black);
		g.setFont(font);
		g.drawString(msg, x, y);
	} // end of gameOverMessage()

	@Override
	protected void simpleUpdate() {
		if (!isWaiting){
			//check for player eats
			player.enemyHit(enemies);
			//calculate the score
			score = lastScore + player.getFriends()-DEFAULT_FRIENDS;
			
			//change enemy colors
			enemies.color(player.getRadius());
			
			//check for enemy eating player or bouncing off other swarms
			boolean hit = enemies.enemyHit(player);
			
			//check if game over conditions are met
			if (hit)gameOver = true;
			else if (enemies.swarms.size() == 0){
				level++;
				setup = false;
			}
			
			//check for wall collisions wall collisions
			player.wallHit();
			enemies.wallHit();
			
			//move player
			player.move();
			//move enemies
			enemies.move();
			enemies.checkSizes(player);
		}
		
		if (!setup){
			if (level == 2){
				soundClip.play("reel", false);
				initLevel2();
			} else if (level == 3){
				soundClip.play("reel", false);
				initLevel3();
			} else if (level == 4){
				soundClip.play("reel", false);
				initLevel4();
			} else if (level == 5){
				soundClip.play("reel", false);
				initLevel5();
			} else if (level == 6){
				soundClip.play("reel", false);
				initLevel6();
			} else if (level == 7){
				soundClip.play("reel", false);
				initLevel7();
			}else gameOver = true;
		}
	}

	@Override
	protected void mouseMove(int x, int y) {
		if (running) { // stops problems with a rapid move after pressing 'quit'
			isOverQuitButton = quitArea.contains(x, y) ? true : false;
			//update player heading
			player.setDestination(x,y);
		}
	}
	
	@Override
	protected void mousePress(int x, int y) {
		if (isOverQuitButton)
			running = false;
		else {
			if (!isPaused && !gameOver) {
				isWaiting = !isWaiting;
				//player.setDestination(x,y);
			}
		}
	}

	@Override
	protected void simpleRender(Graphics g) {
		g.setColor(Color.blue);
		g.setFont(font);
		g.setColor(Color.blue);
	    g.setFont(font);

	    // report frame count & average FPS and UPS at top left
		g.drawString("Average FPS/UPS: " + df.format(averageFPS) + ", " +
	                                df.format(averageUPS), 20, 25);  // was (10,55)
		
		// report time used and boxes used at bottom left
		g.drawString("Time Spent: " + timeSpentInGame + " secs", 10,
				pHeight - 15);
		g.setColor(Color.black);
		g.drawString("Score: " + score, H_OFFSET, V_OFFSET - 18);
		
		// draw the pause and quit 'buttons'
		g.setColor(Color.blue);
		drawButtons(g);

		g.setColor(Color.black);
		
		// draw game elements:player, enemies, the walls, and borders
		drawBG(g);
		player.draw(g);
		enemies.draw(g);
		walls.draw(g);
		drawBorder(g);
	}
	
	/**
	 * draw the animated background
	 * @param G
	 */
	private void drawBG(Graphics g){
		ImageSFXs sfx = new ImageSFXs();
		bgPlayer.updateTick();
		backGround = bgPlayer.getCurrentImage();
		sfx.drawBlurredImage((Graphics2D)g, backGround, H_OFFSET, V_OFFSET, 5);
	}
	
	/**
	 * draw the animated borders
	 * @param g
	 */
	private void drawBorder(Graphics g){
		ImageSFXs sfx = new ImageSFXs();
		//images are blurred to make them slightly less distracting and to add to the sense of motion
		//update and draw left boarder
		leftPlayer.updateTick();
		leftBorder = leftPlayer.getCurrentImage();
		sfx.drawBlurredImage((Graphics2D)g, leftBorder, H_OFFSET-leftBorder.getWidth(), V_OFFSET-WALLS_THICKNESS, 2);
		
		//update and play right border
		rightPlayer.updateTick();
		rightBorder = rightPlayer.getCurrentImage();
		sfx.drawBlurredImage((Graphics2D)g, rightBorder, H_OFFSET+DEFAULT_WIDTH,  V_OFFSET-WALLS_THICKNESS, 2);
	}
	
	/* ==========================================================
	 * For now the levels are hard coded. They lack the 
	 * complexity that would require loading them externally 
	 * ==========================================================
	 */
	
	/**
	 * initialize level 2
	 */
	private void initLevel2(){
		//set up the player
		player = new Player(DEFAULT_FRIENDS,DEFAULT_START_X,DEFAULT_START_Y,DEFAULT_SPEED, playerImage, PLAYER_COLOR, V_OFFSET, H_OFFSET,DEFAULT_WIDTH,DEFAULT_HEIGHT);
		
		//set up the enemies
		enemies = new Enemies();
		//enemies.addEnemy(10, 20+H_OFFSET, 20+H_OFFSET, 2, Color.red,V_OFFSET, H_OFFSET,DEFAULT_WIDTH,DEFAULT_HEIGHT,5000, 2000);
		enemies.generateRandom(player,15,15,5,ENEMY_COLOR,ENEMY_COLOR,V_OFFSET, H_OFFSET,DEFAULT_WIDTH,DEFAULT_HEIGHT);
		
		enemies.color(player.getRadius());
		
		lastScore = score;
		setup = true;
		isWaiting = true;
	}

	/**
	 * initialize level 3
	 */
	private void initLevel3(){
		//set up the player
		player = new Player(DEFAULT_FRIENDS,DEFAULT_START_X,DEFAULT_START_Y,DEFAULT_SPEED, playerImage, PLAYER_COLOR, V_OFFSET, H_OFFSET,DEFAULT_WIDTH,DEFAULT_HEIGHT);
		
		//set up the enemies
		enemies = new Enemies();
		//enemies.addEnemy(10, 20+H_OFFSET, 20+H_OFFSET, 2, Color.red,V_OFFSET, H_OFFSET,DEFAULT_WIDTH,DEFAULT_HEIGHT,5000, 2000);
		enemies.generateRandom(player,25,30,5,ENEMY_COLOR,ENEMY_COLOR,V_OFFSET, H_OFFSET,DEFAULT_WIDTH,DEFAULT_HEIGHT);
		
		enemies.color(player.getRadius());
		
		lastScore = score;
		setup = true;
		isWaiting = true;
	}
	
	/**
	 * initialize level 4
	 */
	private void initLevel4(){
		//set up the player
		player = new Player(DEFAULT_FRIENDS,DEFAULT_START_X,DEFAULT_START_Y,DEFAULT_SPEED, playerImage, PLAYER_COLOR, V_OFFSET, H_OFFSET,DEFAULT_WIDTH,DEFAULT_HEIGHT);
		
		//set up the enemies
		enemies = new Enemies();
		//enemies.addEnemy(10, 20+H_OFFSET, 20+H_OFFSET, 2, Color.red,V_OFFSET, H_OFFSET,DEFAULT_WIDTH,DEFAULT_HEIGHT,5000, 2000);
		enemies.generateRandom(player,35,35,5,ENEMY_COLOR,ENEMY_COLOR,V_OFFSET, H_OFFSET,DEFAULT_WIDTH,DEFAULT_HEIGHT);
		
		enemies.color(player.getRadius());
		
		lastScore = score;
		setup = true;
		isWaiting = true;
	}
	
	/**
	 * initialize level 5
	 */
	private void initLevel5(){
		//set up the player
		player = new Player(DEFAULT_FRIENDS,DEFAULT_START_X,DEFAULT_START_Y,DEFAULT_SPEED, playerImage, PLAYER_COLOR, V_OFFSET, H_OFFSET,DEFAULT_WIDTH,DEFAULT_HEIGHT);
		
		//set up the enemies
		enemies = new Enemies();
		//enemies.addEnemy(10, 20+H_OFFSET, 20+H_OFFSET, 2, Color.red,V_OFFSET, H_OFFSET,DEFAULT_WIDTH,DEFAULT_HEIGHT,5000, 2000);
		enemies.generateRandom(player,35,35,5,ENEMY_COLOR,ENEMY_COLOR,V_OFFSET, H_OFFSET,DEFAULT_WIDTH,DEFAULT_HEIGHT);
		
		enemies.color(player.getRadius());
		
		lastScore = score;
		setup = true;
		isWaiting = true;
	}
	
	/**
	 * initialize level 6
	 */
	private void initLevel6(){
		//set up the player
		player = new Player(DEFAULT_FRIENDS,DEFAULT_START_X,DEFAULT_START_Y,DEFAULT_SPEED, playerImage, PLAYER_COLOR, V_OFFSET, H_OFFSET,DEFAULT_WIDTH,DEFAULT_HEIGHT);
		
		//set up the enemies
		enemies = new Enemies();
		//enemies.addEnemy(10, 20+H_OFFSET, 20+H_OFFSET, 2, Color.red,V_OFFSET, H_OFFSET,DEFAULT_WIDTH,DEFAULT_HEIGHT,5000, 2000);
		enemies.generateRandom(player,35,35,5,ENEMY_COLOR,ENEMY_COLOR,V_OFFSET, H_OFFSET,DEFAULT_WIDTH,DEFAULT_HEIGHT);
		
		enemies.color(player.getRadius());
		
		lastScore = score;
		setup = true;
		isWaiting = true;
	}
	
	/**
	 * initialize level 7
	 */
	private void initLevel7(){
		//set up the player
		player = new Player(DEFAULT_FRIENDS,DEFAULT_START_X,DEFAULT_START_Y,DEFAULT_SPEED, playerImage, PLAYER_COLOR, V_OFFSET, H_OFFSET,DEFAULT_WIDTH,DEFAULT_HEIGHT);
		
		//set up the enemies
		enemies = new Enemies();
		//enemies.addEnemy(10, 20+H_OFFSET, 20+H_OFFSET, 2, Color.red,V_OFFSET, H_OFFSET,DEFAULT_WIDTH,DEFAULT_HEIGHT,5000, 2000);
		enemies.generateRandom(player,35,35,5,ENEMY_COLOR,ENEMY_COLOR,V_OFFSET, H_OFFSET,DEFAULT_WIDTH,DEFAULT_HEIGHT);
		
		enemies.color(player.getRadius());
		
		lastScore = score;
		setup = true;
		isWaiting = true;
	}
}
