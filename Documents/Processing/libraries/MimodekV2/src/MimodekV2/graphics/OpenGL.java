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
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.GLException;
import javax.media.opengl.glu.GLU;

//import mimodek.facade.FacadeFactory;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.opengl.PGraphicsOpenGL;

import MimodekV2.config.Configurator;

import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureData;
import com.sun.opengl.util.texture.TextureIO;
import com.sun.opengl.util.texture.spi.TextureWriter;

// TODO: Auto-generated Javadoc
/**
 * The Class OpenGL.
 */
public class OpenGL {

	/**
	 * The Enum TextureFilters.
	 */
	public enum TextureFilters {
		
		/** The G l_ nearest. */
		GL_NEAREST(GL.GL_NEAREST), 
 /** The G l_ linear. */
 GL_LINEAR(GL.GL_LINEAR), 
 /** The G l_ neares t_ mipma p_ nearest. */
 GL_NEAREST_MIPMAP_NEAREST(
				GL.GL_NEAREST_MIPMAP_NEAREST), 
 /** The G l_ neares t_ mipma p_ linear. */
 GL_NEAREST_MIPMAP_LINEAR(
				GL.GL_NEAREST_MIPMAP_LINEAR), 
 /** The G l_ linea r_ mipma p_ nearest. */
 GL_LINEAR_MIPMAP_NEAREST(
				GL.GL_LINEAR_MIPMAP_NEAREST), 
 /** The G l_ linea r_ mipma p_ linear. */
 GL_LINEAR_MIPMAP_LINEAR(
				GL.GL_LINEAR_MIPMAP_LINEAR);

		/** The value. */
		private int value;

		/**
		 * Instantiates a new texture filters.
		 *
		 * @param filterCode the filter code
		 */
		TextureFilters(int filterCode) {
			this.value = filterCode;
		}

		/**
		 * Gets the value.
		 *
		 * @return the value
		 */
		public int getValue() {
			return value;
		}
	}

	/** The width. */
	public static int width;
	
	/** The height. */
	public static int height;
	
	/** The CIRCL e_ texture. */
	public static int CIRCLE_TEXTURE;
	
	/** The gl. */
	public static GL gl;
	
	/** The glu. */
	public static GLU glu;
	
	/** The texture units number. */
	public static int textureUnitsNumber = -1;
	
	/** The poin sizet info. */
	public static PVector poinSizetInfo = null;
	
	/** The textures. */
	public static ArrayList<Texture> textures = new ArrayList<Texture>();

	// setup the projection for working in 2D
	/**
	 * Sets the up gl for2 d.
	 *
	 * @param app the new up gl for2 d
	 */
	public static void setupGLFor2D(PApplet app) {
		width = app.width;
		height = app.height;
		app.colorMode(PConstants.RGB, 1.0f);
		//gl = ((PGraphicsOpenGL) app.g).gl; 
		glu = ((PGraphicsOpenGL) app.g).glu;
		
		// gl.glEnable(GL.GL_MULTISAMPLE);
		
		
		gl.glEnable(GL.GL_POINT_SMOOTH);
		gl.glHint(GL.GL_POINT_SMOOTH_HINT, GL.GL_NICEST);
		gl.glEnable(GL.GL_LINE_SMOOTH);
		gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
		gl.glEnable(GL.GL_POLYGON_SMOOTH);
		gl.glHint(GL.GL_POLYGON_SMOOTH_HINT, GL.GL_NICEST);
		
		/*
		if(Configurator.getBooleanSetting("FILMING_FLAG")){
			gl.
			glu.glut  .glutInitDisplayMode(GL.GL_DOUBLE | GL.GL_RGB | GL.GL_DEPTH | GL.GL_ACCUM); 
		}
		*/
		/*
		float nRange = 100.0f;
		// Set Viewport to window dimensions
		gl.glViewport(0, 0, width, height);

		// Reset projection matrix stack
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();

		// Establish clipping volume (left, right, bottom, top, near, far)
		if (width <= height)
			gl.glOrtho(-nRange, nRange, -nRange * height / width, nRange
					* height / width, -nRange, nRange);
		else
			gl.glOrtho(-nRange * width / height, nRange * width / height,
					-nRange, nRange, -nRange, nRange);

		// Reset Model view matrix stack
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		*/
		//createCircle(app);
		//((PGraphicsOpenGL) app.g).endGL();
		//enableBlending();
		
		
	}
	
