package mimodek.texture;

import java.io.File;
import java.util.ArrayList;

import processing.core.PImage;

import mimodek.Mimo;
import mimodek.MainHandler;

public class Texturizer {
	public static final int CIRCLE = 1;
	public static final int IMAGE = 2;
	public static final int GENERATED = 3;

	public int mode = 2;

	public int ancestor = 0;
	public int active = 1;

	public ArrayList<SquareTexture> textures;

	public static boolean drawCircle = true;
	public static int ancestorBrightness = 125;
	
	public SimpleDrawer circleDrawer;
	public SimpleDrawer seedDrawer;
	
	

	public Texturizer() {
		// load the textures
		textures = new ArrayList<SquareTexture>();
		loadTextures(MainHandler.app.dataPath("images/"));

		// initialize the drawers
		circleDrawer = new RadialGradient();
		seedDrawer = new SeedGradient();
	}

	public void loadTextures(String textureFolder) {
		File folder = new File(textureFolder);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				System.out.println("File " + listOfFiles[i].getName());
				textures.add(new SquareTexture(textureFolder
						+ listOfFiles[i].getName(), Mimo.maxRadius));
			} else if (listOfFiles[i].isDirectory()) {
				System.out.println("Directory " + listOfFiles[i].getName());
			}
		}
	}

	private void applyStyle(Mimo m) {
		if (mode == CIRCLE) {
			if (m.ancestor) {
				MainHandler.gfx.noFill();
				MainHandler.gfx.strokeWeight((float) 0.5);
				MainHandler.gfx.stroke(125);
			} else {
				MainHandler.gfx.noStroke();
				MainHandler.gfx.fill(255, 255, 255, 100);
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
		switch (mode) {
		case IMAGE:
			MainHandler.gfx.rotate((float) Math.atan2(m.vel.y, m.vel.x));
			if (m.ancestor) {
				MainHandler.gfx.tint(ancestorBrightness);
				textures.get(ancestor).draw(m.radius / Mimo.maxRadius);
				MainHandler.gfx.noTint();
			} else {
				textures.get(active).draw(m.radius / Mimo.maxRadius);
			}
			break;
		case CIRCLE:
			MainHandler.gfx.ellipse(0, 0, m.radius, m.radius);
			break;
		case GENERATED:
			MainHandler.gfx.rotate((float) Math.atan2(m.vel.y, m.vel.x));
			SimpleDrawer drawer = drawCircle?circleDrawer:seedDrawer;
			if (m.drawingData == null) {
				m.drawingData = drawer.getDrawingData(m);
			}
			PImage image =drawer.draw(m.drawingData);
			if(m.ancestor){
				//darken
				MainHandler.gfx.tint(ancestorBrightness);
			}
			MainHandler.gfx.image(image,-image.width/2,-image.height/2);
			if(m.ancestor){
				MainHandler.gfx.noTint();
			}
			break;
		}
		MainHandler.gfx.popStyle();
		MainHandler.gfx.popMatrix();
	}


}
