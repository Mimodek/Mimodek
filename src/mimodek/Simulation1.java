package mimodek;

import java.applet.Applet;
import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import mimodek.texture.Texturizer;
import mimodek.tracking.Tracking;

import controlP5.ControlEvent;
import controlP5.ControlListener;
import controlP5.ControlP5;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;


@SuppressWarnings("serial")
public class Simulation1 extends PApplet implements ControlListener {	
	//Needed to go fullscreen
	public GraphicsDevice graphicsDevice;
	DisplayMode origDisplayMode;
	private Frame mimodekFrame;
	boolean fullScreen  = false;

	public static PGraphics gfx;
	public static PApplet app;
	public static int screenWidth;
	public static int screenHeight;

	//physics simulator
	public static Physics pSim;
	
	//the organism
	public static Organism organism;
	
	//texture handler
	public static Texturizer texturizer;

	//the mimos (active/ancestor) manager
	MimosManager mimosManager;

	
	
	//the tracking module
	private Tracking tracking;

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
	
	// output resolution
	public static int outputWidth = 192;
	public static int outputHeight = 157;
	float scaleFactor;
	//this is for translating to the right spot when showing on the output
	int outputOffsetX = 40;
	int outputOffsetY = 40;
	
	boolean preview = true;
	

	// controls
	ControlP5 controlP5;
	private boolean updatePhysics = false;
	int controlPositionX = 30;
	int controlOffsetY = 30;
	int controlWidth = 200;
	int controlHeight = 10;
	
	
	//TODO: Remove?
	//Mimo underTheMouse;

	public Simulation1(int screenWidth) {
		super();
		Simulation1.screenWidth = screenWidth;
		//we want to have the same ratio as the output
		Simulation1.screenHeight = (int)(Simulation1.screenWidth*((float)outputHeight/(float)outputWidth));
	}
	
	public Simulation1(int screenWidth,int screenHeight) {
		super();
		Simulation1.screenWidth = screenWidth;
		Simulation1.screenHeight = screenHeight;
	}

	public void setup() {
		size(Simulation1.screenWidth, Simulation1.screenHeight);
		//to switch from screen preview to output
		scaleFactor = (float)outputWidth/(float)screenWidth;
		
		smooth();
		frameRate(24);
		PFont font = createFont("Verdana", 10, false);
		hint(ENABLE_NATIVE_FONTS);
		textFont(font);

		app = this;
		gfx = this.g;

		pSim = new Physics(0.2f, 0.1f, false);
		organism = new Organism();
		mimosManager = new MimosManager();
		
		tracking = new Tracking(screenWidth, screenHeight);
		tracking.setListener(mimosManager);
		setupGUI();
		texturizer = new Texturizer();
		tracking.start();
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
		tracking.running = false;
		tracking = new Tracking(screenWidth, screenHeight);
		tracking.setListener(mimosManager);
		tracking.start();
	}
	
	//only do that is full screen is supported 
	public void setGraphicsDevice(GraphicsDevice graphicsDevice){
		if(!graphicsDevice.isFullScreenSupported()){
			System.out.println("Full-screen mode not supported for device "+graphicsDevice);
			return;
		}
		System.out.println("*** Full screen mode available, press f to toggle on/off. ***");
		this.graphicsDevice = graphicsDevice;
		//store the original display mode so we can go back to it
		origDisplayMode = graphicsDevice.getDisplayMode();
	}
	
	public boolean enterFullScreen(boolean on){
		if(graphicsDevice == null)
			return false;
		//pause the animation
		noLoop();
		if(on){
			
			//mimodekFrame.setVisible(false);
			//mimodekFrame.removeNotify();
			mimodekFrame.dispose();
			mimodekFrame.setUndecorated(true);
			graphicsDevice.setFullScreenWindow(mimodekFrame);
			//mimodekFrame.validate();
			//mimodekFrame.addNotify();
			//mimodekFrame.setVisible(true);
			background(0);
			//restart the animation
			loop();
			return true;
		}else{
			//graphicsDevice.setDisplayMode(origDisplayMode);
			mimodekFrame.dispose();
			mimodekFrame.setUndecorated(false);
			graphicsDevice.setFullScreenWindow(null);
			
			
			//mimodekFrame.setVisible(false);
			;
			mimodekFrame.setSize(screenWidth, screenHeight);
			//mimodekFrame.validate();
			mimodekFrame.setVisible(true);
			loop();
			return false;
		}

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
		texturizer.loadTextures("images/");
		background(0);
		updateEnv();
		pSim.update();
		mimosManager.update();
		if(!preview){
			translate(outputOffsetX,outputOffsetY);
			scale(scaleFactor);
		}
		/*if (underTheMouse != null)
			underTheMouse.draw();*/
		if (showOrganism)
			organism.draw();
		if (showMimos)
			mimosManager.draw();
		if (showGUI)
			showPhysicsData();
		if (showSprings)
			pSim.drawSprings(color(0, 0, 255), 1);
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

	/*public void mouseMoved() {
		if (underTheMouse != null)
			underTheMouse.moveTo(mouseX, mouseY);
	}*/

	/*public void mouseClicked() {
		if (showGUI)
			return;

		if (underTheMouse != null) {
			// organism.attachTo();
			//mimosManager.addMimo(underTheMouse.pos);
			underTheMouse = null;
		} else {
			underTheMouse = new Mimo(new PVector(mouseX, mouseY));
		}
	}*/

	public void keyPressed() {
		if (key == ' ') {
			showGUI = !showGUI;
			texturizer.image = !showGUI;
			if (showGUI)
				controlP5.show();
			else
				controlP5.hide();
		}
		if (key == 'o') {
			preview = !preview;
		}
		if (key == 'f') {
			fullScreen = enterFullScreen(!fullScreen);
			println("Fullscreen: "+fullScreen);
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

	public void setParentFrame(Frame appletFrame) {
		this.mimodekFrame = appletFrame;
		
	}
}
