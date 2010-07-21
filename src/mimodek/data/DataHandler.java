package mimodek.data;

import java.util.HashMap;
import p5wp.WPMessageListener;
import p5wp.XMLReceiver;

import mimodek.configuration.Colors;
import mimodek.configuration.Configurator;
import mimodek.utils.Verbose;

public class DataHandler implements WPMessageListener, Runnable {
	Thread runner = null;
	boolean run = true;
	Colors temperatureColors;

	float lastTemperature = 0;
	PollutionLevelsEnum lastPollutionScore = PollutionLevelsEnum.ACCEPTABLE;

	public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";

	XMLReceiver xmlReceiver;

	// http://
	public DataHandler(Colors temperatureColors, XMLReceiver xmlReceiver) {
		this.temperatureColors = temperatureColors;
		this.xmlReceiver = xmlReceiver;
		xmlReceiver.setListener(this);
		Configurator.setSettingIfNotSet("pollutionScore", lastPollutionScore
				.name());
		Configurator.setSettingIfNotSet("temperatureColor", temperatureColors
				.getColorFromRange(lastTemperature));
		runner = new Thread(this);
		runner.start();
	}

	public void disconnect() {
		if (runner.isAlive())
			run = false;
	}

	private synchronized void setPollutionScore(PollutionLevelsEnum score) {
		lastPollutionScore = score;
		Configurator.setSetting("pollutionScore", lastPollutionScore.name());
	}

	private synchronized void setTemperature(float temp) {
		lastTemperature = temp;
		Configurator.setSetting("lastTemperature", temp);
		Configurator.setSetting("temperatureColor", temperatureColors
				.getColorFromRange(lastTemperature));
	}

	public void onResponse(HashMap messages) {
		/*
		 * <item name='NO2'>Bueno</item> <item name='CO'>Bueno</item> <item
		 * name='SO2'>Bueno</item> <item name='particles'>Bueno</item> <item
		 * name='03'>Admisible</item> <item name='temperature'>17.4</item> <item
		 * name='rain'>0.0</item>
		 */
		//Verbose.overRule("Last data received at : " + Verbose.now());
		PollutionLevelsEnum[] pollutionData = new PollutionLevelsEnum[5];
		int c = 0;

		String[] keys = (String[]) messages.keySet().toArray(new String[0]);
		for (int i = 0; i < keys.length; i++) {
			try {
				if (keys[i].equalsIgnoreCase("NO2")) {
					pollutionData[c++] = PollutionLevelsEnum
							.getPollutionLevelForWord((String) messages
									.get(keys[i]));
				} else if (keys[i].equalsIgnoreCase("CO")) {
					pollutionData[c++] = PollutionLevelsEnum
							.getPollutionLevelForWord((String) messages
									.get(keys[i]));	
				} else if (keys[i].equalsIgnoreCase("SO2")) {
					pollutionData[c++] = PollutionLevelsEnum
							.getPollutionLevelForWord((String) messages
									.get(keys[i]));
				} else if (keys[i].equalsIgnoreCase("particles")) {
					pollutionData[c++] = PollutionLevelsEnum
							.getPollutionLevelForWord((String) messages
									.get(keys[i]));
				} else if (keys[i].equalsIgnoreCase("03")) {
					pollutionData[c++] = PollutionLevelsEnum
							.getPollutionLevelForWord((String) messages
									.get(keys[i]));
				} else if (keys[i].equalsIgnoreCase("temperature")) {
					setTemperature(new Float((String) messages.get(keys[i])));
					//Verbose.overRule("Temperature color set.");
				} else if (keys[i].equalsIgnoreCase("rain")) {
					//
				}
			} catch (NotAPollutionLevelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//Verbose.overRule(keys[i] + ": " + messages.get(keys[i]));
		}
		if (c == pollutionData.length) {
			setPollutionScore(PollutionLevelsEnum
					.calculatePollutionScore(pollutionData));
			//Verbose.overRule("Pollution level set.");
		}

	}

	public void run() {
		while (run) {
			xmlReceiver.getFreshData("gatopan");
			try {
				Thread.sleep(2 * 60 * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public Colors getTemperatureColors() {
		return temperatureColors;
	}
}
