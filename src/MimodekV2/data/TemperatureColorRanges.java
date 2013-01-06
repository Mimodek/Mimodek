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

import MimodekV2.debug.Verbose;
import processing.core.PApplet;
import processing.core.PConstants;

// TODO: Auto-generated Javadoc
/**
 * The Class TemperatureColorRanges.
 */
public class TemperatureColorRanges {
	
	/** The app. */
	PApplet app;
	
	/** The colour ranges. */
	private int[] colourRanges;
	
	/** The Constant minT. */
	public static final float minT = -69;
	
	/** The Constant maxT. */
	public static final float maxT = 58;
	
	/** The Constant temperatureRanges. */
	public static final float[] temperatureRanges = new float[]{-19f,0f,11f,23f,33f,41f,maxT};

	/** Definition of default color ranges */
	private static final String[] DEFAULT_RANGES = new String[]{
						"0.85490197,1.0,1.0,0.7267974,1.0,1.0",
						"0.69803923,1.0,1.0,0.55620915,1.0,1.0",
						"0.51960784,1.0,1.0,0.46961805,1.0,0.7529412",
						"0.43464053,1.0,1.0,0.35620916,1.0,1.0",
						"0.20915031,1.0,1.0,0.14509805,1.0,1.0",
						"0.1267974,1.0,1.0,0.052941173,1.0,1.0",
						"0.043790847,1.0,1.0,0.0078125,1.0,0.7529412"
						};;
	
	/** The instance. */
	public static TemperatureColorRanges instance;

	/**
	 * Creates the temperature color ranges.
	 *
	 * @param app the app
	 * @param file the file
	 * @throws Exception 
	 */
	public static void createTemperatureColorRanges(PApplet app, String file) throws Exception{
		instance = new TemperatureColorRanges(app, file);
	}
	
	/**
	 * Gets the color.
	 *
	 * @param t the t
	 * @return the color
	 */
	public static int getColor(float t){
		if(instance ==null)
			return 0;
		return instance.getColorForTemperature(t);
	}
	
	/**
	 * Gets the temperature color.
	 *
	 * @param t the t
	 * @return the temperature color
	 */
	public static int getTemperatureColor(float t){
		if(instance ==null)
			return 0;
		return instance.getColorForTemperature(t);
	}
	
	/**
	 * Gets the lower temperature.
	 *
	 * @param temp the temp
	 * @return the lower temperature
	 */
	public static float getLowerTemperature(float temp){
		if(instance ==null)
			return temp;
		return instance.getTemperatureForLowerRange(temp);
	}
	
	/**
	 * Gets the higher temperature.
	 *
	 * @param temp the temp
	 * @return the higher temperature
	 */
	public static float getHigherTemperature(float temp){
		if(instance ==null)
			return temp;
		return instance.getTemperatureForHigherRange(temp);
	}
	
	/**
	 * Gets the color in normalized.
	 *
	 * @param v the v
	 * @return the color in normalized
	 */
	public static int getColorInNormalized(float v){
		return instance.getColorInNormalizedRange(v) ;
	}
	
	/**
	 * Gets the random temperature in range.
	 *
	 * @param r the r
	 * @return the random temperature in range
	 */
	public static float getRandomTemperatureInRange(int r){
		if(instance ==null)
			return 0;

		r = r-1;
		float start = 0;
		float stop = 0;
		if(r>=temperatureRanges.length-1){
			start = temperatureRanges[temperatureRanges.length-2];
			stop = maxT;
		}
		else if(r<=0){
			start = minT;
			stop = temperatureRanges[0];
		}else{
			start = temperatureRanges[r];
			stop = temperatureRanges[r+1];
		}
		
		float t=  start+(float)Math.random()*(stop-start);
		return t;
	}
	
	
	
	/**
	 * Instantiates a new temperature color ranges.
	 *
	 * @param app the app
	 * @param file the file
	 * @throws Exception 
	 */
	protected TemperatureColorRanges(PApplet app, String file) throws Exception {
		this.app = app;
		loadFromFile(file);
	}
	
