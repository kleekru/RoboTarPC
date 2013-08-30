package ioio.RoboTar.PCconsole;

import cz.versarius.xchords.Chord;
import cz.versarius.xchords.StringInfo;
import cz.versarius.xchords.StringState;

public class Chords {

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

	// what I meant
	/** Git test - Validate see new Chords transformation - Kevin... I'll take a look ok to remove**/
	public static int[] translate(Chord chord) {
		for (int i = 0; i < 6; i++) {
			StringInfo si = chord.getString(i);
			if (si.getState() == StringState.OK || si.getState() == StringState.OPEN) {
				int fret = si.getFret();
				// further processing...
			}
		}
		// just to compile
		return chordReceive;
	}
	
	public static int[] Chords(int lowEstringSend, int AstringSend, int DstringSend, int GstringSend, int BstringSend, int highEstringSend)
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
		Chords.lowEstringReceive = lowEstringReceive;
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
		Chords.highEstringReceive = highEstringReceive;
	}

	public static void setChordReceive(int[] chordReceive) {
		Chords.chordReceive = chordReceive;
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
		Chords.channelLowE = channelLowE;
	}

	public static void setChannelA(int channelA) {
		Chords.channelA = channelA;
	}

	public static void setChannelD(int channelD) {
		Chords.channelD = channelD;
	}

	public static void setChannelG(int channelG) {
		Chords.channelG = channelG;
	}

	public static void setChannelB(int channelB) {
		Chords.channelB = channelB;
	}

	public static void setChannelHighE(int channelHighE) {
		Chords.channelHighE = channelHighE;
	}

	public static float getLowEstringFloat() {
		return lowEstringPosition;
	}

	public static void setLowEstringFloat(float lowEstringFloat) {
		Chords.lowEstringPosition = lowEstringFloat;
	}

	public static float getLowEstringPosition() {
		return lowEstringPosition;
	}

	public static void setLowEstringPosition(float lowEstringPosition) {
		Chords.lowEstringPosition = lowEstringPosition;
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
		Chords.highEStringPosition = highEStringPosition;
	}

			
	
}// End Class
