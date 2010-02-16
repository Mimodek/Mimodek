package mimodek.texture;

import java.io.File;
import java.util.ArrayList;

import mimodek.Mimo;
import mimodek.Simulation1;

public class Texturizer{
	public boolean image = true;
	
	public int ancestor = 0;
	public int active = 1;
	
	public ArrayList<SquareTexture> textures;
	

	public Texturizer() {
		textures = new ArrayList<SquareTexture>();
		loadTextures("./images/");
	}
	
	public void loadTextures(String textureFolder){
		File folder = new File(textureFolder);
	    File[] listOfFiles = folder.listFiles();
	    
	    for (int i = 0; i < listOfFiles.length; i++) {
	      if (listOfFiles[i].isFile()) {
	        System.out.println("File " + listOfFiles[i].getName());
	        textures.add(new SquareTexture(textureFolder+listOfFiles[i].getName(), Mimo.maxRadius));
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
	
	
	public void drawTexture(int textureIndex, int x, int y, int size){
		if(textureIndex>=textures.size())
			return;
		Simulation1.gfx.pushMatrix();
		Simulation1.gfx.translate(x, y);
		textures.get(textureIndex).draw(1);
		Simulation1.gfx.popMatrix();
	}

	public void draw(Mimo m) {
		Simulation1.gfx.pushMatrix();
		Simulation1.gfx.pushStyle();
		Simulation1.gfx.translate(m.pos.x, m.pos.y);
		applyStyle(m);
		if(image){
			if(m.ancestor){
				textures.get(ancestor).draw(m.radius/Mimo.maxRadius);
			}else{
				textures.get(active).draw(m.radius/Mimo.maxRadius);
			}
		}else{
			Simulation1.gfx.ellipse(0, 0, m.radius, m.radius);
		}
		Simulation1.gfx.popStyle();
		Simulation1.gfx.popMatrix();
	}
	
}
