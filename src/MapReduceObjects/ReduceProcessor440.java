package MapReduceObjects;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import Config.Configuration;
import Interfaces.Reducer;

public class ReduceProcessor440<K, V> {
	
	private Configuration config;
	private ArrayList<Integer> split;
	private String dataPath;
	
	public ReduceProcessor440(Configuration config, ArrayList<Integer> split, String dataPath) {
		this.config = config;
		this.split = split;
		this.dataPath = dataPath;
	}
	
	@SuppressWarnings("unchecked")
	public OutputCollecter<K, V> runJob() {
		OutputCollecter<K ,V> dataCollect = new OutputCollecter<K, V>();
		OutputCollecter<K, V> result = new OutputCollecter<K, V>();
		
		BufferedReader br = null;
		try {
			for (Integer i: split) {
				br = Files.newBufferedReader(Paths.get(dataPath), Charset.defaultCharset());
				br.skip(i);
				String record = br.readLine();
				String[] recordSplit = record.split(",");
				String keyStr = recordSplit[0].substring(1);
				String valueStr = recordSplit[1].substring(0, recordSplit[1].length() - 1);
				K key = (K) keyStr;
				V value = (V) valueStr;
				dataCollect.collect(key, value);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/* Get a map class going that we can instantiate */
		Class<?> reduceClass = config.getReducerClass();
		Constructor<?> reduceConst = null;
		try {
			reduceConst = reduceClass.getConstructor();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		
		/* Get a mapper up and running so we can run it */
		Reducer<K, V, K, V> reducer = null;
		try {
			reducer = (Reducer<K, V, K, V>) reduceConst.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		HashMap<K, ArrayList<V>> hm = dataCollect.groupOutput();
		Set<K> keys = hm.keySet();
		TreeSet<K> sortedKeys = new TreeSet<K>(keys);
		Iterator<K> keysIter = sortedKeys.iterator();
		
		while (keysIter.hasNext()) {
			K curKey = keysIter.next();
			Iterator<V> curValues = hm.get(curKey).iterator();
			reducer.reduce(curKey, curValues, result);
		}
		
		return result;
	}
}
