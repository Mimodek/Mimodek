package mimodek;

import java.util.ArrayList;

import processing.core.PVector;
import traer.physics.Particle;

public class Mimo {
	public static float maxRadius = 50;
	public static float minRadius = 10;

	public PVector pos;

	public Particle particle;

	public PVector vel;

	public float radius;

	ArrayList<Mimo> neighbours;

	public boolean ancestor = false;
	public boolean entered = false;

	public Mimo(PVector pos) {
		this.pos = pos;
		this.vel = new PVector(0, 0);
		radius = minRadius + Simulation1.app.random(1)
				* (maxRadius - minRadius);
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
}
