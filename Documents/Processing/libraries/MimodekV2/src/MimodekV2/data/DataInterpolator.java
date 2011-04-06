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

import MimodekV2.config.Configurator;
import processing.core.PApplet;

// TODO: Auto-generated Javadoc
/**
 * The Class DataInterpolator.
 */
public class DataInterpolator {
	
	/** The last update. */
	public long lastUpdate;
	
	/** The last value. */
	public float lastValue;
	
	/** The next value. */
	public float nextValue;
	
	/** The data name. */
	public String dataName;
	
	/**
	 * Instantiates a new data interpolator.
	 *
	 * @param dataName the data name
	 * @param dataHandler the data handler
	 */
	public DataInterpolator(String dataName, DataHandler dataHandler){
		this.dataName = dataName;
		lastUpdate = System.currentTimeMillis();
		lastValue = Configurator.getFloatSetting(dataName);
		nextValue = lastValue;
		dataHandler.addInterpolator(this);
	}
	
	/**
	 * Update.
	 */
	public void update(){
		lastUpdate = System.currentTimeMillis();
		lastValue = nextValue;
		nextValue = Configurator.getFloatSetting(dataName);
	}
	
	/**
	 * Gets the interpolated value.
	 *
	 * @return the interpolated value
	 */
	public float getInterpolatedValue(){
		float completeRefreshTime = Configurator.getFloatSetting("DATA_REFRESH_RATE") * 60f * 1000f;
		float elapsed = System.currentTimeMillis() - lastUpdate;
		//that's the longest time for interpolation
		float interpolationStep = elapsed<completeRefreshTime?elapsed/completeRefreshTime:1f;
		return PApplet.lerp(lastValue, nextValue, interpolationStep);
	}
	/* faster : 0 no interpolation(fast),, 1 regular interpolation(slow) */
	/**
	 * Gets the interpolated value.
	 *
	 * @param faster the faster
	 * @return the interpolated value
	 */
	public float getInterpolatedValue(float faster){
		float completeRefreshTime = (Configurator.getFloatSetting("DATA_REFRESH_RATE") * 60f * 1000f)*faster;
		float elapsed = System.currentTimeMillis() - lastUpdate;
		//that's the longest time for interpolation
		float interpolationStep = elapsed<completeRefreshTime?elapsed/completeRefreshTime:1f;
		return PApplet.lerp(lastValue, nextValue, interpolationStep);
	}
}
