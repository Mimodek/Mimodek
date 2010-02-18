package mimodek.controls;

import java.util.ArrayList;

import mimodek.MainHandler;

public class GUI {
	
	ArrayList<GUIModule> modules;
	GUIModule dragged;
	
	public GUI(){
		modules = new ArrayList<GUIModule>();
	}
	
	public void addModule(GUIModule mod){
		modules.add(mod);
	}

	public static void setBoxStyle() {
		MainHandler.gfx.fill(0, 255, 0, 50);
		MainHandler.gfx.noStroke();
	}

	public static void setHandleStyle() {
		MainHandler.gfx.fill(0, 0, 255);
		MainHandler.gfx.stroke(255);
	}
	
	public static void setTextStyle() {
		MainHandler.gfx.fill(255);
	}
	
	public void draw(){
		MainHandler.gfx.pushStyle();
		for(int i=-1;++i<modules.size();)
			modules.get(i).draw();
		MainHandler.gfx.popStyle();
	}
	
	public void clicked(int mX, int mY){
		int i =0;
		while(i<modules.size() && !modules.get(i).click(mX, mY))
			i++;
	}
	
	public void mouseReleased(){
		dragged = null;
	}	
	
	public void drag(int mX, int mY, int offsetX, int offsetY){
		int i =0;
		if(dragged == null){
			while(i<modules.size() && !modules.get(i).drag(mX, mY, offsetX, offsetY))
				i++;
			if(i<modules.size())
				dragged = modules.get(i);
		}else{
			dragged.offset(offsetX, offsetY);
		}
	}	
}
