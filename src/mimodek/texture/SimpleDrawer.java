package mimodek.texture;

import mimodek.Mimo;
import processing.core.PImage;

public interface SimpleDrawer {
	public Object getDrawingData(Mimo m);
	public PImage draw(Object drawingData);
}
