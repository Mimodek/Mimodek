import mimodek.SimpleMimo;
import mimodek.decorator.Cell;
import mimodek.decorator.DeadMimo2;
import mimodek.decorator.graphics.CircleDrawer;
import processing.core.PApplet;
import processing.core.PVector;


public class Tester extends PApplet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void setup(){
		SimpleMimo simpleMimo = new SimpleMimo(new PVector(10,10));
		simpleMimo.setDiameter(10);
		DeadMimo2 deadMimo2 = new DeadMimo2(simpleMimo);
		CircleDrawer drawer = new CircleDrawer(deadMimo2, color(100,100,100));
		Cell cell = new Cell(drawer);
		print(cell.toXMLString(null,""));
	}
}
