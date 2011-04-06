package mimodek.facade;

// TODO: Auto-generated Javadoc
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

/**
 * The listener interface for receiving facadeEvent events.
 * The class that is interested in processing a facadeEvent
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addFacadeEventListener<code> method. When
 * the facadeEvent event occurs, that object's appropriate
 * method is invoked.
 *
 * @see FacadeEventEvent
 */
public interface FacadeEventListener {
	
	/**
	 * Facade on off.
	 *
	 * @param on the on
	 */
	public void facadeOnOff(boolean on);
}
