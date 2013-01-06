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

import MimodekV2.config.Configurator;

// TODO: Auto-generated Javadoc
/**
 * The Class PollutionDataInterpolator.
 */
public class PollutionDataInterpolator extends DataInterpolator {
		
		/**
		 * Instantiates a new pollution data interpolator.
		 *
		 * @param dataHandler the data handler
		 */
		public PollutionDataInterpolator(DataHandler dataHandler) {
			super("DATA_POLLUTION", dataHandler);
		}
		
		/* (non-Javadoc)
		 * @see MimodekV2.data.DataInterpolator#update()
		 */
		@Override
		public void update(){
			lastUpdate = System.currentTimeMillis();
			lastValue = nextValue;
			PollutionLevelsEnum pollution = PollutionLevelsEnum.getPollutionLevelForScore(Configurator.getFloatSetting("DATA_POLLUTION"));
			//System.out.println("Pollution level: "+pollution); 
			nextValue = TemperatureColorRanges.getRandomTemperatureInRange(pollution.getColorRange());
			//System.out.println("Pollution to temperature: "+nextValue);
		}

	}

