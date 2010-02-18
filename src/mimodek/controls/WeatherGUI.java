package mimodek.controls;

import processing.core.PApplet;
import controlP5.CVector3f;
import controlP5.ListBox;
import controlP5.RadioButton;
import mimodek.MainHandler;
import mimodek.Mimo;
import mimodek.web.Weather;

public class WeatherGUI extends GUIModule {
	int controlPositionX = 15;
	int controlPositionY = 30;
	int controlWidth = 200;
	int controlHeight = 10;

	ListBox listA;
	ListBox listB;
	RadioButton r;

	public WeatherGUI(int x, int y, int width, int height) {
		super(x, y, width, height, "Weather");
		// Toggle between fake and true temperature
		addController(MainHandler.controlP5.addToggle("Use slider value", true, x+controlPositionX,
				y+controlPositionY, 10, 10));
		
		//slider to change temperature
		addController(MainHandler.controlP5.addSlider("Temperature", Weather.MIN_TEMPERATURE, Weather.MAX_TEMPERATURE, MainHandler.weather.temperature(), x+controlPositionX,
				y+controlPositionY*2, controlWidth, controlHeight));
		// Toggle between 'black to color' and 'color to black'
		addController(MainHandler.controlP5.addToggle("Black to color", true, x+controlPositionX+130,
				y+controlPositionY*2+15, 10, 10));
	}

	public void draw() {
		super.draw();
		if (!open)
			return;
		//MainHandler.gfx.colorMode(PApplet.HSB, 1.0f);
		MainHandler.gfx.fill(MainHandler.weather.temperatureColor());
		MainHandler.gfx.rect(x+controlPositionX,y+controlPositionY*2+15, 120, 70);
		//MainHandler.gfx.colorMode(PApplet.RGB, 255);
	}
}
