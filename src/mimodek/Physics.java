package mimodek;

import processing.core.PApplet;
import processing.core.PVector;
import traer.physics.*;

public class Physics {

	public static final int UP = 0;
	public static final int DOWN = 1;
	public static final int RIGHT = 2;
	public static final int LEFT = 3;

	// Springs parameters
	float springStrength = 0.5f;
	float springDamping = 0.01f;
	ParticleSystem physics;
	public int floor;

	public Physics(float gravity, float drag, boolean euler) {
		physics = new ParticleSystem(gravity, drag);
		if (euler)
			physics.setIntegrator(ParticleSystem.MODIFIED_EULER);
	}

	public void setGravity(float x, float y) {
		switch (floor) {
		case RIGHT:
			physics.setGravity(y, x, 0);
			break;
		case LEFT:
			physics.setGravity(-y, x, 0);
			break;
		case UP:
			physics.setGravity(x, -y, 0);
			break;
		default:
			physics.setGravity(x, y, 0);
		}

	}

	public void update() {
		physics.tick();
		// make sure the mimos stay on the floor
		int n = Simulation1.organism.cellCount();// numberOfParticles();
		for (int i = -1; ++i < n;) {
			Mimo m = Simulation1.organism.mimos[i];
			Vector3D p = m.particle.position();
			switch (floor) {
			case RIGHT:
				if (p.x() + m.radius > Simulation1.screenWidth)
					p.setX(Simulation1.screenWidth - m.radius);
				break;
			case LEFT:
				if (p.x() - m.radius < 0)
					p.setX(m.radius);
				break;
			case UP:
				if (p.y() - m.radius < 0)
					p.setY(m.radius);
				break;
			case DOWN:
				if (p.y() + m.radius > Simulation1.screenHeight)
					p.setY(Simulation1.screenHeight - m.radius);
				break;
			}
		}

	}

	public Particle addParticle(PVector pos, float mass, boolean fixed) {
		Particle p = physics.makeParticle(mass, pos.x, pos.y, 0);
		if (fixed)
			p.makeFixed();
		return p;
	}

	/*
	 * Draw all springs as lines
	 */
	public void drawSprings(int c, float strokeW) {
		Simulation1.gfx.pushStyle();
		Simulation1.gfx.stroke(c);
		Simulation1.gfx.strokeWeight(strokeW);
		int n = physics.numberOfSprings();
		for (int i = -1; ++i < n;) {
			Spring s = physics.getSpring(i);
			Particle p1 = s.getOneEnd();
			Particle p2 = s.getTheOtherEnd();
			Simulation1.gfx.line(p1.position().x(), p1.position().y(), p2
					.position().x(), p2.position().y());
		}
		Simulation1.gfx.popStyle();
	}

	public void changeSprings(float springStrength, float springDamping) {
		this.springStrength = springStrength;
		this.springDamping = springDamping;
		int n = physics.numberOfSprings();
		for (int i = -1; ++i < n;) {
			Spring s = physics.getSpring(i);
			s.setDamping(springDamping);
			s.setStrength(springStrength);
		}
	}

	public int springCount() {
		return physics.numberOfSprings();
	}

	public int particleCount() {
		return physics.numberOfParticles();
	}

	/*
	 * Does not allow to have more than 1 spring connecting two particles
	 */
	public void addSpring(Particle a, Particle b) {
		if (areAlreadyConnected(a, b))
			return;
		physics.makeSpring(a, b, springStrength, springDamping, PApplet.dist(a
				.position().x(), a.position().y(), b.position().x(), b
				.position().y()));
	}

	/*
	 * return true if two particle a and b are already connected to each other
	 * by a spring ,false otherwise
	 */
	boolean areAlreadyConnected(Particle a, Particle b) {
		for (int i = -1; ++i < physics.numberOfSprings();) {
			Particle a2 = physics.getSpring(i).getOneEnd();
			Particle b2 = physics.getSpring(i).getTheOtherEnd();
			if ((a == a2 && b == b2) || (a == b2 && b == a2))
				return true;
		}
		return false;
	}
}
