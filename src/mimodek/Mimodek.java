package mimodek;

import mimodek.configuration.Configurator;
import mimodek.controls.ActiveMimoGUI;
import mimodek.controls.GUI;
import mimodek.controls.PhysicsGUI;
import mimodek.controls.StyleGUI;
import mimodek.controls.TrackingGUI;
import mimodek.controls.WeatherGUI;
import mimodek.texture.Texturizer;
import mimodek.tracking.TUIOClient;
import mimodek.tracking.TrackingSimulator;
import mimodek.web.Weather;

import controlP5.ControlEvent;
import controlP5.ControlP5;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class Mimodek {
	// The fachade screen dimensions
	public static final int outputWidth = 192;
	public static final int outputHeight = 157;
	public static final int topPanelHeight = 16;
	public static final int topPanelWidth = 12;

	// Constant for each edge
	public static final int TOP = 0;
	public static final int RIGHT = 1;
	public static final int BOTTOM = 2;
	public static final int LEFT = 3;

	/*
	 * AVAILABLE EVERYWHERE - should be only read outside of this class
	 */
	// The mimodek object itself
	public static Mimodek mimodek;
	// Configuration/settings
	public static Configurator config;
	// Graphics
	public static PApplet gfx;
	public static PApplet app;
	public static int screenWidth;
	public static int screenHeight;

	// physics simulator
	public static Physics pSim;
	// the organism
	public static Organism organism;
	// texture handler
	public static Texturizer texturizer;
	// the mimos (active/ancestor) manager
	public static MimosManager mimosManager;
	// TUIO client
	public static TUIOClient tracking;
	// Weather Data
	public static Weather weather;
	// controls
	public static ControlP5 controlP5;

	public static int temperatureColor = 0;
	public static boolean verbose = true;

	// Graphic User Interface Handler
	GUI gui;

	// number of particles generated
	int numMimos = 100;

	// this is for translating to the right spot when showing on the output
	int outputOffsetX = 40;
	int outputOffsetY = 40;

	// PRIVATE
	float scaleFactor;
	private boolean updatePhysics = false;
	private boolean rescaleMimo = false;
	// don't run it at the same time as real TUIO client
	private TrackingSimulator trackingSimulator;
	private boolean ready = false;

	int controlPositionX = 30;
	int controlOffsetY = 30;
	int controlWidth = 200;
	int controlHeight = 10;

	public Mimodek(PApplet app) {
		Mimodek.app = app;
		app.registerSize(this);
		app.registerDispose(this);
		// Mimodek.app.registerPre(this);
		app.registerDraw(this);
		mimodek = this;
		PApplet.println("MIMODEk says > Hi! Wait a moment I'm setting up...");
		prepare();
	}

	public void dispose() {
		// anything in here will be called automatically when
		// the parent applet shuts down. for instance, this might
		// shut down a thread used by this library.
		// note that this currently has issues, see bug #183
		// http://dev.processing.org/bugs/show_bug.cgi?id=183
		if (trackingSimulator != null && trackingSimulator.running)
			trackingSimulator.off();
		weather.running = false;
		PApplet.println("MIMODEk says > Bye bye!");
	}

	/* Called when the applet sets it size() */
	public void size(int width, int height) {
		try {
			System.out.println("MIMODEk says > I will run in " + width + " X "
					+ height + ".");
			screenWidth = width;
			screenHeight = (int) (Mimodek.screenWidth * ((float) outputHeight / (float) outputWidth));
			// to switch from screen preview to output
			scaleFactor = (float) outputWidth / (float) screenWidth;
			// the rendering context - could be an offscreen buffer also...
			gfx = app;
			PApplet.println("MIMODEk says > MIMODEK ready to run!");
			ready = true; // told you!
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void prepare() {

		// create a configurator object, create this first so other class can
		// register settings
		config = new Configurator();

		// simulation
		pSim = new Physics(0.2f, 0.1f, false);

		// organism
		organism = new Organism();

		// animation
		mimosManager = new MimosManager();

		// weather data
		weather = new Weather("MimodekColourRanges.xml");
		new Thread(weather).start();

		// tracking
		tracking = new TUIOClient();
		tracking.setListener(mimosManager);
		trackingSimulator = new TrackingSimulator(screenWidth, screenHeight);

		texturizer = new Texturizer();

		// load configuration from XML
		config.loadFromFile("config.xml");

		setupGUI();
	}

	public void reset() {
		pSim = new Physics(0.2f, 0.1f, false);
		organism = new Organism();
		mimosManager = new MimosManager();
	}

	// Return true if the coordinate of the vector are in the screen
	public boolean isInTheScreen(PVector coordinate, float edgeDetection) {
		if (coordinate.x < edgeDetection
				|| coordinate.x > screenWidth - edgeDetection
				|| coordinate.y > screenHeight - edgeDetection
				|| coordinate.y < edgeDetection) {
			return false;
		}
		float scaledPanelHeight = (1.0f/scaleFactor) * topPanelHeight;
		// are we on the top part of the screen?
		if (coordinate.y > scaledPanelHeight * 2) {
			// no
			return true;
		}
		// yes
		// special test for the top part of the screen
		float scaledPanelWidth = (1.0f/scaleFactor) * topPanelWidth;
		float halfWidth = screenWidth / 2;
		if (coordinate.y < scaledPanelHeight) {
			float distance = scaledPanelWidth * 2;
			float leftEdge = halfWidth - distance + edgeDetection;
			float rightEdge = halfWidth + distance - edgeDetection;
			if ((coordinate.x < leftEdge) || (coordinate.x > rightEdge)) {
				return false;
			}
		} else {
			float distance = scaledPanelWidth * 2;
			float leftEdge = halfWidth - distance + edgeDetection;
			float rightEdge = halfWidth + distance - edgeDetection;
			if ((coordinate.x < leftEdge) || (coordinate.x > rightEdge)) {
				return false;
			}

		}
		return true;
	}
	
	public boolean isInTheScreen(float x, float y, float edgeDetection) {
		return isInTheScreen(new PVector(x,y),edgeDetection);
	}

	// return which edge is the closest to this coordinate
	public int getClosestEdge(PVector coordinate) {
		// Which edge are we seeding on?
		boolean left = false;
		boolean up = false;
		if (coordinate.x < Mimodek.screenWidth / 2) {
			// Left part of the screen
			left = true;
		}
		if (coordinate.y < Mimodek.screenHeight / 2) {
			// Upper part of the screen
			up = true;
		}
		float hDist = PApplet.abs((Mimodek.screenWidth / 2) - coordinate.x)
				/ (Mimodek.screenWidth / 2);
		float vDist = PApplet.abs((Mimodek.screenHeight / 2) - coordinate.y)
				/ (Mimodek.screenHeight / 2);
		if (left && (hDist > vDist)) {
			// closer to the left edge
			return LEFT;
		}
		if (!left && (hDist > vDist)) {
			// closer to the right edge
			return RIGHT;
		}
		// from this point on it's either top or bottom
		return up ? TOP : BOTTOM;
	}

	public void restartTrackingSimulator() {
		if (trackingSimulator.running) {
			trackingSimulator.off();
		}
		trackingSimulator = new TrackingSimulator(screenWidth, screenHeight);
		trackingSimulator.setListener(mimosManager);
		trackingSimulator.on();
	}

	public void draw() {
		if (!ready)
			return;
		// app.background(0);
		if (!config.getBooleanSetting("pause")) {
			pSim.update();
			mimosManager.update();
		}
		// scale down if not in preview mode
		app.pushMatrix();
		if (!config.getBooleanSetting("preview")) {
			app.noFill();
			app.stroke(app.color(255));

			app.translate(outputOffsetX, outputOffsetY);
			app.rect(-1, -1, outputWidth + 1, outputHeight + 1);
			app.scale(scaleFactor);
		}

		if (config.getBooleanSetting("showOrganism")) {
			organism.draw();
		}
		if (config.getBooleanSetting("showActiveMimos")) {
			mimosManager.draw();
		}
		if (config.getBooleanSetting("showSprings")) {
			pSim.drawSprings(app.color(0, 0, 100), 1);
		}
		app.popMatrix();

		if (config.getBooleanSetting("showGUI"))
			gui.draw();
	}

	public void mouseReleased() {
		if (updatePhysics) {
			pSim.changeSprings(config.getFloatSetting("springStrength"), config
					.getFloatSetting("springDamping"));
			updatePhysics = false;
		} else if (rescaleMimo) {
			texturizer.changeScale(Mimodek.config
					.getFloatSetting("mimosMaxRadius"));
			rescaleMimo = false;
		} else if (config.getBooleanSetting("showGUI")) {
			gui.mouseReleased();
		}
	}

	public void mouseClicked() {
		if (!config.getBooleanSetting("showGUI"))
			return;
		else
			gui.clicked(app.mouseX, app.mouseY);
	}

	public void mouseDragged() {
		if (!config.getBooleanSetting("showGUI"))
			return;
		else
			gui.drag(app.mouseX, app.mouseY, app.mouseX - app.pmouseX,
					app.mouseY - app.pmouseY);
	}

	public void keyPressed() {
		if (app.key == ' ') {
			config.setSetting("showGUI", !config.getBooleanSetting("showGUI"));
			// texturizer.image = !showGUI;
			if (config.getBooleanSetting("showGUI"))
				controlP5.show();
			else
				controlP5.hide();
		}
		if (app.key == 'o') {
			config.setSetting("preview", !config.getBooleanSetting("preview"));
		}
		if (app.key == 's') {
			config.saveToFile("config.xml");
			System.out.println("Settings saved in /data/config/config.xml");
		}
		if (app.key == 'l') {
			config.loadFromFile("config.xml");
			try {
				gui.reset();
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("Settings loaded from /data/config/config.xml");

		}

	}

	public void setupGUI() {
		controlP5 = new ControlP5(app);
		gui = new GUI();
		gui.addModule(new PhysicsGUI(350, controlOffsetY + 270, 310, 250));
		gui.addModule(new StyleGUI(15, controlOffsetY + 270, 310, 250));
		gui.addModule(new WeatherGUI(350, controlOffsetY, 310, 250));
		gui.addModule(new TrackingGUI(700, controlOffsetY, 310, 250));
		gui.addModule(new ActiveMimoGUI(700, controlOffsetY + 270, 310, 250));
	}

	public void controlEvent(ControlEvent cEvent) {
		if (cEvent == null)
			return;
		String crtlName = cEvent.name();
		if (crtlName == "Spring Strength") {
			float val = cEvent.value();
			config.setSetting("springStrength", val / 100);
			updatePhysics = true;
			return;
		}
		if (crtlName == "Spring Damping") {
			float val = cEvent.value();
			config.setSetting("springDamping", val / 1000);
			updatePhysics = true;
			return;
		}
		if (crtlName == "Gravity range: X") {
			float val = cEvent.value();
			config.setSetting("gravX_Range", val / 1000);
			return;
		}
		if (crtlName == "Gravity range: Y") {
			float val = cEvent.value();
			config.setSetting("gravY_Range", val / 1000);
			return;
		}
		if (crtlName == "Show Mimos") {
			float val = cEvent.value();
			config.setSetting("showMimos", val > 0);
			return;
		}
		if (crtlName == "Show Springs") {
			float val = cEvent.value();
			config.setSetting("showSprings", val > 0);
			return;
		}
		if (crtlName == "Show Organism") {
			float val = cEvent.value();
			config.setSetting("showOrganism", val > 0);
			return;
		}
		if (crtlName == "Number of Mimos") {
			float val = cEvent.value();
			config.setSetting("maxCells", (int) val);
			return;
		}

		// Texturizer controls
		if (crtlName == "Graphics") {
			float val = cEvent.group().value();
			config.setSetting("textureMode", (int) val);
			return;
		}
		if (crtlName == "Ancestor Texture") {
			float val = cEvent.group().value();
			config.setSetting("ancestorTexture", (int) val);
			cEvent.group().close();
			return;
		}
		if (crtlName == "Mimo Texture") {
			float val = cEvent.group().value();
			config.setSetting("activeTexture", (int) val);
			cEvent.group().close();
			return;
		}
		if (crtlName == "Mimos' size") {
			Mimodek.config.setSetting("mimosMinRadius", cEvent.controller()
					.arrayValue()[0]);
			Mimodek.config.setSetting("mimosMaxRadius", cEvent.controller()
					.arrayValue()[1]);
			rescaleMimo = true;
			return;
		}
		if (crtlName == "Seed") {
			config.setSetting("drawCircle", cEvent.value() == 0);
			return;
		}
		if (crtlName == "Dots size") {
			config.setSetting("dotSize", cEvent.value());
			return;
		}
		if (crtlName == "Space between dots") {
			config.setSetting("radiScale", cEvent.value());
			return;
		}
		if (crtlName == "Ancestors' brightness") {
			config.setSetting("ancestorBrightness", (int) cEvent.value());
			return;
		}

		// Weather control (let's play God)
		if (crtlName == "Use slider value") {
			weather.realTemperature = !weather.realTemperature;
			return;
		}
		/*if (crtlName == "Temperature") {
			float val = cEvent.value();
			weather.setTemperature(val);
			return;
		}*/
		if (crtlName == "Black to color") {
			config.setSetting("blackToColor", cEvent.value() > 0);
			return;
		}
		if (crtlName == "Gradient") {
			float val = cEvent.group().value();
			config.setSetting("gradientFunction", (int) val);
			return;
		}

		// Tracking controls
		if (crtlName == "Tracking ON/OFF") {
			config.setSetting("trackingOn", cEvent.value() > 0);
			return;
		}
		if (crtlName == "Simulator ON/OFF") {
			trackingSimulator.paused = cEvent.value() > 0;
			return;
		}
		if (crtlName == "Restart simulator") {
			if (mimosManager != null)
				restartTrackingSimulator();
			return;
		}
		if (crtlName == "Distance threshold") {
			config.setSetting("blobDistanceThreshold", cEvent.value());
			return;
		}
		if (crtlName == "Edge zone size") {
			config.setSetting("edgeDetection", cEvent.value());
			return;
		}

		// Active mimo
		if (crtlName == "Easing") {
			config.setSetting("mimosEasing", cEvent.value());
			return;
		}
		if (crtlName == "Ancestor Scale") {
			config.setSetting("ancestorScale", cEvent.value());
			return;
		}
		if (crtlName == "Minimum active time (seconds)") {
			config.setSetting("mimosMinLifeTime", (int) cEvent.value());
			return;
		}

		if (crtlName == "Smoothing") {
			if (cEvent.value() > 0)
				gfx.smooth();
			else
				gfx.noSmooth();
			return;
		}

		if (crtlName == "RESET") {
			app.noLoop();
			boolean tr = config.getBooleanSetting("trackingOn");
			if (tr) {
				// if tracking is on, stop it
				config.setSetting("trackingOn", false);
			}
			reset();
			if (tr) {
				// if tracking was on, start it
				config.setSetting("trackingOn", true);
			}
			app.loop();
			return;
		}
		if (crtlName == "START/PAUSE") {
			config.setSetting("pause", !config.getBooleanSetting("pause"));
			return;
		}

	}
}
