package mimodek.decorator.graphics;

@SuppressWarnings("serial")
public class NoImageException extends Exception {

	public NoImageException(){
		super("No image available");
	}

}