	/**
	 * Load from file.
	 *
	 * @param file the file
	 */
	public void loadFromFile(String file) throws Exception {
		app.pushStyle();
		app.colorMode(PConstants.HSB, 1.0f);
		String lines[] = new String[0];
		boolean useDefault = false;
		try{
			lines = app.loadStrings(file);
		}catch(Exception e){
			e.printStackTrace();
			useDefault = true;
			lines = DEFAULT_RANGES;
		}finally{
			colourRanges = new int[lines.length * 2];
			for (int i = 0; i < lines.length; i++) {
				String[] splitted = lines[i].split(",");
				colourRanges[i * 2] = app.color(Float.parseFloat(splitted[0]),
						Float.parseFloat(splitted[1]), Float
								.parseFloat(splitted[2]));
				colourRanges[i * 2 + 1] = app.color(Float.parseFloat(splitted[3]),
						Float.parseFloat(splitted[4]), Float
								.parseFloat(splitted[5]));
			}
			app.popStyle();
			if(!useDefault)
				Verbose.debug(colourRanges.length/2+" color ranges loaded successfully.");
			else
				throw new Exception("Unable to read from colour range file, using default values");
		}
	}
	
	
	/**
	 * Gets the temperature for lower range.
	 *
	 * @param temp the temp
	 * @return the temperature for lower range
	 */
	protected float getTemperatureForLowerRange(float temp) {
		int range = 0;
		while(range<temperatureRanges.length && temperatureRanges[range]<=temp)
			range++;
		if(range==temperatureRanges.length) //over maximum
			return colourRanges[colourRanges.length-1];

		float step = PApplet.map(temp, range>0?temperatureRanges[range-1]:minT, temperatureRanges[range]-1, 0.0f, 1.0f);
		if(step>1.0)
			step = 1.0f;
		
		 return PApplet.lerp(range>0?temperatureRanges[range-1]:minT, temperatureRanges[range], step);
	}
	
	/**
	 * Gets the temperature for higher range.
	 *
	 * @param temp the temp
	 * @return the temperature for higher range
	 */
	protected float getTemperatureForHigherRange(float temp) {
		int range = 0;
		while(range<temperatureRanges.length && temperatureRanges[range]<=temp)
			range++;
		if(range==temperatureRanges.length) //over maximum
			return colourRanges[colourRanges.length-1];
		
		float step = PApplet.map(temp, range>0?temperatureRanges[range-1]:minT, temperatureRanges[range]-1, 0.0f, 1.0f);
		if(step>1.0)
			step = 1.0f;
		
		 return PApplet.lerp(temperatureRanges[range], (range+1)<temperatureRanges.length?temperatureRanges[range]:maxT, step);
	}
	
	
	/**
	 * Gets the color for temperature.
	 *
	 * @param temp the temp
	 * @return the color for temperature
	 */
	protected int getColorForTemperature(float temp) {
		int range = 0;
		while(range<temperatureRanges.length && temperatureRanges[range]<=temp)
			range++;
		
		if(range==temperatureRanges.length) //over maximum
			return colourRanges[colourRanges.length-1];

		float step = PApplet.map(temp, range>0?temperatureRanges[range-1]:minT, temperatureRanges[range]-1, 0.0f, 1.0f);
		if(step>1.0)
			step = 1.0f;
	
		 return app.lerpColor(colourRanges[range*2], colourRanges[range*2+1], step);
	}
	
	/**
	 * Return a color for a value between O and n-1 (n being the number of ranges)
	 *
	 * @param val the value between O and n-1
	 * @return the corresponding color
	 */
	protected int getColorInNormalizedRange(float val) {
		val -= 1;
		float m = 0;
		float M = colourRanges.length/2-1;
		if(val<0)
			val =  0;
		if(val>M)
			val = M;
		//normalize the temperature
		  float t = PApplet.map(val,m,M,0.0f,1.0f);

		  //calculate the corresponding color
		  float step = 1.0f/(colourRanges.length/2f);
		  int range = 0;
		  while(range*step < t)
		    range++;
		  if(range>0)
			  range--;
		  t = PApplet.map(t,range*step,(range+1)*step,0.0f,1.0f);
		  return app.lerpColor(colourRanges[range*2], colourRanges[range*2+1], t);
	}

}
