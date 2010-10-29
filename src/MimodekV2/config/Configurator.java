package MimodekV2.config;

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

import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;

import MimodekV2.debug.Verbose;
import MimodekV2.graphics.OpenGL;

import processing.core.PApplet;
import processing.xml.XMLElement;

/**
 * This class stores all the parameters that affect Mimodek. It implement a
 * singleton so those parameters can be accessed in a static way by any class.
 * To be available the singleton must first be created by
 * Configurator.createConfigurator(parentSketch). To set a parameter call
 * Configurator.setSetting("nameOfTheParameter",theValueOfTheParameter). The
 * value can be an integer, a long, a float, a string or a boolean. To get a
 * setting call the getter for the desired type of the value with the name of
 * the parameter :
 * <table>
 * <tr>
 * <th>Type of the value</th>
 * <th>Getter method</th>
 * </tr>
 * <tr>
 * <td>Integer</td>
 * <td>Configurator.getIntegerSetting</td>
 * </tr>
 * <tr>
 * <td>Long</td>
 * <td>Configurator.getLongSetting</td>
 * </tr>
 * <tr>
 * <td>Float</td>
 * <td>Configurator.getFloatSetting</td>
 * </tr>
 * <tr>
 * <td>Boolean</td>
 * <td>Configurator.getBooleanSetting</td>
 * </tr>
 * <tr>
 * <td>String</td>
 * <td>Configurator.getStringSetting</td>
 * </tr>
 * </table>
 * This means that you have to know the name of the parameter and what kind of
 * value it contains. The parameters can be saved/loaded to/from a XML file by
 * calling respectively Configurator.saveToFile("fileName.xml") and
 * Configurator.loadFromFile("fileName.xml").
 * 
 * The default setting and their description:
 * <ul>
 * <li>Todo : make the list.</li>
 * </ul>
 * 
 * @author Jonsku
 */
public class Configurator {

	protected Hashtable<String, Object> settings;
	protected Hashtable<String, String> settingsDescription;
	protected static Configurator config;
	protected PApplet app;

