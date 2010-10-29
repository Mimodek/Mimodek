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

import processing.core.PApplet;
import processing.core.PConstants;

public class TemperatureColorRanges {
	PApplet app;
	private int[] colourRanges;
	public static final float minT = -69;
	public static final float maxT = 58;
	public static final float[] temperatureRanges = new float[]{-20f,0f,10f,22f,32f,40f,maxT};
	
	public static TemperatureColorRanges instance;

	public static void createTemperatureColorRanges(PApplet app, String file){
		instance = new TemperatureColorRanges(app, file);
	}
	
	public static int getColor(float t){
		if(instance ==null)
			return 0;
		return instance.getColorForTemperature(t);
	}
	
	public static int getTemperatureColor(float t){
		if(instance ==null)
			return 0;
		return instance.getColorForTemperature(t);
	}
	
	public static float getLowerTemperature(float temp){
		if(instance ==null)
			return temp;
		return instance.getTemperatureForLowerRange(temp);
	}
	
	public static float getHigherTemperature(float temp){
		if(instance ==null)
			return temp;
		return instance.getTemperatureForHigherRange(temp);
	}
	
	public static int getColorInNormalized(float v){
		return instance.getColorInNormalizedRange(v) ;
	}
	
	public static float getRandomTemperatureInRange(int r){
		if(instance ==null)
			return 0;
		//System.out.println("Looking for a temperature in range "+r);
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
		//System.out.println("Returning "+t);
		return t;
		//return instance.getColorForTemperature(t);
	}
	
	
	
	protected TemperatureColorRanges(PApplet app, String file) {
		this.app = app;
		loadFromFile(file);
	}
	
	public void loadFromFile(String file) {
		app.pushStyle();
		app.colorMode(PConstants.HSB, 1.0f);
		String lines[] = app.loadStrings(file);
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
		System.out.println(colourRanges.length/2+" color ranges loaded successfully.");
	}
	
	protected float getTemperatureForLowerRange(float temp) {
		int range = 0;
		while(range<temperatureRanges.length && temperatureRanges[range]<temp)
			range++;
		if(range==temperatureRanges.length) //over maximum
			return colourRanges[colourRanges.length-1];
		
		float step = temp-(range>0?temperatureRanges[range-1]:minT);

		step /=  temperatureRanges[range]-(range>0?temperatureRanges[range-1]:minT);
		if(range>0)
			range--;
		 //System.out.println("Temperature:"+temp+" step: "+step+", range:"  +(range+1));
		 return PApplet.lerp(range>0?temperatureRanges[range-1]:minT, temperatureRanges[range], step);
	}
	
	protected float getTemperatureForHigherRange(float temp) {
		int range = 0;
		while(range<temperatureRanges.length && temperatureRanges[range]<temp)
			range++;
		if(range==temperatureRanges.length) //over maximum
			return colourRanges[colourRanges.length-1];
		
		float step = temp-(range>0?temperatureRanges[range-1]:minT);

		step /=  temperatureRanges[range]-(range>0?temperatureRanges[range-1]:minT);
		 //System.out.println("Temperature:"+temp+" step: "+step+", range:"  +(range+1));
		if(range<temperatureRanges.length-1)
			range++;
		 return PApplet.lerp(range>0?temperatureRanges[range-1]:minT, temperatureRanges[range], step);
	}
	
	
	protected int getColorForTemperature(float temp) {
		int range = 0;
		while(range<temperatureRanges.length && temperatureRanges[range]<temp)
			range++;
		if(range==temperatureRanges.length) //over maximum
			return colourRanges[colourRanges.length-1];
		
		float step = temp-(range>0?temperatureRanges[range-1]:minT);
		step /=  temperatureRanges[range]-(range>0?temperatureRanges[range-1]:minT);
		 //System.out.println("Temperature:"+temp+" step: "+step+", range:"  +(range+1));
		 return app.lerpColor(colourRanges[range*2], colourRanges[range*2+1], step);
	}
	/*

	protected int getColorForTemperature(float temp) {

		//normalize the temperature
		  float t = PApplet.map(temp,minT,maxT,0.0f,1.0f);

		  //calculate the corresponding color
		  float step = 1.0f/(colourRanges.length/2f);
		  int range = 0;
		  while(range*step < t)
		    range++;
		  if(range>0)
			  range--;
		  System.out.println("Temperature:"+temp+" step: "+step+", range:"  +(range+1));
		  t = PApplet.map(t,range*step,(range+1)*step,0.0f,1.0f);
		  return app.lerpColor(colourRanges[range*2], colourRanges[range*2+1], t);
	}
*/
	
	/* return a color for a parameter between O..n-1 (n being the number of ranges) */
	protected int getColorInNormalizedRange(float val) {
		/*
		if (minT == maxT) {
			minT = temp * 0.5f;
			maxT = temp * 1.5f;
		} else {
			minT = PApplet.min(minT, temp);
			maxT = PApplet.max(maxT, temp);
		}
		*/
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
		  //System.out.println("step: "+step+", range:"  +range);
		  t = PApplet.map(t,range*step,(range+1)*step,0.0f,1.0f);
		  return app.lerpColor(colourRanges[range*2], colourRanges[range*2+1], t);
	}

}
