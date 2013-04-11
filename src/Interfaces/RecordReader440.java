package Interfaces;

import MapReduceObjects.Record;

public interface RecordReader440<K, V> {
	
	public void close();
	public K createKey();
	public V createValue();
	public int getPos();
	public float getProgress();
	public Record<K, V> next();
}
