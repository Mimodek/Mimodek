package mimodek.controls;

import controlP5.ControlEvent;
import controlP5.ListBox;
import controlP5.RadioButton;
import controlP5.Toggle;
import mimodek.configuration.Configurator;

public class ActiveMimoGUI extends GUIModule {
	int controlPositionX = 15;
	int controlPositionY = 30;
	int controlWidth = 150;
	int controlHeight = 10;

	ListBox listA;
	ListBox listB;
	RadioButton r;

	public ActiveMimoGUI(int x, int y, int width, int height) {
		super(x, y, width, height, "Behaviour");
		create();
	}

	@Override
	public void create() {
		reset();
		int x = getX();
		int y = getY();
		
		addController(GUI.gui().controlP5.addSlider("Ancestor Scale", 1.0f, 5.0f,Configurator.getFloatSetting("ancestorScale"), x + controlPositionX, y+ controlPositionY , controlWidth, 12));
		GUI.registerEventHandler("Ancestor Scale", this);
		
		addController(GUI.gui().controlP5.addSlider("Minimum active time (seconds)", 0f, 120f,Configurator.getIntegerSetting("mimosMinLifeTime"), x + controlPositionX, y+ controlPositionY*2 , controlWidth, 12));
		GUI.registerEventHandler("Minimum active time (seconds)", this);
		
		addController(GUI.gui().controlP5.addSlider("Easing", 0.01f, 1.0f,Configurator.getFloatSetting("mimosEasing"), x + controlPositionX, y+ controlPositionY*3, controlWidth, 12));
		GUI.registerEventHandler("Easing", this);
		
		addController(GUI.gui().controlP5.addSlider("Active Mimo Repel", 0,
				4,  Configurator.getFloatSetting("activeMimoRepel"), x + controlPositionX, y + controlPositionY*4,
				controlWidth, controlHeight));
		GUI.registerEventHandler("Active Mimo Repel", this);
		
		addController(GUI.gui().controlP5.addSlider("Dead Mimo Attraction", 0,
				20,  Configurator.getFloatSetting("ancestorMimoAttract"), x + controlPositionX, y + controlPositionY*5,
				controlWidth, controlHeight));
		GUI.registerEventHandler("Dead Mimo Attraction", this);
		
		addController(GUI.gui().controlP5.addSlider("Halo Strength", 0f,
				1f,  Configurator.getFloatSetting("haloStrength"), x + controlPositionX, y + controlPositionY*6,
				controlWidth, controlHeight));
		GUI.registerEventHandler("Halo Strength", this);
		
		addController(GUI.gui().controlP5.addSlider("Dead Mimo 1 NRJ",0f,1f,Configurator.getFloatSetting("deadMimo1Energy"),x + controlPositionX, y + controlPositionY * 7,controlWidth, controlHeight));
		GUI.registerEventHandler("Dead Mimo 1 NRJ", this);
		
		addController(GUI.gui().controlP5.addSlider("Halo Fadeoff",0f,1f,Configurator.getFloatSetting("haloFadeOff"),x + controlPositionX, y + controlPositionY * 8,controlWidth, controlHeight));
		GUI.registerEventHandler("Halo Fadeoff", this);
		
		// Toggle smoothing
		Toggle t = GUI.gui().controlP5.addToggle("Seed Fixed", true, x
				+ controlPositionX, y + controlPositionY * 8 + 15, 10, 10);
		t.captionLabel().style().marginLeft = 12;
		t.captionLabel().style().marginTop = -12;
		addController(t);
		GUI.registerEventHandler("Seed Fixed", this);
		
	}

	@Override
	public void controlEvent(ControlEvent cEvent) {
		if (cEvent == null)
			return;
		String crtlName = cEvent.name();
		if (crtlName == "Ancestor Scale") {
			Configurator.setSetting("ancestorScale", cEvent.value());
			return;
		}
		if (crtlName == "Minimum active time (seconds)") {
			Configurator.setSetting("mimosMinLifeTime", (int) cEvent.value());
			return;
		}
		if (crtlName == "Easing") {
			Configurator.setSetting("mimosEasing", cEvent.value());
			return;
		}
		if (crtlName == "Active Mimo Repel") {
			float val = cEvent.value();
			Configurator.setSetting("activeMimoRepel", val);
			return;
		}
		if (crtlName == "Seed Fixed") {
			Configurator.setSetting("seedFixed", cEvent.value() > 0);
		}
		if (crtlName == "Dead Mimo Attraction"){
			float val = cEvent.value();
			Configurator.setSetting("ancestorMimoAttract",val);
		}
		if(crtlName == "Halo Strength"){
			float val = cEvent.value();
			Configurator.setSetting("haloStrength",val);
		}
		if(crtlName == "Dead Mimo 1 NRJ"){
			float val = cEvent.value();
			Configurator.setSetting("deadMimo1Energy",val);
		}
		if(crtlName == "Halo Fadeoff"){
			float val = cEvent.value();
			Configurator.setSetting("haloFadeOff",val);
		}
	}
}
