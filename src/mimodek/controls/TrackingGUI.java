package mimodek.controls;

import mimodek.Mimodek;

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
		addController(Mimodek.controlP5.addToggle("Tracking ON/OFF", true,
				x + controlPositionX, y + controlPositionY, 10, 10));
		// Start/Pause simulator
		addController(Mimodek.controlP5.addToggle("Simulator ON/OFF",
				false, x + controlPositionX, y + controlPositionY * 2, 10, 10));
		// Restart simulator
		addController(Mimodek.controlP5.addButton("Restart simulator", 0, x
				+ controlPositionX + 100, y + controlPositionY * 2, 110, 30));
		// change distance threshold to detect flickering blobs
		addController(Mimodek.controlP5.addSlider("Distance threshold", 0,
				200, Mimodek.config.getFloatSetting("blobDistanceThreshold"), x + controlPositionX, y + controlPositionY * 4,
				controlWidth, controlHeight));
		// change size of edge zone
		addController(Mimodek.controlP5.addSlider("Edge zone size", 0, 200,
				Mimodek.config.getFloatSetting("edgeDetection"), x + controlPositionX, y + controlPositionY * 5,
				controlWidth, controlHeight));
		toggleControllers(Mimodek.config.getBooleanSetting(name+"_GUI_open"));
	}

	public void draw() {
		super.draw();
		if (!Mimodek.config.getBooleanSetting(name+"_GUI_open"))
			return;
		Mimodek.gfx.fill(255);
		Mimodek.gfx.text("Active mimos: "
				+ Mimodek.mimosManager.activeMimoCount(), getX()
				+ controlPositionX, getY() + controlPositionY * 3);
		Mimodek.gfx.text(
				"Receiving TUIO: "
						+ ((Mimodek.config
								.getBooleanSetting("tuioActivity")) ? ":P"
								: ":|"), getX() + controlPositionX, getY()
						+ controlPositionY * 3 + 15);
	}
}
