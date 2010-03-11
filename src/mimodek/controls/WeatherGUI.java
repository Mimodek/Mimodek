package mimodek.controls;

import controlP5.Slider;
import mimodek.Mimodek;


public class WeatherGUI extends GUIModule {
	int controlPositionX = 15;
	int controlPositionY = 30;
	int controlWidth = 200;
	int controlHeight = 10;
	
	Slider s;

	public WeatherGUI(int x, int y, int width, int height) {
		super(x, y, width, height, "Weather");
		create();
	}


	public void create() {
		reset();
		int x = getX();
		int y = getY();;

		// slider to change temperature
		s = Mimodek.controlP5.addSlider("Temperature", -10, 40,
				Mimodek.weather.temperature(), x + controlPositionX, y
				+ controlPositionY * 2, controlWidth, controlHeight);
		addController(s);

		toggleControllers(Mimodek.config.getBooleanSetting(name + "_GUI_open"));
	}


	public void draw() {
		super.draw();
		if (!Mimodek.config.getBooleanSetting(name + "_GUI_open"))
			return;
		Mimodek.gfx.fill(Mimodek.temperatureColor);

		Mimodek.gfx.rect(getX() + controlPositionX, getY() + controlPositionY
				* 2 + 15, 120, 70);
		Mimodek.gfx.fill(255);
		Mimodek.gfx.text("Reading temperature from the web: " + (Mimodek.weather.readingOK?"YES":"!!!NO!!!") , getX() + controlPositionX, getY()
				+ controlPositionY);
		Mimodek.gfx.text("Current temperature: " + Mimodek.weather.temperature() , getX() + controlPositionX, getY()
				+ controlPositionY+15);
		s.setValue(Mimodek.weather.temperature());
	}

}
