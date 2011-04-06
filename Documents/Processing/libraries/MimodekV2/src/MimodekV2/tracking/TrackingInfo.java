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

// TODO: Auto-generated Javadoc
/**
 * The Class TrackingInfo.
 */
public class TrackingInfo {

	/** The Constant UPDATE. */
	public static final int UPDATE = 0;
	
	/** The Constant REMOVE. */
	public static final int REMOVE = 1;
	
	/** The FLI p_ horizontal. */
	public static boolean FLIP_HORIZONTAL = true;
	
	/** The FLI p_ vertical. */
	public static boolean FLIP_VERTICAL = true;
	
	/** The id. */
	public long id;
	
	/** The type. */
	public int type;
	
	/** The x. */
	public float x;
	
	/** The y. */
	public float y;

	/**
	 * Instantiates a new tracking info.
	 *
	 * @param type the type
	 * @param id the id
	 * @param x the x
	 * @param y the y
	 */
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
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return id+" : "+type+", ("+x+","+y+")";
	}
}
