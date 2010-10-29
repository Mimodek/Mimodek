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
import org.apache.sanselan.formats.tiff.constants.TiffConstants;
import org.apache.sanselan.formats.tiff.constants.TiffFieldTypeConstants;
import org.apache.sanselan.formats.tiff.write.TiffOutputDirectory;
import org.apache.sanselan.formats.tiff.write.TiffOutputField;
import org.apache.sanselan.formats.tiff.write.TiffOutputSet;

import MimodekV2.MimodekLocation;
import MimodekV2.config.Configurator;
import MimodekV2.data.PollutionLevelsEnum;
import MimodekV2.debug.Verbose;

public class ExifMetadata {



	/**
	 * Create and add the EXIF meta data tags to the image data and save the jpeg
	 * image to disk
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
					TiffConstants.EXIF_TAG_IMAGE_DESCRIPTION,
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
					TiffConstants.EXIF_TAG_USER_COMMENT,
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
	
	/*
	 * public void removeExifMetadata(File jpegImageFile, File dst) throws
	 * IOException, ImageReadException, ImageWriteException { OutputStream os =
	 * null; try { os = new FileOutputStream(dst); os = new
	 * BufferedOutputStream(os);
	 * 
	 * new ExifRewriter().removeExifMetadata(jpegImageFile, os); } finally { if
	 * (os != null) try { os.close(); } catch (IOException e) {
	 * 
	 * } } }
	 */

	/**
	 * This example illustrates how to add/update EXIF metadata in a JPEG file.
	 * 
	 * @param jpegImageFile
	 *            A source image file.
	 * @param dst
	 *            The output file.
	 * @throws IOException
	 * @throws ImageReadException
	 * @throws ImageWriteException
	 */
	/*
	public static void changeExifMetadata(File jpegImageFile, File dst,
			MimodekLocation location) throws IOException, ImageReadException,
			ImageWriteException {
		OutputStream os = null;
		try {
			TiffOutputSet outputSet = null;

			// note that metadata might be null if no metadata is found.
			IImageMetadata metadata = Sanselan.getMetadata(jpegImageFile);
			JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
			if (null != jpegMetadata) {
				// note that exif might be null if no Exif metadata is found.
				TiffImageMetadata exif = jpegMetadata.getExif();

				if (null != exif) {
					// TiffImageMetadata class is immutable (read-only).
					// TiffOutputSet class represents the Exif data to write.
					//
					// Usually, we want to update existing Exif metadata by
					// changing
					// the values of a few fields, or adding a field.
					// In these cases, it is easiest to use getOutputSet() to
					// start with a "copy" of the fields read from the image.
					outputSet = exif.getOutputSet();
				}
			}

			// if file does not contain any exif metadata, we create an empty
			// set of exif metadata. Otherwise, we keep all of the other
			// existing tags.
			if (null == outputSet)
				outputSet = new TiffOutputSet();

			{
				// Example of how to add a field/tag to the output set.
				//
				// Note that you should first remove the field/tag if it already
				// exists in this directory, or you may end up with duplicate
				// tags. See above.
				//
				// Certain fields/tags are expected in certain Exif directories;
				// Others can occur in more than one directory (and often have a
				// different meaning in different directories).
				//
				// TagInfo constants often contain a description of what
				// directories are associated with a given tag.
				//
				// see
				// org.apache.sanselan.formats.tiff.constants.AllTagConstants
				//
				String description = location.city + "," + location.country
						+ ", " + new Date();
				description += "Temperature: "
						+ Configurator.getFloatSetting("DATA_TEMPERATURE")
						+ "¡C, ";
				description += "Relative Humidity: "
						+ Configurator.getFloatSetting("DATA_HUMIDITY") + "% ";
				description += "Air quality: "
						+ PollutionLevelsEnum
								.getPollutionLevelForScore(Configurator
										.getFloatSetting("DATA_POLLUTION"))
						+ ". ";

				TiffOutputField descriptionTag = new TiffOutputField(
						TiffConstants.EXIF_TAG_IMAGE_DESCRIPTION,
						TiffFieldTypeConstants.FIELD_TYPE_ASCII, description
								.length(), description.getBytes());
				// TiffOutputField descriptionTag =
				// TiffOutputField.create(IMAGE_DESCRIPTION_TAG,outputSet.byteOrder,
				// description);
				TiffOutputDirectory exifDirectory = outputSet
						.getOrCreateExifDirectory();
				// make sure to remove old value if present (this method will
				// not fail if the tag does not exist).
				exifDirectory
						.removeField(TiffConstants.EXIF_TAG_IMAGE_DESCRIPTION);
				exifDirectory.add(descriptionTag);

				// TiffOutputField dcommentTag =
				// TiffOutputField.create(USER_COMMENT_TAG,outputSet.byteOrder,
				// description);
				TiffOutputField commentTag = new TiffOutputField(
						TiffConstants.EXIF_TAG_USER_COMMENT,
						TiffFieldTypeConstants.FIELD_TYPE_ASCII, description
								.length(), description.getBytes());
				exifDirectory = outputSet.getOrCreateExifDirectory();
				// make sure to remove old value if present (this method will
				// not fail if the tag does not exist).
				exifDirectory.removeField(TiffConstants.EXIF_TAG_USER_COMMENT);
				exifDirectory.add(commentTag);
			}

			{
				double longitude = location.longitude;
				double latitude = location.latitude;

				outputSet.setGPSInDegrees(longitude, latitude);
			}

			// outputSet.

			// printTagValue(jpegMetadata, TiffConstants.TIFF_TAG_DATE_TIME);

			os = new FileOutputStream(dst);
			os = new BufferedOutputStream(os);

			new ExifRewriter().updateExifMetadataLossless(jpegImageFile, os,
					outputSet);

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
	*/

