package MimodekV2.data;

// TODO: Auto-generated Javadoc
/*
This is the code source of Mimodek. When not stated otherwise,
it was written by Jonathan 'Jonsku' Cremieux<jonathan.cremieux@aalto.fi> in 2010. 
Copyright (C) yyyy  name of author

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

*/

/**
 * The Enum PollutionLevelsEnum.
 */
public enum PollutionLevelsEnum {
	
	/** The GOOD. */
	GOOD (new String[]{"good","buena","bueno"}, 1,0, 6),
	
	/** The ACCEPTABLE. */
	ACCEPTABLE (new String[]{"ok","admissible","admisible","Admisible"}, 2,6, 2),
	
	/** The BAD. */
	BAD (new String[]{"bad","malo"}, 4,11, 7),
	
	/** The VER y_ bad. */
	VERY_BAD (new String[]{"very bad","muy malo"}, 7,21, 1);
	
	/** The words. */
	private String[] words;
	
	/** The score. */
	private int score;
	
	/** The range start. */
	private int rangeStart;
	
	/** The color range. */
	private int colorRange;
	
	/**
	 * Instantiates a new pollution levels enum.
	 *
	 * @param words the words
	 * @param score the score
	 * @param rangeStart the range start
	 * @param colorRange the color range
	 */
	PollutionLevelsEnum(String[] words, int score, int rangeStart, int colorRange){
		this.words = words;
		//sort the words
		java.util.Arrays.sort(words, String.CASE_INSENSITIVE_ORDER);
		this.rangeStart = rangeStart;
		this.score = score;
		this.colorRange = colorRange;
	}
	
	/**
	 * Gets the score.
	 *
	 * @return the score
	 */
	public int getScore(){
		return score;
	}
	
	/**
	 * Gets the color range.
	 *
	 * @return the color range
	 */
	public int getColorRange(){	
		return colorRange;
	}
	
	/**
	 * Checks if is word.
	 *
	 * @param word the word
	 * @return true, if is word
	 */
	public boolean isWord(String word){
		return java.util.Arrays.binarySearch(words,word,String.CASE_INSENSITIVE_ORDER)>0;
	}
	
	/**
	 * Gets the pollution level for word.
	 *
	 * @param word the word
	 * @return the pollution level for word
	 * @throws NotAPollutionLevelException the not a pollution level exception
	 */
	public static PollutionLevelsEnum getPollutionLevelForWord(String word) throws NotAPollutionLevelException{
		PollutionLevelsEnum[] val = values();
		for(int i=0;i<val.length;i++){
			if(val[i].isWord(word))
				return val[i];
		}
		throw new NotAPollutionLevelException(word);
	}
	
	/**
	 * Calculate pollution score.
	 *
	 * @param pollutionLevels the pollution levels
	 * @return the pollution levels enum
	 */
	public static PollutionLevelsEnum calculatePollutionScore(PollutionLevelsEnum[] pollutionLevels){
		int p = 0;
		//add up all the scores
		for(int i=0;i<pollutionLevels.length;i++)
			p+=pollutionLevels[i].getScore();
		
		//decides which level it is
		if(p<6)
			return PollutionLevelsEnum.GOOD;
		if(p<11)
			return PollutionLevelsEnum.ACCEPTABLE;
		if(p<21)
			return PollutionLevelsEnum.BAD;
		return PollutionLevelsEnum.VERY_BAD;
	}
	
	/**
	 * Gets the score for pollution level.
	 *
	 * @return the score for pollution level
	 */
	public int getScoreForPollutionLevel(){
		return rangeStart;
	}
	
	/**
	 * Gets the pollution level for score.
	 *
	 * @param v the v
	 * @return the pollution level for score
	 */
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
