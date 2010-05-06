package mimodek.decorator;

import mimodek.MimodekObject;
import mimodek.facade.FacadeFactory;
import processing.core.PApplet;
import processing.core.PVector;

public class RandomWalker extends MimodekObjectDecorator {
	public PVector vel;
	private PVector oldVel;
	private PVector targetVel;
	private boolean turning;
	public int numSteps;
	public int current;
	
	public static float maxSpeed = 2;
	
	public RandomWalker(MimodekObject decoratedObject) {
		super(decoratedObject);
		vel = new PVector(0,0);
		oldVel = new PVector(0, 0);
		targetVel = new PVector(0, 0);
		turning = false;
	}

	public void update() {
		if(!FacadeFactory.getFacade().isInTheScreen(getPos(), getDiameter()/2)){
			//orient the mimo towards the center to get it back in the screen
			float a = (float) Math.atan2(FacadeFactory.getFacade().height/2-getPosY(), FacadeFactory.getFacade().width/2-getPosX());
			vel.x = (float) Math.cos(a);
			vel.y = (float) Math.sin(a);
			turning = false;
		}else if (turning){ // compute velocity while turning
			computeTurnVel();
		}else{
			// random walk with smooth turns
			randomWalk();
		}
		PVector p = getPos();
		p.add(vel);
		setPos(p);
	}
	
	private void randomWalk(){
		if (Math.random() < 0.99) {
			return;
		}
		float a = (float)Math.random()*PApplet.TWO_PI;
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