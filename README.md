# RoboTarIOIOforPCConsole #
===========================

RoboTar robotic guitar assistant - Code for PC version

Update 4/22/2013:
This is a draft version of an app that will be usable by both PC and Android users.  iOS device users TBD.
The current version is unfinished but some fundimental pieces are in place.  The following functions are built in:
 - Ability to build a chord using radio buttons (TODO - Need to create ability to save the chord and add it to songs)
 - Ability to actuate a chord on the RoboTar device (drive servos) attached to 16 Channel PWM controller.
 - Ability turn the servo on and off with a physical pushbutton attached to the IOIO board (pin 34)
 - Interface to load an XML file into the memory of the app.  TODO - change formate to use XChords is in progress.
 - User Interface to select from a list of chords
 - User Interface button to test selected chord - TODO, does not actuate the chord yet.
 - User Interface button to start a New chord - simply clears the existing chord selection (TODO - needs to clear list)

TODO:
 - Need to create songs page that does the following:
 -    Allows user to build and save songs including song name and chords in the full sequence of how they occur in a song
 -    Explore: Save additional song attributes including artist and genere and create playlists?
 -    Ability to download songs in XML format and load into the songs UI.
 -    Ability to play the song 
 -      Should show current docked chord (will be on when user pushes the button or pedal)
 -      Should show the chord that is coming up
 -      Ideally, Should show the chords in the meter they will play and include the number of measures for 
 -        chord like realy sheet music.  If words are available, the words should be displayed with the actual 
 -        song the the appropriate timing.
 -    Need to add metronome and tuner (look for something already built to partner with).
 -    Need to build in better error handling:
 -      If IOIO is not connected, is wrong version or other problem with IOIO.
 -      If XML does not load
 -      If there is an error sending to RoboTar
 -      If there is a problem with a specific chord (in a song but not in the library).
 -    Need to look at performance over bluetooth vs. attached via USB.  So far Bluetooth might be too slow.

 ## how to install ##
 
 - prerequisities: java jdk 1.6, eclipse kepler
 
 	cd <to you projects directory> (e.g. `<HOME>/prj`)
	git clone https://github.com/miira/xchords.git
 	git clone https://github.com/miira/xsong.git
	git clone https://github.com/kleekru/RoboTarIOIO.git
	git clone https://github.com/kleekru/RoboTarIOIOforPCConsole.git
	git clone https://github.com/ytai/ioio
	cd RoboTarIOIOforPCConsole
 	cp corrrections.template.xml corrections.xml
	
 - start eclipse
 - (if you developed in eclipse already before, create new workspace - File->Switch Workspace->Other... and type location of new workspace - e.g. <HOME>/ws/robotar)
 - in eclipse File->Import... : General->Existing projects into workspace (click Next) Select root directory - choose your project's folder (<HOME>/prj) From the list of available projects, choose xchords, xsong and RoboTarIOIOforPCConsole robotar (click Finish)
 - (not needed probably: Right click on RoboTarIOIOforPCConsole project name in Package Explorer/OR Project->Properties - add all jar libraries from lib folder on Libraries tab)
 - then you'll need to import ioio
 - File->Import... : General->Existing projects into workspace - Select root directory - choose <HOME>/prj/ioio/software as root dir, select IOIOLibPC and import this project. (or similarly select <HOME>/prj/ioio/software/IOIOlib/target as root dir, choose pc)
 - in project properties of RobotarIOIOforPCConsole add Project dependency on IOIOLibPC (and xchords and xsong, if they are not already there)
 - Project->Clean (all projects)
 - then right click on RobotarIOIOforPCConsole project - run as - java application - should run
 - In RoboTar applications, choose Utilities -> Servo corrections and setup RoboTar device (if you have any), because running with inappropriate values could burn your servos! After changing and saving the values, you can try Chords page and Song page, testing chords individually or play/edit songs.
  
 - instructions for Android version - you need to clone also `https://github.com/kleekru/RoboTarAndroid.git`
 - download android platform (not needed for pc version), follow installation instructions for android developers (http://developer.android.com/sdk/index.html#ExistingIDE and http://developer.android.com/sdk/installing/index.html and http://developer.android.com/sdk/installing/installing-adt.html)
 