package mimodek.controls;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Hashtable;

import mimodek.configuration.Colors;
import mimodek.configuration.Configurator;
import mimodek.facade.FacadeEventListener;
import mimodek.utils.Verbose;

import processing.core.PApplet;

import controlP5.ControlEvent;
import controlP5.ControlGroup;
import controlP5.ControlListener;
import controlP5.ControlP5;
import controlP5.Controller;

public class GUI implements ControlListener, FacadeEventListener {

	protected ArrayList<GUIModule> modules;
	protected GUIModule dragged;
	protected Hashtable<String, ControlListener> eventHandlers;
	public PApplet app;
	protected static GUI gui = null;
	public ControlP5 controlP5;
	public static Colors temperatureColors;

	protected GUI(PApplet app) {
		this.app = app;
		controlP5 = new ControlP5(app);
		app.registerDraw(this);
		app.registerMouseEvent(this);
		app.registerKeyEvent(this);
		modules = new ArrayList<GUIModule>();
		eventHandlers = new Hashtable<String, ControlListener>();
		
	}

	public static GUI createGui(PApplet app, Colors temperatureColors) {
		if (gui == null)
			gui = new GUI(app);
		int controlOffsetY = 30;
		// create some GUI modules and add them to the GUI manager
		// GUI.addModule(new PhysicsGUI(350, controlOffsetY + 270, 310, 250));
		GUI.addModule(new StyleGUI(15, controlOffsetY + 270, 310, 250));
		GUI.addModule(new DataGUI(350, controlOffsetY, 310, 250));
		//GUI.addModule(new TrackingGUI(700, controlOffsetY, 310, 250));
		GUI.addModule(new ActiveMimoGUI(700, controlOffsetY + 270, 310, 250));
		GUI.temperatureColors = temperatureColors;
		return gui;
	}
	
	public static GUI gui() {
		return gui;
	}

	public static void addModule(GUIModule mod) {
		gui.addMod(mod);
	}

	protected void addMod(GUIModule mod) {
		modules.add(mod);
	}

	public static void registerEventHandler(String event,
			ControlListener handler) {
		gui.regEvtHandler(event, handler);
	}

	protected void regEvtHandler(String event, ControlListener handler) {
		eventHandlers.put(event, handler);
		/*
		try {
			controlP5.controller(event).addListener(this);
			Controller c = controlP5.controller(event);
			if (c != null) {
				controlP5.group(event).addListener(this);
			} else {
				c.addListener(this);
			}
		} catch (Exception e) {
			Verbose.debug("Unknown event: " + event);
		}
		*/
	}

	public static void setBoxStyle() {
		gui.setBoxSt();
	}

	protected void setBoxSt() {
		app.fill(0, 255, 0, 50);
		app.noStroke();
	}

	public static void setHandleStyle() {
		gui.setHandleSt();
	}

	protected void setHandleSt() {
		app.fill(0, 0, 255);
		app.stroke(255);
	}

	public static void setTextStyle() {
		gui.setTextSt();
	}

	protected void setTextSt() {
		app.fill(255);
	}

	public void draw() {
		if (!Configurator.getBooleanSetting("showGUI"))
			return;
		app.pushStyle();
		for (int i = -1; ++i < modules.size();)
			modules.get(i).draw();
		app.popStyle();
	}

	public static void hide() {
		gui.hid();
	}

	public static void show() {
		gui.shw();
	}

	protected void hid() {
		controlP5.hide();
	}

	protected void shw() {
		controlP5.show();
	}

	public static void reset() {
		gui.rset();
	}

	public void rset() {
		for (int i = -1; ++i < modules.size();)
			modules.get(i).create();
	}

	public void controlEvent(ControlEvent cEvent) {
		if (cEvent == null)
			return;
		String crtlName = cEvent.name();
		ControlListener mod = eventHandlers.get(crtlName);
		if (mod == null)
			return;
		mod.controlEvent(cEvent);

	}

	/**
	 * Mouse event handler
	 * 
	 * @param event
	 */
	public void mouseEvent(MouseEvent event) {
		switch (event.getID()) {
		case MouseEvent.MOUSE_PRESSED:
			break;
		case MouseEvent.MOUSE_RELEASED:
			mouseReleased();
			break;
		case MouseEvent.MOUSE_CLICKED:
			mouseClicked();
			break;
		case MouseEvent.MOUSE_DRAGGED:
			mouseDragged();
			break;
		case MouseEvent.MOUSE_MOVED:
			break;
		}

	}

	/**
	 * 
	 */
	protected void mouseClicked() {
		if (!Configurator.getBooleanSetting("showGUI"))
			return;
		int i = 0;
		while (i < modules.size()
				&& !modules.get(i).click(app.mouseX, app.mouseY))
			i++;
	}

	/**
	 * 
	 */
	protected void mouseDragged() {
		if (!Configurator.getBooleanSetting("showGUI"))
			return;
		int mX = app.mouseX;
		int mY = app.mouseY;
		int offsetX = app.mouseX - app.pmouseX;
		int offsetY = app.mouseY - app.pmouseY;
		int i = 0;
		if (dragged == null) {
			while (i < modules.size()
					&& !modules.get(i).drag(mX, mY, offsetX, offsetY))
				i++;
			if (i < modules.size())
				dragged = modules.get(i);
		} else {
			dragged.offset(offsetX, offsetY);
		}
	}

	protected void mouseReleased() {
		if (Configurator.getBooleanSetting("showGUI"))
			dragged = null;
	}

	/**
	 * Key event handler.
	 * <table>
	 * <tr>
	 * <th>Key</th>
	 * <th>Effect</th>
	 * <tr>
	 * <tr>
	 * <td>SPACE</td>
	 * <td>hide/show GUI</td>
	 * </tr>
	 * </table>
	 * 
	 * @param e
	 */
	public void keyEvent(KeyEvent e) {
		switch (app.key) {
		case ' ':
			Configurator.setSetting("showGUI", !Configurator
					.getBooleanSetting("showGUI"));
			if (Configurator.getBooleanSetting("showGUI"))
				GUI.show();
			else
				GUI.hide();
			break;
		}
	}

	public void facadeOnOff(boolean on) {
		if(on){
			GUI.hide();
		}else if(Configurator.getBooleanSetting("showGUI")){
			GUI.show();
		}
		
	}
}
