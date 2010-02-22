package mimodek.texture;

import mimodek.MainHandler;
import mimodek.Mimo;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public class SeedGradient implements SimpleDrawer {
	public static final int LINEAR = 1;
	public static final int SIN = 2;
	public static boolean blackToColor = true;
	public static int mode = LINEAR;
	
	public static final float GOLDEN_ANGLE = (float) (PApplet.TWO_PI*0.618034);
	
	public static int maxDots = 50;
	public static float radiScale = 1.13f;
	public static float dotSize = 2.86f;

	public class SeedGradientData {
		int startColor;
		int endColor;
		int numberOfDots;
		int maxRadius;
		PImage img;

		public SeedGradientData(int startColor, int endColor, int maxRadius) {
			this.startColor = startColor;
			this.endColor = endColor;
			this.numberOfDots = 0;
			this.maxRadius = maxRadius;
		}

	}
	
	  

	public SeedGradient() {

	}
	
	

	// it's fine no to provide an empty image
	public PImage draw(Object drawingData) {

		
		if (!(drawingData instanceof SeedGradientData)) {
			// can't draw without the proper data...
			return MainHandler.app.createImage(0, 0, PApplet.RGB);
		}
		SeedGradientData data = (SeedGradientData) drawingData;
		if(data.img == null){
			data.img = MainHandler.app.createGraphics((int) data.maxRadius,
					(int) data.maxRadius, PApplet.JAVA2D);
		}
		
		
		float center = data.maxRadius/2;
		float r=(float) (radiScale*Math.sqrt((float)(data.numberOfDots)));
		if(r<center-dotSize){
			float x,y;
			int c;
		    float angle=GOLDEN_ANGLE*data.numberOfDots;
			x=center+(float) (r*Math.cos(angle));
		      y=center+(float) (r*Math.sin(angle));
			((PGraphics) data.img).beginDraw();
			if(mode == LINEAR) c=MainHandler.app.lerpColor(data.startColor,data.endColor,(float)r/data.maxRadius);
		    else c=MainHandler.app.lerpColor(data.startColor,data.endColor,(float) Math.sin(Math.PI*(float)r/data.maxRadius));
			((PGraphics) data.img).stroke(c);
			((PGraphics) data.img).strokeWeight(dotSize);
			((PGraphics) data.img).point(x,y);
			((PGraphics) data.img).endDraw();
			
		}
		data.numberOfDots++;
		return data.img;
	}

	public Object getDrawingData(Mimo m) {
		if (blackToColor)
			return new SeedGradientData(MainHandler.app.color(0, 0, 0),
					MainHandler.weather.temperatureColor(), (int) m.radius);
		else
			return new SeedGradientData(MainHandler.weather
					.temperatureColor(), MainHandler.app.color(0, 0, 0), (int) m.radius);
	}

}
