package Config;

import Interfaces.InputFormat440;

@SuppressWarnings("rawtypes")
public interface Configuration {
	
	public String getJobName();
	
	public String getMapperClassPath();
	public String getCombinerClassPath();
	public String getReducerClassPath();
	
	public String getMasterLocation();
	public String[] getWorkerLocations();
	
	public String getInputFilePath();
	public String getOutputFilePath();
	
	public int getNumOfMappers();
	public int getNumOfReducers();
	
	public Class<? extends InputFormat440> getInputFormat();
	
	public Class<?> getOutputKeyClass();
	public Class<?> getOutputValueClass();
	
	public int getRecordLength();
}
