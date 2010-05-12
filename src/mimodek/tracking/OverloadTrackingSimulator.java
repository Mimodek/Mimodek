package mimodek.tracking;

import java.awt.event.KeyEvent;
import java.util.Enumeration;

import mimodek.facade.FacadeFactory;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

public class OverloadTrackingSimulator extends TrackingSimulator {
	class RandomWalkingParticle extends TrackingSimulator.Particle{
		
		private PVector oldVel;
		private PVector targetVel;
		private boolean turning;
		public int numSteps;
		public int current;
		public float maxSpeed = 2;
		private PVector center;
		//private boolean canWalkOut = true;
		
		public RandomWalkingParticle(PVector pos,PVector center) {
			super(pos);
			this.center = center;
			vel = new PVector((float)Math.random(),(float)Math.random());
			oldVel = new PVector(0, 0);
			targetVel = new PVector(0, 0);
			turning = false;
		}

		public void update() {
			if(out){
				//orient the mimo towards the center to get it back in the screen
				float a = (float) Math.atan2(center.y-pos.y, center.x-pos.x);
				vel.x = (float) Math.cos(a);
				vel.y = (float) Math.sin(a);
				turning = false;
			
			}else if (turning){ // compute velocity while turning
				computeTurnVel();
			}else{
				// random walk with smooth turns
				randomWalk();
			}
			pos.add(vel);
		}
		
		private void randomWalk(){
			if (Math.random() < 0.99) {
				return;
			}
			float a = (float)Math.random()*PConstants.TWO_PI;
			float speed = (float)Math.random()*0.1f;
			speed = PApplet.constrain(speed + PApplet.dist(0, 0, vel.x, vel.y),0.0f, maxSpeed);

			targetVel = new PVector(speed * PApplet.cos(a), speed* PApplet.sin(a));
			oldVel.x = vel.x;
			oldVel.y = vel.y;

			numSteps = 100;
			current = 0;
			turning = true;
		}
		
		private void computeTurnVel() {

			float x = PApplet.lerp(oldVel.x, targetVel.x, (float) (current)/ numSteps);
			float y = PApplet.lerp(oldVel.y, targetVel.y, (float) (current)/ numSteps);
			vel.x = x;
			vel.y = y;
			current++;
			if (current == numSteps)
				turning = false;
		}
		
	}
	
	public boolean paused = false;
	
	public OverloadTrackingSimulator(PApplet app, int left, int top, int particleNum) {
		super(app, left, top);
		app.registerKeyEvent(this);
		for(int i=0;i<particleNum;i++){
			particles.put(i, new RandomWalkingParticle(new PVector(left+app.random(FacadeFactory.getFacade().width),top+app.random(FacadeFactory.getFacade().height)),new PVector((left+FacadeFactory.getFacade().width/2),(top+FacadeFactory.getFacade().height/2))));
		}
		counter = particleNum;
	}
	
	public synchronized void draw(){
		super.draw();
		if(paused)
			return;
		Enumeration<Integer> e = particles.keys();
		while (e.hasMoreElements()) {
			int i = e.nextElement();
			RandomWalkingParticle p = (RandomWalkingParticle)particles.get(i);
			p.update();
			if(!isInTrackingArea(p.pos.x,p.pos.y)){
				if(!p.out){
					p.out = true;
					p.oldKey = i;
					p.toRemove = true;
				}
			}else{
					p.out = false;
					
			}
		}	
		
	}
	
	public void keyEvent(KeyEvent e) {
		if (e.getID() != KeyEvent.KEY_PRESSED)
			return;
		
		if (app.key == 'p') {
			paused = !paused;
			if(paused){
				Enumeration<Integer> k = particles.keys();
				while (k.hasMoreElements()) {
					int i = k.nextElement();
					RandomWalkingParticle p = (RandomWalkingParticle)particles.get(i);
					listener.trackingEvent(createTrackingInfo(TrackingInfo.REMOVE,i));
				}
			}
		}
	}

}
