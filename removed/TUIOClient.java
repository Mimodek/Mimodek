package mimodek.tracking;

import processing.core.PApplet;

import mimodek.configuration.Configurator;
import mimodek.facade.Facade;
import mimodek.facade.FacadeFactory;
import TUIO.TuioCursor;
import TUIO.TuioObject;
import TUIO.TuioProcessing;

//Wrapper for the TUIO client
public class TUIOClient {
	TrackingListener listener;
	TuioProcessing tuioClient;
	

	public TUIOClient(PApplet app) {
		tuioClient = new TuioProcessing(app);
		Configurator.setSetting("tuioActivity", false);
	}

	public void setListener(TrackingListener listener) {
		this.listener = listener;
	}

	// TUIO EVENTS HANDLERS
	// called when an object is removed from the scene
	public void removeTuioObject(TuioObject tobj) {
		//Show that the TUIO client is active
		Configurator.setSetting("tuioActivity", !Configurator.getBooleanSetting("tuioActivity"));
		if (listener != null){
			Facade facade = FacadeFactory.getFacade();
			listener.trackingEvent(new TrackingInfo(TrackingInfo.REMOVE, tobj
					.getSessionID(), tobj.getScreenX(facade.width),
					tobj.getScreenY(facade.height)));
		}
	}

	// called when an object is moved
	public void updateTuioObject(TuioObject tobj) {
		//Show that the TUIO client is active
		Configurator.setSetting("tuioActivity", !Configurator.getBooleanSetting("tuioActivity"));
		if (listener != null){
			Facade facade = FacadeFactory.getFacade();
			listener.trackingEvent(new TrackingInfo(TrackingInfo.UPDATE, tobj
					.getSessionID(), tobj.getScreenX(facade.width),
					tobj.getScreenY(facade.height)));
		}
	}

	// We probably don't care about those events...
	public void addTuioObject(TuioObject tobj) {
		// System.out.println("addTuioObject");
	}

	// called when a cursor is added to the scene
	public void addTuioCursor(TuioCursor tcur) {
		// System.out.println("addTuioCursor");
	}

	// called when a cursor is moved
	public void updateTuioCursor(TuioCursor tcur) {
		// System.out.println("updateTuioCursor");
	}

	// called when a cursor is removed from the scene
	public void removeTuioCursor(TuioCursor tcur) {
		// System.out.println("removeTuioCursor");
	}
}
