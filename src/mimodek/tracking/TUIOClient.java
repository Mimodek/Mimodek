package mimodek.tracking;

import mimodek.MainHandler;
import TUIO.TuioCursor;
import TUIO.TuioObject;
import TUIO.TuioProcessing;

//Wrapper for the TUIO client
public class TUIOClient {
	TrackingListener listener;
	TuioProcessing tuioClient;
	
	public static boolean receiving = false; 

	public TUIOClient() {
		tuioClient = new TuioProcessing(MainHandler.app);
	}

	public void setListener(TrackingListener listener) {
		this.listener = listener;
	}

	// TUIO EVENTS HANDLERS
	// called when an object is removed from the scene
	public void removeTuioObject(TuioObject tobj) {
		// System.out.println("removeTuioObject");
		receiving = !receiving;
		if (listener != null)
			listener.trackingEvent(new TrackingInfo(TrackingInfo.REMOVE, tobj
					.getSessionID(), tobj.getScreenX(MainHandler.screenWidth),
					tobj.getScreenY(MainHandler.screenHeight)));
	}

	// called when an object is moved
	public void updateTuioObject(TuioObject tobj) {
		// System.out.println("updateTuioObject");
		receiving = !receiving;
		if (listener != null)
			listener.trackingEvent(new TrackingInfo(TrackingInfo.UPDATE, tobj
					.getSessionID(), tobj.getScreenX(MainHandler.screenWidth),
					tobj.getScreenY(MainHandler.screenHeight)));
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
