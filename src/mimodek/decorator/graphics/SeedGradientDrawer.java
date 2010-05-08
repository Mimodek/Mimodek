package mimodek.decorator.graphics;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import mimodek.MimodekObject;
import mimodek.configuration.Configurator;
import mimodek.decorator.ActiveMimo;

public class SeedGradientDrawer extends MimodekObjectGraphicsDecorator {
	public static final float GOLDEN_ANGLE = (float) (PConstants.TWO_PI * 0.618034);
	private int numberOfDots = 0;
	public PGraphics renderer = null;
	private PGraphics alphaMask = null;
	private boolean rendered = false;

	protected SeedGradientDrawer(MimodekObject decoratedObject) {
		super(decoratedObject);
	}

	public SeedGradientDrawer(MimodekObject decoratedObject, int color) {
		super(decoratedObject);
		if (getDrawingData() == null
				|| !(getDrawingData() instanceof GradientData))
			setDrawingData(new GradientData(color, 0));
		getDrawingData().setIteration(0);
	}

	@Override
	public void draw(PApplet app) {
		//GradientData data = (GradientData) getDrawingData();
		if (renderer == null) {
			renderer = app.createGraphics((int) Configurator
					.getFloatSetting("mimosMaxRadius"), (int) Configurator
					.getFloatSetting("mimosMaxRadius"), PConstants.JAVA2D);
			renderer.beginDraw();
			renderer.endDraw();
			alphaMask = app.createGraphics((int) Configurator
					.getFloatSetting("mimosMaxRadius"), (int) Configurator
					.getFloatSetting("mimosMaxRadius"), PConstants.JAVA2D);
			alphaMask.beginDraw();
			alphaMask.background(0);
			alphaMask.endDraw();
		}

		if (decoratedObject instanceof ActiveMimo) {
			float center = Configurator.getFloatSetting("mimosMaxRadius") / 2f;
			float r = (float) (Configurator.getFloatSetting("radiScale") * Math
					.sqrt(numberOfDots));
			
			if (r <= center - Configurator.getFloatSetting("dotSize")) {//
				renderer.beginDraw();
				draw(renderer);
				renderer.endDraw();
				setDiameter(r * 2);
				numberOfDots++;
				getDrawingData().incIteration(1);
			/*
				float x, y;
				int c;
				float angle = GOLDEN_ANGLE * numberOfDots;
				x = center + (float) (r * Math.cos(angle));
				y = center + (float) (r * Math.sin(angle));

				((PGraphics) renderer).beginDraw();
				alphaMask.beginDraw();
				if (Configurator.getIntegerSetting("gradientFunction") != GradientData.LINEAR) {
					c = app.lerpColor(data.getStartColor(), data.getColor(),
							(float) r
									/ Configurator
											.getFloatSetting("mimosMaxRadius"));
				} else {
					c = app
							.lerpColor(
									data.getStartColor(),
									data.getColor(),
									(float) Math
											.sin(Math.PI
													* (float) r
													/ Configurator
															.getFloatSetting("mimosMaxRadius")));
				}
				((PGraphics) renderer).noStroke();
				((PGraphics) renderer).fill(c);
				// ((PGraphics)
				// data.img).strokeWeight(Configurator.getFloatSetting("dotSize"));
				float s = Configurator.getFloatSetting("dotSize");
				((PGraphics) renderer).ellipse(x, y, s, s);
				((PGraphics) renderer).endDraw();
				
				alphaMask.noStroke();
				alphaMask.fill(app.color(0,0,255));
				alphaMask.ellipse(x, y, s, s);
				alphaMask.endDraw();

				setDiameter(r * 2);
				numberOfDots++;
				getDrawingData().incIteration(1);*/
			}
			app.image(renderer, getPosX() - renderer.width / 2, getPosY()
					- renderer.height / 2);
		} else {
			if (!rendered  && renderer instanceof PGraphics) {
				PImage imgGray =  alphaMask.get(Math.round(renderer.width/2-(getDiameter()/2)),Math.round(renderer.height/2-(getDiameter()/2)),Math.round(getDiameter()+5),Math.round(getDiameter()+5));
				//imgGray.filter(PApplet.POSTERIZE,4);
				PImage img = renderer.get(Math.round(renderer.width/2-(getDiameter()/2)),Math.round(renderer.height/2-(getDiameter()/2)),Math.round(getDiameter()+5),Math.round(getDiameter()+5));
				img.mask(imgGray);
				alphaMask.dispose();
				renderer = (PGraphics)img;
				rendered = true;
			}
			app.pushMatrix();
			app.translate(getPosX(), getPosY());
			app.scale(1 / Configurator.getFloatSetting("ancestorScale"));
			app.tint(Configurator.getIntegerSetting("ancestorBrightness"));
			app.image(renderer, -renderer.width / 2, -renderer.height / 2);
			app.noTint();
			app.popMatrix();
		}
	}

