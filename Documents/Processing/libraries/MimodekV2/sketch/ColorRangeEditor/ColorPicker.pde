class ColorChooser{
  int top,left;
  ColorPicker colorPicker;
  Slider h;
  Slider s;
  Slider b;

  ColorRangeHolder colorRanges;
  color lastColor;

  public ColorChooser(int left, int top, ColorRangeHolder colorRanges){
    this.top = top;
    this.left = left;
    colorPicker = new ColorPicker(100,0,0);
    h = new Slider(110,110,10,"Hue",HUE,colorPicker);
    s = new Slider(110,110,40,"Saturation",SATURATION,colorPicker);
    b = new Slider(110,110,70,"Brightness",BRIGHTNESS,colorPicker);
    colorMode(HSB,1.0);
    lastColor = color(0);
    this.colorRanges = colorRanges;
  }

  public void draw(){
    pushMatrix();
    translate(left,top);
    pushStyle();
    fill(0.5);
    rect(0,0,300,100);
    //h.setValue(map(mouseX,0,width,0.0,1.1));
    try{
      color c = colorPicker.colorAt(mouseX-left, mouseY-top);
      fill(c);
      rect(270,5,20,20);
    }catch(Exception e){
    }
    fill(lastColor);
    rect(240,30,50,50);
    colorPicker.draw();
    h.draw();
    s.draw();
    b.draw();
    pushStyle();
    popMatrix();
  }

  void mouseClicked(){
    if(mouseX<left || mouseX>left+300 || mouseY<top || mouseY>top+100)
      return;
    int mX = mouseX-left;
    int mY = mouseY-top;
    if(h.click(mX,mY))
      return;
    if(s.click(mX,mY))
      return;
    if(b.click(mX,mY))
      return;
    
    try{
      color c = colorPicker.colorAt(mX, mX);
      lastColor = c;
      colorRanges.setSelectedColor(c);
    }catch(Exception e){
    }
  }

  void mouseDragged(){
    int mX = mouseX-left;
    int mY = mouseY-top;
    if(!h.dragged && !s.dragged && !b.dragged){
      if(h.drag(mX,mY))
        return;
      if(s.drag(mX,mY))
        return;
      if(b.drag(mX,mY))
        return;
    }
    else if(h.dragged){
      h.drag(mX,mY);
    }
    else if(s.dragged){
      s.drag(mX,mY);
    }
    else{
      b.drag(mX,mY);
    }
  }

  void mouseReleased(){
    h.dragged = false;
    s.dragged = false;
    b.dragged = false;
  }

}

class ColorPicker{


  //graphic buffer
  PGraphics pix;

  //value
  float[] values = new float[3];

  int left,top;

  public ColorPicker(int s, int top, int left){
    this.top = top;
    this.left = left;
    pix = createGraphics(s,s,JAVA2D);
    setParam(HUE, 0);

  }

  public void setParam(int param, float value){
    if(param>BRIGHTNESS || param<HUE)
      return;
    value = value>1?1:(value<0?0:value);
    values[param] = value;
    pix.beginDraw();
    pix.background(255);
    pix.colorMode(HSB,1.0);
    pix.loadPixels();
    for(int i=0;i<pix.width;i++){
      for(int j=0;j<pix.height;j++){
        switch(param){
        case HUE:
          pix.pixels[i+j*pix.width] = color(values[HUE], map(i,0,width,values[SATURATION],1.1),map(j,0,height,values[BRIGHTNESS],1.1));
          break;
        case SATURATION:
          pix.pixels[i+j*pix.width] = color( map(i,0,width,values[HUE],1.1), values[SATURATION],map(j,0,height,values[BRIGHTNESS],1.1));
          break;
        case BRIGHTNESS:
          pix.pixels[i+j*pix.width] = color( map(i,0,width,values[HUE],1.1),map(j,0,height,values[SATURATION],1.1),values[BRIGHTNESS]);
          break;            
        }
      }
    }
    pix.updatePixels();
    pix.endDraw();
  }

  public color colorAt(int x, int y) throws Exception{
    if(x<left || x>left+pix.width || y<top || y>top+pix.width)
      throw new Exception("");

    pix.beginDraw();
    pix.colorMode(HSB,1.0);
    pix.loadPixels();
    color c = pix.pixels[(x-left)+(y-top)*pix.width];
    pix.endDraw();
    return c;
  }

  public void draw(){
    image(pix,left,top);
  }

}

class Slider{
  int length;
  float val = 0;
  int left, top;
  String label;
  boolean dragged = false;
  int param;
  ColorPicker colorPicker;

  public Slider(int length, int left, int top, String label, int param, ColorPicker colorPicker){
    this.length = length;
    this.top = top;
    this.left = left;
    this.label = label;
    this.param = param;
    this.colorPicker = colorPicker;
  }

  public void draw(){
    pushMatrix();
    translate(left,top);
    pushStyle();
    strokeWeight(2);
    stroke(0);
    line(0,0,length,0);
    noStroke();
    fill(1);
    float p = map(val,0.0,1.0,0,length);
    rect(p-4,-4,8,8);
    stroke(0);
    strokeWeight(1);

    text(label,0,20);
    //text(val,70,20);
    popStyle();
    popMatrix();
  }

  public void setValue(float v){
    val = v>1?1:(v<0?0:v);
    colorPicker.setParam(param,val);
  }

  public float getValue(){
    return val;
  }

  public boolean click(int mX, int mY){
    if(mX<left || mX>left+length || mY<top-4 || mY>top+4){
      dragged = false;
      return false;
    }
    setValue(map(mX,left,left+length,0.0,1.0));
    return true;
  }

  public boolean drag(int mX, int mY){
    if(!dragged && (mX<left || mX>left+length || mY<top-4 || mY>top+4))
      return false;
    dragged = true;
    setValue(map(mX,left,left+length,0.0,1.0));
    return true;
  }
}


