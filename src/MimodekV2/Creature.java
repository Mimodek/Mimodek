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

import javax.media.opengl.GL;

import MimodekV2.config.Configurator;
import MimodekV2.graphics.OpenGL; //import MimodekV2.graphics.TextureManager;

import mimodek.facade.FacadeFactory;

import processing.core.PApplet;
import processing.core.PVector;

public class Creature extends Cell {

	PVector pos;
	PVector vel;
	PVector acc;
	float r;

	boolean hasFood = false;

	// float directionAngle;

	PVector lastFoodPos;

	int workCounter = 0;
	int eatCounter = 0;
	CellB cellB = null;

	boolean highLander = false;
	float energy;

	boolean readyToLift = false;
	float currentBrightness = 1f;
	float nextOffsetBrightness = 0.5f;

	PVector foodSight;

	public Creature(PVector pos, boolean highLander) {
		this(pos);
		this.highLander = highLander;
	}

	public Creature(PVector pos) {
		super(pos);
		this.pos = pos;
		acc = new PVector(0, 0);
		vel = new PVector((float) (-1 + Math.random() * 2), (float) (-1 + Math
				.random() * 2));
		// directionAngle = (float)Math.random()*PApplet.TWO_PI;
		lastUpdate = System.currentTimeMillis();
		energy = 1f;
	}

