package mimodek.texture;

import java.io.File;

import mimodek.MainHandler;
import processing.core.PImage;

/*
 * A texture which width is equal to its length (at least it is assumed to be!)
 */
public class SquareTexture {
	
	public String fileName;
	public float scaleFactor;
	public PImage image;
	public float halfWidth;
	
	/*
	 * fileName = image file
	 * maxWidth = maximum of the texture 
	 */
	public SquareTexture(String fileName,float maxWidth){
		image = MainHandler.app.loadImage(fileName);
		this.fileName = new File(fileName).getName();
		scaleFactor = maxWidth/image.width;
		halfWidth = image.width/2;
	}
	
	public void rescale(float maxWidth){
		scaleFactor = maxWidth/image.width;
		halfWidth = image.width/2;
	}
	
	/*
	 * Draw the texture
	 * The size parameter controls how big to draw it relative to the maxWidth (see constructor)
	 */
	public void draw(float size){
		MainHandler.gfx.pushMatrix();
		MainHandler.gfx.scale(scaleFactor*size);
		MainHandler.gfx.image(image,-halfWidth,-halfWidth);
		MainHandler.gfx.popMatrix();
	}
}
