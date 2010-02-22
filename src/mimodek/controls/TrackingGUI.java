package mimodek.controls;


import mimodek.MainHandler;
import mimodek.tracking.TUIOClient;

public class TrackingGUI extends GUIModule {
	int controlPositionX = 15;
	int controlPositionY = 30;
	int controlWidth = 200;
	int controlHeight = 10;


	public TrackingGUI(int x, int y, int width, int height) {
		super(x, y, width, height, "Tracking");
		// Enable/disable receiving tracking info
		addController(MainHandler.controlP5.addToggle("Tracking ON/OFF", true, x+controlPositionX,
				y+controlPositionY, 10, 10));
		// Start/Pause simulator
		addController(MainHandler.controlP5.addToggle("Simulator ON/OFF", false, x+controlPositionX,
				y+controlPositionY*2, 10, 10));
		// Restart simulator
		addController(MainHandler.controlP5.addButton("Restart simulator", 0, x+controlPositionX+100, y+controlPositionY*2,
				110, 30));
		//change distance threshold to detect flickering blobs
		addController(MainHandler.controlP5.addSlider("Distance threshold", 0, 200, 10, x+controlPositionX,
				y+controlPositionY*4, controlWidth, controlHeight));
		//change size of edge zone
		addController(MainHandler.controlP5.addSlider("Edge zone size", 0, 200, 35, x+controlPositionX,
				y+controlPositionY*5, controlWidth, controlHeight));
	}
	
	public void draw(){
		super.draw();
		if(!open)
			return;
		MainHandler.gfx.fill(255);
		MainHandler.gfx.text("Active mimos: " + MainHandler.mimosManager.activeMimoCount(),x+controlPositionX, y+controlPositionY*3);
		MainHandler.gfx.text("Receiving TUIO: " + ((TUIOClient.receiving)?":P":":|"),x+controlPositionX, y+controlPositionY*3+15);	
	}
}
