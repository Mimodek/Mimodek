package mimodek.texture;

import mimodek.Mimodek;
import mimodek.Mimo;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public class SeedGradient implements SimpleDrawer {
	public static final float GOLDEN_ANGLE = (float) (PApplet.TWO_PI*0.618034);
	
	public class SeedGradientData{
		public int startColor;
		public int endColor;
		int numberOfDots;
		private PImage img;
		Mimo m;

		public SeedGradientData(Mimo m, int startColor, int endColor) {
			this.startColor = startColor;
			this.endColor = endColor;
			this.numberOfDots = 0;
			this.m = m;
		}

		public void setImg(PImage img) {
			this.img = img;
		}

		public PImage getImg() {
			return img;
		}

	}
	
	  

	public SeedGradient() {
		Mimodek.config.setSetting("maxDots", 50);
		Mimodek.config.setSetting("radiScale", 1.13f);
		Mimodek.config.setSetting("dotSize", 2.86f);
	}
	
	

	// it's fine no to provide an empty image
	public PImage draw(Object drawingData) {
		
		
		if (!(drawingData instanceof SeedGradientData)) {
			// can't draw without the proper data...
			System.out.println("Don't have proper data for drawing");
			return Mimodek.app.createImage(0, 0, PApplet.RGB);
		}
		SeedGradientData data = (SeedGradientData) drawingData;
		if(data.getImg() == null){
			data.setImg(Mimodek.app.createGraphics((int) Mimodek.config.getFloatSetting("mimosMaxRadius"),
					(int) Mimodek.config.getFloatSetting("mimosMaxRadius"), PApplet.JAVA2D));
		}
		
		if(data.m.ancestor)
			return data.getImg();
		
		float center = Mimodek.config.getFloatSetting("mimosMaxRadius")/2f;
		float r=(float) (Mimodek.config.getFloatSetting("radiScale")*Math.sqrt((float)(data.numberOfDots)));
		
		
		if( r<=center-Mimodek.config.getFloatSetting("dotSize")){//
			float x,y;
			int c;
		    float angle=GOLDEN_ANGLE*data.numberOfDots;
			x=center+(float) (r*Math.cos(angle));
		      y=center+(float) (r*Math.sin(angle));
			((PGraphics) data.getImg()).beginDraw();
			if(Mimodek.config.getIntegerSetting("gradientFunction") == Texturizer.LINEAR){
				c=Mimodek.app.lerpColor(data.startColor,data.endColor,(float)r/Mimodek.config.getFloatSetting("mimosMaxRadius"));
			}else{
				c=Mimodek.app.lerpColor(data.startColor,data.endColor,(float) Math.sin(Math.PI*(float)r/Mimodek.config.getFloatSetting("mimosMaxRadius")));
			}
			((PGraphics) data.getImg()).noStroke();
			((PGraphics) data.getImg()).fill(c);
			//((PGraphics) data.img).strokeWeight(Mimodek.config.getFloatSetting("dotSize"));
			float s = Mimodek.config.getFloatSetting("dotSize");
			((PGraphics) data.getImg()).ellipse(x,y,s,s);
			((PGraphics) data.getImg()).endDraw();
			if(data != null && data.m != null && !data.m.ancestor )
				data.m.radius = r*2;
		}
		data.numberOfDots++;
		return data.getImg();
	}

	public Object getDrawingData(Mimo m) {
		if (Mimodek.config.getBooleanSetting("blackToColor"))
			return new SeedGradientData(m, Mimodek.app.color(0, 0, 0),
					Mimodek.temperatureColor);
		else
			return new SeedGradientData(m, Mimodek.temperatureColor, Mimodek.app.color(0, 0, 0));
	}

}
