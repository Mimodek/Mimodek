package mimodek.controls;

import controlP5.ControlEvent;
import controlP5.RadioButton;
import controlP5.Slider;

import mimodek.configuration.Colors;
import mimodek.configuration.Configurator;
import mimodek.data.PollutionLevelsEnum;


public class DataGUI extends GUIModule {
	int controlPositionX = 15;
	int controlPositionY = 30;
	int controlWidth = 200;
	int controlHeight = 10;
	
	RadioButton r, rG;
	Slider s;

	public DataGUI(int x, int y, int width, int height) {
		super(x, y, width, height, "Data");
		create();
	}


	@Override
	public void create() {
		reset();
		int x = getX();
		int y = getY();
		
		// slider to change temperature
		s = GUI.gui().controlP5.addSlider("Temperature", -10, 40,12, x + controlPositionX, y+ controlPositionY, controlWidth, controlHeight);
		addController(s);
		GUI.registerEventHandler("Temperature", this);
		
		s = GUI.gui().controlP5.addSlider("Pollution", 5, 35,PollutionLevelsEnum.valueOf(Configurator.getStringSetting("pollutionScore")).getScoreForPollutionLevel(), x + controlPositionX, y+ controlPositionY*3, controlWidth, controlHeight);
		addController(s);
		GUI.registerEventHandler("Pollution", this);

		toggleControllers(Configurator.getBooleanSetting(name + "_GUI_open"));
	}


	@Override
	public void draw() {
		super.draw();
		if (!Configurator.getBooleanSetting(name + "_GUI_open"))
			return;
		
		GUI.gui().app.fill(Configurator.getIntegerSetting("temperatureColor"));

		GUI.gui().app.rect(getX() + controlPositionX, getY() + controlPositionY+controlHeight, 30, 30);
		
		GUI.gui().app.fill(255);
		GUI.gui().app.text(Configurator.getStringSetting("pollutionScore") , getX() + controlPositionX, getY()+ controlPositionY*3+controlHeight+10);
		//GUI.gui().app.text("Reading temperature from the web: " + (Mimodek.weather.readingOK?"YES":"!!!NO!!!") , getX() + controlPositionX, getY()+ controlPositionY);
		//GUI.gui().app.text("Current temperature: " + Mimodek.weather.get() , getX() + controlPositionX, getY()+ controlPositionY+15);
		//s.setValue(Mimodek.weather.get());
		
	}


	@Override
	public void controlEvent(ControlEvent cEvent) {
		if (cEvent == null)
			return;
		String crtlName = cEvent.name();
		if (crtlName == "Temperature") {
			Configurator.setSetting("temperatureColor",GUI.temperatureColors.getColorFromRange(cEvent.value()));
			return;
		}
		if(crtlName == "Pollution"){
			Configurator.setSetting("pollutionScore", PollutionLevelsEnum.getPollutionLevelForScore(cEvent.value()).name());
		}
	}

}
