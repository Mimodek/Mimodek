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

import javax.media.opengl.GL;

import MimodekV2.config.Configurator;
import MimodekV2.graphics.OpenGL;
import MimodekV2.graphics.TextureManager;

import MimodekV2.data.DataInterpolator;
import MimodekV2.data.PollutionDataInterpolator;
import MimodekV2.data.TemperatureColorRanges;
import mimodek.facade.FacadeFactory;

import processing.core.PApplet;
import processing.core.PVector;

public class CellB extends Cell {
	public static boolean usePollutionData = true;
	public static PollutionDataInterpolator pollutionInterpolator;
	public static DataInterpolator temperatureInterpolator;
	
	float nextOffsetAngle = 0;
	float currentAngle = 0;
	float distanceToAnchor;
	float currentBrightness = 1f;
	float nextOffsetBrightness = 0.5f;
	float distanceModifier=1f;
	
	float colorOffset;
	
	boolean moving = false;
	boolean eatable = false;
	
	Creature creatureA,creatureB;
	PVector dropMeAtA;
	PVector dropMeAtB;
	
	int color;
	
	
	public CellB(PVector pos,float distanceModifier) {
		super(pos);
		this.distanceModifier = distanceModifier;
		colorOffset = (float)Math.random();
		//radius = Mimodek.CELLB_RADIUS;
	}
	
	public void setEatable(){
		//moveable = true;
		//System.out.print("MAturity "+maturity);
		//System.out.print("Curent Maturity "+currentMaturity);
		//System.out.print("Set eatable? ");
		if(!eatable && currentMaturity>=1f && creatureA ==  null && creatureB == null){
			call2Creatures();
			//System.out.println("OK");
		}
	}
	
	
	
	@Override
	public float radius() {
		// TODO Auto-generated method stub
		return Configurator.getFloatSetting("CELLB_RADIUS");
	}

	void setAnchor(Cell anchor) {
		super.setAnchor(anchor);
		currentAngle = angleToAnchor;
		nextOffsetAngle = angleToAnchor;
		distanceToAnchor = anchor.pos.dist(pos);
	}
	
	void call2Creatures(){
		int count = 0;
		//one from the existing ones
		int indA = PApplet.round((float)Math.random()*(Mimodek.creatures.size()-1));
		while(Mimodek.creatures.get(indA).cellB!=null && count<50){
			count++;
			indA = PApplet.round((float)Math.random()*(Mimodek.creatures.size()-1));
		}
		if(count>=50)
			return;
		
		creatureA = Mimodek.creatures.get(indA);
		creatureA.cellB = this;
		
		//and an fresh new one
		creatureB = Creature.createCreature();
		creatureB.cellB = this;
	}
	
	void drop(){
		moving = false;
		eatable = true;
		pos = new PVector(creatureA.pos.x,creatureA.pos.y);
		setAnchor(new Cell(new PVector(creatureB.pos.x,creatureB.pos.y)));
		creatureA.cellB = null;
		creatureB.cellB = null;
		creatureA = null;
		creatureB = null;
		dropMeAtA = null;
		dropMeAtB = null;
	}
		
	PVector getCreatureTargetPosition(Creature c){
		if(creatureA == null || creatureB == null)
			return null;
		if(creatureA != null & creatureB != null && creatureA.readyToLift && creatureB.readyToLift){
			if(dropMeAtA==null && dropMeAtB==null){
				//find a place to dump the b cell far away from the center
				float a = 0f;
				float d = 0f;
				while(dropMeAtA==null || !FacadeFactory.getFacade().isInTheScreen(dropMeAtA)){
					d = FacadeFactory.getFacade().width<FacadeFactory.getFacade().height?FacadeFactory.getFacade().height/2f:FacadeFactory.getFacade().width/2f;
					a = (float)Math.random()*PApplet.TWO_PI;
					dropMeAtA = new PVector(FacadeFactory.getFacade().width/2f+d*PApplet.cos(a),FacadeFactory.getFacade().height/2f+d*PApplet.sin(a));
				}
				a = (float)Math.random()*PApplet.TWO_PI;
				d = getSize();
				dropMeAtB = new PVector(dropMeAtA.x+d*PApplet.cos(a),dropMeAtA.y+d*PApplet.sin(a));
			}
			moving = true;
			if(c == creatureA){
				return dropMeAtA;
			}else if(c == creatureB){
				return dropMeAtB;
			}else{
				return null;
			}
		}
		if(c == creatureA){ //creature A goes to the top
			return new PVector(anchor.pos.x + currentMaturity * (pos.x - anchor.pos.x), anchor.pos.y + currentMaturity * (pos.y - anchor.pos.y));
		}else if(c == creatureB){ //creature B goes at the bottom
			return new PVector(anchor.pos.x + 0.4f * (pos.x - anchor.pos.x), anchor.pos.y+ 0.4f * (pos.y - anchor.pos.y));
		}else{//we don't know who you are...
			return null;
		}
	}
	
