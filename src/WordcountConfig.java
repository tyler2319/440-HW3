import Config.Configuration;
import Interfaces.InputFormat440;
import Interfaces.Mapper;
import Interfaces.Reducer;

@SuppressWarnings("rawtypes")
public class WordcountConfig implements Configuration {

	public String getJobName() {
		return "wordcount";
	}

	public Class<? extends Mapper> getMapperClass() {
		return WordcountMap.class;
	}

	public Class<? extends Reducer> getCombinerClass() {
		return WordcountReduce.class;
	}

	public Class<? extends Reducer> getReducerClass() {
		return WordcountReduce.class;
	}
	
	public String getMasterLocation() {
		return "localhost:8888";
	}
	
	public String[] getWorkerLocations() {
		String[] locations = {"localhost:8889"};
		return locations;
	}

	public String getInputFilePath() {
		return "/Users/Tyler/Documents/workspace/440-HW3/src/WordcountText.txt";
		//return "/Users/Justin/gitProjects/440-HW3/src/WordcountText.txt";
	}

	public String getOutputFilePath() {
		return "/Users/Tyler/Documents/workspace/440-HW3/src/WordcountOutput.txt";
		//return "/Users/Justin/gitProjects/440-HW3/src/WordcountOutput.txt";
	}

	public int getNumOfMappers() {
		return 1;
	}

	public int getNumOfReducers() {
		return 1;
	}
	
	public Class<? extends InputFormat440> getInputFormat() {
		return DefaultObjects.TextInputFormat440.class;
	}

	public Class<?> getOutputKeyClass() {
		return String.class;
	}

	public Class<?> getOutputValueClass() {
		return Integer.class;
	}

	public int getRecordLength() {
		return 64;
	}
}
