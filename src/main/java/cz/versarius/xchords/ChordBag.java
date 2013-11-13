package cz.versarius.xchords;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Easy structure for managing several chords together.
 * It is assumed to be ordered alphabetically based on chord name.
 * 
 * @author miira
 *
 */
public class ChordBag {
	private Map<String, Chord> chords = new HashMap<String, Chord>();
	
	public ChordBag() {
	}

	public ChordBag(List<Chord> chords) {
		setChords(chords);
	}

	public ChordBag(ChordBag bag) {
		setChords((List<Chord>)bag.getChords());
	}
	public Collection<Chord> getChords() {
		return (Collection<Chord>)chords.values();
	}
	
	public void setChords(List<Chord> chords) {
		for (Chord chord : chords) {
			this.chords.put(chord.getName(), chord);
		}
	}
	
	public Chord findByName(String chordName) {
		return chords.get(chordName);
	}
	
	public boolean isEmpty() {
		return chords.isEmpty();
	}
	
	// currently - getAny.. the map<> should be treemap in future... and take really the first chord
	public Chord getFirst() {
		if (chords.isEmpty()) {
			return null;
		}
		return getChords().iterator().next();
	}
	
	public void add(Chord chord) {
		this.chords.put(chord.getName(), chord);
	}
}
