package mimodek.decorator.graphics;

import mimodek.MimodekObject;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public class TextDrawer extends MimodekObjectGraphicsDecorator {
String text = "";
	public TextDrawer(MimodekObject decoratedObject, int color, String text) {
		super(decoratedObject);
		setDrawingData(new SimpleDrawingData(color));
		this.text = text;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void draw(PApplet app) {
		draw(app.g);
	}

	@Override
	public PImage toImage(PApplet app) {
		int h = Math.round(app.textDescent() + app.textAscent());
		int w = Math.round(app.textWidth(text));
		PGraphics renderer = app.createGraphics(w+2,h+2, PApplet.JAVA2D);//leave a little margin for rounding error 
		renderer.beginDraw();
		renderer.textFont(app.g.textFont);
		renderer.translate(-getPosX()+renderer.width/2, -getPosY()+h);
		draw(renderer);
		renderer.endDraw();
		PGraphics gfx = renderer;
		PImage img = gfx.get();
		gfx.filter(PApplet.GRAY);
		gfx.loadPixels();
		img.mask(gfx.pixels);
		gfx.dispose();
		return img;
	}

	@Override
	protected void draw(PGraphics gfx) {
		gfx.pushStyle();
		gfx.fill(getDrawingData().getColor());
		gfx.text(text,getPosX(),getPosY());
		gfx.popStyle();
	}

}
