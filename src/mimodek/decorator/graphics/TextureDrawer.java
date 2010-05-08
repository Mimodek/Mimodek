package mimodek.decorator.graphics;

import mimodek.MimodekObject;
import mimodek.configuration.Configurator;
import mimodek.decorator.ActiveMimo;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;

public class TextureDrawer extends MimodekObjectGraphicsDecorator {

	protected TextureDrawer(MimodekObject decoratedObject) {
		super(decoratedObject);
	}
	
	public TextureDrawer(MimodekObject decoratedObject, int color) {
		super(decoratedObject);
		setDrawingData(new SimpleDrawingData(color));
	}
	

	@Override
	public void draw(PApplet app) {
		app.pushMatrix();
		
		app.translate(getPosX(),getPosY());
		if (!(decoratedObject instanceof ActiveMimo)) {
			app.colorMode(PConstants.HSB,255);
			int c = getDrawingData().getColor();
			c = app.color(app.hue(c), app.saturation(c),Configurator.getIntegerSetting("ancestorBrightness"));
			app.tint(c);			
			TextureCollection.get(Configurator.getIntegerSetting("ancestorTexture")).draw(getDiameter() / Configurator.getFloatSetting("mimosMaxRadius"));
			app.colorMode(PConstants.RGB, 255);
		}else{
			app.tint(getDrawingData().getColor());
			TextureCollection.get(Configurator.getIntegerSetting("activeTexture")).draw(getDiameter() / Configurator.getFloatSetting("mimosMaxRadius"));
		}
		app.noTint();
		app.popMatrix();
		/*if(decoratedObject instanceof ActiveMimo && getRadius()<Configurator.getFloatSetting("mimosMaxRadius"))
	    	setRadius(getRadius()+0.1f);
	    	*/
	}
	
	@Override
	public void render(PApplet app){
		return;
	}

	@Override
	public PImage toImage(PApplet app) {
		if (!(decoratedObject instanceof ActiveMimo)) {	
			return TextureCollection.get(Configurator.getIntegerSetting("ancestorTexture")).image;
			
		}else{
			return TextureCollection.get(Configurator.getIntegerSetting("activeTexture")).image;
		}
	}
	
	@Override
	protected String constructorToXML(String prefix) {
		String XMLString = super.constructorToXML(prefix);
		XMLString 		+= prefix+"<param position=\"2\" type=\""+Integer.class.getName()+"\" value=\""+getDrawingData().getColor()+"\"/>\n";
		return XMLString;
	}

	@Override
	protected void draw(PGraphics gfx) {
		return;
	}

}
