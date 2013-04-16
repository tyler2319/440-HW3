package MapReduceObjects;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import ClassLoader.ClassLoader440;
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
	public OutputCollecter<K2, V2> runJob() throws NoSuchMethodException, 
	SecurityException, InstantiationException, IllegalAccessException, 
	IllegalArgumentException, InvocationTargetException {
		Class<?> inputClass = config.getInputFormat();
		Constructor<?> inputConst = null;
		inputConst = inputClass.getConstructor();
		InputFormat440<K1, V1> input = null;
		input = (InputFormat440<K1, V1>) inputConst.newInstance();
		input.configure(config);
		
		RecordReader440<K1, V1> rr = input.getRecordReader440(split);
		Record<K1, V1> curRecord = rr.next();
		
		/* Set up the object that will hold the output key/ value pairs */
		OutputCollecter<K2, V2> mapOutput = new OutputCollecter<K2, V2>();
		
		/* Get a map class going that we can instantiate */
		String mapClassPath = config.getMapperClassPath();
		String[] mapParts = mapClassPath.split("/");
		String[] mapFileParts = mapParts[mapParts.length - 1].split("\\.");
		
		if (mapFileParts.length != 2 || !mapFileParts[1].equals("class")) {
			System.out.println("Map file must be a valid Java class");
			return null;
		}
		
		ClassLoader440 cl = new ClassLoader440();
		Class<?> mapClass = cl.getClass(mapClassPath, mapFileParts[0]);
		Constructor<?> mapConst = null;
		mapConst = mapClass.getConstructor();
		/* Get a mapper up and running so we can run it */
		Mapper<K1, V1, K2, V2> map = null;
		map = (Mapper<K1, V1, K2, V2>) mapConst.newInstance();
		
		while (curRecord != null) {
			map.map(curRecord.getKey(), curRecord.getValue(), mapOutput);
			curRecord = rr.next();
		}
		
		rr.close();
		
		OutputCollecter<K2, V2> combOutput = new OutputCollecter<K2, V2>();
		String combClassPath = config.getCombinerClassPath();
		
		if (combClassPath == null) {
			return mapOutput;
		}
		
		String[] combParts = combClassPath.split("/");
		String[] combFileParts = combParts[combParts.length - 1].split("\\.");
		
		if (combFileParts.length != 2 || !combFileParts[1].equals("class")) {
			System.out.println("Configuration file must be a valid Java class");
			return null;
		}
		
		Class<?> combClass = cl.getClass(combClassPath, combFileParts[0]);
		Constructor<?> combConst = null;
		combConst = combClass.getConstructor();
		
		Reducer<K2, V2, K2, V2> combiner = null;
		combiner = (Reducer<K2, V2, K2, V2>) combConst.newInstance();
		
		HashMap<K2, ArrayList<V2>> hm = mapOutput.groupOutput();
		Set<K2> keys = hm.keySet();
		TreeSet<K2> sortedKeys = new TreeSet<K2>(keys);
		Iterator<K2> keysIter = sortedKeys.iterator();
		
		while (keysIter.hasNext()) {
			K2 curKey = keysIter.next();
			Iterator<V2> curValues = hm.get(curKey).iterator();
			combiner.reduce(curKey, curValues, combOutput);
		}
		
		return combOutput;
	}
}
