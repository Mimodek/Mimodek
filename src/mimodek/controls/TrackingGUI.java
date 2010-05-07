package mimodek.controls;

import controlP5.ControlEvent;
import mimodek.MimosManager;
import mimodek.configuration.Configurator;

public class TrackingGUI extends GUIModule {
	int controlPositionX = 15;
	int controlPositionY = 30;
	int controlWidth = 200;
	int controlHeight = 10;

	public TrackingGUI(int x, int y, int width, int height) {
		super(x, y, width, height, "Tracking");
		create();
	}

	public void create() {
		reset();
		int x = getX();
		int y = getY();
		// Enable/disable receiving tracking info
		addController(GUI.gui().controlP5.addToggle("Tracking ON/OFF", true,
				x + controlPositionX, y + controlPositionY, 10, 10));
		GUI.registerEventHandler("Tracking ON/OFF", this);
		
		// Start/Pause simulator
		addController(GUI.gui().controlP5.addToggle("Simulator ON/OFF",
				false, x + controlPositionX, y + controlPositionY * 2, 10, 10));
		//GUI.registerEventHandler("Simulator ON/OFF", this);
		
		// Restart simulator
		addController(GUI.gui().controlP5.addButton("Restart simulator", 0, x
				+ controlPositionX + 100, y + controlPositionY * 2, 110, 30));
		//GUI.registerEventHandler("Restart simulator", this);
		
		// change distance threshold to detect flickering blobs
		addController(GUI.gui().controlP5.addSlider("Distance threshold", 0,
				200, Configurator.getFloatSetting("blobDistanceThreshold"), x + controlPositionX, y + controlPositionY * 4,
				controlWidth, controlHeight));
		GUI.registerEventHandler("Distance threshold", this);
		
		// change size of edge zone
		addController(GUI.gui().controlP5.addSlider("Edge zone size", 0, 200,
				Configurator.getFloatSetting("edgeDetection"), x + controlPositionX, y + controlPositionY * 5,
				controlWidth, controlHeight));
		GUI.registerEventHandler("Edge zone size", this);
		
		toggleControllers(Configurator.getBooleanSetting(name+"_GUI_open"));
	}

	public void draw() {
		super.draw();
		if (!Configurator.getBooleanSetting(name+"_GUI_open"))
			return;
		GUI.gui().app.fill(255);
		/*GUI.gui().app.text("Active mimos: "
				+ MimosManager, getX()
				+ controlPositionX, getY() + controlPositionY * 3);*/
		GUI.gui().app.text(
				"Receiving TUIO: "
						+ ((Configurator
								.getBooleanSetting("tuioActivity")) ? ":P"
								: ":|"), getX() + controlPositionX, getY()
						+ controlPositionY * 3 + 15);
	}

	@Override
	public void controlEvent(ControlEvent cEvent) {
		if (cEvent == null)
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
		}
	}
}
