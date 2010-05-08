package mimodek;

import java.awt.event.KeyEvent;

import controlP5.ControlEvent;
import controlP5.ControlListener;

import mimodek.configuration.Colors;
import mimodek.configuration.Configurator;
import mimodek.data.DataHandler;
import mimodek.decorator.graphics.TextureCollection;
//import mimodek.controls.GUI;
import mimodek.facade.FacadeFactory;
import mimodek.utils.Verbose;

import p5wp.XMLReceiver;
import processing.core.PApplet;

/**
 * Main class for Mimodek.
 * Handles the initialization of environment, events from mouse and keyboard, events from the parent sketch.
 * It basically wraps the details of Mimodek in a single object that the parent sketch can communicate with.
 * @author Jonsku
 */
public class Mimodek implements ControlListener {

	/**
	 * A reference to the parent Processing sketch
	 */
	public PApplet app;

	/**
	 * The mimos (active/ancestor/DLA) manager
	 */
	public MimosManager mimosManager;

	/**
	 * 
	 */
	public DataHandler dataHandler;
	
	/**
	 * If true, a PNG file of Mimodek will be saved to document the growth of
	 * the organism
	 */
	public boolean takeSnapShot = false;

	/**
	 * If true, makes Mimodek talkative
	 */
	public static boolean verbose = false;
	
	/**
	 * If true, saves the configuration of Mimodek when the app is closed.
	 */
	private boolean saveOnQuit = false;

	/**
	 * True when Mimodek is ready to run
	 */
	private boolean ready = false;

	/**
	 * Incremental counter of shots taken. Used in the names of the PNG files
	 */
	int shotCounter = 0;

	/**
	 * If true, the growth of the organism will be documented as a series of
	 * images
	 */
	private boolean film = false;

	/**
	 * Initializes Mimodek and register it with the parent sketch event handlers
	 * 
	 * @param app
	 *            The parent sketch
	 */
	public Mimodek(PApplet app) {
		this.app = app;
		app.registerDispose(this);
		app.registerPost(this);
		app.registerDraw(this);
		app.registerKeyEvent(this);
		Verbose.speak = verbose;
		Verbose.say("Hi! Wait a moment I'm setting up...");
	}

	/**
	 * Anything in here will be called automatically when the parent applet
	 * shuts down. for instance, this might shut down a thread used by this
	 * library. note that this currently has issues, see bug #183
	 * http://dev.processing.org/bugs/show_bug.cgi?id=183
	 * 
	 */
	public void dispose() {
		if(saveOnQuit)
			Configurator.saveToFile("Configurator.xml");
		Verbose.say("Bye bye!");
	}

	/**
	 * Called when the applet sets it size(). Apparently there is a bug in
	 * Processing core and this method is not called automatically, thus for now
	 * the parent sketch should this method in its setup() method after a call
	 * to size().
	 * 
	 * @param width
	 * @param height
	 */
	public void size(int width, int height) {
		try {
			Verbose.say("I will run in " + width + " X "
					+ height + ".");

			prepare(); // do some more initialization

			Verbose.say("MIMODEK ready to run!");
			ready = true; // told you!
		} catch (Exception e) {
			e.printStackTrace(); // useful for debugging, but no really
									// necessary
		}
	}

	/**
	 * Initialize Mimodek system. This is where any
	 */
	private void prepare() {
		FacadeFactory.createPradoFacade(app); // here we select which type of
												// facade Mimodek will be
												// displayed on. (singleton
												// pattern)
		// initialize a Configurator object, create this first so other classes
		// can
		// register/get/modify settings (singleton)
		Configurator.createConfigurator(app);
		// load configuration from XML
		Configurator.loadFromFile("Configurator.xml");
		
		// Creates an empty Mimodek
		Colors activeMimosColors = new Colors(app);
		try {
			activeMimosColors.loadFromCSV("ActiveMimosColor.txt", app);
		} catch (Exception e) {
			Verbose.overRule(e.getMessage());
		}finally{
			mimosManager = new MimosManager(this,activeMimosColors);
		}
		TextureCollection.loadTextures(app);
		
		
		Colors tempColors = new Colors(app);
		try {
			tempColors.loadFromXML("MimodekColourRanges.xml",app);
		} catch (Exception e) {
			Verbose.overRule(e.getMessage());
		}finally{
		// define the range of values to map thes colros to
		tempColors.getColorFromRange(-10);
		tempColors.getColorFromRange(40);
		}
		dataHandler = new DataHandler(tempColors,  new XMLReceiver(app, "http://goldenapple.es/chamanismohorizontal/"));
	}

	

