class ColorRangeHolder{
  ArrayList colorRanges;
  ArrayList deleteButtons;

  int left, top, s;
  int gap = 10;

  public ColorRangeHolder(int left, int top, int s){
    this.left = left;
    this.top = top;
    this.s = s;
    colorRanges = new ArrayList();
    deleteButtons = new ArrayList();
  }
  
  public void loadFromFile(String filePath){
    colorRanges = new ArrayList();
    deleteButtons = new ArrayList();
    String lines[] = loadStrings(filePath);

    for (int i=0; i < lines.length; i++) {
      String[] splitted = lines[i].split(",");
      addColorRange(color(float(splitted[0]),float(splitted[1]),float(splitted[2])), color(float(splitted[3]),float(splitted[4]),float(splitted[5])));
    }
  }
  
  public void saveToFile(String filePath){
    String[] lines = new String[colorRanges.size()];
    for(int i=0;i<colorRanges.size();i++){
      ColorRange cr = (ColorRange)colorRanges.get(i);
      lines[i] = cr.toString();
    }
    saveStrings(/*"../config/"+*/filePath, lines);
  }

  public void setSelectedColor(int col){
    for(int i=0;i<colorRanges.size();i++){
      ColorRange cR = (ColorRange)colorRanges.get(i);
      if(cR.isSelected()>=0){
        cR.setColor(cR.isSelected(), col);
        return;
      }
    }
  }
  
  public void addColorRange(int c1, int c2){
     if(colorRanges.size()==15)
      return;
    colorRanges.add(new ColorRange(left,top+s*(colorRanges.size()+1)+gap*colorRanges.size(),s,c1,c2));
    deleteButtons.add(new DeleteButton(left+s*2+gap*2,top+s*(colorRanges.size())+gap*(colorRanges.size()-1),s));
  }

  public void addColorRange(){
    if(colorRanges.size()==15)
      return;
    colorRanges.add(new ColorRange(left,top+s*(colorRanges.size()+1)+gap*colorRanges.size(),s));
    if(colorRanges.size()>1)
      ((ColorRange)colorRanges.get(colorRanges.size()-1)).setColor(0,((ColorRange)colorRanges.get(colorRanges.size()-2)).colors[1]);
    deleteButtons.add(new DeleteButton(left+s*2+gap*2,top+s*(colorRanges.size())+gap*(colorRanges.size()-1),s));
  }

  public void removeColorRange(int ind){
    int sz = colorRanges.size();
    for(int i=0;i<colorRanges.size();i++){
      if(i<ind)
        continue;
      if(i==ind && colorRanges.size() == sz){
        colorRanges.remove(i);
        deleteButtons.remove(i);
        i--;
      }
      else{
        ((ColorRange)colorRanges.get(i)).moveDown(gap+s);
        ((DeleteButton)deleteButtons.get(i)).moveDown(gap+s);
      }
    }
  }

  public void draw(){
    for(int i=0;i<colorRanges.size();i++){
      ColorRange cr = (ColorRange)colorRanges.get(i);
      cr.draw();
      ((DeleteButton)deleteButtons.get(i)).draw();
    }
  }

  void mouseClicked(){
    for(int i=0;i<colorRanges.size();i++){
      if(((DeleteButton)deleteButtons.get(i)).click()){
        removeColorRange(i);
        return;
      }
      ColorRange cr = (ColorRange)colorRanges.get(i);
      if(cr.click()>=0){
        deselectAllExcept(i);
        return;
      }
    }
  }

  void deselectAllExcept(int ind){
    for(int i=0;i<colorRanges.size();i++){
      if(i!=ind){
        ((ColorRange)colorRanges.get(i)).deselect();
      }
    }
  }
}

class ColorRange{
  int[] colors;
  boolean[] selected;

  int left, top,s;
  public ColorRange(int left, int top, int s){
    this.left = left;
    this.top = top;
    this.s = s;
    colors = new int[2];
    selected = new boolean[2];
  }
  
  public ColorRange(int left, int top, int s, int c1, int c2){
    this(left, top, s);
    colors = new int[]{c1,c2};
  }

  public void moveUp(int offset){
    top+=offset;
  }

  public void moveDown(int offset){
    top-=offset;
  }

  public void setColor(int ind, int col){
    if(ind>1 || ind<0)
      return;
    colors[ind] = col;
  }

  public void draw(){
    pushStyle();

    noStroke();
    if(selected[0]){
      strokeWeight(2);
      stroke(0);
    }
    fill(colors[0]);
    rect(left,top,s,s);

    noStroke();
    if(selected[1]){
      strokeWeight(2);
      stroke(0);
    }
    fill(colors[1]);
    rect(left+s+10,top,s,s);

    popStyle();
  }

  public void deselect(){
    selected = new boolean[2];
  }

  public int isSelected(){
    if(!selected[0] && !selected[1])
      return -1;
    else
      return selected[0]?0:1;
  }

  public int click(){
    if(mouseX<left || mouseX>left+s*2+10 || mouseY<top || mouseY>top+s)
      return -1;
    if(mouseX>left+s && mouseX<left+s+10)
      return -1;
    int r = mouseX<left+s+10?0:1;
    selected[r] = !selected[r];
    if(selected[r])
      selected[r==0?1:0] = false;
    return r;
  }
  
  public String toString(){
    return hue(colors[0])+","+saturation(colors[0])+","+brightness(colors[0])+","+hue(colors[1])+","+saturation(colors[1])+","+brightness(colors[1]);
  }
}

public class DeleteButton{
  int left, top, s;

  public DeleteButton(int left, int top, int s){
    this.left = left;
    this.top = top;
    this.s = s;
  }

  public void moveUp(int offset){
    top+=offset;
  }

  public void moveDown(int offset){
    top-=offset;
  }

  public void draw(){
    pushStyle();
    colorMode(RGB,1.0);
    fill(1,0,0);
    rect(left,top,s,s);
    fill(1);
    text("X",left+6,top+16);
    popStyle();    
  }

  public boolean click(){
    if(mouseX<left || mouseX>left+s || mouseY<top || mouseY>top+s)
      return false;
    return true;
  }

}