	public float getSize(){
		float minDistanceToA = Configurator.getFloatSetting("CELLB_MIN_DISTANCE_TO_A");
		float maxDistanceToA = Configurator.getFloatSetting("CELLB_MAX_DISTANCE_TO_A")-minDistanceToA;
		return radius()+minDistanceToA+distanceModifier*maxDistanceToA;
	}
	
	@Override
	public void update(PApplet app){
		float minDistanceToA = Configurator.getFloatSetting("CELLB_MIN_DISTANCE_TO_A");
		float maxDistanceToA = Configurator.getFloatSetting("CELLB_MAX_DISTANCE_TO_A")-minDistanceToA;
		if(eatable){
			maturity-=0.0001f;
			if(maturity<=0f){
				Mimodek.bCells.remove(this);
				Mimodek.theCells.remove(this);
				return;
			}
		}else{
			
			if (PApplet.abs(currentMaturity-maturity)>0.01f){
				currentMaturity += currentMaturity<maturity?0.01f:-0.01f;
				currentMaturity = currentMaturity>maturity?maturity:currentMaturity;
			}else{
				currentMaturity = maturity;
			}
		}
		
		
		
		if(creatureA == null && creatureB == null && !eatable){
			for (int c = 0; c < Mimodek.creatures.size(); c++) {
				Creature cr = Mimodek.creatures.get(c);
				if (cr.pos.dist(pos) < 20) {
					
					if (PApplet.abs(currentAngle - nextOffsetAngle) < (PApplet.PI / 180.0f)) {
						nextOffsetAngle = (float) (angleToAnchor - PApplet.PI / 8.0 + (float) Math
								.random()
								* (PApplet.PI / 4.0));
					} else {
						currentAngle += nextOffsetAngle > currentAngle ? (PApplet.PI / 540.0f)
								: -(PApplet.PI / 540.0f);
					}
					break;
				}
			}
		}else if(moving){
			currentAngle = PApplet.atan2(creatureA.pos.y-anchor.pos.y,creatureA.pos.x-anchor.pos.x);
		}
		pos.x = anchor.pos.x + PApplet.cos(currentAngle) * (radius()+minDistanceToA+distanceModifier*maxDistanceToA);
		pos.y = anchor.pos.y + PApplet.sin(currentAngle) * (radius()+minDistanceToA+distanceModifier*maxDistanceToA);
		/*else if(){
			pos.x = anchor.pos.x + PApplet.cos(currentAngle) * (radius()+minDistanceToA+distanceModifier*maxDistanceToA);
			pos.y = anchor.pos.y + PApplet.sin(currentAngle) * (radius()+minDistanceToA+distanceModifier*maxDistanceToA);
		}*/
		
		if(!eatable){
			if(!usePollutionData){
				color = TemperatureColorRanges.getColor(TemperatureColorRanges.getHigherTemperature(temperatureInterpolator.getInterpolatedValue()));
			}else{
				color = TemperatureColorRanges.getColor(pollutionInterpolator.getInterpolatedValue());
			}
			if(PApplet.abs(nextOffsetBrightness-currentBrightness)<0.005f){
				nextOffsetBrightness = Configurator.getFloatSetting("CELLB_ALPHA")-(Configurator.getFloatSetting("CELLB_ALPHA")*(float)Math.random()*Configurator.getFloatSetting("CELLB_ALPHA_VARIATION"));
			}
			currentBrightness += nextOffsetBrightness>currentBrightness?0.005f:-0.005f;
		}else{
			currentBrightness = Configurator.getFloatSetting("CELLB_ALPHA")/2f;
			if(maturity<=0.5f){
				float step = 1f-(maturity/0.5f);
				currentBrightness = PApplet.lerp(currentBrightness,0f,step);
			}
		}
	}

