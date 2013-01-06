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

import MimodekV2.config.Configurator;
import MimodekV2.graphics.OpenGL;
import processing.core.PApplet;
import processing.core.PVector;

// TODO: Auto-generated Javadoc
/**
 * The Class Food.
 */
public class Food extends PVector {
	
	/**
	 * Instantiates a new food.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param f the  
	 */
	public Food(float x, float y, float f) {
		super(x, y, f);
	}

	/**
	 * Draw.
	 *
	 * @param app the app
	 */
	public void draw(PApplet app) {
		OpenGL.color(Configurator.getFloatSetting("FOOD_R"),Configurator.getFloatSetting("FOOD_G"),Configurator.getFloatSetting("FOOD_B"),1f);
		OpenGL.circle(x, y, z*Configurator.getFloatSetting("FOOD_SIZE"), z*Configurator.getFloatSetting("FOOD_SIZE"));
	}

	/**
	 * Drop food.
	 *
	 * @param x the x
	 * @param y the y
	 */
	public static void dropFood(float x, float y) {
		int maxFood = Configurator.getIntegerSetting("FOOD_MAX");
		if(Mimodek.foods.size() >= maxFood){
			Mimodek.foods.remove(0);
		}
		Mimodek.foods.add(new Food(x, y, 1f+(float)Math.random()*0.5f));
		Mimodek.foodAvg.add(Mimodek.foods
				.get(Mimodek.foods.size() - 1));
	}
}
