package mimodek;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;

public class Organism {
	public ArrayList<Mimo> mimos;
	PImage texture;
	float textureScale; 
	
	int maxCells = 50;

	Organism() {
		mimos = new ArrayList<Mimo>();
	}

	public int cellCount() {
		return mimos.size();
	}
	
	public Mimo getCell(int i){
		return mimos.get(i);
	}
	
	public void resolveCollision(Mimo m){
		for (int i = -1; ++i < cellCount();) {
			if (mimos.get(i).pos.dist(m.pos) <= (mimos.get(i).radius + m.radius) / 2) {
				mimos.get(i).collided = true;
				m.toStructure = mimos.get(i).pos;
			}
		}
	}

	public boolean attachTo(Mimo m) {
		if (cellCount() == 0) {
			// special case, we're seeding
			// m.pos.y = Simulation1.screenHeight;
			m.setParticle(MainHandler.pSim.addParticle(m.pos, 1, true));
			addCell(m);

			// Which edge are we seeding on?
			boolean left = false;
			boolean up = false;
			if (m.pos.x < MainHandler.screenWidth / 2) {
				// Left part of the screen
				left = true;
			}
			if (m.pos.y < MainHandler.screenHeight / 2) {
				// Upper part of the screen
				up = true;
			}

			float hDist = PApplet.abs((MainHandler.screenWidth / 2) - m.pos.x)
					/ (MainHandler.screenWidth / 2);
			float vDist = PApplet.abs((MainHandler.screenHeight / 2) - m.pos.y)
					/ (MainHandler.screenHeight / 2);
			System.out.println("hDist: "+hDist+" / vDist: "+vDist);
			if (left && (hDist > vDist)) {
				// closer to the left edge
				MainHandler.pSim.floor = Physics.LEFT;
				System.out.println("LEFT EDGE");
				return true;
			}
			if (!left && (hDist > vDist)) {
				// closer to the left edge
				System.out.println("RIGHT EDGE");
				MainHandler.pSim.floor = Physics.RIGHT;
				return true;
			}
			// from this point on it's either top or bottom
			System.out.println((up ? "TOP" : "BOTTOM")+" EDGE");
			MainHandler.pSim.floor = up ? Physics.UP : Physics.DOWN;
			return true;
		}

		boolean added = false;
		for (int i = -1; ++i < cellCount();) {
			if (mimos.get(i).pos.dist(m.pos) <= (mimos.get(i).radius + m.radius) / 2) {
				//All this commented out stuff is to set the radius of the mimo according to their distance to the seed
				/*
				float nuRadius = PApplet.map(m.pos.dist(mimos[0].pos), 0,
						PApplet.max(MainHandler.screenWidth,
								MainHandler.screenHeight), Mimo.maxRadius,
						Mimo.minRadius);
				// 
				float a = PApplet.atan2(mimos[i].pos.y - m.pos.y,
						mimos[i].pos.x - m.pos.x);
				float d = m.radius > nuRadius ? m.radius - nuRadius : nuRadius
						- m.radius;
				m.pos.x += PApplet.cos(a) * d;
				m.pos.x += PApplet.sin(a) * d;
				m.radius = nuRadius;
				*/
				if (!added) {
					m.setParticle(MainHandler.pSim.addParticle(m.pos, m.radius
							/ Mimo.maxRadius, false));
					addCell(m);
					m.father = mimos.get(i);
				}
				
				if(m.neighbours == null || m.neighbours.size()<=5){
					if(MainHandler.pSim.addSpring(m.particle, mimos.get(i).particle)){
						m.addNeighbour(mimos.get(i));
					}
				}
				// m.addNeighbour(mimos[i]);
				// mimos[i].addNeighbour(m);
				added = true;
				
			}
		}
		return added;
	}

	//Previously we were working with an array
	/*
	void addCell(Mimo m) {
		Mimo[] tmp = new Mimo[mimos.length + 1];
		System.arraycopy(mimos, 0, tmp, 0, mimos.length);
		tmp[mimos.length] = m;
		mimos = tmp;
	}*/
	
	void addCell(Mimo m){
		mimos.add(m);
	}
	
	void updateCellsSprings(Mimo m){
		//TODO: crashed once due to a null pointer: reproduce the error, fix it
		try{
			//that's where the references neighbours stored in each mimos become handy
			MainHandler.pSim.changeSpringLength(m.particle,m.father.particle,Math.abs(m.radius-m.father.radius));
			for(int i=-1;++i<m.neighbours.size();){
				MainHandler.pSim.changeSpringLength(m.particle,m.neighbours.get(i).particle,Math.abs(m.radius-m.neighbours.get(i).radius));
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	void removeCell(Mimo m){
		//remove all the possible neighbours
		for(int i=-1;++i<mimos.size();){
			mimos.get(i).removeNeighbour(m);
			if(mimos.get(i).father == m){
				mimos.get(i).father = m.father;
				float d = (m.father.radius+mimos.get(i).radius)/2;
				MainHandler.pSim.addSpring(m.father.particle,mimos.get(i).particle,d);
			}
		}
		MainHandler.pSim.removeParticleAndAttachedSprings(m.particle);
		mimos.remove(m);
	}
	
	//update and draw
	//TODO: Should it be divided in two methods? Doing everything here prevents from running through the array twice...
	void draw() {
		
		if(cellCount()>maxCells){
			//Too much cells, apply homeostasis
			Mimo m = mimos.get(1);
			m.radius--;
			//now update the length of the springs connected to this cell
			if(m.radius <=0){
				System.out.println("---");
				System.out.println("Removing cell. Current count:"+cellCount());
				//remove the cell 
				removeCell(m);
				System.out.println("Cell removed. New count:"+cellCount());
				System.out.println("Particles count:"+MainHandler.pSim.particleCount());
			}else{
				 updateCellsSprings(m);
			}
			
		}
		
		MainHandler.gfx.fill(MainHandler.app.color(0, 255, 0, 50));
		// stroke(color(0,255,0));
		
		for (int i =-1; ++i < cellCount();) {
			Mimo m = mimos.get(i);
			//update the position of the mimos according to the physics simulation particle they bound to
			m.pos.x = m.particle.position().x();
			m.pos.y = m.particle.position().y();
			//don't move the seed
			if(i>0 && mimos.get(i).collided){
				//shake those mimo ancestor's particle a bit
				
				 m.particle.position().add((float) (-4+Math.random()*8), 0, 0);
				 m.pos.x = mimos.get(i).particle.position().x();
				m.pos.y = mimos.get(i).particle.position().y();
				if(MainHandler.showSprings)
					MainHandler.gfx.ellipse(m.pos.x, m.pos.y,m.radius / 3, m.radius / 3);
				//TODO: this information has to be consumed somewhere;
				m.collided = false;
			}
			MainHandler.texturizer.draw(m);
		}
	}
}
