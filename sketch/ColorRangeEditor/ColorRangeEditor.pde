import javax.swing.*;

//constant
final static int HUE = 0;
final static int SATURATION = 1;
final static int BRIGHTNESS = 2;

ColorChooser colorChooser;

ColorRangeHolder cRange;

String pathToColorRangeFile;

void setup(){
  pathToColorRangeFile =  sketchPath+"/../../settings/MimodekColourRanges.txt";
  textFont(loadFont("SansSerif-14.vlw"));
  size(600,600);
  colorMode(HSB,1.0);
  cRange = new ColorRangeHolder(10, 20, 20);
  colorChooser = new ColorChooser(10,height-110,cRange);
  openDialog();
 // cRange.addColorRange();
  
}

void draw(){
  background(1);
  drawColourRanges(0,0,width,15);
  colorChooser.draw();
  cRange.draw();
  fill(0);
  text("> Press SPACE BAR to add a color range",200,100);
  text("> Press 'o' to open a range file",200,150);
  text("> Press 's' to save to a range file",200,200);
  text("Click on a square to select a color of a range\n(a black border will appear around it).\nClick on the color picker to assign it a color.",200,250);
}

void drawColourRanges(int left,int top, int w, int h){
  if(cRange.colorRanges.size()==0)
    return;
  float step = w/(cRange.colorRanges.size());
  for(int i=0;i<cRange.colorRanges.size();i++){
    ColorRange cR = (ColorRange)cRange.colorRanges.get(i);
    for(float j = i*step;j<(i+1)*step;j++){
      stroke(lerpColor(cR.colors[0], cR.colors[1],map(j, i*step,(i+1)*step,0.0,1.0)));
      line(left+j,top,left+j,top+h);
    }
  }
}

void mouseClicked(){
  colorChooser.mouseClicked();
  cRange.mouseClicked();
}

void mouseDragged(){
  colorChooser.mouseDragged();
}

void mouseReleased(){
  colorChooser.mouseReleased();
}

void keyPressed(){
  if(key == ' ')
    cRange.addColorRange();
  if(key == 'o')
    openDialog();
  if(key == 's')
    saveDialog();
}


