package mimodek.data;

public enum PollutionLevelsEnum {
	GOOD (new String[]{"good","buena","bueno"}, 1),
	OK (new String[]{"ok","admissible"}, 2),
	BAD (new String[]{"bad","malo"}, 4),
	VERY_BAD (new String[]{"very bad","muy malo"}, 7);
	
	private String[] words;
	private int score;
	
	PollutionLevelsEnum(String[] words, int score){
		this.words = words;
		//sort the words
		java.util.Arrays.sort(words, String.CASE_INSENSITIVE_ORDER);
		this.score = score;
	}
	
	public int getScore(){
		return score;
	}
	
	public boolean isWord(String word){
		return java.util.Arrays.binarySearch(words,word,String.CASE_INSENSITIVE_ORDER)>0;
	}
	
	public static PollutionLevelsEnum getPollutionLevelForWord(String word) throws NotAPollutionLevelException{
		PollutionLevelsEnum[] val = values();
		for(int i=0;i<val.length;i++){
			if(val[i].isWord(word))
				return val[i];
		}
		throw new NotAPollutionLevelException(word);
			
	}
}
