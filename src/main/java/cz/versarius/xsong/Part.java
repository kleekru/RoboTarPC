package cz.versarius.xsong;

import java.util.List;

/**
 * Part of song, consists of lines.
 * Can be Chorus or Verse.
 * 
 * @author miira
 *
 */
public abstract class Part {
	private List<Line> lines;

	public List<Line> getLines() {
		return lines;
	}

	public void setLines(List<Line> lines) {
		this.lines = lines;
	}
	
	public abstract String getTypeName();
}
