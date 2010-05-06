package mimodek.controls;

import controlP5.ControlEvent;
import controlP5.ListBox;
import controlP5.RadioButton;
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
		
		addController(GUI.gui().controlP5.addSlider("Homeostasis threshold", 100,
				1000,  Configurator.getIntegerSetting("maxCells"), x + controlPositionX, y + controlPositionY*4,
				controlWidth, controlHeight));
		GUI.registerEventHandler("Homeostasis threshold", this);
		
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
		if (crtlName == "Homeostasis threshold") {
			float val = cEvent.value();
			Configurator.setSetting("maxCells", (int) val);
			return;
		}
		
	}
}
