package cz.versarius.xchords;

/**
 * All needed info about a particular string.
 * 
 * @author miira
 *
 */
public class StringInfo {
	private StringState state;
	private int fret;
	private int low;
	private int high;
	private String finger;
	private String name;
	
	public String getFinger() {
		return finger;
	}

	public void setFinger(String finger) {
		this.finger = finger;
	}

	public int getHigh() {
		return high;
	}

	public void setHigh(int high) {
		this.high = high;
	}

	public int getLow() {
		return low;
	}

	public void setLow(int low) {
		this.low = low;
	}

	public int getFret() {
		return fret;
	}

	public void setFret(int fret) {
		this.fret = fret;
	}

	public StringState getState() {
		return state;
	}

	public void setState(StringState state) {
		this.state = state;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPlainText() {
		if (state == StringState.DISABLED) {
			return "X";
		}
		if (state == StringState.OPEN) {
			return "0";
		}
		return Integer.toString(fret, 10);
	}
}
