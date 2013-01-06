package p5wp;

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
import processing.xml.XMLElement;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import MimodekV2.debug.Verbose;

// TODO: Auto-generated Javadoc
/*
 * Originally written by Massimo.
 * Modified by Jonsku <jonathan.cremieux@aalto.fi>.
 */

/**
 * The Class AsyncXMLRequest.
 */
public class AsyncXMLRequest extends Thread
{
  
  /** The parent. */
  PApplet parent;
  
  /** The search term. */
  String searchTerm;
  
  /** The callback. */
  WPMessageListener callback;

  //public static final String SEARCH_URI = "http://search.twitter.com/search.atom?q=";
  /** The host url. */
  String hostURL;
  
  /**
   * Instantiates a new async xml request.
   *
   * @param _parent the _parent
   * @param _searchTerm the _search term
   * @param _hostURL the _host url
   * @param _callback the _callback
   */
  AsyncXMLRequest(PApplet _parent, String _searchTerm, String _hostURL, WPMessageListener _callback) {
    Verbose.debug("AsyncTwitter");
    parent = _parent;
    callback = _callback;
    searchTerm = _searchTerm;
    hostURL = _hostURL;
  }

  /* (non-Javadoc)
   * @see java.lang.Thread#run()
   */
  @Override
public void run() {
    //System.out.println("Hello from a thread!");
    loadMessages();
  }

  /**
   * Load messages.
   */
  void loadMessages() {
    XMLElement xml = null;
    String u = "";
	try {
		u = hostURL + "?processingwp=" + URLEncoder.encode(searchTerm,"UTF-8");
	} catch (UnsupportedEncodingException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}

    try {
      xml = new XMLElement(parent, u);
    } 
    catch (Exception e) {
      System.out.println("The URL "+u+" is not working!?\n\nError: " + e);
    }
    if (xml != null) {
      XMLElement[] entries = xml.getChildren("group");
      HashMap<String,String> messages = new HashMap<String,String>();
      
      for (int i=0; i<entries.length; i++) {
    	  for (int j=0; j<entries[i].getChildren("item").length; j++) {
    		  messages.put(entries[i].getChildren("item")[j].getString("name"), entries[i].getChildren("item")[j].getContent());  
    	  }
      }
      // call the callback
        callback.onResponse(messages);
    }


  }
}




