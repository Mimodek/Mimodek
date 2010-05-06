package mimodek.decorator;

import mimodek.MimodekObject;
import mimodek.configuration.Configurator;
import processing.core.PVector;

public class ActiveMimo extends MimodekObjectDecorator {
	public PVector vel = null;
	public PVector targetPos = null;
	public long lastActiveMovement;
	public long createdAt;

	
	public ActiveMimo(MimodekObject decoratedObject) {
		super(decoratedObject);
		vel = new PVector(0,0);
		targetPos = new PVector(0,0);
	}
	
	public void setTargetPos(PVector tPos){
		targetPos = tPos;
	}

	public void update() {
		activeMimoMove();
		PVector p = getPos();
		p.add(vel);
		setPos(p);
		if(getDiameter()<Configurator.getFloatSetting("mimosMaxRadius"))
	    	setDiameter(getDiameter()+0.1f);
	}
	
	private void activeMimoMove() {
		float easing = Configurator.getFloatSetting("mimosEasing");
		vel.x = easing * (targetPos.x - getPosX());
		vel.y = easing * (targetPos.y - getPosY());
	}

}
