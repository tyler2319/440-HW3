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
	
	public HashMap<K, ArrayList<V>> groupOutput() {
		HashMap<K, ArrayList<V>> hm = new HashMap<K, ArrayList<V>>();
		
		for (int i = 0; i < records.size(); i++) {
			Record<K, V> curRec = records.get(i);
			K key = curRec.getKey();
			V val = curRec.getValue();
			if (hm.containsKey(key)) {
				ArrayList<V> valArray = hm.get(key);
				valArray.add(val);
				hm.put(key, valArray);
			} else {
				ArrayList<V> valArray = new ArrayList<V>();
				valArray.add(val);
				hm.put(key, valArray);
			}
		}
		
		return hm;
	}
}
