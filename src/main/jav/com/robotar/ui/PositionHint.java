package com.robotar.ui;

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
	
}
