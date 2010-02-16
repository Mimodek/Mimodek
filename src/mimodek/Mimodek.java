package mimodek;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Mimodek {
	public static void showDisplaysList() {
		GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment
				.getLocalGraphicsEnvironment();

		GraphicsDevice[] devices = graphicsEnvironment.getScreenDevices();

		for (int cnt = 0; cnt < 1; cnt++) {
			System.out.println(devices[cnt]);
		}// end for loop
	}
	
	//TODO: make this safer
	public static GraphicsDevice getDisplay(int i) {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[i];
		
	}
	
	

	/*
	 * This allows the applet to run as a Java app
	 */
	public static void main(String args[]) {
		//
		showDisplaysList();
		
		// Create the frame this applet will run in
		Frame appletFrame = new Frame(
				"Mimodek : Structure Construction Simulator");
		
		// force the app to quit when window is closed
		appletFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				System.exit(0);
			}
		});
		// The frame needs a layout manager, use the GridLayout to maximize
		// the applet size to the frame.
		appletFrame.setLayout(new GridLayout(1, 0));
		
		//set the background color
		appletFrame.setBackground(new Color(0));

		// Create an instance of the applet
		Simulation1 myApplet = new Simulation1(1024, 768);
		myApplet.setParentFrame(appletFrame);
		myApplet.setGraphicsDevice(getDisplay(0));

		// Have to give the frame a size before it is visible
		appletFrame.setSize(Simulation1.screenWidth, Simulation1.screenHeight);

		// Make the frame appear on the screen. You should make the frame appear
		// before you call the applet's init method. On some Java
		// implementations,
		// some of the graphics information is not available until there is a
		// frame.
		// If your applet uses certain graphics functions like getGraphics() in
		// the
		// init method, it may fail unless there is a frame already created and
		// showing.
		appletFrame.setVisible(true);

		// Add the applet to the frame
		appletFrame.add(myApplet);

		// Initialize and start the applet
		myApplet.init();
		myApplet.start();

	}

}
