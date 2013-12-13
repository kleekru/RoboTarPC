package ioio.robotar.pcconsole;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Per user preferences
 *
 */
public class RoboTarPreferences {
	private static final Logger LOG = LoggerFactory.getLogger(RoboTarPreferences.class);
	
	private String correctionsFile;
	// TODO add other preferences, recent files (chords, songs)...
	
	/**
	 * Loads and saves preferences.
	 * @param p
	 */
	protected RoboTarPreferences(Preferences p) {
		correctionsFile = p.get("correctionsFile", "corrections.xml");
		
		// after load, save them again (useful in first run, but not needed)
		update(p);
		flush(p);
	}
	
	public static RoboTarPreferences load() {
		Preferences up = Preferences.userRoot();
		Preferences p = up.node(RoboTarPreferences.class.getName());
		
		return new RoboTarPreferences(p);
	}
	
	public void save() {
		Preferences up = Preferences.userRoot();
		Preferences p = up.node(this.getClass().getName());
		update(p);
		flush(p);
	}

	private void update(Preferences p) {
		p.put("correctionsFile", correctionsFile);
		
	}
	
	private void flush(Preferences p) {
		try {
			p.flush();
		} catch (BackingStoreException e) {
			LOG.error("cannot save preferences", e);
		}
	}

	public String getCorrectionsFile() {
		return correctionsFile;
	}

	public void setCorrectionsFile(String correctionsFile) {
		this.correctionsFile = correctionsFile;
	}

}
