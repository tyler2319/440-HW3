package DefaultObjects;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import Config.Configuration;
import Interfaces.InputFormat440;
import Interfaces.InputSplit440;
import Interfaces.RecordReader440;

public class TextWordInputFormat440 implements InputFormat440<Long, String>{
private Configuration config;
	
	public void configure(Configuration config) {
		this.config = config;
	}

	public RecordReader440<Long, String> getRecordReader440(InputSplit440 split) {
		return new WordRecordReader440((TextSplit440) split);
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
		
		TextSplit440[] result = new TextSplit440[numSplits];
		//Path path = Paths.get(pathStr);
		
		for(int i = 0; i < numSplits - 1; i++) {
			result[i] = new TextSplit440(pathStr, i * charsPerSplit, charsPerSplit);
		}
		int finalStart = (numSplits - 1) * charsPerSplit;
		int finalLength = chars - (finalStart);
		result[numSplits - 1] = new TextSplit440(pathStr, finalStart, finalLength);
		
		return result;
	}

	public Class<Long> getKeyClass() {
		return Long.class;
	}
	
	public Class<String> getValueClass() {
		return String.class;
	}
}
