package cz.versarius.xsong;

import java.util.ArrayList;
import java.util.List;

import cz.versarius.xchords.ChordBag;

/**
 * Song itself.
 * Identification info, song structure (parts) and list of used chords.
 * 
 * @author miira
 *
 */
public class Song {
	private String title;
	private String interpret;
	private String info;
	private List<Part> parts = new ArrayList<Part>();
	/** List of all used chords. */
	private ChordBag usedChords = new ChordBag();
	/** used in UI, as flag */
	private transient boolean changed;
	
	public List<Part> getParts() {
		return parts;
	}

	public void setParts(List<Part> parts) {
		this.parts = parts;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getInterpret() {
		return interpret;
	}

	public void setInterpret(String interpret) {
		this.interpret = interpret;
	}

	public String getTitle() {
		return title;
	}

	public String getTitleWithMark() {
		return (changed ? title + " *": title);
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public String getFullTitle() {
		StringBuilder sb = new StringBuilder();
		sb.append(title);
		sb.append(" - ");
		sb.append(interpret);
		return sb.toString();
	}

	public ChordBag getUsedChords() {
		return usedChords;
	}

	public void setUsedChords(ChordBag chords) {
		this.usedChords = chords;
	}

	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}
	
	
}
