package cz.versarius.xchords;

import java.util.List;

public class ChordLibrary {
	private List<Chord> chords;
	private String desc;
	
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public List<Chord> getChords() {
		return chords;
	}
	public void setChords(List<Chord> chords) {
		this.chords = chords;
	}
	
}
