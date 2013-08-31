package ioio.robotar.pcconsole;

import cz.versarius.xchords.Chord;
import cz.versarius.xchords.StringInfo;
import cz.versarius.xchords.StringState;

/**
 * Class for holding info for servos.
 * Can translate XChord into the values for RoboTar device.
 */
public class ServoSettings {
	public static final float NEUTRAL = 0.5f;
	public static final float MUTED = 0.8f;
	public static final float PRESSED_TOP_RIGHT = 1.5f;
	public static final float PRESSED_TOP_LEFT = 0.0f;
	public static final float PRESSED_BOTTOM_RIGHT = 0.0f;
	public static final float PRESSED_BOTTOM_LEFT = 1.5f;

	/** this is for top 3 string right - higher numbered fret, bottom 3 strings left - also higher numbered fret */
	public static final float PRESSED_HIGHER = 1.5f;
	/** exact opposite :) */
	public static final float PRESSED_LOWER = 0.0f;
	
	private int[] servos = new int[6];
	private float[] values = new float[6];
	
	/**
	 * Instantiates servos to neutral positions.
	 */
	public ServoSettings() {
		neutralPosition();
	}
	
	/**
	 * Instantiates servos to chord values.
	 * @param chord
	 */
	public ServoSettings(Chord chord) {
		for (int i = 0; i < 6; i++) {
			StringInfo si = chord.getString(i);
			if ((si == null) || (si.getState() == StringState.OPEN)) {
				// neutral position
				servos[i] = i*2;
				values[i] = NEUTRAL;
			} else if (si.getState() == StringState.OK || si.getState() == StringState.OPTIONAL) {
				// something is pressed
				int fret = si.getFret();
				
				// compute values for servos
				int servoNum = i*2 + ((fret - 1) / 2);
				float servoValue = compute(i, fret, servoNum);
				
				servos[i] = servoNum;
				values[i] = servoValue;
			} else if (si.getState() == StringState.DISABLED) {
				// muted
				servos[i] = i*2;
				values[i] = MUTED;
			}
		}
		// servo and values are set..
	}
	
	private void neutralPosition() {
		for (int i = 0; i < 6; i++) {
			servos[i] = i*2;
			values[i] = NEUTRAL;
		}
	}
	
