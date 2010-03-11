package mimodek;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;

import processing.core.PApplet;
import processing.core.PImage;
import processing.xml.XMLElement;

public class Organism {
	public ArrayList<Mimo> mimos;
	PImage texture;
	float textureScale;

	Organism() {
		mimos = new ArrayList<Mimo>();
		// register this setting
		Mimodek.config.setSetting("maxCells", 400);
	}

	public int cellCount() {
		return mimos.size();
	}

	public Mimo getCell(int i) {
		return mimos.get(i);
	}

	public void resolveCollision(Mimo m) {
		for (int i = -1; ++i < cellCount();) {
			if (mimos.get(i).pos.dist(m.pos) <= (mimos.get(i).radius + m.radius) / 2) {
				mimos.get(i).collided = true;
				m.toStructure = mimos.get(i).pos;
			}
		}
	}

	public boolean attachTo(Mimo m) {
		
		if (!Mimodek.mimodek.isInTheScreen(m.pos, 0)) {
			// make sure the organism do not grow outside the screen
			return false;
		}
		float edgeDetection = Mimodek.config.getFloatSetting("edgeDetection");
		if (cellCount() == 0 && !Mimodek.mimodek.isInTheScreen(m.pos, edgeDetection)) {
			// special case, we're seeding
			// m.pos.y = Simulation1.screenHeight;
			m.setParticle(Mimodek.pSim.addParticle(m.pos, 1, true));
			addCell(m);
			// Which edge are we seeding on?
			Mimodek.pSim.floor = Mimodek.mimodek.getClosestEdge(m.pos);
			m.isSeed = true;
			System.out.print("MIMODEK says > Seeding on the ");
			switch (Mimodek.pSim.floor) {
			case Mimodek.RIGHT:
				System.out.print("RIGHT");
				break;
			case Mimodek.LEFT:
				System.out.print("LEFT");
				break;
			case Mimodek.BOTTOM:
				System.out.print("BOTTOM");
				break;
			default:
				System.out.print("TOP");
			}
			System.out.println(" edge.");
			//save a snapshot
			Mimodek.takeSnapShot = true;
			return true;
		}
		float scale = 1 / Mimodek.config.getFloatSetting("ancestorScale");
		boolean added = false;
		for (int i = -1; ++i < cellCount();) {
			if (mimos.get(i).pos.dist(m.pos) <= (mimos.get(i).radius + m.radius)
					* scale / 2) {
				// All this commented out stuff is to set the radius of the mimo
				// according to their distance to the seed
				/*
				 * float nuRadius = PApplet.map(m.pos.dist(mimos[0].pos), 0,
				 * PApplet.max(MainHandler.screenWidth,
				 * MainHandler.screenHeight), Mimo.maxRadius, Mimo.minRadius);
				 * // float a = PApplet.atan2(mimos[i].pos.y - m.pos.y,
				 * mimos[i].pos.x - m.pos.x); float d = m.radius > nuRadius ?
				 * m.radius - nuRadius : nuRadius - m.radius; m.pos.x +=
				 * PApplet.cos(a) * d; m.pos.x += PApplet.sin(a) * d; m.radius =
				 * nuRadius;
				 */

				if (!added) {
					m.setParticle(Mimodek.pSim.addParticle(m.pos, m.radius
							/ Mimodek.config.getFloatSetting("mimosMaxRadius"),
							false));
					addCell(m);

					if (mimos.get(i).isSeed || mimos.get(i).father != null) {
						m.father = mimos.get(i);
						Mimodek.pSim.addSpring(m.particle,
								mimos.get(i).particle);
					}
				}
				if (m.father == null) {
					if (mimos.get(i).isSeed || mimos.get(i).father != null) {
						m.father = mimos.get(i);
						Mimodek.pSim.addSpring(m.particle,
								mimos.get(i).particle);
					}
				}
				if ((m.neighbours == null || m.neighbours.size() <= 3)
						&& (mimos.get(i).neighbours == null || mimos.get(i).neighbours
								.size() <= 3)) {
					if (Mimodek.pSim.addSpring(m.particle,
							mimos.get(i).particle)) {
						m.addNeighbour(mimos.get(i));
					}
				}
				// m.addNeighbour(mimos[i]);
				// mimos[i].addNeighbour(m);
				added = true;

			}
		}
		if(added){
			//save a snapshot
			Mimodek.takeSnapShot = true;
		}
		return added;
	}

