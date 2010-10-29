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
import java.util.Iterator;

import MimodekV2.MimodekLocation;
import MimodekV2.config.Configurator;
import MimodekV2.debug.Verbose;

import processing.core.PApplet;
import processing.xml.XMLElement;

public class WeatherUndergroundClient {
	public final static String GEO_LOOKUP_URL = "http://api.wunderground.com/auto/wui/geo/GeoLookupXML/index.xml";
	public final static String CURRENT_OBSERVATION_URL = "http://api.wunderground.com/weatherstation/WXCurrentObXML.asp";
	private final static int CITY_COUNTRY = 0;
	private final static int LAT_LON = 2;
	
	private PApplet app;
	private int useStation = 0;
	private ArrayList<String> stationIds = new ArrayList<String>();
	/*
	private String city;
	private String country;
	*/
	public WeatherUndergroundClient(PApplet app){
		this.app = app;
	}
	
	/*
	 * Find a station close the location
	 */	
	public boolean findStation(MimodekLocation location){
		int searchBy = LAT_LON;
		if(location.latitude == null  || location.longitude== null){
			if(location.city != null && location.country != null){
				searchBy = CITY_COUNTRY;
			}else{
				return false;
			}
		}
		boolean result = false;
		switch(searchBy){
			case CITY_COUNTRY:
				result = findStationByCityAndCountry(location);
				break;
			case LAT_LON:
				result = findStationByCoordinates(location);
				break;
		}
		return result;
	}
	
	public boolean findStationByCoordinates(MimodekLocation location){
		try{
			XMLElement xml = new XMLElement(app, GEO_LOOKUP_URL+"?query="+location.latitude+","+location.longitude);
			location.city = xml.getChild("city").getContent();
			location.country = xml.getChild("country").getContent();
			
			XMLElement private_stations = xml.getChild("nearby_weather_stations/pws");
			if(private_stations.getChildCount()==0)
				throw new Exception("No station for this location, sorry");
			useStation = 0;
			for(int i=0;i<private_stations.getChildCount();i++){
				stationIds.add(private_stations.getChild(i).getChild("id").getContent());
			}
			//Verbose.overRule("Reading weather data from station:"+stationIds.get(useStation));
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean findStationByCityAndCountry(MimodekLocation location){
		try{
			XMLElement xml = new XMLElement(app, GEO_LOOKUP_URL+"?query="+location.city+","+location.country);
			location.latitude = Float.parseFloat(xml.getChild("lat").getContent());
			location.longitude = Float.parseFloat(xml.getChild("lon").getContent());
			
			XMLElement private_stations = xml.getChild("nearby_weather_stations/pws");
			if(private_stations.getChildCount()==0)
				throw new Exception("No station for this location, sorry");
			useStation = 0;
			for(int i=0;i<private_stations.getChildCount();i++){
				stationIds.add(private_stations.getChild(i).getChild("id").getContent());
			}
			
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	/*
	 * e.g. : "Madrid","Spain"
	 * TODO: It would be great if this returned false if there was a problem  finding a station
	 */
/*
	public boolean findStationByCityAndCountry(String city, String country){
		try{
			XMLElement xml = new XMLElement(app, GEO_LOOKUP_URL+"?query="+city+","+country);
			XMLElement private_station = xml.getChild("nearby_weather_stations/pws").getChild(0);
			stationId = private_station.getChild("id").getContent();
			Verbose.debug("Reading temperature data from station:"+stationId);
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	*/
	
	/*
	 * Retrieve the current observation for the registered station save the data in the configurator.
	 * The hash map maps weather data as they are named in the  xml files of weather underground (key), to data as they are named in mimodek(value)
	 * e.g. : "temp_c","DATA_TEMPERATURE"
	 */
	public boolean readLatestObservation(MimodekLocation location,HashMap<String,String> wuToMimodek){
		if(stationIds.size()<1){
			if(!findStation(location))
				return false;
		}
		try{
			boolean stationOk = false;
			XMLElement xml = new XMLElement(app, CURRENT_OBSERVATION_URL+"?ID="+stationIds.get(useStation));
			while(!stationOk){
				//check the validity of the response
				if(!xml.getChild("station_id").getContent().equalsIgnoreCase(stationIds.get(useStation))){
					useStation++;
					if(useStation>=stationIds.size()){
						useStation = 0;
						return false;
					}
				}else{
					stationOk = true;
				}
			}
			Verbose.overRule("Reading weather data from station:"+stationIds.get(useStation));
			Verbose.debug(xml.getChild("observation_time").getContent());
			Iterator<String> wuIt = wuToMimodek.keySet().iterator();
			while(wuIt.hasNext()){
				String wu = wuIt.next();
				Configurator.setSetting(wuToMimodek.get(wu),Float.parseFloat(xml.getChild(wu).getContent()));
				Verbose.debug(wuToMimodek.get(wu)+","+Configurator.getFloatSetting(wuToMimodek.get(wu)));
			}
			return true;
		}catch(Exception e){
			//useStation
			e.printStackTrace();
			return false;
		}
	}
	
	
	
	
}
