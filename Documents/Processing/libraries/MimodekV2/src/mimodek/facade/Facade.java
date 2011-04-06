package mimodek.facade;

/*
This is the code source of Mimodek. When not stated otherwise,
it was written by Jonathan 'Jonsku' Cremieux<jonathan.cremieux@aalto.fi> in 2010. 
Copyright (C) yyyy  name of author

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

*/

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PVector;

// TODO: Auto-generated Javadoc
/**
 * Abstract class to define the methods and attributes that every implementing class must provide.
 * This allows to configure Mimodek for any display without making any change to the core.
 * @author Jonsku
 */
public abstract class Facade {

	/** The owner PApplet. */
	protected PApplet app;
	
	/** To save the sketch drawing. */
	protected PImage sketchCopy;
	
	/** Width of the facade in pixels. */
	public int width = 0;
	
	/** Height of the facade in pixels. */
	public int height = 0;
	
	/** Half the width of the facade in pixels. */
	public int halfWidth = 0;
	
	/** Half the height of the facade in pixels. */
	public int halfHeight = 0;
	
	/** Left offset for presentation on screen. */
	public int leftOffset = 0;
	
	/** Top offset for presentation on screen. */
	public int topOffset = 0;
	
	/** Horizontal ratio between the sketch and the facade. */
	protected float wRatio = 0;
	
	/** Vertical ratio between the sketch and the facade. */
	protected float hRatio = 0;
	
	/** The mouse horizontal coordinate in the screen space. */
	public float mouseX = 0;
	
	/** The mouse vertical coordinate in the screen space. */
	public float mouseY = 0;
	
	/** The previous mouse horizontal coordinate in the screen space. */
	public float pmouseX = 0;
	
	/** The previous mouse vertical coordinate in the screen space. */
	public float pmouseY = 0;
	
	/** Maintain a border around the edges. */
	public float border = 0f;
	
	/** If true, the preview will be shown. */
	public boolean on;
	
	/** ArrayList of event listeners. */
	ArrayList<FacadeEventListener> listeners;
	
	/**
	 * Instantiates a new facade.
	 *
	 * @param app the app
	 */
	public Facade(PApplet app) {
		this.app = app;
		app.registerPre(this);
		app.registerDraw(this);
		listeners = new ArrayList<FacadeEventListener>();
	}
	
	/**
	 * Adds the listener.
	 *
	 * @param listener the listener
	 */
	public void addListener(FacadeEventListener listener){
		listeners.add(listener);
	}

	/**
	 * Called automatically by the sketch just before drawing.<!-- --> Don't
	 * call it yourself, it won't do any good.
	 *<p>
	 * Calculates the mouse position in the space of the facade and restore
	 * previous drawing.
	 */
	public void pre(){
		pmouseX = mouseX;
		pmouseY = mouseY;
		/*
		mouseX = app.mouseX * wRatio;
		mouseY = app.mouseY * hRatio;
*/
		mouseX = app.mouseX-leftOffset;
		mouseY = app.mouseY-topOffset;
		if (!on || sketchCopy == null)
			return;

		app.image(sketchCopy, 0, 0);
		sketchCopy = null;
	}

	/**
	 * Draws the boundaries of the drawing area used by the facade.
	 */
	public void showDrawingArea() {
		app.pushMatrix();
		app.pushStyle();
		app.resetMatrix();
		app.noFill();
		app.stroke(255);
		app.rectMode(PConstants.CORNER);
		app.rect(leftOffset, topOffset, width, height);
		app.popStyle();
		app.popMatrix();
	}
	
	/**
	 * Switch the preview on/off.
	 */
	public void togglePreview(){
		on = !on;
		//tell the listeners
		for(int i=0;i<listeners.size();i++)
			listeners.get(i).facadeOnOff(on);
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
	public abstract void draw();

	/**
	 * Test if the coordinates of the vector are in the screen.
	 * 
	 * @param coordinate
	 *            a PVector holding the coordinates to test
	 * @param margin
	 *            used to define a margin, set to 0 to test entire area
	 * @return true if the coordinates are in the screen, false otherwise
	 */
	public abstract boolean isInTheScreen(PVector coordinate, float margin);

	/**
	 * Test if the coordinates of the vector are in the screen.
	 * 
	 * @param coordinate
	 *            a PVector holding the coordinates to test
	 * @return true if the coordinates are in the screen, false otherwise
	 */
	public abstract boolean isInTheScreen(PVector coordinate);

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
	public abstract boolean isInTheScreen(float x, float y, float margin);

	/**
	 * Test if the given coordinates are in the screen.
	 * 
	 * @param x
	 *            the horizontal coordinate to test
	 * @param y
	 *            the vertical coordinate to test
	 * @return true if the coordinates are in the screen, false otherwise
	 */
	public abstract boolean isInTheScreen(float x, float y);
}
