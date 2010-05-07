package mimodek;

import mimodek.facade.FacadeFactory;
import processing.core.PVector;

public class SimpleMimo implements MimodekObject {
	private PVector pos = null;
	//private DrawingData drawingData = null;
	private float radius = 0;
	
	public SimpleMimo(PVector pos){
		this.pos = pos;
	}

	/*public DrawingData getDrawingData() {
		return drawingData;
	}*/

	public PVector getPos() {
		return pos;
	}

	public float getPosX() {
		return pos.x;
	}

	public float getPosY() {
		return pos.y;
	}

	/*public void setDrawingData(DrawingData dData) {
		drawingData = dData;
	}*/

	public void setPos(float x, float y) {
		pos.x = x;
		pos.y = y;
	}

	public void setPos(PVector pos) {
		this.pos = pos;

	}

	public void setPosX(float x) {
		pos.x = x;
	}

	public void setPosY(float y) {
		pos.y = y;
	}

	public void update() {
		
	}

	public float getDiameter() {
		return radius;
	}

	public void setDiameter(float r) {
		radius = r;
	}
	
	public String toString(){
		return "SimpleMimo";
	}
	
	public String toXMLString(String prefix){
		return prefix+"<MimoObject className=\""+this.getClass().getName()+"\" posX=\""+getPosX()+"\" posY=\""+getPosY()+"\" radius=\""+getDiameter()+"\"/>\n";
	}

	public boolean isInScreen() {
		return FacadeFactory.getFacade().isInTheScreen(getPos(), getDiameter()/2);
	}

}
