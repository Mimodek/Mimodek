package mimodek.texture;

import mimodek.Simulation1;
import processing.core.PImage;

/*
 * A texture which width is equal to its length (at least it is assumed to be!)
 */
public class SquareTexture {
	
	public float scaleFactor;
	public PImage image;
	public float halfWidth;
	
	/*
	 * fileName = image file
	 * maxWidth = maximum of the texture 
	 */
	public SquareTexture(String fileName,float maxWidth){
		image = Simulation1.app.loadImage(fileName);
		scaleFactor = maxWidth/image.width;
		halfWidth = image.width/2;
	}
	
	/*
	 * Draw the texture
	 * The size parameter controls how big to draw it relative to the maxWidth (see constructor)
	 */
	public void draw(float size){
		Simulation1.gfx.pushMatrix();
		Simulation1.gfx.scale(scaleFactor*size);
		Simulation1.gfx.image(image,-halfWidth,-halfWidth);
		Simulation1.gfx.popMatrix();
	}
}
