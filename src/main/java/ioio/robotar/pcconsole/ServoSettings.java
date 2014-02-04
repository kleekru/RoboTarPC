package ioio.robotar.pcconsole;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import cz.versarius.xchords.Chord;
import cz.versarius.xchords.StringInfo;
import cz.versarius.xchords.StringState;

/**
 * Class for holding info for servos.
 * Can translate XChord into the values for RoboTar device.
 */
public class ServoSettings {
	private static final Logger LOG = LoggerFactory.getLogger(RoboTarChordsPage.class);

	public static final float NEUTRAL = 1.0f;
	public static final float MUTED = 0.9f;
	public static final float PRESSED_TOP_RIGHT = 1.3f;
	public static final float PRESSED_TOP_LEFT = 0.7f;
	public static final float PRESSED_BOTTOM_RIGHT = 0.7f;
	public static final float PRESSED_BOTTOM_LEFT = 1.3f;

	/**
	 * fill these corrections for each servo
	 * they are numbered 0 to 11
	 * first value is neutral correction
	 * second value is muted correction
	 * third value is normal press correction for LEFT
	 * fourth value is normal press correction for RIGHT
	 * see compute() method for futher explanation of left/right
	 * this values are added to the original ones - at the top of this file.
	 * you can also use negative -> -0.1f, if you need
	 */
	private float CORRECTION[][] = { 
		{0.0f, 0.0f, 0.0f, 0.0f}, // servo 0
		{0.0f, 0.0f, 0.0f, 0.0f},
		{0.0f, 0.0f, 0.0f, 0.0f},
		{0.0f, 0.0f, 0.0f, 0.0f},
		{0.0f, 0.0f, 0.0f, 0.0f},
		{0.0f, 0.0f, 0.0f, 0.0f},
		{0.0f, 0.0f, 0.0f, 0.0f},
		{0.0f, 0.0f, 0.0f, 0.0f},
		{0.0f, 0.0f, 0.0f, 0.0f},
		{0.0f, 0.0f, 0.0f, 0.0f},
		{0.0f, 0.0f, 0.0f, 0.0f},
		{0.0f, 0.0f, 0.0f, 0.0f}, // servo 11
		};
	
	private int[] servos = new int[6];
	private float[] values = new float[6];
	
	/**
	 * Instantiates servos to initial positions.
	 * 
	 * BEWARE: constructor resets CORRECTIONS structure!!
	 * use the instance in mainFrame (RoboTarStartPage.servoSettings)
	 * and setInitialPosition() and setChord(chord) of ServoSettings.
	 */
	public ServoSettings() {
		setInitialPosition();
	}
	
	/**
	 * Instantiates servos to chord values.
	 * 
	 * TODO - Set up a page under a Utilities or Setup menu that allows each
	 * individual note to be "trimmed" in case adjustments are needed for different
	 * guitars (different guitars have different action on the strings/frets.
	 * A user can then set up a plus or minus value to the default that will be 
	 * saved permanantly or until the user selects a "reset to default" button. 
	 * While setting each alternate trim value, user should be able to test 
	 * the value by pushing the pedal and plucking the note that is being trimmed 
	 * to ensure the proper pressure is on the string.
	 * 
	 * BEWARE: constructor resets CORRECTIONS structure!!
	 * use the instance in mainFrame (RoboTarStartPage.servoSettings)
	 * and setInitialPosition() and setChord(chord) of ServoSettings.
	 * @param chord
	 */
	public ServoSettings(Chord chord) {
		setChord(chord);
	}
	
	/**
	 * everything is assumed to be set - either in neutral position or values from corrections dialog.
	 * only set servos based on this chord + corrections
	 * @param chord
	 */
	public void setChord(Chord chord) {
		for (int i = 0; i < 6; i++) {
			StringInfo si = chord.getString(i);
			if ((si == null) || (si.getState() == StringState.OPEN) || (si.getState() == null)
					|| ((si.getState() == StringState.OK ) && (si.getFret() == 0))) {
				// neutral position
				servos[i] = i*2;
				values[i] = roundFloat(NEUTRAL + CORRECTION[i*2][0]);
			} else if (si.getState() == StringState.OK || si.getState() == StringState.OPTIONAL) {
				if (si.getFret() == 0) {
					LOG.error("wrong value fret==0, chord name: {}, string name: {}", chord.getId(), si.getName());
				}
				// something is pressed and fret > 0
				int fret = si.getFret();
				
				// compute values for servos
				int servoNum = i*2 + ((fret - 1) / 2);
				float servoValue = compute(i, fret, servoNum);
				
				servos[i] = servoNum;
				values[i] = roundFloat(servoValue);
			} else if (si.getState() == StringState.DISABLED) {
				// muted
				servos[i] = i*2;
				values[i] = roundFloat(MUTED + CORRECTION[i*2][1]);
			}
		}
		// servo and values are set..
	}
	
