-=¡ Mimodek setup and run instructions ¡=-
Last update:
Wednesday the 6th of April 2011

Licence:
Attribution-ShareAlike 3.0 Unported (see Licence.txt for more details or http://creativecommons.org/licenses/by-sa/3.0/)
Copyright (C) 2010 Mimodek team

About:

Mimodek is an interactive installation based on an idea proposed by Marie Polakova for Media Lab Prado (Madrid, Spain) OpenUp workshop in February 2010
and developed by Jonathan 'Jonsku' Cremieux and Marie Polakova.

Mimodek        : http://mimodek.medialab-prado.es/
Media lab Prado: http://medialab-prado.es/
Marie Polakova : http://marura.wordpress.com/
Jonsku         : http://j-u-t-t-u.net/

Files hierarchy:
MimodekV2                             		- base folder
MimodekV2/ReadMe.txt                  		- This file
MimodekV2/Licence.txt                 		- Attribution-ShareAlike 3.0 Unported
MimodekV2/settings/settings.xml  		- The settings file
MimodekV2/settings/MimodekColourRanges.txt 	- A color range definition file
MimodekV2/library/                    		- contains the packaged binary JAVA classes that make up Mimodek and some 3rd party libraries
MimodekV2/sketch/Runner/              		- contains the Processing sketch used to run Mimodek
MimodekV2/sketch/Runner/ReadMe.txt    		- This file
MimodekV2/sketch/Runner/Runner.pde    		- The Processing sketch that runs Mimodek
MimodekV2/cache/        	      		- A cache folder
MimodekV2/screen_shots/		      		- this is where screen shots from Mimodek are stored
MimodekV2/textures/				- contains image files used as textures


Setup:
Mimodek is packaged as a Processing library and comes with an associated Processing sketch.
To install Mimodek, simply copy the MimodekV2 folder in the libraries folder of your Processing sketch book folder.
(Check from Processing preferences if you don't where it is).

Check out : http://www.learningprocessing.com/tutorials/libraries/ if you are having trouble with this step.

Prerequisites:
Processing Version 1.0 or later.

The following Processing libraries need to be installed before running Mimodek:
- ControlP5 (http://www.sojamo.de/libraries/controlP5/)
- Tuio (http://www.tuio.org/?processing)

Your system needs to support OpenGL and your graphics hardware should support at least 2 texture units.
If you get weird error messages related to OpenGL, make sure your system is set up properly (Google search it!).
On most relatively recent hardware it shouldn't be a problem.

You will need a properly setup connection to Internet to use the on location weather data feature.

Running Mimodek:
Open the Runner.pde sketch like any regular Processing sketch. To run full screen select 'Present' from the Sketch menu else just press the run button.
Note that Mimodek is initially paused. you have to start it by clicking the '>' button in the Controls window, tab 'Default'.
Read the section of this file about settings. It contains important information about doing different things with Mimodek.

The keyboards commands are:
's': Save settings in a new file 'settings_saved_#.xml'.
'u': Update settings file (overwrite or create settings.xml) with the current settings.
'l' : Reload (or load if the file was not present at startup) settings from 'settings.xml'.
'p': Show/Hide the pheromones trails.

Mouse controls:
The only thing you can do with the mouse is drawing food flakes on Mimodek. To do so drag the mouse while holding the left button. 

The User Interface (UI):
It lets you control different parameters of Mimodek environment and look. Most controller are in sync with a setting parameter so you are able to save your changes
into a settings file (see keyboard commands). The best way to get to know the UI is to play around with the controllers, but you should be aware of the following:
- Sometimes (or always?) you will have to give focus to the main Mimodek window before being able to use the UI.
- If you close the UI window, then it is lost! You'll have to start Mimodek again to get a new window. (This could be something to improve)
- The default tab show you information about the current state of Mimodek. You might need to give focus to the window to refresh the display.
- In the default tab you can also control the execution of Mimodek (|| or > button). You can also reset the simulation by pressing Reset.
- The graph in the default tab shows the evolution of the creatures population over time.
- When Autofood is enabled, it drops flake of food randomly every frame.
NOTE:
In the data tab, all the sliders except for Refresh Time won't do anything if the setting "FAKE_DATA_FLAG" is set to false. However the sliders will be updated to show the last readings.

Enabling tracking:
Mimodek receives tracking information using the TUIO protocol (http://www.tuio.org/).
Mimodek listen to TUIO OSC message on port 3333.
Refer to the documentation of your tracking application to set it up accordingly.
If you need more details about TUIO in Mimodek, please refer to the source code and the code documentation available on GitHub at
http://github.com/mimodek.

Settings:
Have a look inside settings.xml for a detailed description of all the different settings parameters.

Type of parameter values:
IMPORTANT: When editing the values of the parameters, be aware of the type. You know what type is associated to a parameter by looking at the last
part of its name:
The type is boolean (authorized values true or false) when the name ends with FLAG like in DEBUG_FLAG.
The type is string (any string starting with a letter like MyFile0) when the name ends with STR like in MESSAGE_BOARD_TEXTURE_STR.
The type is integer (number without a fractional or decimal component: 1, -10, 890767 but not 0.98 or -8978.0) when the name ends with INT like in CELLA_MAX_TRY_INT.
Otherwise the type is float (number with a fractional or decimal component: 1.0, -2.0, 0.87 but not 1 or 2 or -878).

Return to default settings:
In case you erase settings.xml, or if you want to reset Mimodek to its default settings, remove settings.xml from the sketch folder
and press 'u' on your keyboard after starting Mimodek.

Useful parameters combinations:
Following are some possible settings to perfomr certain tasks. Feel free to explore the settings and have a look at the source code
to know more about them.

1)Outputting a sequence of images out of Mimodek:
Edit the sketch (Runner.pde) so that its size match the intended image dimensions (e.g. for PAL size(720,576,OPENGL)).
IMPORTANT: Leave the third parameter to OPENGL.
In settings.xml set:
"FACADE_TYPE_INT" to 2 so Mimodek will be shown in the entire window.
"HIGH_RESOLUTION_SCALE" to 1.0 so that the images will be exactly the size you specified in the sketch.
"GLOBAL_SCALING" to 1.0 not compulsory, but it is nicer.
"AUTO_START_FLAG" to true if you want Mimodek animation to start immediately. If false you'll have to press the play button in the UI.
"FAKE_DATA_FLAG" to true if you'd like to control the weather condition and the air quality value yourself.
"MESSAGE_BOARD_FREQUENCY" to 0.0 to disable message board (unless you want it of course).
"POST_PICTURES_FLAG" to false. This is necessary because posting and filming can not work together.
"FILMING_FLAG" to true. That makes Mimodek output all the frames as a sequence of PNG images in the screen_shots folder

2)Showing an image slide show:
This is useful to show messages while Mimodek is on display. Think of it as a rudimentary slideshow.
You can show more than one images but they have to be names in a certain way. Let's say you want to show n images, choose
a base name for them like 'my_message' and then number all the images from 1 to n. If n = 3: 'my_message1',...,'my_message3'.
Put all those images in the textures folder and then edit settings.xml like so:
"MESSAGE_BOARD_TEXTURE_STR" is the base name of the images. In our example it would be my_message.
"MESSAGE_BOARD_NUMBER_INT" how many images to display. In our example it would be 3.
"MESSAGE_BOARD_DURATION" how long one image is shown (not counting fade in/out time). In minutes, so 30 seconds = 0.5.
NOTE: The total duration of the slideshow will be a bit over MESSAGE_BOARD_NUMBER_INT * MESSAGE_BOARD_DURATION
"MESSAGE_BOARD_FADE_SPEED" how fast Mimodek fade in/out to/from the message board. 1.0 and fade in/out is disabled, 0.0 and the message board will never appear.
"MESSAGE_BOARD_FREQUENCY" is the time between 2 appearance of the messages. In minutes. 0.0 disables message board.

3)Settings the location and display:
"FACADE_TYPE_INT" controls the type of display. For now they are only 2 options:
- 1 to display on MediaLab Prado's media facade.
- 2 to display in the entire window. This is the settings you should use for more standard display hardware like screen or projectors.
"LOCATION_CITY_STR" the name of the city you are executing Mimodek in (e.g. Madrid).
"LOCATION_COUNTRY_STR" the name of the country you are executing Mimodek in (e.g. Spain).
NOTE: Remember to set" FAKE_DATA_FLAG" to false to let Mimodek find a weather station.
It could be useful to set DEBUG_FLAG to true so you can read from the console if Mimodek manages to find a weather station for you location.

4)Changing textures:
Images files that are going to be used as textures should be placed in the textures folder.
About images file format: Jpeg and PNG have been tested and are a safe choice. If you need  support for other file format, first try, then if doesn't work
you might have to implement this feature yourself!

The settings controlling which image files to use are the following:
NOTE: use the name of the image file without the trailing . and extension (e.g to use my_texture.png, set one of the parameters to my_texture)
"CELLA_MASK_STR"
"CELLA_TEXTURE_STR"
"CELLB_TEXTURE_STR"

Those settings can also be adjusted through the UI.



