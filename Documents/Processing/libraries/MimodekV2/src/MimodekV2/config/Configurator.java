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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeSet;

import mimodek.facade.FacadeFactory;

import MimodekV2.debug.Verbose;
import MimodekV2.graphics.OpenGL;

import processing.core.PApplet;
import processing.xml.XMLElement;

// TODO: Auto-generated Javadoc
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

	/** The settings. */
	protected Hashtable<String, Object> settings;
	
	/** The settings description. */
	protected Hashtable<String, String> settingsDescription;
	
	/** The config. */
	protected static Configurator config;
	
	/** This counter is increased each time saveAs is called. */
	private static int savedConfig_counter = 0;
	
	/** The app. */
	protected PApplet app;

	/**
	 * Creates the configurator.
	 *
	 * @param app the app
	 */
	public static void createConfigurator(PApplet app) {
		config = new Configurator(app);
		// register some default settings
		/*
		 * If you add new setting, please provide a description of what it do and the expected range.
		 */
		
		
		/*
		 * Scaling settings
		 */
		Configurator.setSetting("SCREEN_SHOT_SCALING_INT",5,"Define the scaling factor used when writting the frames to disk.");
		Configurator.setSetting("GLOBAL_SCALING",1f,"Useful to magnify/shrink the image, leave to 1 when Mimodek is on display or when fiming in PAL");
		
		/*
		 * Debug/filming settings
		 */
		Configurator.setSetting("DEBUG_FLAG",true,"Toggle debug message.");
		Configurator.setSetting("FAKE_DATA_FLAG",true,"Set to true to control the data value from the UI.");
		Configurator.setSetting("AUTO_START_FLAG",true,"Set to true to start running Mimodek right after the program has loaded.");
		//Configurator.setSetting("FILMING_FLAG",false,"Set to true to save each frames to file.");
		
		/*
		 * Setup the display type
		 */
		Configurator.setSetting("FACADE_TYPE_INT",FacadeFactory.PRADO_FACADE,"Set which type of display to use. (PRADO_FACADE)1: Medialab Prado facade, (FULL_WINDOW)2: The size of the Processing sketch, (MINIPAL)3: Half the size of the PAL format");
		
		
		/*
		 * Textures settings.
		 */
		Configurator.setSetting("GL_TEXTURE_MIN_FILTER_INT", OpenGL.TextureFilters.GL_NEAREST_MIPMAP_NEAREST.ordinal(), "Define the texture filtering. Might or might not do anything depending on the hardware");
		Configurator.setSetting("CELLA_MASK_STR","hardcell_mask","The name of the image used as a mask for the hard cells.");
		Configurator.setSetting("CELLA_TEXTURE_STR","hardcell","The name of the image used as texture for the hard cells.");
		Configurator.setSetting("CELLB_MASK_STR","softcell_mask","The name of the image used as a mask for the soft cells.");
		Configurator.setSetting("CELLB_TEXTURE_STR","softcell","The name of the image used as texture for the soft cells.");
		
		/*
		 * Hard cell settings
		 */
		Configurator.setSetting("CELLA_MAX_TRY_INT",50,"How many times the organism should try to find space for a new hardcell. High values might stop the animation alltogether");
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
		Configurator.setSetting("CREATURE_DIM_THRESHOLD_INT",5,"When the number of creatures reach this threshold, their alpha is divided by 2.");
		
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
		Configurator.setSetting("MESSAGE_BOARD_TEXTURE_STR","facade text","The name of the image to use as a board");
		//The images have to be called  MESSAGE_BOARD_TEXTURE_STR1 to MESSAGE_BOARD_TEXTURE_STRn where n = MESSAGE_BOARD_NUMBER_INT
		Configurator.setSetting("MESSAGE_BOARD_NUMBER_INT",1,"How many images to show.");
		
		
		/*
		 * Posting screen shots controls
		 */
		Configurator.setSetting("POST_PICTURES_FLAG",true,"If true, a screen shot of Mimodek will be posted to the blog every UPLOAD_RATE minutes.");
		Configurator.setSetting("SFTP_HOST_STR","servidor.medialab-prado.es","Host name of the ftp server.");
		Configurator.setSetting("GALLERY_FOLDER_STR","/home/mimodek/public_html/blog/test", "Path to the folder on SFTP_HOST where screen shots will be delivered.");
		Configurator.setSetting("WORDPRESS_URL_STR","http://mimodek.medialab-prado.es/", "URL of the blog.");
		Configurator.setSetting("UPLOAD_FORMAT_STR","jpg","Extension of the image format used for automatic posting. WARNING: Because Mimodek is currently using Exif metadata, only jpg is allowed, any other value is untested and will most certainly cause problems.");
		Configurator.setSetting("UPLOAD_RATE",0.5f,"Frequency of screen shot post in minutes.");
		Configurator.setSetting("CLEAR_CACHE_FLAG",true,"If true, the screen shot cache will be cleared after every post. This must be false only when debugging.");
		
		/*
		 * Location settings
		 */
		Configurator.setSetting("LOCATION_CITY_STR","Madrid","City where Mimodek is running.");
		Configurator.setSetting("LOCATION_COUNTRY_STR","Spain","Country where Mimodek is running.");
		/*
		 * NOTE: Coordinates have priority on City and Country when searching for a weather station so don't set them unless you are absolutely sure they are right or you might get weather data for Tombouctou.
		 * Latitude: 16.73222222
		 * Longitude: -3.00527777 
		 * You have been warned!!!
		 */
		//Configurator.setSetting("LOCATION_LONGITUDE",Float.MAX_VALUE,"Longitude of the location.");
		//Configurator.setSetting("LOCATION_LATITUDE",Float.MAX_VALUE,"Latitude of the location.");
	}

	/**
	 * Instantiates a new configurator.
	 *
	 * @param app the app
	 */
	protected Configurator(PApplet app) {
		this.app = app;
		settings = new Hashtable<String, Object>();
		settingsDescription = new Hashtable<String, String>();
	}

	/**
	 * Sets the setting.
	 *
	 * @param name the name
	 * @param value the value
	 */
	public static void setSetting(String name, Object value) {
		config.setSettng(name, value);
	}
	
	/**
	 * Sets the setting.
	 *
	 * @param name the name
	 * @param value the value
	 * @param description the description
	 */
	public static void setSetting(String name, Object value, String description) {
		config.setSettng(name, value, description);
	}
	
	/**
	 * Sets the setting if not set.
	 *
	 * @param name the name
	 * @param value the value
	 */
	public static void setSettingIfNotSet(String name, Object value) {
		if (!config.settings.containsKey(name)) {
			config.setSettng(name, value);
		}
	}

	/**
	 * Sets the settng.
	 *
	 * @param name the name
	 * @param value the value
	 */
	protected void setSettng(String name, Object value) {
		settings.put(name, value);
	}
	
	/**
	 * Sets the settng.
	 *
	 * @param name the name
	 * @param value the value
	 * @param description the description
	 */
	protected void setSettng(String name, Object value, String description) {
		settings.put(name, value);
		settingsDescription.put(name, description);
	}
	
	/**
	 * Checks if is setting set.
	 *
	 * @param name the name
	 * @return true, if is setting set
	 */
	public static boolean isSettingSet(String name) {
		return config.isSettngSet(name);
	}
	
	/**
	 * Checks if is settng set.
	 *
	 * @param name the name
	 * @return true, if is settng set
	 */
	protected boolean isSettngSet(String name) {
		return settings.containsKey(name);
	}

	/**
	 * Gets the integer setting.
	 *
	 * @param name the name
	 * @return the integer setting
	 */
	public static int getIntegerSetting(String name) {
		return config.getIntegerSettng(name);
	}
	


	/**
	 * Gets the integer settng.
	 *
	 * @param name the name
	 * @return the integer settng
	 */
	protected int getIntegerSettng(String name) {
		if (settings.containsKey(name))
			return (Integer) settings.get(name);
		return 0;
	}

	/**
	 * Gets the long setting.
	 *
	 * @param name the name
	 * @return the long setting
	 */
	public static long getLongSetting(String name) {
		return config.getLongSettng(name);
	}

	/**
	 * Gets the long settng.
	 *
	 * @param name the name
	 * @return the long settng
	 */
	protected long getLongSettng(String name) {
		if (settings.containsKey(name))
			return (Long) settings.get(name);
		return 0;
	}

	/**
	 * Gets the float setting.
	 *
	 * @param name the name
	 * @return the float setting
	 */
	public static float getFloatSetting(String name) {
		return config.getFloatSettng(name);
	}

	/**
	 * Gets the float settng.
	 *
	 * @param name the name
	 * @return the float settng
	 */
	protected float getFloatSettng(String name) {
		if (settings.containsKey(name))
			return (Float) settings.get(name);
		return 0;
	}

	/**
	 * Gets the boolean setting.
	 *
	 * @param name the name
	 * @return the boolean setting
	 */
	public static boolean getBooleanSetting(String name) {
		return config.getBooleanSettng(name);
	}

	/**
	 * Gets the boolean settng.
	 *
	 * @param name the name
	 * @return the boolean settng
	 */
	protected boolean getBooleanSettng(String name) {
		if (settings.containsKey(name))
			return (Boolean) settings.get(name);
		return false;
	}

	/**
	 * Gets the string setting.
	 *
	 * @param name the name
	 * @return the string setting
	 */
	public static String getStringSetting(String name) {
		return config.getStringSettng(name);
	}

	/**
	 * Gets the string settng.
	 *
	 * @param name the name
	 * @return the string settng
	 */
	protected String getStringSettng(String name) {
		if (settings.containsKey(name))
			return settings.get(name).toString();
		return "";
	}
	
	public static void save() {
		config.saveToFil("settings.xml");
	}

	/**
	 * Save to file.
	 *
	 * @param fileName the file name
	 */
	public static void saveToFile(String fileName) {
		config.saveToFil(fileName);
	}
	
	/**
	 * Save to file wihtout overwritting current settings.xml.
	 *
	 * @param fileName the file name
	 */
	public static void saveAs() {
		saveToFile("settings_saved_" + savedConfig_counter+ ".xml");		
		savedConfig_counter++;		
	}


	/**
	 * Save to fil.
	 *
	 * @param fileName the file name
	 */
	protected void saveToFil(String fileName) {
		PrintWriter output = app.createWriter(fileName);
		output.println("<?xml version=\"1.0\"?>");
		output.println("<mimodeksettings>");
		//this should output the settings in an alphabetical order
		TreeSet<String> sortedSet = new TreeSet<String>(settings.keySet());
		Iterator<String> e = sortedSet.iterator();// settings.keys();
		while (e.hasNext()) {
			String name = e.next();
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
	
	public static void reload() {
		loadFromFile("settings.xml");
	}

	/**
	 * Load from a file.
	 *
	 * @param fileName the file name or the absolute path to the file if it is not in the sketch folder.
	 */
	public static void loadFromFile(String fileName) {
		try{
			config.loadFromFil(fileName);
		}catch(NullPointerException e){
			Verbose.debug("No settings file found, will use default parameters.");
			config = new Configurator(config.app);
		}catch(Exception e){
			if(Verbose.speak){
				e.printStackTrace();
			}
			System.out.println("Error reading settings file, using default parameters.");
			config = new Configurator(config.app);
		}
	}

	/**
	 * Load from fil.
	 *
	 * @param fileName the file name
	 * @throws Exception the exception
	 */
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

	/**
	 * Checks if is float.
	 *
	 * @param val the val
	 * @return true, if is float
	 */
	protected boolean isFloat(String val) {
		try {
			Float.parseFloat(val);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * Checks if is integer.
	 *
	 * @param val the val
	 * @return true, if is integer
	 */
	protected boolean isInteger(String val) {
		try {
			Integer.parseInt(val);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * Checks if is boolean.
	 *
	 * @param val the val
	 * @return true, if is boolean
	 */
	protected boolean isBoolean(String val) {
		try {
			Boolean.parseBoolean(val);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * Checks if is long.
	 *
	 * @param val the val
	 * @return true, if is long
	 */
	protected boolean isLong(String val) {
		try {
			Long.parseLong(val);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}


}
