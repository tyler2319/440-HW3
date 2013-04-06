package MapReduceObjects;

public class Record<K, V> {

	public final K key;
	public final V value;
	
	public Record (K key, V value) {
		this.key = key;
		this.value = value;
	}
}
