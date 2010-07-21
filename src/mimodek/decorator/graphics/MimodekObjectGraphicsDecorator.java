package mimodek.decorator.graphics;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import mimodek.decorator.MimodekObjectDecorator;
import mimodek.decorator.graphics.DrawingData;
import mimodek.utils.Verbose;
import mimodek.MimodekObject;

/**
 * Decorates a MimodekObject to give it a graphical representation.
 * @author Jonsku
 */
public abstract class MimodekObjectGraphicsDecorator extends MimodekObjectDecorator {
	private DrawingData drawingData;
	
	public MimodekObjectGraphicsDecorator(MimodekObject decoratedObject) {
		super(decoratedObject);
	}

	public void update() {
		decoratedObject.update();
	}
  
	public void render(PApplet app){
		draw(app);
	}
	
	public void setDrawingData(DrawingData dData){
		drawingData = dData;
	}
	
	public DrawingData getDrawingData(){
		return drawingData;
	}
	
	public void renderOne(PApplet app){
		render(app);
	}
	
	public void drawRender(PApplet app){
		draw(app);
	}
	
	public abstract void draw(PApplet app);
	
	protected abstract void draw(PGraphics gfx);
	
	public abstract PImage toImage(PApplet app);
	
	@Override
	public String toXMLString(String prefix){
		Verbose.debug(prefix+this);
		String XMLString = prefix+"<GraphicDecorator className=\""+this.getClass().getName()+"\">\n";
		XMLString 		+= decoratedObject.toXMLString(prefix+"\t");
		if(drawingData!=null)
			XMLString 		+= drawingData.toXMLString(prefix+"\t");
		XMLString		+= prefix+"\t<params>\n";
		XMLString 		+= constructorToXML(prefix+"\t\t");
		XMLString		+= prefix+"\t</params>\n";
		XMLString 		+= prefix+"</GraphicDecorator>\n";
		return XMLString;
	}
	
	protected String constructorToXML(String prefix){
		return prefix+"<param position=\"1\" type=\""+MimodekObject.class.getName()+"\"/>\n";
	}
	

}
