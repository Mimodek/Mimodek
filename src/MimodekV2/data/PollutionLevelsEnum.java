package MimodekV2.data;

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

public enum PollutionLevelsEnum {
	GOOD (new String[]{"good","buena","bueno"}, 1,0, 6),
	ACCEPTABLE (new String[]{"ok","admissible","admisible","Admisible"}, 2,6, 2),
	BAD (new String[]{"bad","malo"}, 4,11, 7),
	VERY_BAD (new String[]{"very bad","muy malo"}, 7,21, 1);
	
	private String[] words;
	private int score;
	private int rangeStart;
	private int colorRange;
	
	PollutionLevelsEnum(String[] words, int score, int rangeStart, int colorRange){
		this.words = words;
		//sort the words
		java.util.Arrays.sort(words, String.CASE_INSENSITIVE_ORDER);
		this.rangeStart = rangeStart;
		this.score = score;
		this.colorRange = colorRange;
	}
	
	public int getScore(){
		return score;
	}
	
	public int getColorRange(){	
		return colorRange;
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
