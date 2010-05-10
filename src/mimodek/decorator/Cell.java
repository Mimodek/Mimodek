package mimodek.decorator;

import java.util.ArrayList;

//import mimodek.configuration.Configurator;
import mimodek.MimodekObject;
import mimodek.configuration.Configurator;
import mimodek.decorator.graphics.ImageDrawer;
import mimodek.decorator.graphics.NoImageException;
import mimodek.facade.FacadeFactory;
import mimodek.utils.Verbose;

import processing.core.PApplet;
import processing.core.PVector;

/**
 * A cell of the DLA. Each cell can have one or more neighbours. Other class
 * should only communicate with the DLA through one and one cell only. Message
 * like attract or repel are passed to neighbours cell and so on, so the whole
 * tree will be queried by calling the method only on a single cell (usually the
 * first one created). Update of two cells from the same DLA will have
 * unexpected result and in any case be take much more time. Tgis also why most
 * methods takes a calling Cell as a parameter so the message will not go back
 * to a cell that has been called already. This class subclass
 * MimodekObjectGraphicsDecorator and decorates only other
 * MimodekObjectGraphicsDecorator because of this: the draw method must call the
 * other cells draw method and so on to display the whole tree. It does not
 * however do any more drawing than the decorated object. Bottom line is : one
 * DLA = one instance of a Cell in the MimoManager class, no more.
 * 
 * @author Jonsku
 */
public class Cell extends ImageDrawer {

	public boolean fixed = false;

	public ArrayList<Cell> neighbours = new ArrayList<Cell>();

	/**
	 * Creates a new cell from a MimodekObjectGraphicsDecorator.
	 * 
	 * @param decoratedObject
	 * @throws NoImageException
	 */
	public Cell(DeadMimo2 decoratedObject, PApplet app) throws NoImageException {
		super(decoratedObject, decoratedObject.activeMimosShape, Configurator.getIntegerSetting("temperatureColor"), app);
		Verbose.debug(decoratedObject.decoratedObject.getClass().getName());
	}

	/**
	 * A leaf is a cell with only one neighbour.
	 * 
	 * @return true if the cell is a leaf
	 */
	public boolean isLeaf() {
		return neighbours.size() <= 1;
	}

	/**
	 * Remove a cell from the neighbours list.
	 * 
	 * @param c
	 *            a neighbour to remove from the list
	 */
	public void removeNeighbour(Cell c) {
		if (!neighbours.contains(c))
			return;
		// find and remove
		neighbours.remove(neighbours.indexOf(c));
	}

	/**
	 * Update the whole tree
	 * 
	 * @see mimodek.decorator.graphics.MimodekObjectGraphicsDecorator#update()
	 */
	@Override
	public void update() {
		for (int i = 0; i < neighbours.size(); i++) {
			Cell c = (neighbours.get(i));
			c.update(this);
		}
	}

	/**
	 * Update the position of a cell and pass the message to neighbours cells
	 * 
	 * @param up
	 *            The calling cell
	 */
	protected void update(Cell up) {
		
		float d = 0;
		if (!fixed && up != null) { // calculate the distance between the
									// calling cell and this one
			d = getPos().dist(up.getPos())
					- (up.getDiameter() / 2 + getDiameter() / 2) / 2;
			if (d > 0) {
				// move to maintain the distance between cells
				float a = (float) Math.atan2(up.getPosY() - getPosY(), up
						.getPosX()
						- getPosX());
				PVector nuPos = new PVector(getPosX() + PApplet.cos(a) * d,
						getPosY() + PApplet.sin(a) * d);
				if (FacadeFactory.getFacade().isInTheScreen(nuPos,
						getDiameter() / 2))
					setPos(nuPos);
			} else if (d < -3) {
				// move to maintain the distance between cells
				float a = (float) Math.atan2(getPosY() - up.getPosY(),
						getPosX() - up.getPosX());
				PVector nuPos = new PVector(getPosX() + PApplet.cos(a) * d,
						getPosY() + PApplet.sin(a) * d);
				if (FacadeFactory.getFacade().isInTheScreen(nuPos,
						getDiameter() / 2))
					setPos(nuPos);
			}
			// pass the message to neighbours cells
			for (int i = 0; i < neighbours.size(); i++) {
				Cell c = (neighbours.get(i));
				if (c != up) { // update only if it is not the calling cell
					c.update(this);
				}
			}
			/*
			 * else if(d<0){ //move to maintain the distance between cells float
			 * a = (float)
			 * Math.atan2(getPosY()-up.getPosY(),getPosX()-up.getPosX());
			 * PVector nuPos = new PVector(getPosX() + PApplet.cos(a) * d,
			 * getPosY() + PApplet.sin(a) * d); if
			 * (FacadeFactory.getFacade().isInTheScreen(nuPos, getRadius() / 2))
			 * setPos(nuPos); }
			 */
		}

	}

	/**
	 * Draw the whole DLA
	 * 
	 * @see mimodek.decorator.graphics.MimodekObjectGraphicsDecorator#draw(processing.core.PApplet)
	 */
	@Override
	public void draw(PApplet app) {
		draw(null, app);
	}

