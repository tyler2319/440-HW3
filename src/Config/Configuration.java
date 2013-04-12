package Config;

import Interfaces.InputFormat440;
import Interfaces.Mapper;
import Interfaces.Reducer;

@SuppressWarnings("rawtypes")
public interface Configuration {
	
	public String getJobName();
	
	public Class<? extends Mapper> getMapperClass();
	public Class<? extends Reducer> getCombinerClass();
	public Class<? extends Reducer> getReducerClass();
	
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
