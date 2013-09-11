package ioio.robotar.pcconsole;

/**
 * Used pin numbers.
 *
 */
public class Pins {
	public final static int PEDAL_PIN = 34;

	// assuming 6 = e low, 5 = a, ...
	// which is 0, 1, ... as string number elsewhere in the code
	public final static int LED_PIN_61 = 6;
	public final static int LED_PIN_51 = 7;
	public final static int LED_PIN_41 = 8;
	public final static int LED_PIN_31 = 9;
	public final static int LED_PIN_21 = 10;
	public final static int LED_PIN_11 = 11;
	
	public final static int LED_PIN_62 = 12;
	public final static int LED_PIN_52 = 13;
	public final static int LED_PIN_42 = 14;
	public final static int LED_PIN_32 = 15;
	public final static int LED_PIN_22 = 16;
	public final static int LED_PIN_12 = 17;
	
	public final static int LED_PIN_63 = 18;
	public final static int LED_PIN_53 = 19;
	public final static int LED_PIN_43 = 20;
	public final static int LED_PIN_33 = 21;
	public final static int LED_PIN_23 = 22;
	public final static int LED_PIN_13 = 23;
	
	public final static int LED_PIN_64 = 24;
	public final static int LED_PIN_54 = 25;
	public final static int LED_PIN_44 = 26;
	public final static int LED_PIN_34 = 27;
	public final static int LED_PIN_24 = 28;
	public final static int LED_PIN_14 = 29;
	
	/**
	 * Implementation must match the numbering of the constants above!
	 * 
	 * @param stringNum 0..5
	 * @param fret 1..4
	 * @return
	 */
	public final static int getLEDPin(int stringNum, int fret) {
		return fret*6 + stringNum;
	}
}
