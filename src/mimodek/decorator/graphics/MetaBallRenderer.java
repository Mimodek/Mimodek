package mimodek.decorator.graphics;

import java.util.ArrayList;

import mimodek.facade.FacadeFactory;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;

public class MetaBallRenderer {
	public static int minQuadSize = 16;
	public static float[][] distlookup;
	/**
	 * the parent sketch
	 */
	PApplet app;
	
	/**
	 * Graphic buffer where the meta balls will be rendered
	 */
	PGraphics buffer;
	
	QTree root;
	
	protected static MetaBallRenderer renderer = null;
	
	public static void createInstance(PApplet app){
		renderer = new MetaBallRenderer(app);
	}
	
	public static MetaBallRenderer getInstance(){
		return renderer;
	}
	/**
	 * Create a new Meta Ball renderer
	 * @param app
	 */
	protected MetaBallRenderer(PApplet app){
		this.app = app;
		buffer = app.createGraphics(FacadeFactory.getFacade().width,FacadeFactory.getFacade().height, PConstants.JAVA2D);
		QTree.createQuadTree(minQuadSize);
		root = QTree.getInstance();
		 distlookup=new float[buffer.width][buffer.height];  
		  for (int i=0;i<buffer.width;i++) { 
		    for (int j=0;j<buffer.height;j++) { 
		      distlookup[i][j]=(float)Math.sqrt(Math.pow(i,2)+Math.pow(j,2)); 
		    } 
		  } 
	}
	
	public void reset(){
		root.reset();
	}
	
	/**
	 * Render all the meta balls
	 */
	public void render(){
		//ready to calculate next frame
		buffer.beginDraw();
		buffer.background(0,0,0,0);
		  buffer.loadPixels();
		  ArrayList<MetaBall> blobs = root.getObjects();
		  for(int bl=0;bl<blobs.size();bl++){
			  renderMetaBall(blobs.get(bl));
		  } 
		  buffer.updatePixels();
		  buffer.endDraw();
		  app.image(buffer, 0,0);
		  //root.draw(app);
		  reset();
		  
	}
	
	private void renderMetaBall(MetaBall b){
		  if(b.used) //if the blob is flagged, no need to process it
		      return;
		  b.used = true; //flag as used
		  //coordinates of the top left and bottom right corner of the rectangle containing the blob
		  PVector start;
		  PVector stop;


		  //overlapping blobs
		  ArrayList<MetaBall> bbs;

		    start = b.getTopLeft();

		    //get all the quad tree's cells that are part of this blob
		    ArrayList<QTree> cells = root.getCellsThatContainRect(start.x, start.y, b.diameter, b.diameter);
		    if(cells == null){ //no need to continue
		      return;
		    }

		    stop = b.getBottomRight();  

		    //this contains all the blobs that need to be taken into account
		    bbs = new ArrayList<MetaBall>();
		    bbs.add(b);
		    for(int c=0;c<cells.size();c++){
		      QTree cell = cells.get(c);
		      start.x = PApplet.min(start.x,cell.x);
		      start.y = PApplet.min(start.y,cell.y);
		      stop.x = PApplet.max(stop.x,cell.x+cell.w);
		      stop.y = PApplet.max(stop.y,cell.y+cell.h);
		      ArrayList<MetaBall> bbs_tmp = cell.getObjects();

		      for(int i=0;i<bbs_tmp.size();i++){
		        if(!bbs.contains(bbs_tmp.get(i))){
		          bbs.add(bbs_tmp.get(i));
		          MetaBall b_tmp =  bbs_tmp.get(i);
		          b_tmp.used = true; //flag as used
		          PVector tl = b_tmp.getTopLeft();
		          ArrayList<QTree> tmp_cells = root.getCellsThatContainRect(tl.x, tl.y, b_tmp.diameter, b_tmp.diameter);
		          for(int k=0;k<tmp_cells.size();k++) // add the cells of this blob to the cells' list
		            cells.add(tmp_cells.get(k));
		        }
		      }
		    }

		    //go through all the pixels and compute the isosurface value for each blobs
		    /*
		    app.noFill();
		    app.stroke(0,255,0);
		    app.strokeWeight(1);
		    app.rect(start.x, start.y, stop.x-start.x, stop.y-start.y);
		    */
		    int[] colors = new int[bbs.size()];
		    float[] strength = new float[bbs.size()];
		    for (float x=start.x-1;++x<stop.x;){
		      for (float y=start.y-1;++y<stop.y;){
		        float sum = 0;
		        for(int i=0;i<bbs.size();i++){
		          MetaBall blob = bbs.get(i);
		          colors[i] = blob.getDrawingData().getColor();
		          strength[i] = blob.equation(x,y); 
		          sum += strength[i];
		          //sum += blob.equation(x,y);
		        }

		        //float col = sum>1 ? 1 : (sum<MetaBall.MIN_THRESHOLD ? 0: sum);
		        int col =  sum<MetaBall.MIN_THRESHOLD ? buffer.color(0,0,0,0) : blendColor(colors, strength);
		        //float col = sum<MIN_THRESHOLD ? 0: sum;
		        int pos = (int)x+(int)y*buffer.width;
		        if(/*col!=0 || */buffer.pixels[pos] == buffer.color(0,0,0,0))
		          buffer.pixels[pos] = col;//buffer.lerpColor(buffer.color(0,0,0,0),buffer.color(255),col); //b.getDrawingData().getColor()
		      }
		    }
		}
	
	
	/**
	 * Blends an array of colors by specifying their strength in the composite.
	 * Note that there must be as much colors as strength.
	 * @param colors Array of colors
	 * @param strength Array of strength matching the colors
	 * @return The composite color
	 */
	private  int blendColor(int[] colors, float[] strength){
		
		 float a = 0;
		  float r = 0;
		  float g = 0;
		  float b = 0;
		  
		  for(int i=0;i<colors.length;i++){
			  a += (colors[i] >> 24 & 0xFF)*strength[i];
		    r += (colors[i]>> 16 & 0xFF)*strength[i];
		    g += (colors[i]>> 8 & 0xFF)*strength[i];
		    b += (colors[i] & 0xFF)*strength[i];
		    
		  }
		  a = a>255?255:a;
		  r = r>255?255:r;
		  g = g>255?255:g;
		  b = b>255?255:b;
		  
		  return app.color(r,g,b,a);
		}
	
	
}