	/**
	 * Register texture writer.
	 *
	 * @param textureWriter the texture writer
	 */
	public static void registerTextureWriter(TextureWriter textureWriter){
		//register our custom textureWriter
		TextureIO.addTextureWriter(textureWriter);
	}

	/**
	 * Begin.
	 *
	 * @param app the app
	 * @return the gL
	 */
	public static GL begin(PApplet app) {
		if(gl==null)
			gl = ((PGraphicsOpenGL) app.g).beginGL();
		return gl;
	}

	/**
	 * End.
	 *
	 * @param app the app
	 */
	public static void end(PApplet app) {
		if(gl==null)
			return;
		((PGraphicsOpenGL) app.g).endGL();
		gl = null;
	}

	/**
	 * Background.
	 *
	 * @param r the r
	 * @param g the g
	 * @param b the b
	 * @param a the a
	 */
	public static void background(float r, float g, float b, float a) {
		gl.glClearColor(r, g, b, a);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
	}
	
	/**
	 * Clear depth.
	 *
	 * @param r the r
	 * @param g the g
	 * @param b the b
	 * @param a the a
	 */
	public static void clearDepth(float r, float g, float b, float a){
		gl.glClearColor(r, g, b, a);
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
	}

	// Return the number of supported texture for multi texturing
	/**
	 * How many texture unit supported.
	 *
	 * @return the int
	 */
	public static int howManyTextureUnitSupported() {
		if (textureUnitsNumber < 0) {
			IntBuffer intB = IntBuffer.allocate(1);
			gl.glGetIntegerv(GL.GL_MAX_TEXTURE_UNITS, intB);
			// println(intB.get()+" textures supported for multitexturing.");
			textureUnitsNumber = intB.get();
		}
		return textureUnitsNumber;
	}

	/**
	 * About point size.
	 *
	 * @return the p vector
	 */
	public static PVector aboutPointSize() {
		if (poinSizetInfo== null) {
			FloatBuffer fB = FloatBuffer.allocate(2);
			// Get supported point size range and step size
			gl.glGetFloatv(GL.GL_POINT_SIZE_RANGE, fB);
			poinSizetInfo = new PVector(fB.get(0), fB.get(1), 0);
			fB.rewind();
			gl.glGetFloatv(GL.GL_POINT_SIZE_GRANULARITY, fB);
			poinSizetInfo.z = fB.get(0);
			// System.out.println("Point size range :"+poinSizetInfo.x+" to "+poinSizetInfo.y+", increment "+poinSizetInfo.z);
		}
		return poinSizetInfo;
	}

	/* Generate the texture for the creatures */
	/**
	 * Creates the creature texture.
	 *
	 * @param app the app
	 * @param STRENGTH the sTRENGTH
	 * @return the int
	 */
	public static int createCreatureTexture(PApplet app, float STRENGTH) {
		PGraphics gfx = app.createGraphics(512, 512, PConstants.JAVA2D);
		gfx.beginDraw();
		//gfx.background(0);
		gfx.loadPixels();
		for (int i = 0; i < gfx.width; i++) {
			for (int j = 0; j < gfx.height; j++) {
				float v = equation(app, gfx.width, gfx.height, i, j, STRENGTH);
				// v = v > 0.95 * STRENGTH ? 1 : v / 2;
				if (v == 0)
					gfx.pixels[i + j * gfx.width] = gfx.color(0, 0, 0, 0);
				else
					gfx.pixels[i + j * gfx.width] = gfx.lerpColor(gfx.color(0), gfx.color(255), v);
			}
		}
		gfx.updatePixels();
		gfx.endDraw();
		int t = createTextureFromImage(gfx);
		gfx.dispose();
		return t;
	}

