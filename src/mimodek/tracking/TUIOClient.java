package mimodek.tracking;

import processing.core.PApplet;
import mimodek.facade.FacadeFactory;
import mimodek.utils.Verbose;
import TUIO.*;



public class TUIOClient implements TuioListener,Tracker{
	TuioClient tuioClient;
	TrackingListener listener;
	
	public TUIOClient(int port, PApplet app){
		TrackingInfo.FLIP_HORIZONTAL = false;
		TrackingInfo.FLIP_VERTICAL = false;
		tuioClient  = new TuioClient(port);
		tuioClient.addTuioListener(this);
		tuioClient.connect();
		app.registerDispose(this);
		Verbose.debug("TUIO client started");
	}
	
	public TUIOClient(PApplet app){
		this(3333,app);
	}
	
	public void dispose(){
		disconnect();
	}
	
	public void disconnect(){
		if (tuioClient.isConnected()) tuioClient.disconnect();
		Verbose.debug("TUIO client disconnected");
	}
	
	private TrackingInfo createTrackingInfo(int infoType,int key){
		TuioObject tobj = tuioClient.getTuioObject(key);
		return new TrackingInfo(infoType,key,tobj.getScreenX(FacadeFactory.getFacade().width),tobj.getScreenY(FacadeFactory.getFacade().height));
	}
	
	public void setListener(TrackingListener listener) {
		this.listener = listener;
	}

	public void addTuioCursor(TuioCursor tobj) {
		Verbose.debug("TUIO: addTuioCursor");
	}

	public void addTuioObject(TuioObject tobj) {
		listener.trackingEvent(createTrackingInfo(TrackingInfo.UPDATE, tobj.getSymbolID()));
	}

	public void refresh(TuioTime tobj) {
		//Verbose.debug("TUIO: Refresh");
	}

	public void removeTuioCursor(TuioCursor tobj) {
		Verbose.debug("TUIO: removeTuioCursor");	
	}

	public void removeTuioObject(TuioObject tobj) {
		listener.trackingEvent(createTrackingInfo(TrackingInfo.REMOVE, tobj.getSymbolID()));	
	}

	public void updateTuioCursor(TuioCursor tobj) {
		Verbose.debug("TUIO: updateTuioCursor");
	}

	public void updateTuioObject(TuioObject tobj) {
		listener.trackingEvent(createTrackingInfo(TrackingInfo.UPDATE, tobj.getSymbolID()));
		
	}
}
