package mimodek.decorator.graphics;

import java.io.File;
import java.util.ArrayList;

import processing.core.PApplet;

import mimodek.configuration.Configurator;

public class TextureCollection {
	protected static TextureCollection collection;
	
	private ArrayList<SquareTexture> textures;
	
	protected TextureCollection(String textureFolder, PApplet app){
		textures = new ArrayList<SquareTexture>();
		loadTextures(textureFolder, app);
	}
	
	public static void loadTextures(PApplet app){
		collection = new TextureCollection(app.dataPath("images/"), app);
		
	}
	
	protected SquareTexture getTexture(int textureInd){
		if(textureInd>=0 && textureInd<textures.size())
			return textures.get(textureInd);
		return null;
	}
	
	public static SquareTexture get(int textureInd){
		return collection.getTexture(textureInd);
	}
	
	public static int size(){
		return collection.getSize();
	}
	
	
	
	protected int getSize() {
		return textures.size();
	}

	protected void loadTextures(String textureFolder,PApplet app) {
		File folder = new File(textureFolder);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				System.out.println("File " + listOfFiles[i].getName());
				textures.add(new SquareTexture(textureFolder
						+ listOfFiles[i].getName(), Configurator.getFloatSetting("mimosMaxRadius"),app));
			} else if (listOfFiles[i].isDirectory()) {
				System.out.println("Directory " + listOfFiles[i].getName());
			}
		}
	}
}