	/**
	 * Draw this cell and pass the message to neighbours cells
	 * 
	 * @param up
	 *            The calling cell
	 * @param app
	 *            The parent Sketch
	 */
	protected void draw(Cell up, PApplet app) {
		super.draw(app);
		for (int i = 0; i < neighbours.size(); i++) {
			Cell c = (neighbours.get(i));
			if (c != up) // draw only if it is not the calling cell
				c.draw(this, app);
		}
	}

	/**
	 * Test if the given object can be attached to the DLA.
	 * 
	 * @param up
	 *            The calling cell
	 * @param obj
	 *            The object to be tested
	 * @return
	 */
	public Cell attachTest(Cell up, MimodekObject obj) {
		if (obj.getPos().dist(getPos()) <= (obj.getDiameter() + getDiameter()) / 2)
			return this;

		for (int i = 0; i < neighbours.size(); i++) {
			Cell c = neighbours.get(i);
			if (c != up) {
				Cell tmp = c.attachTest(this, obj);
				if (tmp != null)
					return tmp;
			}
		}
		return null;
	}

	/**
	 * Attach a cell to this cell. You should perform a attachTest() first.
	 * 
	 * @param c
	 *            The cell to be attached
	 */
	public void attach(Cell c) {
		if (!c.neighbours.contains(this))
			c.neighbours.add(this);
		if (!neighbours.contains(c))
			neighbours.add(c);
	}

	/**
	 * Attract cells if they are close enough (depending on the threshold) to
	 * the coordinates
	 * 
	 * @param up
	 *            The calling Cell
	 * @param x
	 *            Horizontal coordinate
	 * @param y
	 *            Vertical coordinate
	 * @param threshold
	 *            The radius of influence
	 */
	public void attract(Cell up, float x, float y, float threshold) {
		float d = threshold + 1;
		if (!fixed)
			d = PApplet.dist(x, y, getPosX(), getPosY());
		if (!fixed && d <= threshold /* && d >= getRadius()/2 */) {
			float a = PApplet.atan2(y - getPosY(), x - getPosX());
			/*
			 * if (neighbours.size() > 0) d = (2 / neighbours.size()); else
			 */
			d = (d / (getDiameter() / 2)) / (neighbours.size() + 1);
			PVector nuPos = new PVector(getPosX() + PApplet.cos(a) * d,
					getPosY() + PApplet.sin(a) * d);
			if (FacadeFactory.getFacade().isInTheScreen(nuPos,
					getDiameter() / 2))
				setPos(nuPos);
			update(null);
		}
		for (int i = 0; i < neighbours.size(); i++) {
			Cell c = neighbours.get(i);
			if (c != up) {
				c.attract(this, x, y, threshold);
			}
		}
	}

	/**
	 * Repel cells if they are close enough (depending on the threshold) to the
	 * coordinates
	 * 
	 * @param up
	 *            The calling Cell
	 * @param x
	 *            Horizontal coordinate
	 * @param y
	 *            Vertical coordinate
	 * @param threshold
	 *            The radius of influence
	 */
	public void repel(Cell up, float x, float y, float threshold) {
		float d = threshold + 1;
		if (!fixed)
			d = PApplet.dist(x, y, getPosX(), getPosY());
		if (!fixed && d <= threshold /* && d >= getRadius()/2 */) {
			float a = PApplet.atan2(getPosY() - y, getPosX() - x);
			/*
			 * if (neighbours.size() > 0) d = (2 / neighbours.size()); else
			 */
			d = (d / (getDiameter() / 2))/neighbours.size()+1;
			PVector nuPos = new PVector(getPosX() + PApplet.cos(a) * d,
					getPosY() + PApplet.sin(a) * d);
			if (FacadeFactory.getFacade().isInTheScreen(nuPos,
					getDiameter() / 2))
				setPos(nuPos);
			update(null);
		}
		for (int i = 0; i < neighbours.size(); i++) {
			Cell c = neighbours.get(i);
			if (c != up) {
				c.repel(this, x, y, threshold);
			}
		}
	}

	/**
	 * Find all the leaves.
	 * 
	 * @param up
	 *            The calling Cell
	 * @param collector
	 *            This ArrayList will contain all the leaves found
	 */
	public void getLeaves(Cell up, ArrayList<Cell> collector) {
		if (neighbours.size() < 2)
			collector.add(this);
		for (int i = 0; i < neighbours.size(); i++) {
			Cell c = neighbours.get(i);
			if (c != up) {
				c.getLeaves(this, collector);
			}
		}
	}

	/*
	 * @Override public PImage toImage(PApplet app) { return
	 * ((MimodekObjectGraphicsDecorator)decoratedObject).toImage(app); }
	 */

	public String toXMLString(Cell up, String prefix) {

		Verbose.debug(prefix + this);
		String XMLString = prefix + "<DLACell className=\""
				+ this.getClass().getName() + "\" fixed=\"" + fixed + "\">\n";
		XMLString += decoratedObject.toXMLString(prefix + "\t");
		XMLString += prefix + "\t<neighbours>\n";
		for (int i = 0; i < neighbours.size(); i++) {
			Cell c = neighbours.get(i);
			if (c != up) {
				XMLString += c.toXMLString(this, prefix + "\t\t");
			}
		}
		XMLString += prefix + "\t</neighbours>\n";
		XMLString += prefix + "</DLACell>\n";
		return XMLString;
	}

}
