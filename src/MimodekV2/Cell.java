package MimodekV2;

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

import processing.core.PApplet;
import processing.core.PVector;


// TODO: Auto-generated Javadoc
/**
 * The Class Cell.
 */
public class Cell {
	 
 	/** The pos. */
 	PVector pos;
	 
 	/** The maturity. */
 	float maturity = 0;
	 
 	/** The current maturity. */
 	float currentMaturity = 0;
	 
 	/** The anchor. */
 	Cell anchor = null;
	
	/** The angle to anchor. */
	float angleToAnchor = 0;

	/** The z level. */
	float zLevel = 0;
	
	/** The last update. */
	long lastUpdate;
	 
	  /**
  	 * Instantiates a new cell.
  	 *
  	 * @param pos the pos
  	 */
  	public Cell(PVector pos){
	    this.pos = pos;
	    lastUpdate = System.currentTimeMillis();
	  }
	  
	  /**
  	 * Sets the anchor.
  	 *
  	 * @param anchor the new anchor
  	 */
  	void setAnchor(Cell anchor){
	    this.anchor = anchor;
	    angleToAnchor = PApplet.atan2(pos.y-anchor.pos.y,pos.x-anchor.pos.x);
	  }
	  
	  /**
  	 * Update postion and other parameters.
  	 *
  	 * @param app the app
  	 */
  	public void update(PApplet app){
			 
	}
	 
	 /**
 	 * Draw.
 	 *
 	 * @param app the app
 	 */
 	public void draw(PApplet app){
		 
	 }
	 
	 /**
 	 * Feed.
 	 *
 	 * @param amount the amount to feed the cell
 	 */
 	void feed(float amount){
	   maturity+=amount;
	 }
	 
	 /**
 	 * Radius.
 	 *
 	 * @return the radius of the cell
 	 */
 	public float radius(){
		 return 0;
	 }
}
