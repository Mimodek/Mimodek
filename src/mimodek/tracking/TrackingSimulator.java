package mimodek.tracking;

import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.Hashtable;

import mimodek.facade.Facade;
import mimodek.facade.FacadeFactory;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

/**
 * A simple tracking simulator for development and testing purpose.
 * It runs as a thread.
 * Double click to create/remove a tracking object (a little square).
 * Click and drag to move squares around.
 * Click on a square to make it inactive (empty square), moving it won't produce tracking info.
 * Click again on an empty square to make it active.
 * Move the squares in and out the tracking area to create tracking data.
 * See MimodekSketch in the example folder for more info.
 * @author Jonsku
 */
public class TrackingSimulator extends Thread implements Tracker{
	class Particle{
		PVector vel;
		PVector pos;
		boolean active = true;
		boolean out = true;
		boolean toRemove = false;
		int oldKey = -1;
		
		public Particle(PVector pos) {
			this.pos = pos;
			this.vel = new PVector(0, 0);
		}
		
		public void changeState(){
			active = !active;
		}
		
		public void draw(PApplet app){
			app.pushStyle();
			app.rectMode(PConstants.CENTER);
			if(active){
				app.noStroke();
				app.fill(out?125:255);
			}else{
				app.noFill();
				app.stroke(out?125:255);
			}
			app.rect(pos.x, pos.y, 10, 10);
			app.popStyle();
		}
		
		
	}
	
	public boolean running = true;
	
	PApplet app;
	Facade facade;
	TrackingListener listener;
	int dragged = -1;
	int counter = 0;
	
	int top = 0;
	int left = 0;


	Hashtable<Integer,Particle> particles;

	public TrackingSimulator(PApplet app, int left,int top) {
		super();
		this.top = top;
		this.left = left;
		this.app = app;
		//app.registerDraw(this);
		app.registerMouseEvent(this);
		app.registerDispose(this);
		facade = FacadeFactory.getFacade();
		particles = new Hashtable<Integer,Particle>();
		TrackingInfo.FLIP_HORIZONTAL = false;
	}
	
	public void dispose(){
		running = false;
	}
	
	@Override
	public void run() {
		while (running) {
			
			if (listener == null)
				return;
			
			Enumeration<Integer> e = particles.keys();
			while (e.hasMoreElements()) {
				int i = e.nextElement();
				Particle p = particles.get(i);
				if(p!=null && p.active && !p.out)
					listener.trackingEvent(createTrackingInfo(TrackingInfo.UPDATE,i));
				else if(p!=null && p.active && p.toRemove){
					listener.trackingEvent(createTrackingInfo(TrackingInfo.REMOVE,p.oldKey));
					p.toRemove = false;
					particles.remove(p.oldKey);
				}
			}
		}
	}
	
	public synchronized void draw(){
		
		app.pushStyle();
		//draw the tracking area
		app.noFill();
		app.stroke(255);
		app.rectMode(PConstants.CORNER);
		app.rect(left, top, facade.width, facade.height);
		Enumeration<Integer> e = particles.keys();
		while (e.hasMoreElements()) {
			int i = e.nextElement();
			particles.get(i).draw(app);
		}
		app.popStyle();
		
	}
	
	private boolean isInTrackingArea(float x, float y){
		return x>=left && x<=left+facade.width && y>=top && y<=top+facade.height;
	}
	
	private TrackingInfo createTrackingInfo(int infoType,int key){
		Particle p = particles.get(key);
		return new TrackingInfo(infoType,key,p.pos.x-left,p.pos.y-top);
	}
	
	private int findParticleAtPos(int x, int y){
		Enumeration<Integer> e = particles.keys();
		while (e.hasMoreElements()) {
			int i = e.nextElement();
			Particle p = particles.get(i);
			if(PApplet.dist(p.pos.x,p.pos.y,x,y)<5)
				return i;
		}
		return -1;
	}
	
	/**
	 * Mouse event handler
	 * @param event
	 */
	public synchronized void mouseEvent(MouseEvent event) {
		if(event.getClickCount()==2){
			doubleClick();
			return;
		}
		switch (event.getID()) {
		case MouseEvent.MOUSE_PRESSED:
			break;
		case MouseEvent.MOUSE_RELEASED:
			dragged = -1;
			break;
		case MouseEvent.MOUSE_CLICKED:
			mouseClicked();
			break;
		case MouseEvent.MOUSE_DRAGGED:
			mouseDragged();
			break;
		case MouseEvent.MOUSE_MOVED:
			break;
		}
	}
	
	private void doubleClick() {
		int key = findParticleAtPos(app.mouseX,app.mouseY);
		if(key<0){
			particles.put(counter++,new Particle(new PVector(app.mouseX,app.mouseY)));
			if(isInTrackingArea(app.mouseX,app.mouseY) && particles.containsKey(particles.size()-1))
				particles.get(particles.size()-1).out = false;
		}else{
			Particle p = particles.get(key);
			if(isInTrackingArea(p.pos.x,p.pos.y) && listener != null){
				listener.trackingEvent(createTrackingInfo(TrackingInfo.REMOVE,key));
			}
			particles.remove(key);
		}
	}

	private void mouseClicked() {
		int key = findParticleAtPos(app.mouseX,app.mouseY);
		if(key<0)
			return;
		particles.get(key).changeState();
	}

	private void mouseDragged() {
		if(dragged < 0){
			int key = findParticleAtPos(app.mouseX,app.mouseY);
			if(key<0)
				return;
			dragged = key;
		}
		particles.get(dragged).pos = new PVector(app.mouseX,app.mouseY);
		if(!isInTrackingArea(app.mouseX,app.mouseY)){
			if(!particles.get(dragged).out && listener != null){
				particles.get(dragged).oldKey = dragged;
				particles.get(dragged).toRemove = true;
				particles.put(counter,particles.get(dragged));
				
				dragged = counter++;
			}
			particles.get(dragged).out = true;
		}else{
			particles.get(dragged).out = false;
		}
	}

	public void setListener(TrackingListener listener) {
		this.listener = listener;
	}
}
