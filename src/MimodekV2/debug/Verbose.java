package MimodekV2.debug;

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

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Verbose {
	public static final String USA_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String IMAGE_POST_FORMAT = "dd/MM/yyyy HH:mm";

	/*
	 * Toggle debug info
	 */
	public static boolean speak = false;
	
	/*
	 * Say something.
	 */
	public static void say(String sentence){
		if(speak)
			System.out.println("Mimodek says: "+sentence);
	}
	
	/*
	 * Say something as debug.
	 */
	public static void debug(Object sentence){
		if(speak)
			System.out.println("(debug) "+sentence);
	}
	
	/*
	 * Force output regardless of the speak flag.
	 */
	public static void overRule(Object sentence){
			System.out.println("(debug) "+sentence);
	}
	
	/*
	 * Return the date has a string.
	 */
	public static String now(String format) {
	    Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat(format);
	    return sdf.format(cal.getTime());
	 }

}
