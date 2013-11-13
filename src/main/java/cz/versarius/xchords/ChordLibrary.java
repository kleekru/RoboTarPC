package cz.versarius.xchords;

/**
 * Named chord set/bag.
 * 
 * @author miira
 *
 */
public class ChordLibrary extends ChordBag {
	private String desc;
	private String name;
	private String path;
	
	public ChordLibrary() {
	}
	
	public ChordLibrary(String name) {
		this.name = name;
	}
	
	public ChordLibrary(ChordBag bag) {
		super(bag);
	}
	
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
}
