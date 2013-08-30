package cz.versarius.xsong;

import cz.versarius.xchords.Chord;

public class ChordRef {
	private int position;
	private String chordId;
	/**
	 * Chord object will be found in chord library, based on chordId, read from song XML.
	 */
	private Chord chord;
	
	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getChordId() {
		return chordId;
	}

	public void setChordId(String chordId) {
		this.chordId = chordId;
	}

	public Chord getChord() {
		return chord;
	}

	public void setChord(Chord chord) {
		this.chord = chord;
	}
	
}
