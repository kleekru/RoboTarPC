package ioio.robotar.pcconsole;

import java.util.HashMap;
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

	public ChordLibrary findByName(String name) {
		return chordLibraries.get(name);
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
		// TODO watch for problems when loading...
		initialized = true;
	}
	
	private boolean loadLibrary(XMLChordLoader3 loader, String path) {
		ChordLibrary library = new ChordLibrary();
		loader.loadChords(ChordManager.class.getResourceAsStream(path), library);
		if (library.isEmpty()) {
			// TODO
			// log
			return false;
		}
		
		library.setName(Chord.getLibraryName(library.getFirst().getId()));
		library.setPath(path);
		chordLibraries.put(library.getName(), library);
		return true;
	}
	
}
