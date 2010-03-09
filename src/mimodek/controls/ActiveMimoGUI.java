package mimodek.controls;

import controlP5.ListBox;
import controlP5.RadioButton;
import mimodek.Mimodek;

public class ActiveMimoGUI extends GUIModule {
	int controlPositionX = 15;
	int controlPositionY = 30;
	int controlWidth = 200;
	int controlHeight = 10;

	ListBox listA;
	ListBox listB;
	RadioButton r;

	public ActiveMimoGUI(int x, int y, int width, int height) {
		super(x, y, width, height, "Active Mimo");
		create();
	}

	public void create() {
		reset();
		int x = getX();
		int y = getY();
		addController(Mimodek.controlP5.addSlider("Ancestor Scale", 1.0f, 5.0f,Mimodek.config.getFloatSetting("ancestorScale"), x + controlPositionX, y+ controlPositionY , 225, 12));
		addController(Mimodek.controlP5.addSlider("Minimum active time (seconds)", 0f, 120f,Mimodek.config.getIntegerSetting("mimosMinLifeTime"), x + controlPositionX, y+ controlPositionY*2 , 225, 12));
		addController(Mimodek.controlP5.addSlider("Easing", 0.01f, 1.0f,Mimodek.config.getFloatSetting("mimosEasing"), x + controlPositionX, y+ controlPositionY*3, 225, 12));
		
	}
}
