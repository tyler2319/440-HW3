package Config;

import java.io.File;

public interface Configuration<key, value> {
	
	public String getJobName();
	
	public Class<?> getMapperClass();
	public Class<?> getCombinerClass();
	public Class<?> getReducerClass();
	
	public File getInputFile();
	public File getOutputFile();
	
	public int getNumOfMappers();
	public int getNumOfReducers();
	
	public InputFormat440<key, value> getInputValue();
}
