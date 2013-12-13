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
 
 - java jdk 1.7, eclipse kepler, m2eclipse plugin (won't be needed in future)
 - in eclipse Import maven project - robotar
 - project properties add jar libraries from lib folder on Libraries tab
 - git clone https://github.com/ytai/ioio
 - import general project - ioio/software/IOIOlib/target as root dir, choose pc
 - in project properties of Robotar add Project dependency to IOIOLibPC
 - then mvn install from eclipse works - build success
 - copy corrections.template.xml to corrections.xml
 - then right click on project - run as - java application - should run
 
 - download android platform - not needed for pc version
 