	void update() {
		long timeSinceUpdate = System.currentTimeMillis() - lastUpdate;
		// deplete enregy every 2 seconds (2000 millis)
		if (cellB == null || cellB.eatable)
			energy -= ((float) timeSinceUpdate / 2000f) * 0.03f;
		if (highLander && energy < 0.5f) {
			energy = 0.4f;
		} else if (energy <= 0f) {// it's dead...
			// System.out.println("R.I.P.");
			return;
		}
		lastUpdate = System.currentTimeMillis();
		if (hasFood) {
			Cell root = (Cell) Mimodek.aCells.get(0);
			if (pos.dist(root.pos) < 5) {

				Mimodek.genetics.addFood();
				hasFood = false;
				seek(lastFoodPos);
				/*
				 * vel.x = -vel.x; vel.y = -vel.y;
				 */
				// directionAngle = directionAngle+PApplet.PI;
				lastFoodPos = null;
				workCounter++;
			} else {
				seek(root.pos);
				// drop some scent
				Mimodek.scentMap.addScent(pos.x - 5, pos.y - 5, 10, 10, 0.2f,
						lastFoodPos);
			}
		} else {
			// when energy is half full, seek for cell B to eat
			if (energy < 0.5f && cellB == null) {
				// go eat one Bcell
				// System.out.println("I need to eat!!!");
				cellB = CellB.getEatableCell();
			}
			if (cellB != null) {
				if (cellB.eatable) {
					// go move a b cell
					if (cellB.pos.dist(pos) < 5f) {
						// eat
						float amount = cellB.maturity >= 1f - energy ? 1f - energy
								: cellB.maturity;
						cellB.maturity -= amount;
						energy += amount;
						cellB = null;
					} else {
						seek(cellB.pos);
					}
				} else {
					PVector tPos = cellB.getCreatureTargetPosition(this);
					if (tPos == null) {
						cellB = null;
					} else if (!cellB.moving) {
						if (tPos.dist(pos) < 5f) {
							readyToLift = true;
						} else {
							seek(tPos);
						}
					} else {
						// move the cell out of the way
						if (this == cellB.creatureB)
							cellB.setAnchor(this);
						if (tPos.dist(pos) < 10f) {
							readyToLift = false;
							if (!cellB.creatureA.readyToLift
									&& !cellB.creatureB.readyToLift) {
								// can drop the cell now
								cellB.drop();
							}
						} else {
							seek(tPos);
						}
					}
				}
			} else {
				// look for food for the organism
				for (int i = 0; i < Mimodek.foods.size(); i++) {
					PVector f = Mimodek.foods.get(i);
					if (f.dist(pos) < 5) {
						// food has been found
						hasFood = true;
						lastFoodPos = f;
						Mimodek.foods.remove(f);
						Mimodek.foodAvg.sub(f);
						Cell root = Mimodek.aCells.get(0);
						seek(root.pos);
						// directionAngle =
						// PApplet.atan2(root.pos.y-pos.y,root.pos.x-pos.x);
						break;
					}
				}
			}
		}
		// println("b4: "+directionAngle);
		if (cellB == null && !hasFood) {

			// if we are very close to the food we saw before and it has
			// dissapeared, look for another piece
			if (foodSight != null) {
				if(pos.dist(foodSight) < 5f)
					foodSight = null;
				else
					seek(foodSight);
			}else{
				float directionAngle = PApplet.atan2(vel.y, vel.x);
				float cD = smellAround(directionAngle);
				if (cD == directionAngle) {
					float dist = 1000;
					PVector tmp = new PVector(pos.x, pos.y);
					for (int i = 0; i < Mimodek.foods.size(); i++) {
						PVector f = Mimodek.foods.get(i);
						float distT = pos.dist(f);
						if (distT < 50 && distT < dist) {
							dist = distT;
							tmp = f;
							// directionAngle = (float)
							// ((float)PApplet.atan2(f.y-pos.y,f.x-pos.x)+((PApplet.PI/4)-Math.random()*PApplet.HALF_PI));
							// break;
						}
					}
					if (dist < 1000) {
						foodSight = tmp;
						seek(tmp);
					} else if (Math.random() > 0.8) {
						seek(new PVector((float) (Math.random() * FacadeFactory
								.getFacade().width), (float) Math.random()
								* FacadeFactory.getFacade().height));
						// directionAngle +=
						// PApplet.HALF_PI-Math.random()*PApplet.PI;
					}
				} else {
					acc.add(new PVector(PApplet.cos(cD), PApplet.sin(cD)));
					// directionAngle = cD;
				}
			}
		}
		// println("after: "+directionAngle);
		// Apply separation rule
		if (cellB != null && cellB.moving) {
			PVector mnt = maintainDistance();
			mnt.mult(1.0f); // weighted
			acc.add(mnt);
		} else if (!hasFood) {
			PVector sep = separate();
			sep.mult(5.0f); // weighted
			acc.add(sep);
		}

		if (PApplet.abs(nextOffsetBrightness - currentBrightness) < 0.005f) {
			nextOffsetBrightness = Configurator
					.getFloatSetting("CREATURE_ALPHA")
					- ((Mimodek.bCells.size() < Configurator
							.getIntegerSetting("CREATURE_DIM_THRESHOLD") ? Configurator
							.getFloatSetting("CREATURE_ALPHA")
							: Configurator.getFloatSetting("CREATURE_ALPHA") / 2f)
							* (float) Math.random() * Configurator
							.getFloatSetting("CREATURE_ALPHA_VARIATION"));
		}
		currentBrightness += nextOffsetBrightness > currentBrightness ? 0.005f
				: -0.005f;

		// Update velocity
		vel.add(acc);
		// Limit speed
		if(cellB != null && this == cellB.creatureB){
			vel.limit(Configurator.getFloatSetting("CREATURE_MAXSPEED")* PApplet.min(1f, cellB.creatureA.energy));
		}else{
			vel.limit(Configurator.getFloatSetting("CREATURE_MAXSPEED")* PApplet.min(1f, energy));
		}
		if (!FacadeFactory.getFacade().isInTheScreen(PVector.add(pos, acc),
				Configurator.getFloatSetting("CREATURE_SIZE"))) {
			seek(new PVector(
					(float) (Math.random() * FacadeFactory.getFacade().width),
					(float) Math.random() * FacadeFactory.getFacade().height));
		}
		pos.add(vel);
		// Reset accelertion to 0 each cycle
		acc.mult(0);

	}

