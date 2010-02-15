package mimodek;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PVector;
import traer.physics.Particle;

public class Mimo {
	 
	  
	  public PVector pos;
	  
	  public Particle particle;
	  
	  public PVector vel;
	  

	  public static float maxRadius = 50;
	  public static float minRadius = 10;
	  public float radius;
	  
	  ArrayList<Mimo> neighbours;
	  
	  public boolean ancestor = false;
	  
	  
	  public Mimo(PVector pos){
	    this.pos = pos;
	    this.vel = new PVector(0,0);
	    radius = minRadius+Simulation1.app.random(1)*(maxRadius-minRadius);
	  }
	  
	  void moveTo(float x,float y){
		  pos.x = x;
		  pos.y = y;
	  }
	  
	  void addNeighbour(Mimo m){
		  if(neighbours == null)
			  neighbours = new ArrayList<Mimo>();
		
		  if(!neighbours.contains(m))
			  neighbours.add(m);
	  }
	  
	  void setParticle(Particle p){
		  particle = p;
	  }
	  
	  public void draw(){
		  if(particle != null){
			  pos.x = particle.position().x();
		      pos.y = particle.position().y();
		  }
	    Simulation1.gfx.pushMatrix();
	    Simulation1.gfx.pushStyle();
	    Simulation1.gfx.translate(pos.x,pos.y);
	    
	    if(ancestor){
	      Simulation1.gfx.noFill();
	      Simulation1.gfx.strokeWeight((float) 0.5);
	      Simulation1.gfx.stroke(125);
	    }else{
	      Simulation1.gfx.noStroke();
	      Simulation1.gfx.fill(255,255,255,100);
	    }
	    Simulation1.gfx.ellipse(0,0,radius,radius);
	    Simulation1.gfx.popStyle();
	    Simulation1.gfx.popMatrix();
	    
	    /*
	    if(neighbours == null)
	    	return;
	    Simulation1.gfx.strokeWeight(2);
	    Simulation1.gfx.stroke(255,0,0);
	    for(int i=-1;++i<neighbours.size();){
	    	Mimo m = neighbours.get(i);
	    	Simulation1.gfx.line(pos.x,pos.y,m.pos.x,m.pos.y);
	    }
	    */
	  }

}