	public static void createConfigurator(PApplet app) {
		config = new Configurator(app);
		// register some default settings
		/*
		 * If you add new setting, please provide a description of what it do and the expected range.
		 */
		Configurator.setSetting("DEBUG",true,"Toggle debug message.");
		
		/*
		 * Scaling settings
		 */
		Configurator.setSetting("HIGH_RESOLUTION_SCALE",4.5f,"Define the scaling factor for high resolution rendering.");
		Configurator.setSetting("GLOBAL_SCALING",1f,"Useful to magnify/shrink the image, leave to 1 when Mimodek is on display");
		
		
		
		/*
		 * Textures settings.
		 */
		Configurator.setSetting("GL_TEXTURE_MIN_FILTER", OpenGL.TextureFilters.GL_NEAREST_MIPMAP_NEAREST.ordinal(), "Define the texture filtering. Might or might not do anything depending on the hardware");
		Configurator.setSetting("CELLA_MASK","hardcell_mask","The name of the image used as a mask for the hard cells.");
		Configurator.setSetting("CELLA_TEXTURE","hardcell","The name of the image used as texture for the hard cells.");
		Configurator.setSetting("CELLB_MASK","softcell_mask","The name of the image used as a mask for the soft cells.");
		Configurator.setSetting("CELLB_TEXTURE","softcell","The name of the image used as texture for the soft cells.");
		
		/*
		 * Hard cell settings
		 */
		Configurator.setSetting("CELLA_MAX_TRY",50,"How many times the organism should try to find space for a new hardcell. High values might stop the animation alltogether");
		Configurator.setSetting("CELLA_DISTORTION",0.5f,"Range between 0f and 1f. Controls the distortion of the circle that makes the shape of the hard cells.");
		Configurator.setSetting("CELLA_DISTANCE_BETWEEN",0.5f,"Range between 0f and 1f. Controls of far appart the hard cells should be of each others. 0f means that they all share the same center, 1f and the distance between them is the sum of their radius.");
		Configurator.setSetting("CELLA_RADIUS",10f,"The maximum radius of hard cells, that is to say the radiu of the seed.");
		Configurator.setSetting("CELLA_R",0f,"Range between 0f and 1f. The red component of the hard cells color.");
		Configurator.setSetting("CELLA_G",1f,"Range between 0f and 1f. The green component of the hard cells color.");
		Configurator.setSetting("CELLA_B",0f,"Range between 0f and 1f. The blue component of the hard cells color.");
		Configurator.setSetting("CELLA_ALPHA",1f,"Range between 0f and 1f. The alpha component of the hard cells color.");
		
		/*
		 * Soft cell settings
		 */
		Configurator.setSetting("CELLB_RADIUS",10f,"The maximum radius of the soft cells.");
		Configurator.setSetting("CELLB_R",0f,"Range between 0f and 1f. The red component of the soft cells color.");
		Configurator.setSetting("CELLB_G",0f,"Range between 0f and 1f. The green component of the soft cells color.");
		Configurator.setSetting("CELLB_B",1f,"Range between 0f and 1f. The blue component of the soft cells color.");
		Configurator.setSetting("CELLB_ALPHA",1f,"Range between 0f and 1f. The alpha component of the soft cells color.");
		Configurator.setSetting("CELLB_ALPHA_VARIATION",0.25f,"Range between 0f and 1f. The amount the soft cells alpha value is allowed to vary. 1f : from CELLB_ALPHA to 0, 0f: disabled, values in between: CELLB_ALPHA to CELLB_ALPHA*CELLB_ALPHA_VARIATION.");
		Configurator.setSetting("CELLB_MIN_DISTANCE_TO_A",5f,"Defines the minimum distance between a softcell and a hard cell when the softcell is created.");
		Configurator.setSetting("CELLB_MAX_DISTANCE_TO_A",10f,"Defines the maximum distance between a softcell and a hard cell when the softcell is created.");
		
		/*
		 * Creature settings
		 */
		Configurator.setSetting("CREATURE_SIZE",20f,"The diameter of a creature.");
		Configurator.setSetting("CREATURE_DISTANCE_BETWEEN",20f,"The distance creatures try to keep between each others.");
		Configurator.setSetting("CREATURE_R",1f,"Range between 0f and 1f. The red component of the creatures color.");
		Configurator.setSetting("CREATURE_G",1f,"Range between 0f and 1f. The green component of the creatures color.");
		Configurator.setSetting("CREATURE_B",1f,"Range between 0f and 1f. The blue component of the creatures color.");
		Configurator.setSetting("CREATURE_ALPHA",1f,"Range between 0f and 1f. The alpha component of the creature color.");
		Configurator.setSetting("CREATURE_ALPHA_VARIATION",0.25f,"Range between 0f and 1f. The amount the soft cells alpha value is allowed to vary. 1f : from CREATURE_ALPHA to 0, 0f: disabled, values in between: CREATURE_ALPHA to CREATURE_ALPHA*CREATURE_ALPHA_VARIATION.");
		Configurator.setSetting("CREATURE_FULL_R",0f,"Range between 0f and 1f. The red component of the creatures color when it is bringing food to the organism.");
		Configurator.setSetting("CREATURE_FULL_G",0f,"Range between 0f and 1f. The green component of the creatures color when it is bringing food to the organism.");
		Configurator.setSetting("CREATURE_FULL_B",1f,"Range between 0f and 1f. The blue component of the creatures color when it is bringing food to the organism.");
		Configurator.setSetting("CREATURE_MAXSPEED",1f,"Maximum speed of a creature.");
		Configurator.setSetting("CREATURE_STEER_FORCE",0.05f,"Controls how sharp the creatures turn.");
		Configurator.setSetting("CREATURE_DIM_THRESHOLD",5,"When the number of creatures reach this threshold, their alpha is divided by 2.");
		
		/*
		 * Food settings
		 */
		Configurator.setSetting("FOOD_SIZE",1f,"Size of a piece of food.");
		Configurator.setSetting("FOOD_R",1f,"Range between 0f and 1f. The red component of the food color.");
		Configurator.setSetting("FOOD_G",1f,"Range between 0f and 1f. The green component of the food color.");
		Configurator.setSetting("FOOD_B",1f,"Range between 0f and 1f. The blue component of the food color.");
		Configurator.setSetting("FOOD_SCENT_EVAPORATION",0.1f,"How fast the creatures pheromones evaporate.");
		
		/*
		 * Data controls
		 */
		Configurator.setSetting("DATA_REFRESH_RATE",0.5f,"Frequency of data query in minutes.");
		Configurator.setSetting("DATA_TEMPERATURE",30f,"The current temperature. This is set at runtime, but you can specify a default value.");
		Configurator.setSetting("DATA_HUMIDITY",50f,"The current relative humidity. This is set at runtime, but you can specify a default value.");
		Configurator.setSetting("DATA_POLLUTION",0f,"The current air quality score. This is set at runtime, but you can specify a default value.");
		
		
		/*
		 * Message board control.
		 */
		Configurator.setSetting("MESSAGE_BOARD_FREQUENCY",0f,"How often should the board be shown in minutes. O disables message board.");
		Configurator.setSetting("MESSAGE_BOARD_DURATION",0.25f,"How long should the board be shown. In minutes.");
		Configurator.setSetting("MESSAGE_BOARD_FADE_SPEED",0.01f,"How fast the board fades in / out.");
		Configurator.setSetting("MESSAGE_BOARD_TEXTURE","facade text","The name of the image to use as a board");
		//For more than on images, the images have to be called  MESSAGE_BOARD_TEXTURE1,MESSAGE_BOARD_TEXTURE2,MESSAGE_BOARD_TEXTURE3 etc...
		Configurator.setSetting("MESSAGE_BOARD_NUMBER",1,"How many images to show.");
		
		
		/*
		 * Posting screen shots controls
		 */
		Configurator.setSetting("POST_PICTURES_FLAG",false,"If true, a screen shot of Mimodek will be posted to the blog every UPLOAD_RATE minutes.");
		Configurator.setSetting("SFTP_HOST","servidor.medialab-prado.es","Host name of the ftp server.");
		Configurator.setSetting("GALLERY_FOLDER","/home/mimodek/public_html/blog/test", "Path to the folder on SFTP_HOST where screen shots will be delivered.");
		Configurator.setSetting("WORDPRESS_URL","http://mimodek.medialab-prado.es/", "URL of the blog.");
		Configurator.setSetting("UPLOAD_FORMAT","jpg","Extension of the image format used for automatic posting. WARNING: Because Mimodek is currently using Exif metadata, only jpg is allowed, any other value is untested and will most certainly cause problems.");
		Configurator.setSetting("UPLOAD_RATE",0.5f,"Frequency of screen shot post in minutes.");
		Configurator.setSetting("CLEAR_CACHE_FLAG",true,"If true, the screen shot cache will be cleared after every post. This must be false only when debugging.");
		
		/*
		 * Location settings
		 */
		Configurator.setSetting("LOCATION_CITY","Madrid","City where Mimodek is running.");
		Configurator.setSetting("LOCATION_COUNTRY","Spain","Country where Mimodek is running.");
		/*
		 * NOTE: Coordinates have priority on City and Country when searching for a weather station so don't set them unless you are absolutely sure they are right or you might get weather data for Tombouctou.
		 * Latitude: 16.73222222
		 * Longitude: -3.00527777 
		 * You have been warned!!!
		 */
		//Configurator.setSetting("LOCATION_LONGITUDE",Float.MAX_VALUE,"Longitude of the location.");
		//Configurator.setSetting("LOCATION_LATITUDE",Float.MAX_VALUE,"Latitude of the location.");
	}

