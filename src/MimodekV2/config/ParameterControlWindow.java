package MimodekV2.config;

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

import MimodekV2.Mimodek;
import MimodekV2.data.PollutionLevelsEnum;
import MimodekV2.data.TemperatureColorRanges;
import MimodekV2.graphics.OpenGL;
import MimodekV2.graphics.TextureManager;
import controlP5.Button;
import controlP5.Chart;
import controlP5.ControlEvent;
import controlP5.ControlListener;
import controlP5.ControlP5;
import controlP5.ControlWindow;
import controlP5.Label;
import controlP5.Numberbox;
import controlP5.Slider;
import controlP5.Tab;
import processing.core.PApplet;


public class ParameterControlWindow implements ControlListener{
	PApplet app;
	ControlP5 controlP5;
	ControlWindow controlWindow;
	static long lastGraphUpdate;
	
	private static final int RESET_BUTTON = -1;
	
	private static final int PAUSE_BUTTON = -2;
	private static final int AUTOFOOD_BUTTON = -3;
	private static final int GLOBAL_SCALING = -4;
	private static Numberbox creatureCountNbn;
	private static Numberbox softCellCountNbn;
	private static Numberbox hardCellCountNbn;
	private static Chart creatureEvolutionChart;
	

	
	private static final int OPENGL_TAB = 1;
	private static final int OPENGL_TEXTURE_FILTER = OPENGL_TAB*10+1;
	
	private static final int CELLA_TAB = 2;
	private static final int CELLA_MAX_TRY = CELLA_TAB*10+1;
	private static final int CELLA_DISTORTION = CELLA_TAB*10+2;
	private static final int CELLA_DISTANCE_BETWEEN = CELLA_TAB*10+3;
	private static final int CELLA_RADIUS = CELLA_TAB*10+4;
	private static final int CELLA_ALPHA = CELLA_TAB*10+5;
	private static final int CELLA_R =CELLA_TAB*10+6;
	private static final int CELLA_G =CELLA_TAB*10+7;
	private static final int CELLA_B =CELLA_TAB*10+8;
	private static final int CELLA_MASK = CELLA_TAB*10+9;
	private static final int CELLA_TEXTURE = CELLA_TAB*10+10;
	private static Label maskANameLbl;
	private static Label texANameLbl; 
	
	private static final int CELLB_TAB = 3;
	private static final int CELLB_RADIUS = CELLB_TAB*10+1;
	
	private static final int CELLB_MIN_DISTANCE_TO_A = CELLB_TAB*10+2;
	private static final int CELLB_MAX_DISTANCE_TO_A = CELLB_TAB*10+3;
	private static final int CELLB_ALPHA = CELLB_TAB*10+4;
	private static final int CELLB_R =CELLB_TAB*10+5;
	private static final int CELLB_G =CELLB_TAB*10+6;
	private static final int CELLB_B =CELLB_TAB*10+7;
	private static final int CELLB_ALPHA_VARIATION = CELLB_TAB*10+8;
	private static final int CELLB_TEXTURE = CELLB_TAB*10+9;
	private static Label texBNameLbl;
	
	private static final int CREATURE_TAB = 4;
	private static final int CREATURE_SIZE = CREATURE_TAB*10+1;
	private static final int CREATURE_R =CREATURE_TAB*10+2;
	private static final int CREATURE_G =CREATURE_TAB*10+3;
	private static final int CREATURE_B =CREATURE_TAB*10+4;
	
	private static final int CREATURE_FULL_R =CREATURE_TAB*10+5;
	private static final int CREATURE_FULL_G =CREATURE_TAB*10+6;
	private static final int CREATURE_FULL_B =CREATURE_TAB*10+7;
	private static final int CREATURE_DISTANCE_BETWEEN = CREATURE_TAB*10+8;
	private static final int CREATURE_MAXSPEED = CREATURE_TAB*10+9;
	private static final int CREATURE_STEER_FORCE = CREATURE_TAB*10;
	private static final int CREATURE_ALPHA_VARIATION = CREATURE_TAB*100+1;
	private static final int CREATURE_ALPHA= CREATURE_TAB*100+2;
	
