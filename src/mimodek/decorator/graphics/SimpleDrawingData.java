package mimodek.decorator.graphics;

public class SimpleDrawingData implements DrawingData{
		int color;
		int iteration =1;
		
		public SimpleDrawingData(int color){
			this.color = color;
		}
		public int getColor(){
			return color;
		}
		
		public int getIteration() {
			return iteration;
		}
		
		public void setIteration(int n) {
			iteration = n;
		}
		
		public void incIteration(int n) {
			iteration += n;
		}
		
		public String toXMLString(String prefix){
			return prefix+"<DrawingData className=\""+this.getClass().getName()+"\" iteration=\""+iteration+"\" color=\""+color+"\"/>\n";
		}



}
