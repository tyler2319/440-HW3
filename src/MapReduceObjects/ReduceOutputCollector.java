package MapReduceObjects;

import java.util.HashMap;

public class ReduceOutputCollector<K, V> {
	private HashMap<K, V> output;
	
	public ReduceOutputCollector() {
		output = new HashMap<K,V>();
	}
	
	public void collect(K key, V value) {
		
	}
}
