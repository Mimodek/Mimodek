package mimodek;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;

import mimodek.controls.GUI;
import mimodek.controls.GUIModule;
import mimodek.controls.PhysicsGUI;
import mimodek.controls.StyleGUI;
import mimodek.controls.WeatherGUI;
import mimodek.texture.RadialGradient;
import mimodek.texture.Texturizer;
import mimodek.tracking.TUIOClient;
import mimodek.tracking.TrackingSimulator;
import mimodek.web.Weather;

import controlP5.ControlEvent;
import controlP5.ControlP5;
import controlP5.ListBox;
import controlP5.RadioButton;
import controlP5.Range;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;

public class MainHandler {
	// Needed to go fullscreen
	public GraphicsDevice graphicsDevice;
	DisplayMode origDisplayMode;
	// private Frame mimodekFrame;

	public static PGraphics gfx;
	public static PApplet app;
	public static int screenWidth;
	public static int screenHeight;

	// physics simulator
	public static Physics pSim;

	// the organism
	public static Organism organism;

	// texture handler
	public static Texturizer texturizer;
	boolean rescaleMimo = false;

	// the mimos (active/ancestor) manager
	MimosManager mimosManager;

	// Graphic User Interface Handler
	GUI gui;

	// the tracking module
	public TrackingSimulator trackingSimulator;// don't run it at the same time
												// as real tracking input
	public TUIOClient tracking;

	// Weather Data
	public static Weather weather;

	// information toggles
	boolean showGUI = true;
	static boolean showSprings = true;
	boolean showMimos = true;
	boolean showOrganism = true;

	// environment variables
	public static float gravX_Range = 0.01f;
	public static float gravY_Range = 0.02f;
	public static float gravY;
	public static float gravX;

	public static float springStrength = 0.5f;
	public static float springDamping = 0.01f;

	// number of particles generated
	int numMimos = 100;

	// output resolution
	public static int outputWidth = 192;
	public static int outputHeight = 157;
	float scaleFactor;
	// this is for translating to the right spot when showing on the output
	int outputOffsetX = 40;
	int outputOffsetY = 40;

	boolean preview = true;
	public static boolean pause = false;

	// controls
	public static ControlP5 controlP5;
	private boolean updatePhysics = false;
	int controlPositionX = 30;
	int controlOffsetY = 30;
	int controlWidth = 200;
	int controlHeight = 10;

	public MainHandler(int screenWidth, PApplet app) {
		MainHandler.app = app;
		MainHandler.screenWidth = screenWidth;
		// we want to have the same ratio as the output
		MainHandler.screenHeight = (int) (MainHandler.screenWidth * ((float) outputHeight / (float) outputWidth));
		// setup();
	}

	public MainHandler(int screenWidth, int screenHeight, PApplet app) {
		MainHandler.app = app;
		MainHandler.screenWidth = screenWidth;
		MainHandler.screenHeight = screenHeight;
		// setup();
	}

	public void setup() {
		app.size(MainHandler.screenWidth, MainHandler.screenHeight);
		// we want to have the same ratio as the output
		screenHeight = (int) (MainHandler.screenWidth * ((float) outputHeight / (float) outputWidth));
		// to switch from screen preview to output
		scaleFactor = (float) outputWidth / (float) screenWidth;

		app.smooth();
		app.frameRate(24);
		PFont font = app.createFont("Verdana", 10, false);
		app.hint(PApplet.ENABLE_NATIVE_FONTS);
		app.textFont(font);

		// app = this;
		gfx = app.g;

		// simulation
		pSim = new Physics(0.2f, 0.1f, false);

		// organism
		organism = new Organism();

		// animation
		mimosManager = new MimosManager();

		// weather data
		weather = new Weather();

		// tracking
		tracking = new TUIOClient();
		trackingSimulator = new TrackingSimulator(screenWidth, screenHeight);
		tracking.setListener(mimosManager);
		trackingSimulator.setListener(mimosManager);
		texturizer = new Texturizer();
		setupGUI();
		// texturizer = new Texturizer();
		trackingSimulator.on();
	}

	public void reset() {
		/*
		 * gravX_Range = 0.01f; gravY_Range = 0.02f;
		 * 
		 * springStrength = 0.5f; springDamping = 0.01f;
		 */
		pSim = new Physics(0.2f, 0.1f, false);

		organism = new Organism();
		mimosManager = new MimosManager();
		
		// restart the simulator
		trackingSimulator.off();
		trackingSimulator = new TrackingSimulator(screenWidth, screenHeight);
		trackingSimulator.setListener(mimosManager);
		trackingSimulator.on();
	}

	public void setupGUI() {

		controlP5 = new ControlP5(app);
		gui = new GUI();
		gui.addModule(new PhysicsGUI(350, controlOffsetY + 270, 310, 250));
		gui.addModule(new StyleGUI(15, controlOffsetY + 270, 310, 250));
		gui.addModule(new WeatherGUI(350, controlOffsetY, 310, 250));
	}

