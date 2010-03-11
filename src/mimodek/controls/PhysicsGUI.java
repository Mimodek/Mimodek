package mimodek.controls;

import mimodek.Mimodek;

public class PhysicsGUI extends GUIModule {
	int controlPositionX = 15;
	int controlPositionY = 30;
	int controlWidth = 200;
	int controlHeight = 10;

	public PhysicsGUI(int x, int y, int width, int height) {
		super(x, y, width, height, "Simulation");
		create();
	}

	public void create() {
		reset();
		int x = getX();
		int y = getY();
		addController(Mimodek.controlP5.addSlider("Gravity range: X", 0,
				100, Mimodek.config.getFloatSetting("gravX_Range") * 1000f, x + controlPositionX, y + controlPositionY + 35,
				controlWidth, controlHeight));
		addController(Mimodek.controlP5.addSlider("Gravity range: Y", 0,
				100, Mimodek.config.getFloatSetting("gravY_Range") * 1000f, x + controlPositionX, y + controlPositionY + 50,
				controlWidth, controlHeight));

		// Spring control
		addController(Mimodek.controlP5.addSlider("Spring Strength", 0,
				200, Mimodek.config.getFloatSetting("springStrength")*100f, x + controlPositionX, y + controlPositionY + 85,
				controlWidth, controlHeight));
		addController(Mimodek.controlP5.addSlider("Spring Damping", 0, 500,
				Mimodek.config.getFloatSetting("springDamping")*1000f, x + controlPositionX, y + controlPositionY + 100,
				controlWidth, controlHeight));

		// Display control
		addController(Mimodek.controlP5.addToggle("Show Active Mimos", Mimodek.config.getBooleanSetting("showActiveMimos"), x
				+ controlPositionX, y + controlPositionY + 130, 10, 10));
		addController(Mimodek.controlP5.addToggle("Show Springs", Mimodek.config.getBooleanSetting("showSprings"),
				x + 120, y + controlPositionY + 130, 10, 10));
		addController(Mimodek.controlP5.addToggle("Show Organism", Mimodek.config.getBooleanSetting("showOrganism"),
				x + 200, y + controlPositionY + 130, 10, 10));


		// Reset Button
		addController(Mimodek.controlP5.addButton("RESET", 0, x
				+ controlPositionX, y + controlPositionY + 190, 70, 30));
		// Start/Pause
		addController(Mimodek.controlP5.addButton("START/PAUSE", 0, x
				+ controlPositionX + 100, y + controlPositionY + 190, 110, 30));
		toggleControllers(Mimodek.config.getBooleanSetting(name+"_GUI_open"));
	}

	public void draw() {
		super.draw();
		if (!Mimodek.config.getBooleanSetting(name+"_GUI_open"))
			return;
		Mimodek.gfx.fill(255);
		Mimodek.gfx.text("Frame rate (fps): "
				+ (int) Mimodek.app.frameRate + " | Particles: "
				+ Mimodek.pSim.particleCount() + " | Springs: "
				+ Mimodek.pSim.springCount(), getX() + controlPositionX,
				getY() + controlPositionY);
		Mimodek.gfx.text("Gravity (range): X[" + -Mimodek.config.getFloatSetting("gravX_Range") + ";"
				+ Mimodek.config.getFloatSetting("gravX_Range") + "] / Y[" + -Mimodek.config.getFloatSetting("gravY_Range") + ";"
				+ Mimodek.config.getFloatSetting("gravY_Range") + "]", getX() + controlPositionX,
				controlPositionY * 2 + getY());
		Mimodek.gfx.text("Spring strength: "
				+ Mimodek.config.getFloatSetting("springStrength")
				+ " | Spring damping: "
				+ Mimodek.config.getFloatSetting("springDamping"), getX()
				+ controlPositionX, getY() + 110);
	}
}
