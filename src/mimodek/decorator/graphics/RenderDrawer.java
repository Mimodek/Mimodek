package mimodek.decorator.graphics;

import processing.core.PApplet;
import mimodek.MimodekObject;
import mimodek.configuration.Colors;
import mimodek.configuration.Configurator;

public class RenderDrawer extends ImageDrawer {
	
	MimodekObjectGraphicsDecorator gfxDecorator;
	
	protected RenderDrawer(MimodekObject decoratedObject) {
		super(decoratedObject);
	}
	
	public RenderDrawer(MimodekObject decoratedObject, MimodekObjectGraphicsDecorator gfxDecorator, PApplet app) throws NoImageException{
		super(decoratedObject,gfxDecorator.toImage(app), Colors.getColor(Configurator.getFloatSetting("mimosColor")),app);
		this.gfxDecorator = gfxDecorator;
		gfxDecorator.decoratedObject = decoratedObject;
	}
	
	public String toXMLString(String prefix){
		return gfxDecorator.toXMLString(prefix);
	}

}
