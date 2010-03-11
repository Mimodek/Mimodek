package mimodek.texture;

import java.io.File;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

import mimodek.Mimodek;
import mimodek.Mimo;
import mimodek.texture.SeedGradient.SeedGradientData;

public class Texturizer {
	//Constants
	//TODO: Use enums
	public static final int CIRCLE = 1;
	public static final int IMAGE = 2;
	public static final int GENERATED = 3;
	
	public static final int LINEAR = 1;
	public static final int SIN = 2;

	public ArrayList<SquareTexture> textures;
	
	public SimpleDrawer circleDrawer;
	public SimpleDrawer seedDrawer;
	
	

	public Texturizer() {
		// load the textures
		textures = new ArrayList<SquareTexture>();
		loadTextures(Mimodek.app.dataPath("images/"));

		// initialize the drawers
		circleDrawer = new SeedGradient(); //
		seedDrawer = new SeedGradient();
		
		// Register some settings
		Mimodek.config.setSetting("textureMode", CIRCLE);
		Mimodek.config.setSetting("drawCircle", true);
		Mimodek.config.setSetting("ancestorBrightness", 125);
		Mimodek.config.setSetting("ancestorTexture", 0);
		Mimodek.config.setSetting("activeTexture", 1);
		Mimodek.config.setSetting("blackToColor", true);
		Mimodek.config.setSetting("gradientFunction",(int)SIN);
	}

	public void loadTextures(String textureFolder) {
		File folder = new File(textureFolder);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				System.out.println("File " + listOfFiles[i].getName());
				textures.add(new SquareTexture(textureFolder
						+ listOfFiles[i].getName(), Mimodek.config.getFloatSetting("mimosMaxRadius")));
			} else if (listOfFiles[i].isDirectory()) {
				System.out.println("Directory " + listOfFiles[i].getName());
			}
		}
	}

	private void applyStyle(Mimo m) {
		if (Mimodek.config.getIntegerSetting("textureMode") == CIRCLE) {
			if (m.ancestor) {
				Mimodek.gfx.noFill();
				Mimodek.gfx.strokeWeight((float) 0.5);
				Mimodek.gfx.stroke(125);
			} else {
				Mimodek.gfx.noStroke();
				Mimodek.gfx.fill(255, 255, 255, 100);
			}
		} else {
			;
		}
	}

	public void changeScale(float maxSize) {
		for (int i = -1; ++i < textures.size();)
			textures.get(i).rescale(maxSize);
	}

	public void drawTexture(int textureIndex, int x, int y, int size) {
		if (textureIndex >= textures.size())
			return;
		Mimodek.gfx.pushMatrix();
		Mimodek.gfx.translate(x, y);

		textures.get(textureIndex).draw(1);
		Mimodek.gfx.popMatrix();
	}

	public void draw(Mimo m) {
		Mimodek.gfx.pushMatrix();
		Mimodek.gfx.pushStyle();
		Mimodek.gfx.translate(m.pos.x, m.pos.y);
		if(m.ancestor){
			Mimodek.app.scale(1/Mimodek.config.getFloatSetting("ancestorScale"));
		}
		applyStyle(m);
		switch (Mimodek.config.getIntegerSetting("textureMode")) {
		case IMAGE:
			Mimodek.gfx.rotate((float) Math.atan2(m.vel.y, m.vel.x));
			if (m.ancestor) {
				Mimodek.gfx.tint(Mimodek.config.getIntegerSetting("ancestorBrightness"));
				textures.get(Mimodek.config.getIntegerSetting("ancestorTexture")).draw(m.radius / Mimodek.config.getFloatSetting("mimosMaxRadius"));
				Mimodek.gfx.noTint();
			} else {
				textures.get(Mimodek.config.getIntegerSetting("activeTexture")).draw(m.radius / Mimodek.config.getFloatSetting("mimosMaxRadius"));
			}
			break;
		case CIRCLE:
			Mimodek.app.ellipse(0, 0, m.radius, m.radius);
			break;
		case GENERATED:
			Mimodek.gfx.rotate((float) Math.atan2(m.vel.y, m.vel.x));
			SimpleDrawer drawer = Mimodek.config.getBooleanSetting("drawCircle")?circleDrawer:seedDrawer;
			
			if (m.drawingData == null) {
				m.drawingData = drawer.getDrawingData(m);
			}
			PImage image = drawer.draw(m.drawingData);
			
			try{
			Mimodek.gfx.image(image,-image.width/2,-image.height/2);
				if(m.ancestor){
					//darken by drawing a dark filter ellipse on top
					Mimodek.gfx.fill(0,0,0,Mimodek.config.getIntegerSetting("ancestorBrightness"));
					Mimodek.gfx.noStroke();
					Mimodek.gfx.ellipse(1,1,m.radius+2,m.radius+2); //not adding 1 to the radius leaves some bright pixels
				}
			}catch(Exception e){
				e.printStackTrace();
				System.out.println(image.width+","+image.height);
				System.out.println("Is ancestor:"+m.ancestor);
				System.out.println("Radius:"+m.radius);
			}
			/*if(m.ancestor){
				Mimodek.gfx.noTint();
			}*/
			break;
		}
		Mimodek.gfx.popStyle();
		Mimodek.gfx.popMatrix();
	}

}
