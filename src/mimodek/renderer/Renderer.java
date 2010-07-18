package mimodek.renderer;

import java.lang.reflect.InvocationTargetException;

import mimodek.MimodekObject;
import mimodek.SimpleMimo;
import mimodek.configuration.Configurator;
import mimodek.decorator.Cell;
import mimodek.decorator.CellV2;
import mimodek.decorator.DeadMimo2;
import mimodek.decorator.MimodekObjectDecorator;
import mimodek.decorator.graphics.ComboGraphicsDecorator;
import mimodek.decorator.graphics.DrawingData;
import mimodek.decorator.graphics.MimodekObjectGraphicsDecorator;
import mimodek.decorator.graphics.NoImageException;
import mimodek.decorator.graphics.PolarDrawer;
import mimodek.decorator.graphics.RenderDrawer;
import mimodek.decorator.graphics.SeedGradientDrawer;
import mimodek.utils.Verbose;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import processing.xml.XMLElement;

public class Renderer {

	public static CellV2 parseDLACell(XMLElement xmlElement, PApplet app, CellV2 parent)
			throws SecurityException, IllegalArgumentException,
			ClassNotFoundException, NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		boolean fixed = new Boolean(xmlElement.getAttribute("fixed"));
		int color = new Integer(xmlElement.getAttribute("color"));
		XMLElement mimoElement = xmlElement.getChild("mimo");
		//x="172.4207" y="89.47441" diameter="8.5932255" iteration="427" paramA="20.0" paramB="0.0" angle="2.7437181"
		float posX = new Float(mimoElement.getAttribute("x"));
		float posY = new Float(mimoElement.getAttribute("y"));
		int iteration = new Integer(mimoElement.getAttribute("iteration"));
		float diameter = new Float(mimoElement.getAttribute("diameter"));
		float paramA = new Float(mimoElement.getAttribute("paramA"));
		float paramB = new Float(mimoElement.getAttribute("paramB"));
		
		float angle = new Float(mimoElement.getAttribute("angle"));
		
		SimpleMimo sMimo = new SimpleMimo(new PVector(posX,posY));
		
		sMimo.setDiameter(diameter);
		
		MimodekObjectGraphicsDecorator renderingInstance = null;
		DeadMimo2 dm2 = null;
		if(new Boolean(mimoElement.getAttribute("seedGradient"))){
			renderingInstance = new SeedGradientDrawer(sMimo,color);
		}else{
			
			
			app.println(iteration+","+paramA+","+paramB+","+diameter+","+angle);
			
			
			renderingInstance = new PolarDrawer(sMimo,color, paramA,paramB,iteration);
			((PolarDrawer)renderingInstance).angle = angle;
			
			
			
		}
		renderingInstance.getDrawingData().setIteration(iteration);
		renderingInstance.render(app);
		PImage img = renderingInstance.toImage(app);
		System.out.println(img.width+";"+img.height);
		
		dm2 = new DeadMimo2(sMimo, img);
		dm2.maxPoint = iteration;
		if(renderingInstance instanceof SeedGradientDrawer){
			dm2.seedGradient = true;
		}else{
			dm2.angle = angle;
			dm2.paramA = paramA;
			dm2.paramB = paramB;
		}
		
		CellV2 cell = null;
		try {
			if(parent == null){
				cell = new CellV2(dm2, app);
				cell.fixed = fixed;
			}else{
			 cell = new CellV2(dm2, app, parent);
			}
			cell.getDrawingData().setColor(color);
		
			//cell = new Cell(deadMimo2,app);
			cell.fixed = fixed;
			XMLElement neighboursElem = xmlElement.getChild("neighbours");
			int neighboursCount = neighboursElem.getChildCount();
			for (int i = 0; i < neighboursCount; i++) {
				CellV2 neighbour = parseDLACell(neighboursElem.getChild(i), app, cell);
				cell.attachedCells.add(neighbour);
			}
			Verbose.overRule(cell);
			return cell;
		} catch (NoImageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/*
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
		// Verbose.debug(decorator);
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
		// Verbose.debug("Iteration:"+iteration);
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
			Verbose.debug(drawingDataClassName);
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
		// Verbose.debug(decoratorClassName);
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
		// Verbose.debug("Radius:"+radius);
		Class mimoObjectClass = Class.forName(mimoObjectClassName);
		Constructor mimodekObjectConstructor = mimoObjectClass
				.getConstructor(new Class[] { PVector.class });
		MimodekObject mimodekObject = (MimodekObject) mimodekObjectConstructor
				.newInstance(new Object[] { new PVector(posX, posY) });
		mimodekObject.setDiameter(radius);

		Verbose.debug(mimodekObject);
		// now create the decorator
		Constructor decoratorConstructor = decoratorClass
				.getConstructor(new Class[] { MimodekObject.class });
		MimodekObjectDecorator decorator = (MimodekObjectDecorator) decoratorConstructor
				.newInstance(new Object[] { mimodekObject });
		return decorator;
	}*/
}
