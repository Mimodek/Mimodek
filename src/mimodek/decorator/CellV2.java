package mimodek.decorator;

import java.util.ArrayList;

//import mimodek.configuration.Configurator;
import mimodek.MimodekObject;
import mimodek.configuration.Configurator;
import mimodek.decorator.graphics.ImageDrawer;
import mimodek.decorator.graphics.NoImageException;
import mimodek.facade.FacadeFactory;
import mimodek.utils.Verbose;

import processing.core.PApplet;
import processing.core.PVector;

/**
 * A cell of the DLA. Each cell can have one or more neighbours. Other class
 * should only communicate with the DLA through one and one cell only. Message
 * like attract or repel are passed to neighbours cell and so on, so the whole
 * tree will be queried by calling the method only on a single cell (usually the
 * first one created). Update of two cells from the same DLA will have
 * unexpected result and in any case be take much more time. Tgis also why most
 * methods takes a calling Cell as a parameter so the message will not go back
 * to a cell that has been called already. This class subclass
 * MimodekObjectGraphicsDecorator and decorates only other
 * MimodekObjectGraphicsDecorator because of this: the draw method must call the
 * other cells draw method and so on to display the whole tree. It does not
 * however do any more drawing than the decorated object. Bottom line is : one
 * DLA = one instance of a Cell in the MimoManager class, no more.
 * 
 * @author Jonsku
 */
public class CellV2 extends ImageDrawer {

	public boolean fixed = false;

	ArrayList<CellV2> attachedCells;
	  float angle = 0;
	  float cosAngle = 0;
	  float sinAngle = 0;
	  float distanceToParent = 0;
	  CellV2 parent;
	  PVector inPlace;
	  PVector whereToGo;

	  int retractedChild = 0;
	  boolean inPosition = true;

	  boolean isOnLastBranch = false;

	/**
	 * Creates a new cell from a MimodekObjectGraphicsDecorator.
	 * 
	 * @param decoratedObject
	 * @throws NoImageException
	 */
	public CellV2(DeadMimo2 decoratedObject, PApplet app) throws NoImageException {
		super(decoratedObject, decoratedObject.activeMimosShape, Configurator.getIntegerSetting("temperatureColor"), app);
		attachedCells = new ArrayList<CellV2>();
	    inPlace = new PVector(getPosX(),getPosY());
		Verbose.debug(decoratedObject.decoratedObject.getClass().getName());
		whereToGo = new PVector(getPosX(),getPosY());
	}
	
	public CellV2(DeadMimo2 decoratedObject, PApplet app, CellV2 parent) throws NoImageException {
	    this(decoratedObject,app);
	    angle = PApplet.atan2(getPosY()-parent.getPosY(),getPosX()-parent.getPosX());
	    cosAngle = PApplet.cos(angle);
	    sinAngle = PApplet.sin(angle);        
	    distanceToParent = parent.getPos().dist(getPos());
	    this.parent = parent;

	    /*toPlace = new PVector(distanceToParent*cosAngle,distanceToParent*sinAngle);
	     toPlace.add(getPos()); // position of the parent*/
	    //attachedCells.add(parent);
	  }
	
	public boolean attach(DeadMimo2 obj,PApplet app) throws NoImageException{
	    return attach(null,obj,app);
	  }

	  protected boolean attach(CellV2 up,DeadMimo2 obj,PApplet app) throws NoImageException{
	    //circle-circle collision detection
		  //if(parent==null || isInPlace()==0){
		    if(obj.getPos().dist(getPos())<=(obj.getDiameter()/2+getDiameter()/2)){
		      //create the cell and attach it both ways
		      attachedCells.add(new CellV2(obj,app,this));
		      return true;
		    }
		  //}
	      for(int i=0;i<attachedCells.size();i++){
	        CellV2 c = (CellV2)attachedCells.get(i);
	        if(c!=up){
	          if(c.attach(this,obj,app))
	            return true;
	        }
	      }
	      return false;
	  }



