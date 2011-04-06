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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.ImageWriteException;
import org.apache.sanselan.formats.jpeg.exifRewrite.ExifRewriter;
import org.apache.sanselan.formats.tiff.constants.ExifTagConstants;
import org.apache.sanselan.formats.tiff.constants.TiffFieldTypeConstants;
import org.apache.sanselan.formats.tiff.write.TiffOutputDirectory;
import org.apache.sanselan.formats.tiff.write.TiffOutputField;
import org.apache.sanselan.formats.tiff.write.TiffOutputSet;

import MimodekV2.MimodekLocation;
import MimodekV2.config.Configurator;
import MimodekV2.data.PollutionLevelsEnum;
import MimodekV2.debug.Verbose;

// TODO: Auto-generated Javadoc
/**
 * Utility class to save byte array from images to Jpeg and to embed EXIF metadata at the same time.
 * @author Jonsku
 *
 */
public class ExifMetadata {



	/**
	 * Create and add the EXIF meta data tags to the image data and save the jpeg
	 * image to disk.
	 *
	 * @param src the src
	 * @param dst the dst
	 * @param location the location
	 * @param timestamp the timestamp
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ImageReadException the image read exception
	 * @throws ImageWriteException the image write exception
	 */
	public static void createExifMetadataAndSave(byte[] src, File dst,
			MimodekLocation location, long timestamp) throws IOException,
			ImageReadException, ImageWriteException {
		OutputStream os = null;
		try {
			TiffOutputSet outputSet = new TiffOutputSet();
			
			// Create the content of the ImageDescription and UserComment tags
			String time = new SimpleDateFormat(Verbose.IMAGE_POST_FORMAT).format(new Date(timestamp));
			String description = location.city + ", " + location.country + ", "
					+ time+". ";
			description += "Temperature: "
					+ Configurator.getFloatSetting("DATA_TEMPERATURE") + "¡C, ";
			description += "Relative Humidity: "
					+ Configurator.getFloatSetting("DATA_HUMIDITY") + "% ";
			description += "Air quality: "
					+ PollutionLevelsEnum
							.getPollutionLevelForScore(Configurator
									.getFloatSetting("DATA_POLLUTION")) + ". ";

			// Create the image description tag
			TiffOutputField descriptionTag = new TiffOutputField(
					ExifTagConstants.EXIF_TAG_IMAGE_DESCRIPTION,
					TiffFieldTypeConstants.FIELD_TYPE_ASCII, description
							.length(), description.getBytes());
			TiffOutputDirectory exifDirectory = outputSet
					.getOrCreateExifDirectory();
			// make sure to remove old value if present (this method will
			// not fail if the tag does not exist).
			// NOTE: No need to do this since the metadata doesn't exist yet
			// exifDirectory.removeField(TiffConstants.EXIF_TAG_IMAGE_DESCRIPTION);

			// Add the tag
			exifDirectory.add(descriptionTag);

			// Create the user comment tag
			TiffOutputField commentTag = new TiffOutputField(
					ExifTagConstants.EXIF_TAG_USER_COMMENT,
					TiffFieldTypeConstants.FIELD_TYPE_ASCII, description
							.length(), description.getBytes());
			exifDirectory = outputSet.getOrCreateExifDirectory();

			// exifDirectory.removeField(TiffConstants.EXIF_TAG_USER_COMMENT);
			exifDirectory.add(commentTag);

			// add geo coordinates
			if(location.latitude != null && location.longitude != null){
				double longitude = location.longitude;
				double latitude = location.latitude;
				outputSet.setGPSInDegrees(longitude, latitude);
			}
			//Save to file
			os = new FileOutputStream(dst);
			os = new BufferedOutputStream(os);

			new ExifRewriter().updateExifMetadataLossless(src, os, outputSet);
			os.close();
			os = null;
		} finally {
			if (os != null)
				try {
					os.close();
				} catch (IOException e) {

				}
		}
	}
}
