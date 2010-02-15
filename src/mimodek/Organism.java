package mimodek;

import processing.core.PApplet;
import processing.core.PVector;

public class Organism {
	public Mimo[] mimos;
	  
	  Organism(){
	    mimos = new Mimo[0];
	  }
	  
	  public int cellCount(){
	    return mimos.length;
	  }
	  
	  public boolean attachTo(Mimo m){
		if(mimos.length == 0){
			//special case, we're seeding
			m.pos.y = Simulation1.screenHeight;
			m.setParticle(Simulation1.pSim.addParticle(m.pos, 1, true));
			addCell(m);
			Simulation1.pSim.floor = Physics.DOWN;
			return true;
		}
		boolean added = false;
	    for(int i=-1;++i<mimos.length;){
	      if(mimos[i].pos.dist(m.pos)<=(mimos[i].radius+m.radius)/2){
	    	float nuRadius = PApplet.map(m.pos.dist(mimos[0].pos),0,PApplet.max(Simulation1.screenWidth,Simulation1.screenHeight),m.maxRadius,m.minRadius);
	        // 
	        float a = PApplet.atan2(mimos[i].pos.y-m.pos.y,mimos[i].pos.x-m.pos.x);
	        float d = m.radius>nuRadius?m.radius-nuRadius:nuRadius-m.radius;
	        m.pos.x += PApplet.cos(a)*d;
	        m.pos.x += PApplet.sin(a)*d;
	        m.radius = nuRadius;
	        if(!added){
	        	m.setParticle(Simulation1.pSim.addParticle(m.pos, m.radius/Mimo.maxRadius, false));
	        	addCell(m);
	        }
	        Simulation1.pSim.addSpring(m.particle, mimos[i].particle);
	       // m.addNeighbour(mimos[i]);
	        //mimos[i].addNeighbour(m);
	        added = true;
	      }
	    }
	    return added;
	  }
	  
	  void addCell(Mimo m){
	     Mimo[] tmp = new Mimo[mimos.length+1];
	     System.arraycopy(mimos,0,tmp,0,mimos.length);
	     tmp[mimos.length] = m;
	     mimos = tmp;
	  }
	  
	  void draw(){
	          Simulation1.gfx.fill(Simulation1.app.color(0,255,0,50));
	     //stroke(color(0,255,0));
	    for(int i=-1;++i<mimos.length;){
	      /*
	     
	      float orR = mimos[i].radius;
	      mimos[i].radius += offset;*/
	    	// float offset = (Simulation1.app.random(1)>0.5f?-0.5f:0.5f)*Simulation1.app.noise(i,Simulation1.app.frameCount*0.001f);
	      mimos[i].draw();
	      Simulation1.gfx.ellipse(mimos[i].pos.x,mimos[i].pos.y,mimos[i].radius/3,mimos[i].radius/3);
	      //mimos[i].radius = orR;
	    }
	  }
}
