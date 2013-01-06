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

import mimodek.facade.FacadeFactory;
import MimodekV2.Mimodek;
import MimodekV2.data.PollutionLevelsEnum;
import MimodekV2.data.TemperatureColorRanges;
import MimodekV2.graphics.OpenGL;
import MimodekV2.graphics.TextureManager;
import MimodekV2.imageexport.MovieRecorder;
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


/**
 * Wraps the controlP5 library and handles the creation and management of a UI to tweak Mimodek's parameters in a child window.
 * Be aware that is you close this window you won't have access to the UI anymore.
 */
public class ParameterControlWindow implements ControlListener{
	//PApplet app;
	/** The control p5. */
	ControlP5 controlP5;
	
	/** The control window. */
	ControlWindow controlWindow;
	
	/** The last graph update. */
	static long lastGraphUpdate;
	
	/** The Constant RESET_BUTTON. */
	private static final int RESET_BUTTON = -1;
	
	
	/** The Constant AUTO_GROWTH_BUTTON. */
	private static final int AUTO_GROWTH_BUTTON = -3;
	
	/** The Constant GLOBAL_SCALING. */
	private static final int GLOBAL_SCALING = -4;
	
	/** The creature count nbn. */
	private static Numberbox creatureCountNbn;
	
	/** The soft cell count nbn. */
	private static Numberbox softCellCountNbn;
	
	/** The hard cell count nbn. */
	private static Numberbox hardCellCountNbn;
	
	/** The creature evolution chart. */
	private static Chart evolutionChart;
	
	private static float[] creatureChange;
	private static float[] cellAChange;
	private static float[] cellBChange;
	

	
	/** The Constant IO_TAB. */
	private static final int IO_TAB = 1;
	
	/** The Constant OPENGL_TEXTURE_FILTER_INT. */
	private static final int OPENGL_TEXTURE_FILTER_INT = IO_TAB*10+1;
	private static final int SAVE_SETTINGS_AS_BUTTON = IO_TAB*10+2;
	private static final int SAVE_SETTINGS_BUTTON = IO_TAB*10+3;
	private static final int RELOAD_SETTINGS_BUTTON = IO_TAB*10+4;
	
	/** The Constant PAUSE_BUTTON. */
	private static final int PAUSE_BUTTON = IO_TAB*10+5;
	
	/** The Constant RECORD_BUTTON. */
	private static final int RECORD_BUTTON = IO_TAB*10+6;
	private static Button recordBtn = null;
	
	
	private static final int HIGH_RESOLUTION_SCALE = IO_TAB*10+7;
	private static Button hiresBtn = null;
	
	/** The Constant HIGH_RESOLUTION_SCREENSHOT_BUTTON. */
	private static final int HIGH_RESOLUTION_SCREENSHOT_BUTTON = IO_TAB*10+8;
	
	/** The Constant QUICK_SNAP_BUTTON. */
	private static final int QUICK_SNAP_BUTTON = IO_TAB*10+9;	
	
	/** The Constant CELLA_TAB. */
	private static final int CELLA_TAB = 2;
	
	/** The Constant CELLA_MAX_TRY_INT. */
	private static final int CELLA_MAX_TRY_INT = CELLA_TAB*10+1;
	
	/** The Constant CELLA_DISTORTION. */
	private static final int CELLA_DISTORTION = CELLA_TAB*10+2;
	
	/** The Constant CELLA_DISTANCE_BETWEEN. */
	private static final int CELLA_DISTANCE_BETWEEN = CELLA_TAB*10+3;
	
	/** The Constant CELLA_RADIUS. */
	private static final int CELLA_RADIUS = CELLA_TAB*10+4;
	
	/** The Constant CELLA_ALPHA. */
	private static final int CELLA_ALPHA = CELLA_TAB*10+5;
	
	/** The Constant CELLA_R. */
	private static final int CELLA_R =CELLA_TAB*10+6;
	
	/** The Constant CELLA_G. */
	private static final int CELLA_G =CELLA_TAB*10+7;
	
	/** The Constant CELLA_B. */
	private static final int CELLA_B =CELLA_TAB*10+8;
	
	/** The Constant CELLA_MASK_STR. */
	private static final int CELLA_MASK_STR = CELLA_TAB*10+9;
	
	/** The Constant CELLA_TEXTURE_STR. */
	private static final int CELLA_TEXTURE_STR = CELLA_TAB*10+10;
	
	/** The mask a name lbl. */
	private static Label maskANameLbl;
	
	/** The tex a name lbl. */
	private static Label texANameLbl; 
	
	/** The Constant CELLB_TAB. */
	private static final int CELLB_TAB = 3;
	
	/** The Constant CELLB_RADIUS. */
	private static final int CELLB_RADIUS = CELLB_TAB*10+1;
	
	/** The Constant CELLB_MIN_DISTANCE_TO_A. */
	private static final int CELLB_MIN_DISTANCE_TO_A = CELLB_TAB*10+2;
	
