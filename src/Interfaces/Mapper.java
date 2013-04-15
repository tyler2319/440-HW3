package Interfaces;

import MapReduceObjects.OutputCollecter;

/**
 * Interface that all Map classes must implement
 * 
 * @author Tyler Healy (thealy), Justin Greet (jgreet)
 *
 * @param <K1> Input Key Type
 * @param <V1> Input Value Type
 * @param <K2> Intermediate Key Type
 * @param <V2> Intermediate Value Type
 */
public interface Mapper<K1, V1, K2, V2> {
	
	/**
	 * In order for Map-Reduce to be successful, a user has to implement
	 * this method, which takes an input key and an input value, maps it to
	 * an intermediate key and an intermediate value in a user-defined way,
	 * and is collected in the output collecter
	 * 
	 * @param key - key to be mapped
	 * @param value - value to be mapped
	 * @param output - output collecter that will collect the result of the map
	 */
	public void map(K1 key, V1 value, OutputCollecter<K2, V2> output);
}
