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


public class Cell {
	 PVector pos;
	 float maturity = 0;
	 float currentMaturity = 0;
	 //float radius = 0;
	 Cell anchor = null;
	float angleToAnchor = 0;
	float polarX = 0;
	float polarY = 0;
	float zLevel = 0;
	
	long lastUpdate;
	 
	  public Cell(PVector pos){
	    this.pos = pos;
	    lastUpdate = System.currentTimeMillis();
	  }
	  
	  void setAnchor(Cell anchor){
	    this.anchor = anchor;
	    angleToAnchor = PApplet.atan2(pos.y-anchor.pos.y,pos.x-anchor.pos.x);
	    polarX = PApplet.cos(angleToAnchor);
	    polarY = PApplet.sin(angleToAnchor);
	  }
	  
	  public void update(PApplet app){
			 
	}
	 
	 public void draw(PApplet app){
		 
	 }
	 /*{
	   if(anchor!=null)
	    ellipse(anchor.pos.x+currentMaturity*(pos.x-anchor.pos.x),anchor.pos.y+currentMaturity*(pos.y-anchor.pos.y),radius*currentMaturity*2,radius*currentMaturity*2);
	   else
	    ellipse(pos.x,pos.y,radius*currentMaturity*2,radius*currentMaturity*2);
	 } */
	 
	 void feed(float amount){
	   maturity+=amount;
	 }
	 
	 public float radius(){
		 return 0;
	 }
}
