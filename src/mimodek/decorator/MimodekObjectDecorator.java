package mimodek.decorator;

import mimodek.MimodekObject;
import mimodek.facade.FacadeFactory;
import mimodek.utils.Verbose;
import processing.core.PApplet;
import processing.core.PVector;

/**
 * Abstract class that should be extended to decorate other MimodekObjects at run time.
 * The constructor takes an instance of MimodekObject to be decorated. All the methods of the MimodekObject interface, except for update(), are implemented by calling the corresponding methods of the decorated object.
 * Sub classed need only implement update() but can of course implement their own version of the other methods.
 * @author Jonsku
 */
public abstract class MimodekObjectDecorator implements MimodekObject {

	public MimodekObject decoratedObject; // the object being decorated
	 
    public MimodekObjectDecorator(MimodekObject decoratedObject) {
        this.decoratedObject = decoratedObject;
    }
    
    public MimodekObject getDecoratedObject(){
    	if(decoratedObject instanceof MimodekObjectDecorator)
    		return ((MimodekObjectDecorator)decoratedObject).getDecoratedObject();
    	return decoratedObject;
    }
   

	public PVector getPos() {
		return decoratedObject.getPos();
	}

	public float getPosX() {
		return decoratedObject.getPosX();
	}

	public float getPosY() {
		return decoratedObject.getPosY();
	}

	public void setPos(float x, float y) {
		decoratedObject.setPos(x, y);

	}

	public void setPos(PVector pos) {
		decoratedObject.setPos(pos);

	}

	public void setPosX(float x) {
		decoratedObject.setPosX(x);

	}

	public void setPosY(float y) {
		decoratedObject.setPosY(y);
	}
	
	public float getDiameter() {
		return decoratedObject.getDiameter();
	}

	public void setDiameter(float r) {
		decoratedObject.setDiameter(r);
	}
	
	public String toString(){
		return this.getClass().getName()+" >> "+decoratedObject;
	}
	
	public void render(){
		return;
	}
	public boolean isInScreen(){
		return decoratedObject.isInScreen();
	}
	
	public String toXMLString(String prefix){
		Verbose.debug(prefix+this);
		return prefix+"<Decorator className=\""+this.getClass().getName()+"\">\n"+decoratedObject.toXMLString(prefix+"\t")+prefix+"</Decorator>\n";
	}
}
