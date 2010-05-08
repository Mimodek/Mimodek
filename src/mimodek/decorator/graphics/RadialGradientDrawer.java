package mimodek.decorator.graphics;

import mimodek.MimodekObject;
import mimodek.configuration.Configurator;
import mimodek.decorator.ActiveMimo;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;

/**
 * Since it is not used at the moment I haven't tested this and it probably doesn't work as expected.
 * I only converted the previous code to use the decorator pattern
 * @author Jonsku
 */
public class RadialGradientDrawer extends MimodekObjectGraphicsDecorator {

	protected RadialGradientDrawer(MimodekObject decoratedObject) {
		super(decoratedObject);
	}

	public RadialGradientDrawer(MimodekObject decoratedObject, int color) {
		super(decoratedObject);
		setDrawingData(new GradientData(0, color));
	}

	@Override
	public void draw(PApplet app) {
		PGraphics buffer = app.createGraphics(
				(int) Configurator.getFloatSetting("mimosMaxRadius"),
				(int) Configurator.getFloatSetting("mimosMaxRadius"),
				PConstants.JAVA2D);

		buffer.beginDraw();
		draw(buffer);
		buffer.endDraw();
		
		if (!(decoratedObject instanceof ActiveMimo)) {
			app.tint(Configurator.getIntegerSetting("ancestorBrightness"));
			app.image(buffer, getPosX() - buffer.width / 2, getPosY()
					- buffer.height / 2);
			app.noTint();
		} else {
			app.image(buffer, getPosX() - buffer.width / 2, getPosY()
					- buffer.height / 2);
		}
		buffer.dispose();
	}

	@Override
	public PImage toImage(PApplet app) {
		PGraphics buffer = app.createGraphics((int)getDiameter()+4,
				(int) getDiameter()+4,
				PConstants.JAVA2D);
		buffer.beginDraw();
		draw(buffer);
		buffer.endDraw();
		return buffer;
	}

	@Override
	protected void draw(PGraphics gfx) {
		gfx.background(0, 0, 0, 0);
		gfx.noStroke();
		for (int i = (int) getDiameter(); i >= 0; i--) {
			float t = 0;
			switch (Configurator.getIntegerSetting("gradientFunction")) {
			case GradientData.LINEAR:
				t = i / Configurator.getFloatSetting("mimosMaxRadius");
				// break;
			case GradientData.SIN:
				t = (float) Math.sin(Math.PI * i
						/ Configurator.getFloatSetting("mimosMaxRadius"));
				break;
			}
			gfx.fill(gfx.lerpColor(((GradientData) getDrawingData())
					.getStartColor(), getDrawingData().getColor(), t));
			gfx.ellipse(gfx.width / 2, gfx.width / 2, i, i);
		}
		
	}

}
