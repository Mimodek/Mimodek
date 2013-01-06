package MimodekV2.config;

import java.awt.Container;
import java.awt.Frame;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import processing.core.PApplet;

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
 * The Class AWTUtils.
 */
public class AWTUtils {
	
	/**
	 * Find parent frame.
	 *
	 * @param app the app
	 * @return the frame
	 */
	public static Frame findParentFrame(PApplet app){ 
	    Container c = app; 
	    while(c != null){ 
	      if (c instanceof Frame) 
	        return (Frame)c; 

	      c = c.getParent(); 
	    } 
	    return null; 
	  } 
	
	/**
	 * String dialog.
	 *
	 * @param question the question
	 * @param frame the frame
	 * @return the string
	 */
	public static String stringDialog(String question, Frame frame){
		String s = JOptionPane.showInputDialog(
		                    frame,
		                    question,
		                    JOptionPane.QUESTION_MESSAGE);

		//If a string was returned, say so.
		if ((s != null) && (s.length() > 0)) {
		    return s;
		}else{
			return "";
		}
	}
	
	/**
	 * Password dialog.
	 *
	 * @param question the question
	 * @param frame the frame
	 * @return the string
	 */
	public static String passwordDialog(String question, Frame frame){
		JPasswordField pwd = new JPasswordField(10);  
	    int action = JOptionPane.showConfirmDialog(null, pwd,question,JOptionPane.OK_CANCEL_OPTION);  
	    if(action < 0)
	    	return null;
	    else
	    	return new String(pwd.getPassword());  
	}
}
