package MimodekV2;

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


import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.GLException;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.ImageWriteException;

import com.sun.opengl.util.texture.TextureIO;

import MimodekV2.config.AWTUtils;
import MimodekV2.config.Configurator;
import MimodekV2.config.ParameterControlWindow;
import MimodekV2.graphics.FBO;
import MimodekV2.graphics.ImageBoard;
import MimodekV2.graphics.OpenGL;
import MimodekV2.graphics.TextureManager;
import MimodekV2.imageexport.JpgWithExifTextureWriter;
import MimodekV2.imageexport.MovieRecorder;
import MimodekV2.imageexport.SFTPClient;
import MimodekV2.tracking.TUIOClient;
import MimodekV2.tracking.TrackingInfo;
import MimodekV2.tracking.TrackingListener;

import MimodekV2.data.DataHandler;
import MimodekV2.data.DataInterpolator;
import MimodekV2.data.PollutionDataInterpolator;
import MimodekV2.data.TemperatureColorRanges;
import MimodekV2.data.TemperatureDataInterpolator;
import MimodekV2.debug.Verbose;
import mimodek.facade.FacadeFactory;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

// TODO: Auto-generated Javadoc
/**
 * The Class Mimodek.
 */
public class Mimodek implements PConstants, TrackingListener {

	/** The number of vertices making up the geometry of type A cells */
	public static int CELLA_VERTEX_NUMBER = 20;

	/** The Creatures OpenGL texture object. */
	public static int CREATURE_TEXTURE = 0;


	/** The cells. */
	public static ArrayList<Cell> theCells = new ArrayList<Cell>();
	
	/** The a cells. */
	public static ArrayList<CellA> aCells = new ArrayList<CellA>();
	
	/** The b cells. */
	public static ArrayList<CellB> bCells = new ArrayList<CellB>();
	
	/** The growing cells. */
	public static ArrayList<Cell> growingCells = new ArrayList<Cell>();

	/** The foods. */
	public static ArrayList<Food> foods = new ArrayList<Food>();
	
	/** The food average position. */
	public static PVector foodAvg = new PVector(0, 0);
	
	/** The creatures. */
	public static ArrayList<Creature> creatures = new ArrayList<Creature>();

	/** The scent map. */
	public static QTree scentMap;

	/** The genetics. */
	public static LSystem genetics;

	/* instance attributes */

	/** The auto food. */
	public static boolean autoFood = false;
	
	/** The show pheromone. */
	boolean showPheromone = false;
	
	/** The update. */
	public static boolean update = false;
	
	/** The reset. */
	public static boolean reset = false;
	
	/** The take snap shot. */
	public static boolean takeSnapShot = false;
	
	/** The save screen shot. */
	public static boolean saveScreenShot = false;
	
	/** The screen shot_counter. */
	private static int screenShot_counter = 0;
	
	/** If true, next frame will be exported as a set of tile images forming a high resolution screen shot of Mimodek.
	 * Use ImageTileStitcher sketch to stitch them back together in one big image.
	 * */
	public static boolean hd = false;
	
	/**
	 * Number of tiles per row used when rendering high resolution frames
	 */
	public static int hdScale = 15;



	/** The tuio client. */
	TUIOClient tuioClient;

	/** The app. */
	PApplet app;
	
	/** The data handler. */
	DataHandler dataHandler;

	/** The file transfer client. */
	SFTPClient fileTransfer;
	
	/** The image board. */
	ImageBoard imageBoard;
	
	/** The custom texture writer. */
	JpgWithExifTextureWriter customWriter;
	
	/** The location. */
	MimodekLocation location;
	
	
	/** The Frame Buffer Object used for screen shots. */
	private FBO fbo;

	
	
	/** This flag set to true the first time the Mimodek library is initialized to prevent threads and such to be created more than once. */
	private boolean mimodekReady = false;
	
	/**
	 * Used to record Mimodek's frame as image sequence
	 */
	private MovieRecorder movieRecorder;
	
