package MimodekV2.debug;
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
import java.io.IOException;

import javax.media.opengl.GL;
import javax.media.opengl.GLException;

import com.sun.opengl.util.texture.TextureIO;

import processing.core.PApplet;
import MimodekV2.graphics.FBO;
import MimodekV2.graphics.OpenGL;

// TODO: Auto-generated Javadoc
/**
 * The Class HighResTest.
 */
public class HighResTest extends PApplet {
	
	/** The fbo. */
	private FBO fbo;
	
	/** The use fbo. */
	boolean useFBO = true;
	
	/** The size. */
	int size = 5;
	
	/* (non-Javadoc)
	 * @see processing.core.PApplet#setup()
	 */
	@Override
	public void setup() {
		// Resolution to display on Media lab prado facade
		// size(1024, 768, OPENGL);
		// Using custom capabilities (accumulation buffer,...)
		size(200, 200, "MimodekV2.graphics.MimodekPGraphicsOpenGL");
		// To film in PAL
		// size(720,576, OPENGL);
		colorMode(RGB, 1.0f);
		frameRate(30);
		println("Start");
		OpenGL.begin(this);
		OpenGL.setupGLFor2D(this);
		OpenGL.outputError();
		//OpenGL.createCircle(this);

		//OpenGL.begin(this);
		try {
			fbo = FBO.createFBO(width, height);
		} catch (Exception e) {
			e.printStackTrace();
			exit();
		}
		OpenGL.outputError();
		OpenGL.end(this);
		noLoop();
	}

	/* (non-Javadoc)
	 * @see processing.core.PApplet#draw()
	 */
	@Override
	public void draw() {
		
		background(0,1);
		OpenGL.begin(this);
		if(!useFBO){
		render();
		}else{
		GL gl = OpenGL.gl;
		for(int i=0;i<20;i++){
			for(int j=0;j<20;j++){
				gl.glEnable(GL.GL_TEXTURE_2D);
				fbo.enable();

				gl.glPushMatrix();
				//gl.glScalef(20f, 20f, 1f);
				gl.glTranslatef(-i*width, -j*height, 0f);
				//OpenGL.gl.glClearColor(19f/i, 0f,19f/j,1f);
				render();
				gl.glPopMatrix();
				fbo.disable();
				//if(i%2==0 || j%2==0){
						
					//OpenGL.deactivateFBO();
					OpenGL.gl.glActiveTexture(GL.GL_TEXTURE0);
					OpenGL.gl.glBindTexture(GL.GL_TEXTURE_2D, fbo.getTextureId());
					try {
						TextureIO.write(fbo.getTexture(), new File(sketchPath+"/screen_shots/tile_"+i+"_"+j+".png"));
					} catch (GLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					OpenGL.outputError();
					OpenGL.gl.glEnable(GL.GL_TEXTURE_2D);
					// use fbo's texture to texture quad
					OpenGL.color(1f, 0.0f, 0f);
					//OpenGL.gl.glActiveTexture(GL.GL_TEXTURE0);
					
					
					OpenGL.gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_DECAL);
			
					OpenGL.rect(i*10, j*10+10, 10, -10);
					OpenGL.outputError();
				//}
			}
		}
		}
		OpenGL.gl.glClearColor(0f, 0f,0f,1f);
		
		/*
		OpenGL.gl.glEnable(GL.GL_TEXTURE_2D);
		//OpenGL.gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		if(useFBO){
			// Render in fbo
			fbo.enable();
			//OpenGL.activateFBO(fbo[0],width, height); //make sure we draw in a big enough area
		}
		//OpenGL.gl.glDisable(GL.GL_TEXTURE_2D);
		OpenGL.background(0.3f, 0.3f, 0.3f, 1f);
		drawTexture();
		
		if(useFBO){
			fbo.disable();
			//OpenGL.deactivateFBO();
			OpenGL.gl.glActiveTexture(GL.GL_TEXTURE0);
			OpenGL.gl.glBindTexture(GL.GL_TEXTURE_2D, fbo.getTextureId());
			OpenGL.outputError();
		
			OpenGL.background(0f, 0f, 0f, 1f);
			OpenGL.gl.glEnable(GL.GL_TEXTURE_2D);
			// use fbo's texture to texture quad
			OpenGL.color(1f, 0.0f, 0f);
			//OpenGL.gl.glActiveTexture(GL.GL_TEXTURE0);
			
			
			OpenGL.gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_DECAL);
	
			OpenGL.rect(10, 10, 80, 180);
			OpenGL.outputError();
		}*/
		OpenGL.end(this);
		
		//OpenGL.end(this);
	}
	
	/**
	 * Render.
	 */
	public void render(){
		//OpenGL.gl.glClearColor(1f, 1f,1f,1f);
		OpenGL.gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		OpenGL.gl.glDisable(GL.GL_TEXTURE_2D);
		
		OpenGL.gl.glPushMatrix();
		OpenGL.color(0f, 1.0f, 0f,1f);
		OpenGL.rect(width/2f,height/2f, width*19f, height*19f);
		//OpenGL.rect(0,0, width, height);
		//OpenGL.gl.glEnable(GL.GL_TEXTURE_2D);
		OpenGL.gl.glPopMatrix();
	}
	
	/**
	 * Dispose.
	 */
	public void dispose(){
		OpenGL.begin(this);

		//You also have to clean up any renderbuffers you might have allocated, in this case the depthbuffer renderbuffer needs to deleted, which again works the same way as cleaning up a texture:

		//OpenGL.cleanFBO(fbo);
		OpenGL.end(this);

	}

	/* (non-Javadoc)
	 * @see processing.core.PApplet#mouseDragged()
	 */
	@Override
	public void mouseDragged() {
		// mimodek.mouseDragged();
	}

	/* (non-Javadoc)
	 * @see processing.core.PApplet#keyPressed()
	 */
	@Override
	public void keyPressed() {
		useFBO = ! useFBO;
	}
}

