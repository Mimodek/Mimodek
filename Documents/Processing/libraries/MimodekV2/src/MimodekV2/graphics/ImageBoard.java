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

import javax.media.opengl.GL;

import MimodekV2.config.Configurator;
import MimodekV2.debug.Verbose;

import mimodek.facade.FacadeFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class ImageBoard.
 */
public class ImageBoard implements Runnable{
	
	/** The current image. */
	int currentImage = 1;
	
	/** The showing since. */
	long showingSince;
	
	/** The alpha. */
	float alpha = 0f;
	
	/** The on. */
	boolean on = false;
	
	/** The runner. */
	Thread runner;
	
	/**
	 * Instantiates a new image board.
	 */
	public ImageBoard(){
		
	}
	
	/**
	 * Instantiates a new image board.
	 *
	 * @param texture the texture
	 * @param showFor the show for
	 * @param fadeSpeed the fade speed
	 * @param turnOnAfter the turn on after
	 */
	public ImageBoard(int texture, long showFor, float fadeSpeed, long turnOnAfter){
		/*
		boardTexture = texture;
		this.showFor = showFor;
		this.fadeSpeed = fadeSpeed;
		this.turnOnAfter = turnOnAfter;
		*/
	}
	
	/**
	 * Start timer.
	 */
	public void startTimer(){
		if(Configurator.getFloatSetting("MESSAGE_BOARD_FREQUENCY")<=0f)
			return;
		runner = new Thread(this);
		runner.start();
	}
	
	/**
	 * Turn on.
	 */
	public void turnOn(){
		Verbose.debug("Turning on image board.");
		on = true;
	}
	
	/**
	 * Turn off.
	 */
	public void turnOff(){
		Verbose.debug("Turning off image board.");
		on = false;
	}
	
	/**
	 * Checks if is on.
	 *
	 * @return true, if is on
	 */
	public boolean isOn(){
		return on || alpha>0f;
	}
	

	
	/**
	 * Draw.
	 */
	public void draw(){
		//handles fade in / fade off
		if(on){
			if(alpha<1f){
				float fadeSpeed = Configurator.getFloatSetting("MESSAGE_BOARD_FADE_SPEED");
				alpha+=alpha+fadeSpeed<=1f?fadeSpeed:1f-alpha;
				if(alpha>=1f)
					showingSince = System.currentTimeMillis();
			}else{
				if(System.currentTimeMillis()-showingSince>=Configurator.getFloatSetting("MESSAGE_BOARD_DURATION")*60000f){
					if(currentImage<Configurator.getIntegerSetting("MESSAGE_BOARD_NUMBER_INT")){
						currentImage++;
						showingSince = System.currentTimeMillis();
					}else{
						turnOff();
					}
				}
				
			}
		}else if(!on && alpha>=0){
			float fadeSpeed = Configurator.getFloatSetting("MESSAGE_BOARD_FADE_SPEED");
			alpha-=alpha>=fadeSpeed?fadeSpeed:alpha;
			if(alpha == 0){ //at that point the draw function shouldn't be called anymore
				currentImage = 1;
				startTimer();
				return;
			}
		}
		GL gl = OpenGL.gl;
		
		OpenGL.color(1f, 1f, 1f, alpha);
		//gl.glDisable(GL.GL_BLEND);
		OpenGL.disableDepth();
		//gl.glDisable(GL.GL_DEPTH_TEST);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		
		OpenGL.enableTexture(0);
		gl.glBindTexture(GL.GL_TEXTURE_2D, OpenGL.textures.get(TextureManager.getTextureIndex(Configurator.getStringSetting("MESSAGE_BOARD_TEXTURE_STR")+currentImage)).getTextureObject());
		gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_DECAL);
		//gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_ADD);
		OpenGL.rect(0f, 0f, FacadeFactory.getFacade().width, FacadeFactory.getFacade().height);
		// reset to default
		
		OpenGL.disableTexture(0);
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try{
				Thread.sleep((long)Configurator.getFloatSetting("MESSAGE_BOARD_FREQUENCY")*60000);
				turnOn();
				return;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
}