	protected Configurator(PApplet app) {
		this.app = app;
		settings = new Hashtable<String, Object>();
		settingsDescription = new Hashtable<String, String>();
	}

	public static void setSetting(String name, Object value) {
		config.setSettng(name, value);
	}
	
	public static void setSetting(String name, Object value, String description) {
		config.setSettng(name, value, description);
	}
	
	public static void setSettingIfNotSet(String name, Object value) {
		if (!config.settings.containsKey(name)) {
			config.setSettng(name, value);
		}
	}

	protected void setSettng(String name, Object value) {
		settings.put(name, value);
	}
	
	protected void setSettng(String name, Object value, String description) {
		settings.put(name, value);
		settingsDescription.put(name, description);
	}
	
	public static boolean isSettingSet(String name) {
		return config.isSettngSet(name);
	}
	
	protected boolean isSettngSet(String name) {
		return settings.containsKey(name);
	}

	public static int getIntegerSetting(String name) {
		return config.getIntegerSettng(name);
	}
	


	protected int getIntegerSettng(String name) {
		if (settings.containsKey(name))
			return (Integer) settings.get(name);
		return 0;
	}

	public static long getLongSetting(String name) {
		return config.getLongSettng(name);
	}

	protected long getLongSettng(String name) {
		if (settings.containsKey(name))
			return (Long) settings.get(name);
		return 0;
	}

