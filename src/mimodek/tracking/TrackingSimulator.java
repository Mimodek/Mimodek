package mimodek.tracking;

import java.util.ArrayList;

import mimodek.Mimodek;
import mimodek.Mimo;
import processing.core.PApplet;
import processing.core.PVector;

public class TrackingSimulator extends Thread {

	TrackingListener listener;

	int screenHeight;
	int screenWidth;

	PVector directionChangeRange = new PVector(0.1f, 6.1f);
	float maxSpeed = 10;

	ArrayList<Mimo> mimos;

	public boolean running;
	public boolean paused = false;

	public TrackingSimulator(int screenWidth, int screenHeight) {
		mimos = new ArrayList<Mimo>();
		this.screenHeight = screenHeight;
		this.screenWidth = screenWidth;
	}
	
	public void on(){
		running = true;
		
		super.start();
	}
	
	public void off(){
		running = false;
		/*try {
			this.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		//mimos = null;
	}

	public void addMimo(PVector pos) {

		float hDist = (float) Math.random();
		float vDist = (float) Math.random();
		if (hDist > vDist && hDist < 0.5) {
			pos.x = 0;
		}
		if (hDist > vDist && hDist >= 0.5) {
			pos.x = screenWidth;
		}
		if (hDist < vDist && vDist < 0.5) {
			pos.y = 0;
		}
		if (hDist < vDist && vDist >= 0.5) {
			pos.y = screenHeight;
		}
		Mimo m = new Mimo(pos);
		// direct the mimo towards the center
		float a = (float) Math.atan2(screenHeight / 2 - pos.y, screenWidth / 2
				- pos.x);
		m.vel = new PVector((float) Math.cos(a), (float) Math.sin(a));
		m.vel.mult((float) Math.random() * maxSpeed);
		mimos.add(m);
		if (listener != null)
			listener.trackingEvent(new TrackingInfo(TrackingInfo.UPDATE, mimos.size() - 1, pos.x,
					pos.y));

	}

	public void setListener(TrackingListener listener) {
		this.listener = listener;
	}

	void randomWalk(Mimo m) {
		// Simulation1.app.noiseSeed(System.currentTimeMillis());
		float speed = (float) Math.random() * maxSpeed;
		if ((float) Math.random() < 0.8) {
			return;
		}
		float a = PApplet.map((float) Math.random(), 0, 1,
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
			if(!paused && !Mimodek.config.getBooleanSetting("pause")){
				if ((float) Math.random() < 0.1 && mimos.size() < 200) {
					addMimo(new PVector(Mimodek.screenWidth / 2,
							Mimodek.screenHeight / 2));
	
				}
				update();
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//remove all the fake blobs remaining when the thread is stopping
		for (int i = -1; ++i < mimos.size();) {
			Mimo m = mimos.get(i);
			if (m != null) {
				listener.trackingEvent(new TrackingInfo(TrackingInfo.REMOVE,i, m.pos.x,m.pos.y));
			}
			mimos.remove(i--);
		}
	}

	public void update() {
		for (int i = -1; ++i < mimos.size();) {
			Mimo m = mimos.get(i);
			if (m != null) {
				update(m);
				if (m.pos.x <= 0 || m.pos.x >= screenWidth || m.pos.y <= 0
						|| m.pos.y >= screenHeight) {
					listener.trackingEvent(new TrackingInfo(TrackingInfo.REMOVE,i, m.pos.x,
							m.pos.y));
					mimos.set(i, null);
				} else {
					if (listener != null)
						listener.trackingEvent(new TrackingInfo(TrackingInfo.UPDATE,i, m.pos.x,
								m.pos.y));
				}
			}

		}
	}
}
