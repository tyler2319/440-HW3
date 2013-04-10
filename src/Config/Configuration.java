package Config;

public interface Configuration {
	
	public String getJobName();
	
	public Class<?> getMapperClass();
	public Class<?> getCombinerClass();
	public Class<?> getReducerClass();
	
	public String getInputFilePath();
	public String getOutputFilePath();
	
	public int getNumOfMappers();
	public int getNumOfReducers();
	
	public Class<?> getInputFormat();
}