	private static final int FOOD_TAB = 5;
	private static final int FOOD_SIZE = FOOD_TAB*10+1;
	private static final int FOOD_R = FOOD_TAB*10+2;
	private static final int FOOD_G = FOOD_TAB*10+3;
	private static final int FOOD_B = FOOD_TAB*10+4;
	private static final int FOOD_SCENT_EVAPORATION = FOOD_TAB*10+5;
	
	private static final int DATA_TAB = 6;
	private static final int DATA_REFRESH_RATE = DATA_TAB*10+1;
	private static final int DATA_TEMPERATURE = DATA_TAB*10+2;
	private static final int DATA_HUMIDITY = DATA_TAB*10+3;
	private static final int DATA_POLLUTION = DATA_TAB*10+4;
	
	private  int bottomMargin = 10;
	private  int controllerHeight = 14;
	
	public ParameterControlWindow(PApplet app){
		lastGraphUpdate = System.currentTimeMillis();
		this.app = app;
		Slider slider;
		
		controlP5 = new ControlP5(app);
		controlP5.addListener(this);
		//creates a window for the controls
		controlP5.setAutoDraw(false);
		controlWindow = controlP5.addControlWindow("controlP5window",1000,100,400,300);
		controlWindow.hideCoordinates();
		controlWindow.setBackground(app.color(40));
		controlWindow.setTitle("Controls");
		
		int yOffset = bottomMargin*2;
		Button btn = controlP5.addButton("Reset",0,10,yOffset,100,controllerHeight);
		btn.setId(RESET_BUTTON);
		btn.setTab(controlWindow,"default");
		//btn.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		btn = controlP5.addButton("Pause/Run",0,10,yOffset,100,controllerHeight);
		btn.captionLabel().set(Mimodek.update?"||":">");
		btn.setId(PAUSE_BUTTON);
		btn.setTab(controlWindow,"default");
		//btn.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		btn = controlP5.addButton("Autofood",0,10,yOffset,100,controllerHeight);
		btn.setId(AUTOFOOD_BUTTON);
		btn.setTab(controlWindow,"default");
		//btn.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		creatureCountNbn = controlP5.addNumberbox("Creature count",0,10,yOffset,100,controllerHeight);
		creatureCountNbn.setId(-10);
		creatureCountNbn.lock();
		creatureCountNbn.setTab(controlWindow,"default");
		creatureCountNbn.captionLabel().style().marginLeft = 15+100;
		creatureCountNbn.captionLabel().style().marginTop -= controllerHeight;
		yOffset += controllerHeight+bottomMargin;
		
		softCellCountNbn = controlP5.addNumberbox("Soft cell count",0,10,yOffset,100,controllerHeight);
		softCellCountNbn.setId(-11);
		softCellCountNbn.lock();
		softCellCountNbn.setTab(controlWindow,"default");
		softCellCountNbn.captionLabel().style().marginLeft = 15+100;
		softCellCountNbn.captionLabel().style().marginTop -= controllerHeight;
		yOffset += controllerHeight+bottomMargin;
		
		hardCellCountNbn = controlP5.addNumberbox("Hard cell count:",0,10,yOffset,100,controllerHeight);
		hardCellCountNbn.setId(-12);
		hardCellCountNbn.lock();
		hardCellCountNbn.setTab(controlWindow,"default");
		hardCellCountNbn.captionLabel().style().marginLeft = 15+100;
		hardCellCountNbn.captionLabel().style().marginTop -= controllerHeight;
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Scaling",0,10f,Configurator.getFloatSetting("GLOBAL_SCALING"),10,yOffset,100,controllerHeight);
		slider.setDecimalPrecision(1);
		slider.setId(GLOBAL_SCALING);
		slider.setTab(controlWindow,"default");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		creatureEvolutionChart = controlP5.addChart("Creature Evolution",10,yOffset,400,100);
		creatureEvolutionChart.setStrokeWeight(1);
		creatureEvolutionChart.setTab(controlWindow,"default");
		yOffset += controllerHeight+bottomMargin;
		
		/* OPENGL TAB */
		Tab t = controlWindow.addTab("OpenGl");
		t.activateEvent(true);
		t.setId(OPENGL_TAB);
		yOffset = bottomMargin*2;
		
		slider = controlP5.addSlider("Texture Filter",0,OpenGL.TextureFilters.values().length-1,Configurator.getIntegerSetting("GL_TEXTURE_MIN_FILTER"),10,yOffset,100,controllerHeight);
		slider.setDecimalPrecision(0);
		slider.setId(OPENGL_TEXTURE_FILTER);
		slider.setTab(controlWindow,"OpenGl");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		
		/* HARD CELL TAB */
		t = controlWindow.addTab("Hard Cells");
		t.activateEvent(true);
		t.setId(CELLA_TAB);
		yOffset = bottomMargin*2;
		
		slider = controlP5.addSlider("Max try",0,150,Configurator.getIntegerSetting("CELLA_MAX_TRY"),10,yOffset,100,controllerHeight);
		slider.setDecimalPrecision(0);
		slider.setId(CELLA_MAX_TRY);
		slider.setTab(controlWindow,"Hard Cells");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Distortion",0,1f,Configurator.getFloatSetting("CELLA_DISTORTION"),10,yOffset,100,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(CELLA_DISTORTION);
		slider.setTab(controlWindow,"Hard Cells");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Distance between",0,1f,Configurator.getFloatSetting("CELLA_DISTANCE_BETWEEN"),10,yOffset,100,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(CELLA_DISTANCE_BETWEEN);
		slider.setTab(controlWindow,"Hard Cells");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("A Radius",0,50f,Configurator.getFloatSetting("CELLA_RADIUS"),10,yOffset,100,controllerHeight);
		slider.setDecimalPrecision(1);
		slider.setId(CELLA_RADIUS);
		slider.setTab(controlWindow,"Hard Cells");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("A Transparency",0,1f,Configurator.getFloatSetting("CELLA_ALPHA"),10,yOffset,100,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(CELLA_ALPHA);
		slider.setTab(controlWindow,"Hard Cells");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("A Red",0,1f,Configurator.getFloatSetting("CELLA_R"),10,yOffset,100,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(CELLA_R);
		slider.setTab(controlWindow,"Hard Cells");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("A Green",0,1f,Configurator.getFloatSetting("CELLA_G"),10,yOffset,100,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(CELLA_G);
		slider.setTab(controlWindow,"Hard Cells");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("A Blue",0,1f,Configurator.getFloatSetting("CELLA_B"),10,yOffset,100,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(CELLA_B);
		slider.setTab(controlWindow,"Hard Cells");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		/*
		slider = controlP5.addSlider("Mask:",TextureManager.min,TextureManager.max,Configurator.getIntegerSetting("CELLA_MASK"),10,yOffset,100,controllerHeight);
		slider.setDecimalPrecision(0);
		slider.setId(CELLA_MASK);
		slider.setTab(controlWindow,"Hard Cells");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		maskANameLbl = slider.captionLabel();
		
		slider = controlP5.addSlider("A Texture:",TextureManager.min,TextureManager.max,Configurator.getIntegerSetting("CELLA_TEXTURE"),10,yOffset,100,controllerHeight);
		slider.setDecimalPrecision(0);
		slider.setId(CELLA_TEXTURE);
		slider.setTab(controlWindow,"Hard Cells");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		texANameLbl = slider.captionLabel();
		*/


		
		
		/* Soft Cells TAB */
		t = controlWindow.addTab("Soft Cells");
		t.activateEvent(true);
		t.setId(CELLB_TAB);
		yOffset = bottomMargin*2;
		
		slider = controlP5.addSlider("B Radius",0,50f,Configurator.getFloatSetting("CELLB_RADIUS"),10,yOffset,100,controllerHeight);
		slider.setDecimalPrecision(1);
		slider.setId(CELLB_RADIUS);
		slider.setTab(controlWindow,"Soft Cells");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Min distance to structure",0,50f,Configurator.getFloatSetting("CELLB_MIN_DISTANCE_TO_A"),10,yOffset,100,controllerHeight);
		slider.setDecimalPrecision(1);
		slider.setId(CELLB_MIN_DISTANCE_TO_A);
		slider.setTab(controlWindow,"Soft Cells");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Max distance to structure",0,50f,Configurator.getFloatSetting("CELLB_MAX_DISTANCE_TO_A"),10,yOffset,100,controllerHeight);
		slider.setDecimalPrecision(1);
		slider.setId(CELLB_MAX_DISTANCE_TO_A);
		slider.setTab(controlWindow,"Soft Cells");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;

		
		slider = controlP5.addSlider("B Transparency",0,1f,Configurator.getFloatSetting("CELLB_ALPHA"),10,yOffset,100,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(CELLB_ALPHA);
		slider.setTab(controlWindow,"Soft Cells");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("B Red",0,1f,Configurator.getFloatSetting("CELLB_R"),10,yOffset,100,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(CELLB_R);
		slider.setTab(controlWindow,"Soft Cells");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("B Green",0,1f,Configurator.getFloatSetting("CELLB_G"),10,yOffset,100,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(CELLB_G);
		slider.setTab(controlWindow,"Soft Cells");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("B Blue",0,1f,Configurator.getFloatSetting("CELLB_B"),10,yOffset,100,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(CELLB_B);
		slider.setTab(controlWindow,"Soft Cells");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Transparency variation",0,1f,Configurator.getFloatSetting("CELLB_ALPHA_VARIATION"),10,yOffset,100,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(CELLB_ALPHA_VARIATION);
		slider.setTab(controlWindow,"Soft Cells");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		/*
		slider = controlP5.addSlider("B Texture:",TextureManager.min,TextureManager.max,Configurator.getIntegerSetting("CELLB_TEXTURE"),10,yOffset,100,controllerHeight);
		slider.setDecimalPrecision(0);
		slider.setId(CELLB_TEXTURE);
		slider.setTab(controlWindow,"Soft Cells");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		texBNameLbl = slider.captionLabel();
		*/
		
		
		/* CREATURE TAB */
		t = controlWindow.addTab("Creatures");
		t.activateEvent(true);
		t.setId(CREATURE_TAB);
		yOffset = bottomMargin*2;
		
		slider = controlP5.addSlider("Size",OpenGL.poinSizetInfo.x,OpenGL.poinSizetInfo.y,Configurator.getFloatSetting("CREATURE_RADIUS"),10,yOffset,100,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(CREATURE_SIZE);
		slider.setTab(controlWindow,"Creatures");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Creature Red",0,1f,Configurator.getFloatSetting("CREATURE_R"),10,yOffset,100,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(CREATURE_R);
		slider.setTab(controlWindow,"Creatures");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Creature Green",0,1f,Configurator.getFloatSetting("CREATURE_G"),10,yOffset,100,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(CREATURE_G);
		slider.setTab(controlWindow,"Creatures");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Creature Blue",0,1f,Configurator.getFloatSetting("CREATURE_B"),10,yOffset,100,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(CREATURE_B);
		slider.setTab(controlWindow,"Creatures");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Creature Transparency",0,1f,Configurator.getFloatSetting("CREATURE_ALPHA"),10,yOffset,100,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(CREATURE_ALPHA);
		slider.setTab(controlWindow,"Creatures");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Creature Full Red",0,1f,Configurator.getFloatSetting("CREATURE_FULL_R"),10,yOffset,100,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(CREATURE_FULL_R);
		slider.setTab(controlWindow,"Creatures");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Creature Full Green",0,1f,Configurator.getFloatSetting("CREATURE_FULL_G"),10,yOffset,100,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(CREATURE_FULL_G);
		slider.setTab(controlWindow,"Creatures");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Creature Full Blue",0,1f,Configurator.getFloatSetting("CREATURE_FULL_B"),10,yOffset,100,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(CREATURE_FULL_B);
		slider.setTab(controlWindow,"Creatures");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Distance betwwen Creatures",0f,100f,Configurator.getFloatSetting("CREATURE_DISTANCE_BETWEEN"),10,yOffset,100,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(CREATURE_DISTANCE_BETWEEN);
		slider.setTab(controlWindow,"Creatures");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Max. Speed",0f,5f,Configurator.getFloatSetting("CREATURE_MAXSPEED"),10,yOffset,100,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(CREATURE_MAXSPEED);
		slider.setTab(controlWindow,"Creatures");
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Max. Steer Force",0f,1f,Configurator.getFloatSetting("CREATURE_STEER_FORCE"),10,yOffset,100,controllerHeight);
		slider.setDecimalPrecision(3);
		slider.setId(CREATURE_STEER_FORCE);
		slider.setTab(controlWindow,"Creatures");
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Brightness variation",0,1f,Configurator.getFloatSetting("CREATURE_ALPHA_VARIATION"),10,yOffset,100,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(CREATURE_ALPHA_VARIATION);
		slider.setTab(controlWindow,"Creatures");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		
		
		/* FOOD TAB */
		t = controlWindow.addTab("Food");
		t.activateEvent(true);
		t.setId(FOOD_TAB);
		yOffset = bottomMargin*2;
		
		slider = controlP5.addSlider("Food Size",0f,5f,Configurator.getFloatSetting("FOOD_SIZE"),10,yOffset,100,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(FOOD_SIZE);
		slider.setTab(controlWindow,"Food");
		////slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		
		slider = controlP5.addSlider("Food Red",0,1f,Configurator.getFloatSetting("FOOD_R"),10,yOffset,100,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(FOOD_R);
		slider.setTab(controlWindow,"Food");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Food Green",0,1f,Configurator.getFloatSetting("FOOD_G"),10,yOffset,100,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(FOOD_G);
		slider.setTab(controlWindow,"Food");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Food Blue",0,1f,Configurator.getFloatSetting("FOOD_B"),10,yOffset,100,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(FOOD_B);
		slider.setTab(controlWindow,"Food");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Smell Evaporation",0,1f,Configurator.getFloatSetting("FOOD_SCENT_EVAPORATION"),10,yOffset,100,controllerHeight);
		slider.setDecimalPrecision(3);
		slider.setId(FOOD_SCENT_EVAPORATION);
		slider.setTab(controlWindow,"Food");
		yOffset += controllerHeight+bottomMargin;
		
		
		/* DATA TAB */
		t = controlWindow.addTab("Data");
		t.activateEvent(true);
		t.setId(DATA_TAB);
		yOffset = bottomMargin*2;
		
		slider = controlP5.addSlider("Refresh time (in minutes)",0.1f,10f,Configurator.getFloatSetting("DATA_REFRESH_RATE"),10,yOffset,100,controllerHeight);
		slider.setDecimalPrecision(1);
		slider.setId(DATA_REFRESH_RATE);
		slider.setTab(controlWindow,"Data");
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Temperature",TemperatureColorRanges.minT,TemperatureColorRanges.maxT,Configurator.getFloatSetting("DATA_TEMPERATURE"),10,yOffset,100,controllerHeight);
		slider.setDecimalPrecision(1);
		slider.setId(DATA_TEMPERATURE);
		slider.setTab(controlWindow,"Data");
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Humidity",0f,100f,Configurator.getFloatSetting("DATA_HUMIDITY"),10,yOffset,100,controllerHeight);
		slider.setDecimalPrecision(1);
		slider.setId(DATA_HUMIDITY);
		slider.setTab(controlWindow,"Data");
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Pollution",0f,21f,Configurator.getFloatSetting("DATA_POLLUTION"),10,yOffset,100,controllerHeight);
		slider.setDecimalPrecision(0);
		slider.setId(DATA_POLLUTION);
		slider.setTab(controlWindow,"Data");
		yOffset += controllerHeight+bottomMargin;
		
	}

	public void controlEvent(ControlEvent ctrlEvent) {
		//System.out.println("Control Event");
		if(ctrlEvent.isController()){
			switch(ctrlEvent.controller().id()){
			case RESET_BUTTON:
				Mimodek.reset = true;
				break;
			case PAUSE_BUTTON:
				Mimodek.update = !Mimodek.update;
				ctrlEvent.controller().captionLabel().set(Mimodek.update?"||":">");
				break;
			case AUTOFOOD_BUTTON:
				Mimodek.autoFood = !Mimodek.autoFood;
				break;
			case GLOBAL_SCALING:
				Configurator.setSetting("GLOBAL_SCALING", ctrlEvent.controller().value());
				break;
				
			case OPENGL_TEXTURE_FILTER:
				Configurator.setSetting("GL_TEXTURE_MIN_FILTER", (int)ctrlEvent.controller().value());
				break;
				
			/*CELL A*/
			case CELLA_MAX_TRY:
				Configurator.setSetting("CELLA_MAX_TRY", (int)ctrlEvent.controller().value());
				break;
			case CELLA_DISTORTION:
				Configurator.setSetting("CELLA_DISTORTION", ctrlEvent.controller().value());
				break;	
			case CELLA_DISTANCE_BETWEEN:
				Configurator.setSetting("CELLA_DISTANCE_BETWEEN",ctrlEvent.controller().value());
				break;
			case CELLA_RADIUS:
				Configurator.setSetting("CELLA_RADIUS",ctrlEvent.controller().value());
				break;		
			case CELLA_ALPHA:
				Configurator.setSetting("CELLA_ALPHA",ctrlEvent.controller().value());
				break;	
			case CELLA_R:
				Configurator.setSetting("CELLA_R",ctrlEvent.controller().value());
				break;	
			case CELLA_G:
				Configurator.setSetting("CELLA_G",ctrlEvent.controller().value());
				break;	
			case CELLA_B:
				Configurator.setSetting("CELLA_B",ctrlEvent.controller().value());
				break;	
			case CELLA_MASK:
				Configurator.setSetting("CELLA_MASK",(int)ctrlEvent.controller().value());
				maskANameLbl.set("Mask:"+TextureManager.getNameOf((int)ctrlEvent.controller().value()));
				break;
			case CELLA_TEXTURE:
				Configurator.setSetting("CELLA_TEXTURE",(int)ctrlEvent.controller().value());
				texANameLbl.set("Texture:"+TextureManager.getNameOf((int)ctrlEvent.controller().value()));
				break;	
				
				/*CELL B*/
			case CELLB_RADIUS:
				Configurator.setSetting("CELLB_RADIUS",ctrlEvent.controller().value());
				break;	
			case CELLB_MIN_DISTANCE_TO_A:
				Configurator.setSetting("CELLB_MIN_DISTANCE_TO_A",ctrlEvent.controller().value());
				break;
			case CELLB_MAX_DISTANCE_TO_A:
				Configurator.setSetting("CELLB_MAX_DISTANCE_TO_A",ctrlEvent.controller().value());
				break;
			case CELLB_ALPHA:
				Configurator.setSetting("CELLB_ALPHA",ctrlEvent.controller().value());
				break;	
			case CELLB_R:
				Configurator.setSetting("CELLB_R",ctrlEvent.controller().value());
				break;	
			case CELLB_G:
				Configurator.setSetting("CELLB_G",ctrlEvent.controller().value());
				break;	
			case CELLB_B:
				Configurator.setSetting("CELLB_B",ctrlEvent.controller().value());
				break;	
			case CELLB_ALPHA_VARIATION:
				Configurator.setSetting("CELLB_ALPHA_VARIATION",ctrlEvent.controller().value());
				break;	
			case CELLB_TEXTURE:
				Configurator.setSetting("CELLB_TEXTURE",(int)ctrlEvent.controller().value());
				texBNameLbl.set("Texture:"+TextureManager.getNameOf((int)ctrlEvent.controller().value()));
				break;
				
			/*CREATURE*/
			case CREATURE_SIZE:
				Configurator.setSetting("CREATURE_SIZE",ctrlEvent.controller().value());
				break;
			case CREATURE_R:
				Configurator.setSetting("CREATURE_R",ctrlEvent.controller().value());
				break;	
			case CREATURE_G:
				Configurator.setSetting("CREATURE_G",ctrlEvent.controller().value());
				break;	
			case CREATURE_B:
				Configurator.setSetting("CREATURE_B",ctrlEvent.controller().value());
				break;	
			case CREATURE_FULL_R:
				Configurator.setSetting("CREATURE_FULL_R",ctrlEvent.controller().value());
				break;	
			case CREATURE_FULL_G:
				Configurator.setSetting("CREATURE_FULL_G",ctrlEvent.controller().value());
				break;	
			case CREATURE_FULL_B:
				Configurator.setSetting("CREATURE_FULL_B",ctrlEvent.controller().value());
				break;	
			case CREATURE_DISTANCE_BETWEEN:
				Configurator.setSetting("CREATURE_DISTANCE_BETWEEN",ctrlEvent.controller().value());
				break;
			case CREATURE_MAXSPEED:
				Configurator.setSetting("CREATURE_MAXSPEED",ctrlEvent.controller().value());
				break;
			case CREATURE_STEER_FORCE:
				Configurator.setSetting("CREATURE_STEER_FORCE",ctrlEvent.controller().value());
				break;
			case CREATURE_ALPHA_VARIATION:
				Configurator.setSetting("CREATURE_ALPHA_VARIATION",ctrlEvent.controller().value());
				break;
			case CREATURE_ALPHA:
				Configurator.setSetting("CREATURE_ALPHA",ctrlEvent.controller().value());
				break;
				
			/* FOOD */
			case FOOD_SIZE:
				Configurator.setSetting("FOOD_SIZE",ctrlEvent.controller().value());
				break;
			case FOOD_R:
				Configurator.setSetting("FOOD_R",ctrlEvent.controller().value());
				break;	
			case FOOD_G:
				Configurator.setSetting("FOOD_G",ctrlEvent.controller().value());
				break;	
			case FOOD_B:
				Configurator.setSetting("FOOD_B",ctrlEvent.controller().value());
				break;	
			case FOOD_SCENT_EVAPORATION:
				Configurator.setSetting("FOOD_SCENT_EVAPORATION",ctrlEvent.controller().value());
				break;	
				
			/* DATA */
			case DATA_REFRESH_RATE:
				Configurator.setSetting("DATA_REFRESH_RATE",ctrlEvent.controller().value());
				break;
			case DATA_TEMPERATURE:
				Configurator.setSetting("DATA_TEMPERATURE",ctrlEvent.controller().value());
				break;
			case DATA_HUMIDITY:
				Configurator.setSetting("DATA_HUMIDITY",ctrlEvent.controller().value());
				break;
			case DATA_POLLUTION:
				Configurator.setSetting("DATA_POLLUTION",ctrlEvent.controller().value());
				ctrlEvent.controller().captionLabel().set("Pollution: "+PollutionLevelsEnum.getPollutionLevelForScore(ctrlEvent.controller().value()).name());
				break;
			}
			
		}

		
	}
	
	public static void setCounters(int c, int h, int s){
		creatureCountNbn.setValue(c);
		if(System.currentTimeMillis()-lastGraphUpdate>(60*1000)/15f){
			lastGraphUpdate = System.currentTimeMillis();
			creatureEvolutionChart.push(c);
		}
		softCellCountNbn.setValue(s);
		hardCellCountNbn.setValue(h);
	}
	
	
	
	/*public void draw(){
		controlP5.draw();
	}*/
}
