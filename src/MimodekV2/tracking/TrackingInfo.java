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

public class TrackingInfo {

	public static final int UPDATE = 0;
	public static final int REMOVE = 1;
	public static boolean FLIP_HORIZONTAL = true;
	public static boolean FLIP_VERTICAL = true;
	public long id;
	public int type;
	public float x;
	public float y;

	public TrackingInfo(int type ,long id, float x, float y) {
		this.id = id;
		this.type = type;
		if(FLIP_HORIZONTAL){
			this.x = FacadeFactory.getFacade().width-x;
		}else{
			this.x = x;
		}
		if(FLIP_VERTICAL){
			this.y = FacadeFactory.getFacade().height-y;
		}else{
			this.y = y;
		}
	}
	
	@Override
	public String toString(){
		return id+" : "+type+", ("+x+","+y+")";
	}
}
