package mimodek.decorator.graphics;

import mimodek.MimodekObject;
import processing.core.PImage;

public interface SimpleDrawer {
	public DrawingData getDrawingData(MimodekObject m);
	public PImage draw(DrawingData drawingData);
}
