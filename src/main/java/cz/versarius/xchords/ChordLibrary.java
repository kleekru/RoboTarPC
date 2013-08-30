package cz.versarius.xchords;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChordLibrary {
	private Map<String, Chord> chords = new HashMap<String, Chord>();
	private String desc;
	private String name;
	private String path;
	
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public List<Chord> getChords() {
		return (List)chords.values();
	}
	public void setChords(List<Chord> chords) {
		for (Chord chord : chords) {
			this.chords.put(chord.getName(), chord);
		}
	}
	
	public Chord findByName(String chordName) {
		return chords.get(chordName);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
}
