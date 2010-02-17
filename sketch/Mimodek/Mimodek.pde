//Dependencies
import traer.physics.*;
import controlP5.*;

//The mimodek code
import mimodek.*;

/*
* MIMODEK,
* by the Mimodek Team (TODO: put names here)
* Open Up workshop, february 2010 Media Lab Prado, Madrid, Spain
* ---
* TODO : More info?
*/

MainHandler mimodek;

void setup(){
  mimodek = new MainHandler(1024, 768,this);
}

void draw(){
  mimodek.draw();
}

//SEND EVERY EVENTS TO MIMODEK -- There is probably a nicer solution but that will do for now
public void mouseReleased() {
  mimodek.mouseReleased();
}

public void keyPressed() {
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

