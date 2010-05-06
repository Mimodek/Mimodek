//The mimodek code
import mimodek.facade.*;
import mimodek.tracking.*;
import mimodek.controls.*;
import mimodek.*;

//Dependencies
import controlP5.*;

/*
 * MIMODEK,
 * by the Mimodek Team (TODO: put names here)
 * Open Up workshop, february 2010 Media Lab Prado, Madrid, Spain
 * ---
 * TODO : More info?
 */

Mimodek mimodek;

TrackingSimulator tracking;

void setup(){

  //use only this resolution for the presentation PC's screen
  size(1024, 768);
  //The order of initialization is important
  mimodek = new Mimodek(this); //Create Mimodek first so that it can setup the context
  mimodek.size(1024, 768); //set the size
  //THE FOLLOWING IS OPTIONAL BUT USEFUL FOR DEBUG/DEMO
  setupGUI(); //Create a GUI using controlP5
  tracking = new TrackingSimulator(this, FacadeFactory.getFacade().leftOffset,FacadeFactory.getFacade().topOffset*2+FacadeFactory.getFacade().height); //Create a Tracking simulator
  tracking.setListener(mimodek.mimosManager); //set the Mimos Manager as a listener for trakcing data
  tracking.start(); //Start the simulator (Threaded)
  
  smooth();
  frameRate(24);
  PFont font = createFont("Verdana", 10, false);
  hint(PApplet.ENABLE_NATIVE_FONTS);
  textFont(font);

}

/**
 * Initialize the GUI, this should be disabled/locked in production
 * environment. To enable/disable a GUI module, simply un/comment the
 * corresponding line in the method body.
 */
public void setupGUI() {
  GUI.createGui(this);
  // Register Mimodek to handle some events coming from the GUI
  GUI.registerEventHandler("RESET", mimodek);
  GUI.registerEventHandler("Mimos' size", mimodek);
  // GUI.registerEventHandler("Simulator ON/OFF", this);
  // GUI.registerEventHandler("Restart simulator", this);
  // GUI.registerEventHandler("Spring Strength", this);
  // GUI.registerEventHandler("Spring Damping", this);
}

void draw(){
  background(0);
  FacadeFactory.getFacade().showDrawingArea();
  tracking.draw();
}

void keyPressed(){
  if(key == 'o'){
    FacadeFactory.getFacade().togglePreview();
  }
}






