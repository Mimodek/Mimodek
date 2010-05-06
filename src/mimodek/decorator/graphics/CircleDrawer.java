package mimodek.decorator.graphics;

import processing.core.PApplet;
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
		app.pushStyle();	
		if (!(decoratedObject instanceof ActiveMimo)) {
			app.colorMode(PApplet.HSB, 255);
			int c = getDrawingData().getColor();
			c = app.color(app.hue(c), app.saturation(c),Configurator.getIntegerSetting("ancestorBrightness"));
			app.fill(c);
		}else{
			app.fill(getDrawingData().getColor());
		}
	    app.ellipse(getPosX(),getPosY(),getDiameter(),getDiameter());
	    if (!(decoratedObject instanceof ActiveMimo)) {
	    	app.colorMode(PApplet.RGB, 255);
	    }
	    app.popStyle();
	}
	
	@Override
	public void render(PApplet app) {
		getDrawingData().setIteration(1);
	}
	

	@Override
	public PImage toImage(PApplet app) {
		PGraphics renderer = app.createGraphics((int) getDiameter()+4,(int) getDiameter()+4, PApplet.JAVA2D);//leave a little margin for rounding error 
		renderer.beginDraw();
		renderer.noStroke();
		System.out.println("Class name: "+decoratedObject.getClass().getName());
		/*if (!(decoratedObject instanceof ActiveMimo)) {
			
			renderer.colorMode(PApplet.HSB, 255);
			int c = getDrawingData().getColor();
			c = renderer.color(renderer.hue(c), renderer.saturation(c),Configurator.getIntegerSetting("ancestorBrightness"));
			renderer.fill(c);
		}else{*/
			renderer.fill(getDrawingData().getColor());
		//}
		renderer.ellipse(getDiameter()/2+2,getDiameter()/2+2,getDiameter(),getDiameter());
	    /*if (!(decoratedObject instanceof ActiveMimo)) {
	    	renderer.colorMode(PApplet.RGB, 255);
	    }*/
		renderer.endDraw();
		PGraphics gfx = renderer;
		PImage img = gfx.get();
		gfx.filter(PApplet.GRAY);
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

}