	// compare position with other creatures and steer away if too close
	PVector separate() {
		float desiredseparation = Configurator
				.getFloatSetting("CREATURE_DISTANCE_BETWEEN");
		PVector sum = new PVector(0, 0, 0);
		int count = 0;
		// For every boid in the system, check if it's too close
		for (int i = 0; i < Mimodek.creatures.size(); i++) {
			Creature other = Mimodek.creatures.get(i);
			float d = pos.dist(other.pos);
			// If the distance is greater than 0 and less than an arbitrary
			// amount (0 when you are yourself)
			if ((d > 0) && (d < desiredseparation)) {
				// Calculate vector pointing away from neighbor
				PVector diff = PVector.sub(pos, other.pos);
				diff.normalize();
				diff.div(d); // Weight by distance
				sum.add(diff);
				count++; // Keep track of how many
			}
		}
		// Average -- divide by how many
		if (count > 0) {
			sum.div((float) count);
		}
		return sum;
	}

	// maintain distance when moving cell b
	PVector maintainDistance() {
		PVector constraint = new PVector(0, 0);
		if ((this == cellB.creatureA && PApplet.abs(pos
				.dist(cellB.creatureB.pos)
				- cellB.getSize()) > 5f)
				|| (this == cellB.creatureB && PApplet.abs(pos
						.dist(cellB.creatureA.pos)
						- cellB.getSize()) > 5f)) {
			float a = 0;
			if (this == cellB.creatureA) {
				if (pos.dist(cellB.creatureB.pos) - cellB.getSize() > 0) {
					a = PApplet.atan2(cellB.creatureB.pos.y - pos.y,
							cellB.creatureB.pos.x - pos.x);
				} else {
					a = PApplet.atan2(pos.y - cellB.creatureB.pos.y, pos.x
							- cellB.creatureB.pos.x);
				}
			} else {
				if (pos.dist(cellB.creatureA.pos) - cellB.getSize() > 0) {
					a = PApplet.atan2(cellB.creatureA.pos.y - pos.y,
							cellB.creatureA.pos.x - pos.x);
				} else {
					a = PApplet.atan2(pos.y - cellB.creatureA.pos.y, pos.x
							- cellB.creatureA.pos.x);
				}
			}
			constraint = new PVector(PApplet.cos(a), PApplet.sin(a));
		}
		return constraint;
	}

	// goes toward a point
	void seek(PVector target) {
		acc.add(steer(target, false));
	}

	// A method that calculates a steering vector towards a target
	// Takes a second argument, if true, it slows down as it approaches the
	// target
	PVector steer(PVector target, boolean slowdown) {
		PVector steer; // The steering vector
		PVector desired = PVector.sub(target, pos); // A vector pointing from
		// the location to the
		// target
		float d = desired.mag(); // Distance from the target is the magnitude of
		// the vector
		// If the distance is greater than 0, calc steering (otherwise return
		// zero vector)
		if (d > 0) {
			// Normalize desired
			desired.normalize();
			// Two options for desired vector magnitude (1 -- based on distance,
			// 2 -- maxspeed)
			if ((slowdown) && (d < 100.0))
				desired.mult(Configurator.getFloatSetting("CREATURE_MAXSPEED")
						* PApplet.min(1f, energy) * (d / 100.0f)); // This
			// damping
			// is
			// somewhat
			// arbitrary
			else
				desired.mult(Configurator.getFloatSetting("CREATURE_MAXSPEED")
						* PApplet.min(1f, energy));
			// Steering = Desired minus Velocity
			steer = PVector.sub(desired, vel);
			steer.limit(Configurator.getFloatSetting("CREATURE_STEER_FORCE")); // Limit
			// to
			// maximum
			// steering
			// force
		} else {
			steer = new PVector(0, 0);
		}
		return steer;
	}

	float smellAround(float direction) {
		// println("smell");
		PVector p = Mimodek.scentMap.getSmellInRect(pos.x - 5, pos.y - 5, 10,
				10);

		if (p != null && p.dist(pos) < 5) {
			return direction;
		}

		if (p != null && (p.x != 0 || p.y != 0)) {
			return PApplet.atan2(p.y - pos.y, p.x - pos.x);
		}
		return direction;
	}

