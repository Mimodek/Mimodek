package mimodek.controls;

import processing.core.PVector;
import controlP5.CVector3f;
import controlP5.ControlEvent;
import controlP5.ListBox;
import controlP5.RadioButton;
import controlP5.Slider;
import controlP5.Toggle;
import mimodek.MimodekObject;
import mimodek.SimpleMimo;
import mimodek.configuration.Configurator;
import mimodek.decorator.graphics.TextureCollection;

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
	MimodekObject mimo;

	public StyleGUI(int x, int y, int width, int height) {
		super(x, y, width, height, "Style");
		
		create();
	}

	@Override
	public void reset() {
		super.reset();
		if (listA != null)
			GUI.gui().controlP5.remove(listA.name());
		if (listB != null)
			GUI.gui().controlP5.remove(listB.name());
		if (r != null)
			GUI.gui().controlP5.remove(r.name());
		if (rG != null)
			GUI.gui().controlP5.remove(rG.name());
	}

	@Override
	public void create() {
		reset();
		int x = getX();
		int y = getY();
		// Texturize panel
		// size range
		// TODO : At the moment this is very badly handled, should do something
		// about it
		addController(GUI.gui().controlP5.addRange("Mimos' size", 0f, 255,
				Configurator.getFloatSetting("mimosMinRadius"),
				Configurator.getFloatSetting("mimosMaxRadius"), x
						+ controlPositionX, y + controlPositionY, 225, 12));
		//GUI.registerEventHandler("Mimos' size", this);
		
		addController(GUI.gui().controlP5.addSlider("Ancestors' brightness", 0f,
				255f, Configurator.getIntegerSetting("ancestorBrightness"), x
						+ controlPositionX, y + controlPositionY + 20,
				controlWidth - 10, controlHeight));
		GUI.registerEventHandler("Ancestors' brightness", this);
		
		// Toggle smoothing
		Toggle t = GUI.gui().controlP5.addToggle("Smoothing", true, x
				+ controlPositionX, y + controlPositionY * 2 + 5, 10, 10);
		t.captionLabel().style().marginLeft = 12;
		t.captionLabel().style().marginTop = -12;
		addController(t);
		GUI.registerEventHandler("Smoothing", this);

		// display type toggle
		/*
		r = GUI.gui().controlP5.addRadioButton("Graphics", x + controlPositionX,
				y + controlPositionY * 3);
		r.setColorForeground(GUI.gui().app.color(120));
		r.setItemsPerRow(3);
		r.setSpacingColumn(50);
		r.addItem("Circles", Texturizer.CIRCLE);
		r.addItem("Image", Texturizer.IMAGE).absolutePosition().set(20, -10);
		r.addItem("Generated", Texturizer.GENERATED);
		if (r.getItem(0).value() == Configurator
				.getIntegerSetting("textureMode"))
			r.getItem(0).setState(true);
		else if (r.getItem(1).value() == Configurator
				.getIntegerSetting("textureMode"))
			r.getItem(1).setState(true);
		else
			r.getItem(2).setState(true);
		r.update();
		GUI.registerEventHandler("Graphics", this);
*/
		/*
		 * addController(MainHandler.controlP5.addSlider(" size", 0f, 4.0f,
		 * 2.86f, x+controlPositionX, y+controlPositionY+145, controlWidth,
		 * controlHeight));
		 */

		// Toggle circle/seed
		space = GUI.gui().controlP5.addSlider("Space between dots", 0f, 4.0f,
				Configurator.getFloatSetting("radiScale"), x
						+ controlPositionX, y + controlPositionY + 80,
				controlWidth, controlHeight);
		addController(space);
		GUI.registerEventHandler("Space between dots", this);
		
		dotSize = GUI.gui().controlP5.addSlider("Dots size", 0f, 4.0f,
				Configurator.getFloatSetting("dotSize"),
				x + controlPositionX, y + controlPositionY + 100, controlWidth,
				controlHeight);
		addController(dotSize);
		GUI.registerEventHandler("Dots size", this);
		
		// Toggle between 'black to color' and 'color to black'
		blackToColor = GUI.gui().controlP5.addToggle("Black to color", true, x
				+ controlPositionX, y + controlPositionY + 120, 10, 10);
		blackToColor.captionLabel().style().marginLeft = 12;
		blackToColor.captionLabel().style().marginTop = -12;
		addController(blackToColor);
		GUI.registerEventHandler("Black to color", this);
		
		// gradient type toggle
		/*
		rG = GUI.gui().controlP5.addRadioButton("Gradient", x + controlPositionX,
				y + controlPositionY + 140);
		rG.setColorForeground(GUI.gui().app.color(120));
		rG.setItemsPerRow(2);
		rG.setSpacingColumn(50);
		rG.addItem("Linear", Texturizer.LINEAR);
		rG.addItem("Sin", Texturizer.SIN);
		if (rG.getItem(0).value() == Configurator
				.getIntegerSetting("gradientFunction"))
			rG.getItem(0).setState(true);
		else
			rG.getItem(1).setState(true);
		rG.update();
		GUI.registerEventHandler("Gradient", this);
		*/
		listA = GUI.gui().controlP5.addListBox("Ancestor Texture", x
				+ controlPositionX, y + controlPositionY + 100, 120, 120);
		listB = GUI.gui().controlP5.addListBox("Mimo Texture", x
				+ controlPositionX + 160, y + controlPositionY + 100, 120, 120);
		listA.valueLabel().style().marginTop = 1; // the +/- sign
		listB.valueLabel().style().marginTop = 1; // the +/- sign
		// l.setBackgroundColor(color(100,0,0));
		for (int i = 0; i < TextureCollection.size(); i++) {

			listA.addItem(TextureCollection.get(i).fileName, i);
			listB.addItem(TextureCollection.get(i).fileName, i);
		}
		listA.close();
		listB.close();
		GUI.registerEventHandler("Ancestor Texture", this);
		GUI.registerEventHandler("Mimo Texture", this);
		
		//for preview purposes
		mimo = new SimpleMimo(new PVector(getX() + controlPositionX + 60, getY() + controlPositionY * 6 + 35));
		toggleControllers(Configurator.getBooleanSetting(name + "_GUI_open"));
	}

	// only display relevant controls when showing mimos as simple circles
	private void basic() {
		listA.hide();
		listB.hide();
		space.hide();
		dotSize.hide();
		blackToColor.hide();
//		rG.hide();
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

	@Override
	public void toggleControllers(boolean on) {
		super.toggleControllers(on);
		if (on) {
			listA.show();
			listB.show();
//			r.show();
//			rG.show();
		} else {
			listA.hide();
			listB.hide();
//			r.hide();
//			rG.hide();
		}
	}

	@Override
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

	@Override
	public void draw() {
		super.draw();
		if (!Configurator.getBooleanSetting(name + "_GUI_open"))
			return;
		basic();
		/*switch ((int) r.value()) {
		case Texturizer.IMAGE:
			image();
			break;
		case Texturizer.GENERATED:
			generated();
			break;
		}*/
		//ancestor.pos = new PVector(getX() + controlPositionX + 60, getY() + controlPositionY * 6 + 35);
		//active.pos = new PVector(getX() + controlPositionX+ 220, getY() + controlPositionY * 6 + 35);
		GUI.gui().app.fill(10, 10, 10, 255);
		GUI.gui().app.rect(getX() + controlPositionX, getY() + controlPositionY
				* 6, 120, 70);
		
		
		//mimo.ancestor = true;
		mimo.setPos(new PVector(getX() + controlPositionX + 60, getY() + controlPositionY * 6 + 35));
		//Texturizer.draw(mimo);
		/*Mimodek.texturizer.drawTexture(Configurator
				.getIntegerSetting("ancestorTexture"), getX()
				+ controlPositionX + 60, getY() + controlPositionY * 6 + 35, 1);
		*/
		GUI.gui().app.rect(getX() + controlPositionX + 160, getY()
				+ controlPositionY * 6, 120, 70);
		mimo.setPos(new PVector(getX() + controlPositionX+ 220, getY() + controlPositionY * 6 + 35));
		//ActiveMimo aM = new ActiveMimo(mimo);
		//mimo.ancestor = false;
		//Texturizer.draw(aM);
		if(mimo.getDiameter() >= Configurator.getFloatSetting("mimosMaxRadius")){
			mimo = new SimpleMimo(new PVector(getX() + controlPositionX+ 220, getY() + controlPositionY * 6 + 35));
		}
		/*
		Mimodek.texturizer.drawTexture(Configurator
				.getIntegerSetting("activeTexture"), getX() + controlPositionX
				+ 220, getY() + controlPositionY * 6 + 35, 1);
		*/
	}

	@Override
	public void controlEvent(ControlEvent cEvent) {
		if (cEvent == null)
			return;
		String crtlName = cEvent.name();
		if (crtlName == "Graphics") {
			float val = cEvent.group().value();
			Configurator.setSetting("textureMode", (int) val);
			return;
		}
		if (crtlName == "Ancestor Texture") {
			float val = cEvent.group().value();
			Configurator.setSetting("ancestorTexture", (int) val);
			cEvent.group().close();
			return;
		}
		if (crtlName == "Mimo Texture") {
			float val = cEvent.group().value();
			Configurator.setSetting("activeTexture", (int) val);
			cEvent.group().close();
			return;
		}
		
		if (crtlName == "Dots size") {
			Configurator.setSetting("dotSize", cEvent.value());
			return;
		}
		if (crtlName == "Space between dots") {
			Configurator.setSetting("radiScale", cEvent.value());
			return;
		}
		if (crtlName == "Ancestors' brightness") {
			Configurator.setSetting("ancestorBrightness", (int) cEvent.value());
			return;
		}

		// Weather control (let's play God)
		if (crtlName == "Black to color") {
			Configurator.setSetting("blackToColor", cEvent.value() > 0);
			return;
		}
		if (crtlName == "Gradient") {
			float val = cEvent.group().value();
			Configurator.setSetting("gradientFunction", (int) val);
			return;
		}
		
	}
}
