package cz.versarius.xchords;

/**
 * Chord object itself.
 * 
 * These classes (Chord, StringInfo, StringState...) will be enhanced, this is
 * just quick concept
 * 
 * @author miira
 * 
 */
public class Chord {
	private String id;
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

	public static String getChordName(String chordId) {
		String[] arr = chordId.split("-");
		if (arr.length > 2) {
			// for multiple '-'s in the name of chord
			StringBuilder sb = new StringBuilder(5);
			sb.append(arr[1]);
			for (int i = 2; i < arr.length; i++) {
				sb.append("-");
				sb.append(arr[i]);
			}
			return sb.toString();
		} else {
			return arr[1];
		}
	}
	
	public static String getChordNameSimple(String text) {
		return text.split("-")[1];
	}

	public static String getLibraryName(String text) {
		return text.split("-")[0];
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public void setId(String library, String name) {
		StringBuilder sb = new StringBuilder(30);
		sb.append(library);
		sb.append("-");
		sb.append(name);
		this.id = sb.toString();
	}

	public String getPlainText() {
		StringBuilder sb = new StringBuilder(12);
		sb.append(name);
		sb.append(": ");
		for (int i = 0; i < 6; i++) {
			if (strings[i] != null) {
				sb.append(strings[i].getPlainText());
			} else {
				sb.append("0");
			}
		}
		return sb.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Chord) {
			Chord other = (Chord)obj;
			return (this.id.equalsIgnoreCase(other.getId()));
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}

	public String getLibrary() {
		return Chord.getLibraryName(id);
	}

}
