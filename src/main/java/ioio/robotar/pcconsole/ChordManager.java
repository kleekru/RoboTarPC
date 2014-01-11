package ioio.robotar.pcconsole;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.versarius.xchords.Chord;
import cz.versarius.xchords.ChordLibrary;

/**
 * Manages all chord libraries, used in RoboTar. 
 * Can load and save recent chord libraries to preferences.
 * Lazy initialization, loads chord libraries when first needed.
 */
public class ChordManager {
	private static final Logger LOG = LoggerFactory.getLogger(RoboTarChordsPage.class);

	/** name of default chord library for robotar - must match chord id! e.g. <chord id='robotar-C'..> */
	public static final String DEFAULT_ROBOTAR = "robotar";
	/** default prefix */
	public static final String USER_PREFIX = "user";
	
	private Map<String, ChordLibrary> chordLibraries = new HashMap<String, ChordLibrary>();
	private boolean initialized;
	private boolean errors;
	
	private String chosenLibrary;

	private RoboTarPreferences pref;
	
	private XMLChordLoader3 loader = new XMLChordLoader3();
	
	public Map<String, ChordLibrary> getChordLibraries() {
		return chordLibraries;
	}

	public void setChordLibraries(Map<String, ChordLibrary> chordLibraries) {
		this.chordLibraries = chordLibraries;
		this.initialized = true;
		// TODO check paths of libraries and set 'errors', if they exist? OR remove this method.
	}

	/**
	 * Find chord library in already loaded libraries by name.
	 * O(1)
	 * 
	 * @param name
	 * @return
	 */
	public ChordLibrary findByName(String name) {
		return chordLibraries.get(name);
	}
	
	/**
	 * Find library in loaded libraries by path.
	 * O(n)
	 * 
	 * @param path
	 * @return
	 */
	public ChordLibrary findByPath(String path) {
		Iterator<ChordLibrary> itLibs = chordLibraries.values().iterator();
		while (itLibs.hasNext()) {
			ChordLibrary lib = itLibs.next();
			if (lib.getPath().equals(path)) {
				return lib;
			}
		}
		return null;
	}
	
	/**
	 * Is chord manager instance initialized with chord libraries?
	 * @return
	 */
	public boolean isInitialized() {
		return initialized;
	}

	/**
	 * Was there some problem with loading of chords libraries?
	 * 
	 * @return
	 */
	public boolean isErrors() {
		return errors;
	}
	
	/**
	 * Initialize to default chords and recent chord libraries.
	 */
	public synchronized void initialize(RoboTarPreferences preferences) {
		// load default chords
		String result = loadLibrary("/default-chords/robotar-default.xml");
		if (result != null) {
			chosenLibrary = result;
		}
		// load all other recent chord library files
		loadRecent(preferences);
		// TODO watch for problems when loading...
		initialized = true; // TODO what if neither file was found, is it initialized or not?
	}
	
	/** Save content of chord manager to preferences. */
	public void saveRecent() {
		pref.setLibraries(getLibrariesList(chordLibraries.values()));
		pref.setChosenLibrary(chosenLibrary);
	}
	
	/** generate list of used chord libraries without robotar-default-chords. */
	public List<String> getLibrariesList(Collection<ChordLibrary> libs) {
		List<String> list = new ArrayList<String>();
		for (ChordLibrary chl : libs) {
			if (!DEFAULT_ROBOTAR.equals(chl.getName())) {
				list.add(chl.getPath());
			}
		}
		return list;
	}
	
	/** Load content of chord manager from preferences. */
	public void loadRecent(RoboTarPreferences pref) {
		this.pref = pref;
		for (String fileName : pref.getLibraries()) {
			String result = loadLibrary(new File(fileName));
			if (result == null) {
				errors = true;
			}
		}
		setChosenLibrary(pref.getChosenLibrary());
	}
	
	public boolean isNameAvailable(String libName) {
		Iterator<String> itNames = chordLibraries.keySet().iterator();
		while (itNames.hasNext()) {
			String name = itNames.next();
			if (name.equalsIgnoreCase(libName)) {
				return false;
			}
		}
		return true;
	}
	
	private String loadLibrary(XMLChordLoader3 loader, InputStream is, String path) {
		ChordLibrary library = new ChordLibrary();
		loader.loadChords(is, library);
		if (library.isEmpty()) {
			// log
			LOG.error("Chord library is empty. Path: {}", path);
			return null;
		}
		
		library.setName(Chord.getLibraryName(library.getFirst().getId()));
		library.setPath(path);
		// check already existing names
		if (isNameAvailable(library.getName())) {
			chordLibraries.put(library.getName(), library);
			LOG.info("Successfully loaded chord library: {}, path: {}", library.getName(), path);
			
			return library.getName();
		} else {
			LOG.error("Library with name: '{}' already exists in chord manager! Skipping: {}", library.getName(), path);
			return null;
		}
	}

	/** only for default robotar chords */
	public String loadLibrary(String path) {
		InputStream is = ChordManager.class.getResourceAsStream(path);
		if (is == null) {
			LOG.error("Chord library doesn't exist. local resource path: {}", path);
			return null;
		}
		String lib = loadLibrary(loader, is, path);
		if (lib != null) {
			return lib;
		}
		return null;
	}
	
	/** for all other chord libraries file */
	public String loadLibrary(File file) {
		try {
			// check, if it isn't already loaded in chord manager
			ChordLibrary lib = findByPath(file.getPath()); // ? or abs?
			String libName;
			if (lib == null) {
				// load it
				FileInputStream fis = new FileInputStream(file);
				libName = loadLibrary(loader, fis, file.getPath());
			} else {
				libName = lib.getName();
			}
			if (libName != null) {
				return libName;
			}
		} catch (FileNotFoundException e) {
			LOG.error("Chord library doesn't exist. file path: {}", file.getPath());
		}
		return null;
	}
	
	public String getChosenLibrary() {
		return chosenLibrary;
	}

	public void setChosenLibrary(String libraryName) {
		this.chosenLibrary = libraryName;
		saveRecent();
	}
}
