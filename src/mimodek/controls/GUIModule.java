package mimodek.controls;

import java.util.ArrayList;

import mimodek.MainHandler;

import controlP5.CVector3f;
import controlP5.Controller;

public class GUIModule {
	String name;
	int x,y,width,height;
	ArrayList<Controller> controllers;
	public boolean open = true;
	
	public GUIModule(int x, int y, int width, int height, String name){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.name = name;
		controllers = new ArrayList<Controller>(); 
	}
	
	public void addController(Controller c){
		controllers.add(c);
	}
	
	public void draw(){
		MainHandler.gfx.pushMatrix();
		MainHandler.gfx.translate(x,y);
		if(open){
			GUI.setBoxStyle();
			MainHandler.gfx.rect(0,0,width,height);
		}
		GUI.setHandleStyle();
		MainHandler.gfx.rect(0,0,width,10);
		GUI.setTextStyle();
		MainHandler.gfx.text(name,5,10);
		GUI.setHandleStyle();
		MainHandler.gfx.rect(width-10,0,10,10);
		MainHandler.gfx.line(width-10,5,width,5);
		if(!open){
			MainHandler.gfx.line(width-5,0,width-5,10);
		}
		MainHandler.gfx.popMatrix();
	}
	
	public boolean click(int mX, int mY){
		if(mX>=x+width-10 && mX<=x+width && mY>=y && mY<=y+10){
			open = !open;
			toggleControllers(open);
			return true;
		}
		return false;
	}
	
	public void toggleControllers(boolean on){
		for(int i=-1;++i<controllers.size();){
			if(on)
				controllers.get(i).show();
			else
				controllers.get(i).hide();
		}
	}
	
	public void moveControllers(int offsetX, int offsetY){
		for(int i=-1;++i<controllers.size();){
				CVector3f pos = controllers.get(i).position();
				pos.x += offsetX;
				pos.y += offsetY;
				controllers.get(i).setPosition(pos.x,pos.y);
		}
	}
	
	public boolean drag(int mX, int mY, int offsetX, int offsetY){
		if(mX>=x && mX<=x+width && mY>=y && mY<=y+10){
			x+=offsetX;
			y+=offsetY;
			moveControllers(offsetX, offsetY);
			return true;
		}
		return false;
	}
	
	public void offset(int offsetX, int offsetY){
			x+=offsetX;
			y+=offsetY;
			moveControllers(offsetX, offsetY);
	}
}
