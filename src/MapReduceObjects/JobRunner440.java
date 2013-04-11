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
import Interfaces.Mapper;
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
		TypeVariable<?> K = null;
		TypeVariable<?> V = null;
		try {
			input = (InputFormat440<?, ?>) inputConst.newInstance();
			input.configure(config);
			K = input.getKeyClass().getTypeParameters()[0];
			V = input.getValueClass().getTypeParameters()[0];
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		Class<?> mapClass = config.getMapperClass();
		Constructor<?> mapConst = null;
		try {
			mapConst = mapClass.getConstructor();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		
		Mapper<Long, String, String, Integer> map = null;
		try {
			map = (Mapper<Long, String, String, Integer>) mapConst.newInstance();
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
		OutputCollecter<String, Integer> oc = new OutputCollecter();
		
		for (int i = 0; i < splits.length; i++) {
			System.out.println(i);
			RecordReader440<Long, String> rr = (RecordReader440<Long, String>) input.getRecordReader440(splits[i]);
			
			Record<Long, String> rec;
			
			while((rec = rr.next()) != null) {
				System.out.println("<" + rec.key + ", " + rec.value + ">");
				map.map(rec.key, rec.value, oc);
				System.out.println("YAY!");
			}
		}
	}
}
