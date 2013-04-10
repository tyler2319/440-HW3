import Config.Configuration;
import Interfaces.InputFormat440;

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

	public String getInputFilePath() {
		return "/Users/Tyler/Documents/workspace/440-HW3/src/WordcountText.txt";
	}

	public String getOutputFilePath() {
		return "/Users/Tyler/Documents/workspace/440-HW3/src/WordcountOutput.txt";
	}

	public int getNumOfMappers() {
		return 1;
	}

	public int getNumOfReducers() {
		return 1;
	}
	
	public Class<?> getInputFormat() {
		return TextInputFormat440.class;
	}
}
