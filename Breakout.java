/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

/** Width and height of application window in pixels.  On some platforms 
  * these may NOT actually be the dimensions of the graphics canvas. */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

/** Dimensions of game board.  On some platforms these may NOT actually
  * be the dimensions of the graphics canvas. */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;

/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 50;

/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

/** Separation between bricks */
	private static final int BRICK_SEP = 4;

/** Width of a brick */
	private static final int BRICK_WIDTH =
	  (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;

/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

/** Number of turns */
	private static final int NTURNS = 3;
	
/** Limit of paddle's x coordinate */	
	private static final double PADDLE_LIMIT = WIDTH-PADDLE_WIDTH;
	
	private static final int DELAY = 30;
	// private variables
	
	private RandomGenerator rgen = new RandomGenerator();
/** a label to display number of lives remaining */
	private int turnsLeft= NTURNS;
	private GLabel turnLabel;
/** variables for the velocity of the ball in x and y dimensions */
	private double vx, vy;
/** The objects that the game will be built out of */
	private GOval ball;
	private GRect paddle; 
	private GRect brick;
/** Total number of bricks to be placed */
	private int brickCount=NBRICK_ROWS*NBRICKS_PER_ROW;
	
/**an array that determines the number and order of colored
 *  brick layers */
	Color[] brickColors = {
		Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.CYAN
	};
/* Method: run() */
/** Runs the Breakout program. */
	
	public void init() {
		setSize(417,658);
	}
	
	public void run() {
		brickSetup();		//places the bricks on screen
		ballSetup();	//places the ball, and sets a random x velocity
		paddleSetup();	
		placeLabel();
		addMouseListeners();
		while(brickCount>0 && turnsLeft>0) { //checks for game win/loss
			moveBall();
			checkForCollision();
			pause(DELAY);
		}
		gameOver();		//determines if you've won or lost
	}
	
	private void placeLabel() {
		turnLabel=new GLabel("turns left:  "+turnsLeft);
		add(turnLabel,WIDTH-65,15);
	}
	
	private void brickSetup() {
		int x;
		int y = BRICK_Y_OFFSET;
		/** the inner for loop places individual bricks in a row, incrementing x
		 * the outer for loop moves the set onto the next row.  
		 */
		for (int j=0; j<NBRICK_ROWS; j++) {
			x = 
				(WIDTH-(NBRICKS_PER_ROW*(BRICK_WIDTH+BRICK_SEP)))/2 + BRICK_SEP/2;
			for (int i=0; i<NBRICKS_PER_ROW; i++) {
				add(makeBrick(BRICK_WIDTH,BRICK_HEIGHT, brickColors[j/2]), x, y);
				x += BRICK_WIDTH+BRICK_SEP;
			}
			y += BRICK_HEIGHT+BRICK_SEP;			
		}		
	}
	private GRect makeBrick(int width, int height, Color color) {
		brick = new GRect(width, height);
		brick.setFilled(true);
		brick.setFillColor(color);
		brick.setColor(color);
		return brick;
	}

	private void paddleSetup() {
		paddle = new GRect(PADDLE_WIDTH,PADDLE_HEIGHT);
		paddle.setFilled(true);
	}
	
	public void mouseMoved(MouseEvent e) {
		double paddleX= e.getX()-PADDLE_WIDTH/2;
		if (paddleX>PADDLE_LIMIT) paddleX=PADDLE_LIMIT;
		if (paddleX<0) paddleX=0;
		add(paddle,paddleX,HEIGHT-PADDLE_Y_OFFSET);
	}

	private void ballSetup() {
		ball = new GOval(BALL_RADIUS*2,BALL_RADIUS*2);
		ball.setFilled(true);
		add(ball, WIDTH/2-BALL_RADIUS, HEIGHT/2-BALL_RADIUS);
		vy=3.0;
		vx = rgen.nextDouble(1.0,3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;
	}
	
	private void moveBall() {
		ball.move(vx,vy);
	}
	
	private void checkForCollision() {
		/* checks if ball is at the left/right borders, and bounces it */
		if ((ball.getX()+BALL_RADIUS*2 >=WIDTH) || (ball.getX() <=0)) {
			vx *= -1;
		}
		/* checks if the ball is at the bottom border
		 *  and resets/subtracts a lif e*/
		if (ball.getY()+BALL_RADIUS*2 >= HEIGHT) {
			turnsLeft -= 1;
			remove(turnLabel);
			placeLabel();
			remove(ball);
			ballSetup();
		}
		/*checks if the ball is at the top border and bounces it*/
		if (ball.getY()<=0) {
			vy *= -1;
		}
		/* checks if the ball is htting the paddle and bounces it*/
		if (getCollidingObject() == paddle ) {
			vy *= -1;
		/*otherwise, if it's hitting something, and that thing is 
		 * not the life display, it's a brick. We remove the brick, 
		 * and also keep count to see if we win! */
		} else if ((getCollidingObject() != null) && 
				(getCollidingObject() != turnLabel)) {
			remove(getCollidingObject());
			vy *= -1;
			brickCount -=1;
		}
	}
	private GObject getCollidingObject() {
		GObject collider;
		// ball is moving up
		if (vy<0) {
			collider = getElementAt(ball.getX(),ball.getY());
			if (collider != null) return collider;
			collider = getElementAt(ball.getX()+BALL_RADIUS*2,ball.getY());
			return collider;
		//ball is moving down
		} else {		
			collider = getElementAt(ball.getX()+BALL_RADIUS*2,ball.getY()+BALL_RADIUS*2);
			if (collider != null) return collider;
			collider = getElementAt(ball.getX(),ball.getY()+BALL_RADIUS*2);
		} 
		return collider;
	}
	
	private void gameOver() {
		remove(ball);
		GLabel endText;
		if (turnsLeft == 0) {
			endText = new GLabel("GAME OVER");
		} else if (brickCount == 0) {
			endText = new GLabel("YOU WIN!!!");
		} else {
			endText = new GLabel("ERROR");
		}
		endText.setFont("Serif-50");
		add(endText, WIDTH/2-endText.getWidth()/2, HEIGHT/2-endText.getAscent());
	}
	
}
