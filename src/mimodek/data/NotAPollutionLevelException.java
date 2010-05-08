package mimodek.data;

@SuppressWarnings("serial")
public class NotAPollutionLevelException extends Exception {

	public NotAPollutionLevelException(String word) {
		super("The word '"+word+"' is not recognized as a pollution level.");
	}

}
