package mimodek.web;

import mimodek.MainHandler;
import processing.core.*;
import processing.xml.*;

/*
 * By brunovianna
 */
public class Weather {
	public static int MIN_TEMPERATURE = 0;
	public static int MAX_TEMPERATURE = 34;

	private XMLElement xml;

	private static float _temperature = 0;
	public float fakeTemperature = 0;

	private int[] temperatureToColor;

	public boolean realTemperature = false;

	public Weather() {
		temperatureToColor();
		update();
	}

	public Weather(String fileName) {
		loadFromXML(fileName);
		update();
	}

	// load colors from an XML file
	private void loadFromXML(String fileName) {
		XMLElement xml;
		xml = new XMLElement(MainHandler.app, fileName);
		int numColor = xml.getChildCount();
		float minTemp = 1000;
		float maxTemp = -1000;
		MainHandler.app.colorMode(PApplet.HSB, 1f);
		temperatureToColor = new int[numColor];
		for (int i = 0; i < numColor; i++) {
			XMLElement kid = xml.getChild(i);
			int temp = kid.getIntAttribute("temperature");
			minTemp = Math.min(minTemp, temp);
			maxTemp = Math.max(maxTemp, temp);
			String colorStr = kid.getContent();
			String[] splitted = colorStr.split(",");
			temperatureToColor[i] = MainHandler.app.color(Float
					.parseFloat(splitted[0]), Float.parseFloat(splitted[1]),
					Float.parseFloat(splitted[2]));
		}
		MainHandler.app.colorMode(PApplet.RGB, 255);
		MIN_TEMPERATURE = (int) minTemp;
		MAX_TEMPERATURE = (int) maxTemp;
	}

	private void temperatureToColor() {
		temperatureToColor = new int[MAX_TEMPERATURE - MIN_TEMPERATURE + 1];
		MainHandler.app.colorMode(PApplet.HSB, 1f);
		temperatureToColor[0] = MainHandler.app.color(0f, 1f, 0.502f);
		temperatureToColor[1] = MainHandler.app.color(0.279f, 0.489f, 0.859f);
		temperatureToColor[2] = MainHandler.app.color(0.31f, 0.561f, 0.804f);
		temperatureToColor[3] = MainHandler.app.color(0.31f, 0.561f, 0.545f);
		temperatureToColor[4] = MainHandler.app.color(0.333f, 0.777f, 0.439f);
		temperatureToColor[5] = MainHandler.app.color(0.333f, 1f, 0.502f);
		temperatureToColor[6] = MainHandler.app.color(0.333f, 1f, 0.545f);
		temperatureToColor[7] = MainHandler.app.color(0.333f, 1f, 0.804f);
		temperatureToColor[8] = MainHandler.app.color(0.333f, 1f, 1f);
		temperatureToColor[9] = MainHandler.app.color(0.418f, 0.882f, 1f);
		temperatureToColor[10] = MainHandler.app.color(0.424f, 0.611f, 0.706f);
		temperatureToColor[11] = MainHandler.app.color(0.458f, 1f, 1f);
		temperatureToColor[12] = MainHandler.app.color(0.452f, 0.426f, 0.922f);
		temperatureToColor[13] = MainHandler.app.color(0.436f, 0.46f, 0.98f);
		temperatureToColor[14] = MainHandler.app.color(0.5f, 0.265f, 0.933f);
		temperatureToColor[15] = MainHandler.app.color(0.5f, 1f, 1f);
		temperatureToColor[16] = MainHandler.app.color(0.506f, 0.656f, 0.82f);
		temperatureToColor[17] = MainHandler.app.color(0.517f, 0.714f, 0.878f);
		temperatureToColor[18] = MainHandler.app.color(0.509f, 0.82f, 0.698f);
		temperatureToColor[19] = MainHandler.app.color(0.556f, 0.502f, 1f);
		temperatureToColor[20] = MainHandler.app.color(0.557f, 0.502f, 0.804f);
		temperatureToColor[21] = MainHandler.app.color(0.564f, 1f, 0.98f);
		temperatureToColor[22] = MainHandler.app.color(0.584f, 1f, 1f);
		temperatureToColor[23] = MainHandler.app.color(0.593f, 0.669f, 0.545f);
		temperatureToColor[24] = MainHandler.app.color(0.667f, 0.395f, 0.933f);
		temperatureToColor[25] = MainHandler.app.color(0.667f, 0.756f, 0.804f);
		temperatureToColor[26] = MainHandler.app.color(0.667f, 1f, 1f);
		temperatureToColor[27] = MainHandler.app.color(0.667f, 0.755f, 0.545f);
		temperatureToColor[28] = MainHandler.app.color(0.667f, 1f, 0.502f);
		temperatureToColor[29] = MainHandler.app.color(0.667f, 1f, 0.392f);
		temperatureToColor[30] = MainHandler.app.color(0.779f, 0.754f, 0.557f);
		temperatureToColor[31] = MainHandler.app.color(0.778f, 0.756f, 0.804f);
		temperatureToColor[32] = MainHandler.app.color(0.75f, 1f, 1f);
		temperatureToColor[33] = MainHandler.app.color(0.768f, 0.816f, 1f);
		temperatureToColor[34] = MainHandler.app.color(0.908f, 1f, 1f);
		MainHandler.app.colorMode(PApplet.RGB, 255);
	}

	public int temperatureColor() {
		float t = 0;
		if (realTemperature)
			t = _temperature;
		else
			t = fakeTemperature;

		//System.out.println((int)t+" = " + MIN_TEMPERATURE + ": "+ MAX_TEMPERATURE);

		if (t < MIN_TEMPERATURE)
			t = MIN_TEMPERATURE;
		else if (t > MAX_TEMPERATURE)
			t = MIN_TEMPERATURE;
		return temperatureToColor[(int) t - MIN_TEMPERATURE];
	}

	public float temperature() {
		return _temperature;
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
