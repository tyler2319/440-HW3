package MapReduceObjects;

import java.util.ArrayList;
import java.util.HashMap;


public class OutputCollecter<K, V> {
	
	private ArrayList<Record<K, V>> records;
	
	public OutputCollecter() {
		records = new ArrayList<Record<K, V>>();
	}
  
	public void collect(K key, V value) {
		records.add(new Record<K, V>(key, value));
	}
	
	public ArrayList<Record<K, V>> getRecords() {
		return records;
	}
}