	/**
	 * Called by the GUI module when events of controllers occurs.
	 * 
	 * @see controlP5.ControlListener#controlEvent(controlP5.ControlEvent)
	 */
	public void controlEvent(ControlEvent cEvent) {
		if (cEvent == null)
			return;
		String crtlName = cEvent.name();

		if (crtlName == "RESET") {
			app.noLoop();
			boolean tr = Configurator.getBooleanSetting("trackingOn");
			if (tr) {
				// if tracking is on, stop it
				Configurator.setSetting("trackingOn", false);
			}
			reset();
			if (tr) {
				// if tracking was on, start it
				Configurator.setSetting("trackingOn", true);
			}
			app.loop();
			return;
		}

	}

	/**
	 * Call this method to reset Mimodek without quitting the program. WARNING: Every Mimos and the DLA will be lost!!
	 */
	public void reset() {
		Colors c = mimosManager.activeMimosColors;
		mimosManager = new MimosManager(this,c);
	}

	/**
	 * Called after the parent sketch draw method.
	 * Note that it is possible to also do some drawing there, Mimodek won't erase anything but only draw over existing drawings.
	 */
	public void draw() {
		if (!ready)
			return;
		if (!Configurator.getBooleanSetting("pause")) {
			mimosManager.update();
		}
		app.pushMatrix();
		app.translate(FacadeFactory.getFacade().leftOffset, FacadeFactory
				.getFacade().topOffset);
		if (Configurator.getBooleanSetting("showOrganism")) {
			// organism.draw();
		}
		if (Configurator.getBooleanSetting("showActiveMimos")) {
			mimosManager.draw();
		}
		app.popMatrix();
	}

	/**
	 * Called after the sketch is done drawing the frame.
	 * If needed, takes a snapshot of Mimodek and saves it in a PNG file
	 */
	public void post() {
		if (film && takeSnapShot) {
			app.save("mimodek_" + (shotCounter++) + ".png");
		}
		takeSnapShot = false;
	}

	

	/**
	 * Key event handler.
	 * <table>
	 * <tr><th>Key</th><th>Effect</th><tr>
	 * <tr><td>s</td><td>Save current configuration to /data/Configurator/Configurator.xml</td></tr>
	 * <tr><td>l</td><td>Load configuration from /data/Configurator/Configurator.xml</td></tr>
	 * <tr><td>j</td><td>Save DLA to mimodek.xml</td></tr>
	 * <tr><td>k</td><td>Load DLA from mimodek.xml</td></tr>
	 * <tr><td>f</td><td>Toggle image documentation on/off</td></tr>
	 * </table>
	 * 
	 * @param e
	 */
	public void keyEvent(KeyEvent e) {
		if (e.getID() != KeyEvent.KEY_PRESSED)
			return;
		
		if (app.key == 's') {
			Configurator.saveToFile("Configurator.xml");
			Verbose.say("Settings saved in /data/Configurator/Configurator.xml");
		}
		if (app.key == 'l') {
			Configurator.loadFromFile("Configurator.xml");
			try {
				//GUI.reset();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			Verbose.say("Settings loaded from /data/Configurator/Configurator.xml");

		}
		if (app.key == 'j') {
			// organism.saveToFile("mimodek.xml");
		}
		if (app.key == 'k') {
			// organism.loadFromFile("mimodek.xml");
		}
		if (app.key == 'f') {
			/*
			film = !film;
			System.out.println("Recording: " + film);
			*/
			mimosManager.exportXML();
		}

	}

}
