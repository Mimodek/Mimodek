package mimodek.facade;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

/**
 * Provides a preview and utilities methods for Processing sketches that will be
 * shown on the Media-Lab Prado Media Facade.
 * <p>
 * Based on code written by Casey Reas during the OpenUp workshop in Madrid
 * (february 2010). This library makes written sketch for this specific output a
 * bit easier and make the code lighter. Look at the example for usage : <code><pre>
 * import mimodek.facade.*;
 * 
 * MadridPradoFacade facade;
 * 
 * void setup(){
 *  size(1024, 800,JAVA2D);
 *  FacadeFactory.createPradoFacade(this); //NOTE : It is important to call the constructor AFTER a call to size()
 *  facade = (MadridPradoFacade)FacadeFactory.getFacade();
 *  smooth();
 * }
 * 
 * void draw(){ 
 * background(0);
 * float xmove =  facade.halfWidth + cos(frameCount*0.05)*100;
 * translate(facade.leftOffset,facade.topOffset); //this puts the drawing at the right offset for presentation on the facade
 * 
 * fill(255);
 * noStroke();
 * ellipse(facade.width-xmove, facade.halfHeight+10, 60, 60);
 * rect(facade.width-4,facade.height-4,4,4);
 * 
 * stroke(255);
 * noFill();
 * 
 * ellipse(xmove,  facade.halfHeight+10, 60, 60);
 * 
 * if(facade.on)
 *   println(frameRate ); //notice the low frame rate
 * }
 * 
 * void keyPressed(){
 *  facade.on = !facade.on; //press any key to toggle the preview
 * }
 * 
 * void mousePressed(){ //click to switch between nice (but slower) to faster (but a bit uglier) preview
 * 	facade.nice = !facade.nice; 
 * 	println("Nice: "+facade.nice);
 * }</pre>
 *</code>
 * 
 * @author Jonsku
 * 
 */
public class MadridPradoFacade extends Facade implements PConstants {

	
	

	/**
	 * Height of a facade panel in pixels
	 */
	public int bHeight = 16;
	/**
	 * Width of a facade panel in pixels
	 */
	public int bWidth = 12;

	/**
	 * Distance between the center of 2 bulbs
	 */
	private int gap = 5;
	/**
	 * Size of the bulbs
	 */
	private int bulbSize = gap - 1;

	/**
	 * Mask for preview
	 */
	private PImage screenMask;
	/**
	 * To save the sketch drawing
	 */
	private PImage sketchCopy;
	/**
	 * To render 'nice' preview
	 */
	private PImage nicePreview;
	/**
	 * To render 'fast' preview
	 */
	private PGraphics fastPreview;
	/**
	 * Contains the colors of the pixels of the current frame
	 */
	private int[] sketchDrawing;

	/**
	 * If true, the preview will look a bit nicer but will be slower
	 */
	public boolean nice = true;


	/**
	 * Creates a new Facade object instance.
	 *<p>
	 * NOTE : This must be called after a call to size() in the sketch setup()
	 * function.
	 * 
	 * @param app
	 *            a reference to a PApplet object (the base class of a
	 *            Processing sketch)
	 */
	public MadridPradoFacade(PApplet app) {
		super(app);
		leftOffset = 40;
		topOffset = 40;
		width = 192;
		halfWidth = width/2;
		height = 157;
		halfHeight = height/2;
		// calculate ratios
		wRatio = width / (float) app.width;
		hRatio = height / (float) app.height;

		// initialize buffers
		sketchDrawing = new int[Math.round(width * height)];
		// draw mask
		screenMask = createMask();
		nicePreview = app.createImage(this.width * gap, this.height * gap, RGB);
		fastPreview = app.createGraphics(this.width * gap, this.height * gap,
				P2D);
		fastPreview.beginDraw();
		fastPreview.rectMode(CENTER);
		fastPreview.noStroke();
	}


	/**
	 * Called automatically by the sketch just after drawing.<!-- --> Don't call
	 * it yourself, it won't do any good.
	 *<p>
	 * .If the field <b>on</b> is <b>true</b> then a preview of the facade is
	 * drawn, else does nothing.
	 * 
	 * @see #on on
	 */
	public void draw() {
		if (!on)
			return;
		if (!nice) {
			// extract the drawing from the sketch graphic buffer
			app.loadPixels();
			for (int x = leftOffset; x < leftOffset + width; x++) {
				for (int y = topOffset; y < topOffset + height; y++) {
					sketchDrawing[((x - leftOffset) + (y - leftOffset) * width)] = app.pixels[x
							+ y * app.width];
				}
			}
		}
		// save the drawing
		sketchCopy = app.g.get();
		// output the preview
		runPreview();
	}

