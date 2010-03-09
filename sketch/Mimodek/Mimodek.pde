//The mimodek code
import mimodek.*;

//Dependencies
import traer.physics.*;
import controlP5.*;
import TUIO.*;





/*
* MIMODEK,
 * by the Mimodek Team (TODO: put names here)
 * Open Up workshop, february 2010 Media Lab Prado, Madrid, Spain
 * ---
 * TODO : More info?
 */

Mimodek mimodek;

void setup(){
  mimodek = new Mimodek(this);
  //Good resolution for the presentation PC's screen
  size(1024, 768);
  
  smooth();
  frameRate(24);
  PFont font = app.createFont("Verdana", 10, false);
  hint(PApplet.ENABLE_NATIVE_FONTS);
  textFont(font);

}

void draw(){

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
  mimodek.keyPressed();
}

void controlEvent(ControlEvent cEvent){
  if(mimodek!=null)
    mimodek.controlEvent(cEvent);
}

//SEND TRACKING EVENTS TO MIMODEK TRACKING MODULE
// called when an object is added to the scene
void addTuioObject(TuioObject tobj) {
  if(mimodek!=null)
    mimodek.tracking.addTuioObject(tobj);
}

// called when an object is removed from the scene
void removeTuioObject(TuioObject tobj) {
  try{
    if(mimodek!=null)
      mimodek.tracking.removeTuioObject(tobj);
  }
  catch(Exception e){
    e.printStackTrace();
  }
}

// called when an object is moved
void updateTuioObject (TuioObject tobj) {
  if(mimodek!=null)
    mimodek.tracking.updateTuioObject(tobj);
}

// called when a cursor is added to the scene
void addTuioCursor(TuioCursor tcur) {
  if(mimodek!=null)
    mimodek.tracking.addTuioCursor(tcur);
}

// called when a cursor is moved
void updateTuioCursor (TuioCursor tcur) {
  if(mimodek!=null)
    mimodek.tracking.updateTuioCursor(tcur);
}

// called when a cursor is removed from the scene
void removeTuioCursor(TuioCursor tcur) {
  if(mimodek!=null)
    mimodek.tracking.removeTuioCursor(tcur);
}

// called after each message bundle
// representing the end of an image frame
void refresh(TuioTime bundleTime) { 
  //Is it OK to do nothing here????
  //redraw();
}




