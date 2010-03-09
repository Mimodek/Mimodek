package mimodek.controls;

import controlP5.CVector3f;
import controlP5.RadioButton;
import controlP5.Slider;
import mimodek.Mimodek;
import mimodek.texture.Texturizer;

public class WeatherGUI extends GUIModule {
	int controlPositionX = 15;
	int controlPositionY = 30;
	int controlWidth = 200;
	int controlHeight = 10;

	RadioButton r;
	Slider s;

	public WeatherGUI(int x, int y, int width, int height) {
		super(x, y, width, height, "Weather");
		create();
	}

	public void reset() {
		super.reset();
		if (r != null)
			Mimodek.controlP5.remove(r.name());
	}

	public void create() {
		reset();
		int x = getX();
		int y = getY();
		// Toggle between fake and true temperature
		addController(Mimodek.controlP5.addToggle("Use slider value", true, x
				+ controlPositionX, y + controlPositionY, 10, 10));

		// slider to change temperature
		s = Mimodek.controlP5.addSlider("Temperature", -10, 40,
				Mimodek.weather.temperature(), x + controlPositionX, y
				+ controlPositionY * 2, controlWidth, controlHeight);
		addController(s);
		// Toggle between 'black to color' and 'color to black'
		addController(Mimodek.controlP5
				.addToggle("Black to color", true, x + controlPositionX + 130,
						y + controlPositionY * 2 + 15, 10, 10));

		// gradient type toggle
		r = Mimodek.controlP5.addRadioButton("Gradient", x + controlPositionX
				+ 130, y + controlPositionY * 2 + 40);
		r.setColorForeground(Mimodek.app.color(120));
		r.addItem("Linear", Texturizer.LINEAR);
		r.addItem("Sin", Texturizer.SIN);
		if (r.getItem(0).value() == Mimodek.config
				.getIntegerSetting("gradientFunction"))
			r.getItem(0).setState(true);
		else
			r.getItem(1).setState(true);
		r.update();
		toggleControllers(Mimodek.config.getBooleanSetting(name + "_GUI_open"));
	}

	public void toggleControllers(boolean on) {
		super.toggleControllers(on);
		if (on) {
			r.show();
		} else {
			r.hide();
		}
	}

	public void draw() {
		super.draw();
		if (!Mimodek.config.getBooleanSetting(name + "_GUI_open"))
			return;
		Mimodek.gfx.fill(Mimodek.temperatureColor);

		Mimodek.gfx.rect(getX() + controlPositionX, getY() + controlPositionY
				* 2 + 15, 120, 70);
		Mimodek.gfx.fill(255);
		Mimodek.gfx.text("Reading temperature from the web: " + (Mimodek.weather.readingOK?"YES":"!!!NO!!!") , getX() + controlPositionX, getY()
				+ controlPositionY * 5 + 15);
		s.setValue((Mimodek.weather.temperature()));
	}
	
	/*public static void setTemperature(float t){
		s.setValue((Mimodek.weather.temperature()));
	}*/

	public void moveControllers(int offsetX, int offsetY) {
		super.moveControllers(offsetX, offsetY);
		CVector3f pos = r.position();
		pos.x += offsetX;
		pos.y += offsetY;
		r.setPosition(pos.x, pos.y);
	}
}
