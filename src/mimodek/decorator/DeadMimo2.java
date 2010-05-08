package mimodek.decorator;

import processing.core.PImage;
import mimodek.MimodekObject;
import mimodek.configuration.Configurator;
import mimodek.utils.Verbose;

public class DeadMimo2 extends MimodekObjectDecorator {
	float currentScale = 1;
	PImage activeMimosShape;
	float energy = 0;
	


	public DeadMimo2(MimodekObject decoratedObject, PImage activeMimosShape) {
		super(decoratedObject);
		energy = decoratedObject.getDiameter();
		Verbose.debug("Energy:"+energy);
		this.activeMimosShape = activeMimosShape;
	}

	public void update() {
		//setDiameter(decoratedObject.getDiameter()-0.025f);
		energy -= 0.1;
		decoratedObject.update();

	}
	
	public void render(){
		currentScale = Configurator.getFloatSetting("ancestorScale");
	}
	
	public float getEnergy() {
		return energy;
	}
	
	public void setEnergy(float e) {
		energy = e;
	}

	public float getDiameter(){
		if(Math.abs(currentScale-Configurator.getFloatSetting("ancestorScale"))>0.1)
			currentScale += currentScale<Configurator.getFloatSetting("ancestorScale")?0.05:-0.05;
		return decoratedObject.getDiameter()*(1.0f/currentScale);
	}
}
