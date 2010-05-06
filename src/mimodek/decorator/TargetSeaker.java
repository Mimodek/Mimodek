package mimodek.decorator;

import mimodek.MimodekObject;
import mimodek.configuration.Configurator;
import mimodek.facade.FacadeFactory;
import processing.core.PApplet;
import processing.core.PVector;

public class TargetSeaker extends MimodekObjectDecorator {

	public PVector toStructure;
	private PVector vel;
	public static float maxSpeed = 2;
	PVector directionChangeRange = new PVector(-0.3f, 0.3f);

	public TargetSeaker(MimodekObject decoratedObject) {
		super(decoratedObject);
		vel = new PVector(0,0);
	}

	public void update() {
		if(!FacadeFactory.getFacade().isInTheScreen(getPos(), getDiameter())){
			//orient the mimo towards the center to get it back in the screen
			float a = (float) Math.atan2(FacadeFactory.getFacade().height/2-getPosY(), FacadeFactory.getFacade().width/2-getPosX());
			vel.x = (float) Math.cos(a);
			vel.y = (float) Math.sin(a);
		}else{
			walkToStruct();
		}
		PVector p = getPos();
		p.add(vel);
		setPos(p);
	}
	
	private void walkToStruct() {
		if (Math.random() < 0.95) {
			return;
		}
		float easing = Configurator.getFloatSetting("mimosEasing");
		float speed = 0.1f* easing * PApplet.dist(toStructure.x, toStructure.y, getPosX(),
						getPosY());
		speed = PApplet.min(maxSpeed, speed);
		float a = (float)(directionChangeRange.x + Math.random() * directionChangeRange.y);
		a = a + PApplet.atan2(toStructure.y - getPosY(), toStructure.x - getPosX());

		vel.x = speed * PApplet.cos(a);
		vel.y = speed * PApplet.sin(a);

	}

}
