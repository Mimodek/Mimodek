package mimodek.utils;

import mimodek.MimodekObject;

public class Verbose {
	public static boolean speak = false;
	
	public static void say(String sentence){
		if(speak)
			System.out.println("Mimodek says: "+sentence);
	}
	
	public static void debug(Object sentence){
		if(speak)
			System.out.println("(debug) "+sentence);
	}
}
