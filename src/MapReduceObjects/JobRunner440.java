package MapReduceObjects;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
	
	public void computeSplits() {
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
		
		InputSplit440[] splits = input.getSplits(numMappers);
		
		for (int i = 0; i < splits.length; i++) {
			System.out.println(i);
			RecordReader440<?, ?> rr = input.getRecordReader440(splits[i]);
			
			Record<?, ?> rec;
			
			while((rec = rr.next()) != null) {
				System.out.println("<" + rec.key + ", " + rec.value + ">");
			}
		}
	}
}
