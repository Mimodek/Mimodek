package mimodek.controls;

import java.util.ArrayList;

import mimodek.configuration.Configurator;

import controlP5.CVector3f;
import controlP5.ControlEvent;
import controlP5.ControlListener;
import controlP5.Controller;

public abstract class GUIModule implements ControlListener{
	String name;

	ArrayList<Controller> controllers;
	//public boolean open = true;
	
	public GUIModule(int x, int y, int width, int height, String name){
		this.name = name;
		Configurator.setSettingIfNotSet(name+"_GUI_x", x);
		Configurator.setSettingIfNotSet(name+"_GUI_y", y);
		Configurator.setSettingIfNotSet(name+"_GUI_width", width);
		Configurator.setSettingIfNotSet(name+"_GUI_height", height);
		Configurator.setSettingIfNotSet(name+"_GUI_open", true);
	}
	
	public void reset(){
		//remove the controllers
		if(controllers != null && controllers.size()>0){
			for(int i=0;i<controllers.size();i++){
				GUI.gui().controlP5.remove(controllers.get(i).name());
			}
		}
		controllers = new ArrayList<Controller>(); 
	}
	
	public abstract void create();
	
	public void addController(Controller c){
		controllers.add(c);
	}
	
	public int getX(){
		return Configurator.getIntegerSetting(name+"_GUI_x");
	}
	
	public int getY(){
		return Configurator.getIntegerSetting(name+"_GUI_y");
	}
	
	public void setX(int x){
		Configurator.setSetting(name+"_GUI_x", x);
	}
	
	public void setY(int y){
		Configurator.setSetting(name+"_GUI_y", y);
	}
	
	public int getWidth(){
		return Configurator.getIntegerSetting(name+"_GUI_width");
	}
	
	public int getHeight(){
		return Configurator.getIntegerSetting(name+"_GUI_height");
	}
	
	public void draw(){
		GUI.gui().app.pushMatrix();
		GUI.gui().app.translate(getX(),getY());
		if(Configurator.getBooleanSetting(name+"_GUI_open")){
			GUI.setBoxStyle();
			GUI.gui().app.rect(0,0,getWidth(),getHeight());
		}
		GUI.setHandleStyle();
		GUI.gui().app.rect(0,0,getWidth(),10);
		GUI.setTextStyle();
		GUI.gui().app.text(name,5,10);
		GUI.setHandleStyle();
		GUI.gui().app.rect(getWidth()-10,0,10,10);
		GUI.gui().app.line(getWidth()-10,5,getWidth(),5);
		if(!Configurator.getBooleanSetting(name+"_GUI_open")){
			GUI.gui().app.line(getWidth()-5,0,getWidth()-5,10);
		}
		GUI.gui().app.popMatrix();
	}
	
	public boolean click(int mX, int mY){
		if(mX>=getX()+getWidth()-10 && mX<=getX()+getWidth() && mY>=getY() && mY<=getY()+10){
			Configurator.setSetting(name+"_GUI_open",!Configurator.getBooleanSetting(name+"_GUI_open"));
			toggleControllers(Configurator.getBooleanSetting(name+"_GUI_open"));
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
	
	public abstract void controlEvent(ControlEvent cEvent);
}
