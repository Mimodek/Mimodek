package mimodek.controls;

import controlP5.ControlEvent;
import controlP5.Slider;

import mimodek.configuration.Colors;
import mimodek.configuration.Configurator;


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
		int y = getY();
		//Configurator.setSetting("mimosColor", 12.0f);
		// slider to change temperature
		s = GUI.gui().controlP5.addSlider("Temperature", -10, 40,
				12, x + controlPositionX, y
				+ controlPositionY * 2, controlWidth, controlHeight);
		addController(s);
		GUI.registerEventHandler("Temperature", this);

		toggleControllers(Configurator.getBooleanSetting(name + "_GUI_open"));
	}


	public void draw() {
		super.draw();
		if (!Configurator.getBooleanSetting(name + "_GUI_open"))
			return;
		//GUI.gui().app.fill(Colors.getColor(Configurator.getFloatSetting("mimosColor")));

		GUI.gui().app.rect(getX() + controlPositionX, getY() + controlPositionY
				* 2 + 15, 120, 70);
		/*
		GUI.gui().app.fill(255);
		GUI.gui().app.text("Reading temperature from the web: " + (Mimodek.weather.readingOK?"YES":"!!!NO!!!") , getX() + controlPositionX, getY()
				+ controlPositionY);
		GUI.gui().app.text("Current temperature: " + Mimodek.weather.get() , getX() + controlPositionX, getY()
				+ controlPositionY+15);
		s.setValue(Mimodek.weather.get());
		*/
	}


	@Override
	public void controlEvent(ControlEvent cEvent) {
		if (cEvent == null)
			return;
		String crtlName = cEvent.name();
		if (crtlName == "Temperature") {
			Configurator.setSetting("mimosColor", cEvent.value());
			return;
		}
	}

}
