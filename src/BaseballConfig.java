import Config.Configuration;
import Interfaces.InputFormat440;

public class BaseballConfig implements Configuration{
	public String getJobName() {
		return "sluggingPct";
	}

	public String getMapperClassPath() {
		//return "/Users/Tyler/Documents/workspace/440-HW3/bin/WordcountMap.class";
		return "/Users/Justin/gitProjects/440-HW3/bin/BaseballMap.class";
	}

	public String getCombinerClassPath() {
		//return "/Users/Tyler/Documents/workspace/440-HW3/bin/WordcountReduce.class";
		return null;
	}

	public String getReducerClassPath() {
		//return "/Users/Tyler/Documents/workspace/440-HW3/bin/WordcountReduce.class";
		return "/Users/Justin/gitProjects/440-HW3/bin/BaseballReduce.class";
	}
	
	public String getMasterLocation() {
		return "localhost:8888";
	}
	
	public String[] getWorkerLocations() {
		String[] locations = {"localhost:8889:8880", "localhost:8887:8881"};
		return locations;
	}

	public String getInputFilePath() {
		//return "/Users/Tyler/Documents/workspace/440-HW3/src/WordcountText.txt";
		return "/Users/Justin/gitProjects/440-HW3/src/baseballData.txt";
	}

	public String getOutputFilePath() {
		//return "/Users/Tyler/Documents/workspace/440-HW3/src/WordcountOutput.txt";
		return "/Users/Justin/gitProjects/440-HW3/src/BaseballOutput.txt";
	}

	public int getNumOfMappers() {
		return 2;
	}

	public int getNumOfReducers() {
		return 4;
	}
	
	@SuppressWarnings("rawtypes")
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
