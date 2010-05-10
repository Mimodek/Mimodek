package mimodek.configuration;

import java.io.BufferedReader;
import java.util.ArrayList;

import mimodek.utils.Verbose;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.xml.XMLElement;

/**
 * This class is used to store and access a color scheme. It implement a
 * singleton so colors can be accessed in a static way by any class. To be
 * available the singleton must first be created by
 * Colors.createColors(parentSketch) (to use default colors) or
 * Colors.createColors(parentSketch,"fileName.xml") to load colors from an XML
 * file. The color returned by Colors.getColor(value) depends on the minimum and
 * maximum value queried before. If you want to use values between -10 and 45.3
 * for instance, call Colors.getColor(-10) and Colors.getColor(45.3). After
 * those call Colors.getColor(-10) will return the first color of the color
 * scheme while Colors.getColor(45.3) will return the very last one.
 * 
 * @author Jonsku
 */
public class Colors {
	public static final int redIndex = 2;
	// default range
	public float MIN = -10;
	public float MAX = 40;
	private ArrayList<Integer> colorRanges;
	protected PApplet app;

	public Colors(PApplet app) {
		this.app = app;
		colorRanges = new ArrayList<Integer>();
		/*app.colorMode(PConstants.HSB, 1f);
		colorRanges.add(app.color(0f, 1f, 0.502f));

		colorRanges.add(app.color(0.333f, 1f, 0.804f));

		colorRanges.add(app.color(0.424f, 0.611f, 0.706f));

		colorRanges.add(app.color(0.436f, 0.46f, 0.98f));

		colorRanges.add(app.color(0.506f, 0.656f, 0.82f));

		colorRanges.add(app.color(0.509f, 0.82f, 0.698f));

		colorRanges.add(app.color(0.564f, 1f, 0.98f));

		colorRanges.add(app.color(0.667f, 0.395f, 0.933f));
		app.colorMode(PConstants.RGB, 255);*/
	}

	// load colors from an XML file
	public void loadFromXML(String fileName, PApplet app) throws Exception {
		try {
			XMLElement xml;
			xml = new XMLElement(app, "data/config/" + fileName);
			int numRange = xml.getChildCount();
			app.colorMode(PConstants.HSB, 1f);

			colorRanges = new ArrayList<Integer>();
			Verbose.say(numRange + " colors found");
			for (int i = 0; i < numRange; i++) {
				XMLElement kid = xml.getChild(i);
				String from = kid.getStringAttribute("from");
				String to = kid.getStringAttribute("to");
				// a range is defined by 2 colors
				String[] splitted = from.split(",");
				colorRanges.add(app
						.color(Float.parseFloat(splitted[0]), Float
								.parseFloat(splitted[1]), Float
								.parseFloat(splitted[2])));
				splitted = to.split(",");
				colorRanges.add(app
						.color(Float.parseFloat(splitted[0]), Float
								.parseFloat(splitted[1]), Float
								.parseFloat(splitted[2])));
			}

		} catch (Exception e) {
			throw e;
		} finally {
			app.colorMode(PConstants.RGB, 255);
		}
	}

	// load colors from an CSV file
	public void loadFromCSV(String fileName, PApplet app) throws Exception {
		try {
			BufferedReader reader = app.createReader("data/config/"+fileName);
			colorRanges = new ArrayList<Integer>();
			String line;
			line = reader.readLine();
			while (line != null) {
				String[] values = line.split(",");
				if (values.length >= redIndex + 3) {
					colorRanges.add(app.color(Integer.parseInt(values[redIndex]
							.trim()), Integer.parseInt(values[redIndex + 1]
							.trim()), Integer.parseInt(values[redIndex + 2]
							.trim())));
				}
				line = reader.readLine();
			}
		} catch (Exception e) {
			throw e;
		} finally {
			app.colorMode(PConstants.RGB, 255);
		}
	}

	public int getRandomIndividualColor() {
		int value = (int) Math.floor(Math.random() * colorRanges.size());
		Verbose.overRule("Colors, random individual "+value);
		return getIndividualColor(value);
	}

	public int getIndividualColor(int value) {
		value = value > colorRanges.size() - 1 ? colorRanges.size() - 1 : value;
		return colorRanges.get(value);
	}

	public int getRandomColorFromRange() {
		return getColorFromRange((float) (MIN + Math.random() * (MAX - MIN)));
	}
	
	public void setRange(float min, float max){
		MIN = min;
		MAX = max;
	}

	public int getColorFromRange(float value) {
		/*MIN = Math.min(MIN, value);
		MAX = Math.max(MAX, value);*/
		// to animate colors
		// _temperature = (app.frameCount/app.frameRate)/60;
		float size = Math.abs(MAX - MIN);

		float t = Math.abs(value - MIN) / size; // between 0
		// and 1;
		int i = 0;
		float inc = 0;
		float step = (float) (1.0 / (colorRanges.size() / 2.0));
		// find the range
		while (i < colorRanges.size() / 2 && t >= inc) {
			inc += step;
			i++;
		}
		t = PApplet.map(t, (i - 1) * step, i * step, 0, 1);
		return app.lerpColor(colorRanges.get((i - 1) * 2), colorRanges.get((i - 1) * 2 + 1), t);
	}
}