	/**
	 * Equation.
	 *
	 * @param app the app
	 * @param w the w
	 * @param h the h
	 * @param x the x
	 * @param y the y
	 * @param strength the strength
	 * @return the float
	 */
	protected static float equation(PApplet app, float w, float h, float x,
			float y, float strength) {
		float d = PApplet.dist(w / 2, h / 2, x, y);
		if (d > w / 2) {
			return 0;
		}
		if (d < w / 16) {
			return strength;
		}
		return (1 - (d / (w / 2))) * strength;
		//return PApplet.sin(PApplet.PI*((1 - (d / (w / 2))))) * strength;
		// circle around center
		//return ((1f+PApplet.sin((d/(w/2))*2f*PApplet.TWO_PI))/2f)*((1 - (d / (w / 2))) * strength);
	}

	/* By default mipmap generation is activated */
	/**
	 * Creates the texture from image.
	 *
	 * @param img the img
	 * @return the int
	 */
	public static int createTextureFromImage(PImage img) {
		return createTextureFromImage(img, true);
	}

	/**
	 * Creates the texture from image.
	 *
	 * @param img the img
	 * @param useMipmap the use mipmap
	 * @return the int
	 */
	public static int createTextureFromImage(PImage img, boolean useMipmap) {
		IntBuffer buf = BufferUtil.newIntBuffer(img.width * img.height);
		buf.put(img.pixels);
		buf.rewind();
		TextureData textureData = new TextureData(GL.GL_RGBA, img.width,
				img.height, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, useMipmap,
				false, false, buf, new TextureFlusher());
		textures.add(TextureIO.newTexture(textureData));
		return textures.size() - 1;
	}

	/*
	 * public static void rotateTexture(float angle){ // apply a rotation to the
	 * texture gl.glActiveTexture(GL.GL_TEXTURE0);
	 * gl.glMatrixMode(GL.GL_TEXTURE); gl.glLoadIdentity();
	 * gl.glTranslatef(0.5f,0.5f,0.0f); gl.glRotatef(angle,0.0f,0.0f,1.0f);
	 * gl.glTranslatef(-0.5f,-0.5f,0.0f); gl.glMatrixMode(GL.GL_MODELVIEW); }
	 */
	/**
	 * Multi texture.
	 *
	 * @param maskTexture the mask texture
	 * @param insideTexture the inside texture
	 */
	public static void multiTexture(int maskTexture, int insideTexture) {

		gl.glActiveTexture(GL.GL_TEXTURE0);
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glBindTexture(GL.GL_TEXTURE_2D, textures.get(insideTexture)
				.getTextureObject());
		// use linear filtering
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
				GL.GL_LINEAR);
		// gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
		// GL.GL_LINEAR);

		// gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
		// GL.GL_NEAREST);
		gl
				.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
						TextureFilters.values()[Configurator
								.getIntegerSetting("GL_TEXTURE_MIN_FILTER_INT")]
								.getValue());
		gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
		// appl

		gl.glActiveTexture(GL.GL_TEXTURE1);
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glBindTexture(GL.GL_TEXTURE_2D, textures.get(maskTexture)
				.getTextureObject());// mask texture

		// Use the first texture as mask for the 2nd one while using the color of the geometry
		// to taint the result
		gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_ADD);
		// gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE,
		// GL.GL_MODULATE);
	}

	/* create a circle texture */
	/**
	 * Creates the circle.
	 *
	 * @param app the app
	 */
	public static void createCircle(PApplet app) {
		PGraphics gfx = app.createGraphics(512, 512, PConstants.JAVA2D);
		gfx.beginDraw();
		gfx.smooth();
		gfx.background(0, 0);
		gfx.noStroke();
		gfx.fill(0, 0, 0, 255);
		gfx.ellipse(256, 256, 256, 256);
		gfx.endDraw();
		CIRCLE_TEXTURE = createTextureFromImage(gfx);
		gfx.dispose();
		//uncomment to save to file
		//writeTextureToFile("circle.png", app);
	}

	/**
	 * Circle.
	 *
	 * @param x the x
	 * @param y the y
	 * @param rx the rx
	 * @param ry the ry
	 */
	public static void circle(float x, float y, float rx, float ry) {
		enableTexture(0);
		gl.glBindTexture(GL.GL_TEXTURE_2D, textures.get(CIRCLE_TEXTURE).getTextureObject());
		gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_ADD);
		rect(x - rx / 2f, y - ry / 2f, rx, ry);
		// reset to default
		gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
		disableTexture(0);
		// gl.glDisable(GL.GL_TEXTURE_2D);

	}

	// Draw a textured circle
	/**
	 * Circle.
	 *
	 * @param x the x
	 * @param y the y
	 * @param rx the rx
	 * @param ry the ry
	 * @param t the t
	 */
	public static void circle(float x, float y, float rx, float ry, int t) {
		multiTexture(CIRCLE_TEXTURE, t);

		rectMultiTexture(x - rx / 2f, y - ry / 2f, rx, ry);
		// reset to default
		gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
		disableTexture(1);
		// gl.glDisable(GL.GL_TEXTURE_2D);
	}

	
	// Draw a textured circle which textures has already been set
	/**
	 * Circle multi texture.
	 *
	 * @param x the x
	 * @param y the y
	 * @param rx the rx
	 * @param ry the ry
	 */
	public static void circleMultiTexture(float x, float y, float rx, float ry) {

		rectMultiTexture(x - rx / 2f, y - ry / 2f, rx, ry);
		// reset to default
		// gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE,
		// GL.GL_MODULATE);
	}

	/**
	 * Line.
	 *
	 * @param x1 the x1
	 * @param y1 the y1
	 * @param z1 the z1
	 * @param x2 the x2
	 * @param y2 the y2
	 * @param z2 the z2
	 */
	public static void line(float x1, float y1, float z1, float x2, float y2,
			float z2) {
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(x1, y1, z1);
		gl.glVertex3f(x2, y2, z2);
		gl.glEnd();
	}

	/**
	 * Line.
	 *
	 * @param x1 the x1
	 * @param y1 the y1
	 * @param x2 the x2
	 * @param y2 the y2
	 */
	public static void line(float x1, float y1, float x2, float y2) {
		line(x1, y1, 0f, x2, y2, 0f);
	}

	/**
	 * Rect multi texture.
	 *
	 * @param x the x
	 * @param y the y
	 * @param width the width
	 * @param height the height
	 */
	public static void rectMultiTexture(float x, float y, float width,
			float height) {
		gl.glBegin(GL.GL_QUADS);

		gl.glMultiTexCoord2f(GL.GL_TEXTURE0, 0.0f, 0.0f);
		gl.glMultiTexCoord2f(GL.GL_TEXTURE1, 0.0f, 0.0f);
		gl.glVertex3f(x, y, 0f);

		gl.glMultiTexCoord2f(GL.GL_TEXTURE0, 1.0f, 0.0f);
		gl.glMultiTexCoord2f(GL.GL_TEXTURE1, 1.0f, 0.0f);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(x + width, y, 0f);

		gl.glMultiTexCoord2f(GL.GL_TEXTURE0, 1.0f, 1.0f);
		gl.glMultiTexCoord2f(GL.GL_TEXTURE1, 1.0f, 1.0f);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(x + width, y + height, 0f);

		gl.glMultiTexCoord2f(GL.GL_TEXTURE0, 0.0f, 1.0f);
		gl.glMultiTexCoord2f(GL.GL_TEXTURE1, 0.0f, 1.0f);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(x, y + height, 0f);
		gl.glEnd();
	}

	/**
	 * Rect.
	 *
	 * @param x the x
	 * @param y the y
	 * @param width the width
	 * @param height the height
	 */
	public static void rect(float x, float y, float width, float height) {
		gl.glBegin(GL.GL_QUADS);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(x, y, 0f);

		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(x + width, y, 0f);

		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(x + width, y + height, 0f);

		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(x, y + height, 0f);
		gl.glEnd();
	}

	/**
	 * Processing color to vector.
	 *
	 * @param app the app
	 * @param c the c
	 * @return the p vector
	 */
	public static PVector processingColorToVector(PApplet app, int c) {
		return new PVector(app.red(c), app.green(c), app.blue(c));
	}

	/**
	 * Color.
	 *
	 * @param app the app
	 * @param c the c
	 */
	public static void color(PApplet app, int c) {
		color(app.red(c), app.green(c), app.blue(c));
	}

	/**
	 * Color.
	 *
	 * @param r the r
	 */
	public static void color(float r) {
		color(r, r, r, 1f);
	}

	/**
	 * Color.
	 *
	 * @param r the r
	 * @param g the g
	 * @param b the b
	 */
	public static void color(float r, float g, float b) {
		color(r, g, b, 1f);
	}

	/**
	 * Color.
	 *
	 * @param r the r
	 * @param g the g
	 * @param b the b
	 * @param a the a
	 */
	public static void color(float r, float g, float b, float a) {
		gl.glColor4f(r, g, b, a);
	}

	/**
	 * Color.
	 *
	 * @param color the color
	 */
	public static void color(PVector color) {
		// TODO Auto-generated method stub
		color(color.x, color.y, color.z);
	}

	/**
	 * Color.
	 *
	 * @param color the color
	 * @param alpha the alpha
	 */
	public static void color(PVector color, float alpha) {
		// TODO Auto-generated method stub
		color(color.x, color.y, color.z, alpha);
	}

	/**
	 * Point size.
	 *
	 * @param num the num
	 */
	public static void pointSize(float num) {
		num = num < poinSizetInfo.x ? poinSizetInfo.x
				: (num > poinSizetInfo.y ? poinSizetInfo.y : num);
		gl.glPointSize(num);
	}

	/**
	 * Point sprite.
	 *
	 * @param texture the texture
	 * @param size the size
	 * @param r the r
	 * @param g the g
	 * @param b the b
	 * @param a the a
	 */
	public static void pointSprite(int texture, float size, float r, float g,
			float b, float a) {
	
		
		// set the sprite Size
		pointSize(size); // monkey with this
		// gl.glPointParameteri(GL.GL_POINT_SIZE_MAX, 256); // this too
		//gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_ADD);
		
		// render the point
		gl.glBegin(GL.GL_POINTS);
		gl.glColor4f(r, g, b, a);
		gl.glVertex3f(0f, 0f, 0f);
		gl.glEnd();
		
	}
	
	/**
	 * Enable depth.
	 */
	public static void enableDepth(){
		gl.glEnable(GL.GL_DEPTH_TEST); 
	}
	
	/**
	 * Disable depth.
	 */
	public static void disableDepth(){
		gl.glDisable(GL.GL_DEPTH_TEST); 
	}
	
	

	/**
	 * Enable blending.
	 *
	 * @param component the component
	 * @param function the function
	 */
	public static void enableBlending(int component, int function) {
		gl.glEnable(GL.GL_BLEND); // enabling blend
		gl.glBlendFunc(component, function);
		//gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE); // defining additive
	}

	/**
	 * Disable blending.
	 */
	public static void disableBlending() {
		gl.glDisable(GL.GL_BLEND);
	}

	/**
	 * Disable texture.
	 *
	 * @param level the level
	 */
	public static void disableTexture(int level) {
		for(int i=0;i<=level;i++){
		gl.glActiveTexture(GL.GL_TEXTURE0+i);
		gl.glDisable(GL.GL_TEXTURE_2D);
		}
	}

	/**
	 * Enable texture.
	 *
	 * @param level the level
	 */
	public static void enableTexture(int level) {
		for(int i=0;i<=level;i++){
			gl.glActiveTexture(GL.GL_TEXTURE0+i);
			gl.glEnable(GL.GL_TEXTURE_2D);
			}
	}

	/**
	 * Write texture to file.
	 *
	 * @param fileName the file name
	 * @param app the app
	 */
	public static void writeTextureToFile(String fileName, PApplet app) {
		try {
			TextureIO.write(OpenGL.textures.get(OpenGL.CIRCLE_TEXTURE),
					new File(app.sketchPath + "/" + fileName));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Save snap shot.
	 *
	 * @param filePath the file path
	 * @param fileName the file name
	 * @param x the x
	 * @param y the y
	 * @param w the w
	 * @param h the h
	 * @throws Exception the exception
	 */
	public static void saveSnapShot(String filePath, String fileName, int x,
			int y, int w, int h) throws Exception {
		gl.glDisable(GL.GL_DEPTH_TEST);
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		float scaling = Configurator.getFloatSetting("GLOBAL_SCALING");
		// OpenGL.gl.glScalef(Configurator.getFloatSetting("GLOBAL_SCALING"),
		// Configurator.getFloatSetting("GLOBAL_SCALING"), 1f);
		Texture tmpTex = TextureIO.newTexture(GL.GL_TEXTURE_2D);
		//System.out.println(tmpTex.getWidth() + "," + tmpTex.getHeight());
		tmpTex.enable();
		tmpTex.bind();
		
		// !!!OpenGL coordinates!!!
		gl.glCopyTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, x, PApplet
				.round(height - y - h * scaling), PApplet.round(w * scaling),
				PApplet.round(h * scaling), 0);
		try {
			TextureIO.write(tmpTex, new File(filePath + "/" + fileName));
		} finally {
			tmpTex.disable();
			tmpTex.dispose();
			gl.glEnable(GL.GL_DEPTH_TEST);
		}
	}

	/**
	 * Gets the snap shot.
	 *
	 * @param x the x
	 * @param y the y
	 * @param w the w
	 * @param h the h
	 * @return the snap shot
	 */
	public static Texture getSnapShot(int x, int y, int w, int h){
		
		//gl.glClear(GL.GL_DEPTH_BUFFER_BIT); //or else depth buffer will be printed out
		//gl.glDisable(GL.GL_DEPTH_TEST);
		gl.glDisable(GL.GL_ALPHA_TEST);
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		float scaling = Configurator.getFloatSetting("GLOBAL_SCALING");
		// OpenGL.gl.glScalef(Configurator.getFloatSetting("GLOBAL_SCALING"),
		// Configurator.getFloatSetting("GLOBAL_SCALING"), 1f);
		Texture tmpTex = TextureIO.newTexture(GL.GL_TEXTURE_2D);
		//System.out.println(tmpTex.getWidth() + "," + tmpTex.getHeight());
		tmpTex.enable();
		tmpTex.bind();
		// !!!OpenGL coordinates!!!
		gl.glCopyTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, x, PApplet
				.round(height - y - h * scaling), PApplet.round(w * scaling),
				PApplet.round(h * scaling), 0);
		gl.glEnable(GL.GL_DEPTH_TEST);
		return tmpTex;
	}
	
	/**
	 * Cache snap shot.
	 *
	 * @param t the t
	 * @throws GLException the gL exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void cacheSnapShot(Texture t) throws GLException, IOException{ 
		TextureIO.write(t, new File("test.jpg"));
	}
	
	/**
	 * Creates the fbo.
	 *
	 * @return the int
	 */
	public static int createFBO(){
		 int[] array = new int[1];
		gl.glGenFramebuffersEXT(1, array, 0);
		//System.out.println("FBO handle:"+array[0]);
		return array[0];
	}
	
	/**
	 * Bind fbo.
	 *
	 * @param fbo the fbo
	 */
	public static void bindFBO(int fbo){
		gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, fbo);
	}
	
	/**
	 * Inits the fbo.
	 *
	 * @param w the w
	 * @param h the h
	 * @return the object[]
	 * @throws Exception the exception
	 */
	public static Object[] initFBO(int w, int h) throws Exception{
		//frist create a Frame Buffer Object
		int fbo = createFBO();
		gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, fbo);
		
		//Now create a texture
		//gl.glEnable(GL.GL_TEXTURE_2D);
		int[] array = new int[1];
		gl.glGenTextures(1,array,0);
		
		Texture t = TextureIO.newTexture(GL.GL_TEXTURE_2D);
		int tex = t.getTextureObject();
		//gl.glActiveTexture(GL.GL_TEXTURE0);
		enableTexture(0);
		//gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glBindTexture(GL.GL_TEXTURE_2D, tex);
		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, w, h, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, null);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR); //no mipmap
		/*
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR);
		gl.glGenerateMipmapEXT(GL.GL_TEXTURE_2D);
		*/
		//and attach it to the FBO
	    gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT, GL.GL_COLOR_ATTACHMENT0_EXT, GL.GL_TEXTURE_2D, tex, 0);
		
		//then create a render buffer
		
		gl.glGenRenderbuffersEXT(1, array,0);
		int rb = array[0];
		gl.glBindRenderbufferEXT(GL.GL_RENDERBUFFER_EXT, rb);
		//ask for a depth buffer and sets the size
		gl.glRenderbufferStorageEXT(GL.GL_RENDERBUFFER_EXT, GL.GL_DEPTH_COMPONENT, w, h);
		//attach it
		gl.glFramebufferRenderbufferEXT(GL.GL_FRAMEBUFFER_EXT, GL.GL_DEPTH_ATTACHMENT_EXT, GL.GL_RENDERBUFFER_EXT, rb);


		
	    
	    //check for completeness
	    switch(gl.glCheckFramebufferStatusEXT(GL.GL_FRAMEBUFFER_EXT)){
	    	case GL.GL_FRAMEBUFFER_COMPLETE_EXT:
	    		System.out.println("Init FBO: FBO complete!");
	    	break; 
	    	case GL.GL_FRAMEBUFFER_UNSUPPORTED_EXT: 
	    		throw new Exception("Init FBO: Unsupported; choose different formats."); 
	    	default:  
	    	throw new Exception("Init FBO: programming error; will fail on all hardware.");

	    }

	    OpenGL.gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, 0);
	    OpenGL.gl.glBindRenderbufferEXT(GL.GL_RENDERBUFFER_EXT, 0);
	    disableTexture(0);
	    return new Object[]{fbo,rb,t};
	}
	
	/**
	 * Clean fbo.
	 *
	 * @param fbo the fbo
	 */
	public static void cleanFBO(int[] fbo){
		IntBuffer iB = IntBuffer.wrap(fbo);
		
		OpenGL.gl.glDeleteFramebuffersEXT(1,iB);
		//System.out.println(iB.remaining());
		OpenGL.gl.glDeleteRenderbuffersEXT(1,iB);
	}
	
	/**
	 * Activate fbo.
	 *
	 * @param fbo the fbo
	 * @param w the w
	 * @param h the h
	 */
	public static void activateFBO(int fbo, int w, int h){
		OpenGL.gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
		OpenGL.gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT,fbo);
		OpenGL.gl.glPushAttrib(GL.GL_VIEWPORT_BIT); //save the current viewport info
		OpenGL.gl.glViewport(0,0,w, h); //make sure we draw in a big enough area
	}
	
	/**
	 * Deactivate fbo.
	 */
	public static void deactivateFBO(){
		OpenGL.gl.glPopAttrib();
		OpenGL.gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, 0);
	}
	
	
	
	/**
	 * Output error.
	 */
	public static void outputError() {
		if(gl == null)
			return;
	    int c;
	    if ((c = gl.glGetError()) != GL.GL_NO_ERROR)
	        System.out.println("OpenGl error: " + glu.gluErrorString(c));
	}

}