	  boolean move(){
	    float d = isInPlace();
	    if(d==0){
	    	angle = -1;
	      return false;
	    }
	    else{
	      d = PApplet.map(d,0,distanceToParent,1,2);
	      //float a = PApplet.atan2(whereToGo.y-getPosY(),whereToGo.x-getPosX());
	      getPos().add(new PVector(d*PApplet.cos(angle),d*PApplet.sin(angle)));
	      moveChildToSamePosition();
	      return true;
	    }
	  }
	  public void moveBacktoPlace(){
	    getPos().add(new PVector( distanceToParent*cosAngle,distanceToParent*sinAngle));
	    inPosition = true;
	    /*    float d = getPos().dist(PVector.sub(getPos(),toPlace()));
	     if(d>0.1){
	     getPos().add(new PVector(cosAngle,sinAngle));
	     }else{
	     inPosition = true;
	     }*/
	  }

	  public int retractBranch(int i){
	    if(i<0 || i>=attachedCells.size())
	      return 0;
	    return ((CellV2)attachedCells.get(i)).retract();
	  }

	  public float isInPlace(){
	    float d = getPos().dist(whereToGo);
	    if(d>2){
	      return d;
	    }
	    else{
	      setPos(whereToGo.x,whereToGo.y);
	      moveChildToSamePosition();
	      return 0;
	    }
	  }

	  public boolean isReadyToExpand(){
	    return retractedChild == 0;
	  }

	  public boolean isReadyToRetract(){
	    return retractedChild == attachedCells.size() || isLeaf();
	  }


	  protected void moveChildToSamePosition(){
	    if(isLeaf())
	      return;
	    for(int i=0;i<attachedCells.size();i++){
	      CellV2 c = (CellV2)attachedCells.get(i);
	      if(c!=parent){
	        c.setPosX(getPosX());
	        c.setPosY(getPosY());
	        c.moveChildToSamePosition();
	        c.inPosition = false;
	      }
	    }
	  }

	  public int retract(float x, float y, float radius){
	    int counter = 0;
	    if(PApplet.dist(x,y,getPosX(),getPosY())<=getDiameter()/2+radius){
	      counter += retract();
	    }
	    
	      
	      for(int i=0;i<attachedCells.size();i++){
	        CellV2 c = (CellV2)attachedCells.get(i);
	        counter += c.retract(x,y, radius);
	      }
	      return counter;
	  }

	  int retract(){
	    if(!isReadyToRetract()){
	      if(parent!=null && parent.isOnLastBranch){
	        return 0;
	      }
	      boolean childrenReady = true;
	      for(int i=0;i<attachedCells.size();i++){
	        CellV2 c = (CellV2)attachedCells.get(i);
	        if (!c.isReadyToRetract()){
	          childrenReady = false;
	          break;
	        }
	      }
	      if(childrenReady){
	        boolean done = true;
	        for(int i=0;i<attachedCells.size();i++){
	          CellV2 c = (CellV2)attachedCells.get(i);
	          if(c.angle<0)
		    		c.angle = PApplet.atan2(getPosY()-c.getPosY(),getPosX()-c.getPosX());
	          if(c.move()){
	            done = false;
	          }
	        }
	        if(done){
	          moveChildToSamePosition();
	          retractedChild=attachedCells.size();
	          return 1;
	        }
	        else{
	          return 0;
	        }
	      }
	      else{
	        int count = 0;
	        for(int i=0;i<attachedCells.size();i++){
	          CellV2 c = (CellV2)attachedCells.get(i);
	          if (!c.isReadyToRetract()){
	            count+=c.retract();
	          }
	        }
	        return count;
	      }
	    }
	    else{
	      int count = 0;
	      for(int i=0;i<attachedCells.size();i++){
	        CellV2 c = (CellV2)attachedCells.get(i);
	        if (!c.isReadyToRetract()){
	          count+=c.retract();
	        }
	      }/*
	      if(parent!=null && count==0){
	          if(move()){
	        	  return 1;
	          }else{
	        	  return 0;
	          }
	      }*/
	      return count;
	    }
	  }

