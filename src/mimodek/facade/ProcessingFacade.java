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

import processing.core.PApplet;
import processing.core.PVector;

// TODO: Auto-generated Javadoc
/**
 * Minimal implementation of a facade. It does nothing really interesting, it just set the dimensions to the dimensions of the parent sketch...
 * @author Jonsku
 */
public class ProcessingFacade extends Facade {

	/**
	 * Instantiates a new processing facade.
	 *
	 * @param app the app
	 */
	public ProcessingFacade(PApplet app) {
		super(app);
		leftOffset = 0;
		topOffset = 0;
		width = app.width;
		halfWidth = width/2;
		height = app.height;
		halfHeight = height/2;
		// calculate ratios
		wRatio = 1;
		hRatio = 1;
	}

	/* (non-Javadoc)
	 * @see mimodek.facade.Facade#draw()
	 */
	@Override
	public void draw() {
		//does nothing...
	}

	/* (non-Javadoc)
	 * @see mimodek.facade.Facade#isInTheScreen(processing.core.PVector, float)
	 */
	@Override
	public boolean isInTheScreen(PVector coordinate, float margin) {
		return isInTheScreen(coordinate.x, coordinate.y, margin);
	}

	/* (non-Javadoc)
	 * @see mimodek.facade.Facade#isInTheScreen(processing.core.PVector)
	 */
	@Override
	public boolean isInTheScreen(PVector coordinate) {
		return isInTheScreen(coordinate.x, coordinate.y);
	}

	/* (non-Javadoc)
	 * @see mimodek.facade.Facade#isInTheScreen(float, float, float)
	 */
	@Override
	public boolean isInTheScreen(float x, float y, float margin) {
		margin+=border;
		return x>=margin && x<width-margin && y>=margin && y<height-margin;
	}

	/* (non-Javadoc)
	 * @see mimodek.facade.Facade#isInTheScreen(float, float)
	 */
	@Override
	public boolean isInTheScreen(float x, float y) {
		return isInTheScreen(x, y, 0);
	}

}
