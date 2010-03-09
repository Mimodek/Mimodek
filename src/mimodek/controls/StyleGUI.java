package mimodek.controls;

import controlP5.CVector3f;
import controlP5.ListBox;
import controlP5.RadioButton;
import mimodek.Mimodek;
import mimodek.Mimo;
import mimodek.texture.Texturizer;

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
		create();
	}

	public void reset() {
		super.reset();
		if (listA != null)
			Mimodek.controlP5.remove(listA.name());
		if (listB != null)
			Mimodek.controlP5.remove(listB.name());
		if (r != null)
			Mimodek.controlP5.remove(r.name());
	}

	public void create() {
		reset();
		int x = getX();
		int y = getY();
		// Texturize panel
		// size range
		// TODO : At the moment this is very badly handled, should do something
		// about it
		addController(Mimodek.controlP5.addRange("Mimos' size", 0f, 255,
				 Mimodek.config.getFloatSetting("mimosMinRadius"),  Mimodek.config.getFloatSetting("mimosMaxRadius"), x + controlPositionX, y
						+ controlPositionY + 80, 225, 12));

		// display type toggle
		r = Mimodek.controlP5.addRadioButton("Graphics", x
				+ controlPositionX, y + controlPositionY + 100);
		r.setColorForeground(Mimodek.app.color(120));
		r.addItem("Circles", Texturizer.CIRCLE);
		r.addItem("Image",  Texturizer.IMAGE);
		r.addItem("Generated",  Texturizer.GENERATED);
		if(r.getItem(0).value() == Mimodek.config.getIntegerSetting("textureMode"))
			r.getItem(0).setState(true);
		else if(r.getItem(1).value() == Mimodek.config.getIntegerSetting("textureMode"))
			r.getItem(1).setState(true);
		else 
			r.getItem(2).setState(true);
		r.update();

		// Toggle smoothing
		addController(Mimodek.controlP5.addToggle("Smoothing", true, x
				+ controlPositionX + 110, y + controlPositionY + 100, 10, 10));
		// Toggle circle/seed
		addController(Mimodek.controlP5.addToggle("Seed", !Mimodek.config.getBooleanSetting("drawCircle"), x
				+ controlPositionX + 110, y + controlPositionY + 130, 10, 10));
		addController(Mimodek.controlP5.addSlider("Space between dots", 0f,
				4.0f, Mimodek.config.getFloatSetting("radiScale"), x + controlPositionX, y + controlPositionY + 155,
				controlWidth, controlHeight));
		addController(Mimodek.controlP5.addSlider("Dots size", 0f, 4.0f,
				Mimodek.config.getFloatSetting("dotSize"), x + controlPositionX, y + controlPositionY + 175,
				controlWidth, controlHeight));

		addController(Mimodek.controlP5.addSlider("Ancestors' brightness",
				0f, 255f, Mimodek.config.getIntegerSetting("ancestorBrightness"), x + controlPositionX, y + controlPositionY
						+ 200, controlWidth, controlHeight));
		/*
		 * addController(MainHandler.controlP5.addSlider(" size", 0f, 4.0f,
		 * 2.86f, x+controlPositionX, y+controlPositionY+145, controlWidth,
		 * controlHeight));
		 */

		listA = Mimodek.controlP5.addListBox("Ancestor Texture", x
				+ controlPositionX, y + controlPositionY, 120, 120);
		listB = Mimodek.controlP5.addListBox("Mimo Texture", x
				+ controlPositionX + 160, y + controlPositionY, 120, 120);
		listA.valueLabel().style().marginTop = 1; // the +/- sign
		listB.valueLabel().style().marginTop = 1; // the +/- sign
		// l.setBackgroundColor(color(100,0,0));
		for (int i = 0; i < Mimodek.texturizer.textures.size(); i++) {

			listA.addItem(Mimodek.texturizer.textures.get(i).fileName, i);
			listB.addItem(Mimodek.texturizer.textures.get(i).fileName, i);
		}
		listA.close();
		listB.close();
		toggleControllers(Mimodek.config.getBooleanSetting(name+"_GUI_open"));
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

	public void moveControllers(int offsetX, int offsetY) {
		super.moveControllers(offsetX, offsetY);
		CVector3f pos = listA.position();
		pos.x += offsetX;
		pos.y += offsetY;
		listA.setPosition(pos.x, pos.y);

		pos = listB.position();
		pos.x += offsetX;
		pos.y += offsetY;
		listB.setPosition(pos.x, pos.y);

		pos = r.position();
		pos.x += offsetX;
		pos.y += offsetY;
		r.setPosition(pos.x, pos.y);
	}

	public void draw() {
		super.draw();
		if (!Mimodek.config.getBooleanSetting(name+"_GUI_open"))
			return;
		Mimodek.gfx.fill(10, 10, 10, 255);
		Mimodek.gfx.rect(getX() + controlPositionX, getY()
				+ controlPositionY, 120, 70);
		Mimodek.texturizer.drawTexture(Mimodek.config
				.getIntegerSetting("ancestorTexture"), getX()
				+ controlPositionX + 60, getY() + controlPositionY + 35, 1);

		Mimodek.app.rect(getX() + controlPositionX + 160, getY()
				+ controlPositionY, 120, 70);
		Mimodek.texturizer.drawTexture(Mimodek.config
				.getIntegerSetting("activeTexture"), getX() + controlPositionX
				+ 220, getY() + controlPositionY + 35, 1);
	}
}
