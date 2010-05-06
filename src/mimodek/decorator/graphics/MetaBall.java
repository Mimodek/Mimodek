package mimodek.decorator.graphics;

import mimodek.MimodekObject;
import mimodek.facade.FacadeFactory;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

public class MetaBall extends MimodekObjectGraphicsDecorator {
	public static float MIN_THRESHOLD = 0.1f;
	public static float MAX_THRESHOLD = 1.0f; // Minimum and maximum threshold
												// for an isosurface
	public float strength;
	public float threshold;
	public float diameter;
	public float halfRadius;
	public boolean used = false;

	public MetaBall(MimodekObject decoratedObject, int color) {
		super(decoratedObject);
		setDrawingData(new SimpleDrawingData(color));
		strength = 1f;
		reset();
	}
	
	public void update(){
		reset();
	}

	@Override
	public void draw(PApplet app) {
		QTree.getInstance().addBlob(this);
	}

	public PVector getTopLeft() {
		return new PVector(getPosX() - getDiameter()/2 >= 0 ? getPosX() - getDiameter()/2
				: 0, getPosY() - getDiameter()/2 >= 0 ? getPosY() - getDiameter()/2 : 0);
	}

	public PVector getBottomRight() {
		// return new PVector(pos.x+radius <= width ? pos.x+radius : width,
		// pos.y+radius <= height ? pos.y+radius : height);
		/*return new PVector(getPosX() + diameter <= FacadeFactory
				.getFacade().width ? getPosX() + diameter : FacadeFactory
				.getFacade().width, getPosY() + diameter <= FacadeFactory
				.getFacade().height ? getPosY() + diameter : FacadeFactory
				.getFacade().height);*/
		
		return new PVector(getPosX() + getDiameter()/2 <= FacadeFactory
				.getFacade().width ? getPosX() + getDiameter()/2 : FacadeFactory
				.getFacade().width, getPosY() + getDiameter()/2 <= FacadeFactory
				.getFacade().height ? getPosY() + getDiameter()/2 : FacadeFactory
				.getFacade().height);
	}

	public float equation(float x, float y) {

		float d = MetaBallRenderer.distlookup[Math.round(Math
				.abs(x - getPosX()))][Math.round(Math.abs(y - getPosY()))];
		if (d > (getDiameter()/2)) {
			return 0;
		}
		return (1 - (d / (getDiameter()/2))) * strength;
		// return map(d,0,actualRadius,strength,0);

	}

	public void reset() {
		halfRadius = ((1 - (MIN_THRESHOLD / strength)) * getDiameter()/2);
		diameter = halfRadius * 2;
		used = false;
	}
	
	@Override
	public PImage toImage(PApplet app) {
		PGraphics renderer = app.createGraphics((int) getDiameter(),(int) getDiameter(), PApplet.JAVA2D);
		renderer.beginDraw();
		renderer.background(0,0,0,0);
		renderer.loadPixels();
		
		int startX = (int)(getPosX()-getDiameter()/2);
		int startY = (int)(getPosY()-getDiameter()/2);
		for(int i=0;i<renderer.pixels.length;i++){
			renderer.pixels[i] = renderer.lerpColor(renderer.color(0,0,0,0),renderer.color(255),equation(startX+(i%renderer.width),startY+(i/renderer.width)));
		}
		renderer.updatePixels();
		renderer.endDraw();
		PImage img = renderer.get();
		renderer.filter(PApplet.GRAY);
		renderer.loadPixels();
		img.mask(renderer.pixels);
		renderer.dispose();
		return img;
	}

}
