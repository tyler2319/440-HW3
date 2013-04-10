import java.nio.file.Path;

import Config.Configuration;
import Interfaces.InputSplit440;

public class TextLineSplit440 implements InputSplit440 {
	
	private Path path;
	private int start;
	private int length;
	private Configuration config;
	
	public TextLineSplit440(Path path, int start, int length, Configuration config) {
		this.path = path;
		this.start = start;
		this.length = length;
		this.config = config;
	}

	public int getLength() {
		return length;
	}
	
	public Path getPath() {
		return path;
	}

	public int getStart() {
		return start;
	}
}
