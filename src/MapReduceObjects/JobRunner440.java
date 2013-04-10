package MapReduceObjects;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.TypeVariable;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import Config.Configuration;
import Interfaces.InputFormat440;
import Interfaces.InputSplit440;
import Interfaces.RecordReader440;

public class JobRunner440 {
	
	private Configuration config;
	
	public JobRunner440(Configuration config) {
		this.config = config;
	}

	public void printInput() {
		List<String> text = null;
		try {
			text = Files.readAllLines(Paths.get(config.getInputFilePath()), Charset.defaultCharset());
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < text.size(); i++) {
			System.out.println(text.get(i));
		}
	}
	
	public InputSplit440[] getSplits() {
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
		
		return input.getSplits(2);
	}
}
