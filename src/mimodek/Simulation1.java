package mimodek;

import java.applet.Applet;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import controlP5.ControlEvent;
import controlP5.ControlListener;
import controlP5.ControlP5;
import mimodek.tracking.simulation.RandomWalk;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PVector;

@SuppressWarnings("serial")
public class Simulation1 extends PApplet implements ControlListener {
	public static PGraphics gfx;
	public static PApplet app;
	public static int screenWidth;
	public static int screenHeight;

	public static Physics pSim;
	public static Organism organism;

	RandomWalk tracking;

	Mimo underTheMouse;

	// information toggles
	boolean showGUI = true;
	boolean showSprings = true;
	boolean showMimos = true;
	boolean showOrganism = true;

	// environment variables
	float gravX_Range = 0.01f;
	float gravY_Range = 0.02f;
	float gravY;
	float gravX;

	float springStrength = 0.5f;
	float springDamping = 0.01f;

	// number of particles generated
	int numMimos = 100;

	// controls
	ControlP5 controlP5;
	private boolean updatePhysics = false;
	int controlPositionX = 30;
	int controlOffsetY = 30;
	int controlWidth = 200;
	int controlHeight = 10;

	public Simulation1(int screenWidth, int screenHeight) {
		super();
		Simulation1.screenWidth = screenWidth;
		Simulation1.screenHeight = screenHeight;

	}

	public void setup() {
		// size(192,157);
		size(Simulation1.screenWidth, Simulation1.screenHeight);
		smooth();
		frameRate(24);
		PFont font = createFont("Verdana", 10, false);
		hint(ENABLE_NATIVE_FONTS);
		textFont(font);

		app = this;
		gfx = this.g;

		pSim = new Physics(0.2f, 0.1f, false);
		organism = new Organism();
		tracking = new RandomWalk(numMimos);
		setupGUI();
	}

	public void reset() {
		/*
		 * gravX_Range = 0.01f; gravY_Range = 0.02f;
		 * 
		 * springStrength = 0.5f; springDamping = 0.01f;
		 */
		pSim = new Physics(0.2f, 0.1f, false);

		organism = new Organism();
		tracking = new RandomWalk(numMimos);
	}

	public void setupGUI() {

		controlP5 = new ControlP5(this);

		// Gravity control
		controlP5.addSlider("Gravity range: X", 0, 100, 2, controlPositionX,
				controlOffsetY + 65, controlWidth, controlHeight);
		controlP5.addSlider("Gravity range: Y", 0, 100, 50, controlPositionX,
				controlOffsetY + 80, controlWidth, controlHeight);

		// Spring control
		controlP5.addSlider("Spring Strength", 0, 200, 50, controlPositionX,
				controlOffsetY + 115, controlWidth, controlHeight);
		controlP5.addSlider("Spring Damping", 0, 500, 1, controlPositionX,
				controlOffsetY + 130, controlWidth, controlHeight);

		// Display control
		controlP5.addToggle("Show Mimos", true, controlPositionX,
				controlOffsetY + 160, 10, 10);
		controlP5.addToggle("Show Springs", true, 120, controlOffsetY + 160,
				10, 10);
		controlP5.addToggle("Show Organism", true, 200, controlOffsetY + 160,
				10, 10);

		// Reset Button
		controlP5.addSlider("Number of Mimos", 0, 500, 100, controlPositionX,
				controlOffsetY + 200, controlWidth, controlHeight);
		controlP5.addButton("RESET", 0, controlPositionX, controlOffsetY + 220,
				100, 30);

	}

	public void draw() {
		background(0);
		updateEnv();
		pSim.update();
		tracking.update();

		if (underTheMouse != null)
			underTheMouse.draw();
		if (showOrganism)
			organism.draw();
		if (showMimos)
			tracking.draw();
		if (showGUI)
			showPhysicsData();
		if (showSprings)
			pSim.drawsprings(color(0, 0, 255), 1);
	}

	public void updateEnv() {
		gravY = -gravY_Range + sin(noise(frameCount * 0.01f) * 2 * PI)
				* gravY_Range * 2;
		gravX = -gravX_Range + sin(noise(frameCount * 0.01f) * 2 * PI)
				* gravX_Range * 2;
		pSim.setGravity(gravX, gravY);

	}

	public void showPhysicsData() {
		fill(0, 255, 0, 50);
		rect(15, controlOffsetY, 310, 250);
		fill(255);
		text("Frame rate (fps): " + (int) frameRate + " | Particles: "
				+ pSim.particleCount() + " | Springs: " + pSim.springCount(),
				30, 30 + controlOffsetY);
		text("Gravity (range): X[" + -gravX_Range + ";" + gravX_Range
				+ "] / Y[" + -gravY_Range + ";" + gravY_Range + "]", 30,
				60 + controlOffsetY);
		text("Spring strength: " + springStrength + " | Spring damping: "
				+ springDamping, 30, 110 + controlOffsetY);
	}

	public void showFrameRate() {
		fill(255);

	}

	public void mouseReleased() {
		if (updatePhysics) {
			pSim.changeSprings(springStrength, springDamping);
		}
		updatePhysics = false;
	}

	public void mouseMoved() {
		if (underTheMouse != null)
			underTheMouse.moveTo(mouseX, mouseY);
	}

	public void mouseClicked() {
		if (showGUI)
			return;

		if (underTheMouse != null) {
			// organism.attachTo();
			tracking.addMimo(underTheMouse.pos);
			underTheMouse = null;
		} else {
			underTheMouse = new Mimo(new PVector(mouseX, mouseY));
		}
	}

	public void keyPressed() {
		if (key == ' ') {
			showGUI = !showGUI;
			if (showGUI)
				controlP5.show();
			else
				controlP5.hide();
		}
	}

	public void controlEvent(ControlEvent cEvent) {
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
		if (crtlName == "RESET") {
			reset();
		}

	}

	/*
	 * This allows the applet to run as a Java app
	 */
	public static void main(String args[]) {
		// Create the frame this applet will run in
		Frame appletFrame = new Frame(
				"Mimodek : Structure Construction Simulator");
		
		//force the app to quit when window is closed
		appletFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				System.exit(0);
			}
		});
		// The frame needs a layout manager, use the GridLayout to maximize
		// the applet size to the frame.
		appletFrame.setLayout(new GridLayout(1, 0));

		// Create an instance of the applet
		Applet myApplet = new Simulation1(1000, 800);

		// Have to give the frame a size before it is visible
		appletFrame.setSize(Simulation1.screenWidth, Simulation1.screenHeight);

		// Make the frame appear on the screen. You should make the frame appear
		// before you call the applet's init method. On some Java
		// implementations,
		// some of the graphics information is not available until there is a
		// frame.
		// If your applet uses certain graphics functions like getGraphics() in
		// the
		// init method, it may fail unless there is a frame already created and
		// showing.
		appletFrame.setVisible(true);

		// Add the applet to the frame
		appletFrame.add(myApplet);

		// Initialize and start the applet
		myApplet.init();
		myApplet.start();

	}

}
