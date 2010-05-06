package mimodek.decorator;

import mimodek.MimodekObject;
import mimodek.configuration.Configurator;

public class DeadMimo2 extends MimodekObjectDecorator {
	float currentScale = 1;
	
	public DeadMimo2(MimodekObject decoratedObject) {
		super(decoratedObject);
	}

	public void update() {
		setDiameter(decoratedObject.getDiameter()-0.025f);
		decoratedObject.update();

	}
	
	public void render(){
		currentScale = Configurator.getFloatSetting("ancestorScale");
	}

	public float getDiameter(){
		if(Math.abs(currentScale-Configurator.getFloatSetting("ancestorScale"))>0.1)
			currentScale += currentScale<Configurator.getFloatSetting("ancestorScale")?0.05:-0.05;
		return decoratedObject.getDiameter()*(1.0f/currentScale);
	}
}