	public void draw() {

		app.background(0);
		if (!pause) {
			updateEnv();
			pSim.update();
			mimosManager.update();
		}
		app.pushMatrix();
		if (!preview) {
			app.noFill();
			app.stroke(app.color(255));

			app.translate(outputOffsetX, outputOffsetY);
			app.rect(-1, -1, outputWidth + 1, outputHeight + 1);
			app.scale(scaleFactor);

		}
		/*
		 * if (underTheMouse != null) underTheMouse.draw();
		 */
		if (showOrganism)
			organism.draw();
		if (showMimos)
			mimosManager.draw();
		if (showSprings)
			pSim.drawSprings(app.color(0, 0, 255), 1);
		app.popMatrix();
		if (showGUI)
			gui.draw();
	}

	public void updateEnv() {
		gravY = -gravY_Range
				+ PApplet.sin(app.noise(app.frameCount * 0.01f) * 2
						* PApplet.PI) * gravY_Range * 2;
		gravX = -gravX_Range
				+ PApplet.sin(app.noise(app.frameCount * 0.01f) * 2
						* PApplet.PI) * gravX_Range * 2;
		pSim.setGravity(gravX, gravY);

	}

	public void mouseReleased() {
		if (updatePhysics) {
			pSim.changeSprings(springStrength, springDamping);
			updatePhysics = false;
		} else if (rescaleMimo) {
			texturizer.changeScale(Mimo.maxRadius);
			rescaleMimo = false;
		} else if (showGUI) {
			gui.mouseReleased();
		}

	}

	/*
	 * public void mouseMoved() { if (underTheMouse != null)
	 * underTheMouse.moveTo(mouseX, mouseY); }
	 */

	public void mouseClicked() {
		if (!showGUI)
			return;
		else
			gui.clicked(app.mouseX, app.mouseY);
	}

	public void mouseDragged() {
		if (!showGUI)
			return;
		else
			gui.drag(app.mouseX, app.mouseY, app.mouseX - app.pmouseX,
					app.mouseY - app.pmouseY);
	}

	public void keyPressed() {
		if (app.key == ' ') {
			showGUI = !showGUI;
			// texturizer.image = !showGUI;
			if (showGUI)
				controlP5.show();
			else
				controlP5.hide();
		}
		if (app.key == 'o') {
			preview = !preview;
		}

	}

	public void controlEvent(ControlEvent cEvent) {
		if (cEvent == null)
			return;
		String crtlName = cEvent.name();
		if (crtlName == "Spring Strength") {
			float val = cEvent.value();
			springStrength = val / 100;
			updatePhysics = true;
			return;
		}
		if (crtlName == "Spring Damping") {
			float val = cEvent.value();
			springDamping = val / 1000;
			updatePhysics = true;
			return;
		}
		if (crtlName == "Gravity range: X") {
			float val = cEvent.value();
			gravX_Range = val / 1000;
			return;
		}
		if (crtlName == "Gravity range: Y") {
			float val = cEvent.value();
			gravY_Range = val / 1000;
			return;
		}
		if (crtlName == "Show Mimos") {
			float val = cEvent.value();
			showMimos = val > 0;
			return;
		}
		if (crtlName == "Show Springs") {
			float val = cEvent.value();
			showSprings = val > 0;
			return;
		}
		if (crtlName == "Show Organism") {
			float val = cEvent.value();
			showOrganism = val > 0;
			return;
		}
		if (crtlName == "Number of Mimos") {
			float val = cEvent.value();
			numMimos = (int) val;
			return;
		}

		// Texturizer controls
		if (crtlName == "Graphics") {
			float val = cEvent.group().value();
			texturizer.mode = (int) val;
			return;
		}

		if (crtlName == "Ancestor Texture") {
			float val = cEvent.group().value();
			texturizer.ancestor = (int) val;
			cEvent.group().close();
			return;
		}
		if (crtlName == "Mimo Texture") {
			float val = cEvent.group().value();
			texturizer.active = (int) val;
			cEvent.group().close();
			return;
		}
		if (crtlName == "Mimos' size") {
			Mimo.minRadius = cEvent.controller().arrayValue()[0];
			Mimo.maxRadius = cEvent.controller().arrayValue()[1];
			rescaleMimo = true;
			return;
		}

		// Weather control (let's play God)
		if (crtlName == "Use slider value") {
			weather.realTemperature = !weather.realTemperature;
			return;
		}
		if (crtlName == "Temperature") {
			float val = cEvent.value();
			weather.fakeTemperature = val;
			return;
		}
		if (crtlName == "Black to color") {
			RadialGradient.blackToColor = cEvent.value() > 0;
			return;
		}

		if (crtlName == "RESET") {
			app.noLoop();
			//TODO: Is this really necessary now that the thread issue is fixed?
			try {
				reset();
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				app.loop();
			}
			return;
		}
		if (crtlName == "START/PAUSE") {
			pause = !pause;
			return;
		}

	}
}
