package ioio.robotar.pcconsole;

import cz.versarius.xchords.Chord;
import cz.versarius.xchords.StringInfo;
import cz.versarius.xchords.StringState;

public class LEDSettings {
	private int[] leds = new int[6];
	
	public LEDSettings() {
		turnOffAll();
	}
	
	public LEDSettings(Chord chord) {
		for (int i = 0; i < 6; i++) {
			StringInfo si = chord.getString(i);
			if ((si == null) || (si.getState() == StringState.OPEN) || (si.getState() == null) || (si.getState() == StringState.DISABLED)) {
				// turn off
				leds[i] = 0;
			} else {
				// turn on appropriate fret
				// (si.getState() == StringState.OK || si.getState() == StringState.OPTIONAL)
				int fret = si.getFret();
				leds[i] = fret;
			}
		}
	}
	
	private void turnOffAll() {
		for (int i = 0; i < 6; i++) {
			leds[i] = 0;
		}
	}

	public int[] getLeds() {
		return leds;
	}

	public String debugOutput() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		for (int i = 0; i < 6; i++) {
			sb.append("String: ").append(i);
			sb.append(", LED: ").append(leds[i]);
			sb.append("\n");
		}
		return sb.toString();
	}
}
