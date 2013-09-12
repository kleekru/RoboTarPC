package cz.versarius.xsong;

import java.util.List;

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
