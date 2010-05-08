package mimodek.data;

import java.util.HashMap;
import p5wp.WPMessageListener;
import p5wp.XMLReceiver;

import mimodek.configuration.Colors;
import mimodek.configuration.Configurator;
import mimodek.utils.Verbose;

public class DataHandler  implements WPMessageListener, Runnable{
	Thread runner = null;
	boolean run = true;
	Colors temperatureColors;
float lastTemperature = 0;
PollutionLevelsEnum lastPollutionScore = PollutionLevelsEnum.OK;

public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";


XMLReceiver xmlReceiver;

	public DataHandler(Colors temperatureColors, XMLReceiver xmlReceiver){
		this.temperatureColors = temperatureColors;
		this.xmlReceiver = xmlReceiver;
		xmlReceiver.setListener(this);
		Configurator.setSettingIfNotSet("pollutionScore", lastPollutionScore.name());
		Configurator.setSettingIfNotSet("temperatureColor", temperatureColors.getColorFromRange(lastTemperature));
		runner = new Thread(this);
		runner.start();
	}
	
	public void disconnect(){
		if(runner.isAlive())
			run = false;
	}

	private synchronized void setPollutionScore(PollutionLevelsEnum score){
		lastPollutionScore = score;
		Configurator.setSetting("pollutionScore", lastPollutionScore.name());
	}
	
	private synchronized void setTemperature(float temp){
		lastTemperature = temp;
		Configurator.setSetting("temperatureColor", temperatureColors.getColorFromRange(lastTemperature));
	}
	
	private PollutionLevelsEnum calculatePollutionScore(PollutionLevelsEnum[] pollutionLevels){
		int p = 0;
		for(int i=0;i<pollutionLevels.length;i++)
			p+=pollutionLevels[i].getScore();
		if(p<6)
			return PollutionLevelsEnum.GOOD;
		if(p<11)
			return PollutionLevelsEnum.OK;
		if(p<21)
			return PollutionLevelsEnum.BAD;
		return PollutionLevelsEnum.VERY_BAD;
	}

	public void onResponse(HashMap messages) {
		Verbose.overRule("Last data received at : "+Verbose.now());
		 String[] keys = (String[])messages.keySet().toArray(new String[0]);
		  for(int i=0;i<keys.length;i++){
		    Verbose.overRule(keys[i]+": "+messages.get(keys[i]));
		  }
		  Verbose.overRule("---");
	}

	public void run() {
		while(run){
			xmlReceiver.getFreshData("gatopan");
			try {
				Thread.sleep(2*60*1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
