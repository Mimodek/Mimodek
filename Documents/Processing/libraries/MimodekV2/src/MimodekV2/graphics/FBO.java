package MimodekV2.graphics;

import com.sun.opengl.util.texture.Texture;

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
 * The Class FBO.
 */
public class FBO {
	
	/** The id. */
	int id;
	

	/** The width. */
	int width;
	
	/** The height. */
	int height;
	
	/** The depth buffer. */
	int depthBuffer;
	
	/** The texture. */
	Texture texture;
	
	/** The enabled. */
	boolean enabled = false;
	
	/**
	 * Creates the fbo.
	 *
	 * @param width the width
	 * @param height the height
	 * @return the fBO
	 * @throws Exception the exception
	 */
	public static FBO createFBO(int width, int height) throws Exception{
		return new FBO(OpenGL.initFBO(width, height),width,height);
	}
	
	/**
	 * Instantiates a new fBO.
	 *
	 * @param ids the ids
	 * @param width the width
	 * @param height the height
	 */
	protected FBO(Object[] ids, int width, int height){
		id = (Integer)ids[0];
		depthBuffer = (Integer)ids[1];
		texture = (Texture)ids[2];
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Enable.
	 */
	public void enable(){
		OpenGL.activateFBO(id, width, height);
		OpenGL.outputError();
		enabled = true;
	}
	
	/**
	 * Disable.
	 */
	public void disable(){
		OpenGL.deactivateFBO();
		OpenGL.outputError();
		enabled = false;
	}
	
	/**
	 * Checks if is enabled.
	 *
	 * @return true, if is enabled
	 */
	public boolean isEnabled(){
		return enabled;
	}
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Gets the width.
	 *
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Gets the height.
	 *
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Gets the depth buffer.
	 *
	 * @return the depth buffer
	 */
	public int getDepthBuffer() {
		return depthBuffer;
	}
	
	/**
	 * Gets the texture.
	 *
	 * @return the texture
	 */
	public Texture getTexture() {
		return texture;
	}

	/**
	 * Gets the texture id.
	 *
	 * @return the texture id
	 */
	public int getTextureId() {
		return texture.getTextureObject();
	}
	
	/**
	 * Release.
	 */
	public void release(){
		OpenGL.cleanFBO(new int[]{id,depthBuffer});
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return "FBO #"+id+", depth buffer #"+depthBuffer+", texture #"+texture.getTextureObject()+" "+width+" X "+height;
	}
}
