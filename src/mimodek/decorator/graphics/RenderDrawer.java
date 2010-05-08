package mimodek.decorator.graphics;

import processing.core.PApplet;
import mimodek.MimodekObject;

public class RenderDrawer extends ImageDrawer {
	
	MimodekObjectGraphicsDecorator gfxDecorator;
	
	protected RenderDrawer(MimodekObject decoratedObject) {
		super(decoratedObject);
	}
	
	public RenderDrawer(MimodekObject decoratedObject, MimodekObjectGraphicsDecorator gfxDecorator, PApplet app) throws NoImageException{
		super(decoratedObject,gfxDecorator.toImage(app), gfxDecorator.getDrawingData().getColor(),app);
		this.gfxDecorator = gfxDecorator;
		gfxDecorator.decoratedObject = decoratedObject;
	}
	
	@Override
	public String toXMLString(String prefix){
		return gfxDecorator.toXMLString(prefix);
	}

}
