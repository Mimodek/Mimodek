package mimodek.configuration;

import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;

import processing.core.PApplet;
import processing.xml.XMLElement;

/**
 * This class stores all the parameters that affect Mimodek.
 * It implement a singleton so those parameters can be accessed in a static way by any class. To be available the singleton must first be created by Configurator.createConfigurator(parentSketch).
 * To set a parameter call Configurator.setSetting("nameOfTheParameter",theValueOfTheParameter). The value can be an integer, a long, a float, a string or a boolean.
 * To get a setting call the getter for the desired type of the value with the name of the parameter : 
 * <table>
 * <tr><th>Type of the value</th><th>Getter method</th></tr>
 * <tr><td>Integer</td><td>Configurator.getIntegerSetting</td></tr>
 * <tr><td>Long</td><td>Configurator.getLongSetting</td></tr>
 * <tr><td>Float</td><td>Configurator.getFloatSetting</td></tr>
 * <tr><td>Boolean</td><td>Configurator.getBooleanSetting</td></tr>
 * <tr><td>String</td><td>Configurator.getStringSetting</td></tr>
 * </table>
 * This means that you have to know the name of the parameter and what kind of value it contains.
 * The parameters can be saved/loaded to/from a XML file by calling respectively Configurator.saveToFile("fileName.xml") and Configurator.loadFromFile("fileName.xml")
 * @author Jonsku
 */
public class Configurator {

	protected Hashtable<String, Object> settings;
	protected static Configurator config;
	protected PApplet app;
	
	public static void createConfigurator(PApplet app){
		config = new Configurator(app);
		// register some default settings
		Configurator.setSetting("maxSpeed", 10f);
		Configurator.setSetting("edgeDetection", 35f);
		Configurator.setSetting("blobDistanceThreshold", 10f);
		Configurator.setSetting("trackingOn", true);
		Configurator.setSetting("mimosMinRadius", 20f);
		Configurator.setSetting("mimosMaxRadius", 70f);
		Configurator.setSetting("mimosMinLifeTime", 3);//3 seconds
		Configurator.setSetting("mimosMaxLifeTime", 90000);//90 seconds
		Configurator.setSetting("mimosColor", 12.0f);
		Configurator.setSetting("ancestorBrightness", 144);
		Configurator.setSetting("ancestorTexture", 0);
		Configurator.setSetting("activeTexture", 1);
	}
	
	protected Configurator(PApplet app){
		this.app = app;
		settings = new Hashtable<String, Object>();
	}
	
	public static void setSetting(String name, Object value){
		config.setSettng(name, value);
	}
	
	protected void setSettng(String name, Object value){
			settings.put(name, value);
	}
	
	public static int getIntegerSetting(String name){
		return config.getIntegerSettng(name);
	}
	
	protected int getIntegerSettng(String name){
		if(settings.containsKey(name))
			return (Integer)settings.get(name);
		return 0;
	}
	
	public static long getLongSetting(String name){
		return config.getLongSettng(name);
	}
	
	protected long getLongSettng(String name){
		if(settings.containsKey(name))
			return (Long)settings.get(name);
		return 0;
	}
	
	public static float getFloatSetting(String name){
		return config.getFloatSettng(name);
	}
	
	protected float getFloatSettng(String name){
		if(settings.containsKey(name))
			return (Float)settings.get(name);
		return 0;
	}
	
	public static boolean getBooleanSetting(String name){
		return config.getBooleanSettng(name);
	}
	
	protected boolean getBooleanSettng(String name){
		if(settings.containsKey(name))
			return (Boolean)settings.get(name);
		return false;
	}
	
	public static String getStringSetting(String name){
		return config.getStringSettng(name);
	}
	
	protected String getStringSettng(String name){
		if(settings.containsKey(name))
			return (String)settings.get(name);
		return "";
	}
	
	public static void saveToFile(String fileName){
		config.saveToFil(fileName);
	}
	
	protected void saveToFil(String fileName){
		PrintWriter output = app.createWriter("data/config/"+fileName); 
		output.println("<?xml version=\"1.0\"?>");
		output.println("<mimodeksettings>");
		Enumeration<String> e = settings.keys();
		while(e.hasMoreElements()){
			String name = e.nextElement();
			output.println("<setting name=\""+name+"\">"+settings.get(name)+"</setting>");
		}
		output.println("</mimodeksettings>");
		output.flush(); // Writes the remaining data to the file
		output.close();
	}
	
	public static void loadFromFile(String fileName){
		config.loadFromFil(fileName);
	}
	
	protected void loadFromFil(String fileName){
		System.out.println("MIMODEK says > Loading configuration from "+fileName);
		XMLElement xml;
		xml = new XMLElement(app, "data/config/"+fileName);
		int numSettings = xml.getChildCount();
		for (int i = 0; i < numSettings; i++) {
			XMLElement kid = xml.getChild(i);
			String name = kid.getStringAttribute("name");
			String value = kid.getContent();
			//try to parse to find the correct type
			//NOTE : the order of the calls to the method is important
			if(isInteger(value)){
				setSetting(name, Integer.parseInt(value));
			}else if(isLong(value)){
				setSetting(name, Long.parseLong(value));
			}else if(isFloat(value)){
					setSetting(name, Float.parseFloat(value)); 
			}else if(isBoolean(value)){
				setSetting(name, Boolean.parseBoolean(value));
			}else{
				//it is a String after all
				setSetting(name,value);
			}
		}
		
	}
	
	protected boolean isFloat(String val){
		try{
			Float.parseFloat(val);
			return true;
		}catch(NumberFormatException e){
			return false;
		}
	}
	
	protected boolean isInteger(String val){
		try{
			Integer.parseInt(val);
			return true;
		}catch(NumberFormatException e){
			return false;
		}
	}
	
	protected boolean isBoolean(String val){
		try{
			Boolean.parseBoolean(val);
			return true;
		}catch(NumberFormatException e){
			return false;
		}
	}
	
	protected boolean isLong(String val){
		try{
			Long.parseLong(val);
			return true;
		}catch(NumberFormatException e){
			return false;
		}
	}
	
	
	
	
}
