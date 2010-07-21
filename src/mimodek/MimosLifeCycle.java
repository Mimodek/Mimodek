package mimodek;

import mimodek.configuration.Colors;
import mimodek.configuration.Configurator;
import mimodek.data.PollutionLevelsEnum;
import mimodek.decorator.ActiveMimo;
import mimodek.decorator.DeadMimo1;
import mimodek.decorator.DeadMimo2;
import mimodek.decorator.RandomWalker;
import mimodek.decorator.graphics.CircleDrawer;
import mimodek.decorator.graphics.ComboGraphicsDecorator;
import mimodek.decorator.graphics.GraphicsDecoratorEnum;
import mimodek.decorator.graphics.ImageDrawer;
import mimodek.decorator.graphics.LittleLight;
import mimodek.decorator.graphics.MetaBall;
import mimodek.decorator.graphics.MimodekObjectGraphicsDecorator;
import mimodek.decorator.graphics.NoImageException;
import mimodek.decorator.graphics.PolarDrawer;
import mimodek.decorator.graphics.RenderDrawer;
import mimodek.decorator.graphics.SeedGradientDrawer;
import mimodek.decorator.graphics.TextDrawer;
import mimodek.decorator.graphics.TextureDrawer;
import mimodek.tracking.TrackingInfo;
import mimodek.utils.Verbose;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class MimosLifeCycle {
	/**
	 * Creates an Active Mimo from tracking data
	 * 
	 * @param info
	 *            Tracking info to position the Active Mimo
	 */
	public static MimodekObjectGraphicsDecorator createActiveMimo(TrackingInfo info,Colors activeMimosColors) {
		ActiveMimo aM = new ActiveMimo(new SimpleMimo(new PVector(info.x,
				info.y)), info.id);

		aM.setDiameter(Configurator.getFloatSetting("mimosMinRadius"));
		aM.targetPos.x = info.x;
		aM.targetPos.y = info.y;

		// get a color from the colors pool
		int colour = activeMimosColors.getRandomIndividualColor();
		// get the current pollution level
		PollutionLevelsEnum pollutionLevel = PollutionLevelsEnum
				.valueOf(Configurator.getStringSetting("pollutionScore"));
		//
		MimodekObjectGraphicsDecorator decorated = decorateByPollutionLevel(aM,
				colour, pollutionLevel);

		Verbose.debug(decorated);
		return decorated;
	}
	
	private static MimodekObjectGraphicsDecorator decorateByPollutionLevel(
			ActiveMimo aM, int colour, PollutionLevelsEnum pollutionLevel) {
		GraphicsDecoratorEnum chosenDecorator = null;
		float paramA = 0;
		float paramB = 0;
		int iteration = 0;
		chosenDecorator = GraphicsDecoratorEnum.COMBO;// GraphicsDecoratorEnum.COMBO;
		MimodekObjectGraphicsDecorator primaryDecorator = null;
		switch (pollutionLevel) {
		case GOOD:
			primaryDecorator = new SeedGradientDrawer(aM, colour);
			break;
		case ACCEPTABLE:
			paramA = 20;
			paramB = 0;
			iteration = 427;
			primaryDecorator = new PolarDrawer(aM, colour, paramA, paramB,
					iteration);
			break;
		case BAD:
			paramA = 220;
			paramB = 0;
			iteration = 296;
			primaryDecorator = new PolarDrawer(aM, colour, paramA, paramB,
					iteration);
			break;
		case VERY_BAD:
			paramA = 240;
			paramB = 0;
			iteration = 482;
			primaryDecorator = new PolarDrawer(aM, colour, paramA, paramB,
					iteration);
			break;
		}

		switch (chosenDecorator) { // Configurator.getIntegerSetting("textureMode")
		case TEXTURE:
			return new TextureDrawer(aM, colour);
		case GENERATED:
			return new SeedGradientDrawer(aM, colour);
		case METABALL:
			return new MetaBall(aM, colour);
		case POLAR:
			return new PolarDrawer(aM, colour, paramA, paramB, iteration);
		case COMBO:
			MimodekObjectGraphicsDecorator secondaryDecorator = new MetaBall(
					aM, colour);
			return new ComboGraphicsDecorator(primaryDecorator,
					secondaryDecorator);
		case TEXT:
			return new TextDrawer(aM, colour, aM.id + "");
		case CIRCLE:
		default:
			return new CircleDrawer(aM, colour);
		}
	}
	
	/**
	 * Creates a Dead Mimo2 from an Active Mimo. Those Mimos are created when an
	 * Active Mimo gets out of the screen
	 * 
	 * @param aM
	 *            Active Mimo to use as a base for the Dead Mimo 2
	 * @param id
	 *            The id of the Active Mimo in the hashtable
	 */
	public static MimodekObjectGraphicsDecorator createDeadMimo2(MimodekObjectGraphicsDecorator toTransform, MimodekObject obj, PImage activeImage,
			int c, PApplet app) {
		
		DeadMimo2 dm2 = new DeadMimo2(obj, activeImage);

		MimodekObjectGraphicsDecorator decorated = null;

		switch (GraphicsDecoratorEnum.LIGHT) { // Configurator.getIntegerSetting("textureMode")
		case TEXTURE:
			decorated = new TextureDrawer(dm2, c);
			break;
		case IMAGE:
			try {
				decorated = new ImageDrawer(dm2, activeImage, c, app);
			} catch (NoImageException e) {
				return null;
			}
			break;
		case RENDER:
			try {
				decorated = new RenderDrawer(dm2, toTransform, app);
			} catch (NoImageException e) {
				return null;
			}
			break;
		case GENERATED:
			decorated = new SeedGradientDrawer(dm2, c);
			break;
		case METABALL:
			decorated = new MetaBall(dm2, c);
			break;
		case LIGHT:
			decorated = new LittleLight(dm2, c);
			break;
		case CIRCLE:
		default:
			decorated = new CircleDrawer(dm2, c);
		}
		Verbose.debug(decorated);
		return decorated;
	}
	
	/**
	 * Creates a Dead Mimo1 from an Active Mimo. Those Mimos are created when
	 * the tracking lose a blob
	 * 
	 * @param aM
	 *            Active Mimo to use as a base for the Dead Mimo 1
	 */
	public static MimodekObjectGraphicsDecorator createDeadMimo1(ActiveMimo aM, int c) {
		DeadMimo1 dM1 = new DeadMimo1(new RandomWalker(aM.getDecoratedObject(),
				0.5f, true));
		MimodekObjectGraphicsDecorator decorated = null;
		switch (GraphicsDecoratorEnum.LIGHT) { // Configurator.getIntegerSetting("textureMode")
		case TEXTURE:
			decorated = new TextureDrawer(dM1, c);
			break;
		case GENERATED:
			decorated = new SeedGradientDrawer(dM1, c);
			break;
		case METABALL:
			decorated = new MetaBall(dM1, c);
			break;
		case LIGHT:
			decorated = new LittleLight(dM1, c);
			break;
		case CIRCLE:
		default:
			decorated = new CircleDrawer(dM1, c);
		}
		Verbose.debug(decorated);
		return decorated;
		//deadMimos1.put(id, decorated);
	}
}
