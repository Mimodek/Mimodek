import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import mimodek.Mimodek;
import mimodek.MimodekObject;
import mimodek.decorator.CellV2;
import mimodek.decorator.MimodekObjectDecorator;
import mimodek.decorator.graphics.DrawingData;
import mimodek.decorator.graphics.MimodekObjectGraphicsDecorator;
import mimodek.facade.FacadeFactory;
import mimodek.renderer.Renderer;

//Dependencies
import controlP5.*;

CellV2 seed;
Mimodek mimodek;

void setup(){
  mimodek = new Mimodek(this); //Create Mimodek first so that it can setup the context
  mimodek.size(1024, 768);
  size(FacadeFactory.getFacade().width,FacadeFactory.getFacade().height);
  smooth();
  background(0);
  try {
    XMLElement xml;
    xml = new XMLElement(this, "dla.xml");

    seed = Renderer.parseDLACell(xml,this,null);
    //print(seed.toXMLString(null, ""));
  } 
  catch (Exception e) {
    e.printStackTrace();
    exit();
  }
  //noLoop();
}

void draw(){
  seed.draw(this);
}
