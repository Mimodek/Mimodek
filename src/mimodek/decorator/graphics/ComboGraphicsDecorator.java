package mimodek.decorator.graphics;

import mimodek.MimodekObject;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public class ComboGraphicsDecorator extends MimodekObjectGraphicsDecorator {
	MimodekObjectGraphicsDecorator primaryDecorator;
	MimodekObjectGraphicsDecorator secondaryDecorator;
	
	public ComboGraphicsDecorator(MimodekObject decoratedObject) {
		super(decoratedObject);
		// TODO Auto-generated constructor stub
	}
	
	public ComboGraphicsDecorator(MimodekObjectGraphicsDecorator primaryDecorator,MimodekObjectGraphicsDecorator secondaryDecorator) {
		super(primaryDecorator.decoratedObject);
		this.primaryDecorator = primaryDecorator;
		this.secondaryDecorator = secondaryDecorator;
		// TODO Auto-generated constructor stub
	}
	
	public MimodekObjectGraphicsDecorator getPrimaryDecorator(){
		return primaryDecorator;
	}
	
	public MimodekObjectGraphicsDecorator getSecondaryDecorator(){
		return secondaryDecorator;
	}

	@Override
	public void update(){
		secondaryDecorator.update();
		primaryDecorator.update();
	}
	
	@Override
	public void draw(PApplet app) {
		secondaryDecorator.draw(app);
		primaryDecorator.draw(app);
	}

	@Override
	public PImage toImage(PApplet app) {
		// TODO Auto-generated method stub
		return primaryDecorator.toImage(app);
	}

	@Override
	protected void draw(PGraphics gfx) {
		;
	}

}
