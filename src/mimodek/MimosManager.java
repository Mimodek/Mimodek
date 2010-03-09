package mimodek;

import java.util.Enumeration;
import java.util.Hashtable;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

import mimodek.texture.Texturizer;
import mimodek.texture.SeedGradient.SeedGradientData;
import mimodek.tracking.TrackingInfo;
import mimodek.tracking.TrackingListener;

public class MimosManager implements TrackingListener {

	PVector directionChangeRange = new PVector(-0.3f, 0.3f);
	public static float maxSpeed = 2;
	// thread safe
	Hashtable<Long, Mimo> mimos;

	public MimosManager() {
		mimos = new Hashtable<Long, Mimo>();
		// register some default settings
		Mimodek.config.setSetting("maxSpeed", 10f);
		Mimodek.config.setSetting("edgeDetection", 35f);
		Mimodek.config.setSetting("blobDistanceThreshold", 10f);
		Mimodek.config.setSetting("trackingOn", true);
		Mimodek.config.setSetting("mimosMinRadius", 20f);
		Mimodek.config.setSetting("mimosMaxRadius", 70f);
		Mimodek.config.setSetting("mimosMinLifeTime", 3);//3 seconds
		Mimodek.config.setSetting("mimosMaxLifeTime", 90000);//90 seconds
	}

	synchronized void randomWalk(Mimo m) {
		Mimodek.app.noiseSeed(Mimodek.app.millis());
		if (Mimodek.app.random(1) < 0.99) {
			return;
		}
		float a = PApplet.map(Mimodek.app.random(1), 0, 1, 0,
				PApplet.TWO_PI);
		float speed = Mimodek.app.random(0.1f);
		speed = PApplet.constrain(speed + PApplet.dist(0, 0, m.vel.x, m.vel.y),
				0.0f, maxSpeed);

		m.targetVel = new PVector(speed * PApplet.cos(a), speed
				* PApplet.sin(a));
		m.oldVel.x = m.vel.x;
		m.oldVel.y = m.vel.y;

		m.numSteps = 100;
		m.current = 0;
		m.turning = true;

	}

	synchronized void walkToStruct(Mimo m) {
		Mimodek.app.noiseSeed(Mimodek.app.millis());
		if (Mimodek.app.random(1) < 0.95) {
			return;
		}

		float speed = 0.1f
				* m.easing
				* PApplet.dist(m.toStructure.x, m.toStructure.y, m.pos.x,
						m.pos.y);
		speed = PApplet.min(maxSpeed, speed);
		float a = PApplet.map(Mimodek.app.random(1), 0, 1,
				directionChangeRange.x, directionChangeRange.y);
		a = a
				+ PApplet.atan2(m.toStructure.y - m.pos.y, m.toStructure.x
						- m.pos.x);

		m.vel.x = speed * PApplet.cos(a);
		m.vel.y = speed * PApplet.sin(a);

	}

	private synchronized void activeMimoMove(Mimo m) {
		m.vel.x = m.easing * (m.targetPos.x - m.pos.x);
		m.vel.y = m.easing * (m.targetPos.y - m.pos.y);

	}

	public synchronized void computeTurnVel(Mimo m) {

		float x = PApplet.lerp(m.oldVel.x, m.targetVel.x, (float) (m.current)
				/ m.numSteps);
		float y = PApplet.lerp(m.oldVel.y, m.targetVel.y, (float) (m.current)
				/ m.numSteps);
		m.vel.x = x;
		m.vel.y = y;
		m.current++;
		if (m.current == m.numSteps)
			m.turning = false;
	}

	public synchronized int activeMimoCount() {
		return mimos.size();
	}

	private synchronized void update(Mimo m) {
		if (!m.ancestor) {
			// check for collision with the organism
			Mimodek.organism.resolveCollision(m);
			activeMimoMove(m);
			// return;
		} else {

			if (m.toStructure != null){ // pointing to the structure
				walkToStruct(m);
			}else if (m.turning){ // compute velocity while turning
				computeTurnVel(m);
			}else{
				// random walk with smooth turns
				randomWalk(m);
			}
			if(!Mimodek.mimodek.isInTheScreen(m.pos, 0)){ //m.radius
				int closestEdge = Mimodek.mimodek.getClosestEdge(m.pos);
				if(closestEdge == Mimodek.BOTTOM || closestEdge == Mimodek.TOP){
					m.vel.y *= -1;
				}else{
					m.vel.x *= -1;
				}
				m.turning = false;
			}
		}

		m.pos.add(m.vel);
	}

