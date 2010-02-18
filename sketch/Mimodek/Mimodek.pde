//The mimodek code
import mimodek.*;

//Dependencies
import traer.physics.*;
import controlP5.*;
import TUIO.*;
import fullscreen.*; 





/*
* MIMODEK,
 * by the Mimodek Team (TODO: put names here)
 * Open Up workshop, february 2010 Media Lab Prado, Madrid, Spain
 * ---
 * TODO : More info?
 */

MainHandler mimodek;

//We'll deal with full screen here
FullScreen fs;
boolean fullScreen = false;

void setup(){
  // Create the fullscreen object
  fs = new FullScreen(this); 
  mimodek = new MainHandler(1024, 768,this);
  mimodek.setup();

}

void draw(){
  mimodek.draw();
}

//SEND EVERY EVENTS TO MIMODEK -- There is probably a nicer solution but that will do for now
public void mouseReleased() {
  mimodek.mouseReleased();
}


public void mouseClicked() {
  mimodek.mouseClicked();
}

public void mouseDragged() {
  mimodek.mouseDragged();
}


public void keyPressed() {
  if(key == 'f'){
    fullScreen = !fullScreen;
    if(fullScreen){
      fs.enter();
    }
    else{
      fs.leave();
    }
  }
  mimodek.keyPressed();
}

void controlEvent(ControlEvent cEvent){
  mimodek.controlEvent(cEvent);
}

//SEND TRACKING EVENTS TO MIMODEK TRACKING MODULE
// called when an object is added to the scene
void addTuioObject(TuioObject tobj) {
  mimodek.tracking.addTuioObject(tobj);
}

// called when an object is removed from the scene
void removeTuioObject(TuioObject tobj) {
  mimodek.tracking.removeTuioObject(tobj);
}

// called when an object is moved
void updateTuioObject (TuioObject tobj) {
  mimodek.tracking.updateTuioObject(tobj);
}

// called when a cursor is added to the scene
void addTuioCursor(TuioCursor tcur) {
  mimodek.tracking.addTuioCursor(tcur);
}

// called when a cursor is moved
void updateTuioCursor (TuioCursor tcur) {
  mimodek.tracking.updateTuioCursor(tcur);
}

// called when a cursor is removed from the scene
void removeTuioCursor(TuioCursor tcur) {
  mimodek.tracking.removeTuioCursor(tcur);
}

// called after each message bundle
// representing the end of an image frame
void refresh(TuioTime bundleTime) { 
  //IS OK to do nothing here????
  //redraw();
}


