package Interfaces;

import MapReduceObjects.OutputCollecter;

public interface Mapper<K1, V1, K2, V2> {
	
	public void map(K1 key, V1 value, OutputCollecter<K2, V2> output);
}