	/** The Constant CELLB_MAX_DISTANCE_TO_A. */
	private static final int CELLB_MAX_DISTANCE_TO_A = CELLB_TAB*10+3;
	
	/** The Constant CELLB_ALPHA. */
	private static final int CELLB_ALPHA = CELLB_TAB*10+4;
	
	/** The Constant CELLB_R. */
	private static final int CELLB_R =CELLB_TAB*10+5;
	
	/** The Constant CELLB_G. */
	private static final int CELLB_G =CELLB_TAB*10+6;
	
	/** The Constant CELLB_B. */
	private static final int CELLB_B =CELLB_TAB*10+7;
	
	/** The Constant CELLB_ALPHA_VARIATION. */
	private static final int CELLB_ALPHA_VARIATION = CELLB_TAB*10+8;
	
	/** The Constant CELLB_TEXTURE_STR. */
	private static final int CELLB_TEXTURE_STR = CELLB_TAB*10+9;
	
	/** The tex b name lbl. */
	private static Label texBNameLbl;
	
	/** The Constant CREATURE_TAB. */
	private static final int CREATURE_TAB = 4;
	
	/** The Constant CREATURE_SIZE. */
	private static final int CREATURE_SIZE = CREATURE_TAB*10+1;
	
	/** The Constant CREATURE_R. */
	private static final int CREATURE_R =CREATURE_TAB*10+2;
	
	/** The Constant CREATURE_G. */
	private static final int CREATURE_G =CREATURE_TAB*10+3;
	
	/** The Constant CREATURE_B. */
	private static final int CREATURE_B =CREATURE_TAB*10+4;
	
	/** The Constant CREATURE_FULL_R. */
	private static final int CREATURE_FULL_R =CREATURE_TAB*10+5;
	
	/** The Constant CREATURE_FULL_G. */
	private static final int CREATURE_FULL_G =CREATURE_TAB*10+6;
	
	/** The Constant CREATURE_FULL_B. */
	private static final int CREATURE_FULL_B =CREATURE_TAB*10+7;
	
	/** The Constant CREATURE_DISTANCE_BETWEEN. */
	private static final int CREATURE_DISTANCE_BETWEEN = CREATURE_TAB*10+8;
	
	/** The Constant CREATURE_MAXSPEED. */
	private static final int CREATURE_MAXSPEED = CREATURE_TAB*10+9;
	
	/** The Constant CREATURE_STEER_FORCE. */
	private static final int CREATURE_STEER_FORCE = CREATURE_TAB*10;
	
	/** The Constant CREATURE_ALPHA_VARIATION. */
	private static final int CREATURE_ALPHA_VARIATION = CREATURE_TAB*100+1;
	
	/** The Constant CREATURE_ALPHA. */
	private static final int CREATURE_ALPHA= CREATURE_TAB*100+2;
	
	/** The Constant FOOD_TAB. */
	private static final int FOOD_TAB = 5;
	
	/** The Constant FOOD_SIZE. */
	private static final int FOOD_SIZE = FOOD_TAB*10+1;
	
	/** The Constant FOOD_R. */
	private static final int FOOD_R = FOOD_TAB*10+2;
	
	/** The Constant FOOD_G. */
	private static final int FOOD_G = FOOD_TAB*10+3;
	
	/** The Constant FOOD_B. */
	private static final int FOOD_B = FOOD_TAB*10+4;
	
	/** The Constant FOOD_SCENT_EVAPORATION. */
	private static final int FOOD_SCENT_EVAPORATION = FOOD_TAB*10+5;
	
	/** The Constant FOOD_MAX. */
	private static final int FOOD_MAX = FOOD_TAB*10+6;
	
	/** The Constant DATA_TAB. */
	private static final int DATA_TAB = 6;
	
	/** The Constant DATA_REFRESH_RATE. */
	private static final int DATA_REFRESH_RATE = DATA_TAB*10+1;
	
	/** The Constant DATA_TEMPERATURE. */
	private static final int DATA_TEMPERATURE = DATA_TAB*10+2;
	
	/** The Constant DATA_HUMIDITY. */
	private static final int DATA_HUMIDITY = DATA_TAB*10+3;
	
	/** The Constant DATA_POLLUTION. */
	private static final int DATA_POLLUTION = DATA_TAB*10+4;
	
	private static Chart dataChart;
	
	private static float[] temperatureChange;
	private static float[] humidityChange;
	private static float[] pollutionChange;

	
	/** The temperature slider. */
	private static Slider temperatureSlider = null;
	
	/** The humidity slider. */
	private static Slider humiditySlider = null;
	
	/** The pollution slider. */
	private static Slider pollutionSlider = null;
	
	/** The controllers bottom margin. */
	private  int bottomMargin = 10;
	
	/** The controllers height. */
	private  int controllerHeight = 14;
	