	  void randomRetract(float proba, int time){
	    if(Math.random()<proba){
	      while(retract()>time)
	        ;
	      return;
	    }
	    for(int i=0;i<attachedCells.size();i++){
	      CellV2 c = (CellV2)attachedCells.get(i);
	      c.randomRetract(proba,time);
	    }
	  }


	  public int expand(){
	    if(!isReadyToExpand()){
	      if(parent==null || parent.isReadyToExpand()){ //case of the seed
	    	  if(parent!=null){
		    	if(angle<0)
		    		angle = (float)Math.random()*PApplet.TWO_PI;
		        if(move()){
		          //println("Moving");
		          return 1;
		        }
		        else{
		          //println("done");
		          inPosition = true;
		          retractedChild=0;
		          return 0;
		        }
	    	  }else{
	    		  inPosition = true;
		          retractedChild=0;
		          return 0;
	    	  }
	        /*boolean done = true;
	         for(int i=0;i<attachedCells.size();i++){
	         CellV2 c = (CellV2)attachedCells.get(i);
	         c.whereToGo =  PVector.add(c.getPos(),new PVector(c.distanceToParent*c.cosAngle,c.distanceToParent*c.sinAngle));
	         if(c.move()){
	         done = false;
	         }
	         }
	         if(done){
	         inPosition = true;
	         //moveChildToSamePosition();
	         retractedChild=0;
	         return 1;
	         }/*/
	      }
	      return 0;
	    }
	    else{
	      if(isLeaf() && !inPosition){
	    	 if(angle<0)
		    		angle = (float)Math.random()*PApplet.TWO_PI;
	        if(move()){
	          return 1;
	        }
	        inPosition = true;
	        return 1;
	      }
	      int counter = 0;
	      for(int i=0;i<attachedCells.size();i++){
	        CellV2 c = (CellV2)attachedCells.get(i);
	        counter+=c.expand();
	      }
	      return counter;
	    }


	  }


	  public boolean isLeaf(){
	    // println("isLeaf: "+(attachedCells.size() == 0));
	    return attachedCells.size() == 0;
	  }


	  public boolean isParentOf(CellV2 cell){
	    if(this == cell)
	      return true;

	    for(int i=0;i<attachedCells.size();i++){
	      CellV2 c = (CellV2)attachedCells.get(i);
	      if(c!=parent){
	        if(c.isParentOf(cell))
	          return true;
	      }
	    }
	    return false;

	  }

	  public int getBranchOfCell(CellV2 cell){
	    for(int i=0;i<attachedCells.size();i++){
	      CellV2 c = (CellV2)attachedCells.get(i);
	      if(c!=parent && c.isParentOf(cell)){
	        return i;
	      }
	    }
	    return -1;
	  }

	  public CellV2 getCellAtPos(float x, float y){
	   // println("OK");
	    if(PApplet.dist(getPosX(),getPosY(),x,y)<=getDiameter()/2)
	      return this;
	    for(int i=0;i<attachedCells.size();i++){
	      CellV2 c = (CellV2)attachedCells.get(i);
	      if(c!=parent){
	        CellV2 tmp = c.getCellAtPos(x,y);
	        if(tmp != null)
	          return tmp;
	      }
	    }
	    return null;
	  }

	  public boolean tagOnLastBranch(){
	    if(isLeaf()){
	      isOnLastBranch = true;
	      return true;
	    }

	    if(attachedCells.size()>1){
	      isOnLastBranch = false;
	      for(int i=0;i<attachedCells.size();i++){
	        CellV2 c = (CellV2)attachedCells.get(i);
	        c.tagOnLastBranch(); 
	      }
	      return false;
	    }
	    else{
	      isOnLastBranch = attachedCells.size()<1?true:((CellV2)attachedCells.get(0)).tagOnLastBranch();
	      return isOnLastBranch;
	    }

	  }
	  
