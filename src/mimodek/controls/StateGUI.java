package mimodek.controls;

import controlP5.ControlEvent;
import mimodek.configuration.Configurator;

public class StateGUI extends GUIModule {
	int controlPositionX = 15;
	int controlPositionY = 30;
	int controlWidth = 200;
	int controlHeight = 10;

	public StateGUI(int x, int y, int width, int height) {
		super(x, y, width, height, "System Info");
		create();
	}

	@Override
	public void create() {
		reset();
		int x = getX();
		int y = getY();
		toggleControllers(Configurator.getBooleanSetting(name+"_GUI_open"));
	}

	@Override
	public void draw() {
		super.draw();
		if (!Configurator.getBooleanSetting(name+"_GUI_open"))
			return;
		GUI.gui().app.fill(255);
		/*GUI.gui().app.text("Active mimos: "
				+ MimosManager, getX()
				+ controlPositionX, getY() + controlPositionY * 3);*/
		GUI.gui().app.text("Receiving TUIO: "+ ((Configurator.getBooleanSetting("tuioActivity")) ? ":P" : ":|"), getX() + controlPositionX, getY() + controlPositionY);
		GUI.gui().app.text("Frame rate: "+ GUI.gui().app.frameRate, getX() + controlPositionX, getY() + controlPositionY*2);
		GUI.gui().app.text("Active Mimos: "+ Configurator.getIntegerSetting("activeMimoCount"), getX() + controlPositionX, getY() + controlPositionY*3);
		GUI.gui().app.text("Dead Mimos 1: "+ Configurator.getIntegerSetting("deadMimo1Count"), getX() + controlPositionX, getY() + controlPositionY*4);
		GUI.gui().app.text("Dead Mimos 2: "+ Configurator.getIntegerSetting("deadMimo2Count"), getX() + controlPositionX, getY() + controlPositionY*5);
		GUI.gui().app.text("Ancestor cells: "+ Configurator.getIntegerSetting("ancestorCellCount"), getX() + controlPositionX, getY() + controlPositionY*6);
	}

	@Override
	public void controlEvent(ControlEvent cEvent) {
		/*if (cEvent == null)
			return;
		String crtlName = cEvent.name();
		// Tracking controls
		if (crtlName == "Tracking ON/OFF") {
			Configurator.setSetting("trackingOn", cEvent.value() > 0);
			return;
		}
		if (crtlName == "Distance threshold") {
			Configurator.setSetting("blobDistanceThreshold", cEvent.value());
			return;
		}
		if (crtlName == "Edge zone size") {
			Configurator.setSetting("edgeDetection", cEvent.value());
			return;
		}*/
		return;
	}
}
