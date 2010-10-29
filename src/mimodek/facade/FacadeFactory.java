package mimodek.facade;

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
	public static Facade createPradoFacade(PApplet app){
		currentFacade = new MadridPradoFacade(app);
		return currentFacade;
	}
	
	/**
	 * Configure a Processing Facade, it is a rectangle of the same dimensions as the parent sketch 
	 * @param app Parent sketch
	 */
	public static Facade createProcessingFacade(PApplet app){
		currentFacade = new ProcessingFacade(app);
		return currentFacade;
	}
	
	/**
	 * @return The configured facade.
	 */
	public static Facade getFacade(){
		return currentFacade;
	}

}
