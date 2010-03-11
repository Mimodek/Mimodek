package mimodek;

import java.util.ArrayList;

import mimodek.texture.SeedGradient.SeedGradientData;


import processing.core.PVector;
import processing.xml.XMLElement;
import traer.physics.Particle;

public class Mimo {

	public PVector pos;

	public Particle particle;

	public PVector vel;

	public float radius;

	ArrayList<Mimo> neighbours;
	Mimo father;

	public boolean ancestor = false;
	public boolean collided = false;
	
//	public boolean hasEntered = false;


	public Object drawingData;
	
	public PVector toStructure;
	
	//public float growth = 0.01f;
	
	public boolean isSeed = false;
	public PVector targetVel;
	public PVector oldVel;
	public int numSteps;
	public int current;
	public boolean turning;
	public float easing;
	public PVector targetPos;
	private float oldSpeed;
	private float targetSpeed;
	
	public long lastActiveMovement = 0;
	public long createdAt = 0;


	public Mimo(PVector pos) {
		this.pos = pos;
		this.vel = new PVector(0, 0);
		radius = Mimodek.config.getFloatSetting("mimosMinRadius");
		targetPos=new PVector(0.0f,0.0f);  
		easing=Mimodek.config.getFloatSetting("mimosEasing");
		
		oldVel=new PVector(0.0f,0.0f);
		  targetVel=new PVector(0.0f,0.0f);
		  oldSpeed=0.0f;
		  targetSpeed=0.0f;

		   turning=false;
	}

	void moveTo(float x, float y) {
		pos.x = x;
		pos.y = y;
	}

	void addNeighbour(Mimo m) {
		if (neighbours == null)
			neighbours = new ArrayList<Mimo>();

		if (!neighbours.contains(m))
			neighbours.add(m);
	}

	void setParticle(Particle p) {
		particle = p;
	}

	public void removeNeighbour(Mimo m) {
		if (neighbours == null || !neighbours.contains(m))
			return; 
		neighbours.remove(m);
	}
	
	//returns a String representing the mimo in XML 
	public String toXMLCell(){
		String xml = isSeed?"<seed ":"<cell";
		xml +=" pos=\""+pos.x+","+pos.y+"\"";
		xml +=" radius=\""+radius+"\"";
		SeedGradientData d = (SeedGradientData)drawingData;
		xml +=" startColor=\""+d.startColor+"\"";
		xml +=" endColor=\""+d.endColor+"\"";
		xml += "/>";
		return xml;
	}
	
	public static Mimo createtFromXML(XMLElement cell){
		String[] position = cell.getAttribute("pos").split(",");
		Mimo m = new Mimo(new PVector(Float.parseFloat(position[0]),Float.parseFloat(position[1])));
		
		m.isSeed = cell.getName()=="seed";
		
		Object data = Mimodek.texturizer.seedDrawer.getDrawingData(m);
		m.drawingData = data;
		SeedGradientData d = (SeedGradientData)m.drawingData;
		d.startColor = cell.getIntAttribute("startColor");
		d.endColor = cell.getIntAttribute("endColor");
		float r = cell.getFloatAttribute("radius");
		while(m.radius<r){
			Mimodek.texturizer.draw(m);
		}
		m.ancestor = true;
		return m;
	}
}
