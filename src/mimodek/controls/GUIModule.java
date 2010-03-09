package mimodek.controls;

import java.util.ArrayList;

import mimodek.Mimodek;

import controlP5.CVector3f;
import controlP5.Controller;

public abstract class GUIModule {
	String name;

	ArrayList<Controller> controllers;
	//public boolean open = true;
	
	public GUIModule(int x, int y, int width, int height, String name){
		this.name = name;
		Mimodek.config.setSetting(name+"_GUI_x", x);
		Mimodek.config.setSetting(name+"_GUI_y", y);
		Mimodek.config.setSetting(name+"_GUI_width", width);
		Mimodek.config.setSetting(name+"_GUI_height", height);
		Mimodek.config.setSetting(name+"_GUI_open", true);
	}
	
	public void reset(){
		//remove the controllers
		if(controllers != null && controllers.size()>0){
			for(int i=0;i<controllers.size();i++){
				Mimodek.controlP5.remove(controllers.get(i).name());
			}
		}
		controllers = new ArrayList<Controller>(); 
	}
	
	public abstract void create();
	
	public void addController(Controller c){
		controllers.add(c);
	}
	
	public int getX(){
		return Mimodek.config.getIntegerSetting(name+"_GUI_x");
	}
	
	public int getY(){
		return Mimodek.config.getIntegerSetting(name+"_GUI_y");
	}
	
	public void setX(int x){
		Mimodek.config.setSetting(name+"_GUI_x", x);
	}
	
	public void setY(int y){
		Mimodek.config.setSetting(name+"_GUI_y", y);
	}
	
	public int getWidth(){
		return Mimodek.config.getIntegerSetting(name+"_GUI_width");
	}
	
	public int getHeight(){
		return Mimodek.config.getIntegerSetting(name+"_GUI_height");
	}
	
	public void draw(){
		Mimodek.gfx.pushMatrix();
		Mimodek.gfx.translate(getX(),getY());
		if(Mimodek.config.getBooleanSetting(name+"_GUI_open")){
			GUI.setBoxStyle();
			Mimodek.gfx.rect(0,0,getWidth(),getHeight());
		}
		GUI.setHandleStyle();
		Mimodek.gfx.rect(0,0,getWidth(),10);
		GUI.setTextStyle();
		Mimodek.gfx.text(name,5,10);
		GUI.setHandleStyle();
		Mimodek.gfx.rect(getWidth()-10,0,10,10);
		Mimodek.gfx.line(getWidth()-10,5,getWidth(),5);
		if(!Mimodek.config.getBooleanSetting(name+"_GUI_open")){
			Mimodek.gfx.line(getWidth()-5,0,getWidth()-5,10);
		}
		Mimodek.gfx.popMatrix();
	}
	
	public boolean click(int mX, int mY){
		if(mX>=getX()+getWidth()-10 && mX<=getX()+getWidth() && mY>=getY() && mY<=getY()+10){
			Mimodek.config.setSetting(name+"_GUI_open",!Mimodek.config.getBooleanSetting(name+"_GUI_open"));
			toggleControllers(Mimodek.config.getBooleanSetting(name+"_GUI_open"));
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
		if(mX>=getX() && mX<=getX()+getWidth() && mY>=getY() && mY<=getY()+10){
			
			offset(offsetX, offsetY);
			return true;
		}
		return false;
	}
	
	public void offset(int offsetX, int offsetY){
		setX(getX()+offsetX);
		setY(getY()+offsetY);
		moveControllers(offsetX, offsetY);
	}
}
