package mimodek.data;

public enum PollutionLevelsEnum {
	GOOD (new String[]{"good","buena","bueno"}, 1,0),
	ACCEPTABLE (new String[]{"ok","admissible"}, 2,6),
	BAD (new String[]{"bad","malo"}, 4,11),
	VERY_BAD (new String[]{"very bad","muy malo"}, 7,21);
	
	private String[] words;
	private int score;
	private int rangeStart;
	
	PollutionLevelsEnum(String[] words, int score, int rangeStart){
		this.words = words;
		//sort the words
		java.util.Arrays.sort(words, String.CASE_INSENSITIVE_ORDER);
		this.rangeStart = rangeStart;
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
	
	public static PollutionLevelsEnum calculatePollutionScore(PollutionLevelsEnum[] pollutionLevels){
		int p = 0;
		for(int i=0;i<pollutionLevels.length;i++)
			p+=pollutionLevels[i].getScore();
		if(p<6)
			return PollutionLevelsEnum.GOOD;
		if(p<11)
			return PollutionLevelsEnum.ACCEPTABLE;
		if(p<21)
			return PollutionLevelsEnum.BAD;
		return PollutionLevelsEnum.VERY_BAD;
	}
	
	public int getScoreForPollutionLevel(){
		return rangeStart;
	}
	
	public static PollutionLevelsEnum getPollutionLevelForScore(float v){
		if(v<6)
			return PollutionLevelsEnum.GOOD;
		if(v<11)
			return PollutionLevelsEnum.ACCEPTABLE;
		if(v<21)
			return PollutionLevelsEnum.BAD;
		return PollutionLevelsEnum.VERY_BAD;
	}
}
