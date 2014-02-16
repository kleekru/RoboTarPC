package com.robotar.ui;

import cz.versarius.xchords.ChordManager;
import com.robotar.util.RoboTarPreferences;

/** 
 * Implementation of chord manager for PC version. 
 */
public class ChordManagerPC extends ChordManager {
	private RoboTarPreferences preferences;
	
	/** Save content of chord manager to preferences. */
	public void saveToPreferences() {
		preferences.setLibraries(getLibrariesList(getChordLibraries().values()));
		preferences.setChosenLibrary(getChosenLibrary());
	}
	
	/**
	 * Initialize to default chords and recent chord libraries.
	 */
	public synchronized void initialize(RoboTarPreferences preferences) {
		if (!initialized) {
			initialize();
			
			// load all other recent chord library files
			load(preferences.getLibraries(), preferences.getChosenLibrary());
			// TODO watch for problems when loading...
			initialized = true; // TODO what if neither file was found, is it initialized or not?
		}
	}

	public RoboTarPreferences getPreferences() {
		return preferences;
	}

	public void setPreferences(RoboTarPreferences preferences) {
		this.preferences = preferences;
	}

	public void setChosenLibrary(String chosenLibrary) {
		super.setChosenLibrary(chosenLibrary);
		saveToPreferences();
	}
}
