package cz.versarius.xsong;

import cz.versarius.xchords.Chord;

/**
 * Chord reference. 
 * position - above which character in line of text should be the chord placed
 * chordId - complete identification of chord (library-chordname)
 * chord - computed field, will be filled
 * 
 * @author miira
 *
 */
public class ChordRef {
	private int position;
	private String chordId;
	/**
	 * Chord object will be found in chord library, based on chordId, read from song XML.
	 */
	private transient Chord chord;
	
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