	@Override
	public void draw(PApplet app) {
		if(creatureA !=null && creatureB !=null && creatureA.readyToLift && creatureB.readyToLift){
			zLevel = CellA.maxLevel;
		}else if(!eatable){
			zLevel = anchor.zLevel;
		}else{
			zLevel = 0;
		}
		
		PVector c;
		if(eatable){
			float step = 1f;
			if(maturity>0.5f){
				step = 1f-((maturity-0.5f)/0.5f);
			}
			c = new PVector(0.8f,0.8f,0.8f);
			int deadColor = app.color(0.8f);
			c = OpenGL.processingColorToVector(app,app.lerpColor(color,deadColor,step));
		}else{
			c = OpenGL.processingColorToVector(app,color);
			// c = OpenGL.processingColorToVector(app,TemperatureColorRanges.getColorInNormalized(PollutionLevelsEnum.getPollutionLevelForScore(pollutionInterpolator.getInterpolatedValue()).getColorRange()/10f+colorOffset));	
		}
		OpenGL.color(c,currentBrightness);
		//	OpenGL.color(Configurator.getFloatSetting("CELLB_R"), Configurator.getFloatSetting("CELLB_G"), Configurator.getFloatSetting("CELLB_B"), currentBrightness);
		
//		OpenGL.color(color, currentBrightness);
			
		if (currentMaturity > 0.4f){
			//OpenGL.pointSize(1f);
			OpenGL.disableTexture();
			if(creatureA !=null && creatureB !=null && creatureA.readyToLift && creatureB.readyToLift){
				OpenGL.line(
						anchor.pos.x + currentMaturity * (pos.x - anchor.pos.x),anchor.pos.y + currentMaturity * (pos.y - anchor.pos.y),
						anchor.pos.x,anchor.pos.y
						);
			}else if(eatable){
				OpenGL.line(
						anchor.pos.x + currentMaturity * (pos.x - anchor.pos.x),
						anchor.pos.y + currentMaturity * (pos.y - anchor.pos.y),zLevel,
						anchor.pos.x + (pos.x - anchor.pos.x), anchor.pos.y
								+  (pos.y - anchor.pos.y),zLevel);
			}else{
					OpenGL.line(
						anchor.pos.x + currentMaturity * (pos.x - anchor.pos.x),
						anchor.pos.y + currentMaturity * (pos.y - anchor.pos.y),zLevel,
						anchor.pos.x + 0.4f * (pos.x - anchor.pos.x), anchor.pos.y
								+ 0.4f * (pos.y - anchor.pos.y),zLevel);
			}
			OpenGL.enableTexture();
		}
		OpenGL.gl.glPushMatrix();
		float tX = anchor.pos.x + currentMaturity * (pos.x - anchor.pos.x);
		float tY = anchor.pos.y + currentMaturity * (pos.y - anchor.pos.y);
		/*
		OpenGL.gl.glTranslatef(-radius() * currentMaturity,-radius() * currentMaturity,0f);
		OpenGL.gl.glRotatef(PApplet.degrees(currentAngle),0.0f,0.0f,1.0f);
		OpenGL.gl.glTranslatef(radius() * currentMaturity,radius() * currentMaturity,0f);
		*/
		OpenGL.gl.glTranslatef(tX,tY,zLevel);
		OpenGL.gl.glRotatef(PApplet.degrees(PApplet.PI+currentAngle),0.0f,0.0f,1.0f);
		OpenGL.circleMultiTexture(0f, 0f, radius() * currentMaturity * 2.0f,radius() * currentMaturity * 2.0f);
		/*OpenGL.circleMultiTexture(0,0, radius()
				* currentMaturity * 2.0f, radius() * currentMaturity
				* 2.0f);*/
		/*
		OpenGL.circleMultiTexture(anchor.pos.x + currentMaturity * (pos.x - anchor.pos.x),
				anchor.pos.y + currentMaturity * (pos.y - anchor.pos.y), radius()
				* currentMaturity * 2.0f, radius() * currentMaturity
				* 2.0f);
		*/
		OpenGL.gl.glPopMatrix();
		/*
		OpenGL.circle(anchor.pos.x + currentMaturity * (pos.x - anchor.pos.x),
				anchor.pos.y + currentMaturity * (pos.y - anchor.pos.y), radius()
						* currentMaturity * 2.0f, radius() * currentMaturity
						* 2.0f, Mimodek.CELLB_INSIDE_TEXTURE);
		*/
	}

