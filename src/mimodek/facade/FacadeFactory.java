package mimodek.facade;

import processing.core.PApplet;

/**
 * A factory to configure which facades to use.
 * @author Jonsku
 */
public class FacadeFactory {
	protected static Facade currentFacade = null;
	
	/**
	 * Configure a Media Lab Prado Media Facade 
	 * @param app Parent sketch
	 */
	public static void createPradoFacade(PApplet app){
		currentFacade = new MadridPradoFacade(app);
	}
	
	/**
	 * Configure a Processing Facade, it is a rectangle of the same dimensions as the parent sketch 
	 * @param app Parent sketch
	 */
	public static void createProcessingFacade(PApplet app){
		currentFacade = new ProcessingFacade(app);
	}
	
	/**
	 * @return The configured facade.
	 */
	public static Facade getFacade(){
		return currentFacade;
	}

}
