-=¡ Mimodek setup and run instructions ¡=-
Last update:
Friday the 29th of October 2010

Licence:
Attribution-ShareAlike 3.0 Unported (see Licence.txt for more details or http://creativecommons.org/licenses/by-sa/3.0/)
Copyright (C) 2010  Mimodek team

About:

Mimodek is an interactive installation based on an idea proposed by Marie Polakova for Media Lab Prado (Madrid, Spain) OpenUp workshop in February 2010
and developed by Jonathan 'Jonsku' Cremieux and Marie Polakova.

Mimodek        : http://mimodek.medialab-prado.es/
Media lab Prado: http://medialab-prado.es/
Marie Polakova : http://marura.wordpress.com/
Jonsku         : http://j-u-t-t-u.net/

Files hierarchy:
MimodekV2                             - base folder
MimodekV2/ReadMe.txt                  - This file
MimodekV2/Licence.txt                 - Attribution-ShareAlike 3.0 Unported
MimodekV2/library/                    - contains the packaged binary JAVA classes that make up Mimodek and some 3rd party libraries
MimodekV2/sketch/Runner/              - contains the Processing sketch used to run Mimodek
MimodekV2/sketch/Runner/ReadMe.txt    - This file
MimodekV2/sketch/Runner/settings.xml  - The settings file
MimodekV2/sketch/Runner/MimodekColourRanges_edit.txt - A color range definition file
MimodekV2/sketch/Runner/Runner.pde    - The Processing sketch
MimodekV2/sketch/Runner/cache/        - A cache folder
MimodekV2/sketch/Runner/screen_shots/ - Sreen shots folder
MimodekV2/sketch/Runner/textures/     - Images file used as textures


Setup:
Mimodek is packaged as a Processing library and comes with an associated Processing sketch.
To install Mimodek, simply copy the MimodekV2 folder in the libraries folder of your Processing sketch book folder.
(Check from Processing preferences if you don't where it is).

Check out : http://www.learningprocessing.com/tutorials/libraries/ if you are having trouble with this step.

Prerequisites:
The following Processing libraries need to be installed before running Mimodek:
- ControlP5 (http://www.sojamo.de/libraries/controlP5/)
- Tuio (http://www.tuio.org/?processing)

Your system needs to support OpenGL and your graphics hardware should support at least 2 texture units.
If you get weird error messages related to OpenGL, make sure your system is set up properly (Google search it!).
On most relatively recent hardware it shouldn't be a problem.

Running Mimodek:
Open the Runner.pde sketch like any regular Processing sketch. To run full screen select 'Present' from the Sketch menu else just press the run button.
Note that Mimodek is initially paused. you have to start it by clicking the '>' button in the Controls window, tab 'Default'

Enabling tracking:
Mimodek receives tracking information using the TUIO protocol (http://www.tuio.org/).
Mimodek listen to TUIO OSC message on port 3333.
Refer to the documentation of your tracking application to set it up accordingly.
If you need more details about TUIO in Mimodek, please refer to the source code and the code documentation available on GitHub at
http://github.com/mimodek.

Settings:
Have a look inside settings.xml for a description of the different settings.

The keyboards commands are:
's': Save settings in a new file 'settings_saved_#.xml'.
'u': Update settings file (overwrite or create settings.xml) with the current settings.
'l' : Reload (or load if the file was not present at startup) settings from 'settings.xml'.
'p': Show/Hide the pheromones trails.