	// TODO it may not work correctly because of float representation... 
	// if it does not work - rewrite to DecimalFormat usage...
	private float roundFloat(float val) {
		BigDecimal bd = new BigDecimal(Float.toString(val));
		bd = bd.setScale(3, BigDecimal.ROUND_HALF_UP);
		LOG.debug("val {} -> formatted {}", val, bd.floatValue());
		return bd.floatValue();
	}
	
	public void setCorrections(float[][] settings) {
		CORRECTION = settings;
	}
	
	public float[][] getCorrections() {
		return CORRECTION;
	}

	/**
	 * This method should prevent unwanted damage.
	 * @return true in case when something is set (not 0.0f - initial position) - probably adjusted
	 *         false in case nothing is set - the device was probably not adjusted
	 */
	public boolean isAnyCorrectionSet() {
		for (int i = 0; i < CORRECTION.length; i++) {
			for (int j = 0; j < CORRECTION[i].length; j++) {
				if (CORRECTION[i][j] != 0.0f) {
					return true;
				}
			}
		}
		return false;
	}
	
	public float getInitial(int servoNum) {
		return roundFloat(NEUTRAL + CORRECTION[servoNum][0]);
	}
	
	/** 
	 * it means - without chord. 
	 */
	public void setInitialPosition() {
		for (int i = 0; i < 6; i++) {
			servos[i] = i*2;
			values[i] = roundFloat(NEUTRAL + CORRECTION[i*2][0]);
		}
	}
	
	/* old way:
	 * Top 3 strings right = 1.5, left = 0.0
	 * Bottom 3 strings right = 0.0, left = 1.5
	 */
	private float compute(int string, int fret, int servoNum) {
		// higher means - closer to the body of guitar:
		// there are 4 frets, 1, 2, 3, 4. 4 is higher than 3, 2 is higher than 1
		int higher = (fret - 1) % 2;
		if (higher == 1) {
			// frets 2 or 4
			if (string < 3) {
				// e6, a, d
				return PRESSED_TOP_RIGHT + CORRECTION[servoNum][3];
			} else {
				// g, b, e1
				return PRESSED_BOTTOM_RIGHT + CORRECTION[servoNum][3];
			}
		} else {
			// fret 1 or 3
			if (string < 3) {
				// e6, a, d
				return PRESSED_TOP_LEFT + CORRECTION[servoNum][2];
			} else {
				// g, b, e1
				return PRESSED_BOTTOM_LEFT + CORRECTION[servoNum][2];
			}
		}
	}
	
	public String debugOutput() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		for (int i = 0; i < 6; i++) {
			sb.append("Servo: ").append(servos[i]);
			sb.append(", value: ").append(values[i]);
			sb.append("\n");
		}
		return sb.toString();
	}
	
	public int[] getServos() {
		return servos;
	}

	public void setServos(int[] servos) {
		this.servos = servos;
	}

	public float[] getValues() {
		return values;
	}

	public void setValues(float[] values) {
		this.values = values;
	}

	public static ServoSettings loadCorrectionsFrom(File file) {
		XMLSettingLoader loader = new XMLSettingLoader();
		try {
			// first try relative path from current working dir 
			// (where RoboTar was started - it may be location of .bat file, .jnlp file or project root (Eclipse run))
			LOG.info("loading corrections from file: {}", file.getAbsolutePath());
			return loader.load(new FileInputStream(file));
		} catch (IOException e) {
			LOG.error("loadfrom.ioexception", e);
		} catch (ParserConfigurationException e) {
			LOG.error("loadfrom.parseconfigurationexception", e);
		} catch (SAXException e) {
			LOG.error("loadfrom.saxexception", e);
		}
		// not found, try to load from user.home/.robotar/ folder
		try {
			// first try relative path from current working dir 
			// (where RoboTar was started - it may be location of .bat file, .jnlp file or project root (Eclipse run))
			String pathStr = System.getProperty("user.home") + File.separator 
					+ RoboTarStartPage.ROBOTAR_FOLDER + File.separator
					+ file.getPath();
			Path path = Paths.get(pathStr);
			LOG.info("loading corrections from file: {}", path.toFile().getAbsolutePath());
			return loader.load(new FileInputStream(path.toFile()));
		} catch (IOException e) {
			LOG.error("loadfrom.ioexception", e);
		} catch (ParserConfigurationException e) {
			LOG.error("loadfrom.parseconfigurationexception", e);
		} catch (SAXException e) {
			LOG.error("loadfrom.saxexception", e);
		}
		// if still nothing - create new empty settings - will generate warning at the startup
		LOG.info("cannot load corrections, creating new default, empty settings with neutral corrections");
		return new ServoSettings();
	}

	public static void saveCorrectionsAs(File file, ServoSettings sett) {
		XMLSettingSaver saver = new XMLSettingSaver();
		LOG.debug("saving corrections to file: {}", file.getAbsolutePath());
		saver.save(sett.getCorrections(), file);
	}
		
}// End Class
