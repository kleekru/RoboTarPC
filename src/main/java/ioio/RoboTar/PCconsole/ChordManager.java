package ioio.RoboTar.PCconsole;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.versarius.xchords.Chord;
import cz.versarius.xchords.ChordLibrary;

public class ChordManager {
	private Map<String, ChordLibrary> chordLibraries = new HashMap<String, ChordLibrary>();
	private boolean initialized;
	
	public Map<String, ChordLibrary> getChordLibraries() {
		return chordLibraries;
	}

	public void setChordLibraries(Map<String, ChordLibrary> chordLibraries) {
		this.chordLibraries = chordLibraries;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}
	
	public synchronized void initialize() {
		XMLChordLoader3 loader = new XMLChordLoader3();
		loadLibrary(loader, "/default-chords/robotar-default.xml");
		initialized = true;
	}
	
	private void loadLibrary(XMLChordLoader3 loader, String path) {
		ChordLibrary library = new ChordLibrary();
		List<Chord> chords = loader.load(ChordManager.class.getResourceAsStream(path));
		library.setChords(chords);
		library.setName(Chord.getLibraryName(chords.get(0).getId()));
		library.setPath(path);
		chordLibraries.put(library.getName(), library);
	}
}
