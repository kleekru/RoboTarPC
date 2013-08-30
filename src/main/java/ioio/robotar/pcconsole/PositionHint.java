package ioio.robotar.pcconsole;

import cz.versarius.xsong.ChordRef;

public class PositionHint {
	private int offset;
	private int length;
	private int line;
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
