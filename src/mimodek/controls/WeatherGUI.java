package mimodek.controls;

import controlP5.CVector3f;
import controlP5.RadioButton;
import mimodek.MainHandler;
import mimodek.texture.SeedGradient;

public class WeatherGUI extends GUIModule {
	int controlPositionX = 15;
	int controlPositionY = 30;
	int controlWidth = 200;
	int controlHeight = 10;

	RadioButton r;

	public WeatherGUI(int x, int y, int width, int height) {
		super(x, y, width, height, "Weather");
		// Toggle between fake and true temperature
		addController(MainHandler.controlP5.addToggle("Use slider value", true, x+controlPositionX,
				y+controlPositionY, 10, 10));
		
		//slider to change temperature
		addController(MainHandler.controlP5.addSlider("Temperature", -10, 40, MainHandler.weather.temperature(), x+controlPositionX,
				y+controlPositionY*2, controlWidth, controlHeight));
		// Toggle between 'black to color' and 'color to black'
		addController(MainHandler.controlP5.addToggle("Black to color", true, x+controlPositionX+130,
				y+controlPositionY*2+15, 10, 10));

		//gradient type toggle
		r = MainHandler.controlP5.addRadioButton("Gradient", x+controlPositionX+130, y+controlPositionY*2+40);
		r.setColorForeground(MainHandler.app.color(120));
		r.addItem("Linear", SeedGradient.LINEAR).setState(true);
		r.addItem("Sin", SeedGradient.SIN).setState(false);
		r.update();
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
		if (!open)
			return;
		//MainHandler.gfx.colorMode(PApplet.HSB, 1.0f);
		MainHandler.gfx.fill(MainHandler.weather.temperatureColor());
		MainHandler.gfx.rect(x+controlPositionX,y+controlPositionY*2+15, 120, 70);
		//MainHandler.gfx.colorMode(PApplet.RGB, 255);
	}
	
	public void moveControllers(int offsetX, int offsetY){
		super.moveControllers(offsetX, offsetY);
		CVector3f pos = r.position();
		pos.x += offsetX;
		pos.y += offsetY;
		r.setPosition(pos.x,pos.y);
	}
}
