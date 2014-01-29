package ioio.robotar.pcconsole;

import java.util.ArrayList;
import java.util.List;

import cz.versarius.xsong.ChordRef;

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
	
	public PositionHint getLineHintCurr(PositionHint chordHint) {
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
	public PositionHint getLineHint(PositionHint chordHint) {
		return lines.get(chordHint.getLine());
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
	
	/** 
	 * Finds the text line, which contains specified position
	 */
	public PositionHint findLineWith(int position) {
		if (lines.isEmpty()) {
			return null;
		}
		int prev = lines.size() - 1;
		PositionHint curr = lines.get(prev);
		PositionHint last = null;
		while (curr.getOffset() > position && prev > 0) {
			prev--;
			last = curr;
			curr = lines.get(prev);
		}
		// in case of click in chord line and not text line - take next text line as result
		if (curr.getOffset() + curr.getLength() < position) {
			return last;
		}
		return curr;
	}
	
	/**
	 * Move all chords position from lastHint till the end of line.
	 * if needed only!
	 * @param diff
	 * @param lastHint
	 */
	public void moveChords(int diff, PositionHint lastHint) {
		if (diff < 0) {
			return; // we can not shrink, we don't know by how much
		}
		int idx = chords.indexOf(lastHint);
		int line = lastHint.getLine();
		boolean done = false;
		PositionHint prev = lastHint;
		while ((idx < (chords.size() - 1)) && !done) {
			idx++;
			PositionHint chord = chords.get(idx);
			if (chord.getLine() != line) {
				// done
				done = true;
			} else {
				ChordRef ref = chord.getChordRef();
				int maxLen = ref.getPosition() - prev.getChordRef().getPosition() - 1; // one space betweeen
				int prevLen = prev.getChordRef().getChord().getName().length();
				if (prevLen > maxLen) {
					chord.setOffset(chord.getOffset() + prevLen - maxLen);
					ref.setPosition(ref.getPosition() + prevLen - maxLen);
					prev = chord;
				} else {
					done = true;
				}
			}
		}
	}
	
	public PositionHint firstChordOnLine(PositionHint hint) {
		int idx = chords.indexOf(hint);
		int line = hint.getLine();
		boolean done = false;
		PositionHint last = hint;
		while (idx > 0 && !done) {
			idx--;
			PositionHint chord = chords.get(idx);
			if (chord.getLine() != line) {
				done = true;
			} else {
				last = chord;
			}
		}
		return last;
	}
	
	public PositionHint prevChordOnLine(PositionHint hint) {
		int idx = chords.indexOf(hint);
		int line = hint.getLine();
		if (idx > 0) {
			idx--;
			PositionHint prevHint = chords.get(idx);
			if (prevHint.getLine() == line) {
				return prevHint;
			}
		}
		return null;
	}
	
	public PositionHint nextChordOnLine(PositionHint hint) {
		int idx = chords.indexOf(hint);
		int line = hint.getLine();
		if (idx < chords.size() - 1) {
			idx++;
			PositionHint nextHint = chords.get(idx);
			if (nextHint.getLine() == line) {
				return nextHint;
			}
		}
		return null;
	}
}
