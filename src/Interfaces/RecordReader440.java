package Interfaces;

import MapReduceObjects.Record;

/**
 * The Record Reader is used to take raw input and convert them into Records
 * that can be used in a Map-Reduce paradigm. A user should implement all these
 * functions to allow for that. Our API has the following default RecordReaders
 * 
 * LineRecordReader440 - Converts a text file into records containing a line of
 * 						 text as the value
 * 
 * @author Tyler Healy (thealy), Justin Greet (jgreet)
 *
 * @param <K> Key Type
 * @param <V> Value Type
 */
public interface RecordReader440<K, V> {
	
	/**
	 * Closes the RecordReader
	 * To be called once all input is read
	 */
	public void close();
	
	/**
	 * @return the key for the next record
	 */
	public K createKey();
	
	/**
	 * @return the value for the next record
	 */
	public V createValue();
	
	/**
	 * @return the position of the record reader 
	 */
	public int getPos();
	
	/**
	 * Used for monitoring. Calculates the percentage through the input the
	 * RecordReader is.
	 * @return a percentage indicating the progress of the RecordReader
	 */
	public float getProgress();
	
	/**
	 * Should use createKey and createValue to create the next Record from the
	 * raw input
	 * @return the next record
	 */
	public Record<K, V> next();
}
