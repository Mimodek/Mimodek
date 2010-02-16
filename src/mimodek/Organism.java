package mimodek;

import processing.core.PApplet;
import processing.core.PImage;

public class Organism {
	public Mimo[] mimos;
	PImage texture;
	float textureScale; 

	Organism() {
		mimos = new Mimo[0];
	}

	public int cellCount() {
		return mimos.length;
	}

	public boolean attachTo(Mimo m) {
		if (mimos.length == 0) {
			// special case, we're seeding
			// m.pos.y = Simulation1.screenHeight;
			m.setParticle(Simulation1.pSim.addParticle(m.pos, 1, true));
			addCell(m);

			// Which edge are we seeding on?
			boolean left = false;
			boolean up = false;
			if (m.pos.x < Simulation1.screenWidth / 2) {
				// Left part of the screen
				left = true;
			}
			if (m.pos.y < Simulation1.screenHeight / 2) {
				// Upper part of the screen
				up = true;
			}

			float hDist = PApplet.abs((Simulation1.screenWidth / 2) - m.pos.x)
					/ (Simulation1.screenWidth / 2);
			float vDist = PApplet.abs((Simulation1.screenHeight / 2) - m.pos.y)
					/ (Simulation1.screenHeight / 2);
			System.out.println("hDist: "+hDist+" / vDist: "+vDist);
			if (left && (hDist > vDist)) {
				// closer to the left edge
				Simulation1.pSim.floor = Physics.LEFT;
				System.out.println("LEFT EDGE");
				return true;
			}
			if (!left && (hDist > vDist)) {
				// closer to the left edge
				System.out.println("RIGHT EDGE");
				Simulation1.pSim.floor = Physics.RIGHT;
				return true;
			}
			// from this point on it's either top or bottom
			System.out.println((up ? "TOP" : "BOTTOM")+" EDGE");
			Simulation1.pSim.floor = up ? Physics.UP : Physics.DOWN;
			return true;
		}

		boolean added = false;
		for (int i = -1; ++i < mimos.length;) {
			if (mimos[i].pos.dist(m.pos) <= (mimos[i].radius + m.radius) / 2) {
				float nuRadius = PApplet.map(m.pos.dist(mimos[0].pos), 0,
						PApplet.max(Simulation1.screenWidth,
								Simulation1.screenHeight), Mimo.maxRadius,
						Mimo.minRadius);
				// 
				float a = PApplet.atan2(mimos[i].pos.y - m.pos.y,
						mimos[i].pos.x - m.pos.x);
				float d = m.radius > nuRadius ? m.radius - nuRadius : nuRadius
						- m.radius;
				m.pos.x += PApplet.cos(a) * d;
				m.pos.x += PApplet.sin(a) * d;
				m.radius = nuRadius;
				if (!added) {
					m.setParticle(Simulation1.pSim.addParticle(m.pos, m.radius
							/ Mimo.maxRadius, false));
					addCell(m);
				}
				m.addNeighbour(mimos[i]);
				Simulation1.pSim.addSpring(m.particle, mimos[i].particle);
				// m.addNeighbour(mimos[i]);
				// mimos[i].addNeighbour(m);
				added = true;
			}
		}
		return added;
	}

	void addCell(Mimo m) {
		Mimo[] tmp = new Mimo[mimos.length + 1];
		System.arraycopy(mimos, 0, tmp, 0, mimos.length);
		tmp[mimos.length] = m;
		mimos = tmp;
	}

	void draw() {
		Simulation1.gfx.fill(Simulation1.app.color(0, 255, 0, 50));
		// stroke(color(0,255,0));
		for (int i = -1; ++i < mimos.length;) {
			//update the position of the mimos according to the physics simulation particle they bound to
			mimos[i].pos.x = mimos[i].particle.position().x();
			mimos[i].pos.y = mimos[i].particle.position().y();
			Simulation1.texturizer.draw(mimos[i]);
			
			Simulation1.gfx.ellipse(mimos[i].pos.x, mimos[i].pos.y,
					mimos[i].radius / 3, mimos[i].radius / 3);
			// mimos[i].radius = orR;
		}
	}
}
