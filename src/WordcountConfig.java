import java.io.File;

import Config.Configuration;
import Config.InputFormat440;

public class WordcountConfig implements Configuration {

	public String getJobName() {
		return "wordcount";
	}

	public Class<?> getMapperClass() {
		return WordcountMap.class;
	}

	public Class<?> getCombinerClass() {
		return null;
	}

	public Class<?> getReducerClass() {
		return WordcountReduce.class;
	}

	public File getInputFile() {
		return null;
	}

	public File getOutputFile() {
		return null;
	}

	public int getNumOfMappers() {
		return 1;
	}

	public int getNumOfReducers() {
		return 1;
	}

	@Override
	public InputFormat440<?, ?> getInputValue() {
		return null;
	}
}
