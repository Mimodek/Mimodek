package mimodek;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;


import mimodek.texture.Texturizer;
import mimodek.tracking.TUIOClient;
import mimodek.tracking.TrackingSimulator;

import controlP5.ControlEvent;
import controlP5.ControlP5;
import controlP5.ListBox;
import controlP5.RadioButton;
import controlP5.Range;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;


public class MainHandler{	
	//Needed to go fullscreen
	public GraphicsDevice graphicsDevice;
	DisplayMode origDisplayMode;
	//private Frame mimodekFrame;


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
	boolean rescaleMimo = false;

	//the mimos (active/ancestor) manager
	MimosManager mimosManager;

	
	
	//the tracking module
	public TrackingSimulator trackingSimulator;//don't run it at the same time as real tracking input
	public TUIOClient tracking;
	
	// information toggles
	boolean showGUI = true;
	public static boolean showSprings = true;
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

	public MainHandler(int screenWidth, PApplet app) {
		MainHandler.app = app;
		MainHandler.screenWidth = screenWidth;
		//we want to have the same ratio as the output
		MainHandler.screenHeight = (int)(MainHandler.screenWidth*((float)outputHeight/(float)outputWidth));
		setup();
	}
	
	public MainHandler(int screenWidth,int screenHeight, PApplet app) {
		MainHandler.app = app;
		MainHandler.screenWidth = screenWidth;
		MainHandler.screenHeight = screenHeight;
		setup();
	}

	public void setup() {
		app.size(MainHandler.screenWidth, MainHandler.screenHeight);
		//to switch from screen preview to output
		scaleFactor = (float)outputWidth/(float)screenWidth;
		
		app.smooth();
		app.frameRate(24);
		PFont font = app.createFont("Verdana", 10, false);
		app.hint(PApplet.ENABLE_NATIVE_FONTS);
		app.textFont(font);

		//app = this;
		gfx = app.g;

		pSim = new Physics(0.2f, 0.1f, false);
		organism = new Organism();
		mimosManager = new MimosManager();
		
		tracking = new TUIOClient();
		trackingSimulator = new TrackingSimulator(screenWidth, screenHeight);
		tracking.setListener(mimosManager);
		trackingSimulator.setListener(mimosManager);
		texturizer = new Texturizer();
		setupGUI();
		//texturizer = new Texturizer();
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
		
		//TODO: This crash the tracking simulator... For now we'll have to restart 
		trackingSimulator.off();
		//trackingSimulator = new TrackingSimulator(screenWidth, screenHeight);
		trackingSimulator.on();
	}

	public void setupGUI() {

		controlP5 = new ControlP5(app);

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

		
		controlP5.addSlider("Number of Mimos", 0, 500, 100, controlPositionX,
				controlOffsetY + 200, controlWidth, controlHeight);
		// Reset Button
		controlP5.addButton("RESET", 0, controlPositionX, controlOffsetY + 220,
				100, 30);
		
		//Texturize panel
		ListBox listA = controlP5.addListBox("Ancestor Texture",controlPositionX,controlOffsetY + 300,120,120);
		ListBox listB = controlP5.addListBox("Mimo Texture",controlPositionX+160,controlOffsetY + 300,120,120);
		listA.valueLabel().style().marginTop = 1; // the +/- sign
		listB.valueLabel().style().marginTop = 1; // the +/- sign
		  //l.setBackgroundColor(color(100,0,0));
		  for(int i=0;i<texturizer.textures.size();i++) {
			  
			  listA.addItem(texturizer.textures.get(i).fileName,i);
			  listB.addItem(texturizer.textures.get(i).fileName,i);
		  }
		  listA.close();
		  listB.close();
		  
		RadioButton r =controlP5.addRadioButton("Graphics", 500, 100);
		r.setColorForeground(MainHandler.app.color(120));
		r.addItem("Circles", 1).setState(false);
		r.addItem("Image", 2).setState(true);
		r.addItem("Generated", 3).setState(false);
		
		controlP5.addRange("Mimos' size",0f,255, Mimo.minRadius,Mimo.maxRadius, 500,200,200,12);

		 // l.setColorBackground(color(255,128));
		  //l.setColorActive(color(0,0,255,128));


	}

