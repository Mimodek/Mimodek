package MimodekV2.graphics;

/*
This is the code source of Mimodek. When not stated otherwise,
it was written by Jonathan 'Jonsku' Cremieux<jonathan.cremieux@aalto.fi> in 2010. 
Copyright (C) yyyy  name of author

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

*/

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import MimodekV2.debug.Verbose;

import controlP5.ListBox;

import processing.core.PApplet;


// TODO: Auto-generated Javadoc
/**
 * The Class TextureManager.
 */
public class TextureManager {
	
	/** The loaded textures. */
	private static HashMap<String,Integer> loadedTextures  = new HashMap<String,Integer>();
	
	/** The min. */
	public static int min = 10;
	
	/** The max. */
	public static int max = 10;
	
	/**
	 * Load all texture in folder.
	 *
	 * @param app the app
	 * @param folderName the folder name
	 */
	public static void loadAllTextureInFolder(PApplet app, File folder){
		//String sketchPath = app.sketchPath;
	    File[] listOfFiles = folder.listFiles();

	    for (int i = 0; i < listOfFiles.length; i++) {
	      if (listOfFiles[i].isFile()) {
	        String candidate = listOfFiles[i].getName();
	        if(candidate.substring(candidate.length()-3).equalsIgnoreCase("png") || candidate.substring(candidate.length()-3).equalsIgnoreCase("jpg")){
	        	int textInd = OpenGL.createTextureFromImage(app.loadImage(folder.getAbsolutePath()+"/"+candidate));
	        	min = PApplet.min(min,textInd);
	        	max = PApplet.max(max,textInd);
	        	loadedTextures.put(candidate.substring(0,candidate.length()-4), textInd);
	        	Verbose.overRule("Loading texture "+candidate+" ... "+textInd);
	        }
	        //if()
	      }
	    }
	}
	
	/**
	 * Gets the name of.
	 *
	 * @param texInd the tex ind
	 * @return the name of
	 */
	public static String getNameOf(int texInd){
		Iterator<String> keys = loadedTextures.keySet().iterator();
		while(keys.hasNext()){
			String name = keys.next();
			if(loadedTextures.get(name) == texInd)
				return name;
		}
		return "Unknown!";
	}
	
	/**
	 * Gets the texture list.
	 *
	 * @return the texture list
	 */
	public static String[] getTextureList(){
		return loadedTextures.keySet().toArray(new String[0]);
	}
	
	/**
	 * Load texture list.
	 *
	 * @param lb the lb
	 */
	public static void loadTextureList(ListBox lb){
		Iterator<String> keys = loadedTextures.keySet().iterator();
		while(keys.hasNext()){
			String name = keys.next();
			lb.addItem(name,loadedTextures.get(name));
		}
	}
	
	/**
	 * Gets the texture index.
	 *
	 * @param name the name
	 * @return the texture index
	 */
	public static int getTextureIndex(String name){
		try{
			return loadedTextures.get(name);
		}catch(Exception e){
			System.out.println("Texture "+name+" is missing. Exiting...");
			return 0;
		}
	}
	
	
}
