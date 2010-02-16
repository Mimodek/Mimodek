package mimodek.tracking;

import java.util.ArrayList;

import mimodek.Mimo;
import mimodek.Simulation1;
import processing.core.PApplet;
import processing.core.PVector;

public class Tracking extends Thread {

	TrackingListener listener;

	int screenHeight;
	int screenWidth;
	
	PVector directionChangeRange = new PVector(0.1f, 6.1f);
	float maxSpeed = 10;

	ArrayList<Mimo> mimos;

	public boolean running;

	public Tracking(int screenHeight, int screenWidth) {
		this.screenHeight = screenHeight;
		this.screenWidth = screenWidth;
		
		mimos = new ArrayList<Mimo>();
		running = true;
	}

	public void addMimo(PVector pos) {
		mimos.add(new Mimo(pos));
		if(listener!=null)
			listener.trackingEvent(new TrackingInfo(mimos.size() - 1, pos.x, pos.y));

	}
	
	public void setListener(TrackingListener listener){
		this.listener = listener;
	}

	void randomWalk(Mimo m) {
		//Simulation1.app.noiseSeed(System.currentTimeMillis());
		float speed = (float)Math.random()
				* maxSpeed;
		if ((float)Math.random() < 0.99) {
			return;
		}
		float a = PApplet.map((float)Math.random(), 0, 1,
				directionChangeRange.x, directionChangeRange.y);
		m.vel = new PVector(PApplet.cos(a), PApplet.sin(a));
		m.vel.mult(speed);
	}

	private void update(Mimo m) {
		randomWalk(m);
		m.pos.add(m.vel);

	}

	// Thread run method
	public void run() {
		while (running) {
			if ((float)Math.random() < 0.1 && mimos.size()<200) {
				addMimo(new PVector(Simulation1.screenWidth / 2,
						Simulation1.screenHeight / 2));

			}
			update();
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void update() {
		for (int i = -1; ++i < mimos.size();) {
			Mimo m = mimos.get(i);
			if (m != null) {
				update(m);
				if (m.pos.x <= m.radius || m.pos.x > screenWidth
						|| m.pos.y < 0 || m.pos.y > screenHeight) {
					mimos.set(i, null);
				} else {
					if(listener!=null)
						listener.trackingEvent(new TrackingInfo(i, m.pos.x, m.pos.y));
				}
			}

		}
	}
}
