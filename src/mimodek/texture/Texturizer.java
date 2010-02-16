package mimodek.texture;

import java.io.File;

import mimodek.Mimo;
import mimodek.Simulation1;

public class Texturizer {
	public boolean image = false;
	
	SquareTexture texture;

	public Texturizer() {
		texture = new SquareTexture("images/seed.png", Mimo.maxRadius);
		loadTextures("images/");
	}
	
	private void loadTextures(String textureFolder){
		File folder = new File(textureFolder);
	    File[] listOfFiles = folder.listFiles();
	    System.out.println("Hello");
	    for (int i = 0; i < listOfFiles.length; i++) {
	      if (listOfFiles[i].isFile()) {
	        System.out.println("File " + listOfFiles[i].getName());
	      } else if (listOfFiles[i].isDirectory()) {
	        System.out.println("Directory " + listOfFiles[i].getName());
	      }
	    }
	}

	private void applyStyle(Mimo m) {
		if(!image){
			if (m.ancestor) {
				Simulation1.gfx.noFill();
				Simulation1.gfx.strokeWeight((float) 0.5);
				Simulation1.gfx.stroke(125);
			} else {
				Simulation1.gfx.noStroke();
				Simulation1.gfx.fill(255, 255, 255, 100);
			}
		}else{
			;
		}
	}

	public void draw(Mimo m) {
		Simulation1.gfx.pushMatrix();
		Simulation1.gfx.pushStyle();
		Simulation1.gfx.translate(m.pos.x, m.pos.y);
		applyStyle(m);
		if(image){
			texture.draw(m.radius/Mimo.maxRadius);
		}else{
			Simulation1.gfx.ellipse(0, 0, m.radius, m.radius);
		}
		Simulation1.gfx.popStyle();
		Simulation1.gfx.popMatrix();
	}
}
