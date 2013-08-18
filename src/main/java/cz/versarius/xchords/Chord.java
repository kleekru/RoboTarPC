package cz.versarius.xchords;

/**
 * These classes (Chord, StringInfo, StringState...) will be enhanced, this is just quick concept
 * @author miira
 *
 */
public class Chord {
	private String name;
	private StringInfo[] strings;

	public Chord() {
		strings = new StringInfo[6];
	}
	
	public StringInfo[] getStrings() {
		return strings;
	}

	public void setStrings(StringInfo[] strings) {
		this.strings = strings;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setString(int idx, StringInfo si) {
		strings[idx] = si;
	}
	
	public StringInfo getString(int idx) {
		return strings[idx];
	}
}
