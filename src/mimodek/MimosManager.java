package mimodek;

import java.util.Enumeration;
import java.util.Hashtable;

import processing.core.PApplet;
import processing.core.PVector;

import mimodek.tracking.TrackingInfo;
import mimodek.tracking.TrackingListener;

public class MimosManager implements TrackingListener {

	int edgeDetection = 35; //you have to love those magic numbers!
	PVector directionChangeRange = new PVector(0.1f, 6.1f);
	float maxSpeed = 10;

	// thread safe list

	Hashtable<Long, Mimo> mimos;

	public MimosManager() {
		mimos = new Hashtable<Long, Mimo>();
	}

	void randomWalk(Mimo m) {
		MainHandler.app.noiseSeed(MainHandler.app.millis());
		float speed = MainHandler.app.random(1) * maxSpeed;
		if (MainHandler.app.random(1) < 0.99) {
			return;
		}
		float a = PApplet.map(MainHandler.app.random(1), 0, 1,
				directionChangeRange.x, directionChangeRange.y);
		m.vel = new PVector(PApplet.cos(a), PApplet.sin(a));
		m.vel.mult(speed);
	}

	private void update(Mimo m) {
		if (!m.ancestor){
			//check for collision with the organism
			MainHandler.organism.resolveCollision(m);
			return;
		}
			

		if (m.pos.x <= 0 || m.pos.x >= MainHandler.screenWidth || m.pos.y <= 0
				|| m.pos.y >= MainHandler.screenHeight) {
			// orient the ancestor towards the center
			float a = PApplet.atan2(MainHandler.screenHeight / 2 - m.pos.y,
					MainHandler.screenWidth / 2 - m.pos.x);
			m.vel = new PVector(PApplet.cos(a), PApplet.sin(a));
			float speed = MainHandler.app
					.noise(MainHandler.app.frameCount * 0.1f)
					* maxSpeed;
			m.vel.mult(speed);
		} else {
			randomWalk(m);
		}
		m.pos.add(m.vel);
	}

	public void update() {
		Enumeration<Long> e = mimos.keys();
		while (e.hasMoreElements()) {
			long i = e.nextElement();
			Mimo m = mimos.get(i);
			update(m);
			if (m.ancestor) {
				
				if (MainHandler.organism.attachTo(m)) {

					mimos.remove(i);
					i--;
				}
			}
		}

	}

	public void draw() {
		Enumeration<Long> e = mimos.keys();
		while (e.hasMoreElements()) {
			long i = e.nextElement();
			Mimo m = (Mimo) mimos.get(i);
			MainHandler.texturizer.draw(m);
		}
	}

	public void trackingEvent(TrackingInfo info) {
		if(info.type == TrackingInfo.UPDATE){
			if (mimos.containsKey(info.id) && !mimos.get(info.id).ancestor) {
				mimos.get(info.id).pos = new PVector(info.x, info.y);
			} else {
				mimos.put(info.id, new Mimo(new PVector(info.x, info.y)));
			}
		}else{
			//For now, if we receive a remove event, we check if the mimo is near the edge and if it's the case change it to an ancestor
			//if not, just remove it
			Mimo m = mimos.get(info.id);
			System.out.println("Mimo: "+m.pos);
			System.out.println("Tracking: "+info.x+":"+info.y);
			if (m.pos.x <= edgeDetection || m.pos.x >= MainHandler.screenWidth-edgeDetection || m.pos.y <= edgeDetection || m.pos.y >= MainHandler.screenHeight-edgeDetection) {
				m.ancestor = true;
				if (MainHandler.organism.cellCount() == 0) {
					System.out.println("Seeded!");
					MainHandler.organism.attachTo(m);
					mimos.remove(info.id);
				}
				//we're done
				return;
			}
			mimos.remove(info.id);
			
		}
	}

}