	/**
	 * Draws a preview of the facade.<!-- --> Toggle the field {@link on} to
	 * hide/show the preview.
	 * 
	 * @see #on .
	 */
	private void runPreview() {
		PImage preview = null;
		if (nice) {
			nicePreview.copy(sketchCopy, leftOffset, topOffset, width,
					height, 0, 0, width * gap, height * gap);
			preview = nicePreview;
		} else {

			fastPreview.beginDraw();
			fastPreview.background(30);

			for (int y = 0; ++y < height;) {
				for (int x = 0; ++x < width;) {
					// This code makes the cutouts
					if (y < bHeight) {
						int distance = bWidth * 2;
						int leftEdge = halfWidth - distance;
						int rightEdge = halfWidth + distance;
						if ((x < leftEdge - 1) || x > rightEdge + 1) {
							continue;
						}
					} else if (y < bHeight * 2) {
						int distance = bWidth * 5;
						int leftEdge = halfWidth - distance;
						int rightEdge = halfWidth + distance;
						if ((x < leftEdge - 1) || x > rightEdge + 1) {
							continue;
						}
					}

					fastPreview.fill(sketchDrawing[x + y * width]);
					fastPreview.rect(x * gap, y * gap, bulbSize, bulbSize);

				}
			}
			fastPreview.endDraw();

			preview = fastPreview;

		}
		preview.mask(screenMask);
		app.resetMatrix();
		app.background(30);
		app.image(preview, 0, 0);
	}

	/**
	 * Creates a matrix of white circles on a black background to simulate the
	 * bulbs of the media facade (white == transparent, black == opaque
	 */
	private PImage createMask() {
		PGraphics scMask = app
				.createGraphics(width * gap, height * gap, JAVA2D);

		scMask.beginDraw();

		scMask.smooth();

		scMask.background(0);
		scMask.fill(255);
		scMask.noStroke();
		for (int y = 1; y < height; y++) {
			for (int x = 1; x < width; x++) {
				// This code makes the cutouts
				if (y < bHeight) {
					int distance = bWidth * 2;
					int leftEdge = halfWidth - distance;
					int rightEdge = halfWidth + distance;
					if (x < leftEdge - 1 || x > rightEdge + 1) {
						continue;
					}
				} else if (y < bHeight * 2) {
					int distance = bWidth * 5;
					int leftEdge = halfWidth - distance;
					int rightEdge = halfWidth + distance;
					if (x < leftEdge - 1 || x > rightEdge + 1) {
						continue;
					}
				}
				scMask.ellipse(x * gap, y * gap, bulbSize, bulbSize);
			}
		}
		scMask.endDraw();
		return scMask;
	}

	/**
	 * Test if the coordinates of the vector are in the screen.
	 * 
	 * @param coordinate
	 *            a PVector holding the coordinates to test
	 * @param margin
	 *            used to define a margin, set to 0 to test entire area
	 * @return true if the coordinates are in the screen, false otherwise
	 */
	public boolean isInTheScreen(PVector coordinate, float margin) {
		if (coordinate.x < margin || coordinate.x > width - margin
				|| coordinate.y > height - margin || coordinate.y < margin) {
			return false;
		}
		// are we on the top part of the screen?
		if (coordinate.y > bHeight * 2) { // no, so those coordinates are inside
			// the facade
			return true;
		}
		// yes, special test for the top part of the screen
		if (coordinate.y < bHeight) {
			float distance = bWidth * 2;
			float leftEdge = halfWidth - distance + margin;
			float rightEdge = halfWidth + distance - margin;
			if ((coordinate.x < leftEdge) || (coordinate.x > rightEdge)) {
				return false;
			}
		} else {
			float distance = bWidth * 5;
			float leftEdge = halfWidth - distance + margin;
			float rightEdge = halfWidth + distance - margin;
			if ((coordinate.x < leftEdge) || (coordinate.x > rightEdge)) {
				return false;
			}

		}
		return true;
	}

	/**
	 * Test if the coordinates of the vector are in the screen.
	 * 
	 * @param coordinate
	 *            a PVector holding the coordinates to test
	 * @return true if the coordinates are in the screen, false otherwise
	 */
	public boolean isInTheScreen(PVector coordinate) {
		return isInTheScreen(coordinate, 0);
	}

	/**
	 * Test if the given coordinates are in the screen.
	 * 
	 * @param x
	 *            the horizontal coordinate to test
	 * @param y
	 *            the vertical coordinate to test
	 * @param margin
	 *            used to define a margin, set to 0 to test entire area
	 * @return true if the coordinates are in the screen, false otherwise
	 */
	public boolean isInTheScreen(float x, float y, float margin) {
		return isInTheScreen(new PVector(x, y), margin);
	}

	/**
	 * Test if the given coordinates are in the screen.
	 * 
	 * @param x
	 *            the horizontal coordinate to test
	 * @param y
	 *            the vertical coordinate to test
	 * @return true if the coordinates are in the screen, false otherwise
	 */
	public boolean isInTheScreen(float x, float y) {
		return isInTheScreen(new PVector(x, y), 0);
	}
}
