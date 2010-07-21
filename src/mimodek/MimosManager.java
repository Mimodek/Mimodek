package mimodek;

import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import processing.core.PApplet;
import processing.core.PVector;

import mimodek.configuration.Colors;
import mimodek.configuration.Configurator;

import mimodek.decorator.ActiveMimo;
import mimodek.decorator.Cell;
//import mimodek.decorator.CellV2;
import mimodek.decorator.DeadMimo1;
import mimodek.decorator.DeadMimo2;
import mimodek.decorator.RandomWalker;
import mimodek.decorator.TargetSeaker;

import mimodek.decorator.graphics.MetaBallRenderer;
import mimodek.decorator.graphics.MimodekObjectGraphicsDecorator;
import mimodek.decorator.graphics.NoImageException;

import mimodek.facade.FacadeFactory;
import mimodek.tracking.TrackingInfo;
import mimodek.tracking.TrackingListener;
import mimodek.utils.Verbose;

/**
 * The MimosManager manage the different type of Mimos, handles the update and
 * the drawing, and also specifies how a Mimo goes from one state to the other.
 * Its the heart of the system.
 * 
 * @author Jonsku
 */
public class MimosManager implements TrackingListener {
	/*
	 * Executor service to render mimos asynchronously
	 */
	Executor executor = Executors.newFixedThreadPool(5);

	/**
	 * Maps a numerical unique identifier to a decorated MimodekObject. Thread
	 * safe.
	 */
	Hashtable<Long, MimodekObjectGraphicsDecorator> activeMimos;

	Hashtable<Long, MimodekObjectGraphicsDecorator> deadMimos1;

	Hashtable<Long, MimodekObjectGraphicsDecorator> deadMimos2;

	/**
	 * A reference to Mimodek main class
	 */
	Mimodek mimodek;

	/**
	 * The first cell (or seed) of the DLA
	 */
	Cell seed = null;

	/**
	 * A pool of colors to choose from when creating ActiveMimos
	 */
	Colors activeMimosColors;

	/**
	 * Creates a MimosManager by binding it to Mimodek and setting the colors to
	 * use with active mimos.
	 * 
	 * @param mimodek
	 *            An Instance of Mimodek.
	 * @param activeMimosColors
	 *            A pool of colors to use with active mimos.
	 */

	public MimosManager(Mimodek mimodek, Colors activeMimosColors) {
		this.mimodek = mimodek;
		this.activeMimosColors = activeMimosColors;
		activeMimos = new Hashtable<Long, MimodekObjectGraphicsDecorator>();
		deadMimos1 = new Hashtable<Long, MimodekObjectGraphicsDecorator>();
		deadMimos2 = new Hashtable<Long, MimodekObjectGraphicsDecorator>();

		Configurator.setSetting("ancestorCellCount", 0);
		// Initialize a metaball renderer
		MetaBallRenderer.createInstance(mimodek.app);
	}

	/**
	 * Number of Active Mimos in the system
	 */
	public synchronized int activeMimoCount() {
		return activeMimos.size();
	}

