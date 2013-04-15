package Interfaces;

import java.util.Iterator;

import MapReduceObjects.OutputCollecter;

/**
 * Interface that all Reduce classes must implement
 * 
 * @author Tyler Healy (thealy), Justin Greet (jgreet)
 *
 * @param <K1> Intermediate Key Type
 * @param <V1> Intermediate Value Type
 * @param <K2> Output Key Type
 * @param <V2> Output Value Type
 */
public interface Reducer<K1, V1, K2, V2> {
	
	/**
	 * In order for Map-Reduce to be successful, a user has to implement
	 * this method, which takes an input key and an iterator of values associated
	 * with the key, reduces those values to an output key and output value in a
	 * user-defined way, and is collected in the output collecter
	 * 
	 * @param key - intermediate key to be mapped
	 * @param values - iterator of values associated with the key
	 * @param output - output collecter that will collect the results of the reduce
	 */
	public void reduce(K1 key, Iterator<V1> values, OutputCollecter<K2, V2> output);

}
