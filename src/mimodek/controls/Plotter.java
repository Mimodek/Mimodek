package mimodek.controls;

import mimodek.Mimodek;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class Plotter {
	PVector lastPoint = new PVector(0,0);
	int lastFrame = 0;
	
	int width;
	int height;
	float min;
	float max;
	float space;
	public PGraphics buffer;
	
	public Plotter(int width, int height, float min, float max, float space) {
		super();
		this.width = width;
		this.height = height;
		this.min = min;
		this.max = max;
		this.space = space;
		buffer = Mimodek.app.createGraphics(width,height,PApplet.JAVA2D);
		buffer.beginDraw();
		buffer.fill(Mimodek.app.color(0,0,0,0));
		buffer.rect(-1,-1,width+1,height+1);
		buffer.stroke(255);
		buffer.line(0,height/2,width,height/2);
		buffer.endDraw();
	}
	
	public void plot(float value){
		float y = PApplet.map(value,min,max,height,0);
		float x = 0;
		if(lastFrame != 0){
			buffer.beginDraw();
			buffer.stroke(255);
			if(lastPoint.x >= width){
				lastPoint.x = 0;
				buffer.fill(Mimodek.app.color(0,0,0,0));
				buffer.rect(-1,-1,width+1,height+1);
				buffer.stroke(255);
				buffer.line(0,height/2,width,height/2);
			}
			x = lastPoint.x + (Mimodek.app.frameCount - lastFrame)*space;
			
			buffer.line(lastPoint.x,lastPoint.y,x,y);
			buffer.endDraw();
		}
		lastPoint = new PVector(x,y);
		lastFrame = Mimodek.app.frameCount;
	}
}