	/**
	 * The folder where high resolution tiles are stored
	 */
	File tilesFolder;
	
	/**
	 * The folder where screen shots are to be stored
	 */
	File screenFolder;
	
	/**
	 * Instantiates a new mimodek.
	 *
	 * @param app the app
	 */
	public Mimodek(PApplet app) {
		this.app = app;
		app.hint(ENABLE_OPENGL_4X_SMOOTH); // anti-aliasing
		app.registerDispose(this);
		app.registerPost(this);
		app.registerPre(this);
		app.registerDraw(this);
	}

	/**
	 * Start Mimodek.
	 * Get information about the system, reads configuration files, load textures, create threads, etc..
	 */
	private void startMimodek() {
		// IMPORTANT: Create the Configurator first to be sure to have all the
		// parameters properly initialized
		// create configuration
		//Check that all the folder are present and create the missing ones
		String mimodekPath = app.sketchPath.substring(0,app.sketchPath.indexOf(File.separatorChar, app.sketchPath.indexOf("Mimodek")));
		File settingsFolder = new File(mimodekPath+File.separatorChar+"settings");
		Verbose.overRule("Settings folder:"+settingsFolder.getAbsolutePath());
		if(!settingsFolder.exists() || !settingsFolder.isDirectory()){
			if(!settingsFolder.mkdir()){
				Verbose.overRule("Can't create settings folder at "+settingsFolder.getAbsolutePath()+"\n");
				Verbose.overRule("Make sure Mimodek have the correct permission to write on the file system.");
				app.exit();
			}
			Verbose.debug("Settings folder created at "+settingsFolder.getAbsolutePath());
		}else{
			Verbose.debug("Settings folder exists.");
		}
		Configurator.createConfigurator(app);
		Configurator.loadFromFile(settingsFolder.getAbsolutePath()+File.separatorChar+"settings.xml");
		Verbose.speak = Configurator.getBooleanSetting("DEBUG_FLAG");
		update = Configurator.getBooleanSetting("AUTO_START_FLAG");
		
		// setup facade
		FacadeFactory.createFacade(Configurator.getIntegerSetting("FACADE_TYPE_INT"), app).border = 10f;

		// load color range
		try{
			TemperatureColorRanges.createTemperatureColorRanges(app,settingsFolder.getAbsolutePath()+File.separatorChar+"MimodekColourRanges.txt");
		}catch(Exception e){
			Verbose.overRule(e.getMessage());
		}
		
		//Check that all the folder are present and create the missing ones
		File cacheFolder = new File(mimodekPath+File.separatorChar+"cache");
		if(!cacheFolder.exists() || !cacheFolder.isDirectory()){
			if(!cacheFolder.mkdir()){
				Verbose.overRule("Can't create cache folder at "+cacheFolder.getAbsolutePath()+"\n");
				Verbose.overRule("Make sure Mimodek have the correct permission to write on the file system.");
				app.exit();
			}
			Verbose.debug("Cache folder created at"+cacheFolder.getAbsolutePath());
		}else{
			Verbose.debug("Cache folder exists.");
		}
		
		File texturesFolder = new File(mimodekPath+File.separatorChar+"textures");
		if(!texturesFolder.exists() || !texturesFolder.isDirectory()){
			if(!texturesFolder.mkdir()){
				Verbose.overRule("Can't create textures folder at "+texturesFolder.getAbsolutePath());
				Verbose.overRule("Make sure Mimodek have the correct permission to write on the file system.");
				app.exit();
			}
			Verbose.debug("Textures folder created at"+texturesFolder.getAbsolutePath());
			Verbose.overRule("The textures folder is empty, Mimodek will be really boring! Please create some textures and change settings.xml to use them.");
		}else{
			Verbose.debug("Textures folder exists.");
		}
		
		tilesFolder = new File(mimodekPath+File.separatorChar+"tiles");
		if(!tilesFolder.exists() || !tilesFolder.isDirectory()){
			if(!tilesFolder.mkdir()){
				Verbose.overRule("Can't create tiles folder at "+tilesFolder.getAbsolutePath()+"\n");
				Verbose.overRule("Make sure Mimodek have the correct permission to write on the file system.");
				app.exit();
			}
			Verbose.debug("Tiles folder created at"+tilesFolder.getAbsolutePath());
		}else{
			Verbose.debug("Tiles folder exists.");
		}
		
		screenFolder = new File(mimodekPath+File.separatorChar+"screen_shots");
		if(!screenFolder.exists() || !screenFolder.isDirectory()){
			if(!screenFolder.mkdir()){
				Verbose.overRule("Can't create screen shots folder at "+screenFolder.getAbsolutePath());
				Verbose.overRule("Make sure Mimodek have the correct permission to write on the file system.");
				app.exit();
			}
			Verbose.debug("Screen shots folder created at"+screenFolder.getAbsolutePath()+"\n");
		}else{
			Verbose.debug("Screen shots folder exists.");
		}
		

		// tracking client
		tuioClient = new TUIOClient(app);
		tuioClient.setListener(this);

		// Test that we can get a weather station for this location
		// NOTE: this test has a side effect of setting the starting values for
		// the weather variable, neat!
		location = new MimodekLocation();
		location.city = Configurator.isSettingSet("LOCATION_CITY_STR") ? Configurator
				.getStringSetting("LOCATION_CITY_STR")
				: null;
		location.country = Configurator.isSettingSet("LOCATION_COUNTRY_STR") ? Configurator
				.getStringSetting("LOCATION_COUNTRY_STR")
				: null;
		location.latitude = Configurator.isSettingSet("LOCATION_LATITUDE") ? Configurator
				.getFloatSetting("LOCATION_LATITUDE")
				: null;
		location.longitude = Configurator.isSettingSet("LOCATION_LONGITUDE") ? Configurator
				.getFloatSetting("LOCATION_LONGITUDE")
				: null;
		Verbose.overRule("Hello "+location+"!");
		
		try{
			DataHandler.testDataSource(app, location);	
			// data update thread
			dataHandler = DataHandler.getInstance(location, app);
		}catch(Exception e){
			Verbose.debug("No weather station has been found for "+location+". Try the closest biggest city perhaps? \nMimodek will stop.");
			app.exit(); // no weather station so Mimodek can't run, too bad....
		}
		// in the future, more variables could be mapped
		dataHandler.map("temp_c", "DATA_TEMPERATURE");
		dataHandler.map("relative_humidity", "DATA_HUMIDITY");
		
		// create and register data interpolators
		CellA.temperatureInterpolator = new TemperatureDataInterpolator(
				dataHandler);
		CellA.humidityInterpolator = new DataInterpolator("DATA_HUMIDITY",
				dataHandler);
		CellB.pollutionInterpolator = new PollutionDataInterpolator(dataHandler);
		CellB.temperatureInterpolator = new DataInterpolator(
				"DATA_TEMPERATURE", dataHandler);
		dataHandler.start();
		
		// custom texture writer
		customWriter = new JpgWithExifTextureWriter();
		
		if(Configurator.getBooleanSetting("POST_PICTURES_FLAG")){

			
			//SFTP login information
			/*
			//uncomment to use a prompt to enter your login info
			Frame f = AWTUtils.findParentFrame(app);
			String user = AWTUtils.stringDialog("User Name?", f);
			String pass = AWTUtils.passwordDialog("Password?", f);
			*/
			//TODO: FTP : Not safe but easy
			String user = "Put your user name here";
			String pass = "Put your password here";
			if(pass!=null){
				// file transfer thread
				// TODO: prompt for user name and pass
				
					fileTransfer = SFTPClient.createInstance(user,pass,
							Configurator.getStringSetting("SFTP_HOST_STR"), cacheFolder.getAbsolutePath(), Configurator
									.getStringSetting("GALLERY_FOLDER_STR"), customWriter,
							location);
					fileTransfer.start();
			}

		}

		
		
		
		
		// TODO: implement those so we can remove the functions in the sketch
		// app.registerKeyEvent(this);
		// app.registerMouseEvent(this);

		// Do some gfx initialization
		OpenGL.setupGLFor2D(app);
		int textureSupport = OpenGL.howManyTextureUnitSupported();
		if(textureSupport<2){
			Verbose.overRule("Your system needs to support at least 2 texture units on the GPU. Only "+textureSupport+" supported.");
			Verbose.overRule("Mimodek won't work on this system, sorry...");
			app.exit();
		}
		Verbose.debug("Multitexturing:" +OpenGL.howManyTextureUnitSupported() + " textures supported");
		OpenGL.aboutPointSize(); //obtain inforamtion about point size min and max size

		//Load and create textures and buffers
		TextureManager.loadAllTextureInFolder(app, texturesFolder);
		//TODO: Find a better way to handle this
		CREATURE_TEXTURE = OpenGL.createCreatureTexture(app, 0.5f);
		
		//create an FBO with associated texture for screen capture
		int hiResScaling = Configurator.getIntegerSetting("SCREEN_SHOT_SCALING_INT");
		try{	
			fbo = FBO.createFBO(hiResScaling*FacadeFactory.getFacade().width,hiResScaling*FacadeFactory.getFacade().height);
			Verbose.debug(fbo);
		}catch(Exception e){
			e.printStackTrace();
			app.exit();
		}
		
		//Little utility to display messages at a given interval
		imageBoard = new ImageBoard();
		imageBoard.startTimer();
		
		if(!Configurator.getBooleanSetting("FILMING_FLAG"))
			OpenGL.registerTextureWriter(customWriter);
		

		// Create the controls window
		movieRecorder = new MovieRecorder();
		ParameterControlWindow.createControlWindow(app,movieRecorder);

		initMimodek();
	}