	public synchronized void update() {
		Enumeration<Long> e = mimos.keys();
		while (e.hasMoreElements()) {
			long i = e.nextElement();
			Mimo m = mimos.get(i);
			update(m);
			if (m.ancestor) {
				//float edgeDetection = Mimodek.config.getFloatSetting("edgeDetection");
				if (Mimodek.organism.attachTo(m)) {
					mimos.remove(i);
					i--;
				}
			}else if(Mimodek.app.millis()-m.lastActiveMovement > Mimodek.config.getIntegerSetting("mimosMaxLifeTime")*1000){
				//this active mimo hasn't moved for too long, let's remove it
				mimos.remove(i);
				i--;
			}
		}

	}

	public synchronized void draw() {
		Enumeration<Long> e = mimos.keys();
		while (e.hasMoreElements()) {
			long i = e.nextElement();
			Mimo m = (Mimo) mimos.get(i);
			Mimodek.texturizer.draw(m);
		}
	}

	private synchronized Long findClosestActiveMimo(float x, float y) {
		if (mimos.size() == 0)
			return new Long(-1);
		Enumeration<Long> e = mimos.keys();
		Long closest = e.nextElement();
		Mimo m = mimos.get(closest);
		float d = PApplet.dist(x, y, m.pos.x, m.pos.y);
		while (e.hasMoreElements()) {
			Long ind = e.nextElement();
			m = mimos.get(ind);
			float tmp = PApplet.dist(x, y, m.pos.x, m.pos.y);
			if (d > tmp) {
				d = tmp;
				closest = ind;
			}
		}
		if (d <= Mimodek.config.getFloatSetting("blobDistanceThreshold"))
			return closest;
		return new Long(-1);

	}

	public synchronized void trackingEvent(TrackingInfo info) {
		if (!Mimodek.config.getBooleanSetting("trackingOn") || info == null)
			return;

		if (info.type == TrackingInfo.UPDATE) {
				if (mimos.containsKey(info.id)) { // existing active mimo
					if (!mimos.get(info.id).ancestor) {
						// mimos.get(info.id).pos = new PVector(info.x, info.y);
						mimos.get(info.id).targetPos.x = info.x;
						mimos.get(info.id).targetPos.y = info.y;
						//refresh the counter
						mimos.get(info.id).lastActiveMovement = Mimodek.app.millis();
					}
				} else {
					Long ind = findClosestActiveMimo(info.x, info.y);
					if (ind < 0) {
							mimos.put(info.id, new Mimo(new PVector(info.x,
									info.y)));
							mimos.get(info.id).targetPos.x = info.x;
							mimos.get(info.id).targetPos.y = info.y;
							mimos.get(info.id).lastActiveMovement = Mimodek.app.millis();
							mimos.get(info.id).createdAt = mimos.get(info.id).lastActiveMovement;
					} else { // existing active mimo
						mimos.get(ind).targetPos.x = info.x;
						mimos.get(ind).targetPos.y = info.y;
						mimos.get(ind).lastActiveMovement = Mimodek.app.millis();
					}
				}
			} else {				
				Mimo m = mimos.get(info.id);
				if (m == null)
					return;
				//System.out.println("remove");
				if (Mimodek.app.millis()-m.createdAt>=Mimodek.config.getIntegerSetting("mimosMinLifeTime")*1000) {
					//the active mimo has been there long enough to be turned in to an ancestor
						m.ancestor = true;
						Texturizer.darken((SeedGradientData)m.drawingData);
						//m.pos = m.targetPos;
						if (Mimodek.organism.cellCount() == 0) {
							//set the target to the nearest edge
							if(Math.abs(m.pos.x-Mimodek.screenWidth/2) > Math.abs(m.pos.y-Mimodek.screenHeight/2)){
								//the mimo is closer to one of the side edges
								m.toStructure = new PVector((m.pos.x-Mimodek.screenWidth/2)>0?Mimodek.screenWidth:0,m.pos.y);
							}else{
								//the mimo is closer to top or bottom, seed at the bottom
								m.toStructure = new PVector(m.pos.x,Mimodek.screenHeight); 
							}
						}
						if (m.toStructure != null) {
							m.vel.x = 0.1f * m.easing * (m.toStructure.x - m.pos.x);
							m.vel.y = 0.1f * m.easing * (m.toStructure.y - m.pos.y);
						}
						// we're done
						return;
					}
				//flickering blob, let's just remove the active mimo
				mimos.remove(info.id);

			}
	}
}
