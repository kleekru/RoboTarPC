package ioio.RoboTar.PCconsole;

import java.util.Map;

import cz.versarius.xchords.ChordLibrary;

public class ChordManager {
	private Map<String, ChordLibrary> chordLibraries;

	public Map<String, ChordLibrary> getChordLibraries() {
		return chordLibraries;
	}

	public void setChordLibraries(Map<String, ChordLibrary> chordLibraries) {
		this.chordLibraries = chordLibraries;
	}
}
