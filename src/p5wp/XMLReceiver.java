package p5wp;

import processing.core.PApplet;
import java.lang.reflect.*;
import java.util.HashMap;

public class XMLReceiver
{
  
  WPMessageListener listener;
  PApplet parent;
  String hostURL;
  
  public XMLReceiver(PApplet _parent, String _hostURL) {
	    parent = _parent;
	    hostURL = _hostURL;
	  }
  
  public void setListener(WPMessageListener listener){
	  this.listener = listener;
  }

  public void getFreshData(String term) {

    Thread t = new Thread(new AsyncXMLRequest(parent, term, hostURL, this));
    t.start();
  }

  void onMessages(HashMap messages) {
   // System.out.println(messages);
//    foundColor.put("tweets", messages);

    // call the callback
	if(listener != null){
		listener.onResponse(messages);
	}else{
	    try {
	      Class[] argTypes = { HashMap.class };
	      Method	method = parent.getClass().getDeclaredMethod("onResponse", argTypes);
	      method.invoke(parent, new Object[] {messages});
	
	    } catch(NoSuchMethodException e) {
	      System.out.println("You need to define onResponse(HashMap messages)");
	    } catch(Exception e) {
	      System.out.println("Something ain't right: "+e);
	    }
	}
  } 
}
