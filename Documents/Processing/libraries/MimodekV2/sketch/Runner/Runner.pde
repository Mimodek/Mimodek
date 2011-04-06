import TUIO.*;
import controlP5.*;

import processing.opengl.*;
import MimodekV2.*;

import com.jcraft.jsch.*;

Mimodek mimodek;

void setup(){
  //Resolution to display on Media lab prado facade
  size(1024, 768, OPENGL);
  //To film in PAL - See readme.txt file
  //size(720,576, OPENGL);
  
  frameRate(30);
  println("Start");
  mimodek = new Mimodek(this);
  
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
