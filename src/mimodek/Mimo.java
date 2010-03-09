package mimodek;

import java.util.ArrayList;


import processing.core.PVector;
import traer.physics.Particle;

public class Mimo {

	public PVector pos;

	public Particle particle;

	public PVector vel;

	public float radius;

	ArrayList<Mimo> neighbours;
	Mimo father;

	public boolean ancestor = false;
	public boolean collided = false;
	
	public boolean hasEntered = false;

	public Object drawingData;
	
	public PVector toStructure;
	
	public float growth = 0.01f;
	
	public boolean isSeed = false;
	public PVector targetVel;
	public PVector oldVel;
	public int numSteps;
	public int current;
	public boolean turning;
	public float easing;
	public PVector targetPos;
	private float oldSpeed;
	private float targetSpeed;
	
	public long lastActiveMovement = 0;
	public long createdAt = 0;


	public Mimo(PVector pos) {
		this.pos = pos;
		this.vel = new PVector(0, 0);
		radius = Mimodek.config.getFloatSetting("mimosMinRadius");
		targetPos=new PVector(0.0f,0.0f);  
		easing=Mimodek.config.getFloatSetting("mimosEasing");
		
		oldVel=new PVector(0.0f,0.0f);
		  targetVel=new PVector(0.0f,0.0f);
		  oldSpeed=0.0f;
		  targetSpeed=0.0f;

		   turning=false;
	}

	void moveTo(float x, float y) {
		pos.x = x;
		pos.y = y;
	}

	void addNeighbour(Mimo m) {
		if (neighbours == null)
			neighbours = new ArrayList<Mimo>();

		if (!neighbours.contains(m))
			neighbours.add(m);
	}

	void setParticle(Particle p) {
		particle = p;
	}

	public void removeNeighbour(Mimo m) {
		if (neighbours == null || !neighbours.contains(m))
			return; 
		neighbours.remove(m);
	}
}
