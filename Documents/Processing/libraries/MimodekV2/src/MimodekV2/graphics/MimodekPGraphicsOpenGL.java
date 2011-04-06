package MimodekV2.graphics;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLDrawable;
import javax.media.opengl.GLDrawableFactory;

import processing.opengl.PGraphicsOpenGL;

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
 * The Class MimodekPGraphicsOpenGL.
 */
public class MimodekPGraphicsOpenGL extends PGraphicsOpenGL {

	
	/**
	 * Instantiates a new mimodek p graphics open gl.
	 */
	public MimodekPGraphicsOpenGL() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see processing.opengl.PGraphicsOpenGL#allocate()
	 */
	@Override
	protected void allocate() {
		 if (context == null) {
//		      System.out.println("PGraphicsOpenGL.allocate() for " + width + " " + height);
//		      new Exception().printStackTrace(System.out);
		      // If OpenGL 2X or 4X smoothing is enabled, setup caps object for them
		      GLCapabilities capabilities = new GLCapabilities();
		      // Starting in release 0158, OpenGL smoothing is always enabled
		      if (!hints[DISABLE_OPENGL_2X_SMOOTH]) {
		        capabilities.setSampleBuffers(true);
		        capabilities.setNumSamples(2);
		      } else if (hints[ENABLE_OPENGL_4X_SMOOTH]) {
		        capabilities.setSampleBuffers(true);
		        capabilities.setNumSamples(4);
		      }

		      // get a rendering surface and a context for this canvas
		      GLDrawableFactory factory = GLDrawableFactory.getFactory();

		      /*
		      if (PApplet.platform == PConstants.LINUX) {
		        GraphicsConfiguration pconfig = parent.getGraphicsConfiguration();
		        System.out.println("parent config is " + pconfig);

		        //      GraphicsDevice device = config.getDevice();
		        //AbstractGraphicsDevice agd = new AbstractGraphicsDevice(device);
		        //AbstractGraphicsConfiguration agc = factory.chooseGraphicsConfiguration(capabilities, null, null);

		        AWTGraphicsConfiguration agc = (AWTGraphicsConfiguration)
		        factory.chooseGraphicsConfiguration(capabilities, null, null);
		        GraphicsConfiguration config = agc.getGraphicsConfiguration();
		        System.out.println("agc config is " + config);
		      }
		      */
		      //Accumulation buffer
		      capabilities.setAccumBlueBits(8);
		      capabilities.setAccumGreenBits(8);
		      capabilities.setAccumRedBits(8);
		      
		     // System.out.println(capabilities.toString());

		      drawable = factory.getGLDrawable(parent, capabilities, null);
		      context = drawable.createContext(null);

		      // need to get proper opengl context since will be needed below
		      gl = context.getGL();
		      // Flag defaults to be reset on the next trip into beginDraw().
		      settingsInited = false;

		    } else {
		      // The following three lines are a fix for Bug #1176
		      // http://dev.processing.org/bugs/show_bug.cgi?id=1176
		      context.destroy();
		      context = drawable.createContext(null);
		      gl = context.getGL();
		      reapplySettings();
		    }
	}
	
	/**
	 * Gets the drawable.
	 *
	 * @return the drawable
	 */
	public GLDrawable getDrawable(){
		return drawable;
	}

	
}