	public static float getFloatSetting(String name) {
		return config.getFloatSettng(name);
	}

	protected float getFloatSettng(String name) {
		if (settings.containsKey(name))
			return (Float) settings.get(name);
		return 0;
	}

	public static boolean getBooleanSetting(String name) {
		return config.getBooleanSettng(name);
	}

	protected boolean getBooleanSettng(String name) {
		if (settings.containsKey(name))
			return (Boolean) settings.get(name);
		return false;
	}

	public static String getStringSetting(String name) {
		return config.getStringSettng(name);
	}

	protected String getStringSettng(String name) {
		if (settings.containsKey(name))
			return settings.get(name).toString();
		return "";
	}

	public static void saveToFile(String fileName) {
		config.saveToFil(fileName);
	}

	protected void saveToFil(String fileName) {
		PrintWriter output = app.createWriter(fileName);
		output.println("<?xml version=\"1.0\"?>");
		output.println("<mimodeksettings>");
		Enumeration<String> e = settings.keys();
		while (e.hasMoreElements()) {
			String name = e.nextElement();
			String description = "";
			if(settingsDescription.containsKey(name))
				description = settingsDescription.get(name);
			output.println("<setting name=\"" + name + "\" description=\"" + description + "\">"
					+ settings.get(name) + "</setting>");
		}
		output.println("</mimodeksettings>");
		output.flush(); // Writes the remaining data to the file
		output.close();
	}

	public static void loadFromFile(String fileName) {
		try{
			config.loadFromFil(fileName);
		}catch(Exception e){
			if(Verbose.speak){
				e.printStackTrace();
			}
			System.out.println("Error reading settings file, using default parameters.");
		}
	}

	protected void loadFromFil(String fileName) throws Exception {
		XMLElement xml;
		xml = new XMLElement(app, fileName);
		int numSettings = xml.getChildCount();
		for (int i = 0; i < numSettings; i++) {
			XMLElement kid = xml.getChild(i);
			String name = kid.getStringAttribute("name");
			String description = null;
			if(kid.hasAttribute("description"))
				description = kid.getStringAttribute("description");
			String value = kid.getContent();
			// try to parse to find the correct type
			// NOTE : the order of the calls to the method is important
			if (isInteger(value)) {
				setSetting(name, Integer.parseInt(value));
			} else if (isLong(value)) {
				setSetting(name, Long.parseLong(value));
			} else if (isFloat(value)) {
				setSetting(name, Float.parseFloat(value));
			} else if (isBoolean(value)
					&& (value.equalsIgnoreCase("true") || value
							.equalsIgnoreCase("false"))) {
				setSetting(name, Boolean.parseBoolean(value));
			} else {
				// it is a String after all
				setSetting(name, value);
			}
			if(description!=null)
				settingsDescription.put(name, description);
		}

	}

	protected boolean isFloat(String val) {
		try {
			Float.parseFloat(val);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	protected boolean isInteger(String val) {
		try {
			Integer.parseInt(val);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	protected boolean isBoolean(String val) {
		try {
			Boolean.parseBoolean(val);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	protected boolean isLong(String val) {
		try {
			Long.parseLong(val);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

}