	public void draw(PApplet app) {
		/*
		 * pos.x += PApplet.cos(directionAngle); pos.y +=
		 * PApplet.sin(directionAngle);
		 */
		// scentMap.addScent(pos.x-5, pos.y-5, 10,10,0.1);

		float s = Configurator.getFloatSetting("CREATURE_SIZE")
				* Configurator.getFloatSetting("GLOBAL_SCALING");
		/*
		 * if(!FacadeFactory.getFacade().isInTheScreen(pos, s)){
		 * directionAngle+=PApplet.PI; }
		 */
		GL gl = OpenGL.gl;
		for (int i = 0; i < 4; i++) {
			gl.glPushMatrix();
			// gl.glTranslatef(pos.x+(-0.5f+(float)Math.random()),
			// pos.y+(-0.5f+(float)Math.random()), 0f);
			gl.glTranslatef(pos.x, pos.y, 0f);

			if (hasFood) {
				OpenGL.pointSprite(Mimodek.CREATURE_TEXTURE, s, Configurator
						.getFloatSetting("CREATURE_FULL_R"), Configurator
						.getFloatSetting("CREATURE_FULL_G"), Configurator
						.getFloatSetting("CREATURE_FULL_B"), currentBrightness);
			} else {
				// int c=
				// TemperatureColorRanges.getColor(Configurator.getFloatSetting("DATA_TEMPERATURE"));
				// OpenGL.pointSprite(Mimodek.CREATURE_TEXTURE, s, app.red(c),
				// app.green(c), app.blue(c),1f);
				OpenGL.pointSprite(Mimodek.CREATURE_TEXTURE, s, Configurator
						.getFloatSetting("CREATURE_R"), Configurator
						.getFloatSetting("CREATURE_G"), Configurator
						.getFloatSetting("CREATURE_B"), currentBrightness);
			}

			gl.glPopMatrix();
		}
	}

	public static Creature createHighLanderCreature(boolean hL) {
		Cell root = Mimodek.aCells.get(0);
		Creature c = new Creature(new PVector(root.pos.x
				+ (-25.0f + (float) Math.random() * 50f), root.pos.y
				+ (-25.0f + (float) Math.random() * 50f)), hL);
		Mimodek.creatures.add(c);
		return c;
	}

	public static Creature createCreature() {
		return createHighLanderCreature(false);
	}

	public static void goEatSomeSoftCell() {
		Creature c = Mimodek.creatures.get(0);
		int i = 1;
		while (c.hasFood && i < Mimodek.creatures.size()) {
			c = Mimodek.creatures.get(i++);
		}
		if (!c.hasFood) {
			c.workCounter = 10;
		}
	}

	@Override
	public float radius() {
		return Configurator.getFloatSetting("CREATURE_SIZE");
	}

	public static void setOpenGlState() {
		GL gl = OpenGL.gl;
		// disable depth testing
		// gl.glDisable(GL.GL_DEPTH_TEST);
		// gl.glEnable(GL.GL_BLEND); // enabling blend
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE); // defining additive

		// gl.glEnable(GL.GL_BLEND); // enabling blend
		// gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE); // defining additive
		// gl.glActiveTexture(GL.GL_TEXTURE1);
		gl.glBindTexture(GL.GL_TEXTURE_2D, OpenGL.textures.get(
				Mimodek.CREATURE_TEXTURE).getTextureObject());

		gl.glEnable(GL.GL_TEXTURE_2D);

		// gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_ADD);
		// enable point sprites
		gl.glEnable(GL.GL_POINT_SPRITE);

		// make em smooth
		// gl.glEnable(GL.GL_POINT_SMOOTH);
		// gl.glHint(GL.GL_POINT_SMOOTH_HINT, GL.GL_NICEST);
		// set up auto tex coords, point sprites wont work without it
		gl.glTexEnvi(GL.GL_POINT_SPRITE, GL.GL_COORD_REPLACE, GL.GL_TRUE);
	}

	public static void unsetOpenGlState() {
		GL gl = OpenGL.gl;
		// gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDisable(GL.GL_POINT_SPRITE);
		gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
		gl.glDisable(GL.GL_TEXTURE_2D);
	}
}
