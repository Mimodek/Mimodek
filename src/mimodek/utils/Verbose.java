package mimodek.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Verbose {
	public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";

	public static boolean speak = false;
	
	public static void say(String sentence){
		if(speak)
			System.out.println("Mimodek says: "+sentence);
	}
	
	public static void debug(Object sentence){
		if(speak)
			System.out.println("(debug) "+sentence);
	}
	
	public static void overRule(Object sentence){
			System.out.println("(debug) "+sentence);
	}
	
	public static String now() {
	    Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
	    return sdf.format(cal.getTime());

	  }

}