	/**
	 * Update every objects currently in Mimodek. Thread safe.
	 */
	public synchronized void update() {
		if (seed != null) {
			seed.fixed = Configurator.getBooleanSetting("seedFixed");

		}
		// update Active Mimos
		Enumeration<Long> e = activeMimos.keys();
		while (e.hasMoreElements()) {
			long i = e.nextElement();
			MimodekObjectGraphicsDecorator m = activeMimos.get(i);
			m.update();
			if (seed != null) {
				// seed.retract(m.getPosX(), m.getPosY(),
				// m.getDiameter()*Configurator.getFloatSetting("activeMimoRepel"));
				seed.repel(null, m.getPosX(), m.getPosY(), m.getDiameter()
						* Configurator.getFloatSetting("activeMimoRepel"));// *turbulence
			}
			ActiveMimo aM = (ActiveMimo) m.decoratedObject;
			if (mimodek.app.millis() - aM.lastActiveMovement > Configurator
					.getIntegerSetting("mimosMaxLifeTime") * 1000) {
				// this active mimo hasn't been moving for long enough, let's
				// turn it into a dead mimo 1
				// createDeadMimo1(aM, i);
				// remove it from the active mimo list
				activeMimos.remove(i);
				Verbose.debug("Active Mimos -> Dead Mimo 1.");
			} /*
			 * else { // check collision and energy transfer with dead mimos 1
			 * Enumeration<Long> ee = deadMimos1.keys(); while
			 * (ee.hasMoreElements()) { long j = ee.nextElement(); DeadMimo1 dM1
			 * = (DeadMimo1) deadMimos1.get(j).decoratedObject;
			 * dM1.nourrish(aM); // no more energy, remove it if
			 * (dM1.getDiameter() <= 0) deadMimos1.remove(j--); } }
			 */
		}

		// update dead mimos 2
		e = deadMimos2.keys();
		while (e.hasMoreElements()) {
			long i = e.nextElement();
			MimodekObjectGraphicsDecorator m = deadMimos2.get(i);
			m.update();
			if (((DeadMimo2) m.decoratedObject).getEnergy() <= 0) {
				// no more energy, remove the dead mimo 2
				deadMimos2.remove(i);
				continue;
			}
			// float scale = 1 / Configurator.getFloatSetting("ancestorScale");
			// //calculate the scale factor for ancestor
			// float edgeDetection =
			// Configurator.getFloatSetting("edgeDetection");
			if (seed == null
					&& FacadeFactory.getFacade().isInTheScreen(m.getPos(),
							m.getDiameter() / 2)) {

				// the seed is null and this ancestor is on the edge of the
				// screen
				// gfx.decorator.decorato
				try {
					seed = new Cell((DeadMimo2) m.decoratedObject, mimodek.app);
				} catch (NoImageException e1) {
					seed = null;
				} finally {
					deadMimos2.remove(i);
				}
				/*
				 * seed = new CellV2((DeadMimo2) m.decoratedObject,
				 * mimodek.app); seed.fixed =
				 * Configurator.getBooleanSetting("seedFixed");
				 * seed.tagOnLastBranch();
				 * Configurator.setSetting("ancestorCellCount",1); //seed.fixed
				 * = true; // it will stay anchored at its current // position }
				 * catch (NoImageException e1) { seed = null; } finally {
				 * deadMimos2.remove(i); }
				 */
			} else if (seed != null) {
				// test if it touches the DLA
				Cell c = seed.attachTest(null, m);
				if (c != null) { // yes, the ancestor becomes a cell of the DLA
					Cell nC;
					try {
						nC = new Cell((DeadMimo2) m.decoratedObject,
								mimodek.app);
						c.attach(nC);
						Configurator
								.setSetting(
										"ancestorCellCount",
										Configurator
												.getIntegerSetting("ancestorCellCount") + 1);
					} catch (NoImageException e1) {
						// do nothing
					} finally {
						deadMimos2.remove(i);
					}
				}else{
					seed.attract(null, m.getPosX(),m.getPosY(), m.getDiameter()*2*Configurator.getFloatSetting("ancestorMimoAttract"));// *turbulence
				}
			}
		}
		/*
		 * try { if(seed.attach((DeadMimo2) m.decoratedObject,mimodek.app)){
		 * Configurator
		 * .setSetting("ancestorCellCount",Configurator.getIntegerSetting
		 * ("ancestorCellCount")+1); //seed.tagOnLastBranch();
		 * deadMimos2.remove(i); } else { seed.attract(null, m.getPosX(),
		 * m.getPosY(), m
		 * .getDiameter()*2*Configurator.getFloatSetting("ancestorMimoAttract"
		 * ));// *turbulence } } catch (NoImageException e1) { // TODO
		 * Auto-generated catch block e1.printStackTrace(); }/
		 */

		/*
		 * Cell c = seed.attachTest(null, m); if (c != null) { // yes, the
		 * ancestor becomes a cell of the DLA Cell nC; try { nC = new
		 * Cell((DeadMimo2) m.decoratedObject, mimodek.app); c.attach(nC);
		 * Configurator
		 * .setSetting("ancestorCellCount",Configurator.getIntegerSetting
		 * ("ancestorCellCount")+1); } catch (NoImageException e1) { // do
		 * nothing } finally { deadMimos2.remove(i); }/
		 */

		// update dead mimos 1
		e = deadMimos1.keys();
		while (e.hasMoreElements()) {
			long j = e.nextElement();
			deadMimos1.get(j).update();
			DeadMimo1 dm1 = (DeadMimo1) deadMimos1.get(j).decoratedObject;
			// remove it if it wanders of screen or doesn't have any energy left
			if (!dm1.isInScreen() || dm1.getEnergy() <= 0) {
				deadMimos1.remove(j);
				// continue;
			}
			/*
			 * // check if it collides with an ActiveMimo Enumeration<Long> e2 =
			 * activeMimos.keys(); while (e2.hasMoreElements()) {
			 * MimodekObjectGraphicsDecorator aM = activeMimos.get(e2
			 * .nextElement()); if (PApplet.dist(aM.getPosX(), aM.getPosY(),
			 * dm1.getPosX(), dm1 .getPosY()) < aM.getDiameter() / 2 +
			 * dm1.getDiameter() / 2) { // on collision increase the strength of
			 * the metaball using // the remaining energy
			 * ((MetaBall)((ComboGraphicsDecorator)
			 * aM).getSecondaryDecorator()).
			 * increaseStrength(dm1.getEnergy()*Configurator
			 * .getFloatSetting("deadMimo1Energy")); // remove it
			 * deadMimos1.remove(j); continue; } } /
			 */

		}

		// update the ancestors (DLA), if any
		if (seed != null) {

			// seed.expand();
			seed.update();
		}

		Configurator.setSetting("activeMimoCount", activeMimoCount());
		Configurator.setSetting("deadMimo1Count", deadMimos1.size());
		Configurator.setSetting("deadMimo2Count", deadMimos2.size());

	}

