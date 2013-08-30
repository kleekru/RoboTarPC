package cz.versarius.xsong;

import java.util.List;

public class Line {
	private String text;
	private List<ChordRef> chords;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<ChordRef> getChords() {
		return chords;
	}

	public void setChords(List<ChordRef> chords) {
		this.chords = chords;
	}
}
