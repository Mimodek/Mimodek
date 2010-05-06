package mimodek.decorator.graphics;

public interface DrawingData {
	public int getColor();
	
	public int getIteration();
	
	public void setIteration(int n);
	
	public void incIteration(int n);

	public String toXMLString(String prefix);
}
