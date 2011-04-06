package MimodekV2;

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

import processing.core.PApplet;

// TODO: Auto-generated Javadoc
/**
 * The Class LSystem.
 */
public class LSystem {
	
	/** The command index. */
	int commandIndex = 0;
	
	/** The command str. */
	String commandStr = "ab";
	
	/** The app. */
	PApplet app;

	/** The create eatable counter. */
	int createEatableCounter = 0;

	/** The time since last food. */
	long timeSinceLastFood = 0;

	/**
	 * Instantiates a new l system.
	 *
	 * @param seed the seed
	 * @param app the app
	 */
	public LSystem(String seed, PApplet app) {
		commandStr = seed;
		this.app = app;
		timeSinceLastFood = System.currentTimeMillis();
	}

	/**
	 * Command a.
	 *
	 * @return the int
	 */
	private int commandA() {
		float ratio = Mimodek.aCells.size() > 0 ? (float) Mimodek.bCells.size()
				/ (float) Mimodek.aCells.size() : 0f;
		//System.out.println(ratio);
		if (ratio >= 1.25f) {
			CellA nuC = CellA.addCellA(app);
			//TODO: Un-comment if new animation doesn't work
			if (nuC != null) {
				Mimodek.growingCells.add(nuC);
				Mimodek.aCells.add(nuC);
				Mimodek.theCells.add(nuC);
			} else {
				for (int i = 0; i < Mimodek.bCells.size(); i++) {
					// make one bCell eatable
					if (!Mimodek.bCells.get(i).eatable
							&& Mimodek.bCells.get(i).currentMaturity >= 1f
							&& Mimodek.bCells.get(i).creatureA == null
							&& Mimodek.bCells.get(i).creatureB == null) {
						Mimodek.bCells.get(i).setEatable();
						break;
					}
				}
				return -1;
			}
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * Command b.
	 */
	private void commandB() {
		CellB nuC = CellB.addCellB(app);
		if (nuC != null) {
			Mimodek.growingCells.add(nuC);
			Mimodek.bCells.add(nuC);
			Mimodek.theCells.add(nuC);
			createEatableCounter++;
			// TODO: arbitrary values
			if (createEatableCounter == 3) {
				nuC.setEatable();
				createEatableCounter = 0;
			}
		} else {
			float ratio = Mimodek.aCells.size() > 0 ? (float) Mimodek.bCells
					.size()
					/ (float) Mimodek.aCells.size() : 0f;
			if (ratio >= 1.5f) {
				for (int i = 0; i < Mimodek.bCells.size(); i++) {
					// make one bCell eatable
					if (!Mimodek.bCells.get(i).eatable
							&& Mimodek.bCells.get(i).currentMaturity >= 1f
							&& Mimodek.bCells.get(i).creatureA == null
							&& Mimodek.bCells.get(i).creatureB == null) {
						Mimodek.bCells.get(i).setEatable();
						break;
					}
				}
			}
		}
	}

	/**
	 * Regenerate.
	 */
	private void regenerate() {
		if (commandStr.length() >= 256) {
			commandStr = "ab";
		} else {
			StringBuilder nuCmd = new StringBuilder();  //More effective when constructing strings in loops
			for (int i = 0; i < commandStr.length(); i++) {
				char c = commandStr.charAt(i);
				switch (c) {
				case 'a':
					nuCmd.append(Math.random() >= 0.5 ? "b" : "ab");
					break;
				case 'b':
					nuCmd.append(Math.random() >= 0.5 ? "aa" : "ab");
					break;
				}
			}
			commandStr = nuCmd.toString();
		}
		commandIndex = 0;
	}

	/**
	 * Adds the food.
	 */
	public void addFood() {
		long elapsedTime = System.currentTimeMillis() - timeSinceLastFood;
		// if it has been more than 5 seconds, create a creature
		if (elapsedTime > 5 * 1000) {
			Creature.createCreature();
		}
		timeSinceLastFood = System.currentTimeMillis();
		if (Mimodek.growingCells.size() > 0) {
			Cell cell = Mimodek.growingCells.get((int) (float) Math.random()
					* (Mimodek.growingCells.size()));
			cell.maturity += 0.1;
			if (cell.maturity >= 1.0f) {
				cell.maturity = 1.0f;
				Mimodek.growingCells.remove(cell);
			}
		} else {
			if (commandIndex >= commandStr.length()) {
				regenerate();
			}
			char c = commandStr.charAt(commandIndex);
			switch (c) {
			case 'a':
//				Verbose.debug("Command A");
				int t = commandA();
				if (t == 1) {
					commandIndex++;
					break;
				} else if (t < 0) {
					System.out.println("Missed.");
					Creature.goEatSomeSoftCell();
					break;
				}
			case 'b':
//				Verbose.debug("Command B");
				commandB();
				commandIndex++;
				break;
			}
		}
	}
}
