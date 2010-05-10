package mimodek;

import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;

import processing.core.PApplet;
import processing.core.PVector;

import mimodek.configuration.Colors;
import mimodek.configuration.Configurator;
import mimodek.data.PollutionLevelsEnum;
import mimodek.decorator.ActiveMimo;
import mimodek.decorator.Cell;
import mimodek.decorator.DeadMimo1;
import mimodek.decorator.DeadMimo2;
import mimodek.decorator.RandomWalker;
import mimodek.decorator.TargetSeaker;
import mimodek.decorator.graphics.CircleDrawer;
import mimodek.decorator.graphics.ComboGraphicsDecorator;
import mimodek.decorator.graphics.GraphicsDecoratorEnum;
import mimodek.decorator.graphics.ImageDrawer;
import mimodek.decorator.graphics.LittleLight;
import mimodek.decorator.graphics.MetaBall;
import mimodek.decorator.graphics.MetaBallRenderer;
import mimodek.decorator.graphics.MimodekObjectGraphicsDecorator;
import mimodek.decorator.graphics.NoImageException;
import mimodek.decorator.graphics.PolarDrawer;
import mimodek.decorator.graphics.RenderDrawer;
import mimodek.decorator.graphics.SeedGradientDrawer;
import mimodek.decorator.graphics.TextDrawer;
import mimodek.decorator.graphics.TextureDrawer;
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
		if (seed != null) 
			seed.fixed = Configurator.getBooleanSetting("seedFixed");
		// update Active Mimos
		Enumeration<Long> e = activeMimos.keys();
		while (e.hasMoreElements()) {
			long i = e.nextElement();
			MimodekObjectGraphicsDecorator m = activeMimos.get(i);
			m.update();
			if (seed != null) {
				seed.repel(null, m.getPosX(), m.getPosY(), m.getDiameter()*Configurator.getFloatSetting("activeMimoRepel"));// *turbulence
			}
			ActiveMimo aM = (ActiveMimo) m.decoratedObject;
			if (mimodek.app.millis() - aM.lastActiveMovement > Configurator
					.getIntegerSetting("mimosMaxLifeTime") * 1000) {
				// this active mimo hasn't been moving for long enough, let's
				// turn it into a dead mimo 1
				createDeadMimo1(aM, i);
				// remove it from the active mimo list
				activeMimos.remove(i);
				Verbose.debug("Active Mimos -> Dead Mimo 1.");
			} else {
				// check collision and energy transfer with dead mimos 1
				for (int j = 0; j < deadMimos1.size(); j++) {

					DeadMimo1 dM1 = (DeadMimo1) deadMimos1.get(j).decoratedObject;
					dM1.nourrish(aM);
					// no more energy, remove it
					if (dM1.getDiameter() <= 0)
						deadMimos1.remove(j--);
				}
			}
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
			//float edgeDetection = Configurator.getFloatSetting("edgeDetection");
			if (seed == null
					&& FacadeFactory.getFacade().isInTheScreen(m.getPos(),
							m.getDiameter() / 2)) {
				// the seed is null and this ancestor is on the edge of the
				// screen
				// gfx.decorator.decorato
				try {
					seed = new Cell((DeadMimo2) m.decoratedObject, mimodek.app);
					//seed.fixed = true; // it will stay anchored at its current
										// position
				} catch (NoImageException e1) {
					seed = null;
				} finally {
					deadMimos2.remove(i);
				}
			} else if (seed != null) {
				// test if it touches the DLA
				Cell c = seed.attachTest(null, m);
				if (c != null) { // yes, the ancestor becomes a cell of the DLA
					Cell nC;
					try {
						nC = new Cell((DeadMimo2) m.decoratedObject,
								mimodek.app);
						c.attach(nC);
					} catch (NoImageException e1) {
						// do nothing
					} finally {
						deadMimos2.remove(i);
					}
				} else {
					seed.attract(null, m.getPosX(), m.getPosY(), m
							.getDiameter()*2);// *turbulence
				}

			}
		}

		// update dead mimos 1
		e = deadMimos1.keys();
		while (e.hasMoreElements()) {
			long j = e.nextElement();
			deadMimos1.get(j).update();
			DeadMimo1 dm1 = (DeadMimo1) deadMimos1.get(j).decoratedObject;
			// remove it if it wanders of screen or doesn't have any energy left
			if (!dm1.isInScreen() || dm1.getEnergy() <= 0) {
				deadMimos1.remove(j);
				continue;
			}

			// check if it collides with an ActiveMimo
			Enumeration<Long> e2 = activeMimos.keys();
			while (e2.hasMoreElements()) {
				MimodekObjectGraphicsDecorator aM = activeMimos.get(e2
						.nextElement());
				if (PApplet.dist(aM.getPosX(), aM.getPosY(), dm1.getPosX(), dm1
						.getPosY()) < aM.getDiameter() / 2 + dm1.getDiameter()
						/ 2) {
					// on collision increase the strength of the metaball using
					// the remaining energy
					((MetaBall) aM).increaseStrength(dm1.getEnergy() / 10);
					// remove it
					deadMimos1.remove(j);
					continue;
				}
			}

		}

		// update the ancestors (DLA), if any
		if (seed != null)
			seed.update();

	}

	/**
	 * Draw all the objects
	 */
	public synchronized void draw() {

		// render dead mimos 1
		for (int j = 0; j < deadMimos1.size(); j++) {
			deadMimos1.get(j).draw(mimodek.app);
		}

		// render dead mimos 2
		Enumeration<Long> e = deadMimos2.keys();
		while (e.hasMoreElements()) {
			long i = e.nextElement();
			deadMimos2.get(i).draw(mimodek.app);
		}

		// render meta balls
		MetaBallRenderer.getInstance().render();

		// render active mimos
		e = activeMimos.keys();
		while (e.hasMoreElements()) {
			long i = e.nextElement();
			activeMimos.get(i).draw(mimodek.app);
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
	 * Creates an Active Mimo from tracking data
	 * 
	 * @param info
	 *            Tracking info to position the Active Mimo
	 */
	private void createActiveMimo(TrackingInfo info) {
		ActiveMimo aM = new ActiveMimo(new SimpleMimo(new PVector(info.x,
				info.y)),info.id);
		aM.setDiameter(Configurator.getFloatSetting("mimosMinRadius"));
		aM.targetPos.x = info.x;
		aM.targetPos.y = info.y;
		aM.lastActiveMovement = mimodek.app.millis();
		aM.createdAt = aM.lastActiveMovement;
		
		//get a color from the colors pool
		int colour = activeMimosColors.getRandomIndividualColor();
		//get the current pollution level
		PollutionLevelsEnum pollutionLevel = PollutionLevelsEnum.valueOf(Configurator.getStringSetting("pollutionScore"));
		//
		MimodekObjectGraphicsDecorator decorated = decorateByPollutionLevel(aM, colour, pollutionLevel);
		
		Verbose.debug(decorated);
		activeMimos.put(info.id, decorated);
	}
	
	private MimodekObjectGraphicsDecorator decorateByPollutionLevel(ActiveMimo aM, int colour, PollutionLevelsEnum pollutionLevel){
		GraphicsDecoratorEnum chosenDecorator = null;
		float paramA = 0;
		float paramB = 0;
		int iteration = 0;
		chosenDecorator = GraphicsDecoratorEnum.COMBO;
		MimodekObjectGraphicsDecorator primaryDecorator = null;
		switch(pollutionLevel){
		case GOOD:
			primaryDecorator =  new SeedGradientDrawer(aM, colour);
			break;
		case ACCEPTABLE:
			paramA = 20;
			paramB = 0;
			iteration = 427;
			primaryDecorator = new PolarDrawer(aM, colour, paramA, paramB,iteration);
			break;
		case BAD:
			paramA = 220;
			paramB = 0;
			iteration = 296;
			primaryDecorator = new PolarDrawer(aM, colour, paramA, paramB,iteration);
			break;
		case VERY_BAD:
			paramA = 240;
			paramB = 0;
			iteration = 482;
			primaryDecorator = new PolarDrawer(aM, colour, paramA, paramB,iteration);
			break;
		}
		
		switch (chosenDecorator) { // Configurator.getIntegerSetting("textureMode")
		case TEXTURE:
			return new TextureDrawer(aM, colour);
		case GENERATED:
			return new SeedGradientDrawer(aM, colour);
		case METABALL:
			return new MetaBall(aM, colour);
		case POLAR:
			return new PolarDrawer(aM, colour, paramA, paramB, iteration);
		case COMBO:
			MimodekObjectGraphicsDecorator secondaryDecorator = new MetaBall(
					aM, colour);
			return new ComboGraphicsDecorator(primaryDecorator,
					secondaryDecorator);
		case TEXT:
			return new TextDrawer(aM, colour, aM.id + "");
		case CIRCLE:
		default:
			return new CircleDrawer(aM, colour);
		}
	}

	/**
	 * Creates a Dead Mimo1 from an Active Mimo. Those Mimos are created when
	 * the tracking lose a blob
	 * 
	 * @param aM
	 *            Active Mimo to use as a base for the Dead Mimo 1
	 */
	private void createDeadMimo1(ActiveMimo aM, long id) {
		DeadMimo1 dM1 = new DeadMimo1(new RandomWalker(aM.getDecoratedObject(),
				0.5f, true));
		MimodekObjectGraphicsDecorator decorated = null;
		int c = mimodek.app.color(0, 0, 255);
		switch (GraphicsDecoratorEnum.LIGHT) { // Configurator.getIntegerSetting("textureMode")
		case TEXTURE:
			decorated = new TextureDrawer(dM1, c);
			break;
		case GENERATED:
			decorated = new SeedGradientDrawer(dM1, c);
			break;
		case METABALL:
			decorated = new MetaBall(dM1, c);
			break;
		case LIGHT:
			decorated = new LittleLight(dM1, c);
			break;
		case CIRCLE:
		default:
			decorated = new CircleDrawer(dM1, c);
		}
		Verbose.debug(decorated);
		deadMimos1.put(id, decorated);
	}

	/**
	 * Creates a Dead Mimo2 from an Active Mimo. Those Mimos are created when an
	 * Active Mimo gets out of the screen
	 * 
	 * @param aM
	 *            Active Mimo to use as a base for the Dead Mimo 2
	 * @param id
	 *            The id of the Active Mimo in the hashtable
	 */
	private void createDeadMimo2(MimodekObjectGraphicsDecorator toTransform,
			long id) {
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
		DeadMimo2 dm2 = new DeadMimo2(obj, toTransform.toImage(mimodek.app));
		MimodekObjectGraphicsDecorator decorated = null;
		int c = mimodek.app.color(255);
		switch (GraphicsDecoratorEnum.LIGHT) { // Configurator.getIntegerSetting("textureMode")
		case TEXTURE:
			decorated = new TextureDrawer(dm2, c);
			break;
		case IMAGE:
			try {
				decorated = new ImageDrawer(dm2, toTransform
						.toImage(mimodek.app), c, mimodek.app);
			} catch (NoImageException e) {
				return;
			}
			break;
		case RENDER:
			try {
				decorated = new RenderDrawer(dm2, toTransform, mimodek.app);
			} catch (NoImageException e) {
				return;
			}
			break;
		case GENERATED:
			decorated = new SeedGradientDrawer(dm2, c);
			break;
		case METABALL:
			decorated = new MetaBall(dm2, c);
			break;
		case LIGHT:
			decorated = new LittleLight(dm2, mimodek.app.color(255));
			break;
		case CIRCLE:
		default:
			decorated = new CircleDrawer(dm2, c);
		}
		Verbose.debug(decorated);
		deadMimos2.put(id, decorated);
	}

	/**
	 * Handler for tracking event
	 * 
	 * @see mimodek.tracking.TrackingListener#trackingEvent(mimodek.tracking.TrackingInfo)
	 */
	public synchronized void trackingEvent(TrackingInfo info) {
		if (!Configurator.getBooleanSetting("trackingOn") || info == null)
			return;
		// Verbose.debug(info);
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
					createActiveMimo(info);
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
				createDeadMimo2(activeMimos.get(info.id), info.id);
			} else {
				Verbose.debug("------>DeadMimo1 created<---------");
				// turn into deadmimo1
				createDeadMimo1(m, info.id);
			}
			// remove the active mimo from the hashtable
			activeMimos.remove(info.id);
		}
	}

	public void exportXML() {
		if (seed != null) {
			PrintWriter output = mimodek.app.createWriter("data/dla.xml");
			output.println("<?xml version=\"1.0\"?>");
			output.print(seed.toXMLString(null, ""));
			output.flush(); // Writes the remaining data to the file
			output.close();
		}
	}
}
