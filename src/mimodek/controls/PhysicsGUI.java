package mimodek.controls;

import mimodek.MainHandler;

public class PhysicsGUI extends GUIModule {
	int controlPositionX = 15;
	int controlPositionY = 30;
	int controlWidth = 200;
	int controlHeight = 10;
	
	public PhysicsGUI(int x, int y, int width, int height){
		super(x,y,width,height,"Simulation");
		// Gravity control
		addController(MainHandler.controlP5.addSlider("Gravity range: X", 0, 100, 2, x+controlPositionX,
				y+controlPositionY+35, controlWidth, controlHeight));
		addController(MainHandler.controlP5.addSlider("Gravity range: Y", 0, 100, 50, x+controlPositionX,
				y+controlPositionY + 50, controlWidth, controlHeight));

		// Spring control
		addController(MainHandler.controlP5.addSlider("Spring Strength", 0, 200, 50, x+controlPositionX,
				y+controlPositionY + 85, controlWidth, controlHeight));
		addController(MainHandler.controlP5.addSlider("Spring Damping", 0, 500, 1, x+controlPositionX,
				y+controlPositionY + 100, controlWidth, controlHeight));

		// Display control
		addController(MainHandler.controlP5.addToggle("Show Mimos", true, x+controlPositionX,
				y+controlPositionY + 130, 10, 10));
		addController(MainHandler.controlP5.addToggle("Show Springs", true, x+120, y+controlPositionY + 130,
				10, 10));
		addController(MainHandler.controlP5.addToggle("Show Organism", true, x+200, y+controlPositionY + 130,
				10, 10));

		
		addController(MainHandler.controlP5.addSlider("Number of Mimos", 0, 500, 100, x+controlPositionX,
				y+controlPositionY + 170, controlWidth, controlHeight));
		// Reset Button
		addController(MainHandler.controlP5.addButton("RESET", 0, x+controlPositionX, y+controlPositionY + 190,
				70, 30));
		// Start/Pause
		addController(MainHandler.controlP5.addButton("START/PAUSE", 0, x+controlPositionX+100, y+controlPositionY + 190,
				110, 30));
	}
	
	public void draw(){
		super.draw();
		if(!open)
			return;
		MainHandler.gfx.fill(255);
		MainHandler.gfx.text("Frame rate (fps): " + (int) MainHandler.app.frameRate + " | Particles: "
				+ MainHandler.pSim.particleCount() + " | Springs: " + MainHandler.pSim.springCount(),
				x+controlPositionX, y+controlPositionY);
		MainHandler.gfx.text("Gravity (range): X[" + -MainHandler.gravX_Range + ";" + MainHandler.gravX_Range
				+ "] / Y[" + -MainHandler.gravY_Range + ";" + MainHandler.gravY_Range + "]", x+controlPositionX,controlPositionY*2+y);
		MainHandler.gfx.text("Spring strength: " + MainHandler.springStrength + " | Spring damping: "
				+ MainHandler.springDamping, x+controlPositionX, y+110);
	}
}
