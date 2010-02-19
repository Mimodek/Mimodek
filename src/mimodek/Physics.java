package mimodek;

import java.util.ArrayList;

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
		int n = MainHandler.organism.cellCount();// numberOfParticles();
		for (int i = -1; ++i < n;) {
			Mimo m = MainHandler.organism.getCell(i);
			Vector3D p = m.particle.position();
			switch (floor) {
			case RIGHT:
				if (p.x() + m.radius > MainHandler.screenWidth)
					p.setX(MainHandler.screenWidth - m.radius);
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
				if (p.y() + m.radius > MainHandler.screenHeight)
					p.setY(MainHandler.screenHeight - m.radius);
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
		MainHandler.gfx.pushStyle();
		MainHandler.gfx.stroke(c);
		MainHandler.gfx.strokeWeight(strokeW);
		int n = physics.numberOfSprings();
		for (int i = -1; ++i < n;) {
			Spring s = physics.getSpring(i);
			Particle p1 = s.getOneEnd();
			Particle p2 = s.getTheOtherEnd();
			MainHandler.gfx.line(p1.position().x(), p1.position().y(), p2
					.position().x(), p2.position().y());
		}
		MainHandler.gfx.popStyle();
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
	public boolean addSpring(Particle a, Particle b) {
		if (areAlreadyConnected(a, b))
			return false;
		physics.makeSpring(a, b, springStrength, springDamping, PApplet.dist(a
				.position().x(), a.position().y(), b.position().x(), b
				.position().y()));
		return true;
	}
	
	public boolean addSpring(Particle a, Particle b, float restLength) {
		if (areAlreadyConnected(a, b))
			return false;
		physics.makeSpring(a, b, springStrength, springDamping, restLength);
		return true;
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

	public void changeSpringLength(Particle a, Particle b, float length) {
		for (int i = -1; ++i < physics.numberOfSprings();) {
			Particle a2 = physics.getSpring(i).getOneEnd();
			Particle b2 = physics.getSpring(i).getTheOtherEnd();
			if ((a == a2 && b == b2) || (a == b2 && b == a2)) {
				// found the spring
				physics.getSpring(i).setRestLength(length);
				return;
			}
		}
		b.position().clear();
		b.position().add(a.position());
	}


	public void removeParticleAndAttachedSprings(Particle old) {
		//ArrayList<Particle> particles = new ArrayList<Particle>();
		
		System.out.println("Removing attached springs. Current spring count:"+physics.numberOfSprings());
		//Remove all spring of the old node
		int c = 0;
		for (int i = -1; ++i < physics.numberOfSprings();) {
			Particle a2 = physics.getSpring(i).getOneEnd();
			Particle b2 = physics.getSpring(i).getTheOtherEnd();
			if ((old == a2 || old == b2)) {
				physics.removeSpring(i--);
				c++;
			}
		}
		
		System.out.println("Done cutting. Removed:"+c);
		//finally remove the particle
		physics.removeParticle(old);
		System.out.println("Done. New spring count:"+physics.numberOfSprings());
	}
}