	/**
	 * Draw all the objects
	 */
	public synchronized void draw() {

		// render dead mimos 1
		Enumeration<Long> e = deadMimos1.keys();
		while (e.hasMoreElements()) {
			long j = e.nextElement();
			deadMimos1.get(j).draw(mimodek.app);
		}

		// render dead mimos 2
		e = deadMimos2.keys();
		while (e.hasMoreElements()) {
			long i = e.nextElement();
			deadMimos2.get(i).draw(mimodek.app);
		}

		// render meta balls
		MetaBallRenderer.getInstance().render();

		// render active mimos
		e = activeMimos.keys();
		while (e.hasMoreElements()) {
			final long i = e.nextElement();
			
			executor.execute(
			new Runnable(){
				public void run() {
					activeMimos.get(i).renderOne(mimodek.app);
				}
			});
			activeMimos.get(i).drawRender(mimodek.app);
		}

		// render ancestors, if any
		if (seed != null)
			seed.draw(mimodek.app);

	}

	/**
	 * Return the id of the closest Active Mimo from the coordinate given as
	 * parameter.
	 * 
	 * @param x
	 * @param y
	 * @return the id in the activeMimos hashtable of the closest Active Mimo,
	 *         -1 if none was found
	 */
	private synchronized Long findClosestActiveMimo(float x, float y) {
		if (activeMimos.size() == 0) {
			Verbose.debug("No other Active Mimos.");
			return new Long(-1);
		}
		Enumeration<Long> e = activeMimos.keys();
		Long closest = e.nextElement();
		MimodekObject m = activeMimos.get(closest);
		float d = PApplet.dist(x, y, m.getPosX(), m.getPosY());
		while (e.hasMoreElements()) {
			Long ind = e.nextElement();
			m = activeMimos.get(ind);
			float tmp = PApplet.dist(x, y, m.getPosX(), m.getPosY());
			if (d > tmp) {
				d = tmp;
				closest = ind;
			}
		}
		if (d <= Configurator.getFloatSetting("blobDistanceThreshold"))
			return closest;
		Verbose.debug(d + " > "
				+ Configurator.getFloatSetting("blobDistanceThreshold"));
		return new Long(-1);

	}