	public static CellB addCellB(PApplet app) {
		float cellA_radius = Configurator.getFloatSetting("CELLA_RADIUS");
		float cellB_radius = Configurator.getFloatSetting("CELLB_RADIUS");
		float minDistanceToA = Configurator.getFloatSetting("CELLB_MIN_DISTANCE_TO_A");
		float maxDistanceToA = Configurator.getFloatSetting("CELLB_MAX_DISTANCE_TO_A")-minDistanceToA;
		
		CellB added = null;
		int counter = 0;

		CellA anchor = Mimodek.aCells.get(PApplet.round((float) Math
				.random()
				* (Mimodek.aCells.size() - 1)));
		float a = (float) Math.random() * (PApplet.TWO_PI);
		float distanceModifier = (float) Math.random();
		//(20);
		PVector pos = new PVector(anchor.pos.x + PApplet.cos(a)
				* (cellA_radius + cellB_radius+minDistanceToA+distanceModifier*maxDistanceToA), anchor.pos.y + PApplet.sin(a)
				* (cellA_radius + cellB_radius+minDistanceToA+distanceModifier*maxDistanceToA));
		boolean addIt = false;
		while (!addIt && counter < 50) {
			if (!FacadeFactory.getFacade().isInTheScreen(pos, cellB_radius)) {
				anchor = Mimodek.aCells.get(PApplet.round((float) Math
						.random()
						* (Mimodek.aCells.size() - 1)));
				a = (float) Math.random() * (PApplet.TWO_PI);
				distanceModifier = (float) Math.random();
				pos = new PVector(anchor.pos.x + PApplet.cos(a)
						* (cellA_radius +  cellB_radius+minDistanceToA+distanceModifier*maxDistanceToA), anchor.pos.y
						+ PApplet.sin(a) * (cellA_radius +  cellB_radius+minDistanceToA+distanceModifier*maxDistanceToA));
				continue;
			}
			addIt = true;

			for (int i = 0; i < Mimodek.theCells.size(); i++) {
				Cell toTest = Mimodek.theCells.get(i);
				if (toTest.pos.dist(pos) < (cellB_radius + toTest.radius())
						+ ((toTest instanceof CellB) ? 0 : minDistanceToA)) { // 3:7
					// println((cellA_radius+toTest.radius));
					addIt = false;
					break;
				}
			}
			if (addIt) {
				added = new CellB(pos,distanceModifier);
				added.setAnchor(anchor);
			} else {
				anchor = Mimodek.aCells.get(PApplet.round((float) Math
						.random()
						* (Mimodek.aCells.size() - 1)));
				a = (float) Math.random() * (PApplet.TWO_PI);
				distanceModifier = (float) Math.random();
				pos = new PVector(anchor.pos.x + PApplet.cos(a)
						* (cellA_radius +  cellB_radius+minDistanceToA+distanceModifier*maxDistanceToA), anchor.pos.y
						+ PApplet.sin(a) * (cellA_radius +  cellB_radius+minDistanceToA+distanceModifier*maxDistanceToA));
			}
			counter++;
		}
		if (counter >= 50) {
			return null;
		}
		// println(added.pos);
		return added;
	}
	
	public static void setOpenGlState(){
		//OpenGL.multiTexture(OpenGL.CIRCLE_TEXTURE, Configurator.getIntegerSetting("CELLB_TEXTURE"));
		OpenGL.multiTexture(TextureManager.getTextureIndex(Configurator.getStringSetting("CELLB_MASK")), TextureManager.getTextureIndex(Configurator.getStringSetting("CELLB_TEXTURE")));
	}
	
	public static void unsetOpenGlState(){
		GL gl = OpenGL.gl;
		// diable point sprites
		//gl.glTexEnvi(GL.GL_POINT_SPRITE, GL.GL_COORD_REPLACE, GL.GL_FALSE);
		OpenGL.disableTexture();
		gl.glDisable(GL.GL_POINT_SPRITE);
		// default
		//gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);

		
		// gl.glDisable(GL.GL_TEXTURE_2D);
		// gl.glDisable(GL.GL_BLEND);
	}
	
	
	public static CellB getEatableCell(){
		for(int i=0;i<Mimodek.bCells.size();i++){
			if(Mimodek.bCells.get(i).eatable)
				return Mimodek.bCells.get(i);
		}
		return null;
	}

	

}
