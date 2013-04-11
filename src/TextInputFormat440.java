import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import Config.Configuration;
import Interfaces.InputFormat440;
import Interfaces.InputSplit440;
import Interfaces.RecordReader440;

public class TextInputFormat440 implements InputFormat440<Long, String> {
	
	private Configuration config;
	
	public void configure(Configuration config) {
		this.config = config;
	}

	public RecordReader440<Long, String> getRecordReader440(InputSplit440 split) {
		return new LineRecordReader440(config, (TextLineSplit440) split);
	}

	public InputSplit440[] getSplits(int numSplits) {
		String pathStr = config.getInputFilePath();
		BufferedReader br = null;
		
		try {
			br = Files.newBufferedReader(Paths.get(pathStr), Charset.defaultCharset());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int chars = 0;
		try {
			chars = (int)br.skip(Long.MAX_VALUE);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int charsPerSplit = chars / numSplits;
		
		TextLineSplit440[] result = new TextLineSplit440[numSplits];
		Path path = Paths.get(pathStr);
		
		for(int i = 0; i < numSplits - 1; i++) {
			result[i] = new TextLineSplit440(path, i * charsPerSplit, charsPerSplit, config);
		}
		int finalStart = (numSplits - 1) * charsPerSplit;
		int finalLength = chars - (finalStart);
		result[numSplits - 1] = new TextLineSplit440(path, finalStart, finalLength, config);
		
		return result;
	}

	public Class<?> getKeyClass() {
		return Long.class;
	}
	
	public Class<?> getValueClass() {
		return String.class;
	}
}
