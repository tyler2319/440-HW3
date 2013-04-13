package DefaultObjects;

import java.nio.file.Path;

import Interfaces.InputSplit440;

public class TextLineSplit440 implements InputSplit440 {
	
	private String path;
	private int start;
	private int length;
	
	public TextLineSplit440(String path, int start, int length) {
		this.path = path;
		this.start = start;
		this.length = length;
	}

	public int getLength() {
		return length;
	}
	
	public String getPath() {
		return path;
	}

	public int getStart() {
		return start;
	}
}
