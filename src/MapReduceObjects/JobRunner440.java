package MapReduceObjects;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import Config.Configuration;
import Interfaces.InputFormat440;
import Interfaces.InputSplit440;

public class JobRunner440 {
	
	private Configuration config;
	
	public JobRunner440(Configuration config) {
		this.config = config;
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
}
