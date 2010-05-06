package mimodek.decorator.graphics;

import mimodek.MimodekObject;
import mimodek.decorator.DeadMimo2;
import mimodek.facade.FacadeFactory;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public class LittleLight extends MimodekObjectGraphicsDecorator {

	public LittleLight(MimodekObject decoratedObject) {
		super(decoratedObject);
	}

	@Override
	public void draw(PApplet app) {
		 //strength = Strength;//*(map(dist(pos.x,pos.y,facade.mouseX,facade.mouseY),0,dist(0,0,facade.width,facade.height),1,0));
		float strength = ((DeadMimo2)decoratedObject).getEnergy();
		    PGraphics img = app.createGraphics((int)getDiameter(),(int)getDiameter(),PApplet.JAVA2D);
		    img.beginDraw();
		    img.loadPixels();
		    for(int i=0;i<img.width;i++){
		      for(int j=0;j<img.height;j++){
		        float v = equation(i,j,strength);
		        v = v>0.95*strength?1:v/2;
		        img.pixels[i+j*img.width] = app.lerpColor(app.color(0,0,0,0),app.color(255),v);
		      }
		    }
		    img.updatePixels();
		    img.endDraw();
		    app.pushMatrix();
		    app.translate(getPosX(),getPosY());
		    app.image(img,-img.width/2,-img.height/2);
		    app.popMatrix();
	}
	
	 protected float equation(float x, float y,float strength){
		    int i = Math.round(x+FacadeFactory.getFacade().width/2-getDiameter()/2);
		    int j = Math.round(y+FacadeFactory.getFacade().height/2-getDiameter()/2);
		    if(i>=FacadeFactory.getFacade().width|| i<0 || j<0 ||j>=FacadeFactory.getFacade().height )
		      return 0;
		    float d = MetaBallRenderer.distlookup[i][j];//dist(Radius,Radius,x,y);
		    if(d>getDiameter()/2){
		      return 0;
		    }
		    return (1-(d/getDiameter()/2))*strength;
	 }

	@Override
	public PImage toImage(PApplet app) {
		float strength = ((DeadMimo2)decoratedObject).getEnergy();
		PGraphics img = app.createGraphics((int)getDiameter(),(int)getDiameter(),PApplet.JAVA2D);
	    img.beginDraw();
	    img.loadPixels();
	    for(int i=0;i<img.width;i++){
	      for(int j=0;j<img.height;j++){
	        float v = equation(i,j,strength);
	        v = v>0.95*strength?1:v/2;
	        img.pixels[i+j*img.width] = app.lerpColor(app.color(0,0,0,0),app.color(255),v);
	      }
	    }
	    img.updatePixels();
	    img.endDraw();
	    return img;
	}

}
