package p5wp;

import processing.core.PApplet;
import processing.xml.XMLElement;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.*;
import java.net.URLEncoder;
import java.util.HashMap;

public class AsyncXMLRequest extends Thread
{
  PApplet parent;
  String searchTerm;
  Object callback;

  //public static final String SEARCH_URI = "http://search.twitter.com/search.atom?q=";
  String hostURL;
  AsyncXMLRequest(PApplet _parent, String _searchTerm, String _hostURL, Object _callback) {
    System.out.println("AsyncTwitter");
    parent = _parent;
    callback = _callback;
    searchTerm = _searchTerm;
    hostURL = _hostURL;
  }

  @Override
public void run() {
    //System.out.println("Hello from a thread!");
    loadMessages();
  }

  void loadMessages() {
    XMLElement xml = null;
    String u = "";
	try {
		u = hostURL + "?processingwp=" + URLEncoder.encode(searchTerm,"UTF-8");
	} catch (UnsupportedEncodingException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
    //    System.out.println("\nProcessingWP:\n" + u);

    try {
      xml = new XMLElement(parent, u);
    } 
    catch (Exception e) {
      System.out.println("The URL "+u+" is not working!?\n\nError: " + e);
    }
    if (xml != null) {
      XMLElement[] entries = xml.getChildren("group");
      //ArrayList listOfValues = new ArrayList();
      //String[] messages = new String[entries.length];
      
      HashMap messages = new HashMap();
      
      for (int i=0; i<entries.length; i++) {
    	  for (int j=0; j<entries[i].getChildren("item").length; j++) {
    		  //messages[i] = entries[i].getChildren("item")[0].getContent();
    		  messages.put(entries[i].getChildren("item")[j].getAttribute("name"), entries[i].getChildren("item")[j].getContent());
    		  //listOfValues.add(entries[i].getChildren("item")[j].getContent());
    		  //System.out.println(entries[i].getChildren("item")[j].getAttribute("name"));
    		  
    	  }
//        System.out.println(messages[i]);
      }
/*
      String[] messages = new String[listOfValues.size()];
      for (int count=0; count < listOfValues.size(); count++) {
    	  messages[count] = (String) listOfValues.get(count);
      }
  //*/    
      
      
      // call the callback
      try {
        Class[] argTypes = { 
          HashMap.class                       };
        Method method = callback.getClass().getDeclaredMethod("onMessages", argTypes);
        method.invoke(callback, new Object[] {
          messages                      }
        );

      } 
      catch(NoSuchMethodException e) {
        System.out.println("You need to define onMessages(String[] messages)");
      } 
      catch(Exception e) {
        System.out.println("Something ain't right: "+e);
      }
    }


  }
}




