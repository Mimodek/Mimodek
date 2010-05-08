package mimodek.decorator.graphics;

import java.io.File;

import mimodek.configuration.Configurator;

import processing.core.PApplet;
import processing.core.PImage;

/*
 * A texture which width is equal to its length (at least it is assumed to be!)
 */
public class SquareTexture {
	
	public String fileName;
	public float scaleFactor;
	public PImage image;
	public float halfWidth;
	protected PApplet app;
	
	/*
	 * fileName = image file
	 * maxWidth = maximum of the texture 
	 */
	public SquareTexture(String fileName,float maxWidth, PApplet app){
		this.app = app;
		image = app.loadImage(fileName);
		this.fileName = new File(fileName).getName();
		scaleFactor = maxWidth/image.width;
		halfWidth = image.width/2;
	}
	
	public SquareTexture(PImage image, PApplet app){
		this.app = app;
		this.image = image;
		this.fileName = "Generated";
		scaleFactor = 1;
		halfWidth = image.width/2;
	}
	
	/*
	 * Draw the texture
	 * The size parameter controls how big to draw it relative to the maxWidth (see constructor)
	 */
	public void draw(float size){
		app.pushMatrix();
		scaleFactor = Configurator.getFloatSetting("mimosMaxRadius")/image.width;
		halfWidth = image.width/2;
		app.scale(scaleFactor*size);
		app.image(image,-halfWidth,-halfWidth);
		app.popMatrix();
	}
}
