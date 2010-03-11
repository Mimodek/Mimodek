package mimodek.controls;

import processing.core.PVector;
import controlP5.CVector3f;
import controlP5.ListBox;
import controlP5.RadioButton;
import controlP5.Slider;
import controlP5.Toggle;
import mimodek.Mimo;
import mimodek.Mimodek;
import mimodek.texture.Texturizer;

public class StyleGUI extends GUIModule {
	int controlPositionX = 15;
	int controlPositionY = 30;
	int controlWidth = 200;
	int controlHeight = 10;

	Slider space;
	Slider dotSize;
	Toggle blackToColor;

	ListBox listA;
	ListBox listB;
	RadioButton r, rG;
	
	//for preview
	Mimo mimo;

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
		if (rG != null)
			Mimodek.controlP5.remove(rG.name());
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
				Mimodek.config.getFloatSetting("mimosMinRadius"),
				Mimodek.config.getFloatSetting("mimosMaxRadius"), x
						+ controlPositionX, y + controlPositionY, 225, 12));
		addController(Mimodek.controlP5.addSlider("Ancestors' brightness", 0f,
				255f, Mimodek.config.getIntegerSetting("ancestorBrightness"), x
						+ controlPositionX, y + controlPositionY + 20,
				controlWidth - 10, controlHeight));
		// Toggle smoothing
		Toggle t = Mimodek.controlP5.addToggle("Smoothing", true, x
				+ controlPositionX, y + controlPositionY * 2 + 5, 10, 10);
		t.captionLabel().style().marginLeft = 12;
		t.captionLabel().style().marginTop = -12;
		addController(t);

		// display type toggle
		r = Mimodek.controlP5.addRadioButton("Graphics", x + controlPositionX,
				y + controlPositionY * 3);
		r.setColorForeground(Mimodek.app.color(120));
		r.setItemsPerRow(3);
		r.setSpacingColumn(50);
		r.addItem("Circles", Texturizer.CIRCLE);
		r.addItem("Image", Texturizer.IMAGE).absolutePosition().set(20, -10);
		r.addItem("Generated", Texturizer.GENERATED);
		if (r.getItem(0).value() == Mimodek.config
				.getIntegerSetting("textureMode"))
			r.getItem(0).setState(true);
		else if (r.getItem(1).value() == Mimodek.config
				.getIntegerSetting("textureMode"))
			r.getItem(1).setState(true);
		else
			r.getItem(2).setState(true);
		r.update();

		/*
		 * addController(MainHandler.controlP5.addSlider(" size", 0f, 4.0f,
		 * 2.86f, x+controlPositionX, y+controlPositionY+145, controlWidth,
		 * controlHeight));
		 */

		// Toggle circle/seed
		space = Mimodek.controlP5.addSlider("Space between dots", 0f, 4.0f,
				Mimodek.config.getFloatSetting("radiScale"), x
						+ controlPositionX, y + controlPositionY + 80,
				controlWidth, controlHeight);
		addController(space);
		dotSize = Mimodek.controlP5.addSlider("Dots size", 0f, 4.0f,
				Mimodek.config.getFloatSetting("dotSize"),
				x + controlPositionX, y + controlPositionY + 100, controlWidth,
				controlHeight);
		addController(dotSize);
		// Toggle between 'black to color' and 'color to black'
		blackToColor = Mimodek.controlP5.addToggle("Black to color", true, x
				+ controlPositionX, y + controlPositionY + 120, 10, 10);
		blackToColor.captionLabel().style().marginLeft = 12;
		blackToColor.captionLabel().style().marginTop = -12;
		addController(blackToColor);
		// gradient type toggle
		rG = Mimodek.controlP5.addRadioButton("Gradient", x + controlPositionX,
				y + controlPositionY + 140);
		rG.setColorForeground(Mimodek.app.color(120));
		rG.setItemsPerRow(2);
		rG.setSpacingColumn(50);
		rG.addItem("Linear", Texturizer.LINEAR);
		rG.addItem("Sin", Texturizer.SIN);
		if (rG.getItem(0).value() == Mimodek.config
				.getIntegerSetting("gradientFunction"))
			rG.getItem(0).setState(true);
		else
			rG.getItem(1).setState(true);
		rG.update();

		listA = Mimodek.controlP5.addListBox("Ancestor Texture", x
				+ controlPositionX, y + controlPositionY + 100, 120, 120);
		listB = Mimodek.controlP5.addListBox("Mimo Texture", x
				+ controlPositionX + 160, y + controlPositionY + 100, 120, 120);
		listA.valueLabel().style().marginTop = 1; // the +/- sign
		listB.valueLabel().style().marginTop = 1; // the +/- sign
		// l.setBackgroundColor(color(100,0,0));
		for (int i = 0; i < Mimodek.texturizer.textures.size(); i++) {

			listA.addItem(Mimodek.texturizer.textures.get(i).fileName, i);
			listB.addItem(Mimodek.texturizer.textures.get(i).fileName, i);
		}
		listA.close();
		listB.close();
		
		//for preview purposes
		mimo = new Mimo(new PVector(getX() + controlPositionX + 60, getY() + controlPositionY * 6 + 35));
		
		toggleControllers(Mimodek.config.getBooleanSetting(name + "_GUI_open"));
	}

	// only display relevant controls when showing mimos as simple circles
	private void basic() {
		listA.hide();
		listB.hide();
		space.hide();
		dotSize.hide();
		blackToColor.hide();
		rG.hide();
		// the size range
		// brightness control
	}

	// only display relevant controls when showing mimos as images
	private void image() {
		listA.show();
		listB.show();

		// size range
		// brightness control
		// 2 texture drop lists

	}

	// only display relevant controls when showing mimos as generated images
	private void generated() {
		space.show();
		dotSize.show();
		blackToColor.show();
		rG.show();
		// size range
		// brightness control
		// space between dots
		// dots size
		// gradient control
	}

	public void toggleControllers(boolean on) {
		super.toggleControllers(on);
		if (on) {
			listA.show();
			listB.show();
			r.show();
			rG.show();
		} else {
			listA.hide();
			listB.hide();
			r.hide();
			rG.hide();
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

		pos = rG.position();
		pos.x += offsetX;
		pos.y += offsetY;
		rG.setPosition(pos.x, pos.y);
	}

	public void draw() {
		super.draw();
		if (!Mimodek.config.getBooleanSetting(name + "_GUI_open"))
			return;
		basic();
		switch ((int) r.value()) {
		case Texturizer.IMAGE:
			image();
			break;
		case Texturizer.GENERATED:
			generated();
			break;
		}
		//ancestor.pos = new PVector(getX() + controlPositionX + 60, getY() + controlPositionY * 6 + 35);
		//active.pos = new PVector(getX() + controlPositionX+ 220, getY() + controlPositionY * 6 + 35);
		Mimodek.gfx.fill(10, 10, 10, 255);
		Mimodek.gfx.rect(getX() + controlPositionX, getY() + controlPositionY
				* 6, 120, 70);
		mimo.ancestor = true;
		mimo.pos = new PVector(getX() + controlPositionX + 60, getY() + controlPositionY * 6 + 35);
		Mimodek.texturizer.draw(mimo);
		/*Mimodek.texturizer.drawTexture(Mimodek.config
				.getIntegerSetting("ancestorTexture"), getX()
				+ controlPositionX + 60, getY() + controlPositionY * 6 + 35, 1);
		*/
		Mimodek.app.rect(getX() + controlPositionX + 160, getY()
				+ controlPositionY * 6, 120, 70);
		mimo.pos = new PVector(getX() + controlPositionX+ 220, getY() + controlPositionY * 6 + 35);
		mimo.ancestor = false;
		Mimodek.texturizer.draw(mimo);
		if(mimo.radius >= Mimodek.config.getFloatSetting("mimosMaxRadius")){
			mimo = new Mimo(new PVector(getX() + controlPositionX+ 220, getY() + controlPositionY * 6 + 35));
		}
		/*
		Mimodek.texturizer.drawTexture(Mimodek.config
				.getIntegerSetting("activeTexture"), getX() + controlPositionX
				+ 220, getY() + controlPositionY * 6 + 35, 1);
		*/
	}
}