	@Override
	public void render(PApplet app) {
		int it = 0;

		//GradientData data = (GradientData) getDrawingData();
		if (renderer == null) {
			renderer = app.createGraphics((int) Configurator
					.getFloatSetting("mimosMaxRadius"), (int) Configurator
					.getFloatSetting("mimosMaxRadius"), PConstants.JAVA2D);
			(renderer).beginDraw();
			(renderer).endDraw();
			alphaMask = app.createGraphics((int) Configurator
					.getFloatSetting("mimosMaxRadius"), (int) Configurator
					.getFloatSetting("mimosMaxRadius"), PConstants.JAVA2D);
			alphaMask.beginDraw();
			alphaMask.background(0);
			alphaMask.endDraw();
		}

		float center = Configurator.getFloatSetting("mimosMaxRadius") / 2f;
		(renderer).beginDraw();
		alphaMask.beginDraw();
		while (it < getDrawingData().getIteration()) {
			float r = (float) (Configurator.getFloatSetting("radiScale") * Math
					.sqrt(numberOfDots));

			if (r <= center - Configurator.getFloatSetting("dotSize")) {//
				draw(renderer);
				setDiameter(r * 2);
				numberOfDots++;
				it++;
			}
			alphaMask.endDraw();
			(renderer).endDraw();
		}
		if (!rendered  && renderer instanceof PGraphics) {
			int w = PApplet.round(getDiameter()+5);
			int h = PApplet.round(getDiameter()+5);
			int tLX = PApplet.round(renderer.width/2-w/2);
			int tLY = PApplet.round(renderer.height/2-h/2);
			
			PImage imgGray =  alphaMask.get(tLX,tLY,w,h);
			PImage img = renderer.get(tLX,tLY,w,h);
			img.mask(imgGray);
			alphaMask.dispose();
			renderer = (PGraphics) img;
			rendered = true;
		}
	}

	@Override
	public PImage toImage(PApplet app) {
		if(!rendered){
			int w = PApplet.round(getDiameter()+5);
			int h = PApplet.round(getDiameter()+5);
			int tLX = PApplet.round(renderer.width/2-w/2);
			int tLY = PApplet.round(renderer.height/2-h/2);
			
			PImage imgGray =  alphaMask.get(tLX,tLY,w,h);
			PImage img = renderer.get(tLX,tLY,w,h);
			img.mask(imgGray);
			alphaMask.dispose();
			renderer = (PGraphics) img;
			rendered = true;
		}
		return renderer;
	}

	@Override
	protected String constructorToXML(String prefix) {
		String XMLString = super.constructorToXML(prefix);
		XMLString 		+= prefix+"<param position=\"2\" type=\""+Integer.class.getName()+"\" value=\""+((GradientData) getDrawingData()).startColor+"\"/>\n";
		return XMLString; 
	}

	@Override
	protected void draw(PGraphics gfx) {
		gfx.pushStyle();
		GradientData data = (GradientData) getDrawingData();
		float center = Configurator.getFloatSetting("mimosMaxRadius") / 2f;
		float r = (float) (Configurator.getFloatSetting("radiScale") * Math
				.sqrt(numberOfDots));
			float x, y;
			int c;
			float angle = GOLDEN_ANGLE * numberOfDots;
			x = center + (float) (r * Math.cos(angle));
			y = center + (float) (r * Math.sin(angle));

			//((PGraphics) renderer).beginDraw();
			//alphaMask.beginDraw();
			if (Configurator.getIntegerSetting("gradientFunction") != GradientData.LINEAR) {
				c = gfx.lerpColor(data.getStartColor(), data.getColor(),
						r
								/ Configurator
										.getFloatSetting("mimosMaxRadius"));
			} else {
				c = gfx
						.lerpColor(
								data.getStartColor(),
								data.getColor(),
								(float) Math
										.sin(Math.PI
												* r
												/ Configurator
														.getFloatSetting("mimosMaxRadius")));
			}
			gfx.noStroke();
			gfx.fill(c);
			// ((PGraphics)
			// data.img).strokeWeight(Configurator.getFloatSetting("dotSize"));
			float s = Configurator.getFloatSetting("dotSize");
			gfx.ellipse(x, y, s, s);
			
			
			alphaMask.noStroke();
			alphaMask.fill(gfx.color(0,0,255));
			alphaMask.ellipse(x, y, s, s);
			alphaMask.endDraw();
			gfx.popStyle();
			
		
	}

}
