package MimodekV2.imageexport;

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

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;

import com.sun.opengl.util.FileUtil;
import com.sun.opengl.util.ImageUtil;
import com.sun.opengl.util.texture.TextureData;
import com.sun.opengl.util.texture.TextureIO;
import com.sun.opengl.util.texture.spi.TextureWriter;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.ImageWriteException;

import MimodekV2.MimodekLocation;

// TODO: Auto-generated Javadoc
/**
 * The Class JpgWithExifTextureWriter.
 */
public class JpgWithExifTextureWriter implements TextureWriter {

	/*
	 * Store the last converted texture
	 */
	/** The buffered image. */
	private static BufferedImage bufferedImage;
	
	/*
	 * Store the time of conversion
	 */
	/** The latest snap time stamp. */
	public static long latestSnapTimeStamp = 0;

	/*
	 * Convert an OpenGL texture of pixel format GL.GL_RGB to a BufferedImage to
	 * be later saved as a Jpeg with Exif metadata
	 * 
	 * @see com.sun.opengl.util.texture.spi.TextureWriter#write(java.io.File,
	 * com.sun.opengl.util.texture.TextureData)
	 */
	/* (non-Javadoc)
	 * @see com.sun.opengl.util.texture.spi.TextureWriter#write(java.io.File, com.sun.opengl.util.texture.TextureData)
	 */
	public boolean write(File file, TextureData data) throws IOException {
		int pixelFormat = data.getPixelFormat();
	    int pixelType   = data.getPixelType();
	      
		
		if ((pixelFormat != GL.GL_RGB && pixelFormat != GL.GL_RGBA) || (pixelType != GL.GL_BYTE && pixelType != GL.GL_UNSIGNED_BYTE) || (!TextureIO.JPG.equals(FileUtil.getFileSuffix(file)) && !TextureIO.PNG.equals(FileUtil.getFileSuffix(file)))) // only
		// RGB/A
		// Bytes
		// and
		// JPG, PNG
		// files
		{
			return false;

		}
		//final TextureData tData = data;
		//TODO:
		//I tried to make this run concurrently to minimize the impact on the animation, but it needs more work....
		/*
		new Thread() {
			public void run() {
		 */
				// Convert TextureData to appropriate BufferedImage
				BufferedImage image = new BufferedImage(data.getWidth(), data
						.getHeight(), (pixelFormat == GL.GL_RGB) ?
                                BufferedImage.TYPE_3BYTE_BGR :
                                    BufferedImage.TYPE_4BYTE_ABGR);
				byte[] imageData = ((DataBufferByte) image.getRaster()
						.getDataBuffer()).getData();
				ByteBuffer buf = (ByteBuffer) data.getBuffer();
				if (buf == null) {
					buf = (ByteBuffer) data.getMipmapData()[0];
				}
				buf.rewind();
				buf.get(imageData);
				buf.rewind();

				// Swizzle image components to be correct
		        if (pixelFormat == GL.GL_RGB) {
		          for (int i = 0; i < imageData.length; i += 3) {
		            byte red  = imageData[i + 0];
		            byte blue = imageData[i + 2];
		            imageData[i + 0] = blue;
		            imageData[i + 2] = red;
		          }
		        } else {
		          for (int i = 0; i < imageData.length; i += 4) {
		            byte red   = imageData[i + 0];
		            byte green = imageData[i + 1];
		            byte blue  = imageData[i + 2];
		            byte alpha = imageData[i + 3];
		            imageData[i + 0] = alpha;
		            imageData[i + 1] = blue;
		            imageData[i + 2] = green;
		            imageData[i + 3] = red;
		          }
		        }


				// Flip image vertically for the user's convenience
				ImageUtil.flipImageVertically(image);
				
				 // Since we're exporting JPEGS we don't use the alpha channel
		        if (image.getType() == BufferedImage.TYPE_4BYTE_ABGR) {
		          BufferedImage tmpImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		          Graphics g = tmpImage.getGraphics();
		          g.drawImage(image, 0, 0, null);
		          g.dispose();
		          image = tmpImage;
		        }

		        /*
				BufferedImage tmpImage = new BufferedImage(image.getWidth(),
						image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
				Graphics g = tmpImage.getGraphics();
				g.drawImage(image, 0, 0, null);
				g.dispose();
				*/
				bufferedImage = image;
				latestSnapTimeStamp = System.currentTimeMillis();
		/*
		 }
		}.start();
		*/
		return true;
	}
	
	/*
	 * Write the current buffered image to disk as Jpg or Png
	 * file
	 */
	/**
	 * Write to disk.
	 *
	 * @param path the path
	 * @param fileNameWithoutExtension the file name without extension
	 * @param extension the extension
	 * @param location the location
	 * @param eraseBuffer the erase buffer
	 * @return true, if successful
	 * @throws ImageWriteException the image write exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ImageReadException the image read exception
	 */
	public boolean writeToDisk(String path, String fileNameWithoutExtension, String extension, MimodekLocation location, boolean eraseBuffer) throws ImageWriteException, IOException, ImageReadException{
		boolean result = false;
		if (bufferedImage == null)
			return false;
		if(extension.equalsIgnoreCase("jpg")){
			result =  writeJPGToDisk(path, fileNameWithoutExtension, location, latestSnapTimeStamp);
		}else if(extension.equalsIgnoreCase("png")){
			File file = new File(path+File.separatorChar+fileNameWithoutExtension+".png");
			result = ImageIO.write(bufferedImage, FileUtil.getFileSuffix(file), file);
		}
		if(eraseBuffer){
			// so we don't rewrite this texture by mistake
			bufferedImage = null;
		}
		return result;
	}

	/*
	 * Add EXIF meta-data to the last converted texture and write it to a Jpeg
	 * file
	 */
	/**
	 * Write jpg to disk.
	 *
	 * @param path the path
	 * @param fileNameWithoutExtension the file name without extension
	 * @param location the location
	 * @param snapDate the snap date
	 * @return true, if successful
	 * @throws ImageWriteException the image write exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ImageReadException the image read exception
	 */
	public boolean writeJPGToDisk(String path, String fileNameWithoutExtension, MimodekLocation location, long snapDate)
			throws ImageWriteException, IOException, ImageReadException {
		if (bufferedImage == null)
			return false;

		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();

		// write the image data in the byte array stream
		ImageIO.write(bufferedImage, "JPEG", byteOutputStream);

		// add metadata and write file
		ExifMetadata.createExifMetadataAndSave(byteOutputStream.toByteArray(),
				new File(path +File.separatorChar+ fileNameWithoutExtension + ".jpg"),
				location, snapDate);
		byteOutputStream.close();
		return true;
	}

}
