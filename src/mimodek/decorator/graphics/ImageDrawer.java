package mimodek.decorator.graphics;

import mimodek.MimodekObject;
import mimodek.configuration.Configurator;
import mimodek.decorator.ActiveMimo;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public class ImageDrawer extends MimodekObjectGraphicsDecorator {

	protected SquareTexture image;
	protected ImageDrawer(MimodekObject decoratedObject) {
		super(decoratedObject);
	}
	
	public ImageDrawer(MimodekObject decoratedObject, PImage image, int color, PApplet app) throws NoImageException{
		super(decoratedObject);
		if(image == null)
			throw new NoImageException();
		
		setDrawingData(new SimpleDrawingData(color));
		this.image = new SquareTexture(image,app);
	}
	

	@Override
	public void draw(PApplet app) {
		draw(app.g);
	}

	@Override
	public PImage toImage(PApplet app) {
		return image.image;
	}

	@Override
	protected void draw(PGraphics gfx) {
		gfx.pushMatrix();
		gfx.pushStyle();
		gfx.translate(getPosX(),getPosY());
		if (!(decoratedObject instanceof ActiveMimo)) {
			gfx.colorMode(PApplet.HSB,255);
			int c = getDrawingData().getColor();
			c = gfx.color(gfx.hue(c), gfx.saturation(c),Configurator.getIntegerSetting("ancestorBrightness"));
			//c = gfx.color(0, 0,Configurator.getIntegerSetting("ancestorBrightness"));
			gfx.tint(c);			
			image.draw(getDiameter() / Configurator.getFloatSetting("mimosMaxRadius"));
			gfx.colorMode(PApplet.RGB, 255);
		}else{
			gfx.tint(getDrawingData().getColor());
			
			image.draw(getDiameter() / Configurator.getFloatSetting("mimosMaxRadius"));
		}
		gfx.popStyle();
		gfx.popMatrix();	
	}
	
}
