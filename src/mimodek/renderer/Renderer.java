package mimodek.renderer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import mimodek.MimodekObject;
import mimodek.decorator.Cell;
import mimodek.decorator.DeadMimo2;
import mimodek.decorator.MimodekObjectDecorator;
import mimodek.decorator.graphics.DrawingData;
import mimodek.decorator.graphics.MimodekObjectGraphicsDecorator;
import mimodek.decorator.graphics.RenderDrawer;
import processing.core.PApplet;
import processing.core.PVector;
import processing.xml.XMLElement;

public class Renderer {

	public static Cell parseDLACell(XMLElement xmlElement, PApplet app)
			throws SecurityException, IllegalArgumentException,
			ClassNotFoundException, NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		boolean fixed = new Boolean(xmlElement.getAttribute("fixed"));
		MimodekObjectGraphicsDecorator gfxDecorator = parseGfxDecorator(
				xmlElement.getChild("GraphicDecorator"), app);
		DeadMimo2 deadMimo2 = new DeadMimo2(gfxDecorator.decoratedObject);
		
		RenderDrawer render = new RenderDrawer(deadMimo2,
				gfxDecorator, app);
		deadMimo2.render();
		//((MimodekObjectDecorator)gfxDecorator.decoratedObject).render();
		Cell cell = new Cell(render);
		cell.fixed = fixed;
		XMLElement neighboursElem = xmlElement.getChild("neighbours");
		int neighboursCount = neighboursElem.getChildCount();
		for (int i = 0; i < neighboursCount; i++) {
			Cell neighbour = parseDLACell(neighboursElem.getChild(i), app);
			cell.attach(neighbour);
		}
		System.out.println(cell);
		return cell;

	}

	public static MimodekObjectGraphicsDecorator parseGfxDecorator(
			XMLElement xmlElement, PApplet app) throws SecurityException,
			IllegalArgumentException, ClassNotFoundException,
			NoSuchMethodException, InstantiationException,
			IllegalAccessException, InvocationTargetException {

		String gfxDecoratorClassName = xmlElement
				.getStringAttribute("className");
		Class gfxDecoratorClass = Class.forName(gfxDecoratorClassName);

		MimodekObjectDecorator decorator = parseDecorator(xmlElement
				.getChild("Decorator"));
		// System.out.println(decorator);
		DrawingData drawingData = parseDarwingData(xmlElement
				.getChild("DrawingData"));

		// retrieve the parameters for the constructor
		XMLElement params = xmlElement.getChild("params");
		Class[] types = new Class[params.getChildCount()];
		Object[] values = new Object[params.getChildCount()];
		for (int i = 0; i < params.getChildCount(); i++) {
			XMLElement param = params.getChild(i);
			int pos = param.getIntAttribute("position") - 1;
			Class type = Class.forName(param.getStringAttribute("type"));
			types[pos] = type;
			if (pos == 0) {
				values[0] = decorator.decoratedObject;
			} else {
				Object v = type.getConstructor(new Class[] { String.class })
						.newInstance(param.getAttribute("value"));
				if (v instanceof Integer) {
					v = ((Integer) v).intValue();
					types[pos] = int.class;
				} else if (v instanceof Float) {
					v = ((Float) v).floatValue();
					types[pos] = float.class;
				}
				values[pos] = v;
			}
		}
		Constructor gfxDecoratorConstructor = gfxDecoratorClass
				.getConstructor(types);
		MimodekObjectGraphicsDecorator gfxDecorator = (MimodekObjectGraphicsDecorator) gfxDecoratorConstructor
				.newInstance(values);

		// int iteration = drawingData.getIteration();
		// drawingData.setIteration(0);
		gfxDecorator.getDrawingData().setIteration(drawingData.getIteration());
		// System.out.println("Iteration:"+iteration);
		// while(drawingData.getIteration()<iteration){
		gfxDecorator.render(app);
		// }
		return gfxDecorator;
	}

	public static DrawingData parseDarwingData(XMLElement xmlElement)
			throws ClassNotFoundException, SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		String drawingDataClassName = xmlElement
				.getStringAttribute("className");
		Class drawingDataClass = Class.forName(drawingDataClassName);
		int iteration = xmlElement.getIntAttribute("iteration");
		int color = xmlElement.getIntAttribute("color");
		DrawingData drawingData = null;
		if (xmlElement.hasAttribute("startColor")) {
			int startColor = xmlElement.getIntAttribute("startColor");
			Constructor drawingDataConstructor = drawingDataClass
					.getConstructor(new Class[] { int.class, int.class });
			drawingData = (DrawingData) drawingDataConstructor
					.newInstance(new Object[] { startColor, color });
		} else {
			System.out.println(drawingDataClassName);
			Constructor drawingDataConstructor = drawingDataClass
					.getConstructor(new Class[] { int.class });
			drawingData = (DrawingData) drawingDataConstructor
					.newInstance(new Object[] { color });
		}
		drawingData.setIteration(iteration);
		return drawingData;
	}

	public static MimodekObjectDecorator parseDecorator(XMLElement xmlElement)
			throws ClassNotFoundException, SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		String decoratorClassName = xmlElement.getStringAttribute("className");
		// System.out.println(decoratorClassName);
		Class decoratorClass = Class.forName(decoratorClassName);



		XMLElement decoratorElem = xmlElement.getChild("Decorator");
		if (decoratorElem != null) {
			return parseDecorator(decoratorElem);
		}
		// creates the MimodekObject
		XMLElement mimoObjectElem = xmlElement.getChild("MimoObject");
		// className="mimodek.SimpleMimo"posX="59.688038" posY="43.665977"
		// radius="40.792645"
		String mimoObjectClassName = mimoObjectElem
				.getStringAttribute("className");
		float posX = mimoObjectElem.getFloatAttribute("posX");
		float posY = mimoObjectElem.getFloatAttribute("posY");
		float radius = mimoObjectElem.getFloatAttribute("radius");
		// System.out.println("Radius:"+radius);
		Class mimoObjectClass = Class.forName(mimoObjectClassName);
		Constructor mimodekObjectConstructor = mimoObjectClass
				.getConstructor(new Class[] { PVector.class });
		MimodekObject mimodekObject = (MimodekObject) mimodekObjectConstructor
				.newInstance(new Object[] { new PVector(posX, posY) });
		mimodekObject.setDiameter(radius);

		System.out.println(mimodekObject);
		// now create the decorator
		Constructor decoratorConstructor = decoratorClass
				.getConstructor(new Class[] { MimodekObject.class });
		MimodekObjectDecorator decorator = (MimodekObjectDecorator) decoratorConstructor
				.newInstance(new Object[] { mimodekObject });
		return decorator;
	}
}
