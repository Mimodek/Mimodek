package mimodek.decorator.graphics;

import java.util.ArrayList;

import mimodek.facade.FacadeFactory;

import processing.core.PApplet;
import processing.core.PVector;

public class QTree{
	  public float x;
	  public float y;
	  public float w;
	  public float h;
	  QTree topLeft;
	  QTree topRight;
	  QTree bottomRight;
	  QTree bottomLeft;

	  ArrayList<MetaBall> objects;
	  
	  boolean marked = false;
	  
	  protected static QTree quadTree = null;
	  
	  public static void createQuadTree(int rectSize){
		  quadTree = new QTree(0,0,FacadeFactory.getFacade().width,FacadeFactory.getFacade().height,rectSize);  
	  }
	  
	  public static QTree getInstance(){
		  return quadTree;
	  }

	  protected QTree(float x, float y, float w, float h, int minSize){
	    this.x = x;
	    this.y = y;
	    this.w = w;
	    this.h = h;

	    float halfW = w/2;
	    float halfH = h/2;
	    if(halfW > minSize || halfH > minSize){
	      topLeft = new QTree(x,y,halfW, halfH,minSize);
	      topRight = new QTree(x+halfW,y,halfW, halfH,minSize);
	      bottomRight = new QTree(x+halfW,y+halfH,halfW, halfH,minSize);
	      bottomLeft = new QTree(x,y+halfH,halfW, halfH,minSize);
	    }
	    objects = new ArrayList<MetaBall>();
	  }

	  public void reset(){
	    objects = new ArrayList<MetaBall>();
	    marked = false;
	    if(!isLeaf()){
	      topLeft.reset();
	      topRight.reset();
	      bottomRight.reset();
	      bottomLeft.reset();
	    }
	  }

	  public ArrayList<MetaBall> getObjects(){

	    if(!isLeaf()){
	      ArrayList<MetaBall> tl = topLeft.getObjects();
	      if(!tl.isEmpty()){
	        for(int i=0;i<tl.size();i++){
	          if(!objects.contains(tl.get(i)))
	            objects.add(tl.get(i));
	        }
	      }
	      tl = topRight.getObjects();
	      if(!tl.isEmpty()){
	        for(int i=0;i<tl.size();i++){
	          if(!objects.contains(tl.get(i)))
	            objects.add(tl.get(i));
	        }
	      }
	      tl = bottomRight.getObjects();
	      if(!tl.isEmpty()){
	        for(int i=0;i<tl.size();i++){
	          if(!objects.contains(tl.get(i)))
	            objects.add(tl.get(i));
	        }
	      }
	      tl = bottomLeft.getObjects();
	      if(!tl.isEmpty()){
	        for(int i=0;i<tl.size();i++){
	          if(!objects.contains(tl.get(i)))
	            objects.add(tl.get(i));
	        }
	      }
	    }
	    return objects;
	  }

	  public QTree addObject(MetaBall o){
	    if(!objects.contains(o))
	      objects.add(o);  
	    return this;
	  }
	  
	  public void addBlob(MetaBall b){
	    PVector tl = b.getTopLeft();
	    PVector br = b.getBottomRight();
	    //register the blobs in the quad tree
	    addObject(tl.x, tl.y, br.x-tl.x, br.y-tl.y, b);
	  }

	  public void addObject(float x, float y, MetaBall o){
	   /* if(!isInCell(x,y))
	      return null;*/



	    if(isLeaf()){
	      objects.add(o);
	      //return this;
	    }

	    if(topLeft.isInCell(x,y)){
	      topLeft.addObject(x,y,o);
	    }
	    else if(topRight.isInCell(x,y)){
	      topRight.addObject(x,y,o);
	    }
	    else if(bottomRight.isInCell(x,y)){
	     bottomRight.addObject(x,y,o);
	    }
	    else{
	      bottomLeft.addObject(x,y,o);
	    }
	  }

	  public void addObject(float x, float y, float w, float h, MetaBall o){
	    if(isInCell(x,y,w,h)){
	      if(isLeaf()){
	        addObject(o);
	      }
	      else{
	        topLeft.addObject(x,y,w,h,o);
	        topRight.addObject(x,y,w,h,o);
	        bottomRight.addObject(x,y,w,h,o);
	        bottomLeft.addObject(x,y,w,h,o);
	      }
	    }
	  }

	  public ArrayList<QTree> getCellsThatContainRect(float x, float y, float w, float h){
	    if(isInCell(x,y,w,h)){
	      ArrayList<QTree> cells = new ArrayList<QTree>();
	      if(isLeaf()){
	        
	        //if(!objects.isEmpty()){
	          if(!marked){
	            cells.add(this);
	            marked = true;
	            return cells;
	          }else{
	            return null;
	          }
	        //}else{
	        //  return null;
	        //}
	      }
	      if(topLeft.isInCell(x,y,w,h)){
	    	  ArrayList<QTree> t = topLeft.getCellsThatContainRect(x,y,w,h);
	        if(t!=null){
	          for(int i=0;i<t.size();i++){
	            cells.add(t.get(i));
	          }
	        }
	      }
	      if(topRight.isInCell(x,y,w,h)){
	    	  ArrayList<QTree> t = topRight.getCellsThatContainRect(x,y,w,h);
	        if(t!=null){
	          for(int i=0;i<t.size();i++){
	            cells.add(t.get(i));
	          }
	        }
	      }
	      if(bottomRight.isInCell(x,y,w,h)){
	    	  ArrayList<QTree> t = bottomRight.getCellsThatContainRect(x,y,w,h);
	        if(t!=null){
	          for(int i=0;i<t.size();i++){
	            cells.add(t.get(i));
	          }
	        }
	      }
	      if(bottomLeft.isInCell(x,y,w,h)){
	    	  ArrayList<QTree> t = bottomLeft.getCellsThatContainRect(x,y,w,h);
	        if(t!=null){
	          for(int i=0;i<t.size();i++){
	            cells.add(t.get(i));
	          }
	        }
	      }
	      return cells;
	    }else{
	      return null;
	    }
	  }

	  public void draw(PApplet app){
	    if(isLeaf() && !objects.isEmpty()){
	      app.noFill();
	      app.strokeWeight(1);
	      if(objects.size()>1){
	        app.stroke(0,0,255);
	        app.rect(x,y,w,h);
	      }
	      /*}else{
	         app.stroke(255);
	      }*/
	      
	    }
	    else if(!isLeaf()){
	      topLeft.draw(app);
	      topRight.draw(app);
	      bottomRight.draw(app);
	      bottomLeft.draw(app);
	    }
	  }

	  public boolean isLeaf(){
	    return topLeft == null &&  topRight == null && bottomRight == null && bottomLeft == null;
	  }

	  public boolean isInCell(float x, float y){
	    return x>=this.x && x<= this.x+w && y>=this.y && y<= this.y+h;
	  }

	  public boolean isInCell(float x, float y, float w, float h){

	    //boolean b = x>=this.x && w<= this.w && y>=this.y && y+h<= this.y+this.h
	    return !(x>=this.x+this.w || y>=this.y+this.h || x+w <= this.x || y+h<=this.y);
	  }




	}



