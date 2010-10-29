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

import MimodekV2.config.Configurator;
import processing.core.PApplet;
import processing.core.PVector;

/**
 * Minimal implementation of a facade. It does nothing really interesting, it just set the dimensions to the dimensions of the parent sketch...
 * @author Jonsku
 */
public class ProcessingFacade extends Facade {

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

	@Override
	public void draw() {
		//does nothing...
	}

	@Override
	public boolean isInTheScreen(PVector coordinate, float margin) {
		return isInTheScreen(coordinate.x, coordinate.y, margin);
	}

	@Override
	public boolean isInTheScreen(PVector coordinate) {
		return isInTheScreen(coordinate.x, coordinate.y);
	}

	@Override
	public boolean isInTheScreen(float x, float y, float margin) {
		return x>=margin+Configurator.getFloatSetting("mimosMaxRadius")/2 && x<width-margin-Configurator.getFloatSetting("mimosMaxRadius")/2 && y>=margin+Configurator.getFloatSetting("mimosMaxRadius")/2 && y<height-margin-Configurator.getFloatSetting("mimosMaxRadius")/2 ;
	}

	@Override
	public boolean isInTheScreen(float x, float y) {
		return isInTheScreen(x, y, 0);
	}

}
