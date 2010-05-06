package mimodek.configuration;

import processing.core.PApplet;
import processing.xml.XMLElement;

/**
 * This class is used to store and access a color scheme.
 * It implement a singleton so colors can be accessed in a static way by any class. To be available the singleton must first be created by Colors.createColors(parentSketch) (to use default colors) or Colors.createColors(parentSketch,"fileName.xml") to load colors from an XML file.
 * The color returned by Colors.getColor(value) depends on the minimum and maximum value queried before.
 * If you want to use values between -10 and 45.3 for instance, call Colors.getColor(-10) and Colors.getColor(45.3). After those call Colors.getColor(-10) will return the first color of the color scheme while Colors.getColor(45.3) will return the very last one.
 * @author Jonsku
 */
public class Colors {
	//default range
	private static float MIN = -10;
	private static float MAX = 40;
	private int[] colorRanges;
	protected PApplet app;
	protected static Colors colors;
	
	protected Colors(PApplet app){
		this.app = app;
		colorRanges = new int[8];
		app.colorMode(PApplet.HSB, 1f);
		colorRanges[0] = app.color(0f, 1f, 0.502f);

		colorRanges[1] = app.color(0.333f, 1f, 0.804f);

		colorRanges[2] = app.color(0.424f, 0.611f, 0.706f);

		colorRanges[3] = app.color(0.436f, 0.46f, 0.98f);

		colorRanges[4] = app.color(0.506f, 0.656f, 0.82f);

		colorRanges[5] = app.color(0.509f, 0.82f, 0.698f);

		colorRanges[6] = app.color(0.564f, 1f, 0.98f);

		colorRanges[7] = app.color(0.667f, 0.395f, 0.933f);
		app.colorMode(PApplet.RGB, 255);
	}
	
	public static void createColors(PApplet app){
		colors = new Colors(app);
		getColor(MIN);
		getColor(MAX);
	}
	
	public static void createColors(PApplet app, String fileName){
		colors = new Colors(app);
		try{
			loadFromXML(fileName, app);
		}catch(Exception ex){
			System.out.println("MIMODEK says > Failed to load color palette from "+fileName);
			ex.printStackTrace();
		}
	}

	// load colors from an XML file
	private static void loadFromXML(String fileName, PApplet app) throws Exception {
		try {
			XMLElement xml;
			xml = new XMLElement(app, "data/config/"+fileName);
			int numRange = xml.getChildCount();
			app.colorMode(PApplet.HSB, 1f);
			// a range is defined by 2 colors
			colors.colorRanges = new int[numRange * 2];
			System.out.println(colors.colorRanges.length / 2 + " color ranges found");
			for (int i = 0; i < numRange; i++) {
				XMLElement kid = xml.getChild(i);
				String from = kid.getStringAttribute("from");
				String to = kid.getStringAttribute("to");
				//
				String[] splitted = from.split(",");
				colors.colorRanges[i * 2] = app.color(Float
						.parseFloat(splitted[0]),
						Float.parseFloat(splitted[1]), Float
								.parseFloat(splitted[2]));
				splitted = to.split(",");
				colors.colorRanges[i * 2 + 1] = app.color(Float
						.parseFloat(splitted[0]),
						Float.parseFloat(splitted[1]), Float
								.parseFloat(splitted[2]));
			}

		} catch (Exception e) {
			throw e;
		} finally {
			app.colorMode(PApplet.RGB, 255);
		}
	}
	
	public static int getColor(float value){
		return colors.getC(value);
	}
	
	public static int getRandomColor(){
		return colors.getC((float)(MIN+Math.random()*(MAX-MIN)));
	}

	protected int getC(float value) {
		MIN = Math.min(MIN, value);
		MAX = Math.max(MAX, value);
		//to animate colors
		//_temperature = (app.frameCount/app.frameRate)/60;
		float size = Math.abs(MAX - MIN);

		float t = Math.abs(value - MIN) / size; // between 0
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
		return app.lerpColor(colorRanges[(i-1)*2], colorRanges[(i-1)*2+1], t);
	}
}
