package mimodek.decorator.graphics;

import mimodek.MimodekObject;
import mimodek.configuration.Configurator;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;

/**
 * Generates shapes using a polar coordinate equation
 * 
 * @author Jonsku
 * 
 */
public class PolarDrawer extends MimodekObjectGraphicsDecorator {
	public int maxPoint = 200;
	public float paramA = 1;
	public float paramB = 1;
	public float angle = 0;
	
	public static int scaleFactor = 8;
	public static float strokeWeight = 0.8f;
	
	
	public PGraphics renderer = null;

	public PolarDrawer(MimodekObject decoratedObject) {
		super(decoratedObject);
	}

	public PolarDrawer(MimodekObject decoratedObject, int color, float paramA,
			float paramB, int iteration) {
		super(decoratedObject);
		setDrawingData(new SimpleDrawingData(color));
		this.paramA = paramA;
		this.paramB = paramB;
		this.maxPoint = iteration;
		this.angle = (float)Math.random()*PApplet.TWO_PI;
	}

	public float getParamA() {
		return paramA;
	}

	public void setParamA(float paramA) {
		this.paramA = paramA;
	}
	
	public void renderOne(PApplet app){
		DrawingData data = getDrawingData();
		if (renderer == null) {
			renderer = app.createGraphics((int) Configurator
					.getFloatSetting("mimosMaxRadius"), (int) Configurator
					.getFloatSetting("mimosMaxRadius"), PConstants.JAVA2D);
			renderer.smooth();
			renderer.beginDraw();
			renderer.endDraw();
		}

		if (data.getIteration() < maxPoint) {
			
			renderer.beginDraw();
			renderer.translate(renderer.width / 2, renderer.height / 2);
			renderer.rotate(angle);
			draw(renderer);
			renderer.endDraw();
			data.incIteration(1);
		}
	}
	
	public void drawRender(PApplet app){
		if (renderer == null) {
			renderOne(app);
		}
		app.pushStyle();
		app.tint(getDrawingData().getColor());
		app.image(renderer, getPosX() - renderer.width / 2, getPosY()
				- renderer.height / 2);
		app.popStyle();
	}

	@Override
	public void draw(PApplet app) {
		DrawingData data = getDrawingData();
		if (renderer == null) {
			renderer = app.createGraphics((int) Configurator
					.getFloatSetting("mimosMaxRadius"), (int) Configurator
					.getFloatSetting("mimosMaxRadius"), PConstants.JAVA2D);
			renderer.smooth();
			renderer.beginDraw();
			renderer.endDraw();
		}

		if (data.getIteration() < maxPoint) {
			
			renderer.beginDraw();
			renderer.translate(renderer.width / 2, renderer.height / 2);
			renderer.rotate(angle);
			draw(renderer);
			renderer.endDraw();
			data.incIteration(1);
		}
		app.pushStyle();
		app.tint(getDrawingData().getColor());
		app.image(renderer, getPosX() - renderer.width / 2, getPosY()
				- renderer.height / 2);
		app.popStyle();
	}

	@Override
	public void render(PApplet app) {
		DrawingData data = getDrawingData();
		int it = data.getIteration();
		data.setIteration(0);
		
		if (renderer == null) {
			renderer = app.createGraphics((int) Configurator
					.getFloatSetting("mimosMaxRadius"), (int) Configurator
					.getFloatSetting("mimosMaxRadius"), PConstants.JAVA2D);
			renderer.smooth();
			renderer.beginDraw();
			renderer.endDraw();
		}
		renderer.beginDraw();
		renderer.translate(renderer.width / 2, renderer.height / 2);
		renderer.rotate(angle);
		while (it >= data.getIteration()) {
			draw(renderer);
			data.incIteration(1);
			
		}
		renderer.endDraw();
	}

	@Override
	public PImage toImage(PApplet app) {
		if(renderer == null)
			return null;
		int w = PApplet.round((2*getDiameter() / (scaleFactor)) * 2);
		int h = PApplet.round((2*getDiameter() / (scaleFactor)) * 2);
		int tLX = PApplet.round(renderer.width / 2 - w / 2);
		int tLY = PApplet.round(renderer.height / 2 - h / 2);
		PImage imgGray = renderer.get(tLX, tLY, w, h);//alphaMask.get(tLX, tLY, w, h);
		PImage img = renderer.get(tLX, tLY, w, h);
		img.mask(imgGray);
		//alphaMask.dispose();
		return img;
	}
	
	@Override
	protected void draw(PGraphics gfx) {
		float a = getDrawingData().getIteration() * 0.05f;
		float r = rose(a, paramA) * getDiameter() / scaleFactor;
		float x = r * PApplet.cos(a);
		float y = r * PApplet.sin(a);

		float rB = rose(a, paramB) * getDiameter() / scaleFactor;
		float xB = rB * PApplet.cos(a);
		float yB = rB * PApplet.sin(a);
		
		gfx.strokeWeight(strokeWeight);
		if (r <= getDiameter()) {
			gfx.stroke(255);
			gfx.point(x, y);
		}
		if (rB <= getDiameter()) {
			gfx.stroke(0, 0, 0, 0);
			gfx.point(xB, yB);
		}
		
	}

	private float rose(float a, float petal) {
		return ((float) (2 * Math.sin(petal * a)));
	}

	@Override
	protected String constructorToXML(String prefix) {
		String XMLString = super.constructorToXML(prefix);
		XMLString += prefix + "<param position=\"2\" type=\""
				+ Integer.class.getName() + "\" value=\""
				+ getDrawingData().getColor() + "\"/>\n";
		XMLString += prefix + "<param position=\"3\" type=\""
				+ Float.class.getName() + "\" value=\"" + paramA + "\"/>\n";
		XMLString += prefix + "<param position=\"4\" type=\""
				+ Float.class.getName() + "\" value=\"" + paramB + "\"/>\n";
		XMLString += prefix + "<param position=\"5\" type=\""
		+ Integer.class.getName() + "\" value=\"" + maxPoint + "\"/>\n";
		return XMLString;
	}



}