	  public void update(){
	    if(parent!=null){
	      float d = getPos().dist(parent.getPos());
	      if(d>distanceToParent){
	        d-=distanceToParent;
	        float a = PApplet.atan2(parent.getPosY()-getPosY(),parent.getPosX()-getPosX());
	        getPos().add(new PVector(PApplet.cos(a)*d,PApplet.sin(a)*d));
	        //whereToGo = new PVector(getPosX(),getPosY());
	      }
	    }/*else if(parent!=null){
	    	angle = PApplet.atan2(getPosY()-parent.getPosY(),getPosX()-parent.getPosX());
		    cosAngle = PApplet.cos(angle);
		    sinAngle = PApplet.sin(angle);      
	    	inPlace = new PVector(parent.getPosX()+distanceToParent*cosAngle,parent.getPosY()+distanceToParent*cosAngle);
	    	setPos(inPlace.x,inPlace.y);
	    }*/
	    for (int i = 0; i < attachedCells.size(); i++) {
	      CellV2 c = (CellV2)attachedCells.get(i);
	      c.update();
	    }
	  }

	  public void attract(float x, float y, float threshold) {
	    if((parent==null && !fixed) || isOnLastBranch ){
	      float d = threshold + 1;
	        d = PApplet.dist(x, y, getPosX(), getPosY());
	        if (d <= threshold /* && d >= getRadius()/2 */) {
	          float a = PApplet.atan2(y - getPosY(), x - getPosX());
	          d = PApplet.map(d,0,d,1,3);
	          PVector nuPos = new PVector(getPosX() + PApplet.cos(a) * d,getPosY() + PApplet.sin(a) * d);
	          setPos(nuPos);
	        }
	    }
	    for (int i = 0; i < attachedCells.size(); i++) {
	      CellV2 c = (CellV2)attachedCells.get(i);
	      c.attract(x, y, threshold);
	    }
	  }

	

	/**
	 * Draw the whole DLA
	 * 
	 * @see mimodek.decorator.graphics.MimodekObjectGraphicsDecorator#draw(processing.core.PApplet)
	 */
	@Override
	public void draw(PApplet app) {
		draw(null, app);
		app.ellipse(getPosX(),getPosY(),getDiameter(),getDiameter());
	}

	/**
	 * Draw this cell and pass the message to neighbours cells
	 * 
	 * @param up
	 *            The calling cell
	 * @param app
	 *            The parent Sketch
	 */
	protected void draw(CellV2 up, PApplet app) {
		super.draw(app);
		for (int i = 0; i < attachedCells.size(); i++) {
			CellV2 c = (attachedCells.get(i));
			if (c != up) // draw only if it is not the calling cell
				c.draw(this, app);
		}
	}

	
	/*
	 * @Override public PImage toImage(PApplet app) { return
	 * ((MimodekObjectGraphicsDecorator)decoratedObject).toImage(app); }
	 */

	public String toXMLString(CellV2 up, String prefix) {

		Verbose.debug(prefix + this);
		String XMLString = prefix + "<DLACell className=\""
				+ this.getClass().getName() + "\" fixed=\"" + fixed + "\">\n";
		XMLString += decoratedObject.toXMLString(prefix + "\t");
		XMLString += prefix + "\t<neighbours>\n";
		for (int i = 0; i < attachedCells.size(); i++) {
			CellV2 c = attachedCells.get(i);
			if (c != up) {
				XMLString += c.toXMLString(this, prefix + "\t\t");
			}
		}
		XMLString += prefix + "\t</neighbours>\n";
		XMLString += prefix + "</DLACell>\n";
		return XMLString;
	}

}
