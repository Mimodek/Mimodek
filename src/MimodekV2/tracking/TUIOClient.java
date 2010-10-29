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

public class TUIOClient implements TuioListener, Tracker {
	TuioClient tuioClient;
	TrackingListener listener;

	public TUIOClient(int port, PApplet app) {
		TrackingInfo.FLIP_HORIZONTAL = true;
		TrackingInfo.FLIP_VERTICAL = false;
		tuioClient = new TuioClient(port);
		tuioClient.addTuioListener(this);
		tuioClient.connect();
		app.registerDispose(this);
		System.out.println("TUIO client started");
	}

	public TUIOClient(PApplet app) {
		this(3333, app);
	}

	public void dispose() {
		disconnect();
	}

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
	private TrackingInfo createTrackingInfo(int infoType, TuioObject tobj) {
		//TuioObject tobj = tuioClient.getTuioObject(key);
		if (tobj == null)
			return null;
		return new TrackingInfo(infoType, tobj.getSymbolID(), tobj.getScreenX(FacadeFactory
				.getFacade().width), tobj
				.getScreenY(FacadeFactory.getFacade().height));
	}

	public void setListener(TrackingListener listener) {
		this.listener = listener;
	}

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

	public void removeTuioCursor(TuioCursor tobj) {
		System.out.println("TUIO: removeTuioCursor");
	}

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

	public void updateTuioObject(TuioObject tobj) {
		System.out.println(tobj.getX());
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

	public void updateTuioCursor(TuioCursor tobj) {
		
		System.out.println("TUIO: updateTuioCursor");
	}

	public void refresh(TuioTime tobj) {
		Configurator.setSetting("tuioActivity", !Configurator
				.getBooleanSetting("tuioActivity"));

	}

	public void addTuioCursor(TuioCursor tobj) {
		System.out.println("TUIO: addTuioCursor");
	}
}
