package MapReduceObjects;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import Config.Configuration;

public class JobRunner440 {
	
	private Configuration config;
	
	public JobRunner440(Configuration config) {
		this.config = config;
	}

	public void printInput() {
		List<String> text = null;
		try {
			text = Files.readAllLines(Paths.get(config.getInputFilePath()), Charset.defaultCharset());
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < text.size(); i++) {
			System.out.println(text.get(i));
		}
	}
}
