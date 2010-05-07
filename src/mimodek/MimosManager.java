package mimodek;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import processing.core.PApplet;
import processing.core.PVector;

import mimodek.configuration.Colors;
import mimodek.configuration.Configurator;
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
 * 
 * @author Jonsku
 */
public class MimosManager implements TrackingListener {
	

	/**
	 * Maps a numerical unique identifier to a decorated MimodekObject. Thread
	 * safe.
	 */
	Hashtable<Long, MimodekObjectGraphicsDecorator> activeMimos;

	ArrayList<MimodekObjectGraphicsDecorator> deadMimos1;

	Hashtable<Long, MimodekObjectGraphicsDecorator> deadMimos2;

	/**
	 * A reference to Mimodek main class
	 */
	Mimodek mimodek;

	/**
	 * The first cell (or seed) of the DLA
	 */
	Cell seed = null;

	public MimosManager(Mimodek mimodek) {
		this.mimodek = mimodek;
		activeMimos = new Hashtable<Long, MimodekObjectGraphicsDecorator>();
		deadMimos1 = new ArrayList<MimodekObjectGraphicsDecorator>();
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
		// update Active Mimos
		Enumeration<Long> e = activeMimos.keys();
		while (e.hasMoreElements()) {
			long i = e.nextElement();
			MimodekObjectGraphicsDecorator m = activeMimos.get(i);
			m.update();
			if (seed != null) {
				seed.repel(null, m.getPosX(), m.getPosY(), m.getDiameter());// *turbulence
			}
			ActiveMimo aM = (ActiveMimo) m.decoratedObject;
			if (mimodek.app.millis() - aM.lastActiveMovement > Configurator
					.getIntegerSetting("mimosMaxLifeTime") * 1000) {
				// this active mimo hasn't been moving for long enough, let's
				// turn it into a dead mimo 1
				createDeadMimo1(aM);
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
			if(((DeadMimo2)m.decoratedObject).getEnergy()<=0){
				//no more energy, remove the dead mimo 2
				deadMimos2.remove(i);
				continue;
			}
			// float scale = 1 / Configurator.getFloatSetting("ancestorScale");
			// //calculate the scale factor for ancestor
			float edgeDetection = Configurator.getFloatSetting("edgeDetection");
			if (seed == null
					&& FacadeFactory.getFacade().isInTheScreen(m.getPos(),
							edgeDetection)) {
				// the seed is null and this ancestor is on the edge of the
				// screen
				//gfx.decorator.decorato
				try {
					seed = new Cell((DeadMimo2)m.decoratedObject,mimodek.app);
					//seed.fixed = true; // it will stay anchored at its current
				} catch (NoImageException e1) {
					seed = null;
				}finally{				
					deadMimos2.remove(i);
				}
			} else if (seed != null) {
				// test if it touches the DLA
				Cell c = seed.attachTest(null, m);
				if (c != null) { // yes, the ancestor becomes a cell of the DLA
					Cell nC;
					try {
						nC = new Cell((DeadMimo2)m.decoratedObject,mimodek.app);
						c.attach(nC);
					} catch (NoImageException e1) {
						deadMimos2.remove(i);
					}
				} else {
					seed.attract(null, m.getPosX(), m.getPosY(),
							m.getDiameter() * 3);// *turbulence
				}

			}
		}
		
		// update dead mimos 1
		for (int j = 0; j < deadMimos1.size(); j++) {
			deadMimos1.get(j).update();
			DeadMimo1 dm1 = (DeadMimo1)deadMimos1.get(j).decoratedObject;
			//remove it if it wanders of screen or doesn't have any energy left
			if(!dm1.isInScreen() || dm1.getEnergy() <= 0){
				deadMimos1.remove(j--);
				continue;
			}
			
			// check if it collides with an ActiveMimo
			e = activeMimos.keys();
			while (e.hasMoreElements()) {
				MimodekObjectGraphicsDecorator aM = activeMimos.get(e.nextElement());
				if(PApplet.dist(aM.getPosX(),aM.getPosY(),dm1.getPosX(),dm1.getPosY())<aM.getDiameter()/2+dm1.getDiameter()/2){
					//on collision increase the strength of the metaball using the remaining energy
					((MetaBall)aM).increaseStrength(dm1.getEnergy()/10);
					//remove it
					deadMimos1.remove(j--);
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
		if (activeMimos.size() == 0){
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
		Verbose.debug(d+" > "+Configurator.getFloatSetting("blobDistanceThreshold"));
		return new Long(-1);

	}


	/**
	 * Creates an Active Mimo from tracking data
	 * 
	 * @param info Tracking info to position the Active Mimo
	 */
	private void createActiveMimo(TrackingInfo info) {
		ActiveMimo aM = new ActiveMimo(new SimpleMimo(new PVector(info.x,
				info.y)));
		aM.setDiameter(Configurator.getFloatSetting("mimosMinRadius"));
		aM.targetPos.x = info.x;
		aM.targetPos.y = info.y;
		aM.lastActiveMovement = mimodek.app.millis();
		aM.createdAt = aM.lastActiveMovement;
		MimodekObjectGraphicsDecorator decorated = null;
		int colour = Colors.getRandomColor();
		switch (GraphicsDecoratorEnum.COMBO) { // Configurator.getIntegerSetting("textureMode")
		case TEXTURE:
			decorated = new TextureDrawer(aM, colour);
			break;
		case GENERATED:
			decorated = new SeedGradientDrawer(aM, colour);
			break;
		case METABALL:
			decorated = new MetaBall(aM, colour);
			break;
		case POLAR:
			decorated = new PolarDrawer(aM,colour, -50+mimodek.app.noise(aM.getPosX(),aM.getPosY())*100,-100+mimodek.app.noise(aM.getPosY(),aM.getPosX())*200);
			break;
		case COMBO:
			MimodekObjectGraphicsDecorator primaryDecorator = new PolarDrawer(aM,colour, -50+mimodek.app.noise(aM.getPosX(),aM.getPosY())*100,-100+mimodek.app.noise(aM.getPosY(),aM.getPosX())*200/*-50+mimodek.app.noise(aM.getPosX(),aM.getPosY())*100,-100+mimodek.app.noise(aM.getPosY(),aM.getPosX())*200*/);
			MimodekObjectGraphicsDecorator secondaryDecorator = new MetaBall(aM,colour);
			decorated = new ComboGraphicsDecorator(primaryDecorator,secondaryDecorator);
			break;
		case TEXT:
			decorated = new TextDrawer(aM,colour,info.id+"");
			break;
		case CIRCLE:
		default:
			decorated = new CircleDrawer(aM, colour);
		}
		Verbose.debug(decorated);
		activeMimos.put(info.id, decorated);
	}
	
	/**
	 * Creates a Dead Mimo1 from an Active Mimo.
	 * Those Mimos are created when the tracking lose a blob
	 * 
	 * @param aM Active Mimo to use as a base for the Dead Mimo 1
	 */
	private void createDeadMimo1(ActiveMimo aM) {
		DeadMimo1 dM1 = new DeadMimo1(new RandomWalker(aM.getDecoratedObject(),0.5f,true));
		MimodekObjectGraphicsDecorator decorated = null;
		switch (GraphicsDecoratorEnum.LIGHT) { // Configurator.getIntegerSetting("textureMode")
		case TEXTURE:
			decorated = new TextureDrawer(dM1, Colors.getColor(Configurator
					.getFloatSetting("mimosColor")));
			break;		
		case GENERATED:
			decorated = new SeedGradientDrawer(dM1, Colors
					.getColor(Configurator.getFloatSetting("mimosColor")));
			break;
		case METABALL:
			decorated = new MetaBall(dM1,Colors
					.getColor(Configurator.getFloatSetting("mimosColor")));
			break;
		case LIGHT:
			decorated = new LittleLight(dM1,mimodek.app.color(0,0,255));
			break;
		case CIRCLE:
		default:
			decorated = new CircleDrawer(dM1, Colors.getColor(Configurator
					.getFloatSetting("mimosColor")));
		}
		Verbose.debug(decorated);
		deadMimos1.add(decorated);
	}

	/**
	 * Creates a Dead Mimo2 from an Active Mimo.
	 * Those Mimos are created when an Active Mimo gets out of the screen
	 * 
	 * @param aM Active Mimo to use as a base for the Dead Mimo 2
	 * @param id The id of the Active Mimo in the hashtable
	 */
	private void createDeadMimo2(MimodekObjectGraphicsDecorator toTransform, long id) {
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
			obj = new RandomWalker(toTransform.getDecoratedObject(),2,false);
		}
		DeadMimo2 dm2 = new DeadMimo2(obj,toTransform.toImage(mimodek.app));
		MimodekObjectGraphicsDecorator decorated = null;
		switch(GraphicsDecoratorEnum.LIGHT) { // Configurator.getIntegerSetting("textureMode")
		case TEXTURE:
			decorated = new TextureDrawer(dm2, Colors.getColor(Configurator
					.getFloatSetting("mimosColor")));
			break;
		case IMAGE:
			try {
				decorated = new ImageDrawer(dm2,toTransform.toImage(mimodek.app), Colors.getColor(Configurator
						.getFloatSetting("mimosColor")),mimodek.app);
			} catch (NoImageException e) {
				return;
			}
			break;
		case RENDER:
			try {
				decorated = new RenderDrawer(dm2,toTransform,mimodek.app);
			} catch (NoImageException e) {
				return;
			}
			break;
		case GENERATED:
			decorated = new SeedGradientDrawer(dm2, Colors
					.getColor(Configurator.getFloatSetting("mimosColor")));
			break;
		case METABALL:
			decorated = new MetaBall(dm2,Colors
					.getColor(Configurator.getFloatSetting("mimosColor")));
			break;
		case LIGHT:
			decorated = new LittleLight(dm2,mimodek.app.color(255));
			break;
		case CIRCLE:
		default:
			decorated = new CircleDrawer(dm2, Colors.getColor(Configurator
					.getFloatSetting("mimosColor")));
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
		//Verbose.debug(info);
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
				if (ind < 0) {;
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
			}else{
				Verbose.debug("------>DeadMimo1 created<---------");
				//turn into deadmimo1
				createDeadMimo1(m);
			}
			// remove the active mimo from the hashtable
			activeMimos.remove(info.id);
		}
	}
	
	public void exportXML(){
		if(seed!=null){
			PrintWriter output = mimodek.app.createWriter("data/dla.xml"); 
			output.println("<?xml version=\"1.0\"?>");
			output.print(seed.toXMLString(null,""));
			output.flush(); // Writes the remaining data to the file
			output.close();
		}
	}
}
