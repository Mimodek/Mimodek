package mimodek.texture;

import mimodek.Mimodek;
import mimodek.Mimo;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public class RadialGradient implements SimpleDrawer {

	public class RadialGradientData{
		int startColor;
		int endColor;
		Mimo m;

		public RadialGradientData(Mimo m, int startColor, int endColor) {
			this.startColor = startColor;
			this.endColor = endColor;
			this.m = m;
		}

	}

	public RadialGradient() {

	}

	// it's fine no to provide an empty image
	public PImage draw(Object drawingData) {
		if (!(drawingData instanceof RadialGradientData)) {
			// can't draw without the proper data...
			return Mimodek.app.createImage(0, 0, PApplet.RGB);
		}
		RadialGradientData data = (RadialGradientData) drawingData;
		PGraphics buffer = Mimodek.app.createGraphics((int) Mimodek.config.getFloatSetting("mimosMaxRadius"),
				(int) Mimodek.config.getFloatSetting("mimosMaxRadius"), PApplet.JAVA2D);

		buffer.beginDraw();
		buffer.background(0, 0, 0, 0);
		buffer.noStroke();
		for (int i = (int) data.m.radius; i >= 0; i--) {
			float t = 0;
			switch (Mimodek.config.getIntegerSetting("gradientFunction")) {
			case Texturizer.LINEAR:
				t = (float) i / Mimodek.config.getFloatSetting("mimosMaxRadius");
				break;
			case Texturizer.SIN:
				t = (float) Math.sin(Math.PI * i / Mimodek.config.getFloatSetting("mimosMaxRadius"));
				break;
			}
			buffer.fill(Mimodek.app.lerpColor(data.startColor,
					data.endColor, t));
			buffer.ellipse(buffer.width / 2, buffer.width / 2, i, i);
		}
		buffer.endDraw();
		return buffer;
	}

	public Object getDrawingData(Mimo m) {
		if (Mimodek.config.getBooleanSetting("blackToColor"))
			return new RadialGradientData(m, Mimodek.app.color(0, 0, 0),
					Mimodek.temperatureColor);
		else
			return new RadialGradientData(m, Mimodek.temperatureColor, Mimodek.app.color(0, 0, 0));
	}

}
