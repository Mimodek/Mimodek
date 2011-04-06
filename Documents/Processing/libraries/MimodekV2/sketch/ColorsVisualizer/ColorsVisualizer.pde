import p5wp.*;
import MimodekV2.imageexport.*;
import MimodekV2.data.*;
import MimodekV2.config.*;
import MimodekV2.debug.*;
import mimodek.facade.*;
import MimodekV2.*;
import MimodekV2.tracking.*;
import MimodekV2.graphics.*;

int colorSquareSize = 50;
int squaresPerRow = 0;
float gradientSteps = 1;

void setup(){
  textFont(loadFont("SansSerif-20.vlw"));
//  colorMode(RGB,1.0);
  colorMode(HSB,1.0);
  File settingsFolder = new File(sketchPath+"/../../settings");
  try{
    TemperatureColorRanges.createTemperatureColorRanges(this,settingsFolder.getAbsolutePath()+"/MimodekColourRanges.txt");
  }catch(Exception e){
  }
  int tempCount = int(TemperatureColorRanges.maxT - TemperatureColorRanges.minT);
  TemperatureColorRanges.getColor(10.9999);
    TemperatureColorRanges.getColor(11);
  TemperatureColorRanges.getColor(22);
// exit();

  squaresPerRow = int(sqrt(tempCount));
  size(squaresPerRow*colorSquareSize,squaresPerRow*colorSquareSize);
  noLoop();
}

void draw(){
  background(0);
  noStroke();
  int row = 0;
  int col = 0;
  int t = int(TemperatureColorRanges.minT);

  while(t<=TemperatureColorRanges.maxT){
    for(int i=0;i<gradientSteps;i++){
      fill(TemperatureColorRanges.getColor(t+(1.0/gradientSteps)*i));
      println(t+(1.0/gradientSteps)*i);
      rect(col*colorSquareSize+i*(colorSquareSize/gradientSteps),row*colorSquareSize,(colorSquareSize/gradientSteps),colorSquareSize);
    }
    fill(0);
    text(t,col*colorSquareSize,(row+1)*colorSquareSize);
    if(col==squaresPerRow){
      row++;
      col = 0;
    }else{
      col++;
    }
    t++;
  }
  save("TemperatureColors.png");
}



