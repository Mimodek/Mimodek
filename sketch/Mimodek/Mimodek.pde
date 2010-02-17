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

//SEND EVERY EVENTS TO MIMODEK
public void mouseReleased() {
  mimodek.mouseReleased();
}

public void keyPressed() {
  mimodek.keyPressed();
}

void controlEvent(ControlEvent cEvent){
  mimodek.controlEvent(cEvent);
}


