package cz.versarius.xchords;

/**
 * Possible states of string mark.
 * 
 * OK - pressed normally
 * DISABLED - this string should not be played
 * OPTIONAL - this string may be pressed, if you want, may sound better
 * OPEN - this string is not pressed at all
 * 
 * It is possible to create chord only with OK states, OPEN states will be calculated.
 * 
 * @author miira
 *
 */
public enum StringState {
	OK("ok"), DISABLED("no"), OPTIONAL("opt"), OPEN("open");

	private String value;

	private StringState(String val) {
		setValue(val);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public static StringState fromValue(String value) {
		if (value != null) {
			for (StringState s : StringState.values()) {
				if (value.equalsIgnoreCase(s.value)) {
					return s;
				}
			}
		}
		return null;
	}
}
