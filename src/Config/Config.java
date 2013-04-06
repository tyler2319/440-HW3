package Config;

import java.io.File;

public class Config {
	
	private String jobName;
	
	private Class<?> mapper;
	private Class<?> combiner;
	private Class<?> reducer;
	
	private File inputFile;
	private File outputFile;
	
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	
	public String getJobName() {
		return jobName;
	}
	
	public void setMapperClass(Class<?> mapper) {
		this.mapper = mapper;
	}
	
	public Class<?> getMapperClass() {
		return mapper;
	}
	
	public void setCombinerClass(Class<?> combiner) {
		this.combiner = combiner;
	}
	
	public Class<?> getCombinerClass() {
		return combiner;
	}
	
	public void setReducerClass(Class<?> reducer) {
		this.reducer = reducer;
	}
	
	public Class<?> getReducerClass() {
		return reducer;
	}
	
	public void setInputFile(File inputFile) {
		this.inputFile = inputFile;
	}
	
	public File getInputFile() {
		return inputFile;
	}
	
	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}
	
	public File getOutputFile() {
		return outputFile;
	}
}

