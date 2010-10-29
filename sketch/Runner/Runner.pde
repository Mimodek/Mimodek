import TUIO.*;
import controlP5.*;

import processing.opengl.*;
import MimodekV2.*;

import com.jcraft.jsch.*;

Mimodek mimodek;

void setup(){
  size(1024, 768, OPENGL);
  frameRate(30);
  println("Start");
  mimodek = Mimodek.startMimodek(this);
  
}

void draw(){
  background(0.3f);
}

void mouseDragged() {
		mimodek.mouseDragged();
	}

void keyPressed() {
  		mimodek.keyPressed();
}