	/** The controllers height. */
	private  int controllerWidth = 150;
	
	private int windowWidth = 400;
	private int windowHeight = 300;
	
	private MovieRecorder movieRecorder;
	
	/** The instance. */
	protected static ParameterControlWindow instance;
	
	/**
	 * Creates the control window.
	 *
	 * @param app the parent PApplet
	 * @return the parameter control window
	 */
	public static ParameterControlWindow createControlWindow(PApplet app, MovieRecorder movieRecorder){
		if(instance == null)
			instance = new ParameterControlWindow(app, movieRecorder);
		return instance;
	}
	
	/**
	 * Instantiates a new parameter control window.
	 *
	 * @param app the app
	 */
	protected ParameterControlWindow(PApplet app, MovieRecorder movieRecorder){
		this.movieRecorder = movieRecorder;
		lastGraphUpdate = System.currentTimeMillis();
		//this.app = app;
		Slider slider;
		
		controlP5 = new ControlP5(app);
		controlP5.addListener(this);
		//creates a window for the controls
		controlP5.setAutoDraw(false);
		controlWindow = controlP5.addControlWindow("controlP5window",1000,100,windowWidth,windowHeight);
		controlWindow.hideCoordinates();
		controlWindow.setBackground(app.color(40));
		controlWindow.setTitle("Controls");
		
		int yOffset = bottomMargin*2;
		Button btn = controlP5.addButton("Reset",0,10,yOffset,controllerWidth,controllerHeight);
		btn.setId(RESET_BUTTON);
		btn.setTab(controlWindow,"default");
		//btn.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		btn = controlP5.addButton("Auto growth",0,10,yOffset,controllerWidth,controllerHeight);
		btn.setId(AUTO_GROWTH_BUTTON);
		btn.setTab(controlWindow,"default");
		//btn.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		creatureCountNbn = controlP5.addNumberbox("Creature count",0,10,yOffset,controllerWidth,controllerHeight);
		creatureCountNbn.setId(-10);
		creatureCountNbn.lock();
		creatureCountNbn.setTab(controlWindow,"default");
		creatureCountNbn.captionLabel().style().marginLeft = 15+controllerWidth;
		creatureCountNbn.captionLabel().style().marginTop -= controllerHeight;
		yOffset += controllerHeight+bottomMargin;
		
		softCellCountNbn = controlP5.addNumberbox("Soft cell count",0,10,yOffset,controllerWidth,controllerHeight);
		softCellCountNbn.setId(-11);
		softCellCountNbn.lock();
		softCellCountNbn.setTab(controlWindow,"default");
		softCellCountNbn.captionLabel().style().marginLeft = 15+controllerWidth;
		softCellCountNbn.captionLabel().style().marginTop -= controllerHeight;
		softCellCountNbn.color().setForeground(app.color(0,1,0));
		yOffset += controllerHeight+bottomMargin;
		
		hardCellCountNbn = controlP5.addNumberbox("Hard cell count:",0,10,yOffset,controllerWidth,controllerHeight);
		hardCellCountNbn.setId(-12);
		hardCellCountNbn.lock();
		hardCellCountNbn.setTab(controlWindow,"default");
		hardCellCountNbn.captionLabel().style().marginLeft = 15+controllerWidth;
		hardCellCountNbn.captionLabel().style().marginTop -= controllerHeight;
		hardCellCountNbn.color().setForeground(app.color(1));
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Scaling",0,10f,Configurator.getFloatSetting("GLOBAL_SCALING"),10,yOffset,controllerWidth,controllerHeight);
		slider.setDecimalPrecision(1);
		slider.setId(GLOBAL_SCALING);
		slider.setTab(controlWindow,"default");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		evolutionChart = controlP5.addChart("Creature Evolution",10,yOffset,windowWidth-20,100);
		evolutionChart.setStrokeWeight(1);
		evolutionChart.setTab(controlWindow,"default");
		evolutionChart.setStrokeWeight(1.5f);
		creatureChange = new float[100];
		
		evolutionChart.addDataSet().getColor().setForeground(app.color(1));
		cellAChange = new float[100];
		
		evolutionChart.addDataSet().getColor().setForeground(app.color(0,1,0));
		cellBChange = new float[100];
		
		yOffset += controllerHeight+bottomMargin;
		
		/* I/O TAB */
		Tab t = controlWindow.addTab("I/O");
		t.activateEvent(true);
		t.setId(IO_TAB);
		yOffset = bottomMargin*2;
		
		slider = controlP5.addSlider("Texture Filter",0,OpenGL.TextureFilters.values().length-1,Configurator.getIntegerSetting("GL_TEXTURE_MIN_FILTER_INT"),10,yOffset,controllerWidth,controllerHeight);
		slider.setDecimalPrecision(0);
		slider.setId(OPENGL_TEXTURE_FILTER_INT);
		slider.setTab(controlWindow,"I/O");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		btn = controlP5.addButton("Save settings as...",0,10,yOffset,controllerWidth,controllerHeight);
		btn.setId(SAVE_SETTINGS_AS_BUTTON);
		btn.setTab(controlWindow,"I/O");
		//btn.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		btn = controlP5.addButton("Save settings",0,10,yOffset,controllerWidth,controllerHeight);
		btn.setId(SAVE_SETTINGS_BUTTON);
		btn.setTab(controlWindow,"I/O");
		//btn.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		btn = controlP5.addButton("Reload settings from file",0,10,yOffset,controllerWidth,controllerHeight);
		btn.setId(RELOAD_SETTINGS_BUTTON);
		btn.setTab(controlWindow,"I/O");
		//btn.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		btn = controlP5.addButton("Pause/Run",0,10,yOffset,controllerWidth,controllerHeight);
		btn.captionLabel().set(Mimodek.update?"||":">");
		btn.setId(PAUSE_BUTTON);
		btn.setTab(controlWindow,"I/O");
		//btn.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		recordBtn = controlP5.addButton("Record Image sequence (Off)",0,10,yOffset,controllerWidth,controllerHeight);
		recordBtn.setId(RECORD_BUTTON);
		recordBtn.setTab(controlWindow,"I/O");
		//btn.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		
		slider = controlP5.addSlider("HD scale",1,20,15,10,yOffset,controllerWidth,controllerHeight);
		slider.setDecimalPrecision(0);
		slider.setId(HIGH_RESOLUTION_SCALE);
		slider.setTab(controlWindow,"I/O");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		
		hiresBtn = controlP5.addButton("Export HD "+(FacadeFactory.getFacade().width*Mimodek.hdScale)+" x "+(FacadeFactory.getFacade().height*Mimodek.hdScale),0,10,yOffset,controllerWidth,controllerHeight);
		hiresBtn.setId(HIGH_RESOLUTION_SCREENSHOT_BUTTON);
		hiresBtn.setTab(controlWindow,"I/O");
		//btn.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		btn = controlP5.addButton("Take a quick snapshot",0,10,yOffset,controllerWidth,controllerHeight);
		btn.setId(QUICK_SNAP_BUTTON);
		btn.setTab(controlWindow,"I/O");
		//btn.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		
		 
		
		
		/* HARD CELL TAB */
		t = controlWindow.addTab("Hard Cells");
		t.activateEvent(true);
		t.setId(CELLA_TAB);
		yOffset = bottomMargin*2;
		
		slider = controlP5.addSlider("Max try",0,150,Configurator.getIntegerSetting("CELLA_MAX_TRY_INT"),10,yOffset,controllerWidth,controllerHeight);
		slider.setDecimalPrecision(0);
		slider.setId(CELLA_MAX_TRY_INT);
		slider.setTab(controlWindow,"Hard Cells");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Distortion",0,1f,Configurator.getFloatSetting("CELLA_DISTORTION"),10,yOffset,controllerWidth,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(CELLA_DISTORTION);
		slider.setTab(controlWindow,"Hard Cells");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Distance between",0,1f,Configurator.getFloatSetting("CELLA_DISTANCE_BETWEEN"),10,yOffset,controllerWidth,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(CELLA_DISTANCE_BETWEEN);
		slider.setTab(controlWindow,"Hard Cells");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("A Radius",0,50f,Configurator.getFloatSetting("CELLA_RADIUS"),10,yOffset,controllerWidth,controllerHeight);
		slider.setDecimalPrecision(1);
		slider.setId(CELLA_RADIUS);
		slider.setTab(controlWindow,"Hard Cells");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("A Transparency",0,1f,Configurator.getFloatSetting("CELLA_ALPHA"),10,yOffset,controllerWidth,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(CELLA_ALPHA);
		slider.setTab(controlWindow,"Hard Cells");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("A Red",0,1f,Configurator.getFloatSetting("CELLA_R"),10,yOffset,controllerWidth,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(CELLA_R);
		slider.setTab(controlWindow,"Hard Cells");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("A Green",0,1f,Configurator.getFloatSetting("CELLA_G"),10,yOffset,controllerWidth,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(CELLA_G);
		slider.setTab(controlWindow,"Hard Cells");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("A Blue",0,1f,Configurator.getFloatSetting("CELLA_B"),10,yOffset,controllerWidth,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(CELLA_B);
		slider.setTab(controlWindow,"Hard Cells");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		
		slider = controlP5.addSlider("Mask:",TextureManager.min,TextureManager.max,TextureManager.getTextureIndex(Configurator.getStringSetting("CELLA_MASK_STR")),10,yOffset,controllerWidth,controllerHeight);
		slider.setDecimalPrecision(0);
		slider.setId(CELLA_MASK_STR);
		slider.setTab(controlWindow,"Hard Cells");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		maskANameLbl = slider.captionLabel();
		maskANameLbl.set("Mask: "+Configurator.getStringSetting("CELLA_MASK_STR"));
		
		slider = controlP5.addSlider("A Texture:",TextureManager.min,TextureManager.max,TextureManager.getTextureIndex(Configurator.getStringSetting("CELLA_TEXTURE_STR")),10,yOffset,controllerWidth,controllerHeight);
		slider.setDecimalPrecision(0);
		slider.setId(CELLA_TEXTURE_STR);
		slider.setTab(controlWindow,"Hard Cells");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		texANameLbl = slider.captionLabel();
		texANameLbl.set("Mask: "+Configurator.getStringSetting("CELLA_TEXTURE_STR"));


		
		
		/* Soft Cells TAB */
		t = controlWindow.addTab("Soft Cells");
		t.activateEvent(true);
		t.setId(CELLB_TAB);
		yOffset = bottomMargin*2;
		
		slider = controlP5.addSlider("B Radius",0,50f,Configurator.getFloatSetting("CELLB_RADIUS"),10,yOffset,controllerWidth,controllerHeight);
		slider.setDecimalPrecision(1);
		slider.setId(CELLB_RADIUS);
		slider.setTab(controlWindow,"Soft Cells");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Min distance to structure",0,50f,Configurator.getFloatSetting("CELLB_MIN_DISTANCE_TO_A"),10,yOffset,controllerWidth,controllerHeight);
		slider.setDecimalPrecision(1);
		slider.setId(CELLB_MIN_DISTANCE_TO_A);
		slider.setTab(controlWindow,"Soft Cells");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Max distance to structure",0,50f,Configurator.getFloatSetting("CELLB_MAX_DISTANCE_TO_A"),10,yOffset,controllerWidth,controllerHeight);
		slider.setDecimalPrecision(1);
		slider.setId(CELLB_MAX_DISTANCE_TO_A);
		slider.setTab(controlWindow,"Soft Cells");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;

		
		slider = controlP5.addSlider("B Transparency",0,1f,Configurator.getFloatSetting("CELLB_ALPHA"),10,yOffset,controllerWidth,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(CELLB_ALPHA);
		slider.setTab(controlWindow,"Soft Cells");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("B Red",0,1f,Configurator.getFloatSetting("CELLB_R"),10,yOffset,controllerWidth,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(CELLB_R);
		slider.setTab(controlWindow,"Soft Cells");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("B Green",0,1f,Configurator.getFloatSetting("CELLB_G"),10,yOffset,controllerWidth,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(CELLB_G);
		slider.setTab(controlWindow,"Soft Cells");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("B Blue",0,1f,Configurator.getFloatSetting("CELLB_B"),10,yOffset,controllerWidth,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(CELLB_B);
		slider.setTab(controlWindow,"Soft Cells");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Transparency variation",0,1f,Configurator.getFloatSetting("CELLB_ALPHA_VARIATION"),10,yOffset,controllerWidth,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(CELLB_ALPHA_VARIATION);
		slider.setTab(controlWindow,"Soft Cells");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		
		slider = controlP5.addSlider("B Texture:",TextureManager.min,TextureManager.max,TextureManager.getTextureIndex(Configurator.getStringSetting("CELLB_TEXTURE_STR")),10,yOffset,controllerWidth,controllerHeight);
		slider.setDecimalPrecision(0);
		slider.setId(CELLB_TEXTURE_STR);
		slider.setTab(controlWindow,"Soft Cells");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		texBNameLbl = slider.captionLabel();
		texBNameLbl.set("B Texture: "+Configurator.getStringSetting("CELLB_TEXTURE_STR"));
		
		
		
		/* CREATURE TAB */
		t = controlWindow.addTab("Creatures");
		t.activateEvent(true);
		t.setId(CREATURE_TAB);
		yOffset = bottomMargin*2;
		
		slider = controlP5.addSlider("Size",OpenGL.poinSizetInfo.x,OpenGL.poinSizetInfo.y,Configurator.getFloatSetting("CREATURE_RADIUS"),10,yOffset,controllerWidth,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(CREATURE_SIZE);
		slider.setTab(controlWindow,"Creatures");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Creature Red",0,1f,Configurator.getFloatSetting("CREATURE_R"),10,yOffset,controllerWidth,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(CREATURE_R);
		slider.setTab(controlWindow,"Creatures");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Creature Green",0,1f,Configurator.getFloatSetting("CREATURE_G"),10,yOffset,controllerWidth,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(CREATURE_G);
		slider.setTab(controlWindow,"Creatures");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Creature Blue",0,1f,Configurator.getFloatSetting("CREATURE_B"),10,yOffset,controllerWidth,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(CREATURE_B);
		slider.setTab(controlWindow,"Creatures");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Creature Transparency",0,1f,Configurator.getFloatSetting("CREATURE_ALPHA"),10,yOffset,controllerWidth,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(CREATURE_ALPHA);
		slider.setTab(controlWindow,"Creatures");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Creature Full Red",0,1f,Configurator.getFloatSetting("CREATURE_FULL_R"),10,yOffset,controllerWidth,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(CREATURE_FULL_R);
		slider.setTab(controlWindow,"Creatures");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Creature Full Green",0,1f,Configurator.getFloatSetting("CREATURE_FULL_G"),10,yOffset,controllerWidth,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(CREATURE_FULL_G);
		slider.setTab(controlWindow,"Creatures");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Creature Full Blue",0,1f,Configurator.getFloatSetting("CREATURE_FULL_B"),10,yOffset,controllerWidth,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(CREATURE_FULL_B);
		slider.setTab(controlWindow,"Creatures");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Distance betwwen Creatures",0f,100f,Configurator.getFloatSetting("CREATURE_DISTANCE_BETWEEN"),10,yOffset,controllerWidth,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(CREATURE_DISTANCE_BETWEEN);
		slider.setTab(controlWindow,"Creatures");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Max. Speed",0f,5f,Configurator.getFloatSetting("CREATURE_MAXSPEED"),10,yOffset,controllerWidth,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(CREATURE_MAXSPEED);
		slider.setTab(controlWindow,"Creatures");
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Max. Steer Force",0f,1f,Configurator.getFloatSetting("CREATURE_STEER_FORCE"),10,yOffset,controllerWidth,controllerHeight);
		slider.setDecimalPrecision(3);
		slider.setId(CREATURE_STEER_FORCE);
		slider.setTab(controlWindow,"Creatures");
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Brightness variation",0,1f,Configurator.getFloatSetting("CREATURE_ALPHA_VARIATION"),10,yOffset,controllerWidth,controllerHeight);
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
		
		slider = controlP5.addSlider("Food Size",0f,5f,Configurator.getFloatSetting("FOOD_SIZE"),10,yOffset,controllerWidth,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(FOOD_SIZE);
		slider.setTab(controlWindow,"Food");
		////slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		
		slider = controlP5.addSlider("Food Red",0,1f,Configurator.getFloatSetting("FOOD_R"),10,yOffset,controllerWidth,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(FOOD_R);
		slider.setTab(controlWindow,"Food");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Food Green",0,1f,Configurator.getFloatSetting("FOOD_G"),10,yOffset,controllerWidth,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(FOOD_G);
		slider.setTab(controlWindow,"Food");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Food Blue",0,1f,Configurator.getFloatSetting("FOOD_B"),10,yOffset,controllerWidth,controllerHeight);
		slider.setDecimalPrecision(2);
		slider.setId(FOOD_B);
		slider.setTab(controlWindow,"Food");
		//slider.addListener(this);
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Smell Evaporation",0,1f,Configurator.getFloatSetting("FOOD_SCENT_EVAPORATION"),10,yOffset,controllerWidth,controllerHeight);
		slider.setDecimalPrecision(3);
		slider.setId(FOOD_SCENT_EVAPORATION);
		slider.setTab(controlWindow,"Food");
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Max food",0,1000,Configurator.getIntegerSetting("FOOD_MAX"),10,yOffset,controllerWidth,controllerHeight);
		slider.setDecimalPrecision(0);
		slider.setId(FOOD_MAX);
		slider.setTab(controlWindow,"Food");
		yOffset += controllerHeight+bottomMargin;
		
		
		/* DATA TAB */
		t = controlWindow.addTab("Data");
		t.activateEvent(true);
		t.setId(DATA_TAB);
		yOffset = bottomMargin*2;
		
		slider = controlP5.addSlider("Refresh time (in minutes)",0.1f,10f,Configurator.getFloatSetting("DATA_REFRESH_RATE"),10,yOffset,controllerWidth,controllerHeight);
		slider.setDecimalPrecision(1);
		slider.setId(DATA_REFRESH_RATE);
		slider.setTab(controlWindow,"Data");
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Temperature",TemperatureColorRanges.minT,TemperatureColorRanges.maxT,Configurator.getFloatSetting("DATA_TEMPERATURE"),10,yOffset,controllerWidth,controllerHeight);
		slider.setDecimalPrecision(1);
		slider.setId(DATA_TEMPERATURE);
		slider.setTab(controlWindow,"Data");
		slider.color().setValueLabel(app.color(0));
		slider.color().setForeground(app.color(1,0,0));
		temperatureSlider = slider;
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Humidity",0f,100f,Configurator.getFloatSetting("DATA_HUMIDITY"),10,yOffset,controllerWidth,controllerHeight);
		slider.setDecimalPrecision(1);
		slider.setId(DATA_HUMIDITY);
		slider.setTab(controlWindow,"Data");
		slider.setColorValueLabel(app.color(0));
		slider.color().setForeground(app.color(1));
		humiditySlider = slider;
		yOffset += controllerHeight+bottomMargin;
		
		slider = controlP5.addSlider("Pollution",0f,21f,Configurator.getFloatSetting("DATA_POLLUTION"),10,yOffset,controllerWidth,controllerHeight);
		slider.setDecimalPrecision(0);
		slider.setId(DATA_POLLUTION);
		slider.setTab(controlWindow,"Data");
		slider.setColorValueLabel(app.color(0));
		slider.color().setForeground(app.color(0,1,0));
		pollutionSlider = slider;
		yOffset += controllerHeight+bottomMargin;
		
		dataChart = controlP5.addChart("Data Chart",10,yOffset,windowWidth-20,100);
		dataChart.setStrokeWeight(1);
		dataChart.setTab(controlWindow,"Data");
		dataChart.setStrokeWeight(1.5f);
		dataChart.addDataSet().getColor().setForeground(app.color(1,0,0));
		temperatureChange = new float[100];
		
		dataChart.addDataSet().getColor().setForeground(app.color(1));
		humidityChange = new float[100];
		
		dataChart.addDataSet().getColor().setForeground(app.color(0,1,0));
		pollutionChange = new float[100];
		
		yOffset += controllerHeight+bottomMargin;
		
	}

	/* (non-Javadoc)
	 * @see controlP5.ControlListener#controlEvent(controlP5.ControlEvent)
	 */
	public void controlEvent(ControlEvent ctrlEvent) {
		//System.out.println("Control Event");
		if(ctrlEvent.isController()){
			switch(ctrlEvent.controller().id()){
			case RESET_BUTTON:
				Mimodek.reset = true;
				break;
			case AUTO_GROWTH_BUTTON:
				Mimodek.autoFood = !Mimodek.autoFood;
				ctrlEvent.controller().captionLabel().set("Auto growth ("+(Mimodek.autoFood?"on":"off")+")");
				break;
			case GLOBAL_SCALING:
				Configurator.setSetting("GLOBAL_SCALING", ctrlEvent.controller().value());
				break;
				
			case OPENGL_TEXTURE_FILTER_INT:
				Configurator.setSetting("GL_TEXTURE_MIN_FILTER_INT", (int)ctrlEvent.controller().value());
				break;
			case SAVE_SETTINGS_AS_BUTTON :
				Configurator.saveAs();
				break;
			case SAVE_SETTINGS_BUTTON :
				Configurator.save();
				break;
			case RELOAD_SETTINGS_BUTTON :
				Configurator.reload();
				break;
			case PAUSE_BUTTON:
				Mimodek.update = !Mimodek.update;
				if(!Mimodek.update && movieRecorder.isRecording()){
					movieRecorder.pause();
					recordBtn.captionLabel().set("Record Image sequence (Paused)");
					//recordBtn.disableSprite();
				}else if(Mimodek.update && movieRecorder.isPaused()){
					movieRecorder.resume();
					recordBtn.captionLabel().set("Record Image sequence (Recording)");
					//recordBtn.enableSprite();
				}
				ctrlEvent.controller().captionLabel().set(Mimodek.update?"||":">");
				break;
			case RECORD_BUTTON :
					movieRecorder.toggleRecording();
					if(!movieRecorder.isRecording() && movieRecorder.isPaused()){
						movieRecorder.resume(); //reset default state
					}else if(movieRecorder.isRecording() && !Mimodek.update){
						movieRecorder.pause();
					}
					recordBtn.captionLabel().set("Record Image sequence ("+(movieRecorder.isRecording()?(movieRecorder.isPaused()?"Paused":"Recording"):"Off")+")");
				break;
			case HIGH_RESOLUTION_SCALE:
				Mimodek.hdScale = (int)ctrlEvent.controller().value();
				hiresBtn.captionLabel().set("Export HD "+(FacadeFactory.getFacade().width*Mimodek.hdScale)+" x "+(FacadeFactory.getFacade().height*Mimodek.hdScale));
				break;
			case HIGH_RESOLUTION_SCREENSHOT_BUTTON:
				Mimodek.hd = true;
				break;
			case QUICK_SNAP_BUTTON:
				Mimodek.takeSnapShot = true;
				Mimodek.saveScreenShot = true;
				break;
				
			/*CELL A*/
			case CELLA_MAX_TRY_INT:
				Configurator.setSetting("CELLA_MAX_TRY_INT", (int)ctrlEvent.controller().value());
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
			case CELLA_MASK_STR:
				Configurator.setSetting("CELLA_MASK_STR",TextureManager.getNameOf((int)ctrlEvent.controller().value()));
				maskANameLbl.set("Texture:"+Configurator.getStringSetting("CELLA_MASK_STR"));
				break;
			case CELLA_TEXTURE_STR:
				Configurator.setSetting("CELLA_TEXTURE_STR",TextureManager.getNameOf((int)ctrlEvent.controller().value()));
				texANameLbl.set("Texture:"+Configurator.getStringSetting("CELLA_TEXTURE_STR"));
				break;	
				
				/*CELL B*/
			case CELLB_RADIUS:
				Configurator.setSetting("CELLB_RADIUS",ctrlEvent.controller().value());
				break;	
			case CELLB_MIN_DISTANCE_TO_A:
				float minD = ctrlEvent.controller().value();
				if(minD>Configurator.getFloatSetting("CELLB_MAX_DISTANCE_TO_A")){
					minD = Configurator.getFloatSetting("CELLB_MAX_DISTANCE_TO_A");
					ctrlEvent.controller().setValue(minD);
				}
				Configurator.setSetting("CELLB_MIN_DISTANCE_TO_A",minD);
				break;
			case CELLB_MAX_DISTANCE_TO_A:
				float maxD = ctrlEvent.controller().value();
				if(maxD<Configurator.getFloatSetting("CELLB_MIN_DISTANCE_TO_A")){
					maxD = Configurator.getFloatSetting("CELLB_MIN_DISTANCE_TO_A");
					ctrlEvent.controller().setValue(maxD);
				}
				Configurator.setSetting("CELLB_MAX_DISTANCE_TO_A",maxD);
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
			case CELLB_TEXTURE_STR:
				Configurator.setSetting("CELLB_TEXTURE_STR",TextureManager.getNameOf((int)ctrlEvent.controller().value()));
				texBNameLbl.set("Texture:"+Configurator.getStringSetting("CELLB_TEXTURE_STR"));
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
			case FOOD_MAX:
				Configurator.setSetting("FOOD_MAX",(int)ctrlEvent.controller().value());
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
	
	/**
	 * Update the counters.
	 *
	 * @param c the number of creatures
	 * @param h the number of type A cells
	 * @param s the number of type B cells
	 */
	public static void setCounters(int c, int h, int s){
		creatureCountNbn.setValue(c);
		softCellCountNbn.setValue(s);
		hardCellCountNbn.setValue(h);
		if(System.currentTimeMillis()-lastGraphUpdate>(60*1000)/15f){
			lastGraphUpdate = System.currentTimeMillis();
			
			 float[] tf = new float[creatureChange.length-1];
			    PApplet.arrayCopy(creatureChange,tf,tf.length);
			    creatureChange[0] = c;
			    PApplet.arrayCopy(tf,0,creatureChange,1,tf.length);
			    evolutionChart.updateData(0,creatureChange);
			    
			    PApplet.arrayCopy(cellAChange,tf,tf.length);
			    cellAChange[0] = h/2f;
			    PApplet.arrayCopy(tf,0,cellAChange,1,tf.length);
			    evolutionChart.updateData(1,cellAChange);
			    
			    PApplet.arrayCopy(cellBChange,tf,tf.length);
			    cellBChange[0] = s/2f;
			    PApplet.arrayCopy(tf,0,cellBChange,1,tf.length);
			    evolutionChart.updateData(2,cellBChange);
			
		}
	}
	
	/**
	 * Update the data sliders to display the current state of each variables.
	 */
	public static void setData(){
		//float[] tf = new float[99];
		if(temperatureSlider != null){
			temperatureSlider.setValue(Configurator.getFloatSetting("DATA_TEMPERATURE"));
			//PApplet.arrayCopy(temperatureChange,tf,tf.length);
			//temperatureChange[0] = Configurator.getFloatSetting("DATA_TEMPERATURE")+20f;
		    //PApplet.arrayCopy(tf,0,temperatureChange,1,tf.length);
		    dataChart.addFirst(1,Configurator.getFloatSetting("DATA_TEMPERATURE")+50f);
		    dataChart.removeLast(1);
		}
		if(humiditySlider != null){
			humiditySlider.setValue(Configurator.getFloatSetting("DATA_HUMIDITY"));
			//PApplet.arrayCopy(humidityChange,tf,tf.length);
			//humidityChange[0] = Configurator.getFloatSetting("DATA_HUMIDITY");
		    //PApplet.arrayCopy(tf,0,humidityChange,1,tf.length);
		    //dataChart.updateData(0,humidityChange);
		    dataChart.addFirst(2,Configurator.getFloatSetting("DATA_HUMIDITY"));
		    dataChart.removeLast(2);
		}
		if(pollutionSlider != null){
			pollutionSlider.setValue(Configurator.getFloatSetting("DATA_POLLUTION"));
			//PApplet.arrayCopy(pollutionChange,tf,tf.length);
			//pollutionChange[0] = Configurator.getFloatSetting("DATA_POLLUTION");
		    //PApplet.arrayCopy(tf,0,pollutionChange,1,tf.length);
		    dataChart.addFirst(3, Configurator.getFloatSetting("DATA_POLLUTION"));
		    dataChart.removeLast(3);
		}
	}	

}
