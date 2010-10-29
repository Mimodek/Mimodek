package MimodekV2.data;

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

import java.util.ArrayList;
import java.util.HashMap;

import MimodekV2.CellB;
import MimodekV2.MimodekLocation;

import MimodekV2.config.Configurator;
import MimodekV2.debug.Verbose;

import p5wp.WPMessageListener;
import p5wp.XMLReceiver;
import processing.core.PApplet;

public class DataHandler implements Runnable, WPMessageListener {
	Thread runner = null;
	boolean run = false;
	MimodekLocation location;
	public ArrayList<DataInterpolator> dataInterpolators = new ArrayList<DataInterpolator>();
	
	public WeatherUndergroundClient wunderground;
	
	public HashMap<String,String> mapping = new HashMap<String,String>();
	private XMLReceiver xmlReceiver;
	
	public static int instanceCounter = 0;
	
	protected static DataHandler instance;
	
	public static boolean testDataSource(PApplet app, MimodekLocation location){
		WeatherUndergroundClient wunderground = new WeatherUndergroundClient(app);
		if(wunderground.findStation(location)){
			Verbose.debug("A weather station for the location has been found.");
			HashMap<String,String> testMap = new HashMap<String, String>();
			testMap.put("temp_c", "DATA_TEMPERATURE");
			testMap.put("relative_humidity", "DATA_HUMIDITY");
			if(wunderground.readLatestObservation(location, testMap)){
				Verbose.debug("The weather station is working properly.");
				return true;
			}else{
				Verbose.overRule("A problem occured reading from the weather station.");
				return false;
			}
		}else{
			Verbose.overRule("Couldn't find a weather station for location:"+location+". Try providing more information about the location.");
			return false;
		}
	}
	
	public static DataHandler getInstance(MimodekLocation location, PApplet app){
		if(instance == null)
			instance = new DataHandler(location, app);
		return instance;
	}
	
	private DataHandler(MimodekLocation location, PApplet app) {
		this.location = location;
		//Weather underground
		wunderground = new WeatherUndergroundClient(app);
		wunderground.findStation(location);
		//Wordpress plugin
		this.xmlReceiver = new XMLReceiver(app, Configurator.getStringSetting("WORDPRESS_URL"));
		xmlReceiver.setListener(this);
		runner = new Thread(this);
		
		instanceCounter++;
		Verbose.debug("I'm instance #"+instanceCounter);
	}
	
	public void map(String key, String data){
		mapping.put(key, data);
	}
	
	public void addInterpolator(DataInterpolator dataInterpolator) {
		dataInterpolators.add(dataInterpolator);
	}
	
	public void start(){
		if(run)
			return;
		run = true;
		runner.start();
	}
	
	public void stop(){
		run = false;
	}
	
	public void run() {
		while (run) {
			//Mimodek.dataUpdate = true;
			try {
				Verbose.debug("DataHandler #"+instanceCounter);
				//query the wordpress plugin
				xmlReceiver.getFreshData(location.city);
				if(wunderground.readLatestObservation(location, mapping)){
					for(int i=0;i<dataInterpolators.size();i++)
						dataInterpolators.get(i).update();
				}
				Thread.sleep((long)(Configurator.getFloatSetting("DATA_REFRESH_RATE") * 60 * 1000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	/*
	 * This deals with the data returned by the wordpress plugin (pollution,...), not the weather data
	 */
	public void onResponse(HashMap<String,String> messages) {
		/*
		 * <item name='NO2'>Bueno</item> <item name='CO'>Bueno</item> <item
		 * name='SO2'>Bueno</item> <item name='particles'>Bueno</item> <item
		 * name='03'>Admisible</item> <item name='temperature'>17.4</item> <item
		 * name='rain'>0.0</item>
		 */
		//Verbose.overRule("Last data received at : " + Verbose.now());
			PollutionLevelsEnum[] pollutionData = new PollutionLevelsEnum[5];
			int c = 0;
	
			String[] keys = (String[]) messages.keySet().toArray(new String[0]);
			for (int i = 0; i < keys.length; i++) {
				try {
					if (keys[i].equalsIgnoreCase("NO2")) {
						pollutionData[c++] = PollutionLevelsEnum
								.getPollutionLevelForWord((String) messages
										.get(keys[i]));
					} else if (keys[i].equalsIgnoreCase("CO")) {
						pollutionData[c++] = PollutionLevelsEnum
								.getPollutionLevelForWord((String) messages
										.get(keys[i]));	
					} else if (keys[i].equalsIgnoreCase("SO2")) {
						pollutionData[c++] = PollutionLevelsEnum
								.getPollutionLevelForWord((String) messages
										.get(keys[i]));
					} else if (keys[i].equalsIgnoreCase("particles")) {
						pollutionData[c++] = PollutionLevelsEnum
								.getPollutionLevelForWord((String) messages
										.get(keys[i]));
					} else if (keys[i].equalsIgnoreCase("03")) {
						pollutionData[c++] = PollutionLevelsEnum
								.getPollutionLevelForWord((String) messages
										.get(keys[i]));
					}
				} catch (NotAPollutionLevelException e) {
					e.printStackTrace();
				}
				//Verbose.overRule(keys[i] + ": " + messages.get(keys[i]));
			}
			if (c == pollutionData.length) {
				CellB.usePollutionData = true;
				Configurator.setSetting("DATA_POLLUTION", (float)PollutionLevelsEnum.calculatePollutionScore(pollutionData).getScore());
				Verbose.debug("New pollution level: "+PollutionLevelsEnum.getPollutionLevelForScore(Configurator.getFloatSetting("DATA_POLLUTION")));
				//Verbose.overRule("Pollution level set.");
			}
	}
	
}
