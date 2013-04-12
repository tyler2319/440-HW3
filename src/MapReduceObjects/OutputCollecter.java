package MapReduceObjects;

import java.util.ArrayList;
import java.util.HashMap;


public class OutputCollecter<K, V> {
	
	private HashMap<K, ArrayList<V>> records;
	
	public OutputCollecter() {
		records = new HashMap<K, ArrayList<V>>();
	}
  
	public void collect(K key, V value) {
		if (records.containsKey(key)) {
			ArrayList<V> values = records.get(key);
			values.add(value);
			records.put(key, values);
		} else {
			ArrayList<V> values = new ArrayList<V>();
			values.add(value);
			records.put(key, values);
		}
	}
	
	public HashMap<K, ArrayList<V>> getRecords() {
		return records;
	}
}
