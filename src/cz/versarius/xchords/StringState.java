package cz.versarius.xchords;

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
