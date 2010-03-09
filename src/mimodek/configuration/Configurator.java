package mimodek.configuration;

import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;

import processing.xml.XMLElement;

import mimodek.Mimodek;

public class Configurator {

	Hashtable<String, Object> settings;
	
	public Configurator(){
		settings = new Hashtable<String, Object>();
	}
	
	public void setSetting(String name, Object value){
			settings.put(name, value);
	}
	
	public int getIntegerSetting(String name){
		if(settings.containsKey(name))
			return (Integer)settings.get(name);
		return 0;
	}
	
	public long getLongSetting(String name){
		if(settings.containsKey(name))
			return (Long)settings.get(name);
		return 0;
	}
	
	public float getFloatSetting(String name){
		if(settings.containsKey(name))
			return (Float)settings.get(name);
		return 0;
	}
	
	public boolean getBooleanSetting(String name){
		if(settings.containsKey(name))
			return (Boolean)settings.get(name);
		return false;
	}
	
	public String getStringSetting(String name){
		if(settings.containsKey(name))
			return (String)settings.get(name);
		return "";
	}
	
	public void saveToFile(String fileName){
		PrintWriter output = Mimodek.app.createWriter("data/config/"+fileName); 
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
	
	public void loadFromFile(String fileName){
		System.out.println("MIMODEK says > Loading configuration from "+fileName);
		XMLElement xml;
		xml = new XMLElement(Mimodek.app, "data/config/"+fileName);
		int numSettings = xml.getChildCount();
		for (int i = 0; i < numSettings; i++) {
			XMLElement kid = xml.getChild(i);
			String name = kid.getStringAttribute("name");
			String value = kid.getContent();
			//try to parse to find the correct type
			
				
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
	
	private boolean isFloat(String val){
		try{
			Float.parseFloat(val);
			return true;
		}catch(NumberFormatException e){
			return false;
		}
	}
	
	private boolean isInteger(String val){
		try{
			Integer.parseInt(val);
			return true;
		}catch(NumberFormatException e){
			return false;
		}
	}
	
	private boolean isBoolean(String val){
		try{
			Boolean.parseBoolean(val);
			return true;
		}catch(NumberFormatException e){
			return false;
		}
	}
	
	private boolean isLong(String val){
		try{
			Long.parseLong(val);
			return true;
		}catch(NumberFormatException e){
			return false;
		}
	}
	
	
	
	
}
