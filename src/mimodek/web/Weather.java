package mimodek.web;

import mimodek.MainHandler;
import processing.core.*;
import processing.xml.*;

/*
 * By brunovianna
 */
public class Weather {
	public static float MIN_TEMPERATURE = 0;
	public static float MAX_TEMPERATURE = 0;

	private XMLElement xml;

	private static float _temperature = 0;
	public float fakeTemperature = 0;

	private int[] colorRanges;

	public boolean realTemperature = false;

	public Weather() {
		defaultRange();
		update();
	}

	public Weather(String fileName) {
		try{
			loadFromXML(fileName);
		}catch(Exception e){
			System.out.println("Weather class says > Failed to load color range. Using default.");
			e.printStackTrace();
			defaultRange();
		}
		update();
	}

	// load colors from an XML file
	private void loadFromXML(String fileName) throws Exception{
		XMLElement xml;
		xml = new XMLElement(MainHandler.app, fileName);
		int numRange = xml.getChildCount();
		MainHandler.app.colorMode(PApplet.HSB, 1f);
		//a range is defined by 2 colors
		colorRanges = new int[numRange*2];
		for (int i = 0; i < numRange; i++) {
			XMLElement kid = xml.getChild(i);
			String from = kid.getStringAttribute("from");
			String to = kid.getStringAttribute("to");
			//
			String[] splitted = from.split(",");
			colorRanges[i*2] = MainHandler.app.color(Float
					.parseFloat(splitted[0]), Float.parseFloat(splitted[1]),
					Float.parseFloat(splitted[2]));
			splitted = to.split(",");
			colorRanges[i*2+1] = MainHandler.app.color(Float
					.parseFloat(splitted[0]), Float.parseFloat(splitted[1]),
					Float.parseFloat(splitted[2]));
		}
		MainHandler.app.colorMode(PApplet.RGB, 255);
	}

	private void defaultRange() {
		colorRanges = new int[8];
		MainHandler.app.colorMode(PApplet.HSB, 1f);
		colorRanges[0] = MainHandler.app.color(0f, 1f, 0.502f);

		colorRanges[1] = MainHandler.app.color(0.333f, 1f, 0.804f);

		colorRanges[2]  = MainHandler.app.color(0.424f, 0.611f, 0.706f);

		colorRanges[3]  = MainHandler.app.color(0.436f, 0.46f, 0.98f);

		colorRanges[4]  = MainHandler.app.color(0.506f, 0.656f, 0.82f);

		colorRanges[5]  = MainHandler.app.color(0.509f, 0.82f, 0.698f);

		colorRanges[6]  = MainHandler.app.color(0.564f, 1f, 0.98f);

		colorRanges[7]  = MainHandler.app.color(0.667f, 0.395f, 0.933f);
		MainHandler.app.colorMode(PApplet.RGB, 255);
	}

	//Temperature has to be between MIN and MAX or this will fail
	public int temperatureColor() {
		

		 float size = Math.abs(MAX_TEMPERATURE-MIN_TEMPERATURE);

		  float t = Math.abs(_temperature-MIN_TEMPERATURE)/size; //between 0 and 1;
		  int i = 0;
		  float inc = 0;
		  float step = (float) (1.0/(colorRanges.length/2.0));
		  //find the range
		  while(i<colorRanges.length/2 && t>=inc){
		    inc += step;
		    i++;
		  }
		 t = PApplet.map(t,(i-1)*step,i*step,0,1);
		 //System.out.println("Color from range "+i+", offset "+t);
		  return MainHandler.app.lerpColor(colorRanges[i-1], colorRanges[i], t);
	}

	public float temperature() {
		return _temperature;
	}
	
	public void setTemperature(float temp){
		//update the minima/maxima
		MIN_TEMPERATURE=Math.min(MIN_TEMPERATURE,temp);
		MAX_TEMPERATURE=Math.max(MAX_TEMPERATURE,temp);
		_temperature = temp;
	}

	public boolean update() {
		//_temperature = 0;

		try {
			xml = new XMLElement(MainHandler.app,"http://rss.wunderground.com/auto/rss_full/global/stations/08221.xml?units=metric");

			if (xml != null) {
				System.out.println("Hey");
				XMLElement firstItem = xml.getChild(0).getChild(11).getChild(1);
				String content = firstItem.getContent();
				String[] conditionsA = PApplet.split(content, ':');
				System.out.println(conditionsA);
				String[] conditionsB = PApplet.split(conditionsA[1], 'C');
				System.out.println(conditionsB); //
				_temperature = PApplet.parseFloat(conditionsB[0]);
				setTemperature(_temperature);
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

}
