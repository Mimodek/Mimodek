package mimodek.facade;

import processing.core.PApplet;
import processing.core.PVector;

/**
 * Minimal implementation of a facade. It does nothing really interesting, it just set the dimensions to the dimensions of the parent sketch...
 * @author Jonsku
 */
public class ProcessingFacade extends Facade {

	public ProcessingFacade(PApplet app) {
		super(app);
		leftOffset = 0;
		topOffset = 0;
		width = app.width;
		halfWidth = width/2;
		height = app.height;
		halfHeight = height/2;
		// calculate ratios
		wRatio = 1;
		hRatio = 1;
	}

	@Override
	public void draw() {
		//does nothing...
	}

	@Override
	public boolean isInTheScreen(PVector coordinate, float margin) {
		return coordinate.x>=margin && coordinate.x<width-margin && coordinate.y>=margin && coordinate.y<height-margin;
	}

	@Override
	public boolean isInTheScreen(PVector coordinate) {
		return coordinate.x>=0 && coordinate.x<=width && coordinate.y>=0 && coordinate.y<height;
	}

	@Override
	public boolean isInTheScreen(float x, float y, float margin) {
		return x>=margin && x<width-margin && y>=margin && y<height-margin;
	}

	@Override
	public boolean isInTheScreen(float x, float y) {
		return x>=0 && x<=width && y>=0 && y<height;
	}

}
