package mimodek.controls;

import controlP5.CVector3f;
import controlP5.ListBox;
import controlP5.RadioButton;
import mimodek.MainHandler;
import mimodek.Mimo;

public class StyleGUI extends GUIModule {
	int controlPositionX = 15;
	int controlPositionY = 30;
	int controlWidth = 200;
	int controlHeight = 10;

	ListBox listA;
	ListBox listB;
	RadioButton r;

	public StyleGUI(int x, int y, int width, int height) {
		super(x, y, width, height, "Style");
		// Texturize panel
		//size range
		//TODO : At the moment this is very badly handled, should do something about it
		addController(MainHandler.controlP5.addRange("Mimos' size", 0f, 255,
				Mimo.minRadius, Mimo.maxRadius, x + controlPositionX, y+controlPositionY+80, 225, 12));

		//display type toggle
		r = MainHandler.controlP5.addRadioButton("Graphics", x + controlPositionX, y + controlPositionY+ 100);
		r.setColorForeground(MainHandler.app.color(120));
		r.addItem("Circles", 1).setState(false);
		r.addItem("Image", 2).setState(true);
		r.addItem("Generated", 3).setState(false);
		r.update();
		
		// Toggle smoothing
		addController(MainHandler.controlP5.addToggle("Smoothing", true, x+controlPositionX+110,
				y + controlPositionY+ 100, 10, 10));
		// Toggle circle/seed
		addController(MainHandler.controlP5.addToggle("Seed", false, x+controlPositionX+110,
				y + controlPositionY+ 130, 10, 10));
		addController(MainHandler.controlP5.addSlider("Space between dots", 0f, 4.0f, 1.13f, x+controlPositionX,
				y+controlPositionY+155, controlWidth, controlHeight));
		addController(MainHandler.controlP5.addSlider("Dots size", 0f, 4.0f, 2.86f, x+controlPositionX,
				y+controlPositionY+175, controlWidth, controlHeight));
		
		addController(MainHandler.controlP5.addSlider("Ancestors' brightness", 0f, 255f, 125f, x+controlPositionX,
				y+controlPositionY+200, controlWidth, controlHeight));
		/*addController(MainHandler.controlP5.addSlider(" size", 0f, 4.0f, 2.86f, x+controlPositionX,
				y+controlPositionY+145, controlWidth, controlHeight));*/
		
		listA = MainHandler.controlP5.addListBox("Ancestor Texture", x
				+ controlPositionX, y + controlPositionY, 120, 120);
		listB = MainHandler.controlP5.addListBox("Mimo Texture", x
				+ controlPositionX + 160, y + controlPositionY, 120, 120);
		listA.valueLabel().style().marginTop = 1; // the +/- sign
		listB.valueLabel().style().marginTop = 1; // the +/- sign
		// l.setBackgroundColor(color(100,0,0));
		for (int i = 0; i < MainHandler.texturizer.textures.size(); i++) {

			listA.addItem(MainHandler.texturizer.textures.get(i).fileName, i);
			listB.addItem(MainHandler.texturizer.textures.get(i).fileName, i);
		}
		listA.close();
		listB.close();
		

	}

	public void toggleControllers(boolean on) {
		super.toggleControllers(on);
		if (on) {
			listA.show();
			listB.show();
			r.show();
		} else {
			listA.hide();
			listB.hide();
			r.hide();
		}
	}
	
	public void moveControllers(int offsetX, int offsetY){
		super.moveControllers(offsetX, offsetY);
		CVector3f pos = listA.position();
		pos.x += offsetX;
		pos.y += offsetY;
		listA.setPosition(pos.x,pos.y);
		
		pos = listB.position();
		pos.x += offsetX;
		pos.y += offsetY;
		listB.setPosition(pos.x,pos.y);
		
		pos = r.position();
		pos.x += offsetX;
		pos.y += offsetY;
		r.setPosition(pos.x,pos.y);
	}

	public void draw() {
		super.draw();
		if (!open)
			return;
		MainHandler.gfx.fill(10, 10, 10, 255);
		MainHandler.gfx.rect(x+controlPositionX,y+controlPositionY, 120, 70);
		MainHandler.texturizer.drawTexture(MainHandler.texturizer.ancestor, x+controlPositionX+60,y+controlPositionY + 35, 1);
		
		MainHandler.app.rect(x+controlPositionX+160,y+controlPositionY, 120, 70);
		MainHandler.texturizer.drawTexture(MainHandler.texturizer.active, x+controlPositionX+220,y+controlPositionY + 35, 1);
	}
}
