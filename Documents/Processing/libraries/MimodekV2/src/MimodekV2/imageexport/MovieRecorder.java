package MimodekV2.imageexport;
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
public class MovieRecorder {
	boolean recording = false;
	boolean paused = false;
	
	long frameNumber = 0;
	
	public MovieRecorder(){
		
	}
	
	public void reset(){
		 recording = false;
	     frameNumber = 0;
	}
	
	public boolean isRecording() {
		return recording;
	}
	
	public boolean isPaused() {
		return recording && paused;
	}
	
	public void pause() {
		paused = true;
	}
	
	public void resume() {
		if(paused)
			paused = false;
		else 
			recording = true;
	}



	public void toggleRecording() {
		recording = !recording;
	}



	public long nextFrame(){
		return frameNumber++;
	}
}
