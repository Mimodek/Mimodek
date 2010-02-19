package mimodek.texture;

import mimodek.MainHandler;
import mimodek.Mimo;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public class RadialGradient implements SimpleDrawer {
	public static final int LINEAR = 1;
	public static final int SIN = 2;
	public static boolean blackToColor = true;
	public static int mode = LINEAR;

	public class RadialGradientData {
		int startColor;
		int endColor;
		float radius;
		float maxRadius;

		public RadialGradientData(int startColor, int endColor, int radius,
				int maxRadius) {
			this.startColor = startColor;
			this.endColor = endColor;
			this.radius = radius;
			this.maxRadius = maxRadius;
		}

	}

	public RadialGradient() {

	}

	// it's fine no to provide an empty image
	public PImage draw(Object drawingData) {
		if (!(drawingData instanceof RadialGradientData)) {
			// can't draw without the proper data...
			return MainHandler.app.createImage(0, 0, PApplet.RGB);
		}
		RadialGradientData data = (RadialGradientData) drawingData;
		PGraphics buffer = MainHandler.app.createGraphics((int) data.maxRadius,
				(int) data.maxRadius, PApplet.JAVA2D);

		buffer.beginDraw();
		buffer.background(0, 0, 0, 0);
		buffer.noStroke();
		for (int i = (int) data.radius; i >= 0; i--) {
			float t = 0;
			switch (mode) {
			case LINEAR:
				t = (float) i / data.maxRadius;
				break;
			case SIN:
				t = (float) Math.sin(Math.PI * i / data.maxRadius);
				break;
			}
			buffer.fill(MainHandler.app.lerpColor(data.startColor,
					data.endColor, t));
			buffer.ellipse(buffer.width / 2, buffer.width / 2, i, i);
		}
		buffer.endDraw();
		if (data.radius < data.maxRadius)
			data.radius++;
		return buffer;
	}

	public Object getDrawingData(Mimo m) {
		if (blackToColor)
			return new RadialGradientData(MainHandler.app.color(0, 0, 0),
					MainHandler.weather.temperatureColor(), 0, (int) m.radius);
		else
			return new RadialGradientData(MainHandler.weather
					.temperatureColor(), MainHandler.app.color(0, 0, 0), 0,
					(int) m.radius);
	}

}
