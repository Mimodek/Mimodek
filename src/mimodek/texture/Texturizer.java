package mimodek.texture;

import java.io.File;
import java.util.ArrayList;

import mimodek.Mimo;
import mimodek.MainHandler;

public class Texturizer{
	public static int CIRCLE = 1;
	public static int IMAGE = 2;
	public static int GENERATED = 3;
	
	public int mode = IMAGE;
	
	public int ancestor = 0;
	public int active = 1;
	
	public ArrayList<SquareTexture> textures;
	

	public Texturizer() {
		textures = new ArrayList<SquareTexture>();
		loadTextures(MainHandler.app.dataPath("images/"));
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
		if(mode == CIRCLE){
			if (m.ancestor) {
				MainHandler.gfx.noFill();
				MainHandler.gfx.strokeWeight((float) 0.5);
				MainHandler.gfx.stroke(125);
			} else {
				MainHandler.gfx.noStroke();
				MainHandler.gfx.fill(255, 255, 255, 100);
			}
		}else{
			;
		}
	}
	
	
	public void drawTexture(int textureIndex, int x, int y, int size){
		if(textureIndex>=textures.size())
			return;
		MainHandler.gfx.pushMatrix();
		MainHandler.gfx.translate(x, y);

		textures.get(textureIndex).draw(1);
		MainHandler.gfx.popMatrix();
	}

	public void draw(Mimo m) {
		MainHandler.gfx.pushMatrix();
		MainHandler.gfx.pushStyle();
		MainHandler.gfx.translate(m.pos.x, m.pos.y);
		applyStyle(m);
		if(mode == IMAGE){
			//TODO: ask Lali if this is right
			MainHandler.gfx.rotate((float) Math.atan(m.vel.y/m.vel.x));
			if(m.ancestor){
				textures.get(ancestor).draw(m.radius/Mimo.maxRadius);
			}else{
				textures.get(active).draw(m.radius/Mimo.maxRadius);
			}
		}else{
			MainHandler.gfx.ellipse(0, 0, m.radius, m.radius);
		}
		MainHandler.gfx.popStyle();
		MainHandler.gfx.popMatrix();
	}
	
}