	public void draw() {
		app.background(0);
		updateEnv();
		pSim.update();
		mimosManager.update();
		if(!preview){
			app.noFill();
			app.stroke(app.color(255));
			
			app.translate(outputOffsetX,outputOffsetY);
			
			app.scale(scaleFactor);
			app.rect(-1,-1,screenWidth,screenHeight);
		}
		/*if (underTheMouse != null)
			underTheMouse.draw();*/
		if (showOrganism)
			organism.draw();
		if (showMimos)
			mimosManager.draw();
		if (showSprings)
			pSim.drawSprings(app.color(0, 0, 255), 1);
		if (showGUI)
			showPhysicsData();
	}

	public void updateEnv() {
		gravY = -gravY_Range + PApplet.sin(app.noise(app.frameCount * 0.01f) * 2 * PApplet.PI)
				* gravY_Range * 2;
		gravX = -gravX_Range + PApplet.sin(app.noise(app.frameCount * 0.01f) * 2 * PApplet.PI)
				* gravX_Range * 2;
		pSim.setGravity(gravX, gravY);

	}

	public void showPhysicsData() {
		app.fill(0, 255, 0, 50);
		app.rect(15, controlOffsetY, 310, 250);
		app.fill(255);
		app.text("Frame rate (fps): " + (int) app.frameRate + " | Particles: "
				+ pSim.particleCount() + " | Springs: " + pSim.springCount(),
				30, 30 + controlOffsetY);
		app.text("Gravity (range): X[" + -gravX_Range + ";" + gravX_Range
				+ "] / Y[" + -gravY_Range + ";" + gravY_Range + "]", 30,
				60 + controlOffsetY);
		app.text("Spring strength: " + springStrength + " | Spring damping: "
				+ springDamping, 30, 110 + controlOffsetY);
		
		//Texture viewer
		app.fill(0, 255, 0, 50);
		app.rect(15, controlOffsetY + 270, 310, 110);
		
		app.fill(10, 10, 10, 255);
		app.rect(controlPositionX,controlOffsetY + 300, 120, 70);
		texturizer.drawTexture(texturizer.ancestor, controlPositionX+60,controlOffsetY + 335, 1);
		
		app.rect(controlPositionX+160,controlOffsetY + 300, 120, 70);
		texturizer.drawTexture(texturizer.active, controlPositionX+220,controlOffsetY + 335, 1);
	}

	public void mouseReleased() {
		if (updatePhysics) {
			pSim.changeSprings(springStrength, springDamping);
			updatePhysics = false;
		}else if (rescaleMimo) {
			texturizer.changeScale(Mimo.maxRadius);
			rescaleMimo = false;
		}
		
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
		if (app.key == ' ') {
			showGUI = !showGUI;
			//texturizer.image = !showGUI;
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
		
		//Texturizer controls
		if(crtlName == "Graphics"){
			float val = cEvent.group().value();
			texturizer.mode = (int)val;
			return;
		}
		
		if (crtlName == "Ancestor Texture") {
			float val = cEvent.group().value();
			texturizer.ancestor = (int)val;
			cEvent.group().close();
			return;
		}
		if (crtlName == "Mimo Texture") {
			float val = cEvent.group().value();
			texturizer.active = (int)val;
			cEvent.group().close();
			return;
		}
		if (crtlName == "Mimos' size") {
			Mimo.minRadius = cEvent.controller().arrayValue()[0];
			Mimo.maxRadius = cEvent.controller().arrayValue()[1];
			rescaleMimo = true;
			return;
		}
		
		if (crtlName == "RESET") {
			reset();
		}

	}
}
