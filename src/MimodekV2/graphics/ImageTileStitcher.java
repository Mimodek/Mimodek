package MimodekV2.graphics;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import processing.core.PApplet;
import processing.core.PImage;

// TODO: Auto-generated Javadoc
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

/**
 * Little utility to stitch back high resolution image exported from Mimodek.
 */
public class ImageTileStitcher extends PApplet {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see processing.core.PApplet#setup()
	 */
	@Override
	public void setup(){
		boolean deleteAfter = false;
		try{
		print("Would you like to erase the tiles after the stiching is completed?(Y/n) ");
		BufferedReader stdin = new BufferedReader(new
			       InputStreamReader(System.in));                     
			  String answer = stdin.readLine();
			  if(answer.equals("Y")){
				  deleteAfter = true;
			  }
			  println("Tiles images "+(deleteAfter?"will":"won't")+" be deleted on completion.");
		}catch(Exception e){
			println("Tiles images won't be deleted on completion.");
		}
		
		try{
			PImage tile = loadImage(sketchPath+"/tiles/tile_0_0.png");
			int tileW = tile.width;
			int tileH = tile.height;
			println("Tile dimensions: "+tileW+" X "+tileH);
			//now see how many tiles we have in both directions
			int i = 1;
			File tmpFile = new File(sketchPath+"/tiles/tile_1_0.png");
			while(tmpFile.exists()){
				i++;
				tmpFile = new File(sketchPath+"/tiles/tile_"+i+"_0.png");
			}
			int j = 1;
			tmpFile = new File(sketchPath+"/tiles/tile_0_1.png");
			while(tmpFile.exists()){
				j++;
				tmpFile = new File(sketchPath+"/tiles/tile_0_"+j+".png");
			}
			int imageW = i*tileW;
			int imageH = j*tileH;
			println("Resulting image size:"+imageW+" X "+imageH);
			
			int currentTile = 0;
			PImage result = new PImage(i*tile.width, j*tile.height);
			result.loadPixels();
			for(int _i=0;_i<i;_i++){
				for(int _j=0;_j<j;_j++){
					if(_i+_j!=0){
						tile = loadImage(sketchPath+"/tiles/tile_"+_i+"_"+_j+".png");
					}
					currentTile++;
					tile.loadPixels();
					//to find the current position in the pixels array
					int xStart = _i*tile.width;
					int yStart = _j*tile.height;
					//copy pixels line by line
					for(int line = 0;line<tile.height;line++){
						System.arraycopy(tile.pixels, line*tile.width, result.pixels, xStart+(yStart+line)*result.width, tile.width);	
					}
					print(getProgressBar(currentTile, (i*j), 10));
				}	
			}
			result.updatePixels();
			//and that's it, easy peasy
			result.save(sketchPath+"/tiles/Stitched_"+i*tile.width+"_"+j*tile.height+".png");
			if(deleteAfter){
				print("Deleting tiles images...");
				for(;i>=0;i--){
					for(;j>=0;j--){
						tmpFile = new File(sketchPath+"/tiles/tile_"+i+"_"+j+".png");
						if(tmpFile.exists()){
							tmpFile.delete();
						}
					}
				}
				println("Done!");
			}
		}catch(Exception e){
			e.printStackTrace();
			println("No tile_0_0 found. Can't do anything. Byeeee!");
			
		}finally{
			println("Stitching done, enjoy!                    \n");
			exit();
		}
		
	}
	
	String getProgressBar(float pos, float total, int size){
		float barNum = (pos/total)*(float)size;
		StringBuilder str = new StringBuilder("\r|");
		for(int i=0;i<size;i++){
			if(i<barNum)
				str.append("=");
			else
				str.append(" ");
		}
		str.append("| "+(int)((pos/total)*100)+"%");
		return str.toString();
	}

}
