package MapReduceObjects;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import Config.Configuration;
import Interfaces.InputFormat440;
import Interfaces.InputSplit440;
import Interfaces.Mapper;
import Interfaces.RecordReader440;
import Interfaces.Reducer;

public class MapProcessor440<K1, V1, K2, V2> {
	
	private Configuration config;
	private InputSplit440 split;
	
	public MapProcessor440(Configuration config, InputSplit440 split) {
		this.config = config;
		this.split = split;
	}
	
	@SuppressWarnings("unchecked")
	public OutputCollecter<K2, V2> runJob() {
		Class<?> inputClass = config.getInputFormat();
		Constructor<?> inputConst = null;
		try {
			inputConst = inputClass.getConstructor();
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		} catch (SecurityException e1) {
			e1.printStackTrace();
		}
		
		InputFormat440<K1, V1> input = null;
		try {
			input = (InputFormat440<K1, V1>) inputConst.newInstance();
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
		
		RecordReader440<K1, V1> rr = input.getRecordReader440(split);
		Record<K1, V1> curRecord = rr.next();
		
		/* Set up the object that will hold the output key/ value pairs */
		OutputCollecter<K2, V2> mapOutput = new OutputCollecter<K2, V2>();
		
		/* Get a map class going that we can instantiate */
		Class<?> mapClass = config.getMapperClass();
		Constructor<?> mapConst = null;
		try {
			mapConst = mapClass.getConstructor();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		
		/* Get a mapper up and running so we can run it */
		Mapper<K1, V1, K2, V2> map = null;
		try {
			map = (Mapper<K1, V1, K2, V2>) mapConst.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		while (curRecord != null) {
			map.map(curRecord.getKey(), curRecord.getValue(), mapOutput);
			curRecord = rr.next();
		}
		
		OutputCollecter<K2, V2> combOutput = new OutputCollecter<K2, V2>();
		Class<?> combClass = config.getCombinerClass();
		
		if (combClass == null) {
			return mapOutput;
		}
		
		Constructor<?> combConst = null;
		try {
			combConst = combClass.getConstructor();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		
		Reducer<K2, V2, K2, V2> combiner = null;
		try {
			combiner = (Reducer<K2, V2, K2, V2>) combConst.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		HashMap<K2, ArrayList<V2>> records = mapOutput.getRecords();
		Set<K2> keys = records.keySet();
		TreeSet<K2> sortedKeys = new TreeSet<K2>(keys);
		Iterator<K2> keysIter = sortedKeys.iterator();
		
		while (keysIter.hasNext()) {
			K2 curKey = keysIter.next();
			Iterator<V2> curValues = records.get(curKey).iterator();
			combiner.reduce(curKey, curValues, combOutput);
		}
		
		return combOutput;
	}
}