	void addCell(Mimo m) {
		mimos.add(m);
	}

	void updateCellsSprings(Mimo m) {
		// TODO: crashed once due to a null pointer: reproduce the error, fix it
		try {
			// that's where the references neighbours stored in each mimos
			// become handy
			Mimodek.pSim.changeSpringLength(m.particle, m.father.particle, Math
					.abs(m.radius - m.father.radius));
			for (int i = -1; ++i < m.neighbours.size();) {
				Mimodek.pSim.changeSpringLength(m.particle,
						m.neighbours.get(i).particle, Math.abs(m.radius
								- m.neighbours.get(i).radius));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void removeCell(Mimo m) {
		// remove all the possible neighbours
		for (int i = -1; ++i < mimos.size();) {
			mimos.get(i).removeNeighbour(m);
			if (mimos.get(i).father == m) {
				mimos.get(i).father = m.father;
				float d = (m.father.radius + mimos.get(i).radius) / 2;
				Mimodek.pSim.addSpring(m.father.particle,
						mimos.get(i).particle, d);
			}
		}
		Mimodek.pSim.removeParticleAndAttachedSprings(m.particle);
		mimos.remove(m);
	}

	// update and draw
	void draw() {
		if (cellCount() > Mimodek.config.getIntegerSetting("maxCells")) {
			// Too much cells, apply homeostasis
			Mimo m = mimos.get(1);
			m.radius--;
			// now update the length of the springs connected to this cell
			if (m.radius <= 0) {
				removeCell(m);
				//save a snapshot
				Mimodek.takeSnapShot = true;
			} else {
				updateCellsSprings(m);
			}

		}

		Mimodek.gfx.fill(Mimodek.app.color(0, 255, 0, 50));
		// stroke(color(0,255,0));

		for (int i = -1; ++i < cellCount();) {
			Mimo m = mimos.get(i);
			// update the position of the mimos according to the physics
			// simulation particle they are bound to

			/*if (!Mimodek.mimodek.isInTheScreen(m.particle.position().x(),
					m.particle.position().y(), 0)) {
				// Ohoho, out of the screen... No need to waste CPU on those
				removeCell(m);
				i--;
			} else {*/
				m.pos.x = m.particle.position().x();
				m.pos.y = m.particle.position().y();
				// don't move the seed
				if (i > 0 && mimos.get(i).collided) {
					// shake those mimo ancestor's particle a bit

					m.particle.position().add(
							(float) (-2f + Math.random() * 4f), 0, 0);
					m.pos.x = mimos.get(i).particle.position().x();
					m.pos.y = mimos.get(i).particle.position().y();
					if (Mimodek.config.getBooleanSetting("showSprings"))
						Mimodek.gfx.ellipse(m.pos.x, m.pos.y, m.radius / 3,
								m.radius / 3);
					// TODO: this information has to be consumed somewhere;
					m.collided = false;
				}
				Mimodek.texturizer.draw(m);
			//}
		}
	}
	
	//save to an XML file
	public void saveToFile(String fileName){
		System.out.println("MIMODEK says > Saving organism to "+fileName);
		PrintWriter output = Mimodek.app.createWriter("data/save/"+fileName); 
		output.println("<?xml version=\"1.0\"?>");
		output.println("<mimodek>");
		for(int i=0;i<mimos.size();i++){
			output.println(mimos.get(i).toXMLCell());
		}
		output.println("</mimodek>");
		output.flush(); // Writes the remaining data to the file
		output.close();
	}
	
	//load from an XML file
	public void loadFromFile(String fileName){
		mimos = new ArrayList<Mimo>();
		System.out.println("MIMODEK says > Loading organism from "+fileName);
		XMLElement xml;
		xml = new XMLElement(Mimodek.app, "data/save/"+fileName);
		int numCells = xml.getChildCount();
		System.out.println("MIMODEK says > Found "+numCells+" cells.");
		for (int i = 0; i < numCells; i++) {
			attachTo(Mimo.createtFromXML(xml.getChild(i)));
		}
		System.out.println("MIMODEK says > Created organism of "+mimos.size()+" cells.");
	}
}