	private float compute(int string, int fret, int servoNum) {
		  /* Top 3 strings right = 1.5, left = 0.0
         * Bottom 3 strings right = 0.0, left = 1.5
             */
		// higher also known as right one :)
		int higher = (fret - 1) % 2;
		if (higher == 1) {
			if (string < 3) {
				return PRESSED_TOP_RIGHT;
			} else {
				return PRESSED_BOTTOM_RIGHT;
			}
			//return PRESSED_HIGHER;
		} else {
			if (string < 3) {
				return PRESSED_TOP_LEFT;
			} else {
				return PRESSED_BOTTOM_LEFT;
			}
			//return PRESSED_LOWER;
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
	
	//////////TODO delete after validating, all works
	
	
	private static int lowEstringReceive;
	private static int AstringReceive;
	private static int DstringReceive;
	private static int GstringReceive;
	private static int BstringReceive;
	private static int highEstringReceive;
	private static int channelLowE;
	private static int channelA;
	private static int channelD;
	private static int channelG;
	private static int channelB;
	private static int channelHighE;
	private static float lowEstringPosition;
	private static float AStringPosition;
	private static float DStringPosition;
	private static float GStringPosition;
	private static float BStringPosition;
	private static float highEStringPosition;
	private static int[] chordReceive;
	
	public static int[] deprecated(int lowEstringSend, int AstringSend, int DstringSend, int GstringSend, int BstringSend, int highEstringSend)
		{
		
		if (lowEstringSend == 1 | lowEstringSend == 3) {
			lowEstringPosition=0.0f;
			}
			else if (lowEstringSend == 2 | lowEstringSend == 4) {
				lowEstringPosition = 1.5f;
			}
			else {
				lowEstringPosition = 0.5f;
			};
		
		if (AstringSend == 1 | AstringSend == 3) {
			AStringPosition=0.0f;
			}
			else if (AstringSend == 2 | AstringSend == 4) {
				AStringPosition = 1.5f;
			}
			else {
				AStringPosition = 0.5f;
			};
		
		if (DstringSend == 1 | DstringSend == 3) {
			DStringPosition=0.0f;
			}
			else if (DstringSend == 2 | DstringSend == 4) {
				DStringPosition = 1.5f;
			}
			else {
				DStringPosition = 0.5f;
			};
			
		if (GstringSend == 1 | GstringSend == 3) {
			GStringPosition=0.0f;
			}
			else if (GstringSend == 2 | GstringSend == 4) {
				GStringPosition = 1.5f;
			}
			else {
				GStringPosition = 0.5f;
			};
			
		if (BstringSend == 1 | BstringSend == 3) {
			BStringPosition=0.0f;
			}
			else if (BstringSend == 2 | BstringSend == 4) {
				BStringPosition = 1.5f;
			}
			else {
				BStringPosition = 0.5f;
			};
			
		if (highEstringSend == 1 | highEstringSend == 3) {
			highEStringPosition=0.0f;
			}
			else if (highEstringSend == 2 | highEstringSend == 4) {
				highEStringPosition = 1.5f;
			}
			else {
				highEStringPosition = 0.5f;
			};
		// End Chords Constructor

		chordReceive = new int[6];	
		chordReceive[0] = lowEstringReceive;
		chordReceive[1] = AstringReceive;
		chordReceive[2] = DstringReceive;
		chordReceive[3] = GstringReceive;
		chordReceive[4] = BstringReceive;
		chordReceive[5] = highEstringReceive;
		return chordReceive;
		
		} //End BuildChord method
	
	public static int getLowEstringReceive() {
		return lowEstringReceive;
	}

	public static int getAstringReceive() {
		return AstringReceive;
	}

	public static int getDstringReceive() {
		return DstringReceive;
	}

	public static int getGstringReceive() {
		return GstringReceive;
	}

	public static int getBstringReceive() {
		return BstringReceive;
	}

	public static int getHighEstringReceive() {
		return highEstringReceive;
	}

	public int[] getChordReceive() {
		return chordReceive;
	}

	public static void setLowEstringReceive(int lowEstringReceive) {
		ServoSettings.lowEstringReceive = lowEstringReceive;
	}

	public void setAstringReceive(int astringReceive) {
		AstringReceive = astringReceive;
	}

	public void setDstringReceive(int dstringReceive) {
		DstringReceive = dstringReceive;
	}

	public void setGstringReceive(int gstringReceive) {
		GstringReceive = gstringReceive;
	}

	public void setBstringReceive(int bstringReceive) {
		BstringReceive = bstringReceive;
	}

	public static void setHighEstringReceive(int highEstringReceive) {
		ServoSettings.highEstringReceive = highEstringReceive;
	}

	public static void setChordReceive(int[] chordReceive) {
		ServoSettings.chordReceive = chordReceive;
	}

	public static int getChannelLowE() {
		return channelLowE;
	}

	public static int getChannelA() {
		return channelA;
	}

	public static int getChannelD() {
		return channelD;
	}

	public static int getChannelG() {
		return channelG;
	}

	public static int getChannelB() {
		return channelB;
	}

	public static int getChannelHighE() {
		return channelHighE;
	}

	public static void setChannelLowE(int channelLowE) {
		ServoSettings.channelLowE = channelLowE;
	}

	public static void setChannelA(int channelA) {
		ServoSettings.channelA = channelA;
	}

	public static void setChannelD(int channelD) {
		ServoSettings.channelD = channelD;
	}

	public static void setChannelG(int channelG) {
		ServoSettings.channelG = channelG;
	}

	public static void setChannelB(int channelB) {
		ServoSettings.channelB = channelB;
	}

	public static void setChannelHighE(int channelHighE) {
		ServoSettings.channelHighE = channelHighE;
	}

	public static float getLowEstringFloat() {
		return lowEstringPosition;
	}

	public static void setLowEstringFloat(float lowEstringFloat) {
		ServoSettings.lowEstringPosition = lowEstringFloat;
	}

	public static float getLowEstringPosition() {
		return lowEstringPosition;
	}

	public static void setLowEstringPosition(float lowEstringPosition) {
		ServoSettings.lowEstringPosition = lowEstringPosition;
	}

	public static float getAStringPosition() {
		return AStringPosition;
	}

	public static float getDStringPosition() {
		return DStringPosition;
	}

	public static float getGStringPosition() {
		return GStringPosition;
	}

	public static float getBStringPosition() {
		return BStringPosition;
	}

	public static float getHighEStringPosition() {
		return highEStringPosition;
	}

	public static void setAStringPosition(float aStringPosition) {
		AStringPosition = aStringPosition;
	}

	public static void setDStringPosition(float dStringPosition) {
		DStringPosition = dStringPosition;
	}

	public static void setGStringPosition(float gStringPosition) {
		GStringPosition = gStringPosition;
	}

	public static void setBStringPosition(float bStringPosition) {
		BStringPosition = bStringPosition;
	}

	public static void setHighEStringPosition(float highEStringPosition) {
		ServoSettings.highEStringPosition = highEStringPosition;
	}

			
	
}// End Class