	/**
	 * Handler for tracking event
	 * 
	 * @see mimodek.tracking.TrackingListener#trackingEvent(mimodek.tracking.TrackingInfo)
	 */
	public synchronized void trackingEvent(TrackingInfo info) {
		if (!Configurator.getBooleanSetting("trackingOn") || info == null)
			return;

		// Verbose.overRule(info);
		if (info.type == TrackingInfo.UPDATE) {
			if (activeMimos.containsKey(info.id)) { // existing active mimo
				ActiveMimo aM = (ActiveMimo) activeMimos.get(info.id).decoratedObject;
				// activeMimos.get(info.id).pos = new PVector(info.x, info.y);
				aM.targetPos.x = info.x;
				aM.targetPos.y = info.y;
				// refresh the counter
				aM.lastActiveMovement = mimodek.app.millis();
			} else {
				Long ind = findClosestActiveMimo(info.x, info.y);
				if (ind < 0) {
					;
					// add an ActiveMimo
					MimodekObjectGraphicsDecorator decorated = MimosLifeCycle.createActiveMimo(info, activeMimosColors);
					((ActiveMimo)decorated.decoratedObject).lastActiveMovement = mimodek.app.millis();
					((ActiveMimo)decorated.decoratedObject).createdAt = ((ActiveMimo)decorated.decoratedObject).lastActiveMovement;
					activeMimos.put(info.id, decorated);
				} else { // existing active mimo
					ActiveMimo aM = (ActiveMimo) activeMimos.get(ind).decoratedObject;
					aM.targetPos.x = info.x;
					aM.targetPos.y = info.y;
					aM.lastActiveMovement = mimodek.app.millis();
				}
			}
		} else {
			if (activeMimos.get(info.id) == null)
				return;
			ActiveMimo m = (ActiveMimo) activeMimos.get(info.id).decoratedObject;

			if (mimodek.app.millis() - m.createdAt >= Configurator
					.getIntegerSetting("mimosMinLifeTime") * 1000) {
				MimodekObjectGraphicsDecorator toTransform = activeMimos.get(info.id);
				PVector toStructure = null;
				// the active mimo has been there long enough to be turned in to
				// an ancestor
				if (seed == null) {
					// no DLA yet, orient this mimo to go to the nearest edge
					// set the target to the nearest edge
					if (Math.abs(toTransform.getPosX()
							- FacadeFactory.getFacade().halfWidth) > Math
							.abs(toTransform.getPosY()
									- FacadeFactory.getFacade().halfHeight)) {
						// the mimo is closer to one of the side edges
						toStructure = new PVector(
								(toTransform.getPosX() - FacadeFactory.getFacade().halfWidth) > 0 ? FacadeFactory
										.getFacade().width
										: 0, toTransform.getPosY());
					} else {
						// the mimo is closer to top or bottom, seed at the
						// bottom
						toStructure = new PVector(toTransform.getPosX(), FacadeFactory
								.getFacade().height);
					}
				}
				MimodekObject obj = null;
				if (toStructure != null) {
					// this mimo will move toward the coordinates specified by
					// toStructure
					TargetSeaker tS = new TargetSeaker(toTransform.getDecoratedObject());
					tS.toStructure = toStructure;
					obj = tS;
				} else {
					obj = new RandomWalker(toTransform.getDecoratedObject(), 2, false);
				}
				MimodekObjectGraphicsDecorator decorated = MimosLifeCycle.createDeadMimo2(toTransform, obj, toTransform.toImage(mimodek.app),mimodek.app.color(255),mimodek.app);
				if(decorated!=null)
					deadMimos2.put(info.id, decorated);
			} else {
				Verbose.debug("------>DeadMimo1 created<---------");
				// turn into deadmimo1
				MimosLifeCycle.createDeadMimo1(m,mimodek.app.color(255, 218, 3));
			}
			// remove the active mimo from the hashtable
			activeMimos.remove(info.id);
		}
	}

	public void exportXML() {
		if (seed != null) {
			PrintWriter output = mimodek.app.createWriter("data/dla.xml");
			output.println("<?xml version=\"1.0\"?>");
			output.print(seed.toXMLString(""));
			output.flush(); // Writes the remaining data to the file
			output.close();
		}
	}
}
