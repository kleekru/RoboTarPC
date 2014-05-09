package com.robotar.ui;

import cz.versarius.xchords.Chord;
import cz.versarius.xsong.ChordRef;

public class PositionHint {
	/** absolute position in document */
	private int offset;
	/** length of the line, aka text part length. (currently the same as chord part length) */
	private int length;
	/** index of line. chords and lines share the same line index. starts with 0. */
	private int line;
	/** reference to chord */
	private ChordRef chordRef;
	
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public int getLine() {
		return line;
	}
	public void setLine(int line) {
		this.line = line;
	}
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	public ChordRef getChordRef() {
		return chordRef;
	}
	public void setChordRef(ChordRef ref) {
		this.chordRef = ref;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(30);
		sb.append("offset: ").append(offset);
		if (chordRef != null) {
			sb.append("chord: ").append(chordRef.getChordId());
		}
		return sb.toString();
	}
	@Override
	public int hashCode() {
		return line;
	}
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof PositionHint)) return false;
		PositionHint other = (PositionHint)obj;
		if (other.line == this.line && other.offset == this.offset) {
			if (other.chordRef != null && this.chordRef != null) {
				if ((other.chordRef.getPosition() == this.chordRef.getPosition())
					&& (other.chordRef.getChordId().equals(this.chordRef.getChordId()))) {
					// chord position hints are equal
					return true;
				}
			} else {
				// line position hints are equal
				return true;
			}
		}
		return false;
	}
	/**
	 * Get first position available after this CHORD hint.
	 * e.g.
	 * Em  
	 *    ^ this is first available position
	 *     
	 * @return -1 if not chord hint, 
	 */
	public int getOffsetAfter() {
		if (chordRef == null) {
			return -1;
		}
		int pos = offset;
		pos += Chord.getChordName(chordRef.getChordId()).length();
		pos++; // space
		
		return pos;
	}
}
