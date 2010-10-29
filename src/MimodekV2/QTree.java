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

import MimodekV2.config.Configurator;
import MimodekV2.graphics.OpenGL;
import mimodek.facade.FacadeFactory;
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

  float scent = 0;
  PVector foodPos = new PVector(0,0);

  boolean marked = false;

  public QTree(float x, float y, float w, float h, int minSize){
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
    //objects = new ArrayList();
  }

  public void update(){
    marked = false;
    if(!isLeaf()){
      topLeft.update();
      topRight.update();
      bottomRight.update();
      bottomLeft.update();
    }
    else{
      scent -= Configurator.getFloatSetting("FOOD_SCENT_EVAPORATION");
      scent = scent<0?0:scent;
      if(scent <= 0)
        foodPos = new PVector(FacadeFactory.getFacade().halfWidth,FacadeFactory.getFacade().halfHeight);
       
    }
  }

  public float getScent(){

    if(!isLeaf()){
      return topLeft.getScent()+topRight.getScent()+bottomLeft.getScent()+bottomRight.getScent();
    }
    else{
      return scent;
    }
  }

  /*
  public QTree addScent(float a){
   scent += a;
   return this;
   }
   
   public void addScent(float a){
   scent += a;
   PVector tl = b.getTopLeft();
   //register the blobs in the quad tree
   addObject(tl.x, tl.y, b.actualRadius, b.actualRadius, (Object)b);
   }
   */
  public void addScent(float x, float y, float s){
    /* if(!isInCell(x,y))
     return null;*/



    if(isLeaf()){
      scent += s;
      scent = scent>1?1:scent;
      //return this;
    }

    if(topLeft.isInCell(x,y)){
      topLeft.addScent(x,y,s);
    }
    else if(topRight.isInCell(x,y)){
      topRight.addScent(x,y,s);
    }
    else if(bottomRight.isInCell(x,y)){
      bottomRight.addScent(x,y,s);
    }
    else{
      bottomLeft.addScent(x,y,s);
    }
  }

  public void addScent(float x, float y, float w, float h, float s, PVector fP){
    
    if(isInCell(x,y,w,h)){
      if(isLeaf()){
        scent += s;
        foodPos.add(fP);
        foodPos.div(2.0f);
      }
      else{
        topLeft.addScent(x,y,w,h,s,fP);
        topRight.addScent(x,y,w,h,s,fP);
        bottomRight.addScent(x,y,w,h,s,fP);
        bottomLeft.addScent(x,y,w,h,s,fP);
      }
    }
  }
  
  public PVector getSmellInRect(float x, float y, float w, float h){
    if(isInCell(x,y,w,h)){
      if(isLeaf()){
    	  //scent -= 0.01f;
        if(scent == 0)
          return new PVector(0,0);
        return foodPos;
      }
      PVector p = new PVector(0,0);
      int c = 0;
      PVector pp = null;
      if(topLeft.isInCell(x,y,w,h)){
        pp = topLeft.getSmellInRect(x,y,w,h);
        //println(pp);
        if(pp.x!=0 || pp.y!=0){
          p.add(pp);
          c++;
        }
      }
      if(bottomLeft.isInCell(x,y,w,h)){
        pp = bottomLeft.getSmellInRect(x,y,w,h);
               // println(pp);
        if(pp.x!=0 || pp.y!=0){
          p.add(pp);
          c++;
        }
      }
      if(topRight.isInCell(x,y,w,h)){
       pp = topRight.getSmellInRect(x,y,w,h);
              // println(pp);
       if(pp.x!=0 || pp.y!=0){
          p.add(pp);
          c++;
        }
      }
      if(bottomRight.isInCell(x,y,w,h)){
        pp = bottomRight.getSmellInRect(x,y,w,h);
               // println(pp);
        if(pp.x!=0 || pp.y!=0){
          p.add(pp);
          c++;
        }
      }
      if(c > 0)
        p.div(c);

      return p;
    }else{
      return null;
    }
  }
  
/*
  public ArrayList<> getCellsThatContainRect(float x, float y, float w, float h){
    if(isInCell(x,y,w,h)){
      ArrayList cells = new ArrayList();
      if(isLeaf()){

        //if(!objects.isEmpty()){
        if(!marked){
          cells.add(this);
          marked = true;
          return cells;
        }
        else{
          return null;
        }
        //}else{
        //  return null;
        //}
      }
      if(topLeft.isInCell(x,y,w,h)){
        ArrayList t = topLeft.getCellsThatContainRect(x,y,w,h);
        if(t!=null){
          for(int i=0;i<t.size();i++){
            cells.add(t.get(i));
          }
        }
      }
      if(topRight.isInCell(x,y,w,h)){
        ArrayList t = topRight.getCellsThatContainRect(x,y,w,h);
        if(t!=null){
          for(int i=0;i<t.size();i++){
            cells.add(t.get(i));
          }
        }
      }
      if(bottomRight.isInCell(x,y,w,h)){
        ArrayList t = bottomRight.getCellsThatContainRect(x,y,w,h);
        if(t!=null){
          for(int i=0;i<t.size();i++){
            cells.add(t.get(i));
          }
        }
      }
      if(bottomLeft.isInCell(x,y,w,h)){
        ArrayList t = bottomLeft.getCellsThatContainRect(x,y,w,h);
        if(t!=null){
          for(int i=0;i<t.size();i++){
            cells.add(t.get(i));
          }
        }
      }
      return cells;
    }
    else{
      return null;
    }
  }
*/
  
  public void draw(){
    if(isLeaf() && scent>0){
      OpenGL.color(0.0f,scent,0.0f,0.5f);
      OpenGL.rect(x,y,w,h);
    }
    else if(!isLeaf()){
      topLeft.draw();
      topRight.draw();
      bottomRight.draw();
      bottomLeft.draw();
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
