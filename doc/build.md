#build

## current IOIO preparation

download App-IOIO0400.zip from https://github.com/ytai/ioio/wiki/Downloads
extract into ioio0400
layout:
prj/RoboTarPC
prj/ioio0400/IOIOLib/target/pc/
prj/libs

edit build.xml (if it is there - only in github version, not in this package) (add upload target, see below)
otherwise create build.xml with this content

<?xml version="1.0" encoding="UTF-8"?>
<project basedir="." default="jar" name="IOIOLibPC">
	<target name="jar">
		<echo message="jarring"/>
		<mkdir dir="dist"/>
		<jar destfile="dist/ioiolibpc-0.4.0.jar" basedir="bin"/>
	</target>
</project>

you can also add this target - that puts the resulting ioio.lib into libs folder (where we defined our local gradle repository)
	<target name="upload" depends="jar">
		<copy file="dist/ioiolibpc-0.4.0.jar" todir="../../../../libs/" overwrite="true"/>
	</target>

run ant jar (or ant upload) and copy final ioiolibpc-0.4.0.jar into libs folder (gradle repo)

then you can build RoboTarPC


##A. mixed

yeah. that's the mixed build. i can see it in my env too.
[17.12.2013 1:23:07] Miroslav Mocek: 1. delete all in target subdirectory
[17.12.2013 1:23:19] Miroslav Mocek: 2. do Project - Clean (will build it)
[17.12.2013 1:23:44] Miroslav Mocek: 3. check the target/classes directory, it should have about 4 subfolders (cz, ioio, net, org)
[17.12.2013 1:24:00] Miroslav Mocek: 4. do mvn install now
[17.12.2013 1:24:08] Miroslav Mocek: 5. check the target/classes directory again
[17.12.2013 1:24:34] Miroslav Mocek: there should be added next directories - data, default-chords, defaults-songs, xsd
[17.12.2013 1:24:41] Kevin Krumwiede: yes for sources tab.
[17.12.2013 1:25:14] Miroslav Mocek: the app when running, wants the icons in target/classes/data (which is put there by maven only. not the eclipse any other process)
[17.12.2013 1:25:32] Miroslav Mocek: 6. if there is the data directory, run from eclipse - it should work


##B. normal

##C. gradle

##D. running from cmd line:

1. you have to build from eclipse
 
2. from cmd line, project root, run for the first time:
mvn dependency:copy-dependencies package
(without clean!, otherwise it will ruin step 1 :) )
it will create target/dependency folder and copy .jars, which we depend on to it. 
Next time you can use only 'mvn package', as long as the .jars remains in dependency folder.
 
3. manually copy content of IOIOLib/target/pc/bin to target/dependency. (it's folder ioio and all the tree hierarchy below, so it will look like target/dependency/ioio/lib/api... for instance)
 
4. manually copy forms-1.3.0 to the target/dependency folder

5. from cmd line, project root /target dir, run:
java -jar RoboTarIOIOforPCConsole-0.0.1-SNAPSHOT.jar


##E. izpack build

modify install.xml

create installation jar

	cd RoboTarPC
	gradle izPackCreateInstaller
	cd build/distributions/
	java -jar RoboTarPC-0.2-installer.jar

run after install:
	
	cd /Applications/RoboTar/
	./robotar.sh

	