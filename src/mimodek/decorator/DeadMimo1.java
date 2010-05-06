package mimodek.decorator;

import mimodek.MimodekObject;
import mimodek.configuration.Configurator;
import processing.core.PApplet;

public class DeadMimo1 extends MimodekObjectDecorator {

	public DeadMimo1(MimodekObject decoratedObject) {
		super(decoratedObject);
	}

	public void update() {
		setDiameter(decoratedObject.getDiameter()-0.025f);
		decoratedObject.update();
	}
	
	public float getDiameter(){
		return decoratedObject.getDiameter()*(1.0f/Configurator.getFloatSetting("ancestorScale"));
	}
	
	public void nourrish(ActiveMimo aM){
		
		if(PApplet.dist(getPosX(),getPosY(),aM.getPosX(),aM.getPosY())>(getDiameter()+aM.getDiameter())/2)
			return;
		//There is a collision
		float amount = getDiameter()>=0.05f?0.05f:getDiameter();
		aM.setDiameter(aM.getDiameter()+amount);
		setDiameter(getDiameter()-amount);
	}
	
	

}