	/**
	 * This example illustrates how to remove a tag (if present) from EXIF
	 * metadata in a JPEG file.
	 * 
	 * In this case, we remove the "aperture" tag from the EXIF metadata if
	 * present.
	 * 
	 * @param jpegImageFile
	 *            A source image file.
	 * @param dst
	 *            The output file.
	 * @throws IOException
	 * @throws ImageReadException
	 * @throws ImageWriteException
	 */
	/*
	 * public void removeExifTag(File jpegImageFile, File dst) throws
	 * IOException, ImageReadException, ImageWriteException { OutputStream os =
	 * null; try { TiffOutputSet outputSet = null;
	 * 
	 * // note that metadata might be null if no metadata is found.
	 * IImageMetadata metadata = Sanselan.getMetadata(jpegImageFile);
	 * JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata; if (null
	 * != jpegMetadata) { // note that exif might be null if no Exif metadata is
	 * found. TiffImageMetadata exif = jpegMetadata.getExif();
	 * 
	 * if (null != exif) { // TiffImageMetadata class is immutable (read-only).
	 * // TiffOutputSet class represents the Exif data to write. // // Usually,
	 * we want to update existing Exif metadata by // changing // the values of
	 * a few fields, or adding a field. // In these cases, it is easiest to use
	 * getOutputSet() to // start with a "copy" of the fields read from the
	 * image. outputSet = exif.getOutputSet(); } }
	 * 
	 * if (null == outputSet) { // file does not contain any exif metadata. We
	 * don't need to // update the file; just copy it.
	 * IOUtils.copyFileNio(jpegImageFile, dst); return; }
	 * 
	 * { // Example of how to remove a single tag/field. // There are two ways
	 * to do this.
	 * 
	 * // Option 1: brute force // Note that this approach is crude: Exif data
	 * is organized in // directories. The same tag/field may appear in more
	 * than one // directory, and have different meanings in each.
	 * outputSet.removeField(TiffConstants.EXIF_TAG_APERTURE_VALUE);
	 * 
	 * // Option 2: precision // We know the exact directory the tag should
	 * appear in, in this // case the "exif" directory. // One complicating
	 * factor is that in some cases, manufacturers // will place the same tag in
	 * different directories. // To learn which directory a tag appears in,
	 * either refer to // the constants in ExifTagConstants.java or go to Phil
	 * Harvey's // EXIF website. TiffOutputDirectory exifDirectory = outputSet
	 * .getExifDirectory(); if (null != exifDirectory) exifDirectory
	 * .removeField(TiffConstants.EXIF_TAG_APERTURE_VALUE); }
	 * 
	 * os = new FileOutputStream(dst); os = new BufferedOutputStream(os);
	 * 
	 * new ExifRewriter().updateExifMetadataLossless(jpegImageFile, os,
	 * outputSet);
	 * 
	 * os.close(); os = null; } finally { if (os != null) try { os.close(); }
	 * catch (IOException e) {
	 * 
	 * } } }
	 */

	/**
	 * This example illustrates how to set the GPS values in JPEG EXIF metadata.
	 * 
	 * @param jpegImageFile
	 *            A source image file.
	 * @param dst
	 *            The output file.
	 * @throws IOException
	 * @throws ImageReadException
	 * @throws ImageWriteException
	 */
	/*
	 * public void setExifGPSTag(File jpegImageFile, File dst) throws
	 * IOException, ImageReadException, ImageWriteException { OutputStream os =
	 * null; try { TiffOutputSet outputSet = null;
	 * 
	 * // note that metadata might be null if no metadata is found.
	 * IImageMetadata metadata = Sanselan.getMetadata(jpegImageFile);
	 * JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata; if (null
	 * != jpegMetadata) { // note that exif might be null if no Exif metadata is
	 * found. TiffImageMetadata exif = jpegMetadata.getExif();
	 * 
	 * if (null != exif) { // TiffImageMetadata class is immutable (read-only).
	 * // TiffOutputSet class represents the Exif data to write. // // Usually,
	 * we want to update existing Exif metadata by // changing // the values of
	 * a few fields, or adding a field. // In these cases, it is easiest to use
	 * getOutputSet() to // start with a "copy" of the fields read from the
	 * image. outputSet = exif.getOutputSet(); } }
	 * 
	 * // if file does not contain any exif metadata, we create an empty // set
	 * of exif metadata. Otherwise, we keep all of the other // existing tags.
	 * if (null == outputSet) outputSet = new TiffOutputSet();
	 * 
	 * { // Example of how to add/update GPS info to output set.
	 * 
	 * // New York City double longitude = -74.0; // 74 degrees W (in Degrees
	 * East) double latitude = 40 + 43 / 60.0; // 40 degrees N (in Degrees //
	 * North)
	 * 
	 * outputSet.setGPSInDegrees(longitude, latitude); }
	 * 
	 * os = new FileOutputStream(dst); os = new BufferedOutputStream(os);
	 * 
	 * new ExifRewriter().updateExifMetadataLossless(jpegImageFile, os,
	 * outputSet);
	 * 
	 * os.close(); os = null; } finally { if (os != null) try { os.close(); }
	 * catch (IOException e) {
	 * 
	 * } } }
	 */
}
