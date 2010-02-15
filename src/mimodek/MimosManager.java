package mimodek;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PVector;

import mimodek.*;
import mimodek.tracking.TrackingInfo;
import mimodek.tracking.TrackingListener;

public class MimosManager implements TrackingListener {

	PVector directionChangeRange = new PVector(0.1f, 6.1f);
	float maxSpeed = 10;

	ArrayList<Mimo> mimos;

	public MimosManager() {
		mimos = new ArrayList<Mimo>();
	}

	//TODO: remove if not needed
	public void addMimo(PVector pos) {
		mimos.add(new Mimo(pos));
	}

	void randomWalk(Mimo m) {
		Simulation1.app.noiseSeed(Simulation1.app.millis());
		float speed = Simulation1.app.random(1)
				* maxSpeed;
		if (Simulation1.app.random(1) < 0.99) {
			return;
		}
		float a = PApplet.map(Simulation1.app.random(1), 0, 1,
				directionChangeRange.x, directionChangeRange.y);
		m.vel = new PVector(PApplet.cos(a), PApplet.sin(a));
		m.vel.mult(speed);
	}

	// we only update ancestor mimos
	private void update(Mimo m) {
		if (!m.ancestor)
			return;

		if (m.pos.x <= 0 || m.pos.x >= Simulation1.screenWidth || m.pos.y <= 0
				|| m.pos.y >= Simulation1.screenHeight) {
			// orient the ancestor towards the center
			float a = PApplet.atan2(Simulation1.screenHeight / 2 - m.pos.y,
					Simulation1.screenWidth / 2 - m.pos.x);
			m.vel = new PVector(PApplet.cos(a), PApplet.sin(a));
			float speed = Simulation1.app
					.noise(Simulation1.app.frameCount * 0.1f)
					* maxSpeed;
			m.vel.mult(speed);
		} else {
			randomWalk(m);
		}
		m.pos.add(m.vel);
	}

	public void update() {
		for (int i = -1; ++i < mimos.size();) {
			Mimo m = mimos.get(i);

			if (!m.ancestor
					&& (m.pos.x <= m.radius
							|| m.pos.x >= Simulation1.screenWidth - m.radius
							|| m.pos.y <= m.radius || m.pos.y >= Simulation1.screenHeight
							- m.radius)) {
				m.ancestor = true;
				if (Simulation1.organism.cellCount() == 0) {
					System.out.println("Seeded!");
					Simulation1.organism.attachTo(m);
					mimos.remove(m);
					i--;
				}
			} else if (m.ancestor) {
				update(m);
				if (Simulation1.organism.attachTo(m)) {
					mimos.remove(m);
					i--;
				}
			}
		}
	}

	public void draw() {
		for (int i = -1; ++i < mimos.size();) {
			Mimo m = (Mimo) mimos.get(i);
			m.draw();
		}
	}

	public void trackingEvent(TrackingInfo info) {
		if (info.id < mimos.size() && mimos.get(info.id) != null) {
			mimos.get(info.id).pos = new PVector(info.x, info.y);
		} else {
			mimos.add(info.id, new Mimo(new PVector(info.x, info.y)));
		}
	}

}
