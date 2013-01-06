package MimodekV2.debug;

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

import MimodekV2.Mimodek;
import processing.core.PApplet;

// TODO: Auto-generated Javadoc
/**
 * The Class MimodekApplet.
 */
public class MimodekApplet extends PApplet {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The mimodek. */
	Mimodek mimodek;
	
	/* (non-Javadoc)
	 * @see processing.core.PApplet#setup()
	 */
	@Override
	public void setup(){
		  //Resolution to display on Media lab prado facade
		  size(1024, 768, OPENGL);
		//Using custom capabilities (accumulation buffer,...)
		//size(1024, 768, "MimodekV2.graphics.MimodekPGraphicsOpenGL");
		//size(1024, 1024, "MimodekV2.graphics.MimodekPGraphicsOpenGL");
		  //To film in PAL
		 // size(720,576, OPENGL);
		
		  frameRate(30);
		  println("Start");
		  mimodek = new Mimodek(this);
		  
		}

		/* (non-Javadoc)
		 * @see processing.core.PApplet#draw()
		 */
		@Override
		public void draw(){
		  background(0.3f);
		}

		/* (non-Javadoc)
		 * @see processing.core.PApplet#mouseDragged()
		 */
		@Override
		public void mouseDragged() {
				mimodek.mouseDragged();
			}

		/* (non-Javadoc)
		 * @see processing.core.PApplet#keyPressed()
		 */
		@Override
		public void keyPressed() {
		  		mimodek.keyPressed();
		}
}
