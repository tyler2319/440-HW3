package MapReduceObjects;

import java.util.ArrayList;


public class OutputCollecter<K, V> {
	
	private ArrayList<Record<K, V>> records;
  
	public void collect(K key, V value) {
		records.add(new Record<K, V>(key, value));
	}
	
	public void printOutput() {
		for (int i = 0; i < records.size(); i++) {
			Record<K, V> curRecord = records.get(i);
			System.out.println("<" + curRecord.getKey() + ", " + 
					curRecord.getValue() + ">");
		}
	}
}
