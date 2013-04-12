package MapReduceObjects;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import ClassLoader.ClassLoader440;
import Config.Configuration;
import Interfaces.InputFormat440;
import Interfaces.InputSplit440;

public class JobRunner440 {
	
	private Configuration config;
	
	public JobRunner440(String configPath) {
		this.config = getConfig(configPath);
	}
	
	private Configuration getConfig(String path) {
		String[] parts = path.split("/");
		String[] fileParts = parts[parts.length - 1].split("\\.");
		
		if (fileParts.length != 2 || !fileParts[1].equals("class")) {
			System.out.println("Configuration file must be a valid Java class");
			return null;
		}
		
		ClassLoader440 cl = new ClassLoader440();
		Class<?> configClass = cl.getClass(path, fileParts[0]);
		Constructor<?> configConst = null;
		try {
			configConst = configClass.getConstructor();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		
		Configuration config = null;
		try {
			config = (Configuration)configConst.newInstance();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return config;
	}
	
	public InputSplit440[] computeSplits() {
		int numMappers = config.getNumOfMappers();
		Class<?> inputClass = config.getInputFormat();
		
		Constructor<?> inputConst = null;
		try {
			inputConst = inputClass.getConstructor();
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		} catch (SecurityException e1) {
			e1.printStackTrace();
		}
		
		InputFormat440<?, ?> input = null;
		try {
			input = (InputFormat440<?, ?>) inputConst.newInstance();
			input.configure(config);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return input.getSplits(numMappers);
	}
	
	public Configuration getConfig() {
		return config;
	}
}
