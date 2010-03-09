package mimodek.web;

import mimodek.Mimodek;
import processing.core.*;
import processing.xml.*;

/*
 * By brunovianna (RSS call, XML parser) and Jonsku
 */
public class Weather implements Runnable{
	//DO NOT INITIALIZE WITH EQUAL VALUES OR BE PREPARED TO FACE INFINITY!!!! (see temperatureToColor())
	public static float MIN_TEMPERATURE = 0;
	public static float MAX_TEMPERATURE = 34;

	private XMLElement xml;

	private static float _temperature = 0;
	public float fakeTemperature = 0;

	private int[] colorRanges;

	public boolean realTemperature = false;
	public boolean running;
	public int fetchDataEvery = 1; //in minutes
	
	public boolean readingOK = false;

	public Weather() {
		defaultRange();
	}

	public Weather(String fileName) {
		try {
			loadFromXML(fileName);
		} catch (Exception e) {
			System.out
					.println("MIMODEK says > Failed to load color range. Using default.");
			e.printStackTrace();
			defaultRange();
		}
		temperatureColor();
		running = true;
		//update();
	}

	// load colors from an XML file
	private void loadFromXML(String fileName) throws Exception {
		try {
			XMLElement xml;
			xml = new XMLElement(Mimodek.app, fileName);
			int numRange = xml.getChildCount();
			Mimodek.app.colorMode(PApplet.HSB, 1f);
			// a range is defined by 2 colors
			colorRanges = new int[numRange * 2];
			System.out.println(colorRanges.length / 2 + " color ranges found");
			for (int i = 0; i < numRange; i++) {
				XMLElement kid = xml.getChild(i);
				String from = kid.getStringAttribute("from");
				String to = kid.getStringAttribute("to");
				//
				String[] splitted = from.split(",");
				colorRanges[i * 2] = Mimodek.app.color(Float
						.parseFloat(splitted[0]),
						Float.parseFloat(splitted[1]), Float
								.parseFloat(splitted[2]));
				splitted = to.split(",");
				colorRanges[i * 2 + 1] = Mimodek.app.color(Float
						.parseFloat(splitted[0]),
						Float.parseFloat(splitted[1]), Float
								.parseFloat(splitted[2]));
			}

		} catch (Exception e) {
			throw e;
		} finally {
			Mimodek.app.colorMode(PApplet.RGB, 255);
		}
	}

	private void defaultRange() {
		colorRanges = new int[8];
		Mimodek.app.colorMode(PApplet.HSB, 1f);
		colorRanges[0] = Mimodek.app.color(0f, 1f, 0.502f);

		colorRanges[1] = Mimodek.app.color(0.333f, 1f, 0.804f);

		colorRanges[2] = Mimodek.app.color(0.424f, 0.611f, 0.706f);

		colorRanges[3] = Mimodek.app.color(0.436f, 0.46f, 0.98f);

		colorRanges[4] = Mimodek.app.color(0.506f, 0.656f, 0.82f);

		colorRanges[5] = Mimodek.app.color(0.509f, 0.82f, 0.698f);

		colorRanges[6] = Mimodek.app.color(0.564f, 1f, 0.98f);

		colorRanges[7] = Mimodek.app.color(0.667f, 0.395f, 0.933f);
		Mimodek.app.colorMode(PApplet.RGB, 255);
	}

	// Temperature has to be between MIN and MAX or this will fail
	public void temperatureColor() {
		//to animate colors
		//_temperature = (Mimodek.app.frameCount/Mimodek.app.frameRate)/60;
		float size = Math.abs(MAX_TEMPERATURE - MIN_TEMPERATURE);

		float t = Math.abs(_temperature - MIN_TEMPERATURE) / size; // between 0
																	// and 1;
		int i = 0;
		float inc = 0;
		float step = (float) (1.0 / (colorRanges.length / 2.0));
		// find the range
		while (i < colorRanges.length / 2 && t >= inc) {
			inc += step;
			i++;
		}
		t = PApplet.map(t, (i - 1) * step, i * step, 0, 1);
		Mimodek.temperatureColor =  Mimodek.app.lerpColor(colorRanges[(i-1)*2], colorRanges[(i-1)*2+1], t);
	}

	public float temperature() {
		return _temperature;
	}

	public void setTemperature(float temp) {
		// update the minima/maxima
		MIN_TEMPERATURE = Math.min(MIN_TEMPERATURE, temp);
		MAX_TEMPERATURE = Math.max(MAX_TEMPERATURE, temp);
		if(Mimodek.verbose) System.out.println("MIMODEK says > The temperature is "+temp+". Next weather report in "+fetchDataEvery+" minutes.");
		_temperature = temp;
		temperatureColor();
	}

	private boolean update() {
		// _temperature = 0;
		System.out.println("MIMODEK says > I'm looking up data on the net...");
		try {
			xml = new XMLElement(
					Mimodek.app,
					"http://rss.wunderground.com/auto/rss_full/global/stations/08221.xml?units=metric");

			if (xml != null) {
				XMLElement firstItem = xml.getChild(0).getChild(11).getChild(1);
				String content = firstItem.getContent();
				String[] conditionsA = PApplet.split(content, ':');
				
				String[] conditionsB = PApplet.split(conditionsA[1], 'C');
				_temperature = PApplet.parseFloat(conditionsB[0]);
				setTemperature(_temperature);
				readingOK = true;
				return true;
			} else {
				readingOK = false;
				return false;
			}
		} catch (Exception e) {
			readingOK = false;
			e.printStackTrace();
			return false;
		}

	}

	public void run() {
		while(running){
			update();
			try {
				Thread.sleep(fetchDataEvery*60000);
			} catch (InterruptedException e) {
				// The thread was interrupted, stop waiting and end
				return;
			}
		}
		
	}

}
