import Config.Configuration;
import Interfaces.InputFormat440;

public class ColorcountConfig implements Configuration {

	public String getJobName() {
		return "colorcount";
	}

	public String getMapperClassPath() {
		return "/Users/Tyler/Documents/workspace/440-HW3/bin/ColorcountMap.class";
		//return "/Users/Justin/gitProjects/440-HW3/bin/ColorcountMap.class";
	}

	public String getCombinerClassPath() {
		return "/Users/Tyler/Documents/workspace/440-HW3/bin/ColorcountReduce.class";
		//return "/Users/Justin/gitProjects/440-HW3/bin/ColorcountReduce.class";
	}

	public String getReducerClassPath() {
		return "/Users/Tyler/Documents/workspace/440-HW3/bin/ColorcountReduce.class";
		//return "/Users/Justin/gitProjects/440-HW3/bin/ColorcountReduce.class";
	}
	
	public String getMasterLocation() {
		return "localhost:8888";
	}
	
	public String[] getWorkerLocations() {
		String[] locations = {"localhost:8887:8880", "localhost:8889:8881"};
		return locations;
	}

	public String getInputFilePath() {
		return "/Users/Tyler/Documents/workspace/440-HW3/yankees.jpg";
		//return "/Users/Justin/gitProjects/440-HW3/yankees.jpg";
	}

	public String getOutputFilePath() {
		return "/Users/Tyler/Documents/workspace/440-HW3/src/ColorcountOutput.txt";
		//return "/Users/Justin/gitProjects/440-HW3/src/ColorcountOutput.txt";
	}

	public int getNumOfMappers() {
		return 4;
	}

	public int getNumOfReducers() {
		return 2;
	}
	
	@SuppressWarnings("rawtypes")
	public Class<? extends InputFormat440> getInputFormat() {
		return DefaultObjects.ImageInputFormat440.class;
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
