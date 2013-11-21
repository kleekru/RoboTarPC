package ioio.robotar.pcconsole;

import java.util.ArrayList;
import java.util.List;

public class PositionHints {
	private List<PositionHint> chords = new ArrayList<PositionHint>();
	private List<PositionHint> lines = new ArrayList<PositionHint>();
	private int currentChord;
	private int currentLine;
	
	public List<PositionHint> getChords() {
		return chords;
	}
	public void setChords(List<PositionHint> chords) {
		this.chords = chords;
	}
	public List<PositionHint> getLines() {
		return lines;
	}
	public void setLines(List<PositionHint> lines) {
		this.lines = lines;
	}
	public int getCurrentChord() {
		return currentChord;
	}
	public void setCurrentChord(int currentChord) {
		this.currentChord = currentChord;
	}
	public int getCurrentLine() {
		return currentLine;
	}
	public void setCurrentLine(int currentLine) {
		this.currentLine = currentLine;
	}
	public PositionHint getChordHint() {
		if (currentChord < 0) {
			return null;
		}
		return chords.get(currentChord);
	}
	public PositionHint getNextChordHint() {
		currentChord++;
		if (currentChord >= chords.size()) {
			currentChord--;
			return null;
		}
		return chords.get(currentChord);
	}
	public PositionHint getLineHint(PositionHint chordHint) {
		if (chordHint == null) {
			return null;
		}
		PositionHint lineHint = lines.get(currentLine);
		if (lineHint.getLine() != chordHint.getLine()) {
			currentLine++;
			lineHint = lines.get(currentLine);
		}
		return lineHint;
	}
	public int lookAhead(PositionHint lineHint) {
		int ahead = currentLine + 2;
		if (lines.size() <= ahead) {
			return -1;
		}
		return lines.get(ahead).getOffset();
	}
}
