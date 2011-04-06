package MimodekV2.tracking;

/*
This is the code source of Mimodek. When not stated otherwise,
it was written by Jonathan 'Jonsku' Cremieux<jonathan.cremieux@aalto.fi> in 2010. 
Copyright (C) yyyy  name of author

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

*/

import mimodek.facade.FacadeFactory;
import processing.core.PApplet;
import MimodekV2.config.Configurator;
import TUIO.*;

// TODO: Auto-generated Javadoc
/**
 * The Class TUIOClient.
 */
public class TUIOClient implements TuioListener, Tracker {
	
	/** The tuio client. */
	TuioClient tuioClient;
	
	/** The listener. */
	TrackingListener listener;

	/**
	 * Instantiates a new tUIO client.
	 *
	 * @param port the port
	 * @param app the app
	 */
	public TUIOClient(int port, PApplet app) {
		TrackingInfo.FLIP_HORIZONTAL = true;
		TrackingInfo.FLIP_VERTICAL = false;
		tuioClient = new TuioClient(port);
		tuioClient.addTuioListener(this);
		tuioClient.connect();
		app.registerDispose(this);
		System.out.println("TUIO client started");
	}

	/**
	 * Instantiates a new tUIO client.
	 *
	 * @param app the app
	 */
	public TUIOClient(PApplet app) {
		this(3333, app);
	}

	/**
	 * Dispose.
	 */
	public void dispose() {
		disconnect();
	}

	/**
	 * Disconnect.
	 */
	public void disconnect() {
		if (tuioClient.isConnected())
			tuioClient.disconnect();
		System.out.println("TUIO client disconnected");
	}
/*
	private TrackingInfo createTrackingInfo(int infoType, int key) {
		TuioObject tobj = tuioClient.getTuioObject(key);
		if (tobj == null)
			return null;
		return new TrackingInfo(infoType, key, tobj.getScreenX(FacadeFactory
				.getFacade().width), tobj
				.getScreenY(FacadeFactory.getFacade().height));
	}
	*/
	/**
 * Creates the tracking info.
 *
 * @param infoType the info type
 * @param tobj the tobj
 * @return the tracking info
 */
private TrackingInfo createTrackingInfo(int infoType, TuioObject tobj) {
		//TuioObject tobj = tuioClient.getTuioObject(key);
		if (tobj == null)
			return null;
		return new TrackingInfo(infoType, tobj.getSymbolID(), tobj.getScreenX(FacadeFactory
				.getFacade().width), tobj
				.getScreenY(FacadeFactory.getFacade().height));
	}

	/* (non-Javadoc)
	 * @see MimodekV2.tracking.Tracker#setListener(MimodekV2.tracking.TrackingListener)
	 */
	public void setListener(TrackingListener listener) {
		this.listener = listener;
	}

	/* (non-Javadoc)
	 * @see TUIO.TuioListener#addTuioObject(TUIO.TuioObject)
	 */
	public void addTuioObject(TuioObject tobj) {
		if (tobj == null)
			return;
		if (listener != null) {
			TrackingInfo tI = createTrackingInfo(TrackingInfo.UPDATE, tobj);
			if (tI != null)
				listener.trackingEvent(tI);
		} else {
			System.out.println("TUIOClient: not listener is set...");
		}
	}

	/* (non-Javadoc)
	 * @see TUIO.TuioListener#removeTuioCursor(TUIO.TuioCursor)
	 */
	public void removeTuioCursor(TuioCursor tobj) {
		System.out.println("TUIO: removeTuioCursor");
	}

	/* (non-Javadoc)
	 * @see TUIO.TuioListener#removeTuioObject(TUIO.TuioObject)
	 */
	public void removeTuioObject(TuioObject tobj) {
		if (tobj == null)
			return;
		if (listener != null) {
			TrackingInfo tI = createTrackingInfo(TrackingInfo.REMOVE, tobj);
			if (tI != null)
				listener.trackingEvent(tI);
		} else {
			System.out.println("TUIOClient: not listener is set...");
		}
	}

	/* (non-Javadoc)
	 * @see TUIO.TuioListener#updateTuioObject(TUIO.TuioObject)
	 */
	public void updateTuioObject(TuioObject tobj) {
		if (tobj == null)
			return;
		
		//System.out.println(tobj.getX());
		if (listener != null) {
			TrackingInfo tI = createTrackingInfo(TrackingInfo.UPDATE, tobj);
			if (tI != null)
				listener.trackingEvent(tI);
		} else {
			System.out.println("TUIOClient: not listener is set...");
		}
	}

	/* (non-Javadoc)
	 * @see TUIO.TuioListener#updateTuioCursor(TUIO.TuioCursor)
	 */
	public void updateTuioCursor(TuioCursor tobj) {
		
		System.out.println("TUIO: updateTuioCursor");
	}

	/* (non-Javadoc)
	 * @see TUIO.TuioListener#refresh(TUIO.TuioTime)
	 */
	public void refresh(TuioTime tobj) {
		Configurator.setSetting("TUIO_ACTIVITY_FLAG", !Configurator
				.getBooleanSetting("TUIO_ACTIVITY_FLAG"));

	}

	/* (non-Javadoc)
	 * @see TUIO.TuioListener#addTuioCursor(TUIO.TuioCursor)
	 */
	public void addTuioCursor(TuioCursor tobj) {
		System.out.println("TUIO: addTuioCursor");
	}
}