	/**
	 * Set the initial running conditions of Mimodek.
	 */
	public void initMimodek() {
		scentMap = new QTree(0, 0, FacadeFactory.getFacade().width, FacadeFactory.getFacade().height, 1);
		genetics = new LSystem("ab", app);

		//FRAME_COUNT = 0;

		theCells = new ArrayList<Cell>();
		aCells = new ArrayList<CellA>();
		bCells = new ArrayList<CellB>();
		growingCells = new ArrayList<Cell>();

		foods = new ArrayList<Food>();
		foodAvg = new PVector(0, 0);
		creatures = new ArrayList<Creature>();

		CellA a = new CellA(new PVector(FacadeFactory.getFacade().halfWidth,
				FacadeFactory.getFacade().halfHeight), 1f);
		a.maturity = 1.0f;
		aCells.add(a);
		theCells.add(a);
		Creature.createHighLanderCreature(true);
		Creature.createHighLanderCreature(true);
		Creature.createHighLanderCreature(true);
		reset = false;
	}

	
	
	/**
	 * Pre.
	 */
	public void pre() {
		OpenGL.begin(app);
		if(!mimodekReady){ //initialisation is done here instead of in the constructor, because the parent PApplet setup() is called twice.
			startMimodek();
			mimodekReady = true;
		}
		if(hd){
			try {
				createHighResolutionTiling(hdScale);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			hd = false;
		}
		if(movieRecorder.isRecording() && !movieRecorder.isPaused())
			takeSnapShot = true;
		/*
		if(takeSnapShot) //draw and export frame
		 
			draw(true);
			*/
	}
	

	/**
	 * Draw function called by the parent sketch.
	 */
	/*
	public void draw() {
		System.out.println(app.frameRate);
		if(!takeSnapShot)
			draw(true);
		else{ //the frame has been drawn already
			draw(true);
		}
	}
	*/
	
	
	/**
	 * Draw function called by the parent sketch.
	 * Draw Mimodek. Call OpenGL.begin before calling this method and call it only once per frame.
	 *
	 * @param toFbo If true, the frame is rendered in the fbo and optionally saved in file, else in the window framebuffer
	 */
	public void draw() { //boolean toFbo
			//if(toFbo){ //render in a FBO
				OpenGL.gl.glEnable(GL.GL_TEXTURE_2D);
				fbo.enable();
				
				OpenGL.gl.glPushMatrix();
				//OpenGL.gl.glTranslatef(5f, -15f, 0f);
				OpenGL.gl.glScalef((float)app.width/(float)FacadeFactory.getFacade().width,(float)app.height/(float)FacadeFactory.getFacade().height, 1f);
				/*
			}else{
				OpenGL.gl.glPushMatrix();
				//OpenGL.gl.glTranslatef(5f, -15f, 0f);
				OpenGL.gl.glTranslatef(FacadeFactory.getFacade().leftOffset,FacadeFactory.getFacade().topOffset, 0f);
				OpenGL.gl.glScalef(Configurator.getFloatSetting("GLOBAL_SCALING"),Configurator.getFloatSetting("GLOBAL_SCALING"), 1f);
			}
			*/
			OpenGL.gl.glClearColor(0.f, 0.f, 1.f, 1.f);
			OpenGL.gl.glClear(GL.GL_COLOR_BUFFER_BIT );
		
			
			//OpenGL.gl.glColor4f(0f, 0f, 0f, 1f);
			//OpenGL.rect(0,0,FacadeFactory.getFacade().width,FacadeFactory.getFacade().height);
			drawMimodek();
			OpenGL.gl.glPopMatrix();
		
		//OpenGL.background(0f, 0f, 1f, 1f);
		//if(toFbo){ //texture a quad with the content of the FBO
			fbo.disable();
			//OpenGL.deactivateFBO();
			//OpenGL.gl.glActiveTexture(GL.GL_TEXTURE0);
			OpenGL.gl.glBindTexture(GL.GL_TEXTURE_2D, fbo.getTextureId());
			OpenGL.outputError();
		
			//OpenGL.background(0.3f, 0.3f, 0.3f, 1f);
			OpenGL.gl.glEnable(GL.GL_TEXTURE_2D);
			if(takeSnapShot){
				 //OpenGL.gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, 0);
				 //now convert texture to an image
				 try {
						TextureIO.write(fbo.getTexture(), new File("image.png"));
					} catch (GLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				if(saveScreenShot || movieRecorder.isRecording()){
					new Thread(){
					 @Override
					public void run(){
						try {
							//don't erase the buffered image is the post function is enabled
							if(customWriter.writeToDisk(screenFolder.getAbsolutePath(),(movieRecorder.isRecording()?"movie_"+movieRecorder.nextFrame():"capture_"+screenShot_counter),"png",null, !Configurator.getBooleanSetting("POST_PICTURES_FLAG")) && !movieRecorder.isRecording())
								screenShot_counter++;
						} catch (ImageWriteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ImageReadException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					 }
					}.start();
				}
				
				takeSnapShot = false;
				saveScreenShot = false;
				Verbose.debug("SNAP!");
			}
			// use fbo's texture to texture quad
			OpenGL.color(0f, 0.0f, 0f,1f);
			//OpenGL.gl.glActiveTexture(GL.GL_TEXTURE0);
			
			
			OpenGL.gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_DECAL);
			//flip the quad because the texture is upside down
			OpenGL.gl.glTranslatef(FacadeFactory.getFacade().leftOffset,FacadeFactory.getFacade().topOffset, 0f);
			OpenGL.gl.glScalef(Configurator.getFloatSetting("GLOBAL_SCALING"),Configurator.getFloatSetting("GLOBAL_SCALING"), 1f);
			
			
			
			OpenGL.rect(0, FacadeFactory.getFacade().height, FacadeFactory.getFacade().width, -FacadeFactory.getFacade().height);
			
			OpenGL.outputError();
		//}
		
		OpenGL.end(app);	
	}
	
	/**
	 * Draw all Mimodek's objects.
	 * This is kept in a separate function for the sake of modularity.
	 */
	public void drawMimodek(){
		OpenGL.disableBlending();
		OpenGL.disableDepth();
		OpenGL.gl.glClearColor(0.f, 0.f, 1.f, 1.f);
		OpenGL.gl.glClear(GL.GL_COLOR_BUFFER_BIT );
		
		OpenGL.gl.glPushMatrix();
		
		OpenGL.gl.glColor4f(0f, 0f, 0f, 1f);
		OpenGL.rect(0,0,FacadeFactory.getFacade().width,FacadeFactory.getFacade().height);

		OpenGL.enableDepth();
		OpenGL.enableBlending(GL.GL_SRC_ALPHA, GL.GL_ONE);
		OpenGL.gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
		OpenGL.gl.glEnable(GL.GL_ALPHA_TEST);
		OpenGL.gl.glAlphaFunc(GL.GL_GREATER, 0.1f);
		
		
		if (showPheromone)
			scentMap.draw();
		
		// Draw B cells
		CellB.setOpenGlState();
		for (int i = 0; i < bCells.size(); i++)
			bCells.get(i).draw(app);
	
		for (int i = 0; i < growingCells.size(); i++) {
			if (growingCells.get(i) instanceof CellB)
				growingCells.get(i).draw(app);
		}
		CellB.unsetOpenGlState();
		
		// Draw A cells
		CellA.setOpenGlState();
		for (int i = 0; i < aCells.size(); i++)
			aCells.get(i).draw(app);
	
		
		for (int i = 0; i < growingCells.size(); i++) {
			if (growingCells.get(i) instanceof CellA)
				growingCells.get(i).draw(app);
		}
		CellA.unsetOpenGlState();
		
		// Draw food
		for (int f = 0; f < foods.size(); f++) {
			Food food = foods.get(f);
			food.draw(app);
		}
		
		Creature.setOpenGlState();
		for (int c = 0; c < creatures.size(); c++) {
			Creature cr = creatures.get(c);
			cr.draw(app);
		}
		Creature.unsetOpenGlState();
		
		OpenGL.gl.glDisable(GL.GL_ALPHA_TEST);
		
		OpenGL.gl.glPopMatrix();
	}
	
	/**
	 * Creates and export a high resolution tiling of the current frame.
	 * Don't call this method if you're showing Mimodek as it will pause the animation for some time.
	 *
	 * @param xTiles the number of tiles per rows
	 * @param yTiles the number of rows
	 * @throws Exception the exception
	 */
	public void createHighResolutionTiling(int scale) throws Exception{
			FBO tile = FBO.createFBO(FacadeFactory.getFacade().width,FacadeFactory.getFacade().height);
			Verbose.debug(tile);
			GL gl = OpenGL.gl;
			for(int i=0;i<scale;i++){
				for(int j=0;j<scale;j++){
					gl.glEnable(GL.GL_TEXTURE_2D);
					tile.enable();
					OpenGL.background(0.3f, 0.3f, 0.3f, 1f);
					//gl.glViewport(0, 0, FacadeFactory.getFacade().width*xTiles,FacadeFactory.getFacade().height*yTiles);

					gl.glPushMatrix();
					gl.glTranslatef(-i*app.width, -j*app.height, 0f);
					gl.glScalef(((float)app.width/(float)FacadeFactory.getFacade().width)*scale,((float)app.height/(float)FacadeFactory.getFacade().height)*scale, 1f);
					
					//gl.glTranslatef(-i*width, -j*height, 0f);
					//OpenGL.gl.glClearColor(19f/i, 0f,19f/j,1f);
					drawMimodek();
					gl.glPopMatrix();
					
					tile.disable();
					//if(i%2==0 || j%2==0){
							
						//OpenGL.deactivateFBO();
					OpenGL.gl.glBindTexture(GL.GL_TEXTURE_2D, tile.getTextureId());
					OpenGL.outputError();
				
					
					OpenGL.gl.glEnable(GL.GL_TEXTURE_2D);
						try {
							TextureIO.write(tile.getTexture(), new File("bogus.png"));
							customWriter.writeToDisk(tilesFolder.getAbsolutePath(),"tile_"+i+"_"+j,"png",null, true);
						} catch (GLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						OpenGL.outputError();
					//}
				}
			}
		tile.release();
		
		
	}

	/**
	 * Called after a frame has been drawn.
	 * No drawing allowed here!
	 */
	public void post() {
		if (update)
			update();
		if (reset) {
			initMimodek();
		}
	}
	
	/*
	 * Update the status, position, life, etc... of Mimodek's world
	 */
	/**
	 * Update.
	 */
	public void update() {
		if (autoFood /*&& app.frameCount % 3 == 0*/) {
			/*
			Food.dropFood(app.random(FacadeFactory.getFacade().width), app
					.random(FacadeFactory.getFacade().height));
			*/
			genetics.addFood();
		}

		ParameterControlWindow.setCounters(creatures.size(), aCells.size(),
				bCells.size());

		scentMap.update();

		// Update cells
		for (int i = 0; i < theCells.size(); i++)
			theCells.get(i).update(app);

		for (int i = 0; i < growingCells.size(); i++) {
			growingCells.get(i).update(app);
		}

		// Update/Draw food
		for (int f = 0; f < foods.size(); f++) {
			Food food = foods.get(f);
			food.z -= 0.0005 + Math.random() * 0.001;
			if (food.z <= 0) {
				foodAvg.sub(food);
				foods.remove(food);
				f--;
			}
		}

		// Update/Draw creatures
		for (int c = 0; c < creatures.size(); c++) {
			Creature cr = creatures.get(c);
			cr.update();
			if (cr.energy <= 0f) {
				creatures.remove(cr);
				Food.dropFood(cr.pos.x, cr.pos.y);
				c--;
			}
		}
	}


	/**
	 * Does some clean up on shutdown. This may or may not be called, so don't
	 * do anything vital here.
	 */
	public void dispose() {
		dataHandler.stop();
		tuioClient.disconnect();
		tuioClient.dispose();
		fileTransfer.stop();
	}

	/*
	 * Handle data incoming from the Tracking application
	 */
	/* (non-Javadoc)
	 * @see MimodekV2.tracking.TrackingListener#trackingEvent(MimodekV2.tracking.TrackingInfo)
	 */
	public void trackingEvent(TrackingInfo info) {
		if (info.type == TrackingInfo.UPDATE) {
			Food.dropFood(info.x + (-1f + (float) Math.random() * 2), info.y
					+ (-1f + (float) Math.random() * 2));
		}
	}

	/**
	 * Mouse dragged.
	 */
	public void mouseDragged() {
		Food.dropFood(FacadeFactory.getFacade().mouseX/Configurator.getFloatSetting("GLOBAL_SCALING") + (-1f + (float) Math.random() * 2),FacadeFactory.getFacade().mouseY/Configurator.getFloatSetting("GLOBAL_SCALING") + (-1f + (float) Math.random() * 2));
		// Food.dropFood(FacadeFactory.getFacade().mouseX,
		// FacadeFactory.getFacade().mouseY);
	}

	/**
	 * Key pressed.
	 */
	public void keyPressed() {
		if (app.key == 's') { //save settings in a new file
			Configurator.saveAs();
			
		}
		if (app.key == 'u') { //update current settings file (overwrite)
			Configurator.save();
		} 
		if (app.key == 'l') //load/update settings from xml file
			Configurator.reload();
		
		if (app.key == 'p') //show/hide pheromones
			showPheromone = !showPheromone;
		
		if (app.key == 'f') //feed the beast
			genetics.addFood();
		
		if (app.key == 'h') //high resolution screen shot
			hd = true;
		
		if (app.key == 'c') { //take a screen shot and saves it to the screen_shot folder
			if(!takeSnapShot){
				takeSnapShot = true;
				saveScreenShot = true;
			}
		}
		
	}
}
