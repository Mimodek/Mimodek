package mimodek.decorator.graphics;

import mimodek.MimodekObject;
import mimodek.configuration.Configurator;
import mimodek.decorator.DeadMimo2;
import mimodek.facade.FacadeFactory;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;

public class LittleLight extends MimodekObjectGraphicsDecorator {
	public static float STRENGTH = 0.95f;
	public static int littleLightGfx = -1;
	
	public LittleLight(MimodekObject decoratedObject) {
		super(decoratedObject);
	}
	
	public LittleLight(MimodekObject decoratedObject, int color) {
		super(decoratedObject);
		setDrawingData(new SimpleDrawingData(color));
	}

	@Override
	public void draw(PApplet app) {
		//create the image if not created yet
		if(littleLightGfx < 0){
			//makes the biggest possible image
			float r = Configurator.getFloatSetting("mimosMaxRadius");
			float e = ((DeadMimo2)decoratedObject).getEnergy(); //set the energy to max level
			((DeadMimo2)decoratedObject).setEnergy(r);
			if((int)r<=0)
				return;
			PGraphics img = app.createGraphics((int)r,(int)r,PConstants.JAVA2D);
			draw(img);
			littleLightGfx = TextureCollection.addTexture(new SquareTexture(img,app));
			((DeadMimo2)decoratedObject).setEnergy(e);//restore actual energy
			
		}
		app.pushMatrix();
		app.pushStyle();
		app.translate(getPosX(),getPosY());
		app.tint(getDrawingData().getColor());
		//app.ellipse(0, 0, ((DeadMimo2)decoratedObject).getEnergy(), ((DeadMimo2)decoratedObject).getEnergy());
		TextureCollection.get(littleLightGfx).draw(((DeadMimo2)decoratedObject).getEnergy()/Configurator.getFloatSetting("mimosMaxRadius"));
		app.popStyle();
		app.popMatrix();
		 //strength = Strength;//*(map(dist(pos.x,pos.y,facade.mouseX,facade.mouseY),0,dist(0,0,facade.width,facade.height),1,0));
		/*float r = ((DeadMimo2)decoratedObject).getEnergy();
		if((int)r<=0)
			return;
		PGraphics img = app.createGraphics((int)r,(int)r,PApplet.JAVA2D);
		draw(img);
		app.pushMatrix();
		app.translate(getPosX(),getPosY());
		app.image(img,-img.width/2,-img.height/2);
		app.popMatrix();
		img.dispose();*/
	}
	
	 protected float equation(float x, float y,float strength){
		 	float r = ((DeadMimo2)decoratedObject).getEnergy()/2;
		    int i = Math.round(x+FacadeFactory.getFacade().width/2-r);
		    int j = Math.round(y+FacadeFactory.getFacade().height/2-r);
		    if(i>=FacadeFactory.getFacade().width|| i<0 || j<0 ||j>=FacadeFactory.getFacade().height )
		      return 0;
		    //float d = MetaBallRenderer.distlookup[i][j];//dist(Radius,Radius,x,y);
		    float d = PApplet.dist(r, r, x, y);
		    if(d>r){
		      return 0;
		    }
		    return (1-(d/r))*strength;
	 }

	@Override
	public PImage toImage(PApplet app) {
		float r = ((DeadMimo2)decoratedObject).getEnergy();
		if((int)r<=0)
			return null;
		PGraphics img = app.createGraphics((int)r,(int)r,PConstants.JAVA2D);
		draw(img);
	    return img;
	}

	@Override
	protected void draw(PGraphics gfx) {
	    gfx.beginDraw();
	    gfx.loadPixels();
	    for(int i=0;i<gfx.width;i++){
	      for(int j=0;j<gfx.height;j++){
	        float v = equation(i,j,STRENGTH);
	        v = v>0.95*STRENGTH?1:v/2;
	        gfx.pixels[i+j*gfx.width] = gfx.lerpColor(gfx.color(0,0,0,0),gfx.color(255),v);
	      }
	    }
	    gfx.updatePixels();
	    gfx.endDraw();
	}

}
