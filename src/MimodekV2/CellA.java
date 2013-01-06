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
import MimodekV2.data.TemperatureColorRanges;
import mimodek.facade.FacadeFactory;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

// TODO: Auto-generated Javadoc
/**
 * The Class CellA.
 */
public class CellA extends Cell {
	
	/** The level. */
	int level = 0;
	
	/** The max level. */
	public static int maxLevel = 0;
	
	/** The maxz level. */
	public static float maxzLevel = 0f;
	
	/** The minz level. */
	public static float minzLevel = 0f;
	
	/** The radius modifier. */
	float radiusModifier = 1f;
	
	/** The angular movement. */
	float angularMovement;
	
	/** The aa. */
	float aa;
	
	/** The z level slope. */
	float zLevelSlope = 1f;
	
	//CellA newCell;
	
	
	/** The temperature interpolator. */
	public static DataInterpolator temperatureInterpolator;
	//float distanceModifier = 1f;

	/** The humidity interpolator. */
	public static DataInterpolator humidityInterpolator;
	
	/**
	 * Instantiates a new cell a.
	 *
	 * @param pos the pos
	 * @param color the color
	 */
	public CellA(PVector pos, PVector color) {
		super(pos);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Instantiates a new cell a.
	 *
	 * @param pos the pos
	 * @param radius the radius
	 */
	public CellA(PVector pos, float radius) {
		this(pos, new PVector((float) Math.random(), (float) Math.random(),
				(float) Math.random()));
		this.radiusModifier = radius;
		// TODO Auto-generated constructor stub
	}
	/*
	public void createNewCell(CellA newCell){
		this.newCell = newCell;
	}
	*/
	
	/* (non-Javadoc)
	 * @see MimodekV2.Cell#setAnchor(MimodekV2.Cell)
	 */
	@Override
	void setAnchor(Cell anchor){
		super.setAnchor(anchor);
		level = ((CellA)anchor).level+1;
		zLevel = anchor.zLevel+((CellA)anchor).zLevelSlope;
		maxzLevel = PApplet.max(maxzLevel,zLevel);
		minzLevel = PApplet.min(minzLevel,zLevel);
		zLevelSlope = ((CellA)anchor).zLevelSlope;
		((CellA)anchor).zLevelSlope*=-1f;
		maxLevel = PApplet.max(level,maxLevel);
		angularMovement = (float) (Math.random() * (PConstants.HALF_PI / 10f));
		angularMovement *= Math.random() > 0.5 ? 1f : -1f;
	}
	
	
	/* (non-Javadoc)
	 * @see MimodekV2.Cell#radius()
	 */
	@Override
	public float radius() {
		// TODO Auto-generated method stub
		return Configurator.getFloatSetting("CELLA_RADIUS")*radiusModifier;
	}
	
	/* (non-Javadoc)
	 * @see MimodekV2.Cell#update(processing.core.PApplet)
	 */
	@Override
	public void update(PApplet app) {
		if(anchor!=null){
			//apply angular movement
			aa = angleToAnchor + angularMovement* PApplet.sin(PApplet.radians(app.frameCount + level));
			float distanceBetween = 0.42f+(humidityInterpolator.getInterpolatedValue()/100f)*(0.75f-0.42f);
			pos.x = anchor.pos.x + (radius()+anchor.radius())* distanceBetween * PApplet.cos(aa);
			pos.y = anchor.pos.y + (radius()+anchor.radius())* distanceBetween * PApplet.sin(aa);
		}
		/*
		 * See the big chung of code commented in draw()....
		 */
		/*
		//if a new cell is being created, update it as well
		if(newCell !=null){
			newCell.update(app);
			if(newCell.currentMaturity >= 1f){
				//the new cell has reached maturity and can be detached from its parent
				Mimodek.aCells.add(newCell);
				Mimodek.theCells.add(newCell);
				newCell = null;
			}
		}
		*/
		if (currentMaturity < maturity)
			currentMaturity += 0.01;
	}

	/* (non-Javadoc)
	 * @see MimodekV2.Cell#draw(processing.core.PApplet)
	 */
	@Override
	public void draw(PApplet app) {
		float distortion = Configurator.getFloatSetting("CELLA_DISTORTION");
		

		float sX, sY;
		GL gl = OpenGL.gl;

		int frameCount = app.frameCount;
		
		float brightness = 1f;// PApplet.map(zLevel, minzLevel, maxzLevel,0.2f,1f);
		PVector c = OpenGL.processingColorToVector(app,TemperatureColorRanges.getColor(temperatureInterpolator.getInterpolatedValue(PApplet.lerp(1f,0f,(float)level/(float)maxLevel))));
		c.mult(brightness);
		OpenGL.color(c,Configurator.getFloatSetting("CELLA_ALPHA"));
		
		  
		gl.glPushMatrix();
		if (anchor != null) {
			gl.glTranslatef(anchor.pos.x + currentMaturity
					* (pos.x - anchor.pos.x), anchor.pos.y + currentMaturity
					* (pos.y - anchor.pos.y), zLevel);
		} else {
			gl.glTranslatef(pos.x, pos.y, zLevel);
		}
		//gl.glTranslatef(0.5f,0.5f,0.0f);
		 gl.glRotatef(PApplet.degrees(aa),0.0f,0.0f,1.0f);
		 //gl.glTranslatef(-0.5f,-0.5f,0.0f);
		
		gl.glScalef(currentMaturity*radius(), currentMaturity*radius(), 1f);

		/*
		 * Here I was trying to animate some king of smoother separation of a new cell to a mother cell.
		 * The idea was to make the new circle emerge from the existing circle. The main problem is to make a nice texture mapping...
		 */
//		/*
//		if(newCell != null && newCell.currentMaturity>0){ //we don't want to divide by zero
//			//animate separation
//			//we work in a unit circle where the cell's radius is a unit and its position is the center (scaling takes care of the size)
//			float radius = currentMaturity*radius();
//			
//			//convert the new cell's radius
//			float newCellRadius = newCell.currentMaturity*newCell.radius();
//			newCellRadius /= radius;
//			
//			//now for the coordinates
//			float angleBetweenCells = newCell.aa;// PApplet.atan2(pos.y-newCell.pos.y,pos.x-newCell.pos.x);
//			float d = PApplet.dist(pos.x,pos.y,newCell.pos.x,newCell.pos.y); //the distance between the 2 cells
//			d/= radius;
//			
//			PVector p1 = new PVector(PApplet.cos(angleBetweenCells)*d,PApplet.sin(angleBetweenCells)*d);
//			//radius = 1f;
//			
//			
//			//first perform a test to see if we need to bother at all
//			//1:println("one circle is contained within the other.");  
//			//2:println("the circles are coincident.");<-- this will probably never happen.
//			if(!(d<newCellRadius) && !(d == 0 && newCellRadius == 1f)){
//				
//			
//			//let's transform the coordinates once more so the mother cell is in the unit circle with center 0.5
//		/*
//				radius = 0.5f;
//			newCellRadius /= 2f;
//			d /= 2f;
//			p1.x = (p1.x+1f)*(1f+newCellRadius)/2f;
//			p1.y = (p1.y+1f)*(1f+newCellRadius)/2f;
//			*/
//			//System.out.println(p1);
//			
//			//At this stage, we know they are 2 and only 2 intersection points	
//			//let's calculate their coordinates
//			//a = (r02 - r12 + d2 ) / (2 d) 
//			float a = (1 - newCellRadius*newCellRadius + d*d) / (2*d);
//
//			//h2 = r02 - a2
//			float h = PApplet.sqrt(1 - a*a);
//
//			//P2 = P0 + a ( P1 - P0 ) / d 
//			
//			PVector p2 = new PVector((a*p1.x)/d, (a*p1.y)/d);
//			
//			//this is the center of the triangle fan
//			gl.glBegin(GL.GL_TRIANGLE_FAN);
//			
//			//center point of the traingle fan
//			gl.glMultiTexCoord2f(GL.GL_TEXTURE0, 1f, 0.5f);
//			gl.glMultiTexCoord2f(GL.GL_TEXTURE1, 1f, 0.5f);
//			gl.glVertex2f(p2.x, p2.y);
//			
//
//			//x3 = x2 +- h ( y1 - y0 ) / d
//			//y3 = y2 -+ h ( x1 - x0 ) / d 
//			PVector p3 = new PVector(p2.x-(-h*p1.y)/d, p2.y-(+h*p1.x/d));
//			h = -h;
//			PVector p4 = new PVector(p2.x-(-h*p1.y)/d, p2.y-(+h*p1.x)/d);
//			
//			
//
//			//now we calculate the key points four our shape
//
//
//			float endAngle = PApplet.atan2(p3.y,p3.x);
//			if(endAngle<0)
//			  endAngle += PApplet.TWO_PI;
//			 else
//			  endAngle = endAngle%PApplet.TWO_PI;
//
//			float endAngle1 = PApplet.atan2(p4.y-p1.y,p4.x-p1.x); 
//
//
//			if(endAngle1<0)
//			  endAngle1 += PApplet.TWO_PI;
//			 else
//			   endAngle1 =  endAngle1%PApplet.TWO_PI;
//			  
//			float startAngle = PApplet.atan2(p4.y,p4.x);
//			if(startAngle<0)
//			  startAngle += PApplet.TWO_PI;
//			  else
//			   startAngle=  startAngle%PApplet.TWO_PI;
//
//			float startAngle1 = PApplet.atan2(p3.y-p1.y,p3.x-p1.x);   
//			if(startAngle1<0)
//			  startAngle1 += PApplet.TWO_PI;
//			  else
//			  startAngle1 = startAngle1%PApplet.TWO_PI;
//			
//			/*
//			float startAngle = PApplet.atan2(p3.y,p3.x);
//			if(startAngle<0)
//				startAngle += PApplet.TWO_PI;
//			 else
//				 startAngle = startAngle%PApplet.TWO_PI;
//
//			float startAngle1 = PApplet.atan2(p4.y-p1.y,p4.x-p1.x); 
//
//
//			if(startAngle1<0)
//				startAngle1 += PApplet.TWO_PI;
//			 else
//				 startAngle1 =  startAngle1%PApplet.TWO_PI;
//			  
//			float endAngle = PApplet.atan2(p4.y,p4.x);
//			if(endAngle<0)
//			  endAngle += PApplet.TWO_PI;
//			  else
//			   endAngle=  endAngle%PApplet.TWO_PI;
//
//			float endAngle1 = PApplet.atan2(p3.y-p1.y,p3.x-p1.x);   
//			if(endAngle1<0)
//			  endAngle1 += PApplet.TWO_PI;
//			  else
//			  endAngle1 = endAngle1%PApplet.TWO_PI;
//			*/
//		
//			  //swap angles order if needed
//			
//			  if(startAngle>endAngle){
//			    endAngle += PApplet.TWO_PI;
//			  }
//			  
//			  if(startAngle1>endAngle1){
//			    endAngle1 += PApplet.TWO_PI;
//			  }
//			
//			 //This is to produce the desired number of vertices
//			//float step0 = (endAngle-startAngle)/Mimodek.CELLA_VERTEX_NUMBER;
//			float step0 = (endAngle-startAngle)/Mimodek.CELLA_VERTEX_NUMBER;
//			float step1 = (endAngle1-startAngle1)/Mimodek.CELLA_VERTEX_NUMBER;
//			float x, y = 0; //for the shape
//			float s, t = 0; //for the texture
//			//float size = 
//			//float textureRadius = 1f/(1f+newCellRadius);
//			//System.out.println(textureRadius);
//			
//			//OpenGL.rectMultiTexture(-1f, -1f, 1f+d+newCellRadius, 1f+d+newCellRadius);
//			//startAngle = 0f;
//			//endAngle = PApplet.TWO_PI;
//			//now draw the triangles
//			//OpenGL.gl.glBegin(GL.GL_POINTS);                // Select points as the primitive 
//			//glVertex3f(0.0f, 0.0f, 0.0f);        // Specify a point 
//			//glVertex3f(50.0f, 50.0f, 50.0f);     // Specify another point 
//			//glEnd(); 
//			float n = 0;
//			float n0 = app.noise(frameCount * 0.01f, PApplet.cos(startAngle), PApplet.sin(startAngle))* distortion;
//			x = PApplet.cos(startAngle);
//		    y = PApplet.sin(startAngle);
//		    s = (x+ 1.0f) * 0.5f;
//			t = (y+ 1.0f) * 0.5f;
//
//			gl.glMultiTexCoord2f(GL.GL_TEXTURE0, s, t);
//			gl.glMultiTexCoord2f(GL.GL_TEXTURE1, s, t);
//			//System.out.println(i+": "+x+","+y);
//			gl.glVertex2f(x* (1 - n0),y*(1 - n0));
//			
//			  for(int i=1;i<=Mimodek.CELLA_VERTEX_NUMBER;i++){
//				  //System.out.println(startAngle+": "+endAngle);
//				  //System.out.println("Current: "+(startAngle+i*step0)+"/"+endAngle);
//			  if(startAngle+i*step0<endAngle){
//			  //if(endAngle-(i*step0)>startAngle){
//				  //mother cell
//				n = app.noise(frameCount * 0.01f, pos.x + startAngle+i*step0) * distortion;
//			    x = PApplet.cos(startAngle+i*step0);
//			    y = PApplet.sin(startAngle+i*step0);
//			    s = 0.5f+(x* 0.5f);
//			    t = 0.5f+(y* 0.5f);
//	
//				gl.glMultiTexCoord2f(GL.GL_TEXTURE0, s, t);
//				gl.glMultiTexCoord2f(GL.GL_TEXTURE1, s, t);
//				//System.out.println(i+": "+x+","+y);
//				gl.glVertex2f(x* (1 - n),y* (1 - n));
//			  }else{
//				  //System.out.println("This shouldn't happen");
//				  //new cell
//			    for(int j=0;j<Mimodek.CELLA_VERTEX_NUMBER;j++){	
//			    	n = app.noise(frameCount * 0.01f, newCell.pos.x + startAngle1+(j*step1)) * distortion;
//			    	x = PApplet.cos(startAngle1+(j*step1));
//				    y = PApplet.sin(startAngle1+(j*step1));
//				    
//				    s = 0.5f+(x* 0.5f);
//				    t = 0.5f+(y* 0.5f);
//					
//					gl.glMultiTexCoord2f(GL.GL_TEXTURE0, s, t);
//					gl.glMultiTexCoord2f(GL.GL_TEXTURE1, s, t);
//					 x *= newCellRadius;
//					 x += p1.x;
//				     y += p1.y;
//					//System.out.println("n: "+j+": "+x+","+y);
//					gl.glVertex2f(x* (1 - n),y* (1 - n));
//			    }
//			    //loop the loop
//			    x = PApplet.cos(startAngle);
//			    y = PApplet.sin(startAngle);
//			    s = (x+ 1.0f) * 0.5f;
//				t = (y+ 1.0f) * 0.5f;
//	
//				gl.glMultiTexCoord2f(GL.GL_TEXTURE0, s, t);
//				gl.glMultiTexCoord2f(GL.GL_TEXTURE1, s, t);
//				//System.out.println(i+": "+x+","+y);
//				gl.glVertex2f(x* (1 - n0),y* (1 - n0));
//			  }
//			  
//			}
//			 // System.out.println("Done drawing");
//			gl.glEnd();
//			
//			//we're are done!
//			gl.glPopMatrix();
//			return;
//			}
//		}
//*/
	gl.glBegin(GL.GL_TRIANGLE_FAN);
	
	//center point of the traingle fan
	gl.glMultiTexCoord2f(GL.GL_TEXTURE0, 0.5f, 0.5f);
	gl.glMultiTexCoord2f(GL.GL_TEXTURE1, 0.5f, 0.5f);
		gl.glVertex2f(0f, 0f);
			
	
			float n0 = app
					.noise(frameCount * 0.01f, PApplet.cos(0), PApplet.sin(0))
					* distortion;
			sX = (PApplet.cos(0) + 1.0f) * 0.5f;
			sY = (PApplet.sin(0) + 1.0f) * 0.5f;
	
			gl.glMultiTexCoord2f(GL.GL_TEXTURE0, sX, sY);
			gl.glMultiTexCoord2f(GL.GL_TEXTURE1, sX, sY);
			gl.glVertex2f(PApplet.cos(0) * (1 - n0), PApplet.sin(0) * (1 - n0));
			for (float a = PConstants.TWO_PI / Mimodek.CELLA_VERTEX_NUMBER; a < PConstants.TWO_PI; a += PConstants.TWO_PI
					/ (Mimodek.CELLA_VERTEX_NUMBER + 1)) {
				float n = app.noise(frameCount * 0.01f, pos.x + a) * distortion;
				sX = (PApplet.cos(a) + 1.0f) * 0.5f;
				sY = (PApplet.sin(a) + 1.0f) * 0.5f;
	
				gl.glMultiTexCoord2f(GL.GL_TEXTURE0, sX, sY);
				gl.glMultiTexCoord2f(GL.GL_TEXTURE1, sX, sY);
				gl.glVertex2f(PApplet.cos(a) * (1 - n), PApplet.sin(a) * (1 - n));
			}
	
			sX = (PApplet.cos(0) + 1.0f) * 0.5f;
			sY = (PApplet.sin(0) + 1.0f) * 0.5f;
			gl.glMultiTexCoord2f(GL.GL_TEXTURE0, sX, sY);
			gl.glMultiTexCoord2f(GL.GL_TEXTURE1, sX, sY);
			gl.glVertex2f(PApplet.cos(0) * (1 - n0), PApplet.sin(0) * (1 - n0));
			gl.glEnd();
		//gl.glDisable(GL.GL_TEXTURE_2D);
		gl.glPopMatrix();
	}

	/**
	 * Adds the cell a.
	 *
	 * @param app the app
	 * @return the cell a
	 */
	public static CellA addCellA(PApplet app) {
		float dTo = 0.75f;//0.42f+(humidityInterpolator.getInterpolatedValue()/100f)*(0.75f-0.42f);
		//float dTo = Configurator.getFloatSetting("CELLA_DISTANCE_BETWEEN");
		float radius = Configurator.getFloatSetting("CELLA_RADIUS");
		Cell root = Mimodek.aCells.get(0);
		float maxD = root.pos.dist(new PVector(0, 0));
		
		int counter = 0;

		CellA added = null;
		//System.out.println("Pick a random aCell:"+PApplet.round((float)Math.random() * (Mimodek.aCells.size() - 1)));
		CellA anchor = Mimodek.aCells.get(PApplet.round((float)Math.random() * (Mimodek.aCells.size() - 1)));
		float a = (float) Math.random() * PConstants.TWO_PI;
		PVector pos = new PVector(anchor.pos.x + PApplet.cos(a) * anchor.radius()
				* dTo, anchor.pos.y + PApplet.sin(a) * anchor.radius() * dTo);
		boolean addIt = false;
		
		float radiusModifier = 1f;
		if (Mimodek.aCells.size() > 0) {

			radiusModifier = (maxD - pos.dist(root.pos)) / maxD;
			pos = new PVector(anchor.pos.x + PApplet.cos(a)
					* (anchor.radius() + radius*radiusModifier)* dTo, anchor.pos.y + PApplet.sin(a)
					* (anchor.radius() + radius*radiusModifier)* dTo);
		}
		while (!addIt && counter < Configurator.getIntegerSetting("CELLA_MAX_TRY_INT")) {
			if (!FacadeFactory.getFacade().isInTheScreen(pos, radius*radiusModifier)) {
				anchor = Mimodek.aCells.get(PApplet.round((float)Math.random() * (Mimodek.aCells.size() - 1)));
				a = (float) Math.random() * PConstants.TWO_PI;
				pos = new PVector(anchor.pos.x + PApplet.cos(a) * anchor.radius()
						* dTo, anchor.pos.y + PApplet.sin(a) * anchor.radius()
						* dTo);
				//radius = Mimodek.CELLA_RADIUS;
				if (Mimodek.aCells.size() > 0) {

					radiusModifier = (maxD - pos.dist(root.pos)) / maxD;
					pos = new PVector(anchor.pos.x + PApplet.cos(a)
							* (anchor.radius() + radius*radiusModifier), anchor.pos.y
							+ PApplet.sin(a) * (anchor.radius() + radius*radiusModifier));
				}
				continue;
			}
			addIt = true;

			for (int i = 0; i < Mimodek.theCells.size(); i++) {
				Cell toTest = Mimodek.theCells.get(i);
				if (toTest == anchor /*|| (toTest instanceof CellB && ((CellB)toTest).eatable)*/)
					continue;
				if (toTest.pos.dist(pos) < (radius*radiusModifier + toTest.radius()*dTo )) {
					//System.out.println((radius*radiusModifier+toTest.radius()*dTo));
					addIt = false;
					break;
				}
			}
			if (addIt) {
				added = new CellA(pos, radiusModifier);
				added.setAnchor(anchor);
				//TODO:Remove if new animation doesn't work
				//anchor.createNewCell(added);
			} else {
				anchor = Mimodek.aCells.get(PApplet.round((float)Math.random() * (Mimodek.aCells.size() - 1)));
				a = (float) Math.random() * PConstants.TWO_PI;
				pos = new PVector(anchor.pos.x + PApplet.cos(a) * anchor.radius()
						* dTo, anchor.pos.y + PApplet.sin(a) * anchor.radius()
						* dTo);
				//radius = Mimodek.CELLA_RADIUS;
				if (Mimodek.aCells.size() > 0) {

					radiusModifier = (maxD - pos.dist(root.pos)) / maxD;
					pos = new PVector(anchor.pos.x + PApplet.cos(a)
							* (anchor.radius() + radius*radiusModifier)*dTo, anchor.pos.y
							+ PApplet.sin(a) * (anchor.radius() + radius*radiusModifier)*dTo);
				}
			}
			counter++;
		}
		if (counter >= Configurator.getIntegerSetting("CELLA_MAX_TRY_INT")) {
			return null;
		}
		// println(added.pos);
		return added;
	}

	/**
	 * Sets the open gl state.
	 */
	public static void setOpenGlState() {
		//OpenGL.multiTexture(OpenGL.CIRCLE_TEXTURE, Configurator.getIntegerSetting("CELLA_TEXTURE"));
		OpenGL.multiTexture(TextureManager.getTextureIndex(Configurator.getStringSetting("CELLA_MASK_STR")),TextureManager.getTextureIndex(Configurator.getStringSetting("CELLA_TEXTURE_STR")));
	}

	/**
	 * Unset open gl state.
	 */
	public static void unsetOpenGlState() {
		//GL gl = OpenGL.gl;
		OpenGL.disableTexture(1);
		/*
		gl.glActiveTexture(GL.GL_TEXTURE0);
		gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
		gl.glDisable(GL.GL_TEXTURE_2D);
		gl.glActiveTexture(GL.GL_TEXTURE1);
		gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
		gl.glDisable(GL.GL_TEXTURE_2D);
		*/
	}



	

}
