package ioio.robotar.pcconsole;

import java.util.ArrayList;
import java.util.List;

public class PositionHints {
	private List<PositionHint> chords = new ArrayList<PositionHint>();
	private List<PositionHint> lines = new ArrayList<PositionHint>();
	/** used for playing song */
	private int currentChord;
	/** used for playing song */
	private int currentLine;
	/** used for editing */
	private PositionHint lastSelectedChord;
	
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
	
	public PositionHint getNextChordHint(PositionHint chordHint) {
		int curr = chords.indexOf(chordHint);
		if (curr < 0 ) {
			return null;
		}
		curr++;
		if (curr == chords.size()) {
			return null;
		}
		return chords.get(curr);
	}
	
	public PositionHint getPrevChordHint(PositionHint chordHint) {
		int curr = chords.indexOf(chordHint);
		curr--;
		if (curr < 0) {
			return null;
		}
		return chords.get(curr);
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
	public int getLineOffset(PositionHint chordHint) {
		return lines.get(chordHint.getLine()).getOffset();
	}
	public int getPrevLineOffset(PositionHint chordHint, int min) {
		int lineidx = chordHint.getLine();
		if (lineidx == 0) {
			return min;
		}
		return lines.get(--lineidx).getOffset();
	}
	public int getNextLineOffset(PositionHint chordHint, int max) {
		int lineidx = chordHint.getLine();
		if (lineidx >= (lines.size() - 1)) {
			return max;
		}
		return lines.get(++lineidx).getOffset();
	}
	
	public int lookAhead(PositionHint lineHint) {
		int ahead = currentLine + 2;
		if (lines.size() <= ahead) {
			return -1;
		}
		return lines.get(ahead).getOffset();
	}
	public PositionHint getLastSelectedChordHint() {
		return lastSelectedChord;
	}
	public void setLastSelectedChord(PositionHint lastSelectedChord) {
		this.lastSelectedChord = lastSelectedChord;
	}
	/** 
	 * Finds the nearest chord before specified position in doc.
	 * @param position
	 * @return null if no such chord
	 */
	public PositionHint findChordBefore(int position) {
		if (chords.isEmpty()) {
			return null;
		}
		int next = 0;
		PositionHint curr = chords.get(next);
		PositionHint before = null;
		while (curr.getOffset() < position && next < chords.size() - 1) {
			next++;
			before = curr;
			curr = chords.get(next);
		}
		if (curr.getOffset() < position) {
			return curr;
		} else {
			return before;
		}
	}
	
	/** 
	 * Finds the nearest chord after specified position in doc.
	 */
	public PositionHint findChordAfter(int position) {
		if (chords.isEmpty()) {
			return null;
		}
		int next = 0;
		PositionHint curr = chords.get(next);
		while (curr.getOffset() < position && next < chords.size() - 1) {
			next++;
			curr = chords.get(next);
		}
		if (curr.getOffset() > position) {
			return curr;
		} else {
			return null;
		}
	}
}
