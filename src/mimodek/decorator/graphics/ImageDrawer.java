package mimodek.decorator.graphics;

import mimodek.MimodekObject;
import mimodek.configuration.Configurator;
import mimodek.decorator.ActiveMimo;
import processing.core.PApplet;
import processing.core.PImage;

public class ImageDrawer extends MimodekObjectGraphicsDecorator {

	protected SquareTexture image;
	protected ImageDrawer(MimodekObject decoratedObject) {
		super(decoratedObject);
	}
	
	public ImageDrawer(MimodekObject decoratedObject, PImage image, int color, PApplet app) {
		super(decoratedObject);
		setDrawingData(new SimpleDrawingData(color));
		this.image = new SquareTexture(image,app);
	}
	

	@Override
	public void draw(PApplet app) {
		app.pushMatrix();
		
		app.translate(getPosX(),getPosY());
		/*
		app.pushStyle();
		app.noFill();
		app.stroke(255);
		app.ellipse(0,0,getRadius()/2,getRadius()/2);
		app.popStyle();
		*/
		if (!(decoratedObject instanceof ActiveMimo)) {
			app.colorMode(PApplet.HSB,255);
			int c = getDrawingData().getColor();
			//c = app.color(app.hue(c), app.saturation(c),Configurator.getIntegerSetting("ancestorBrightness"));
			c = app.color(0, 0,Configurator.getIntegerSetting("ancestorBrightness"));
			app.tint(c);			
			image.draw(getDiameter() / Configurator.getFloatSetting("mimosMaxRadius"));
			app.colorMode(PApplet.RGB, 255);
		}else{
			app.tint(getDrawingData().getColor());
			
			image.draw(getDiameter() / Configurator.getFloatSetting("mimosMaxRadius"));
		}
		app.noTint();
		app.popMatrix();
	}

	@Override
	public PImage toImage(PApplet app) {
		return image.image;
	}
	
}
