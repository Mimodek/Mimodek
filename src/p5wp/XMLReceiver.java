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
import java.util.HashMap;

// TODO: Auto-generated Javadoc
/*
 * Originally written by Massimo.
 * Modified by Jonsku <jonathan.cremieux@aalto.fi>.
 */

/**
 * The Class XMLReceiver.
 */
public class XMLReceiver implements WPMessageListener{
  
  /** The listener. */
  WPMessageListener listener;
  
  /** The parent. */
  PApplet parent;
  
  /** The host url. */
  String hostURL;
  
  /**
   * Instantiates a new xML receiver.
   *
   * @param _parent the _parent
   * @param _hostURL the _host url
   */
  public XMLReceiver(PApplet _parent, String _hostURL) {
	    parent = _parent;
	    hostURL = _hostURL;
	  }
  
  /**
   * Sets the listener.
   *
   * @param listener the new listener
   */
  public void setListener(WPMessageListener listener){
	  this.listener = listener;
  }

  /**
   * Gets the fresh data.
   *
   * @param term the term
   * @return the fresh data
   */
  public void getFreshData(String term) {

    Thread t = new Thread(new AsyncXMLRequest(parent, term, hostURL, this));
    t.start();
  }

  /* (non-Javadoc)
   * @see p5wp.WPMessageListener#onResponse(java.util.HashMap)
   */
  public void onResponse(HashMap<String,String> messages) {
	if(listener != null){
		listener.onResponse(messages);
	}
  }
}
