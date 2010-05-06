package mimodek.decorator.graphics;

public class GradientData extends SimpleDrawingData{
		public static final int LINEAR = 1;
		public static final int SIN = 2;
		
		int startColor;

		public GradientData(int startColor, int endColor) {
			super(endColor);
			this.startColor = startColor;
		}
		
		public int getStartColor(){
			return startColor;
		}
		
		public String toXMLString(String prefix){
			return prefix+"<DrawingData className=\""+this.getClass().getName()+"\" iteration=\""+iteration+"\" color=\""+color+"\" startColor=\""+startColor+"\"/>\n";
		}
}
