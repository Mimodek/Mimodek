package mimodek;

import processing.core.PVector;



/**
 * Defines an interface for the minimum data and behaviour needed by the objects in Mimodek.
 * Here are only simple and convenient setters/getters to get spatial information about the object and update its state.
 * Looked at SimpleMimo for the simplest implementation.
 * To change/enhance the behaviour of objects, I decided to use the decorator pattern. It is particularly helpful in changing the aspect AND movement of an object at run time.
 * It is much more flexible than writing a class for each possible combination of motion/look.
 * Have a look at MimodekObjectDecorator and MimodekObjectGraphicsDecorator for more details.
 * @author Jonsku
 */
public interface MimodekObject {
	/**
	 * @return the horizontal position of the center of the object
	 */
	public float getPosX();
	/**
	 * @return the vertical position of the center of the object
	 */
	public float getPosY();
	/**
	 * @return the position of the center of the object
	 */
	public PVector getPos();
	/**
	 * Set the horizontal position of the center of the object
	 * @param x
	 */
	public void setPosX(float x);
	/**
	 * Set the vertical position of the center of the object
	 * @param y
	 */
	public void setPosY(float y);
	/**
	 * Set the position of the center of the object
	 * @param x
	 * @param y
	 */
	public void setPos(float x, float y);
	/**
	 * Set the position of the center of the object
	 * @param pos
	 */
	public void setPos(PVector pos);
	/**
	 * @return the radius of the object
	 */
	public float getDiameter();
	/**
	 * Set the radius of the object to r.
	 * @param r
	 */
	public void setDiameter(float r);
	
	/**
	 * Do some calculation to update the state of the object
	 */
	public void update();
	
	public boolean isInScreen();
	
	public String toXMLString(String prefix);
	
}
