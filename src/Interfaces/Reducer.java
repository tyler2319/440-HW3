package Interfaces;

import java.util.Iterator;

import MapReduceObjects.OutputCollecter;

public interface Reducer<K1, V1, K2, V2> {
	
	public void reduce(K1 key, Iterator<V1> values, OutputCollecter<K2, V2> output);

}
