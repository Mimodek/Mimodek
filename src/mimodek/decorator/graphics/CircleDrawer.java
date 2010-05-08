package mimodek.decorator.graphics;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import mimodek.MimodekObject;
import mimodek.configuration.Configurator;
import mimodek.decorator.ActiveMimo;

public class CircleDrawer extends MimodekObjectGraphicsDecorator {
	
	protected CircleDrawer(MimodekObject decoratedObject) {
		super(decoratedObject);
	}
	
	public CircleDrawer(MimodekObject decoratedObject, int color) {
		super(decoratedObject);
		setDrawingData(new SimpleDrawingData(color));
	}

	@Override
	public void draw(PApplet app) {
		getDrawingData().setIteration(1);
		draw(app.g);
	}
	
	@Override
	public void render(PApplet app) {
		getDrawingData().setIteration(1);
	}
	

	@Override
	public PImage toImage(PApplet app) {
		PGraphics renderer = app.createGraphics((int) getDiameter()+4,(int) getDiameter()+4, PConstants.JAVA2D);//leave a little margin for rounding error 
		renderer.beginDraw();
		renderer.noStroke();
		renderer.translate(-getPosX()+getDiameter()/2+2, -getPosY()+getDiameter()/2+2);
		draw(renderer);
		renderer.endDraw();
		PGraphics gfx = renderer;
		//remove the background
		PImage img = gfx.get();
		gfx.filter(PConstants.GRAY);
		gfx.loadPixels();
		img.mask(gfx.pixels);
		gfx.dispose();
		return img;
	}
	
	@Override
	protected String constructorToXML(String prefix) {
		String XMLString = super.constructorToXML(prefix);
		XMLString 		+= prefix+"<param position=\"2\" type=\""+Integer.class.getName()+"\" value=\""+getDrawingData().getColor()+"\"/>\n";
		return XMLString;
	}

	@Override
	protected void draw(PGraphics gfx) {
		gfx.pushStyle();
		if (!(decoratedObject instanceof ActiveMimo)) {
			gfx.colorMode(PConstants.HSB, 255);
			int c = getDrawingData().getColor();
			c = gfx.color(gfx.hue(c), gfx.saturation(c),Configurator.getIntegerSetting("ancestorBrightness"));
			gfx.fill(c);
		}else{
			gfx.fill(getDrawingData().getColor());
		}
		gfx.ellipse(getPosX(),getPosY(),getDiameter(),getDiameter());
	   /* if (!(decoratedObject instanceof ActiveMimo)) {
	    	app.colorMode(PApplet.RGB, 255);
	    }*/
		gfx.popStyle();
	}

}
